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

    private lateinit var gateway: OrderConfirmationGateway<*>
    @Mock
    private lateinit var apiService: ApiService
    @Mock
    private lateinit var errorMapper: Mapper<Throwable, Throwable>
    @Mock
    private lateinit var order: Order
    @Mock
    private lateinit var apiSimpleResult: ApiSimpleResult<Void?>
    @Captor
    private lateinit var orderDecisionCaptor: ArgumentCaptor<ApiOrderDecision>

    @Before
    fun setUp() {
        `when`(apiService.acceptOrderOffer(any())).thenReturn(Single.never<ApiSimpleResult<Void?>>())
        gateway = OrderConfirmationGatewayImpl(apiService, errorMapper)
    }

    /* Проверяем работу с API */

    /**
     * Должен запросить у API отправку сообщения.
     */
    @Test
    fun askStompClientToSendMessage() {
        // Дано:
        val inOrder = inOrder(apiService)
        `when`(order.id).thenReturn(7L)

        // Действие:
        gateway.sendDecision(order, false).test()
        gateway.sendDecision(order, true).test()

        // Результат:
        inOrder.verify<ApiService>(apiService, times(2)).acceptOrderOffer(orderDecisionCaptor.capture())
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
        // Дано:
        `when`(apiSimpleResult.message).thenReturn("he-he-he")
        `when`(apiSimpleResult.status).thenReturn("ONLINE")
        `when`(apiService.acceptOrderOffer(any())).thenReturn(Single.just(apiSimpleResult))

        // Действие:
        val testObserver = gateway.sendDecision(order, false).test()

        // Результат:
        testObserver.assertNoErrors()
        testObserver.assertComplete()
        testObserver.assertValueCount(1)
        val pair = testObserver.values()[0]
        assertEquals(pair.first, ExecutorState.ONLINE)
        assertEquals(pair.first!!.data, "he-he-he")
        assertNull(pair.second)
    }

    /**
     * Должен ответить успехом без сообщений.
     */
    @Test
    fun answerSendDecisionSuccessWithoutMessage() {
        // Дано:
        `when`(apiSimpleResult.status).thenReturn("ONLINE")
        `when`(apiService.acceptOrderOffer(any())).thenReturn(Single.just(apiSimpleResult))

        // Действие:
        val testObserver = gateway.sendDecision(order, false).test()

        // Результат:
        testObserver.assertNoErrors()
        testObserver.assertComplete()
        testObserver.assertValueCount(1)
        val pair = testObserver.values()[0]
        assertEquals(pair.first, ExecutorState.ONLINE)
        assertNull(pair.first!!.data)
        assertNull(pair.second)
    }

    /**
     * Должен ответить спамленной ошибкой.
     */
    @Test
    @Throws(Exception::class)
    fun answerSendDecisionError() {
        // Дано:
        `when`(apiService.acceptOrderOffer(any()))
                .thenReturn(Single.error<ApiSimpleResult<Void>>(IllegalArgumentException()))
        `when`(errorMapper.map(any())).thenReturn(IllegalStateException())

        // Действие:
        val testObserver = gateway.sendDecision(order, false).test()

        // Результат:
        testObserver.assertNotComplete()
        testObserver.assertError(IllegalStateException::class.java)
        testObserver.assertNoValues()
    }

    private fun <T> any(): T {
        Mockito.any<T>()
        return uninitialized()
    }

    private fun <T> uninitialized(): T = null as T
}