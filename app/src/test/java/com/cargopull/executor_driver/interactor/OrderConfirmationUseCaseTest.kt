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
    private lateinit var orderConfirmationGateway: OrderConfirmationGateway<String>
    @Mock
    private lateinit var orderConfirmationGateway2: OrderConfirmationGateway<Int>
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
        // Action:
        useCase.sendDecision(true).test()
        useCase.sendDecision(false).test()

        // Effect:
        verify<OrderUseCase>(orderUseCase, times(2)).orders
        verifyNoMoreInteractions(orderUseCase)
    }

    /**
     * Должен запросить у юзкейса заказа получение заказов при запросе таймаутов.
     */
    @Test
    fun askOrderUseCaseForOrdersOnGetTimeout() {
        // Action:
        useCase.orderDecisionTimeout.test()
        useCase.orderDecisionTimeout.test()

        // Effect:
        verify<OrderUseCase>(orderUseCase, times(2)).orders
        verifyNoMoreInteractions(orderUseCase)
    }

    /* Проверяем работу с юзкейсом принятия решения по заказу */

    /**
     * Не должно ничего сломаться, если юзкейса принятия решения нет.
     */
    @Test
    fun shouldNotCrashIfNoDecisionUseCaseSet() {
        // Given:
        `when`(orderUseCase.orders).thenReturn(Flowable.just(order).concatWith(Flowable.never()))
        `when`(orderConfirmationGateway.sendDecision(order, false))
                .thenReturn(Single.just(Pair(ExecutorState.ONLINE, "success")))
        `when`(orderConfirmationGateway.sendDecision(order, true))
                .thenReturn(Single.just(Pair(ExecutorState.ONLINE, "success")))
        useCase = OrderConfirmationUseCaseImpl(orderUseCase, orderConfirmationGateway, null,
                ordersUseCase, updateExecutorStateUseCase, updateDataUseCase)

        // Action:
        useCase.sendDecision(false).test()
        useCase.sendDecision(true).test()

        // Effect:
        verifyNoInteractions(orderDecisionUseCase)
    }

    /**
     * Должен запросить у юзкейса заказа деактуализацию заказов.
     */
    @Test
    fun askOrderDecisionUseCaseToSetCurrentOrderExpired() {
        // Given:
        `when`(orderUseCase.orders).thenReturn(Flowable.just(order).concatWith(Flowable.never()))
        `when`(orderConfirmationGateway.sendDecision(any(), anyBoolean()))
                .thenReturn(Single.just(Pair(ExecutorState.ONLINE, "success")))

        // Action:
        useCase.sendDecision(true).test()
        useCase.sendDecision(false).test()

        // Effect:
        verify<OrderDecisionUseCase>(orderDecisionUseCase, times(2)).setOrderOfferDecisionMade()
        verifyNoMoreInteractions(orderDecisionUseCase)
    }

    /* Проверяем работу с гейтвеем */

    /**
     * Должен запросить у гейтвея передачу решений.
     */
    @Test
    fun askGatewayToSendDecisionsForOrders() {
        // Given:
        `when`(orderUseCase.orders).thenReturn(Flowable.just(order).concatWith(Flowable.never()))

        // Action:
        useCase.sendDecision(true).test()
        useCase.sendDecision(false).test()

        // Effect:
        verify(orderConfirmationGateway).sendDecision(order, true)
        verify(orderConfirmationGateway).sendDecision(order, false)
        verifyNoMoreInteractions(orderConfirmationGateway)
    }

    /**
     * Должен запросить у гейтвея передачу решений только для первого свежего заказа.
     */
    @Test
    fun askGatewayToSendDecisionsForFirstOrderOnly() {
        // Given:
        `when`(orderUseCase.orders)
                .thenReturn(Flowable.just(order, order2).concatWith(Flowable.never()))

        // Action:
        useCase.sendDecision(true).test()
        useCase.sendDecision(false).test()

        // Effect:
        verify(orderConfirmationGateway, times(2)).sendDecision(eq(order), anyBoolean())
        verifyNoMoreInteractions(orderConfirmationGateway)
    }

    /**
     * Должен игнорировать если пришел тот же заказ.
     */
    @Test
    fun ignoreForSameNextOrder() {
        // Given:
        val inOrder = inOrder(orderConfirmationGateway)
        `when`(orderUseCase.orders)
                .thenReturn(Flowable.just(order, order).concatWith(Flowable.never()))
        `when`(orderConfirmationGateway.sendDecision(any(), anyBoolean()))
                .thenReturn(Single.never<Pair<ExecutorState?, String?>>().doOnDispose(action))

        // Action:
        useCase.sendDecision(true).test()
        useCase.sendDecision(false).test()

        // Effect:
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
        // Given:
        val inOrder = inOrder(orderConfirmationGateway, action)
        `when`(orderUseCase.orders)
                .thenReturn(Flowable.just(order, order2).concatWith(Flowable.never()))
        `when`(orderConfirmationGateway.sendDecision(any(), anyBoolean()))
                .thenReturn(Single.never<Pair<ExecutorState?, String?>>().doOnDispose(action))

        // Action:
        useCase.sendDecision(true).test()
        useCase.sendDecision(false).test()

        // Effect:
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
        // Given:
        val inOrder = inOrder(orderConfirmationGateway, action)
        `when`(orderUseCase.orders).thenReturn(
                Flowable.just(order).concatWith(Flowable.error(OrderOfferExpiredException(""))))
        `when`(orderConfirmationGateway.sendDecision(any(), anyBoolean()))
                .thenReturn(Single.never<Pair<ExecutorState?, String?>>().doOnDispose(action))

        // Action:
        useCase.sendDecision(true).test()
        useCase.sendDecision(false).test()

        // Effect:
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
        // Given:
        `when`(orderUseCase.orders).thenReturn(Flowable.just(order).concatWith(Flowable.never()))
        `when`(orderConfirmationGateway.sendDecision(order, false))
                .thenReturn(Single.just(Pair(ExecutorState.ONLINE, "success")))
        `when`(orderConfirmationGateway.sendDecision(order, true))
                .thenReturn(Single.just(Pair(ExecutorState.ONLINE, "success")))
        useCase = OrderConfirmationUseCaseImpl(orderUseCase, orderConfirmationGateway,
                orderDecisionUseCase, null, updateExecutorStateUseCase, updateDataUseCase)

        // Action:
        useCase.sendDecision(false).test()
        useCase.sendDecision(true).test()

        // Effect:
        verifyNoInteractions(ordersUseCase)
    }

    /**
     * Должен передать юзкейсу успешно отвергнутый заказа.
     */
    @Test
    fun passRefusedOrderToOrdersUseCase() {
        // Given:
        `when`(orderUseCase.orders).thenReturn(Flowable.just(order).concatWith(Flowable.never()))
        `when`(orderConfirmationGateway.sendDecision(order, false))
                .thenReturn(Single.just(Pair(ExecutorState.ONLINE, "success")))

        // Action:
        useCase.sendDecision(false).test()

        // Effect:
        verify<OrdersUseCase>(ordersUseCase, only()).removeOrder(order)
    }

    /**
     * Должен передать юзкейсу успешно принятый заказа.
     */
    @Test
    fun passConfirmedOrderToOrdersUseCase() {
        // Given:
        `when`(orderUseCase.orders).thenReturn(Flowable.just(order).concatWith(Flowable.never()))
        `when`(orderConfirmationGateway.sendDecision(order, true))
                .thenReturn(Single.just(Pair(ExecutorState.ONLINE, "success")))

        // Action:
        useCase.sendDecision(true).test()

        // Effect:
        verify<OrdersUseCase>(ordersUseCase, only()).addOrder(order)
    }

    /**
     * Не должен трогать юзкейс если пришел новый заказ.
     */
    @Test
    fun doNotTouchOrdersUseCaseIfNextOrder() {
        // Given:
        `when`(orderUseCase.orders)
                .thenReturn(Flowable.just(order, order2).concatWith(Flowable.never()))

        // Action:
        useCase.sendDecision(true).test()

        // Effect:
        verifyNoInteractions(ordersUseCase)
    }

    /**
     * Не должен трогать юзкейс если заказ истек.
     */
    @Test
    fun doNotTouchOrdersUseCaseIfOrderExpired() {
        // Given:
        `when`(orderUseCase.orders).thenReturn(
                Flowable.just(order).concatWith(Flowable.error(OrderOfferExpiredException(""))))

        // Action:
        useCase.sendDecision(true).test()

        // Effect:
        verifyNoInteractions(ordersUseCase)
    }

    /* Проверяем работу с юзкейсом обновления статуса исполнителя */

    /**
     * Не должно ничего сломаться, если юзкейса обновления статусов нет.
     */
    @Test
    fun shouldNotCrashIfNoUpdateExecutorStateUseCaseSet() {
        // Given:
        `when`(orderUseCase.orders).thenReturn(Flowable.just(order).concatWith(Flowable.never()))
        `when`(orderConfirmationGateway.sendDecision(order, false))
                .thenReturn(Single.just(Pair(ExecutorState.ONLINE, "success")))
        `when`(orderConfirmationGateway.sendDecision(order, true))
                .thenReturn(Single.just(Pair(ExecutorState.ONLINE, "success")))
        useCase = OrderConfirmationUseCaseImpl(orderUseCase, orderConfirmationGateway,
                orderDecisionUseCase, ordersUseCase, null, updateDataUseCase)

        // Action:
        useCase.sendDecision(false).test()
        useCase.sendDecision(true).test()

        // Effect:
        verifyNoInteractions(updateExecutorStateUseCase)
    }

    /**
     * Не должен трогать юзкейс если новый статус не пришел после успешно отвергнутого заказа.
     */
    @Test
    fun doNotTouchUpdateExecutorStateUseCaseIfNoExecutorStateReturnedForRefusedOrder() {
        // Given:
        `when`(orderUseCase.orders).thenReturn(Flowable.just(order).concatWith(Flowable.never()))
        `when`(orderConfirmationGateway.sendDecision(order, false))
                .thenReturn(Single.just(Pair(null, "success")))

        // Action:
        useCase.sendDecision(false).test()

        // Effect:
        verifyNoInteractions(updateExecutorStateUseCase)
    }

    /**
     * Не должен трогать юзкейс если новый статус не пришел после успешно принятого заказа.
     */
    @Test
    fun doNotTouchUpdateExecutorStateUseCaseIfNoExecutorStateReturnedForConfirmedOrder() {
        // Given:
        `when`(orderUseCase.orders).thenReturn(Flowable.just(order).concatWith(Flowable.never()))
        `when`(orderConfirmationGateway.sendDecision(order, true))
                .thenReturn(Single.just(Pair(null, "success")))

        // Action:
        useCase.sendDecision(true).test()

        // Effect:
        verifyNoInteractions(updateExecutorStateUseCase)
    }

    /**
     * Должен передать юзкейсу статус после успешно отвергнутого заказа.
     */
    @Test
    fun passStatusForRefusedOrderToUpdateExecutorStateUseCase() {
        // Given:
        `when`(orderUseCase.orders).thenReturn(Flowable.just(order).concatWith(Flowable.never()))
        `when`(orderConfirmationGateway.sendDecision(order, false))
                .thenReturn(Single.just(Pair(ExecutorState.ONLINE, "success")))

        // Action:
        useCase.sendDecision(false).test()

        // Effect:
        verify(updateExecutorStateUseCase, only()).updateWith(ExecutorState.ONLINE)
    }

    /**
     * Должен передать юзкейсу статус после успешно принятого заказа.
     */
    @Test
    fun passStatusForConfirmedOrderToUpdateExecutorStateUseCase() {
        // Given:
        `when`(orderUseCase.orders).thenReturn(Flowable.just(order).concatWith(Flowable.never()))
        `when`(orderConfirmationGateway.sendDecision(order, true))
                .thenReturn(Single.just(Pair(ExecutorState.ONLINE, "success")))

        // Action:
        useCase.sendDecision(true).test()

        // Effect:
        verify(updateExecutorStateUseCase, only()).updateWith(ExecutorState.ONLINE)
    }

    /**
     * Не должен трогать юзкейс если пришел новый заказ.
     */
    @Test
    fun doNotTouchUpdateExecutorStateUseCaseIfNextOrder() {
        // Given:
        `when`(orderUseCase.orders)
                .thenReturn(Flowable.just(order, order2).concatWith(Flowable.never()))

        // Action:
        useCase.sendDecision(true).test()

        // Effect:
        verifyNoInteractions(updateExecutorStateUseCase)
    }

    /**
     * Не должен трогать юзкейс если заказ истек.
     */
    @Test
    fun doNotTouchUpdateExecutorStateUseCaseIfOrderExpired() {
        // Given:
        `when`(orderUseCase.orders).thenReturn(
                Flowable.just(order).concatWith(Flowable.error(OrderOfferExpiredException(""))))

        // Action:
        useCase.sendDecision(true).test()

        // Effect:
        verifyNoInteractions(updateExecutorStateUseCase)
    }

    /* Проверяем работу с юзкейсом обновления данных */

    /**
     * Не должно ничего сломаться, если юзкейса обновления данных нет.
     */
    @Test
    fun shouldNotCrashIfNoUpdateDataUseCaseSet() {
        // Given:
        `when`(orderUseCase.orders).thenReturn(Flowable.just(order).concatWith(Flowable.never()))
        `when`(orderConfirmationGateway.sendDecision(order, false))
                .thenReturn(Single.just(Pair(ExecutorState.ONLINE, "success")))
        `when`(orderConfirmationGateway.sendDecision(order, true))
                .thenReturn(Single.just(Pair(ExecutorState.ONLINE, "success")))
        useCase = OrderConfirmationUseCaseImpl(orderUseCase, orderConfirmationGateway,
                orderDecisionUseCase, ordersUseCase, updateExecutorStateUseCase, null)

        // Action:
        useCase.sendDecision(false).test()
        useCase.sendDecision(true).test()

        // Effect:
        verifyNoInteractions(updateDataUseCase)
    }

    /**
     * Должен передать юзкейсу данные после успешно отвергнутого заказа.
     */
    @Test
    fun doNotTouchUpdateDataUseCaseIfNoExecutorStateReturnedForRefusedOrder() {
        // Given:
        `when`(orderUseCase.orders).thenReturn(Flowable.just(order).concatWith(Flowable.never()))
        `when`(orderConfirmationGateway.sendDecision(order, false))
                .thenReturn(Single.just(Pair(ExecutorState.ONLINE, null)))

        // Action:
        useCase.sendDecision(false).test()

        // Effect:
        verifyNoInteractions(updateDataUseCase)
    }

    /**
     * Должен передать юзкейсу данные после успешно отвергнутого заказа.
     */
    @Test
    fun doNotTouchUpdateDataUseCaseIfNoExecutorStateReturnedForConfirmedOrder() {
        // Given:
        `when`(orderUseCase.orders).thenReturn(Flowable.just(order).concatWith(Flowable.never()))
        `when`(orderConfirmationGateway.sendDecision(order, true))
                .thenReturn(Single.just(Pair(ExecutorState.ONLINE, null)))

        // Action:
        useCase.sendDecision(true).test()

        // Effect:
        verifyNoInteractions(updateDataUseCase)
    }

    /**
     * Должен передать юзкейсу данные после успешно отвергнутого заказа.
     */
    @Test
    fun passStatusForRefusedOrderToUpdateDataUseCase() {
        // Given:
        `when`(orderUseCase.orders).thenReturn(Flowable.just(order).concatWith(Flowable.never()))
        `when`(orderConfirmationGateway.sendDecision(order, false))
                .thenReturn(Single.just(Pair(ExecutorState.ONLINE, "success")))

        // Action:
        useCase.sendDecision(false).test()

        // Effect:
        verify(updateDataUseCase, only()).updateWith("success")
    }

    /**
     * Должен передать юзкейсу данные после успешно принятого заказа.
     */
    @Test
    fun passStatusForConfirmedOrderToUpdateDataUseCase() {
        // Given:
        `when`(orderUseCase.orders).thenReturn(Flowable.just(order).concatWith(Flowable.never()))
        `when`(orderConfirmationGateway.sendDecision(order, true))
                .thenReturn(Single.just(Pair(ExecutorState.ONLINE, "success")))

        // Action:
        useCase.sendDecision(true).test()

        // Effect:
        verify(updateDataUseCase, only()).updateWith("success")
    }

    /**
     * Не должен трогать юзкейс если пришел новый заказ.
     */
    @Test
    fun doNotTouchUpdateDataUseCaseIfNextOrder() {
        // Given:
        `when`(orderUseCase.orders)
                .thenReturn(Flowable.just(order, order2).concatWith(Flowable.never()))

        // Action:
        useCase.sendDecision(true).test()

        // Effect:
        verifyNoInteractions(updateDataUseCase)
    }

    /**
     * Не должен трогать юзкейс если заказ истек.
     */
    @Test
    fun doNotTouchUpdateDataUseCaseIfOrderExpired() {
        // Given:
        `when`(orderUseCase.orders).thenReturn(
                Flowable.just(order).concatWith(Flowable.error(OrderOfferExpiredException(""))))

        // Action:
        useCase.sendDecision(true).test()

        // Effect:
        verifyNoInteractions(updateDataUseCase)
    }

    /* Проверяем ответы на запрос отправки решения */

    /**
     * Должен ответить ошибкой маппинга на запрос таймаутов.
     */
    @Test
    fun answerDataMappingErrorForGetTimeouts() {
        // Given:
        `when`(orderUseCase.orders).thenReturn(Flowable.error(DataMappingException()))

        // Action:
        val test = useCase.orderDecisionTimeout.test()

        // Effect:
        test.assertError(DataMappingException::class.java)
        test.assertNoValues()
        test.assertNotComplete()
    }

    /**
     * Должен ответить ошибкой не актуальности заказа на запрос таймаутов.
     */
    @Test
    fun answerOrderExpiredErrorForGetTimeoutsIfErrorAfterValue() {
        // Given:
        `when`(order.id).thenReturn(101L)
        `when`(order.timeout).thenReturn(12345L)
        `when`(orderUseCase.orders).thenReturn(
                Flowable.just(order).concatWith(Flowable.error(OrderOfferExpiredException("")))
        )

        // Action:
        val test = useCase.orderDecisionTimeout.test()

        // Effect:
        test.assertError(OrderOfferExpiredException::class.java)
        test.assertValue(Pair(101L, 12345L))
        test.assertNotComplete()
    }

    /**
     * Должен ответить ошибкой не актуальности заказа на подтверждение.
     */
    @Test
    fun answerWithTimeoutsForGetTimeouts() {
        // Given:
        `when`(order.id).thenReturn(101L, 202L)
        `when`(order.timeout).thenReturn(12345L, 54321L)
        `when`(order2.id).thenReturn(303L)
        `when`(order2.timeout).thenReturn(34543L)
        `when`(orderUseCase.orders)
                .thenReturn(Flowable.just(order, order2, order).concatWith(Flowable.never()))

        // Action:
        val test = useCase.orderDecisionTimeout.test()

        // Effect:
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
        // Given:
        `when`(orderUseCase.orders).thenReturn(Flowable.error(DataMappingException()))

        // Action:
        val test = useCase.sendDecision(true).test()

        // Effect:
        test.assertError(DataMappingException::class.java)
        test.assertNoValues()
        test.assertNotComplete()
    }

    /**
     * Должен ответить ошибкой не актуальности заказа на подтверждение.
     */
    @Test
    fun ingonreSameSecondSecondValue() {
        // Given:
        `when`(orderUseCase.orders)
                .thenReturn(Flowable.just(order, order).concatWith(Flowable.never()))

        // Action:
        val test = useCase.sendDecision(true).test()

        // Effect:
        test.assertNoErrors()
        test.assertNoValues()
        test.assertNotComplete()
    }

    /**
     * Должен ответить ошибкой не актуальности заказа на подтверждение.
     */
    @Test
    fun answerOrderExpiredErrorForAcceptIfSecondValue() {
        // Given:
        `when`(orderUseCase.orders)
                .thenReturn(Flowable.just(order, order2).concatWith(Flowable.never()))

        // Action:
        val test = useCase.sendDecision(true).test()

        // Effect:
        test.assertError(OrderOfferDecisionException::class.java)
        test.assertNoValues()
        test.assertNotComplete()
    }

    /**
     * Должен ответить ошибкой не актуальности заказа на подтверждение.
     */
    @Test
    fun answerOrderExpiredErrorForAcceptIfErrorAfterValue() {
        // Given:
        `when`(orderUseCase.orders).thenReturn(
                Flowable.just(order).concatWith(Flowable.error(OrderOfferExpiredException("")))
        )

        // Action:
        val test = useCase.sendDecision(true).test()

        // Effect:
        test.assertError(OrderOfferExpiredException::class.java)
        test.assertNoValues()
        test.assertNotComplete()
    }

    /**
     * Должен ответить ошибкой сети на подтверждение.
     */
    @Test
    fun answerNoNetworkErrorForAccept() {
        // Given:
        `when`(orderUseCase.orders).thenReturn(Flowable.just(order).concatWith(Flowable.never()))
        `when`(orderConfirmationGateway.sendDecision(any(), anyBoolean()))
                .thenReturn(Single.error(NoNetworkException()))

        // Action:
        val test = useCase.sendDecision(true).test()

        // Effect:
        test.assertError(NoNetworkException::class.java)
        test.assertNoValues()
        test.assertNotComplete()
    }

    /**
     * Должен ответить ошибкой сети на отказ.
     */
    @Test
    fun answerNoNetworkErrorForDecline() {
        // Given:
        `when`(orderUseCase.orders).thenReturn(Flowable.just(order).concatWith(Flowable.never()))
        `when`(orderConfirmationGateway.sendDecision(any(), anyBoolean()))
                .thenReturn(Single.error(NoNetworkException()))

        // Action:
        val test = useCase.sendDecision(false).test()

        // Effect:
        test.assertError(NoNetworkException::class.java)
        test.assertNoValues()
        test.assertNotComplete()
    }

    /**
     * Должен ответить успехом передачи подтверждения с сообщением.
     */
    @Test
    fun answerSendAcceptSuccessful() {
        // Given:
        ExecutorState.ONLINE.data = "successor"
        `when`(orderUseCase.orders).thenReturn(Flowable.just(order).concatWith(Flowable.never()))
        `when`(orderConfirmationGateway.sendDecision(any(), anyBoolean()))
                .thenReturn(Single.just(Pair(ExecutorState.ONLINE, "success")))

        // Action:
        val test = useCase.sendDecision(true).test()

        // Effect:
        test.assertComplete()
        test.assertNoErrors()
        test.assertValue("success")
    }

    /**
     * Должен ответить успехом передачи отказа с сообщением.
     */
    @Test
    fun answerSendDeclineSuccessful() {
        // Given:
        ExecutorState.ONLINE.data = "successor"
        `when`(orderUseCase.orders).thenReturn(Flowable.just(order).concatWith(Flowable.never()))
        `when`(orderConfirmationGateway.sendDecision(any(), anyBoolean()))
                .thenReturn(Single.just(Pair(ExecutorState.ONLINE, "success")))

        // Action:
        val test = useCase.sendDecision(false).test()

        // Effect:
        test.assertComplete()
        test.assertNoErrors()
        test.assertValue("success")
    }

    /**
     * Должен ответить успехом передачи подтверждения с пустым текстом.
     */
    @Test
    fun answerSendAcceptSuccessfulWithEmptyMessage() {
        // Given:
        ExecutorState.ONLINE.data = "successor"
        `when`(orderUseCase.orders).thenReturn(Flowable.just(order).concatWith(Flowable.never()))
        `when`(orderConfirmationGateway.sendDecision(any(), anyBoolean()))
                .thenReturn(Single.just(Pair(ExecutorState.ONLINE, null)))

        // Action:
        val test = useCase.sendDecision(true).test()

        // Effect:
        test.assertComplete()
        test.assertNoErrors()
        test.assertValue("successor")
    }

    /**
     * Должен ответить успехом передачи отказа с пустым текстом.
     */
    @Test
    fun answerSendDeclineSuccessfulWithEmptyMessage() {
        // Given:
        ExecutorState.ONLINE.data = "successor"
        `when`(orderUseCase.orders).thenReturn(Flowable.just(order).concatWith(Flowable.never()))
        `when`(orderConfirmationGateway.sendDecision(any(), anyBoolean()))
                .thenReturn(Single.just(Pair(ExecutorState.ONLINE, null)))

        // Action:
        val test = useCase.sendDecision(false).test()

        // Effect:
        test.assertComplete()
        test.assertNoErrors()
        test.assertValue("successor")
    }

    /**
     * Должен ответить успехом передачи подтверждения с сообщением если статуса нет.
     */
    @Test
    fun answerSendAcceptSuccessfulIfExecutorStateIsNull() {
        // Given:
        `when`(orderUseCase.orders).thenReturn(Flowable.just(order).concatWith(Flowable.never()))
        `when`(orderConfirmationGateway.sendDecision(any(), anyBoolean()))
                .thenReturn(Single.just(Pair(null, "success")))

        // Action:
        val test = useCase.sendDecision(true).test()

        // Effect:
        test.assertComplete()
        test.assertNoErrors()
        test.assertValue("success")
    }

    /**
     * Должен ответить успехом передачи отказа с сообщением если статуса нет.
     */
    @Test
    fun answerSendDeclineSuccessfulIfExecutorStateIsNull() {
        // Given:
        `when`(orderUseCase.orders).thenReturn(Flowable.just(order).concatWith(Flowable.never()))
        `when`(orderConfirmationGateway.sendDecision(any(), anyBoolean()))
                .thenReturn(Single.just(Pair(null, "success")))

        // Action:
        val test = useCase.sendDecision(false).test()

        // Effect:
        test.assertComplete()
        test.assertNoErrors()
        test.assertValue("success")
    }

    /**
     * Должен ответить успехом передачи подтверждения с текстом из статуса.
     */
    @Test
    fun answerAcceptedSuccessfulWithDataFromExecutorState() {
        // Given:
        ExecutorState.ONLINE.data = "successor"
        `when`(orderUseCase.orders).thenReturn(Flowable.just(order).concatWith(Flowable.never()))
        `when`(orderConfirmationGateway2.sendDecision(any(), anyBoolean()))
                .thenReturn(Single.just(Pair(ExecutorState.ONLINE, 0)))
        useCase = OrderConfirmationUseCaseImpl(orderUseCase, orderConfirmationGateway2,
                orderDecisionUseCase, ordersUseCase, updateExecutorStateUseCase, null)

        // Action:
        val test = useCase.sendDecision(true).test()

        // Effect:
        test.assertComplete()
        test.assertNoErrors()
        test.assertValue("successor")
    }

    /**
     * Должен ответить успехом передачи отказа с текстом из статуса.
     */
    @Test
    fun answerDeclinedSuccessfulWithDataFromExecutorState() {
        // Given:
        ExecutorState.ONLINE.data = "successor"
        `when`(orderUseCase.orders).thenReturn(Flowable.just(order).concatWith(Flowable.never()))
        `when`(orderConfirmationGateway2.sendDecision(any(), anyBoolean()))
                .thenReturn(Single.just(Pair(ExecutorState.ONLINE, 0)))
        useCase = OrderConfirmationUseCaseImpl(orderUseCase, orderConfirmationGateway2,
                orderDecisionUseCase, ordersUseCase, updateExecutorStateUseCase, null)

        // Action:
        val test = useCase.sendDecision(false).test()

        // Effect:
        test.assertComplete()
        test.assertNoErrors()
        test.assertValue("successor")
    }

    /**
     * Должен ответить успехом передачи подтверждения с пустым текстом с текстом из статуса если результата нет.
     */
    @Test
    fun answerAcceptedSuccessfulWithEmptyMessageIfExecutorStateAndResultAreNull() {
        // Given:
        ExecutorState.ONLINE.data = "successor"
        `when`(orderUseCase.orders).thenReturn(Flowable.just(order).concatWith(Flowable.never()))
        `when`(orderConfirmationGateway2.sendDecision(any(), anyBoolean()))
                .thenReturn(Single.just(Pair(ExecutorState.ONLINE, null)))
        useCase = OrderConfirmationUseCaseImpl(orderUseCase, orderConfirmationGateway2,
                orderDecisionUseCase, ordersUseCase, updateExecutorStateUseCase, null)

        // Action:
        val test = useCase.sendDecision(true).test()

        // Effect:
        test.assertComplete()
        test.assertNoErrors()
        test.assertValue("successor")
    }

    /**
     * Должен ответить успехом передачи отказа с пустым текстом с текстом из статуса если результата нет.
     */
    @Test
    fun answerDeclinedSuccessfulWithEmptyMessageIfExecutorStateAndResultAreNull() {
        // Given:
        ExecutorState.ONLINE.data = "successor"
        `when`(orderUseCase.orders).thenReturn(Flowable.just(order).concatWith(Flowable.never()))
        `when`(orderConfirmationGateway2.sendDecision(any(), anyBoolean()))
                .thenReturn(Single.just(Pair(ExecutorState.ONLINE, null)))
        useCase = OrderConfirmationUseCaseImpl(orderUseCase, orderConfirmationGateway2,
                orderDecisionUseCase, ordersUseCase, updateExecutorStateUseCase, null)

        // Action:
        val test = useCase.sendDecision(false).test()

        // Effect:
        test.assertComplete()
        test.assertNoErrors()
        test.assertValue("successor")
    }

    /**
     * Должен ответить успехом передачи подтверждения с пустым текстом если в статусе нет данных.
     */
    @Test
    fun answerAcceptedSuccessfulWithEmptyMessageIfDataInExecutorStateIsNull() {
        // Given:
        `when`(orderUseCase.orders).thenReturn(Flowable.just(order).concatWith(Flowable.never()))
        `when`(orderConfirmationGateway2.sendDecision(any(), anyBoolean()))
                .thenReturn(Single.just(Pair(ExecutorState.ONLINE, 0)))
        useCase = OrderConfirmationUseCaseImpl(orderUseCase, orderConfirmationGateway2,
                orderDecisionUseCase, ordersUseCase, updateExecutorStateUseCase, null)

        // Action:
        val test = useCase.sendDecision(true).test()

        // Effect:
        test.assertComplete()
        test.assertNoErrors()
        test.assertValue("")
    }

    /**
     * Должен ответить успехом передачи отказа с пустым текстом если в статусе нет данных.
     */
    @Test
    fun answerDeclinedSuccessfulWithEmptyMessageIfDataInExecutorStateIsNull() {
        // Given:
        `when`(orderUseCase.orders).thenReturn(Flowable.just(order).concatWith(Flowable.never()))
        `when`(orderConfirmationGateway2.sendDecision(any(), anyBoolean()))
                .thenReturn(Single.just(Pair(ExecutorState.ONLINE, 0)))
        useCase = OrderConfirmationUseCaseImpl(orderUseCase, orderConfirmationGateway2,
                orderDecisionUseCase, ordersUseCase, updateExecutorStateUseCase, null)

        // Action:
        val test = useCase.sendDecision(false).test()

        // Effect:
        test.assertComplete()
        test.assertNoErrors()
        test.assertValue("")
    }

    /**
     * Должен ответить успехом передачи подтверждения с пустым текстом если статуса нет.
     */
    @Test
    fun answerAcceptedSuccessfulWithEmptyMessageIfExecutorStateIsNull() {
        // Given:
        `when`(orderUseCase.orders).thenReturn(Flowable.just(order).concatWith(Flowable.never()))
        `when`(orderConfirmationGateway2.sendDecision(any(), anyBoolean()))
                .thenReturn(Single.just(Pair(null, 0)))
        useCase = OrderConfirmationUseCaseImpl(orderUseCase, orderConfirmationGateway2,
                orderDecisionUseCase, ordersUseCase, updateExecutorStateUseCase, null)

        // Action:
        val test = useCase.sendDecision(true).test()

        // Effect:
        test.assertComplete()
        test.assertNoErrors()
        test.assertValue("")
    }

    /**
     * Должен ответить успехом передачи отказа с пустым текстом если статуса нет.
     */
    @Test
    fun answerDeclinedSuccessfulWithEmptyMessageIfExecutorStateIsNull() {
        // Given:
        `when`(orderUseCase.orders).thenReturn(Flowable.just(order).concatWith(Flowable.never()))
        `when`(orderConfirmationGateway2.sendDecision(any(), anyBoolean()))
                .thenReturn(Single.just(Pair(null, 0)))
        useCase = OrderConfirmationUseCaseImpl(orderUseCase, orderConfirmationGateway2,
                orderDecisionUseCase, ordersUseCase, updateExecutorStateUseCase, null)

        // Action:
        val test = useCase.sendDecision(false).test()

        // Effect:
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

    @Suppress("UNCHECKED_CAST")
    private fun <T> uninitialized(): T = null as T
}