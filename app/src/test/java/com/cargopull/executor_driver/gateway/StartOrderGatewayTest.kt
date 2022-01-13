package com.cargopull.executor_driver.gateway

import com.cargopull.executor_driver.GatewayThreadTestRule
import com.cargopull.executor_driver.backend.web.ApiService
import com.cargopull.executor_driver.backend.web.incoming.ApiOrder
import com.cargopull.executor_driver.backend.web.incoming.ApiSimpleResult
import com.cargopull.executor_driver.entity.ExecutorState
import com.cargopull.executor_driver.entity.Order
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
    private lateinit var mapper: Mapper<ApiSimpleResult<ApiOrder>, Pair<ExecutorState, Order?>>
    @Mock
    private lateinit var order: Order

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
        // Action:
        gateway.data.test()

        // Effect:
        verify(apiService, only()).startOrder(Collections.singletonMap("status", "START_ORDER"))
    }

    /* Проверяем работу с маппером */

    /**
     * Не должен трогать маппер при ошибке сети.
     */
    @Test
    fun doNotTouchMapperOnError() {
        // Given:
        `when`(apiService.startOrder(Collections.singletonMap("status", "START_ORDER")))
                .thenReturn(Single.error(Exception()))

        // Action:
        gateway.data.test()

        // Effect:
        verifyNoInteractions(mapper)
    }

    /**
     * Должен запросить у маппера маппинг данных.
     */
    @Test
    @Throws(Exception::class)
    fun askMapperToMapData() {
        // Given:
        `when`(apiService.startOrder(Collections.singletonMap("status", "START_ORDER")))
                .thenReturn(Single.just(ApiSimpleResult()))

        // Action:
        gateway.data.test()

        // Effect:
        verify(mapper, only()).map(any())
    }

    /* Проверяем результаты отправки сообщений */

    /**
     * Должен ответить успехом.
     */
    @Test
    @Throws(Exception::class)
    fun answerCompleteTheOrderSuccess() {
        // Given:
        `when`(apiService.startOrder(Collections.singletonMap("status", "START_ORDER")))
                .thenReturn(Single.just(ApiSimpleResult()))
        `when`(mapper.map(any())).thenReturn(Pair(ExecutorState.ONLINE, order))

        // Action:
        gateway.data.test()

        // Action:
        val testObserver = gateway.data.test()

        // Effect:
        testObserver.assertNoErrors()
        testObserver.assertComplete()
        testObserver.assertValue(Pair(ExecutorState.ONLINE, order))
    }

    /**
     * Должен ответить ошибкой.
     */
    @Test
    fun answerCompleteTheOrderError() {
        // Given:
        `when`(apiService.startOrder(Collections.singletonMap("status", "START_ORDER")))
                .thenReturn(Single.error(IllegalArgumentException()))

        // Action:
        val testObserver = gateway.data.test()

        // Effect:
        testObserver.assertNotComplete()
        testObserver.assertError(IllegalArgumentException::class.java)
    }

    /**
     * Должен ответить ошибкой формата данных.
     */
    @Test
    fun answerCompleteTheOrderDataErrorForWrongModel() {
        // Given:
        `when`(apiService.startOrder(Collections.singletonMap("status", "START_ORDER")))
                .thenReturn(Single.error(JsonParseException("")))

        // Action:
        gateway.data.test()

        // Action:
        val testObserver = gateway.data.test()

        // Effect:
        testObserver.assertNotComplete()
        testObserver.assertError(DataMappingException::class.java)
    }

    /**
     * Должен ответить ошибкой формата данных.
     */
    @Test
    @Throws(Exception::class)
    fun answerCompleteTheOrderDataErrorForMapperError() {
        // Given:
        `when`(apiService.startOrder(Collections.singletonMap("status", "START_ORDER")))
                .thenReturn(Single.just(ApiSimpleResult()))
        doThrow(DataMappingException()).`when`(mapper).map(any())

        // Action:
        gateway.data.test()

        // Action:
        val testObserver = gateway.data.test()

        // Effect:
        testObserver.assertNotComplete()
        testObserver.assertError(DataMappingException::class.java)
    }

    private fun <T> any(): T {
        Mockito.any<T>()
        return uninitialized()
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> uninitialized(): T = null as T
}