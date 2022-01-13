package com.cargopull.executor_driver.interactor

import com.cargopull.executor_driver.UseCaseThreadTestRule
import com.cargopull.executor_driver.entity.ExecutorState
import com.cargopull.executor_driver.gateway.DataMappingException
import io.reactivex.Single
import org.junit.Before
import org.junit.ClassRule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner
import java.io.IOException

@RunWith(MockitoJUnitRunner::class)
class NextExecutorStateUseCaseTest {

    companion object {
        @ClassRule
        @JvmField
        val classRule = UseCaseThreadTestRule()
    }

    private lateinit var useCase: NextExecutorStateUseCaseImpl<String>

    @Mock
    private lateinit var updateUseCase: DataUpdateUseCase<String>
    @Mock
    lateinit var updateExecutorStateUseCase: DataUpdateUseCase<ExecutorState>
    @Mock
    lateinit var gateway: CommonGatewaySingle<Pair<ExecutorState, String?>>

    @Before
    fun setUp() {
        `when`(gateway.data).thenReturn(Single.never())
        useCase = NextExecutorStateUseCaseImpl(gateway, updateExecutorStateUseCase, updateUseCase)
    }

    /* Проверяем работу с гейтвеем */

    /**
     * Должен запросить данные у гейтвея.
     */
    @Test
    fun askGatewayToProceedToNextState() {
        // Action:
        useCase.proceedToNextState.test()

        // Effect:
        verify(gateway, only()).data
    }

    /* Проверяем работу с юзкейсом обновления состояния водителя */

    /**
     * Не должен трогать юзкейс до прихода данных.
     */
    @Test
    fun doNotTouchUpdateExecutorStateUseCase() {
        // Action:
        useCase.proceedToNextState.test()
        useCase.proceedToNextState.test()
        useCase.proceedToNextState.test()
        useCase.proceedToNextState.test()

        // Effect:
        verifyNoInteractions(updateExecutorStateUseCase)
    }

    /**
     * Не должен трогать юзкейс при ошибках.
     */
    @Test
    fun doNotTouchUpdateExecutorStateUseCaseOnErrors() {
        // Given:
        `when`(gateway.data).thenReturn(
                Single.error(DataMappingException()),
                Single.error(IOException()),
                Single.error(NullPointerException())
        )

        // Action:
        useCase.proceedToNextState.test()
        useCase.proceedToNextState.test()
        useCase.proceedToNextState.test()
        useCase.proceedToNextState.test()

        // Effect:
        verifyNoInteractions(updateExecutorStateUseCase)
    }

    /**
     * Должен передать юзкейсу обновленные данные.
     */
    @Test
    fun passUpdateExecutorStateToUpdateUseCase() {
        // Given:
        val inOrder = inOrder(updateExecutorStateUseCase)
        `when`(gateway.data).thenReturn(
                Single.just(Pair(ExecutorState.ONLINE, "lalala")),
                Single.just(Pair(ExecutorState.WAITING_FOR_CLIENT, "lelele")),
                Single.just(Pair(ExecutorState.PAYMENT_CONFIRMATION, null)),
                Single.just(Pair(ExecutorState.ORDER_FULFILLMENT, "lilili"))
        )

        // Action:
        useCase.proceedToNextState.test()
        useCase.proceedToNextState.test()
        useCase.proceedToNextState.test()
        useCase.proceedToNextState.test()

        // Effect:
        inOrder.verify(updateExecutorStateUseCase).updateWith(ExecutorState.ONLINE)
        inOrder.verify(updateExecutorStateUseCase).updateWith(ExecutorState.WAITING_FOR_CLIENT)
        inOrder.verify(updateExecutorStateUseCase).updateWith(ExecutorState.PAYMENT_CONFIRMATION)
        inOrder.verify(updateExecutorStateUseCase).updateWith(ExecutorState.ORDER_FULFILLMENT)
    }

    /* Проверяем работу с юзкейсом обновления данных */

    /**
     * Не должен трогать юзкейс до прихода данных.
     */
    @Test
    fun doNotTouchUpdatedDataUseCase() {
        // Action:
        useCase.proceedToNextState.test()
        useCase.proceedToNextState.test()
        useCase.proceedToNextState.test()
        useCase.proceedToNextState.test()

        // Effect:
        verifyNoInteractions(updateUseCase)
    }

    /**
     * Не должен трогать юзкейс при ошибках.
     */
    @Test
    fun doNotTouchUpdatedDataUseCaseOnErrors() {
        // Given:
        `when`(gateway.data).thenReturn(
                Single.error(DataMappingException()),
                Single.error(IOException()),
                Single.error(NullPointerException())
        )

        // Action:
        useCase.proceedToNextState.test()
        useCase.proceedToNextState.test()
        useCase.proceedToNextState.test()
        useCase.proceedToNextState.test()

        // Effect:
        verifyNoInteractions(updateUseCase)
    }

    /**
     * Должен передать юзкейсу обновленные данные.
     */
    @Test
    fun passUpdatedDataToUpdateUseCase() {
        // Given:
        val inOrder = inOrder(updateUseCase)
        `when`(gateway.data).thenReturn(
                Single.just(Pair(ExecutorState.ONLINE, "lalala")),
                Single.just(Pair(ExecutorState.WAITING_FOR_CLIENT, "lelele")),
                Single.just(Pair(ExecutorState.PAYMENT_CONFIRMATION, null)),
                Single.just(Pair(ExecutorState.ORDER_FULFILLMENT, "lilili"))
        )

        // Action:
        useCase.proceedToNextState.test()
        useCase.proceedToNextState.test()
        useCase.proceedToNextState.test()
        useCase.proceedToNextState.test()

        // Effect:
        inOrder.verify(updateUseCase).updateWith("lalala")
        inOrder.verify(updateUseCase).updateWith("lelele")
        inOrder.verify(updateUseCase).updateWith("lilili")
    }

    /* Проверяем ответы на запрос следующего статуса */

    /**
     * Должен ответить ошибкой маппинга.
     */
    @Test
    fun answerDataMappingError() {
        // Given:
        `when`(gateway.data).thenReturn(Single.error(DataMappingException()))

        // Action:
        val test = useCase.proceedToNextState.test()

        // Effect:
        test.assertError(DataMappingException::class.java)
        test.assertNoValues()
        test.assertNotComplete()
    }

    /**
     * Должен ответить следующим статусом исполнителя до завершения.
     */
    @Test
    fun answerWithOrdersBeforeComplete() {
        // Given:
        `when`(gateway.data).thenReturn(Single.just(Pair(ExecutorState.ONLINE, "lalala")))

        // Action:
        val test = useCase.proceedToNextState.test()

        // Effect:
        test.assertNoValues()
        test.assertComplete()
        test.assertNoErrors()
    }
}