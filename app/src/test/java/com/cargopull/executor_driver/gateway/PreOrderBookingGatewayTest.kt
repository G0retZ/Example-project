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
import org.mockito.ArgumentMatchers.any
import org.mockito.Captor
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class PreOrderBookingGatewayTest {

    companion object {

        @ClassRule
        @JvmField
        val classRule = GatewayThreadTestRule()
    }

    private lateinit var gateway: OrderConfirmationGateway<String?>
    @Mock
    private lateinit var apiService: ApiService
    @Mock
    private lateinit var order: Order
    @Mock
    private lateinit var apiSimpleResult: ApiSimpleResult<String?>
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
        // Дано:
        val inOrder = inOrder(apiService)
        `when`(order.id).thenReturn(7L)

        // Действие:
        gateway.sendDecision(order, false).test()
        gateway.sendDecision(order, true).test()

        // Результат:
        inOrder.verify<ApiService>(apiService, times(2)).sendPreOrderDecision(orderDecisionCaptor.capture())
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
        // Дано:
        `when`(apiService.sendPreOrderDecision(any())).thenReturn(Single.just<ApiSimpleResult<String?>>(apiSimpleResult))
        `when`(apiSimpleResult.code).thenReturn("200")
        `when`(apiSimpleResult.message).thenReturn("message")

        // Действие:
        val testObserver = gateway.sendDecision(order, false).test()

        // Результат:
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
        // Дано:
        `when`(apiService.sendPreOrderDecision(any())).thenReturn(Single.just<ApiSimpleResult<String?>>(apiSimpleResult))
        `when`(apiSimpleResult.code).thenReturn("200")

        // Действие:
        val testObserver = gateway.sendDecision(order, false).test()

        // Результат:
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
        // Дано:
        `when`(apiService.sendPreOrderDecision(any())).thenReturn(Single.just<ApiSimpleResult<String?>>(apiSimpleResult))
        `when`(apiSimpleResult.code).thenReturn("409")
        `when`(apiSimpleResult.message).thenReturn("error")

        // Действие:
        val testObserver = gateway.sendDecision(order, false).test()

        // Результат:
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
        // Дано:
        `when`(apiService.sendPreOrderDecision(any()))
                .thenReturn(Single.error(IllegalArgumentException()))

        // Действие:
        val testObserver = gateway.sendDecision(order, false).test()

        // Результат:
        testObserver.assertNotComplete()
        testObserver.assertError(IllegalArgumentException::class.java)
        testObserver.assertNoValues()
    }
}