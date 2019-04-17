package com.cargopull.executor_driver.interactor

import com.cargopull.executor_driver.UseCaseThreadTestRule
import com.cargopull.executor_driver.backend.web.NoNetworkException
import com.cargopull.executor_driver.entity.ExecutorState
import com.cargopull.executor_driver.entity.Order
import com.cargopull.executor_driver.entity.OrderOfferDecisionException
import com.cargopull.executor_driver.entity.OrderOfferExpiredException
import com.cargopull.executor_driver.gateway.DataMappingException
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.functions.Action
import org.junit.Before
import org.junit.ClassRule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyBoolean
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class OrderConfirmationUseCaseTest {

    companion object {

        @ClassRule
        @JvmField
        val classRule = UseCaseThreadTestRule()
    }

    private lateinit var useCase: OrderConfirmationUseCase

    @Mock
    private lateinit var orderUseCase: OrderUseCase
    @Mock
    private lateinit var orderDecisionUseCase: OrderDecisionUseCase
    @Mock
    private lateinit var orderConfirmationGateway: OrderConfirmationGateway<String?>
    @Mock
    private lateinit var orderConfirmationGateway2: OrderConfirmationGateway<Int?>
    @Mock
    private lateinit var order: Order
    @Mock
    private lateinit var order2: Order
    @Mock
    private lateinit var action: Action
    @Mock
    private lateinit var ordersUseCase: OrdersUseCase
    @Mock
    private lateinit var updateExecutorStateUseCase: DataUpdateUseCase<ExecutorState>
    @Mock
    private lateinit var updateDataUseCase: DataUpdateUseCase<String>

    @Before
    fun setUp() {
        ExecutorState.ONLINE.data = null
        `when`(orderUseCase.orders).thenReturn(Flowable.never())
        `when`(orderConfirmationGateway.sendDecision(any(), anyBoolean()))
                .thenReturn(Single.never<Pair<ExecutorState?, String?>>())
        useCase = OrderConfirmationUseCaseImpl(orderUseCase, orderConfirmationGateway,
                orderDecisionUseCase, ordersUseCase, updateExecutorStateUseCase, updateDataUseCase)
    }

    /* Проверяем работу с юзкейсом заказа */

    /**
     * Должен запросить у юзкейса заказа получение заказов при отправке решения.
     */
    @Test
    fun askOrderUseCaseForOrdersOnSendDecision() {
        // Действие:
        useCase.sendDecision(true).test()
        useCase.sendDecision(false).test()

        // Результат:
        verify<OrderUseCase>(orderUseCase, times(2)).orders
        verifyNoMoreInteractions(orderUseCase)
    }

    /**
     * Должен запросить у юзкейса заказа получение заказов при запросе таймаутов.
     */
    @Test
    fun askOrderUseCaseForOrdersOnGetTimeout() {
        // Действие:
        useCase.orderDecisionTimeout.test()
        useCase.orderDecisionTimeout.test()

        // Результат:
        verify<OrderUseCase>(orderUseCase, times(2)).orders
        verifyNoMoreInteractions(orderUseCase)
    }

    /* Проверяем работу с юзкейсом принятия решения по заказу */

    /**
     * Не должно ничего сломаться, если юзкейса принятия решения нет.
     */
    @Test
    fun shouldNotCrashIfNoDecisionUseCaseSet() {
        // Дано:
        `when`(orderUseCase.orders).thenReturn(Flowable.just(order).concatWith(Flowable.never()))
        `when`(orderConfirmationGateway.sendDecision(order, false))
                .thenReturn(Single.just(Pair(ExecutorState.ONLINE, "success")))
        `when`(orderConfirmationGateway.sendDecision(order, true))
                .thenReturn(Single.just(Pair(ExecutorState.ONLINE, "success")))
        useCase = OrderConfirmationUseCaseImpl(orderUseCase, orderConfirmationGateway, null,
                ordersUseCase, updateExecutorStateUseCase, updateDataUseCase)

        // Действие:
        useCase.sendDecision(false).test()
        useCase.sendDecision(true).test()

        // Результат:
        verifyZeroInteractions(orderDecisionUseCase)
    }

    /**
     * Должен запросить у юзкейса заказа деактуализацию заказов.
     */
    @Test
    fun askOrderDecisionUseCaseToSetCurrentOrderExpired() {
        // Дано:
        `when`(orderUseCase.orders).thenReturn(Flowable.just(order).concatWith(Flowable.never()))
        `when`(orderConfirmationGateway.sendDecision(any(), anyBoolean()))
                .thenReturn(Single.just(Pair(ExecutorState.ONLINE, "success")))

        // Действие:
        useCase.sendDecision(true).test()
        useCase.sendDecision(false).test()

        // Результат:
        verify<OrderDecisionUseCase>(orderDecisionUseCase, times(2)).setOrderOfferDecisionMade()
        verifyNoMoreInteractions(orderDecisionUseCase)
    }

    /* Проверяем работу с гейтвеем */

    /**
     * Должен запросить у гейтвея передачу решений.
     */
    @Test
    fun askGatewayToSendDecisionsForOrders() {
        // Дано:
        `when`(orderUseCase.orders).thenReturn(Flowable.just(order).concatWith(Flowable.never()))

        // Действие:
        useCase.sendDecision(true).test()
        useCase.sendDecision(false).test()

        // Результат:
        verify(orderConfirmationGateway).sendDecision(order, true)
        verify(orderConfirmationGateway).sendDecision(order, false)
        verifyNoMoreInteractions(orderConfirmationGateway)
    }

    /**
     * Должен запросить у гейтвея передачу решений только для первого свежего заказа.
     */
    @Test
    fun askGatewayToSendDecisionsForFirstOrderOnly() {
        // Дано:
        `when`(orderUseCase.orders)
                .thenReturn(Flowable.just(order, order2).concatWith(Flowable.never()))

        // Действие:
        useCase.sendDecision(true).test()
        useCase.sendDecision(false).test()

        // Результат:
        verify(orderConfirmationGateway, times(2)).sendDecision(eq(order), anyBoolean())
        verifyNoMoreInteractions(orderConfirmationGateway)
    }

    /**
     * Должен игнорировать если пришел тот же заказ.
     */
    @Test
    fun ignoreForSameNextOrder() {
        // Дано:
        val inOrder = inOrder(orderConfirmationGateway)
        `when`(orderUseCase.orders)
                .thenReturn(Flowable.just(order, order).concatWith(Flowable.never()))
        `when`(orderConfirmationGateway.sendDecision(any(), anyBoolean()))
                .thenReturn(Single.never<Pair<ExecutorState?, String?>>().doOnDispose(action))

        // Действие:
        useCase.sendDecision(true).test()
        useCase.sendDecision(false).test()

        // Результат:
        inOrder.verify(orderConfirmationGateway).sendDecision(order, true)
        inOrder.verify(orderConfirmationGateway).sendDecision(order, false)
        verifyNoMoreInteractions(orderConfirmationGateway, action)
    }

    /**
     * Должен отменить запрос у гейтвея на передачу решений если пришел новый заказ.
     */
    @Test
    @Throws(Exception::class)
    fun cancelGatewayToSendDecisionsForNextOrder() {
        // Дано:
        val inOrder = inOrder(orderConfirmationGateway, action)
        `when`(orderUseCase.orders)
                .thenReturn(Flowable.just(order, order2).concatWith(Flowable.never()))
        `when`(orderConfirmationGateway.sendDecision(any(), anyBoolean()))
                .thenReturn(Single.never<Pair<ExecutorState?, String?>>().doOnDispose(action))

        // Действие:
        useCase.sendDecision(true).test()
        useCase.sendDecision(false).test()

        // Результат:
        inOrder.verify(orderConfirmationGateway).sendDecision(order, true)
        inOrder.verify(action).run()
        inOrder.verify(orderConfirmationGateway).sendDecision(order, false)
        inOrder.verify(action).run()
        verifyNoMoreInteractions(orderConfirmationGateway, action)
    }

    /**
     * Должен отменить запрос у гейтвея на передачу решений если заказ истек.
     */
    @Test
    @Throws(Exception::class)
    fun cancelGatewayToSendDecisionsForOrderExpired() {
        // Дано:
        val inOrder = inOrder(orderConfirmationGateway, action)
        `when`(orderUseCase.orders).thenReturn(
                Flowable.just(order).concatWith(Flowable.error(OrderOfferExpiredException(""))))
        `when`(orderConfirmationGateway.sendDecision(any(), anyBoolean()))
                .thenReturn(Single.never<Pair<ExecutorState?, String?>>().doOnDispose(action))

        // Действие:
        useCase.sendDecision(true).test()
        useCase.sendDecision(false).test()

        // Результат:
        inOrder.verify(orderConfirmationGateway).sendDecision(order, true)
        inOrder.verify(action).run()
        inOrder.verify(orderConfirmationGateway).sendDecision(order, false)
        inOrder.verify(action).run()
        verifyNoMoreInteractions(orderConfirmationGateway, action)
    }

    /* Проверяем работу с юзкейсом списка заказов */

    /**
     * Не должно ничего сломаться, если юзкейса заказов нет.
     */
    @Test
    fun shouldNotCrashIfNoUseCaseSet() {
        // Дано:
        `when`(orderUseCase.orders).thenReturn(Flowable.just(order).concatWith(Flowable.never()))
        `when`(orderConfirmationGateway.sendDecision(order, false))
                .thenReturn(Single.just(Pair(ExecutorState.ONLINE, "success")))
        `when`(orderConfirmationGateway.sendDecision(order, true))
                .thenReturn(Single.just(Pair(ExecutorState.ONLINE, "success")))
        useCase = OrderConfirmationUseCaseImpl(orderUseCase, orderConfirmationGateway,
                orderDecisionUseCase, null, updateExecutorStateUseCase, updateDataUseCase)

        // Действие:
        useCase.sendDecision(false).test()
        useCase.sendDecision(true).test()

        // Результат:
        verifyZeroInteractions(ordersUseCase)
    }

    /**
     * Должен передать юзкейсу успешно отвергнутый заказа.
     */
    @Test
    fun passRefusedOrderToOrdersUseCase() {
        // Дано:
        `when`(orderUseCase.orders).thenReturn(Flowable.just(order).concatWith(Flowable.never()))
        `when`(orderConfirmationGateway.sendDecision(order, false))
                .thenReturn(Single.just(Pair(ExecutorState.ONLINE, "success")))

        // Действие:
        useCase.sendDecision(false).test()

        // Результат:
        verify<OrdersUseCase>(ordersUseCase, only()).removeOrder(order)
    }

    /**
     * Должен передать юзкейсу успешно принятый заказа.
     */
    @Test
    fun passConfirmedOrderToOrdersUseCase() {
        // Дано:
        `when`(orderUseCase.orders).thenReturn(Flowable.just(order).concatWith(Flowable.never()))
        `when`(orderConfirmationGateway.sendDecision(order, true))
                .thenReturn(Single.just(Pair(ExecutorState.ONLINE, "success")))

        // Действие:
        useCase.sendDecision(true).test()

        // Результат:
        verify<OrdersUseCase>(ordersUseCase, only()).addOrder(order)
    }

    /**
     * Не должен трогать юзкейс если пришел новый заказ.
     */
    @Test
    fun doNotTouchOrdersUseCaseIfNextOrder() {
        // Дано:
        `when`(orderUseCase.orders)
                .thenReturn(Flowable.just(order, order2).concatWith(Flowable.never()))

        // Действие:
        useCase.sendDecision(true).test()

        // Результат:
        verifyZeroInteractions(ordersUseCase)
    }

    /**
     * Не должен трогать юзкейс если заказ истек.
     */
    @Test
    fun doNotTouchOrdersUseCaseIfOrderExpired() {
        // Дано:
        `when`(orderUseCase.orders).thenReturn(
                Flowable.just(order).concatWith(Flowable.error(OrderOfferExpiredException(""))))

        // Действие:
        useCase.sendDecision(true).test()

        // Результат:
        verifyZeroInteractions(ordersUseCase)
    }

    /* Проверяем работу с юзкейсом обновления статуса исполнителя */

    /**
     * Не должно ничего сломаться, если юзкейса обновления статусов нет.
     */
    @Test
    fun shouldNotCrashIfNoUpdateExecutorStateUseCaseSet() {
        // Дано:
        `when`(orderUseCase.orders).thenReturn(Flowable.just(order).concatWith(Flowable.never()))
        `when`(orderConfirmationGateway.sendDecision(order, false))
                .thenReturn(Single.just(Pair(ExecutorState.ONLINE, "success")))
        `when`(orderConfirmationGateway.sendDecision(order, true))
                .thenReturn(Single.just(Pair(ExecutorState.ONLINE, "success")))
        useCase = OrderConfirmationUseCaseImpl(orderUseCase, orderConfirmationGateway,
                orderDecisionUseCase, ordersUseCase, null, updateDataUseCase)

        // Действие:
        useCase.sendDecision(false).test()
        useCase.sendDecision(true).test()

        // Результат:
        verifyZeroInteractions(updateExecutorStateUseCase)
    }

    /**
     * Не должен трогать юзкейс если новый статус не пришел после успешно отвергнутого заказа.
     */
    @Test
    fun doNotTouchUpdateExecutorStateUseCaseIfNoExecutorStateReturnedForRefusedOrder() {
        // Дано:
        `when`(orderUseCase.orders).thenReturn(Flowable.just(order).concatWith(Flowable.never()))
        `when`(orderConfirmationGateway.sendDecision(order, false))
                .thenReturn(Single.just(Pair(null, "success")))

        // Действие:
        useCase.sendDecision(false).test()

        // Результат:
        verifyZeroInteractions(updateExecutorStateUseCase)
    }

    /**
     * Не должен трогать юзкейс если новый статус не пришел после успешно принятого заказа.
     */
    @Test
    fun doNotTouchUpdateExecutorStateUseCaseIfNoExecutorStateReturnedForConfirmedOrder() {
        // Дано:
        `when`(orderUseCase.orders).thenReturn(Flowable.just(order).concatWith(Flowable.never()))
        `when`(orderConfirmationGateway.sendDecision(order, true))
                .thenReturn(Single.just(Pair(null, "success")))

        // Действие:
        useCase.sendDecision(true).test()

        // Результат:
        verifyZeroInteractions(updateExecutorStateUseCase)
    }

    /**
     * Должен передать юзкейсу статус после успешно отвергнутого заказа.
     */
    @Test
    fun passStatusForRefusedOrderToUpdateExecutorStateUseCase() {
        // Дано:
        `when`(orderUseCase.orders).thenReturn(Flowable.just(order).concatWith(Flowable.never()))
        `when`(orderConfirmationGateway.sendDecision(order, false))
                .thenReturn(Single.just(Pair(ExecutorState.ONLINE, "success")))

        // Действие:
        useCase.sendDecision(false).test()

        // Результат:
        verify(updateExecutorStateUseCase, only()).updateWith(ExecutorState.ONLINE)
    }

    /**
     * Должен передать юзкейсу статус после успешно принятого заказа.
     */
    @Test
    fun passStatusForConfirmedOrderToUpdateExecutorStateUseCase() {
        // Дано:
        `when`(orderUseCase.orders).thenReturn(Flowable.just(order).concatWith(Flowable.never()))
        `when`(orderConfirmationGateway.sendDecision(order, true))
                .thenReturn(Single.just(Pair(ExecutorState.ONLINE, "success")))

        // Действие:
        useCase.sendDecision(true).test()

        // Результат:
        verify(updateExecutorStateUseCase, only()).updateWith(ExecutorState.ONLINE)
    }

    /**
     * Не должен трогать юзкейс если пришел новый заказ.
     */
    @Test
    fun doNotTouchUpdateExecutorStateUseCaseIfNextOrder() {
        // Дано:
        `when`(orderUseCase.orders)
                .thenReturn(Flowable.just(order, order2).concatWith(Flowable.never()))

        // Действие:
        useCase.sendDecision(true).test()

        // Результат:
        verifyZeroInteractions(updateExecutorStateUseCase)
    }

    /**
     * Не должен трогать юзкейс если заказ истек.
     */
    @Test
    fun doNotTouchUpdateExecutorStateUseCaseIfOrderExpired() {
        // Дано:
        `when`(orderUseCase.orders).thenReturn(
                Flowable.just(order).concatWith(Flowable.error(OrderOfferExpiredException(""))))

        // Действие:
        useCase.sendDecision(true).test()

        // Результат:
        verifyZeroInteractions(updateExecutorStateUseCase)
    }

    /* Проверяем работу с юзкейсом обновления данных */

    /**
     * Не должно ничего сломаться, если юзкейса обновления данных нет.
     */
    @Test
    fun shouldNotCrashIfNoUpdateDataUseCaseSet() {
        // Дано:
        `when`(orderUseCase.orders).thenReturn(Flowable.just(order).concatWith(Flowable.never()))
        `when`(orderConfirmationGateway.sendDecision(order, false))
                .thenReturn(Single.just(Pair(ExecutorState.ONLINE, "success")))
        `when`(orderConfirmationGateway.sendDecision(order, true))
                .thenReturn(Single.just(Pair(ExecutorState.ONLINE, "success")))
        useCase = OrderConfirmationUseCaseImpl(orderUseCase, orderConfirmationGateway,
                orderDecisionUseCase, ordersUseCase, updateExecutorStateUseCase, null)

        // Действие:
        useCase.sendDecision(false).test()
        useCase.sendDecision(true).test()

        // Результат:
        verifyZeroInteractions(updateDataUseCase)
    }

    /**
     * Должен передать юзкейсу данные после успешно отвергнутого заказа.
     */
    @Test
    fun doNotTouchUpdateDataUseCaseIfNoExecutorStateReturnedForRefusedOrder() {
        // Дано:
        `when`(orderUseCase.orders).thenReturn(Flowable.just(order).concatWith(Flowable.never()))
        `when`(orderConfirmationGateway.sendDecision(order, false))
                .thenReturn(Single.just(Pair(ExecutorState.ONLINE, null)))

        // Действие:
        useCase.sendDecision(false).test()

        // Результат:
        verifyZeroInteractions(updateDataUseCase)
    }

    /**
     * Должен передать юзкейсу данные после успешно отвергнутого заказа.
     */
    @Test
    fun doNotTouchUpdateDataUseCaseIfNoExecutorStateReturnedForConfirmedOrder() {
        // Дано:
        `when`(orderUseCase.orders).thenReturn(Flowable.just(order).concatWith(Flowable.never()))
        `when`(orderConfirmationGateway.sendDecision(order, true))
                .thenReturn(Single.just(Pair(ExecutorState.ONLINE, null)))

        // Действие:
        useCase.sendDecision(true).test()

        // Результат:
        verifyZeroInteractions(updateDataUseCase)
    }

    /**
     * Должен передать юзкейсу данные после успешно отвергнутого заказа.
     */
    @Test
    fun passStatusForRefusedOrderToUpdateDataUseCase() {
        // Дано:
        `when`(orderUseCase.orders).thenReturn(Flowable.just(order).concatWith(Flowable.never()))
        `when`(orderConfirmationGateway.sendDecision(order, false))
                .thenReturn(Single.just(Pair(ExecutorState.ONLINE, "success")))

        // Действие:
        useCase.sendDecision(false).test()

        // Результат:
        verify(updateDataUseCase, only()).updateWith("success")
    }

    /**
     * Должен передать юзкейсу данные после успешно принятого заказа.
     */
    @Test
    fun passStatusForConfirmedOrderToUpdateDataUseCase() {
        // Дано:
        `when`(orderUseCase.orders).thenReturn(Flowable.just(order).concatWith(Flowable.never()))
        `when`(orderConfirmationGateway.sendDecision(order, true))
                .thenReturn(Single.just(Pair(ExecutorState.ONLINE, "success")))

        // Действие:
        useCase.sendDecision(true).test()

        // Результат:
        verify(updateDataUseCase, only()).updateWith("success")
    }

    /**
     * Не должен трогать юзкейс если пришел новый заказ.
     */
    @Test
    fun doNotTouchUpdateDataUseCaseIfNextOrder() {
        // Дано:
        `when`(orderUseCase.orders)
                .thenReturn(Flowable.just(order, order2).concatWith(Flowable.never()))

        // Действие:
        useCase.sendDecision(true).test()

        // Результат:
        verifyZeroInteractions(updateDataUseCase)
    }

    /**
     * Не должен трогать юзкейс если заказ истек.
     */
    @Test
    fun doNotTouchUpdateDataUseCaseIfOrderExpired() {
        // Дано:
        `when`(orderUseCase.orders).thenReturn(
                Flowable.just(order).concatWith(Flowable.error(OrderOfferExpiredException(""))))

        // Действие:
        useCase.sendDecision(true).test()

        // Результат:
        verifyZeroInteractions(updateDataUseCase)
    }

    /* Проверяем ответы на запрос отправки решения */

    /**
     * Должен ответить ошибкой маппинга на запрос таймаутов.
     */
    @Test
    fun answerDataMappingErrorForGetTimeouts() {
        // Дано:
        `when`(orderUseCase.orders).thenReturn(Flowable.error(DataMappingException()))

        // Действие:
        val test = useCase.orderDecisionTimeout.test()

        // Результат:
        test.assertError(DataMappingException::class.java)
        test.assertNoValues()
        test.assertNotComplete()
    }

    /**
     * Должен ответить ошибкой не актуальности заказа на запрос таймаутов.
     */
    @Test
    fun answerOrderExpiredErrorForGetTimeoutsIfErrorAfterValue() {
        // Дано:
        `when`(order.id).thenReturn(101L)
        `when`(order.timeout).thenReturn(12345L)
        `when`(orderUseCase.orders).thenReturn(
                Flowable.just(order).concatWith(Flowable.error(OrderOfferExpiredException("")))
        )

        // Действие:
        val test = useCase.orderDecisionTimeout.test()

        // Результат:
        test.assertError(OrderOfferExpiredException::class.java)
        test.assertValue(Pair(101L, 12345L))
        test.assertNotComplete()
    }

    /**
     * Должен ответить ошибкой не актуальности заказа на подтверждение.
     */
    @Test
    fun answerWithTimeoutsForGetTimeouts() {
        // Дано:
        `when`(order.id).thenReturn(101L, 202L)
        `when`(order.timeout).thenReturn(12345L, 54321L)
        `when`(order2.id).thenReturn(303L)
        `when`(order2.timeout).thenReturn(34543L)
        `when`(orderUseCase.orders)
                .thenReturn(Flowable.just(order, order2, order).concatWith(Flowable.never()))

        // Действие:
        val test = useCase.orderDecisionTimeout.test()

        // Результат:
        test.assertNoErrors()
        test.assertValueCount(3)
        test.assertValueAt(0, Pair(101L, 12345L))
        test.assertValueAt(1, Pair(303L, 34543L))
        test.assertValueAt(2, Pair(202L, 54321L))
        test.assertNotComplete()
    }

    /**
     * Должен ответить ошибкой маппинга на подтверждение.
     */
    @Test
    fun answerDataMappingErrorForAccept() {
        // Дано:
        `when`(orderUseCase.orders).thenReturn(Flowable.error(DataMappingException()))

        // Действие:
        val test = useCase.sendDecision(true).test()

        // Результат:
        test.assertError(DataMappingException::class.java)
        test.assertNoValues()
        test.assertNotComplete()
    }

    /**
     * Должен ответить ошибкой не актуальности заказа на подтверждение.
     */
    @Test
    fun ingonreSameSecondSecondValue() {
        // Дано:
        `when`(orderUseCase.orders)
                .thenReturn(Flowable.just(order, order).concatWith(Flowable.never()))

        // Действие:
        val test = useCase.sendDecision(true).test()

        // Результат:
        test.assertNoErrors()
        test.assertNoValues()
        test.assertNotComplete()
    }

    /**
     * Должен ответить ошибкой не актуальности заказа на подтверждение.
     */
    @Test
    fun answerOrderExpiredErrorForAcceptIfSecondValue() {
        // Дано:
        `when`(orderUseCase.orders)
                .thenReturn(Flowable.just(order, order2).concatWith(Flowable.never()))

        // Действие:
        val test = useCase.sendDecision(true).test()

        // Результат:
        test.assertError(OrderOfferDecisionException::class.java)
        test.assertNoValues()
        test.assertNotComplete()
    }

    /**
     * Должен ответить ошибкой не актуальности заказа на подтверждение.
     */
    @Test
    fun answerOrderExpiredErrorForAcceptIfErrorAfterValue() {
        // Дано:
        `when`(orderUseCase.orders).thenReturn(
                Flowable.just(order).concatWith(Flowable.error(OrderOfferExpiredException("")))
        )

        // Действие:
        val test = useCase.sendDecision(true).test()

        // Результат:
        test.assertError(OrderOfferExpiredException::class.java)
        test.assertNoValues()
        test.assertNotComplete()
    }

    /**
     * Должен ответить ошибкой сети на подтверждение.
     */
    @Test
    fun answerNoNetworkErrorForAccept() {
        // Дано:
        `when`(orderUseCase.orders).thenReturn(Flowable.just(order).concatWith(Flowable.never()))
        `when`(orderConfirmationGateway.sendDecision(any(), anyBoolean()))
                .thenReturn(Single.error(NoNetworkException()))

        // Действие:
        val test = useCase.sendDecision(true).test()

        // Результат:
        test.assertError(NoNetworkException::class.java)
        test.assertNoValues()
        test.assertNotComplete()
    }

    /**
     * Должен ответить ошибкой сети на отказ.
     */
    @Test
    fun answerNoNetworkErrorForDecline() {
        // Дано:
        `when`(orderUseCase.orders).thenReturn(Flowable.just(order).concatWith(Flowable.never()))
        `when`(orderConfirmationGateway.sendDecision(any(), anyBoolean()))
                .thenReturn(Single.error(NoNetworkException()))

        // Действие:
        val test = useCase.sendDecision(false).test()

        // Результат:
        test.assertError(NoNetworkException::class.java)
        test.assertNoValues()
        test.assertNotComplete()
    }

    /**
     * Должен ответить успехом передачи подтверждения с сообщением.
     */
    @Test
    fun answerSendAcceptSuccessful() {
        // Дано:
        ExecutorState.ONLINE.data = "successor"
        `when`(orderUseCase.orders).thenReturn(Flowable.just(order).concatWith(Flowable.never()))
        `when`(orderConfirmationGateway.sendDecision(any(), anyBoolean()))
                .thenReturn(Single.just(Pair(ExecutorState.ONLINE, "success")))

        // Действие:
        val test = useCase.sendDecision(true).test()

        // Результат:
        test.assertComplete()
        test.assertNoErrors()
        test.assertValue("success")
    }

    /**
     * Должен ответить успехом передачи отказа с сообщением.
     */
    @Test
    fun answerSendDeclineSuccessful() {
        // Дано:
        ExecutorState.ONLINE.data = "successor"
        `when`(orderUseCase.orders).thenReturn(Flowable.just(order).concatWith(Flowable.never()))
        `when`(orderConfirmationGateway.sendDecision(any(), anyBoolean()))
                .thenReturn(Single.just(Pair(ExecutorState.ONLINE, "success")))

        // Действие:
        val test = useCase.sendDecision(false).test()

        // Результат:
        test.assertComplete()
        test.assertNoErrors()
        test.assertValue("success")
    }

    /**
     * Должен ответить успехом передачи подтверждения с пустым текстом.
     */
    @Test
    fun answerSendAcceptSuccessfulWithEmptyMessage() {
        // Дано:
        ExecutorState.ONLINE.data = "successor"
        `when`(orderUseCase.orders).thenReturn(Flowable.just(order).concatWith(Flowable.never()))
        `when`(orderConfirmationGateway.sendDecision(any(), anyBoolean()))
                .thenReturn(Single.just(Pair(ExecutorState.ONLINE, null)))

        // Действие:
        val test = useCase.sendDecision(true).test()

        // Результат:
        test.assertComplete()
        test.assertNoErrors()
        test.assertValue("successor")
    }

    /**
     * Должен ответить успехом передачи отказа с пустым текстом.
     */
    @Test
    fun answerSendDeclineSuccessfulWithEmptyMessage() {
        // Дано:
        ExecutorState.ONLINE.data = "successor"
        `when`(orderUseCase.orders).thenReturn(Flowable.just(order).concatWith(Flowable.never()))
        `when`(orderConfirmationGateway.sendDecision(any(), anyBoolean()))
                .thenReturn(Single.just(Pair(ExecutorState.ONLINE, null)))

        // Действие:
        val test = useCase.sendDecision(false).test()

        // Результат:
        test.assertComplete()
        test.assertNoErrors()
        test.assertValue("successor")
    }

    /**
     * Должен ответить успехом передачи подтверждения с сообщением если статуса нет.
     */
    @Test
    fun answerSendAcceptSuccessfulIfExecutorStateIsNull() {
        // Дано:
        `when`(orderUseCase.orders).thenReturn(Flowable.just(order).concatWith(Flowable.never()))
        `when`(orderConfirmationGateway.sendDecision(any(), anyBoolean()))
                .thenReturn(Single.just(Pair(null, "success")))

        // Действие:
        val test = useCase.sendDecision(true).test()

        // Результат:
        test.assertComplete()
        test.assertNoErrors()
        test.assertValue("success")
    }

    /**
     * Должен ответить успехом передачи отказа с сообщением если статуса нет.
     */
    @Test
    fun answerSendDeclineSuccessfulIfExecutorStateIsNull() {
        // Дано:
        `when`(orderUseCase.orders).thenReturn(Flowable.just(order).concatWith(Flowable.never()))
        `when`(orderConfirmationGateway.sendDecision(any(), anyBoolean()))
                .thenReturn(Single.just(Pair(null, "success")))

        // Действие:
        val test = useCase.sendDecision(false).test()

        // Результат:
        test.assertComplete()
        test.assertNoErrors()
        test.assertValue("success")
    }

    /**
     * Должен ответить успехом передачи подтверждения с текстом из статуса.
     */
    @Test
    fun answerAcceptedSuccessfulWithDataFromExecutorState() {
        // Дано:
        ExecutorState.ONLINE.data = "successor"
        `when`(orderUseCase.orders).thenReturn(Flowable.just(order).concatWith(Flowable.never()))
        `when`(orderConfirmationGateway2.sendDecision(any(), anyBoolean()))
                .thenReturn(Single.just(Pair(ExecutorState.ONLINE, 0)))
        useCase = OrderConfirmationUseCaseImpl(orderUseCase, orderConfirmationGateway2,
                orderDecisionUseCase, ordersUseCase, updateExecutorStateUseCase, null)

        // Действие:
        val test = useCase.sendDecision(true).test()

        // Результат:
        test.assertComplete()
        test.assertNoErrors()
        test.assertValue("successor")
    }

    /**
     * Должен ответить успехом передачи отказа с текстом из статуса.
     */
    @Test
    fun answerDeclinedSuccessfulWithDataFromExecutorState() {
        // Дано:
        ExecutorState.ONLINE.data = "successor"
        `when`(orderUseCase.orders).thenReturn(Flowable.just(order).concatWith(Flowable.never()))
        `when`(orderConfirmationGateway2.sendDecision(any(), anyBoolean()))
                .thenReturn(Single.just(Pair(ExecutorState.ONLINE, 0)))
        useCase = OrderConfirmationUseCaseImpl(orderUseCase, orderConfirmationGateway2,
                orderDecisionUseCase, ordersUseCase, updateExecutorStateUseCase, null)

        // Действие:
        val test = useCase.sendDecision(false).test()

        // Результат:
        test.assertComplete()
        test.assertNoErrors()
        test.assertValue("successor")
    }

    /**
     * Должен ответить успехом передачи подтверждения с пустым текстом с текстом из статуса если результата нет.
     */
    @Test
    fun answerAcceptedSuccessfulWithEmptyMessageIfExecutorStateAndResultAreNull() {
        // Дано:
        ExecutorState.ONLINE.data = "successor"
        `when`(orderUseCase.orders).thenReturn(Flowable.just(order).concatWith(Flowable.never()))
        `when`(orderConfirmationGateway2.sendDecision(any(), anyBoolean()))
                .thenReturn(Single.just(Pair(ExecutorState.ONLINE, null)))
        useCase = OrderConfirmationUseCaseImpl(orderUseCase, orderConfirmationGateway2,
                orderDecisionUseCase, ordersUseCase, updateExecutorStateUseCase, null)

        // Действие:
        val test = useCase.sendDecision(true).test()

        // Результат:
        test.assertComplete()
        test.assertNoErrors()
        test.assertValue("successor")
    }

    /**
     * Должен ответить успехом передачи отказа с пустым текстом с текстом из статуса если результата нет.
     */
    @Test
    fun answerDeclinedSuccessfulWithEmptyMessageIfExecutorStateAndResultAreNull() {
        // Дано:
        ExecutorState.ONLINE.data = "successor"
        `when`(orderUseCase.orders).thenReturn(Flowable.just(order).concatWith(Flowable.never()))
        `when`(orderConfirmationGateway2.sendDecision(any(), anyBoolean()))
                .thenReturn(Single.just(Pair(ExecutorState.ONLINE, null)))
        useCase = OrderConfirmationUseCaseImpl(orderUseCase, orderConfirmationGateway2,
                orderDecisionUseCase, ordersUseCase, updateExecutorStateUseCase, null)

        // Действие:
        val test = useCase.sendDecision(false).test()

        // Результат:
        test.assertComplete()
        test.assertNoErrors()
        test.assertValue("successor")
    }

    /**
     * Должен ответить успехом передачи подтверждения с пустым текстом если в статусе нет данных.
     */
    @Test
    fun answerAcceptedSuccessfulWithEmptyMessageIfDataInExecutorStateIsNull() {
        // Дано:
        `when`(orderUseCase.orders).thenReturn(Flowable.just(order).concatWith(Flowable.never()))
        `when`(orderConfirmationGateway2.sendDecision(any(), anyBoolean()))
                .thenReturn(Single.just(Pair(ExecutorState.ONLINE, 0)))
        useCase = OrderConfirmationUseCaseImpl(orderUseCase, orderConfirmationGateway2,
                orderDecisionUseCase, ordersUseCase, updateExecutorStateUseCase, null)

        // Действие:
        val test = useCase.sendDecision(true).test()

        // Результат:
        test.assertComplete()
        test.assertNoErrors()
        test.assertValue("")
    }

    /**
     * Должен ответить успехом передачи отказа с пустым текстом если в статусе нет данных.
     */
    @Test
    fun answerDeclinedSuccessfulWithEmptyMessageIfDataInExecutorStateIsNull() {
        // Дано:
        `when`(orderUseCase.orders).thenReturn(Flowable.just(order).concatWith(Flowable.never()))
        `when`(orderConfirmationGateway2.sendDecision(any(), anyBoolean()))
                .thenReturn(Single.just(Pair(ExecutorState.ONLINE, 0)))
        useCase = OrderConfirmationUseCaseImpl(orderUseCase, orderConfirmationGateway2,
                orderDecisionUseCase, ordersUseCase, updateExecutorStateUseCase, null)

        // Действие:
        val test = useCase.sendDecision(false).test()

        // Результат:
        test.assertComplete()
        test.assertNoErrors()
        test.assertValue("")
    }

    /**
     * Должен ответить успехом передачи подтверждения с пустым текстом если статуса нет.
     */
    @Test
    fun answerAcceptedSuccessfulWithEmptyMessageIfExecutorStateIsNull() {
        // Дано:
        `when`(orderUseCase.orders).thenReturn(Flowable.just(order).concatWith(Flowable.never()))
        `when`(orderConfirmationGateway2.sendDecision(any(), anyBoolean()))
                .thenReturn(Single.just(Pair(null, 0)))
        useCase = OrderConfirmationUseCaseImpl(orderUseCase, orderConfirmationGateway2,
                orderDecisionUseCase, ordersUseCase, updateExecutorStateUseCase, null)

        // Действие:
        val test = useCase.sendDecision(true).test()

        // Результат:
        test.assertComplete()
        test.assertNoErrors()
        test.assertValue("")
    }

    /**
     * Должен ответить успехом передачи отказа с пустым текстом если статуса нет.
     */
    @Test
    fun answerDeclinedSuccessfulWithEmptyMessageIfExecutorStateIsNull() {
        // Дано:
        `when`(orderUseCase.orders).thenReturn(Flowable.just(order).concatWith(Flowable.never()))
        `when`(orderConfirmationGateway2.sendDecision(any(), anyBoolean()))
                .thenReturn(Single.just(Pair(null, 0)))
        useCase = OrderConfirmationUseCaseImpl(orderUseCase, orderConfirmationGateway2,
                orderDecisionUseCase, ordersUseCase, updateExecutorStateUseCase, null)

        // Действие:
        val test = useCase.sendDecision(false).test()

        // Результат:
        test.assertComplete()
        test.assertNoErrors()
        test.assertValue("")
    }

    private fun <T> any(): T {
        Mockito.any<T>()
        return uninitialized()
    }

    private fun <T> eq(value: T): T {
        Mockito.eq<T>(value)
        return uninitialized()
    }

    private fun <T> uninitialized(): T = null as T
}