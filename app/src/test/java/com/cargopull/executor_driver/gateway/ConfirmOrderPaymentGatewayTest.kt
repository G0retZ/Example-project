package com.cargopull.executor_driver.gateway

import com.cargopull.executor_driver.GatewayThreadTestRule
import com.cargopull.executor_driver.backend.web.ApiService
import com.cargopull.executor_driver.backend.web.incoming.ApiSimpleResult
import com.cargopull.executor_driver.entity.ExecutorState
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
class ConfirmOrderPaymentGatewayTest {

    companion object {

        @ClassRule
        @JvmField
        val classRule = GatewayThreadTestRule()
    }

    private lateinit var gateway: ConfirmOrderPaymentGateway
    @Mock
    private lateinit var apiService: ApiService
    @Mock
    private lateinit var mapper: Mapper<ApiSimpleResult<Void>, Pair<ExecutorState, Void?>>

    @Before
    fun setUp() {
        `when`(apiService.completeOrderPayment(Collections.singletonMap("status", "COMPLETE_PAYMENT_CONFIRMATION")))
                .thenReturn(Single.never())
        gateway = ConfirmOrderPaymentGateway(apiService, mapper)
    }

    /* Проверяем работу с API */

    /**
     * Должен запросить у API отправку сообщения.
     */
    @Test
    fun askApiToCompleteTheOrder() {
        // Действие:
        gateway.data.test()

        // Результат:
        verify(apiService, only()).completeOrderPayment(Collections.singletonMap("status", "COMPLETE_PAYMENT_CONFIRMATION"))
    }

    /* Проверяем работу с маппером */

    /**
     * Не должен трогать маппер при ошибке сети.
     */
    @Test
    fun doNotTouchMapperOnError() {
        // Дано:
        `when`(apiService.completeOrderPayment(Collections.singletonMap("status", "COMPLETE_PAYMENT_CONFIRMATION")))
                .thenReturn(Single.error(Exception()))

        // Действие:
        gateway.data.test()

        // Результат:
        verifyNoInteractions(mapper)
    }

    /**
     * Должен запросить у маппера маппинг данных.
     */
    @Test
    @Throws(Exception::class)
    fun askMapperToMapData() {
        // Дано:
        `when`(apiService.completeOrderPayment(Collections.singletonMap("status", "COMPLETE_PAYMENT_CONFIRMATION")))
                .thenReturn(Single.just(ApiSimpleResult()))

        // Действие:
        gateway.data.test()

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
        `when`(apiService.completeOrderPayment(Collections.singletonMap("status", "COMPLETE_PAYMENT_CONFIRMATION")))
                .thenReturn(Single.just(ApiSimpleResult()))
        `when`(mapper.map(any())).thenReturn(Pair(ExecutorState.ONLINE, null))

        // Действие:
        gateway.data.test()

        // Действие:
        val testObserver = gateway.data.test()

        // Результат:
        testObserver.assertNoErrors()
        testObserver.assertComplete()
        testObserver.assertValue(Pair(ExecutorState.ONLINE, null))
    }

    /**
     * Должен ответить ошибкой.
     */
    @Test
    fun answerCompleteTheOrderError() {
        // Дано:
        `when`(apiService.completeOrderPayment(Collections.singletonMap("status", "COMPLETE_PAYMENT_CONFIRMATION")))
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
        `when`(apiService.completeOrderPayment(Collections.singletonMap("status", "COMPLETE_PAYMENT_CONFIRMATION")))
                .thenReturn(Single.error(JsonParseException("")))

        // Действие:
        gateway.data.test()

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
        `when`(
            apiService.completeOrderPayment(
                Collections.singletonMap(
                    "status",
                    "COMPLETE_PAYMENT_CONFIRMATION"
                )
            )
        )
            .thenReturn(Single.just(ApiSimpleResult()))
        doThrow(DataMappingException()).`when`(mapper).map(any())

        // Действие:
        gateway.data.test()

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

    @Suppress("UNCHECKED_CAST")
    private fun <T> uninitialized(): T = null as T
}