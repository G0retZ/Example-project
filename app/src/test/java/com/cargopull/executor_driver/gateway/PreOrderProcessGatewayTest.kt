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

    private lateinit var gateway: OrderConfirmationGateway<Order>
    @Mock
    private lateinit var apiService: ApiService
    @Mock
    private lateinit var mapper: Mapper<ApiSimpleResult<ApiOrder>, Pair<ExecutorState, Order?>>
    @Mock
    private lateinit var order: Order
    @Mock
    private lateinit var apiSimpleResult: ApiSimpleResult<ApiOrder>
    @Captor
    private lateinit var orderDecisionCaptor: ArgumentCaptor<ApiOrderDecision>

    @Before
    fun setUp() {
        `when`(apiService.sendPreOrderProcess(any())).thenReturn(Single.never())
        gateway = PreOrderProcessGateway(apiService, mapper)
    }

    /* Проверяем работу с API */

    /**
     * Должен запросить у API отправку сообщения.
     */
    @Test
    fun askApiServiceToSendDecisions() {
        // Given:
        val inOrder = inOrder(apiService)
        `when`(order.id).thenReturn(7L)

        // Action:
        gateway.sendDecision(order, false).test()
        gateway.sendDecision(order, true).test()

        // Effect:
        inOrder.verify(apiService, times(2)).sendPreOrderProcess(orderDecisionCaptor.kCapture())
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
        // Given:
        `when`(apiService.sendPreOrderProcess(any())).thenReturn(Single.error(Exception()))

        // Action:
        gateway.sendDecision(order, false).test()
        gateway.sendDecision(order, true).test()

        // Effect:
        verifyNoInteractions(mapper)
    }

    /**
     * Не должен трогать маппер при ошибке в ответе.
     */
    @Test
    fun doNotTouchMapperOnErrorResponse() {
        // Given:
        `when`(apiService.sendPreOrderProcess(any())).thenReturn(Single.just(apiSimpleResult))

        // Action:
        gateway.sendDecision(order, false).test()
        gateway.sendDecision(order, true).test()

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
        `when`(apiService.sendPreOrderProcess(any())).thenReturn(Single.just(apiSimpleResult))
        `when`(apiSimpleResult.code).thenReturn("200")

        // Action:
        gateway.sendDecision(order, false).test()
        gateway.sendDecision(order, true).test()

        // Effect:
        verify(mapper, times(2)).map(any())
        verifyNoMoreInteractions(mapper)
    }

    /* Проверяем результаты обработки сообщений от сервера */

    /**
     * Должен ответить ошибкой маппинга, если заказ забронирован.
     */
    @Test
    fun answerSendDecisionDataMappingError() {
        // Given:
        `when`(apiService.sendPreOrderProcess(any())).thenReturn(Single.just(apiSimpleResult))
        `when`(apiSimpleResult.code).thenReturn("200")
        doThrow(DataMappingException()).`when`(mapper).map(any())

        // Action:
        val testObserver = gateway.sendDecision(order, false).test()

        // Effect:
        testObserver.assertNotComplete()
        testObserver.assertError(DataMappingException::class.java)
        testObserver.assertNoValues()
    }

    /**
     * Должен ответить успехом, если заказ забронирован.
     */
    @Test
    fun answerSendDecisionServerSuccess() {
        // Given:
        `when`(apiService.sendPreOrderProcess(any())).thenReturn(Single.just(apiSimpleResult))
        `when`(apiSimpleResult.code).thenReturn("200")
        `when`(mapper.map(any())).thenReturn(Pair(ExecutorState.ONLINE, order))

        // Action:
        val testObserver = gateway.sendDecision(order, false).test()

        // Effect:
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
        // Given:
        `when`(apiService.sendPreOrderProcess(any())).thenReturn(Single.just(apiSimpleResult))
        `when`(apiSimpleResult.code).thenReturn("409")
        `when`(apiSimpleResult.message).thenReturn("error")

        // Action:
        val testObserver = gateway.sendDecision(order, false).test()

        // Effect:
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
        // Given:
        `when`(apiService.sendPreOrderProcess(any())).thenReturn(Single.error(IllegalArgumentException()))

        // Action:
        val testObserver = gateway.sendDecision(order, false).test()

        // Effect:
        testObserver.assertNotComplete()
        testObserver.assertError(IllegalArgumentException::class.java)
        testObserver.assertNoValues()
    }

    private fun <T> ArgumentCaptor<T>.kCapture(): T {
        capture()
        return uninitialized()
    }

    private fun <T> any(): T {
        Mockito.any<T>()
        return uninitialized()
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> uninitialized(): T = null as T
}