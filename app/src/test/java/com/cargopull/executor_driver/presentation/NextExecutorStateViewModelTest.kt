package com.cargopull.executor_driver.presentation

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.cargopull.executor_driver.ViewModelThreadTestRule
import com.cargopull.executor_driver.backend.analytics.ErrorReporter
import com.cargopull.executor_driver.backend.web.ServerResponseException
import com.cargopull.executor_driver.gateway.DataMappingException
import com.cargopull.executor_driver.interactor.NextExecutorStateUseCase
import io.reactivex.Completable
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Before
import org.junit.ClassRule
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner
import retrofit2.HttpException
import retrofit2.Response

@RunWith(MockitoJUnitRunner::class)
class NextExecutorStateViewModelTest {

    companion object {

        @ClassRule
        @JvmField
        val classRule = ViewModelThreadTestRule()
    }

    @Rule
    @JvmField
    val rule: TestRule = InstantTaskExecutorRule()
    private lateinit var viewModel: NextExecutorStateViewModel
    @Mock
    private lateinit var errorReporter: ErrorReporter
    @Mock
    private lateinit var nextExecutorStateUseCase: NextExecutorStateUseCase

    @Mock
    private lateinit var viewStateObserver: Observer<ViewState<FragmentViewActions>>
    @Mock
    private lateinit var navigateObserver: Observer<String>

    @Before
    fun setUp() {
        `when`(nextExecutorStateUseCase.proceedToNextState).thenReturn(Completable.never())
        viewModel = NextExecutorStateViewModelImpl(errorReporter, nextExecutorStateUseCase)
    }

    /* Проверяем отправку ошибок в репортер */

    /**
     * Должет отправить ошибку преобразования данных ТС.
     */
    @Test
    fun reportVehicleDataMappingError() {
        // Given:
        `when`(nextExecutorStateUseCase.proceedToNextState)
                .thenReturn(Completable.error(DataMappingException()))

        // Action:
        viewModel.routeToNextState()

        // Effect:
        verify(errorReporter, only()).reportError(any(DataMappingException::class.java))
    }

    /**
     * Должен отправить ошибку сети.
     */
    @Test
    fun reportNoNetworkErrorAgain() {
        // Given:
        `when`(nextExecutorStateUseCase.proceedToNextState)
                .thenReturn(Completable.error(ServerResponseException("403", "")))

        // Action:
        viewModel.routeToNextState()

        // Effect:
        verify(errorReporter, only()).reportError(any(ServerResponseException::class.java))
    }

    /**
     * Не должен отправлять другие ошибки.
     */
    @Test
    fun doNotReportOtherErrors() {
        // Given:
        `when`(nextExecutorStateUseCase.proceedToNextState)
                .thenReturn(Completable.error(Exception()))

        // Action:
        viewModel.routeToNextState()

        // Effect:
        verifyNoInteractions(errorReporter)
    }

    /**
     * Не должен отправлять другие ошибки сети.
     */
    @Test
    fun doNotReportOtherNetworkErrors() {
        // Given:
        `when`(nextExecutorStateUseCase.proceedToNextState)
                .thenReturn(Completable.error(HttpException(
                    Response.error<Any>(
                        404,
                        "".toResponseBody("applocation/json".toMediaType())
                    )
                )))

        // Action:
        viewModel.routeToNextState()

        // Effect:
        verifyNoInteractions(errorReporter)
    }

    /* Тетсируем работу с юзкейсом перехода к следующему состоянию исполнителя. */

    /**
     * Не должен трогать юзкейс на подписках.
     */
    @Test
    fun doNotTouchUseCaseOnSubscriptions() {
        // Action:
        viewModel.viewStateLiveData
        viewModel.navigationLiveData
        viewModel.viewStateLiveData
        viewModel.navigationLiveData

        // Effect:
        verifyNoInteractions(nextExecutorStateUseCase)
    }

    /**
     * Должен просить юзкейс перейти к следующему состоянию исполнителя.
     */
    @Test
    fun askUseCaseToRouteToNextExecutorState() {
        // Given:
        `when`(nextExecutorStateUseCase.proceedToNextState).thenReturn(Completable.complete())

        // Action:
        viewModel.routeToNextState()
        viewModel.routeToNextState()
        viewModel.routeToNextState()
        viewModel.routeToNextState()

        // Effect:
        verify(nextExecutorStateUseCase, times(4)).proceedToNextState
    }

    /**
     * Не должен трогать юзкейс, если предыдущий запрос перехода к следующему состоянию исполнителя
     * еще не завершился.
     */
    @Test
    fun doNotTouchUseCaseDuringRoutingToNextExecutorState() {
        // Action:
        viewModel.routeToNextState()
        viewModel.routeToNextState()
        viewModel.routeToNextState()
        viewModel.routeToNextState()

        // Effect:
        verify(nextExecutorStateUseCase, only()).proceedToNextState
    }

    /* Тетсируем переключение состояний. */

    /**
     * Должен вернуть состояние вида бездействия изначально.
     */
    @Test
    fun setPendingViewStateToLiveDataInitially() {
        // Given:
        val inOrder = inOrder(viewStateObserver)

        // Action:
        viewModel.viewStateLiveData.observeForever(viewStateObserver)

        // Effect:
        inOrder.verify(viewStateObserver).onChanged(any(NextExecutorStateViewStateIdle::class.java))
        verifyNoMoreInteractions(viewStateObserver)
    }

