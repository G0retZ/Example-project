package com.cargopull.executor_driver.gateway

import com.cargopull.executor_driver.GatewayThreadTestRule
import com.cargopull.executor_driver.backend.web.ApiService
import com.cargopull.executor_driver.backend.web.incoming.ApiSimpleResult
import com.cargopull.executor_driver.backend.web.outgoing.ApiOrderDecision
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
class PreOrderBookingGatewayTest {

    companion object {

        @ClassRule
        @JvmField
        val classRule = GatewayThreadTestRule()
    }

    private lateinit var gateway: OrderConfirmationGateway<String>
    @Mock
    private lateinit var apiService: ApiService
    @Mock
    private lateinit var order: Order
    @Mock
    private lateinit var apiSimpleResult: ApiSimpleResult<String>
    @Captor
    private lateinit var orderDecisionCaptor: ArgumentCaptor<ApiOrderDecision>

    @Before
    fun setUp() {
        `when`(apiService.sendPreOrderDecision(any())).thenReturn(Single.never())
        gateway = PreOrderBookingGateway(apiService)
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
        inOrder.verify<ApiService>(apiService, times(2)).sendPreOrderDecision(orderDecisionCaptor.kCapture())
        verifyNoMoreInteractions(apiService)
        assertEquals(orderDecisionCaptor.allValues[0].id, 7)
        assertFalse(orderDecisionCaptor.allValues[0].isApproved)
        assertEquals(orderDecisionCaptor.allValues[1].id, 7)
        assertTrue(orderDecisionCaptor.allValues[1].isApproved)
        verifyNoMoreInteractions(apiService)
    }

    /* Проверяем результаты обработки сообщений от сервера */

    /**
     * Должен ответить успехом, если заказ забронирован.
     */
    @Test
    fun answerSendDecisionServerSuccess() {
        // Given:
        `when`(apiService.sendPreOrderDecision(any())).thenReturn(Single.just(apiSimpleResult))
        `when`(apiSimpleResult.code).thenReturn("200")
        `when`(apiSimpleResult.message).thenReturn("message")

        // Action:
        val testObserver = gateway.sendDecision(order, false).test()

        // Effect:
        testObserver.assertNoErrors()
        testObserver.assertComplete()
        testObserver.assertValueCount(1)
        val pair = testObserver.values()[0]
        assertNull(pair.first)
        assertEquals("message", pair.second)
    }

    /**
     * Должен ответить успехом, если заказ забронирован без сообщений.
     */
    @Test
    fun answerSendDecisionServerSuccessWithoutMessage() {
        // Given:
        `when`(apiService.sendPreOrderDecision(any())).thenReturn(Single.just(apiSimpleResult))
        `when`(apiSimpleResult.code).thenReturn("200")

        // Action:
        val testObserver = gateway.sendDecision(order, false).test()

        // Effect:
        testObserver.assertNoErrors()
        testObserver.assertComplete()
        testObserver.assertValueCount(1)
        val pair = testObserver.values()[0]
        assertNull(pair.first)
        assertNull(pair.second)
    }

    /**
     * Должен ответить успехом, если заказ не забронирован.
     */
    @Test
    fun answerSendDecisionServerError() {
        // Given:
        `when`(apiService.sendPreOrderDecision(any())).thenReturn(Single.just(apiSimpleResult))
        `when`(apiSimpleResult.code).thenReturn("409")
        `when`(apiSimpleResult.message).thenReturn("error")

        // Action:
        val testObserver = gateway.sendDecision(order, false).test()

        // Effect:
        testObserver.assertNotComplete()
        testObserver.assertError(OrderConfirmationFailedException::class.java)
        testObserver.assertNoValues()
        assertEquals(testObserver.errors().size.toLong(), 1)
        assertEquals(testObserver.errors()[0].message, "error")
    }

    /**
     * Должен ответить ошибкой.
     */
    @Test
    fun answerSendDecisionError() {
        // Given:
        `when`(apiService.sendPreOrderDecision(any()))
                .thenReturn(Single.error(IllegalArgumentException()))

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