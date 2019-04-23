package com.cargopull.executor_driver.interactor

import com.cargopull.executor_driver.GatewayThreadTestRule
import com.cargopull.executor_driver.backend.web.ApiService
import com.cargopull.executor_driver.backend.web.incoming.ApiRoutePoint
import com.cargopull.executor_driver.backend.web.incoming.ApiSimpleResult
import com.cargopull.executor_driver.entity.RoutePoint
import com.cargopull.executor_driver.gateway.Mapper
import com.cargopull.executor_driver.gateway.OrderRouteGatewayImpl
import io.reactivex.Single
import org.junit.Before
import org.junit.ClassRule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner
import java.util.*

@RunWith(MockitoJUnitRunner::class)
class OrderRouteGatewayTest {

    companion object {

        @ClassRule
        @JvmField
        val classRule = GatewayThreadTestRule()
    }

    private lateinit var gateway: OrderRouteGateway
    @Mock
    private lateinit var apiService: ApiService
    @Mock
    private lateinit var mapper: Mapper<ApiRoutePoint, RoutePoint>
    @Mock
    private lateinit var apiSimpleResult: ApiSimpleResult<List<ApiRoutePoint>>
    @Mock
    private lateinit var apiRoutePoint: ApiRoutePoint
    @Mock
    private lateinit var routePoint: RoutePoint
    @Mock
    private lateinit var routePoint1: RoutePoint
    @Mock
    private lateinit var routePoint2: RoutePoint
    @Mock
    private lateinit var routePoint3: RoutePoint

    @Before
    fun setUp() {
        `when`(apiService.completeRoutePoint(anyLong())).thenReturn(Single.never())
        `when`(apiService.makeRoutePointNext(anyLong())).thenReturn(Single.never())
        gateway = OrderRouteGatewayImpl(apiService, mapper)
    }

    /* Проверяем работу с клиентом STOMP */

    /**
     * Должен запросить у клиента STOMP отправку сообщения.
     */
    @Test
    fun askStompClientToSendCloseRoutePointMessage() {
        // Дано:
        `when`(routePoint.id).thenReturn(7L)

        // Действие:
        gateway.closeRoutePoint(routePoint).test().isDisposed

        // Результат:
        verify<ApiService>(apiService, only()).completeRoutePoint(7)
    }

    /**
     * Должен запросить у клиента STOMP отправку сообщения.
     */
    @Test
    fun askStompClientToSendNextRoutePointMessage() {
        // Дано:
        `when`(routePoint.id).thenReturn(7L)

        // Действие:
        gateway.nextRoutePoint(routePoint).test().isDisposed

        // Результат:
        verify<ApiService>(apiService, only()).makeRoutePointNext(7)
    }

    /* Проверяем работу с маппером */

    /**
     * Не должен трогать маппер при ошибке сети на закрытие точки.
     */
    @Test
    fun doNotTouchMapperOnCloseRoutePointError() {
        // Дано:
        `when`(apiService.completeRoutePoint(anyLong()))
                .thenReturn(Single.error(Exception()))
        `when`(routePoint.id).thenReturn(7L)

        // Действие:
        gateway.closeRoutePoint(routePoint).test()

        // Результат:
        verifyZeroInteractions(mapper)
    }

    /**
     * Не должен трогать маппер при ошибке сети на смену точки.
     */
    @Test
    fun doNotTouchMapperOnNextRoutePointError() {
        // Дано:
        `when`(apiService.makeRoutePointNext(anyLong()))
                .thenReturn(Single.error(Exception()))
        `when`(routePoint.id).thenReturn(7L)

        // Действие:
        gateway.nextRoutePoint(routePoint).test()

        // Результат:
        verifyZeroInteractions(mapper)
    }

    /**
     * Должен запросить у маппера маппинг данных на закрытие точки.
     */
    @Test
    @Throws(Exception::class)
    fun askMapperToMapCloseRoutePointData() {
        // Дано:
        `when`(apiService.completeRoutePoint(anyLong())).thenReturn(Single.just(apiSimpleResult))
        `when`(routePoint.id).thenReturn(7L)
        `when`(apiSimpleResult.data).thenReturn(Arrays.asList(apiRoutePoint, apiRoutePoint, apiRoutePoint))
        `when`(mapper.map(any())).thenReturn(routePoint, routePoint1, routePoint2, routePoint3)

        // Действие:
        gateway.closeRoutePoint(routePoint).test()

        // Результат:
        verify(mapper, times(3)).map(apiRoutePoint)
        verifyNoMoreInteractions(mapper)
    }

