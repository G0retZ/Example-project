package com.cargopull.executor_driver.interactor

import com.cargopull.executor_driver.UseCaseThreadTestRule
import com.cargopull.executor_driver.backend.web.NoNetworkException
import com.cargopull.executor_driver.entity.Order
import com.cargopull.executor_driver.entity.RoutePoint
import com.cargopull.executor_driver.gateway.DataMappingException
import io.reactivex.Flowable
import io.reactivex.Single
import org.junit.Before
import org.junit.ClassRule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.any
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner
import java.util.*

@RunWith(MockitoJUnitRunner::class)
class OrderRouteUseCaseTest {

    companion object {

        @ClassRule
        @JvmField
        val classRule = UseCaseThreadTestRule()
    }

    private lateinit var useCase: OrderRouteUseCaseImpl

    @Mock
    private lateinit var orderUseCase: OrderUseCase
    @Mock
    private lateinit var orderRouteGateway: OrderRouteGateway
    @Mock
    private lateinit var order: Order
    @Mock
    private lateinit var order2: Order
    @Mock
    private lateinit var routePoint: RoutePoint
    @Mock
    private lateinit var routePoint1: RoutePoint
    @Mock
    private lateinit var routePoint2: RoutePoint
    @Mock
    private lateinit var routePoint3: RoutePoint
    @Mock
    private lateinit var routePoint4: RoutePoint

    @Before
    fun setUp() {
        `when`(orderUseCase.orders).thenReturn(Flowable.never())
        `when`(orderRouteGateway.closeRoutePoint(any())).thenReturn(Single.never())
        `when`(orderRouteGateway.nextRoutePoint(any())).thenReturn(Single.never())
        useCase = OrderRouteUseCaseImpl(orderUseCase, orderRouteGateway)
    }

    /* Проверяем работу с юзкейсом заказа */

    /**
     * Должен запросить у юзкейсом получение выполняемого заказа только 1 раз.
     */
    @Test
    fun askGatewayForRoutes() {
        // Action:
        useCase.orderRoutePoints.test().isDisposed
        useCase.orderRoutePoints.test().isDisposed
        useCase.orderRoutePoints.test().isDisposed
        useCase.orderRoutePoints.test().isDisposed

        // Effect:
        verify<OrderUseCase>(orderUseCase, only()).orders
    }

    /* Проверяем работу с гейтвеем маршрута заказа */

    /**
     * Не должен трогать гейтвей.
     */
    @Test
    fun doNotTouchGateway() {
        // Action:
        useCase.updateWith(Arrays.asList<RoutePoint>(routePoint, routePoint1, routePoint2, routePoint4))

        // Effect:
        verifyNoInteractions(orderRouteGateway)
    }

    /**
     * Должен запросить у гейтвея отметить точку.
     */
    @Test
    fun askGatewayToCheckRoutePoint() {
        // Action:
        useCase.closeRoutePoint(routePoint).test().isDisposed

        // Effect:
        verify<OrderRouteGateway>(orderRouteGateway, only()).closeRoutePoint(routePoint)
    }

    /**
     * Должен запросить у гейтвея выбрать другую точку.
     */
    @Test
    fun askGatewayToUseNextRoutePoint() {
        // Action:
        useCase.nextRoutePoint(routePoint).test().isDisposed

        // Effect:
        verify<OrderRouteGateway>(orderRouteGateway, only()).nextRoutePoint(routePoint)
    }

    /* Проверяем ответы на запрос маршрута */

    /**
     * Должен ответить ошибкой маппинга.
     */
    @Test
    fun answerDataMappingError() {
        // Given:
        `when`(orderUseCase.orders).thenReturn(Flowable.error(DataMappingException()))

        // Action:
        val test = useCase.orderRoutePoints.test()

        // Effect:
        test.assertError(DataMappingException::class.java)
        test.assertNoValues()
        test.assertNotComplete()
    }

    /**
     * Должен ответить маршрутами до завершения.
     */
    @Test
    fun answerWithRoutesBeforeComplete() {
        // Given:
        `when`(orderUseCase.orders).thenReturn(Flowable.just(order, order2))
        `when`(order.routePath).thenReturn(Arrays.asList<RoutePoint>(routePoint1, routePoint2, routePoint3))
        `when`(order2.routePath).thenReturn(Arrays.asList<RoutePoint>(routePoint4, routePoint, routePoint3))

        // Action:
        val test = useCase.orderRoutePoints.test()
        useCase.updateWith(Arrays.asList<RoutePoint>(routePoint4, routePoint3))
        useCase.updateWith(Arrays.asList<RoutePoint>(routePoint, routePoint2))

        // Effect:
        test.assertValueCount(2)
        test.assertValueAt(0, Arrays.asList<RoutePoint>(routePoint1, routePoint2, routePoint3))
        test.assertValueAt(1, Arrays.asList<RoutePoint>(routePoint4, routePoint, routePoint3))
        test.assertComplete()
        test.assertNoErrors()
    }