    /**
     * Должен вернуть состояние вида "В процессе".
     */
    @Test
    fun setPendingViewStateWithEnRouteViewStateToLiveDataForCompleteTheOrder() {
        // Given:
        val inOrder = inOrder(viewStateObserver)
        viewModel.viewStateLiveData.observeForever(viewStateObserver)

        // Action:
        viewModel.routeToNextState()

        // Effect:
        inOrder.verify(viewStateObserver).onChanged(any(NextExecutorStateViewStateIdle::class.java))
        inOrder.verify(viewStateObserver).onChanged(any(NextExecutorStateViewStatePending::class.java))
        verifyNoMoreInteractions(viewStateObserver)
    }

    /**
     * Должен вернуть состояние вида "Бездействие" при ошибке смены состояния.
     */
    @Test
    fun setPendingViewStateWithNoRouteTrueViewStateToLiveDataForCompleteTheOrder() {
        // Given:
        val inOrder = inOrder(viewStateObserver)
        viewModel.viewStateLiveData.observeForever(viewStateObserver)
        `when`(nextExecutorStateUseCase.proceedToNextState)
                .thenReturn(Completable.error(Exception()))

        // Action:
        viewModel.routeToNextState()

        // Effect:
        inOrder.verify(viewStateObserver).onChanged(any(NextExecutorStateViewStateIdle::class.java))
        inOrder.verify(viewStateObserver).onChanged(any(NextExecutorStateViewStatePending::class.java))
        inOrder.verify(viewStateObserver).onChanged(any(NextExecutorStateViewStateIdle::class.java))
        verifyNoMoreInteractions(viewStateObserver)
    }

    /**
     * Должен вернуть состояние вида "Бездействие" при успехе.
     */
    @Test
    fun setPendingViewStateWithNoRouteFalseViewStateToLiveDataForCompleteTheOrder() {
        // Given:
        val inOrder = inOrder(viewStateObserver)
        viewModel.viewStateLiveData.observeForever(viewStateObserver)
        `when`(nextExecutorStateUseCase.proceedToNextState).thenReturn(Completable.complete())

        // Action:
        viewModel.routeToNextState()

        // Effect:
        inOrder.verify(viewStateObserver).onChanged(any(NextExecutorStateViewStateIdle::class.java))
        inOrder.verify(viewStateObserver).onChanged(any(NextExecutorStateViewStatePending::class.java))
        inOrder.verify(viewStateObserver).onChanged(any(NextExecutorStateViewStateIdle::class.java))
        verifyNoMoreInteractions(viewStateObserver)
    }

    /* Тестируем навигацию. */

    /**
     * Не должен никуда ходить до ответа от сервера.
     */
    @Test
    fun doNotTouchNavigationObserverBeforeResponse() {
        // Given:
        viewModel.navigationLiveData.observeForever(navigateObserver)

        // Action:
        viewModel.routeToNextState()
        viewModel.routeToNextState()
        viewModel.routeToNextState()

        // Effect:
        verifyNoInteractions(navigateObserver)
    }

    /**
     * Должен вернуть перейти к "ошибке данных сервера".
     */
    @Test
    fun setNavigateToServerDataError() {
        // Given:
        viewModel.navigationLiveData.observeForever(navigateObserver)
        `when`(nextExecutorStateUseCase.proceedToNextState)
                .thenReturn(Completable.error(DataMappingException()))

        // Action:
        viewModel.routeToNextState()

        // Effect:
        verify(navigateObserver, only()).onChanged(CommonNavigate.SERVER_DATA_ERROR)
    }

    /**
     * Должен вернуть перейти к ошибке сети при прочих ошибках.
     */
    @Test
    fun navigateToNoConnectionForOtherErrors() {
        // Given:
        viewModel.navigationLiveData.observeForever(navigateObserver)
        `when`(nextExecutorStateUseCase.proceedToNextState)
                .thenReturn(Completable.error(Exception()))

        // Action:
        viewModel.routeToNextState()

        // Effect:
        verify(navigateObserver, only()).onChanged(CommonNavigate.NO_CONNECTION)
    }

    /**
     * Не должен никуда переходить при успешных ответах.
     */
    @Test
    fun navigateToCorrespondingStates() {
        // Given:
        `when`(nextExecutorStateUseCase.proceedToNextState).thenReturn(Completable.complete())
        viewModel.navigationLiveData.observeForever(navigateObserver)

        // Action:
        viewModel.routeToNextState()

        // Effect:
        verifyNoInteractions(navigateObserver)
    }

    private fun <T> any(type: Class<T>): T {
        Mockito.any<T>(type)
        return uninitialized()
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> uninitialized(): T = null as T
}

@RunWith(MockitoJUnitRunner::class)
class NextExecutorStateViewStateIdleTest {

    @Mock
    private lateinit var viewActions: FragmentViewActions

    @Test
    fun testActions() {
        // Action:
        NextExecutorStateViewStateIdle().apply(viewActions)

        // Effect:
        verify(viewActions, only()).unblockWithPending("NextExecutorState")
    }
}

@RunWith(MockitoJUnitRunner::class)
class NextExecutorStateViewStatePendingTest {

    @Mock
    private lateinit var viewActions: FragmentViewActions

    @Test
    fun testActions() {
        // Action:
        NextExecutorStateViewStatePending().apply(viewActions)

        // Effect:
        verify(viewActions, only()).blockWithPending("NextExecutorState")
    }
}