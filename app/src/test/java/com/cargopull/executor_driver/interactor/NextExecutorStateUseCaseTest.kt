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
        // Действие:
        useCase.proceedToNextState.test()

        // Результат:
        verify(gateway, only()).data
    }

    /* Проверяем работу с юзкейсом обновления состояния водителя */

    /**
     * Не должен трогать юзкейс до прихода данных.
     */
    @Test
    fun doNotTouchUpdateExecutorStateUseCase() {
        // Действие:
        useCase.proceedToNextState.test()
        useCase.proceedToNextState.test()
        useCase.proceedToNextState.test()
        useCase.proceedToNextState.test()

        // Результат:
        verifyNoInteractions(updateExecutorStateUseCase)
    }

    /**
     * Не должен трогать юзкейс при ошибках.
     */
    @Test
    fun doNotTouchUpdateExecutorStateUseCaseOnErrors() {
        // Дано:
        `when`(gateway.data).thenReturn(
                Single.error(DataMappingException()),
                Single.error(IOException()),
                Single.error(NullPointerException())
        )

        // Действие:
        useCase.proceedToNextState.test()
        useCase.proceedToNextState.test()
        useCase.proceedToNextState.test()
        useCase.proceedToNextState.test()

        // Результат:
        verifyNoInteractions(updateExecutorStateUseCase)
    }

    /**
     * Должен передать юзкейсу обновленные данные.
     */
    @Test
    fun passUpdateExecutorStateToUpdateUseCase() {
        // Дано:
        val inOrder = inOrder(updateExecutorStateUseCase)
        `when`(gateway.data).thenReturn(
                Single.just(Pair(ExecutorState.ONLINE, "lalala")),
                Single.just(Pair(ExecutorState.WAITING_FOR_CLIENT, "lelele")),
                Single.just(Pair(ExecutorState.PAYMENT_CONFIRMATION, null)),
                Single.just(Pair(ExecutorState.ORDER_FULFILLMENT, "lilili"))
        )

        // Действие:
        useCase.proceedToNextState.test()
        useCase.proceedToNextState.test()
        useCase.proceedToNextState.test()
        useCase.proceedToNextState.test()

        // Результат:
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
        // Действие:
        useCase.proceedToNextState.test()
        useCase.proceedToNextState.test()
        useCase.proceedToNextState.test()
        useCase.proceedToNextState.test()

        // Результат:
        verifyNoInteractions(updateUseCase)
    }

    /**
     * Не должен трогать юзкейс при ошибках.
     */
    @Test
    fun doNotTouchUpdatedDataUseCaseOnErrors() {
        // Дано:
        `when`(gateway.data).thenReturn(
                Single.error(DataMappingException()),
                Single.error(IOException()),
                Single.error(NullPointerException())
        )

        // Действие:
        useCase.proceedToNextState.test()
        useCase.proceedToNextState.test()
        useCase.proceedToNextState.test()
        useCase.proceedToNextState.test()

        // Результат:
        verifyNoInteractions(updateUseCase)
    }

    /**
     * Должен передать юзкейсу обновленные данные.
     */
    @Test
    fun passUpdatedDataToUpdateUseCase() {
        // Дано:
        val inOrder = inOrder(updateUseCase)
        `when`(gateway.data).thenReturn(
                Single.just(Pair(ExecutorState.ONLINE, "lalala")),
                Single.just(Pair(ExecutorState.WAITING_FOR_CLIENT, "lelele")),
                Single.just(Pair(ExecutorState.PAYMENT_CONFIRMATION, null)),
                Single.just(Pair(ExecutorState.ORDER_FULFILLMENT, "lilili"))
        )

        // Действие:
        useCase.proceedToNextState.test()
        useCase.proceedToNextState.test()
        useCase.proceedToNextState.test()
        useCase.proceedToNextState.test()

        // Результат:
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
        // Дано:
        `when`(gateway.data).thenReturn(Single.error(DataMappingException()))

        // Действие:
        val test = useCase.proceedToNextState.test()

        // Результат:
        test.assertError(DataMappingException::class.java)
        test.assertNoValues()
        test.assertNotComplete()
    }

    /**
     * Должен ответить следующим статусом исполнителя до завершения.
     */
    @Test
    fun answerWithOrdersBeforeComplete() {
        // Дано:
        `when`(gateway.data).thenReturn(Single.just(Pair(ExecutorState.ONLINE, "lalala")))

        // Действие:
        val test = useCase.proceedToNextState.test()

        // Результат:
        test.assertNoValues()
        test.assertComplete()
        test.assertNoErrors()
    }
}