    /**
     * Должен ответить маршрутами.
     */
    @Test
    fun answerWithAllRoutes() {
        // Given:
        `when`(orderUseCase.orders)
                .thenReturn(Flowable.just(order, order2).concatWith(Flowable.never()))
        `when`(order.routePath).thenReturn(Arrays.asList<RoutePoint>(routePoint1, routePoint2, routePoint3))
        `when`(order2.routePath).thenReturn(Arrays.asList<RoutePoint>(routePoint4, routePoint, routePoint3))

        // Action:
        val test = useCase.orderRoutePoints.test()
        useCase.updateWith(Arrays.asList<RoutePoint>(routePoint4, routePoint3))
        useCase.updateWith(Arrays.asList<RoutePoint>(routePoint, routePoint2))

        // Effect:
        test.assertValueCount(4)
        test.assertValueAt(0, Arrays.asList<RoutePoint>(routePoint1, routePoint2, routePoint3))
        test.assertValueAt(1, Arrays.asList<RoutePoint>(routePoint4, routePoint, routePoint3))
        test.assertValueAt(2, Arrays.asList<RoutePoint>(routePoint4, routePoint3))
        test.assertValueAt(3, Arrays.asList<RoutePoint>(routePoint, routePoint2))
        test.assertNotComplete()
        test.assertNoErrors()
    }

    /* Проверяем ответы на запросы управления точками */

    /**
     * Должен ответить ошибкой сети на запрос закрытия точки.
     */
    @Test
    fun answerNoNetworkErrorForCloseRoutePoint() {
        // Given:
        `when`(orderRouteGateway.closeRoutePoint(any()))
                .thenReturn(Single.error(NoNetworkException()))

        // Action:
        val test = useCase.closeRoutePoint(routePoint).test()

        // Effect:
        test.assertError(NoNetworkException::class.java)
        test.assertNoValues()
        test.assertNotComplete()
    }

    /**
     * Должен ответить ошибкой сети на на запрос выбора другой точки.
     */
    @Test
    fun answerNoNetworkErrorForUseNextRoutePoint() {
        // Given:
        `when`(orderRouteGateway.nextRoutePoint(any()))
                .thenReturn(Single.error(NoNetworkException()))

        // Action:
        val test = useCase.nextRoutePoint(routePoint).test()

        // Effect:
        test.assertError(NoNetworkException::class.java)
        test.assertNoValues()
        test.assertNotComplete()
    }

    /**
     * Должен ответить успехом на запрос закрытия точки.
     */
    @Test
    fun answerSendCloseRoutePointSuccessful() {
        // Given:
        `when`(orderRouteGateway.closeRoutePoint(any())).thenReturn(Single.just(Arrays.asList<RoutePoint>(routePoint, routePoint1, routePoint2, routePoint4)))

        // Action:
        val test = useCase.closeRoutePoint(routePoint).test()

        // Effect:
        test.assertComplete()
        test.assertNoErrors()
    }

    /**
     * Должен ответить успехом на запрос выбора другой точки.
     */
    @Test
    fun answerSendUseNextRoutePointSuccessful() {
        // Given:
        `when`(orderRouteGateway.nextRoutePoint(any())).thenReturn(Single.just(Arrays.asList<RoutePoint>(routePoint, routePoint1, routePoint2, routePoint4)))

        // Action:
        val test = useCase.nextRoutePoint(routePoint).test()

        // Effect:
        test.assertComplete()
        test.assertNoErrors()
    }

    /**
     * Должен ответить маршрутами на запрос закрытия точки.
     */
    @Test
    fun answerWithAllRoutesSendCloseRoutePointSuccessful() {
        // Given:
        `when`(orderRouteGateway.closeRoutePoint(any())).thenReturn(Single.just(Arrays.asList<RoutePoint>(routePoint, routePoint1, routePoint2, routePoint4)))

        // Action:
        val test = useCase.orderRoutePoints.test()
        useCase.closeRoutePoint(routePoint).test()

        // Effect:
        test.assertValue(Arrays.asList<RoutePoint>(routePoint, routePoint1, routePoint2, routePoint4))
        test.assertNotComplete()
        test.assertNoErrors()
    }

    /**
     * Должен ответить успехом на запрос выбора другой точки.
     */
    @Test
    fun answerWithAllRoutesSendUseNextRoutePointSuccessful() {
        // Given:
        `when`(orderRouteGateway.nextRoutePoint(any())).thenReturn(Single.just(Arrays.asList<RoutePoint>(routePoint, routePoint1, routePoint2, routePoint4)))

        // Action:
        val test = useCase.orderRoutePoints.test()
        useCase.nextRoutePoint(routePoint).test()

        // Effect:
        test.assertValue(Arrays.asList<RoutePoint>(routePoint, routePoint1, routePoint2, routePoint4))
        test.assertNotComplete()
        test.assertNoErrors()
    }
}