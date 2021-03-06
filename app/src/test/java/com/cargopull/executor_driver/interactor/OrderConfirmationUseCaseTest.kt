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

    /* ?????????????????? ???????????? ?? ???????????????? ???????????? */

    /**
     * ???????????? ?????????????????? ?? ?????????????? ???????????? ?????????????????? ?????????????? ?????? ???????????????? ??????????????.
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
     * ???????????? ?????????????????? ?? ?????????????? ???????????? ?????????????????? ?????????????? ?????? ?????????????? ??????????????????.
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

    /* ?????????????????? ???????????? ?? ???????????????? ???????????????? ?????????????? ???? ???????????? */

    /**
     * ???? ???????????? ???????????? ??????????????????, ???????? ?????????????? ???????????????? ?????????????? ??????.
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
     * ???????????? ?????????????????? ?? ?????????????? ???????????? ???????????????????????????? ??????????????.
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

    /* ?????????????????? ???????????? ?? ???????????????? */

    /**
     * ???????????? ?????????????????? ?? ?????????????? ???????????????? ??????????????.
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
     * ???????????? ?????????????????? ?? ?????????????? ???????????????? ?????????????? ???????????? ?????? ?????????????? ?????????????? ????????????.
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
     * ???????????? ???????????????????????? ???????? ???????????? ?????? ???? ??????????.
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
     * ???????????? ???????????????? ???????????? ?? ?????????????? ???? ???????????????? ?????????????? ???????? ???????????? ?????????? ??????????.
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
     * ???????????? ???????????????? ???????????? ?? ?????????????? ???? ???????????????? ?????????????? ???????? ?????????? ??????????.
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

    /* ?????????????????? ???????????? ?? ???????????????? ???????????? ?????????????? */

    /**
     * ???? ???????????? ???????????? ??????????????????, ???????? ?????????????? ?????????????? ??????.
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
     * ???????????? ???????????????? ?????????????? ?????????????? ?????????????????????? ????????????.
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
     * ???????????? ???????????????? ?????????????? ?????????????? ???????????????? ????????????.
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
     * ???? ???????????? ?????????????? ???????????? ???????? ???????????? ?????????? ??????????.
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
     * ???? ???????????? ?????????????? ???????????? ???????? ?????????? ??????????.
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

    /* ?????????????????? ???????????? ?? ???????????????? ???????????????????? ?????????????? ?????????????????????? */

    /**
     * ???? ???????????? ???????????? ??????????????????, ???????? ?????????????? ???????????????????? ???????????????? ??????.
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
     * ???? ???????????? ?????????????? ???????????? ???????? ?????????? ???????????? ???? ???????????? ?????????? ?????????????? ???????????????????????? ????????????.
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
     * ???? ???????????? ?????????????? ???????????? ???????? ?????????? ???????????? ???? ???????????? ?????????? ?????????????? ?????????????????? ????????????.
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
     * ???????????? ???????????????? ?????????????? ???????????? ?????????? ?????????????? ???????????????????????? ????????????.
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
     * ???????????? ???????????????? ?????????????? ???????????? ?????????? ?????????????? ?????????????????? ????????????.
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
     * ???? ???????????? ?????????????? ???????????? ???????? ???????????? ?????????? ??????????.
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
     * ???? ???????????? ?????????????? ???????????? ???????? ?????????? ??????????.
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

    /* ?????????????????? ???????????? ?? ???????????????? ???????????????????? ???????????? */

    /**
     * ???? ???????????? ???????????? ??????????????????, ???????? ?????????????? ???????????????????? ???????????? ??????.
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
     * ???????????? ???????????????? ?????????????? ???????????? ?????????? ?????????????? ???????????????????????? ????????????.
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
     * ???????????? ???????????????? ?????????????? ???????????? ?????????? ?????????????? ???????????????????????? ????????????.
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
     * ???????????? ???????????????? ?????????????? ???????????? ?????????? ?????????????? ???????????????????????? ????????????.
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
     * ???????????? ???????????????? ?????????????? ???????????? ?????????? ?????????????? ?????????????????? ????????????.
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
     * ???? ???????????? ?????????????? ???????????? ???????? ???????????? ?????????? ??????????.
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
     * ???? ???????????? ?????????????? ???????????? ???????? ?????????? ??????????.
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

    /* ?????????????????? ???????????? ???? ???????????? ???????????????? ?????????????? */

    /**
     * ???????????? ???????????????? ?????????????? ???????????????? ???? ???????????? ??????????????????.
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
     * ???????????? ???????????????? ?????????????? ???? ???????????????????????? ???????????? ???? ???????????? ??????????????????.
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
     * ???????????? ???????????????? ?????????????? ???? ???????????????????????? ???????????? ???? ??????????????????????????.
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
     * ???????????? ???????????????? ?????????????? ???????????????? ???? ??????????????????????????.
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
     * ???????????? ???????????????? ?????????????? ???? ???????????????????????? ???????????? ???? ??????????????????????????.
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
     * ???????????? ???????????????? ?????????????? ???? ???????????????????????? ???????????? ???? ??????????????????????????.
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
     * ???????????? ???????????????? ?????????????? ???? ???????????????????????? ???????????? ???? ??????????????????????????.
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
     * ???????????? ???????????????? ?????????????? ???????? ???? ??????????????????????????.
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
     * ???????????? ???????????????? ?????????????? ???????? ???? ??????????.
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
     * ???????????? ???????????????? ?????????????? ???????????????? ?????????????????????????? ?? ????????????????????.
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
     * ???????????? ???????????????? ?????????????? ???????????????? ???????????? ?? ????????????????????.
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
     * ???????????? ???????????????? ?????????????? ???????????????? ?????????????????????????? ?? ???????????? ??????????????.
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
     * ???????????? ???????????????? ?????????????? ???????????????? ???????????? ?? ???????????? ??????????????.
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
     * ???????????? ???????????????? ?????????????? ???????????????? ?????????????????????????? ?? ???????????????????? ???????? ?????????????? ??????.
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
     * ???????????? ???????????????? ?????????????? ???????????????? ???????????? ?? ???????????????????? ???????? ?????????????? ??????.
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
     * ???????????? ???????????????? ?????????????? ???????????????? ?????????????????????????? ?? ?????????????? ???? ??????????????.
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
     * ???????????? ???????????????? ?????????????? ???????????????? ???????????? ?? ?????????????? ???? ??????????????.
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
     * ???????????? ???????????????? ?????????????? ???????????????? ?????????????????????????? ?? ???????????? ?????????????? ?? ?????????????? ???? ?????????????? ???????? ???????????????????? ??????.
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
     * ???????????? ???????????????? ?????????????? ???????????????? ???????????? ?? ???????????? ?????????????? ?? ?????????????? ???? ?????????????? ???????? ???????????????????? ??????.
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
     * ???????????? ???????????????? ?????????????? ???????????????? ?????????????????????????? ?? ???????????? ?????????????? ???????? ?? ?????????????? ?????? ????????????.
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
     * ???????????? ???????????????? ?????????????? ???????????????? ???????????? ?? ???????????? ?????????????? ???????? ?? ?????????????? ?????? ????????????.
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
     * ???????????? ???????????????? ?????????????? ???????????????? ?????????????????????????? ?? ???????????? ?????????????? ???????? ?????????????? ??????.
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
     * ???????????? ???????????????? ?????????????? ???????????????? ???????????? ?? ???????????? ?????????????? ???????? ?????????????? ??????.
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