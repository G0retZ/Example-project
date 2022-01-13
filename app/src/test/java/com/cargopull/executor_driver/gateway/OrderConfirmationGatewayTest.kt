package com.cargopull.executor_driver.gateway

import com.cargopull.executor_driver.GatewayThreadTestRule
import com.cargopull.executor_driver.backend.web.ApiService
import com.cargopull.executor_driver.backend.web.incoming.ApiSimpleResult
import com.cargopull.executor_driver.backend.web.outgoing.ApiOrderDecision
import com.cargopull.executor_driver.entity.ExecutorState
import com.cargopull.executor_driver.entity.Order
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
class OrderConfirmationGatewayTest {

    companion object {

        @ClassRule
        @JvmField
        val classRule = GatewayThreadTestRule()
    }

    private lateinit var gateway: OrderConfirmationGateway<Void>
    @Mock
    private lateinit var apiService: ApiService
    @Mock
    private lateinit var errorMapper: Mapper<Throwable, Throwable>
    @Mock
    private lateinit var order: Order
    @Mock
    private lateinit var apiSimpleResult: ApiSimpleResult<Void>
    @Captor
    private lateinit var orderDecisionCaptor: ArgumentCaptor<ApiOrderDecision>

    @Before
    fun setUp() {
        ExecutorState.ONLINE.customerTimer = 0
        `when`(apiService.acceptOrderOffer(any())).thenReturn(Single.never())
        gateway = OrderConfirmationGatewayImpl(apiService, errorMapper)
    }

    /* Проверяем работу с API */

    /**
     * Должен запросить у API отправку сообщения.
     */
    @Test
    fun askStompClientToSendMessage() {
        // Given:
        val inOrder = inOrder(apiService)
        `when`(order.id).thenReturn(7L)

        // Action:
        gateway.sendDecision(order, false).test()
        gateway.sendDecision(order, true).test()

        // Effect:
        inOrder.verify<ApiService>(apiService, times(2)).acceptOrderOffer(orderDecisionCaptor.kCapture())
        verifyNoMoreInteractions(apiService)
        assertEquals(orderDecisionCaptor.allValues[0].id, 7)
        assertFalse(orderDecisionCaptor.allValues[0].isApproved)
        assertEquals(orderDecisionCaptor.allValues[1].id, 7)
        assertTrue(orderDecisionCaptor.allValues[1].isApproved)
        verifyNoMoreInteractions(apiService)
    }

    /* Проверяем результаты обработки сообщений от сервера */

    /**
     * Должен ответить успехом.
     */
    @Test
    fun answerSendDecisionSuccess() {
        // Given:
        `when`(apiSimpleResult.message).thenReturn("he-he-he")
        `when`(apiSimpleResult.status).thenReturn("ONLINE")
        `when`(apiService.acceptOrderOffer(any())).thenReturn(Single.just(apiSimpleResult))

        // Action:
        val testObserver = gateway.sendDecision(order, false).test()

        // Effect:
        testObserver.assertNoErrors()
        testObserver.assertComplete()
        testObserver.assertValueCount(1)
        val pair = testObserver.values()[0]
        assertEquals(pair.first, ExecutorState.ONLINE)
        assertEquals(pair.first!!.customerTimer, 0)
        assertEquals(pair.first!!.data, "he-he-he")
        assertNull(pair.second)
    }

    /**
     * Должен ответить успехом со значением тамера.
     */
    @Test
    fun answerSendDecisionSuccessWithTimerValue() {
        // Given:
        `when`(apiSimpleResult.message).thenReturn("he-he-he")
        `when`(apiSimpleResult.status).thenReturn("CLIENT_ORDER_CONFIRMATION")
        `when`(apiService.acceptOrderOffer(any())).thenReturn(Single.just(apiSimpleResult))

        // Action:
        val testObserver = gateway.sendDecision(order, false).test()

        // Effect:
        testObserver.assertNoErrors()
        testObserver.assertComplete()
        testObserver.assertValueCount(1)
        val pair = testObserver.values()[0]
        assertEquals(pair.first, ExecutorState.CLIENT_ORDER_CONFIRMATION)
        assertEquals(pair.first!!.customerTimer, 600_000)
        assertEquals(pair.first!!.data, "he-he-he")
        assertNull(pair.second)
    }

    /**
     * Должен ответить успехом без сообщений.
     */
    @Test
    fun answerSendDecisionSuccessWithoutMessage() {
        // Given:
        `when`(apiSimpleResult.status).thenReturn("ONLINE")
        `when`(apiService.acceptOrderOffer(any())).thenReturn(Single.just(apiSimpleResult))

        // Action:
        val testObserver = gateway.sendDecision(order, false).test()

        // Effect:
        testObserver.assertNoErrors()
        testObserver.assertComplete()
        testObserver.assertValueCount(1)
        val pair = testObserver.values()[0]
        assertEquals(pair.first, ExecutorState.ONLINE)
        assertEquals(pair.first!!.customerTimer, 0)
        assertNull(pair.first!!.data)
        assertNull(pair.second)
    }

    /**
     * Должен ответить спамленной ошибкой.
     */
    @Test
    @Throws(Exception::class)
    fun answerSendDecisionError() {
        // Given:
        `when`(apiService.acceptOrderOffer(any())).thenReturn(Single.error(IllegalArgumentException()))
        `when`(errorMapper.map(any())).thenReturn(IllegalStateException())

        // Action:
        val testObserver = gateway.sendDecision(order, false).test()

        // Effect:
        testObserver.assertNotComplete()
        testObserver.assertError(IllegalStateException::class.java)
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