    /**
     * Должен запросить у маппера маппинг данных на смену точки.
     */
    @Test
    @Throws(Exception::class)
    fun askMapperToMapNextRoutePointData() {
        // Дано:
        `when`(apiService.makeRoutePointNext(anyLong())).thenReturn(Single.just(apiSimpleResult))
        `when`(routePoint.id).thenReturn(7L)
        `when`(apiSimpleResult.data).thenReturn(Arrays.asList(apiRoutePoint, apiRoutePoint, apiRoutePoint))
        `when`(mapper.map(any())).thenReturn(routePoint, routePoint1, routePoint2, routePoint3)

        // Действие:
        gateway.nextRoutePoint(routePoint).test()

        // Результат:
        verify(mapper, times(3)).map(apiRoutePoint)
        verifyNoMoreInteractions(mapper)
    }

    /* Проверяем результаты отправки сообщений */

    /**
     * Должен ответить успехом.
     */
    @Test
    @Throws(Exception::class)
    fun answerSendCloseRoutePointSuccess() {
        // Дано:
        `when`(apiService.completeRoutePoint(anyLong())).thenReturn(Single.just(apiSimpleResult))
        `when`(apiSimpleResult.data).thenReturn(Arrays.asList<ApiRoutePoint>(apiRoutePoint, apiRoutePoint, apiRoutePoint, apiRoutePoint))
        `when`(mapper.map(any())).thenReturn(routePoint, routePoint1, routePoint2, routePoint3)

        // Действие:
        val testObserver = gateway.closeRoutePoint(routePoint).test()

        // Результат:
        testObserver.assertNoErrors()
        testObserver.assertComplete()
        testObserver.assertValue(Arrays.asList<RoutePoint>(routePoint, routePoint1, routePoint2, routePoint3))
    }

    /**
     * Должен ответить успехом.
     */
    @Test
    @Throws(Exception::class)
    fun answerSendNextRoutePointSuccess() {
        // Дано:
        `when`(apiService.makeRoutePointNext(anyLong())).thenReturn(Single.just(apiSimpleResult))
        `when`(apiSimpleResult.data).thenReturn(Arrays.asList<ApiRoutePoint>(apiRoutePoint, apiRoutePoint, apiRoutePoint, apiRoutePoint))
        `when`(mapper.map(any())).thenReturn(routePoint, routePoint1, routePoint2, routePoint3)

        // Действие:
        val testObserver = gateway.nextRoutePoint(routePoint).test()

        // Результат:
        testObserver.assertNoErrors()
        testObserver.assertComplete()
        testObserver.assertValue(Arrays.asList<RoutePoint>(routePoint, routePoint1, routePoint2, routePoint3))
    }

    /**
     * Должен ответить ошибкой.
     */
    @Test
    fun answerSendCloseRoutePointError() {
        // Дано:
        `when`(apiService.completeRoutePoint(anyLong()))
                .thenReturn(Single.error(IllegalArgumentException()))

        // Действие:
        val testObserver = gateway.closeRoutePoint(routePoint).test()

        // Результат:
        testObserver.assertNotComplete()
        testObserver.assertError(IllegalArgumentException::class.java)
    }

    /**
     * Должен ответить ошибкой.
     */
    @Test
    fun answerSendNextRoutePointError() {
        // Дано:
        `when`(apiService.makeRoutePointNext(anyLong()))
                .thenReturn(Single.error(IllegalArgumentException()))

        // Действие:
        val testObserver = gateway.nextRoutePoint(routePoint).test()

        // Результат:
        testObserver.assertNotComplete()
        testObserver.assertError(IllegalArgumentException::class.java)
    }

    private fun <T> any(): T {
        Mockito.any<T>()
        return uninitialized()
    }

    private fun <T> uninitialized(): T = null as T
}