package com.cargopull.executor_driver.gateway

import com.cargopull.executor_driver.GatewayThreadTestRule
import com.cargopull.executor_driver.backend.web.ApiService
import com.cargopull.executor_driver.backend.web.incoming.ApiOrder
import com.cargopull.executor_driver.backend.web.incoming.ApiSimpleResult
import com.cargopull.executor_driver.backend.web.outgoing.ApiOrderDecision
import com.cargopull.executor_driver.entity.ExecutorState
import com.cargopull.executor_driver.entity.Order
import com.cargopull.executor_driver.entity.OrderConfirmationFailedException
import com.cargopull.executor_driver.interactor.OrderConfirmationGateway
import io.reactivex.Single
import org.junit.Assert.*
import org.junit.Before
import org.junit.ClassRule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class PreOrderProcessGatewayTest {

    companion object {

        @ClassRule
        @JvmField
        val classRule = GatewayThreadTestRule()
    }

    private lateinit var gateway: OrderConfirmationGateway<*>
    @Mock
    private lateinit var apiService: ApiService
    @Mock
    private lateinit var mapper: Mapper<ApiSimpleResult<ApiOrder?>, Pair<ExecutorState, Order?>>
    @Mock
    private lateinit var order: Order
    @Mock
    private lateinit var apiSimpleResult: ApiSimpleResult<ApiOrder?>
    @Captor
    private lateinit var orderDecisionCaptor: ArgumentCaptor<ApiOrderDecision>

    @Before
    fun setUp() {
        `when`<Single<ApiSimpleResult<ApiOrder?>>>(apiService.sendPreOrderProcess(any()))
                .thenReturn(Single.never<ApiSimpleResult<ApiOrder?>>())
        gateway = PreOrderProcessGateway(apiService, mapper)
    }

    /* Проверяем работу с API */

    /**
     * Должен запросить у API отправку сообщения.
     */
    @Test
    fun askApiServiceToSendDecisions() {
        // Дано:
        val inOrder = inOrder(apiService)
        `when`(order.id).thenReturn(7L)

        // Действие:
        gateway.sendDecision(order, false).test()
        gateway.sendDecision(order, true).test()

        // Результат:
        inOrder.verify<ApiService>(apiService, times(2)).sendPreOrderProcess(orderDecisionCaptor.capture())
        verifyNoMoreInteractions(apiService)
        assertEquals(orderDecisionCaptor.allValues[0].id, 7)
        assertFalse(orderDecisionCaptor.allValues[0].isApproved)
        assertEquals(orderDecisionCaptor.allValues[1].id, 7)
        assertTrue(orderDecisionCaptor.allValues[1].isApproved)
        verifyNoMoreInteractions(apiService)
    }

    /* Проверяем работу с маппером */

    /**
     * Не должен трогать маппер при ошибке сети.
     */
    @Test
    fun doNotTouchMapperOnError() {
        // Дано:
        `when`(apiService.sendPreOrderProcess(any())).thenReturn(Single.error(Exception()))

        // Действие:
        gateway.sendDecision(order, false).test()
        gateway.sendDecision(order, true).test()

        // Результат:
        verifyZeroInteractions(mapper)
    }

    /**
     * Не должен трогать маппер при ошибке в ответе.
     */
    @Test
    fun doNotTouchMapperOnErrorResponse() {
        // Дано:
        `when`(apiService.sendPreOrderProcess(any())).thenReturn(Single.just(apiSimpleResult))

        // Действие:
        gateway.sendDecision(order, false).test()
        gateway.sendDecision(order, true).test()

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
        `when`(apiService.sendPreOrderProcess(any())).thenReturn(Single.just(apiSimpleResult))
        `when`(apiSimpleResult.code).thenReturn("200")

        // Действие:
        gateway.sendDecision(order, false).test()
        gateway.sendDecision(order, true).test()

        // Результат:
        verify(mapper, times(2)).map(any())
        verifyNoMoreInteractions(mapper)
    }

    /* Проверяем результаты обработки сообщений от сервера */

    /**
     * Должен ответить ошибкой маппинга, если заказ забронирован.
     */
    @Test
    fun answerSendDecisionDataMappingError() {
        // Дано:
        `when`(apiService.sendPreOrderProcess(any()))
                .thenReturn(Single.just<ApiSimpleResult<ApiOrder?>>(apiSimpleResult))
        `when`(apiSimpleResult.code).thenReturn("200")
        doThrow(DataMappingException()).`when`(mapper).map(any())

        // Действие:
        val testObserver = gateway.sendDecision(order, false).test()

        // Результат:
        testObserver.assertNotComplete()
        testObserver.assertError(DataMappingException::class.java)
        testObserver.assertNoValues()
    }

    /**
     * Должен ответить успехом, если заказ забронирован.
     */
    @Test
    fun answerSendDecisionServerSuccess() {
        // Дано:
        `when`(apiService.sendPreOrderProcess(any()))
                .thenReturn(Single.just<ApiSimpleResult<ApiOrder?>>(apiSimpleResult))
        `when`(apiSimpleResult.code).thenReturn("200")
        `when`(mapper.map(any())).thenReturn(Pair(ExecutorState.ONLINE, order))

        // Действие:
        val testObserver = gateway.sendDecision(order, false).test()

        // Результат:
        testObserver.assertNoErrors()
        testObserver.assertComplete()
        testObserver.assertValueCount(1)
        val pair = testObserver.values()[0]
        assertEquals(ExecutorState.ONLINE, pair.first)
        assertEquals(order, pair.second)
    }

    /**
     * Должен ответить успехом, если заказ не забронирован.
     */
    @Test
    fun answerSendDecisionServerError() {
        // Дано:
        `when`(apiService.sendPreOrderProcess(any()))
                .thenReturn(Single.just<ApiSimpleResult<ApiOrder?>>(apiSimpleResult))
        `when`(apiSimpleResult.code).thenReturn("409")
        `when`(apiSimpleResult.message).thenReturn("error")

        // Действие:
        val testObserver = gateway.sendDecision(order, false).test()

        // Результат:
        testObserver.assertNotComplete()
        testObserver.assertError(OrderConfirmationFailedException::class.java)
        testObserver.assertNoValues()
        assertEquals(testObserver.errors().size, 1)
        assertEquals(testObserver.errors()[0].message, "error")
    }

    /**
     * Должен ответить ошибкой.
     */
    @Test
    fun answerSendDecisionError() {
        // Дано:
        `when`(apiService.sendPreOrderProcess(any()))
                .thenReturn(Single.error<ApiSimpleResult<ApiOrder?>>(IllegalArgumentException()))

        // Действие:
        val testObserver = gateway.sendDecision(order, false).test()

        // Результат:
        testObserver.assertNotComplete()
        testObserver.assertError(IllegalArgumentException::class.java)
        testObserver.assertNoValues()
    }

    private fun <T> any(): T {
        Mockito.any<T>()
        return uninitialized()
    }

    private fun <T> uninitialized(): T = null as T
}