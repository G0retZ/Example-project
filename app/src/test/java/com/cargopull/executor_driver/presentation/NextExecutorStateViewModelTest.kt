package com.cargopull.executor_driver.presentation

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.cargopull.executor_driver.ViewModelThreadTestRule
import com.cargopull.executor_driver.backend.analytics.ErrorReporter
import com.cargopull.executor_driver.backend.web.ServerResponseException
import com.cargopull.executor_driver.gateway.DataMappingException
import com.cargopull.executor_driver.interactor.NextExecutorStateUseCase
import io.reactivex.Completable
import okhttp3.MediaType
import okhttp3.ResponseBody
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
        // Дано:
        `when`(nextExecutorStateUseCase.proceedToNextState)
                .thenReturn(Completable.error(DataMappingException()))

        // Действие:
        viewModel.routeToNextState()

        // Результат:
        verify(errorReporter, only()).reportError(any(DataMappingException::class.java))
    }

    /**
     * Должен отправить ошибку сети.
     */
    @Test
    fun reportNoNetworkErrorAgain() {
        // Дано:
        `when`(nextExecutorStateUseCase.proceedToNextState)
                .thenReturn(Completable.error(ServerResponseException("403", "")))

        // Действие:
        viewModel.routeToNextState()

        // Результат:
        verify(errorReporter, only()).reportError(any(ServerResponseException::class.java))
    }

    /**
     * Не должен отправлять другие ошибки.
     */
    @Test
    fun doNotReportOtherErrors() {
        // Дано:
        `when`(nextExecutorStateUseCase.proceedToNextState)
                .thenReturn(Completable.error(Exception()))

        // Действие:
        viewModel.routeToNextState()

        // Результат:
        verifyZeroInteractions(errorReporter)
    }

    /**
     * Не должен отправлять другие ошибки сети.
     */
    @Test
    fun doNotReportOtherNetworkErrors() {
        // Дано:
        `when`(nextExecutorStateUseCase.proceedToNextState)
                .thenReturn(Completable.error(HttpException(
                        Response.error<Any>(404, ResponseBody.create(MediaType.get("applocation/json"), ""))
                )))

        // Действие:
        viewModel.routeToNextState()

        // Результат:
        verifyZeroInteractions(errorReporter)
    }

    /* Тетсируем работу с юзкейсом перехода к следующему состоянию исполнителя. */

    /**
     * Не должен трогать юзкейс на подписках.
     */
    @Test
    fun doNotTouchUseCaseOnSubscriptions() {
        // Действие:
        viewModel.viewStateLiveData
        viewModel.navigationLiveData
        viewModel.viewStateLiveData
        viewModel.navigationLiveData

        // Результат:
        verifyZeroInteractions(nextExecutorStateUseCase)
    }

    /**
     * Должен просить юзкейс перейти к следующему состоянию исполнителя.
     */
    @Test
    fun askUseCaseToRouteToNextExecutorState() {
        // Дано:
        `when`(nextExecutorStateUseCase.proceedToNextState).thenReturn(Completable.complete())

        // Действие:
        viewModel.routeToNextState()
        viewModel.routeToNextState()
        viewModel.routeToNextState()
        viewModel.routeToNextState()

        // Результат:
        verify(nextExecutorStateUseCase, times(4)).proceedToNextState
    }

    /**
     * Не должен трогать юзкейс, если предыдущий запрос перехода к следующему состоянию исполнителя
     * еще не завершился.
     */
    @Test
    fun doNotTouchUseCaseDuringRoutingToNextExecutorState() {
        // Действие:
        viewModel.routeToNextState()
        viewModel.routeToNextState()
        viewModel.routeToNextState()
        viewModel.routeToNextState()

        // Результат:
        verify(nextExecutorStateUseCase, only()).proceedToNextState
    }

    /* Тетсируем переключение состояний. */

    /**
     * Должен вернуть состояние вида бездействия изначально.
     */
    @Test
    fun setPendingViewStateToLiveDataInitially() {
        // Дано:
        val inOrder = inOrder(viewStateObserver)

        // Действие:
        viewModel.viewStateLiveData.observeForever(viewStateObserver)

        // Результат:
        inOrder.verify(viewStateObserver).onChanged(any(NextExecutorStateViewStateIdle::class.java))
        verifyNoMoreInteractions(viewStateObserver)
    }

    /**
     * Должен вернуть состояние вида "В процессе".
     */
    @Test
    fun setPendingViewStateWithEnRouteViewStateToLiveDataForCompleteTheOrder() {
        // Дано:
        val inOrder = inOrder(viewStateObserver)
        viewModel.viewStateLiveData.observeForever(viewStateObserver)

        // Действие:
        viewModel.routeToNextState()

        // Результат:
        inOrder.verify(viewStateObserver).onChanged(any(NextExecutorStateViewStateIdle::class.java))
        inOrder.verify(viewStateObserver).onChanged(any(NextExecutorStateViewStatePending::class.java))
        verifyNoMoreInteractions(viewStateObserver)
    }

    /**
     * Должен вернуть состояние вида "Бездействие" при ошибке смены состояния.
     */
    @Test
    fun setPendingViewStateWithNoRouteTrueViewStateToLiveDataForCompleteTheOrder() {
        // Дано:
        val inOrder = inOrder(viewStateObserver)
        viewModel.viewStateLiveData.observeForever(viewStateObserver)
        `when`(nextExecutorStateUseCase.proceedToNextState)
                .thenReturn(Completable.error(Exception()))

        // Действие:
        viewModel.routeToNextState()

        // Результат:
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
        // Дано:
        val inOrder = inOrder(viewStateObserver)
        viewModel.viewStateLiveData.observeForever(viewStateObserver)
        `when`(nextExecutorStateUseCase.proceedToNextState).thenReturn(Completable.complete())

        // Действие:
        viewModel.routeToNextState()

        // Результат:
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
        // Дано:
        viewModel.navigationLiveData.observeForever(navigateObserver)

        // Действие:
        viewModel.routeToNextState()
        viewModel.routeToNextState()
        viewModel.routeToNextState()

        // Результат:
        verifyZeroInteractions(navigateObserver)
    }

    /**
     * Должен вернуть перейти к "ошибке данных сервера".
     */
    @Test
    fun setNavigateToServerDataError() {
        // Дано:
        viewModel.navigationLiveData.observeForever(navigateObserver)
        `when`(nextExecutorStateUseCase.proceedToNextState)
                .thenReturn(Completable.error(DataMappingException()))

        // Действие:
        viewModel.routeToNextState()

        // Результат:
        verify(navigateObserver, only()).onChanged(CommonNavigate.SERVER_DATA_ERROR)
    }

    /**
     * Должен вернуть перейти к ошибке сети при прочих ошибках.
     */
    @Test
    fun navigateToNoConnectionForOtherErrors() {
        // Дано:
        viewModel.navigationLiveData.observeForever(navigateObserver)
        `when`(nextExecutorStateUseCase.proceedToNextState)
                .thenReturn(Completable.error(Exception()))

        // Действие:
        viewModel.routeToNextState()

        // Результат:
        verify(navigateObserver, only()).onChanged(CommonNavigate.NO_CONNECTION)
    }

    /**
     * Не должен никуда переходить при успешных ответах.
     */
    @Test
    fun navigateToCorrespondingStates() {
        // Дано:
        `when`(nextExecutorStateUseCase.proceedToNextState).thenReturn(Completable.complete())
        viewModel.navigationLiveData.observeForever(navigateObserver)

        // Действие:
        viewModel.routeToNextState()

        // Результат:
        verifyZeroInteractions(navigateObserver)
    }

    private fun <T> any(type: Class<T>): T {
        Mockito.any<T>(type)
        return uninitialized()
    }

    private fun <T> uninitialized(): T = null as T
}

@RunWith(MockitoJUnitRunner::class)
class NextExecutorStateViewStateIdleTest {

    @Mock
    private lateinit var viewActions: FragmentViewActions

    @Test
    fun testActions() {
        // Действие:
        NextExecutorStateViewStateIdle().apply(viewActions)

        // Результат:
        verify(viewActions, only()).unblockWithPending("NextExecutorState")
    }
}

@RunWith(MockitoJUnitRunner::class)
class NextExecutorStateViewStatePendingTest {

    @Mock
    private lateinit var viewActions: FragmentViewActions

    @Test
    fun testActions() {
        // Действие:
        NextExecutorStateViewStatePending().apply(viewActions)

        // Результат:
        verify(viewActions, only()).blockWithPending("NextExecutorState")
    }
}