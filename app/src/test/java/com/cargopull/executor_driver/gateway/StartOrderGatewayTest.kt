package com.cargopull.executor_driver.gateway

import com.cargopull.executor_driver.GatewayThreadTestRule
import com.cargopull.executor_driver.backend.web.ApiService
import com.cargopull.executor_driver.backend.web.incoming.ApiRoutePoint
import com.cargopull.executor_driver.backend.web.incoming.ApiSimpleResult
import com.cargopull.executor_driver.entity.ExecutorState
import com.cargopull.executor_driver.entity.RoutePoint
import com.google.gson.JsonParseException
import io.reactivex.Single
import org.junit.Before
import org.junit.ClassRule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner
import java.util.*

@RunWith(MockitoJUnitRunner::class)
class StartOrderGatewayTest {

    companion object {

        @ClassRule
        @JvmField
        val classRule = GatewayThreadTestRule()
    }

    private lateinit var gateway: StartOrderGateway
    @Mock
    private lateinit var apiService: ApiService
    @Mock
    private lateinit var mapper: Mapper<ApiSimpleResult<List<ApiRoutePoint>>, Pair<ExecutorState, List<RoutePoint>?>>
    @Mock
    private lateinit var route: List<RoutePoint>

    @Before
    fun setUp() {
        `when`(apiService.startOrder(Collections.singletonMap("status", "START_ORDER")))
                .thenReturn(Single.never())
        gateway = StartOrderGateway(apiService, mapper)
    }

    /* Проверяем работу с API */

    /**
     * Должен запросить у API отправку сообщения.
     */
    @Test
    fun askApiToCompleteTheOrder() {
        // Действие:
        gateway.data.test().isDisposed

        // Результат:
        verify(apiService, only()).startOrder(Collections.singletonMap("status", "START_ORDER"))
    }

    /* Проверяем работу с маппером */

    /**
     * Не должен трогать маппер при ошибке сети.
     */
    @Test
    fun doNotTouchMapperOnError() {
        // Дано:
        `when`(apiService.startOrder(Collections.singletonMap("status", "START_ORDER")))
                .thenReturn(Single.error(Exception()))

        // Действие:
        gateway.data.test().isDisposed

        // Результат:
        verifyZeroInteractions(mapper)
    }

    /**
     * Должен запросить у маппера маппинг данных.
     */
    @Test
    @Throws(Exception::class)
    fun askMapperToMapData() {
        // Дано:
        `when`(apiService.startOrder(Collections.singletonMap("status", "START_ORDER")))
                .thenReturn(Single.just(ApiSimpleResult()))

        // Действие:
        gateway.data.test().isDisposed

        // Результат:
        verify(mapper, only()).map(any())
    }

    /* Проверяем результаты отправки сообщений */

    /**
     * Должен ответить успехом.
     */
    @Test
    @Throws(Exception::class)
    fun answerCompleteTheOrderSuccess() {
        // Дано:
        `when`(apiService.startOrder(Collections.singletonMap("status", "START_ORDER")))
                .thenReturn(Single.just(ApiSimpleResult()))
        `when`(mapper.map(any())).thenReturn(Pair(ExecutorState.ONLINE, route))

        // Действие:
        gateway.data.test().isDisposed

        // Действие:
        val testObserver = gateway.data.test()

        // Результат:
        testObserver.assertNoErrors()
        testObserver.assertComplete()
        testObserver.assertValue(Pair(ExecutorState.ONLINE, route))
    }

    /**
     * Должен ответить ошибкой.
     */
    @Test
    fun answerCompleteTheOrderError() {
        // Дано:
        `when`(apiService.startOrder(Collections.singletonMap("status", "START_ORDER")))
                .thenReturn(Single.error(IllegalArgumentException()))

        // Действие:
        val testObserver = gateway.data.test()

        // Результат:
        testObserver.assertNotComplete()
        testObserver.assertError(IllegalArgumentException::class.java)
    }

    /**
     * Должен ответить ошибкой формата данных.
     */
    @Test
    fun answerCompleteTheOrderDataErrorForWrongModel() {
        // Дано:
        `when`(apiService.startOrder(Collections.singletonMap("status", "START_ORDER")))
                .thenReturn(Single.error(JsonParseException("")))

        // Действие:
        gateway.data.test().isDisposed

        // Действие:
        val testObserver = gateway.data.test()

        // Результат:
        testObserver.assertNotComplete()
        testObserver.assertError(DataMappingException::class.java)
    }

    /**
     * Должен ответить ошибкой формата данных.
     */
    @Test
    @Throws(Exception::class)
    fun answerCompleteTheOrderDataErrorForMapperError() {
        // Дано:
        `when`(apiService.startOrder(Collections.singletonMap("status", "START_ORDER")))
                .thenReturn(Single.just(ApiSimpleResult()))
        doThrow(DataMappingException()).`when`(mapper).map(any())

        // Действие:
        gateway.data.test().isDisposed

        // Действие:
        val testObserver = gateway.data.test()

        // Результат:
        testObserver.assertNotComplete()
        testObserver.assertError(DataMappingException::class.java)
    }

    private fun <T> any(): T {
        Mockito.any<T>()
        return uninitialized()
    }

    private fun <T> uninitialized(): T = null as T
}