package com.cargopull.executor_driver.interactor.auth

import com.cargopull.executor_driver.GatewayThreadTestRule
import com.cargopull.executor_driver.backend.web.ApiService
import com.cargopull.executor_driver.backend.web.NoNetworkException
import com.cargopull.executor_driver.gateway.CallCodeGatewayImpl
import com.cargopull.executor_driver.gateway.SmsCodeGatewayImpl
import io.reactivex.Completable
import org.junit.Before
import org.junit.ClassRule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class CodeGatewayTest {

    companion object {

        @ClassRule
        @JvmField
        val classRule = GatewayThreadTestRule()
    }

    private lateinit var gateway: CodeGateway

    @Mock
    private lateinit var api: ApiService

    @Before
    fun setUp() {
        `when`(api.sendMeCode(anyString())).thenReturn(Completable.never())
        `when`(api.callMeCode(anyString())).thenReturn(Completable.never())
    }

    /* Проверяем работу с АПИ */

    /**
     * Должен запросить у АПИ completable на запрос входящего СМС с кодом.
     */
    @Test
    fun smsMeCompletableRequested() {
        // Дано:
        gateway = SmsCodeGatewayImpl(api)

        // Действие:
        gateway.sendMeCode("012345")

        // Результат:
        verify<ApiService>(api, only()).sendMeCode("012345")
    }

    /**
     * Должен запросить у АПИ completable на запрос входящего звонка с кодом.
     */
    @Test
    fun callMeCompletableRequested() {
        // Дано:
        gateway = CallCodeGatewayImpl(api)

        // Действие:
        gateway.sendMeCode("012345")

        // Результат:
        verify<ApiService>(api, only()).callMeCode("012345")
    }

    /* Проверяем ответы на АПИ */

    /**
     * Должен ответить ошибкой сети.
     */
    @Test
    fun answerNoNetworkErrorForSms() {
        // Дано:
        gateway = SmsCodeGatewayImpl(api)

        // Действие:
        `when`(api.sendMeCode(anyString())).thenReturn(Completable.error(NoNetworkException()))

        // Результат:
        gateway.sendMeCode("01234").test().assertError(NoNetworkException::class.java)
    }

    /**
     * Должен ответить ошибкой сети.
     */
    @Test
    fun answerNoNetworkErrorForCall() {
        // Дано:
        gateway = CallCodeGatewayImpl(api)

        // Действие:
        `when`(api.callMeCode(anyString())).thenReturn(Completable.error(NoNetworkException()))

        // Результат:
        gateway.sendMeCode("01234").test().assertError(NoNetworkException::class.java)
    }

    /**
     * Должен ответить успехом.
     */
    @Test
    fun answerSmsSuccessful() {
        // Дано:
        gateway = SmsCodeGatewayImpl(api)

        // Действие:
        `when`(api.sendMeCode(anyString())).thenReturn(Completable.complete())

        // Результат:
        gateway.sendMeCode("012345").test().assertComplete()
    }

    /**
     * Должен ответить успехом.
     */
    @Test
    fun answerCallSuccessful() {
        // Дано:
        gateway = CallCodeGatewayImpl(api)

        // Действие:
        `when`(api.callMeCode(anyString())).thenReturn(Completable.complete())

        // Результат:
        gateway.sendMeCode("012345").test().assertComplete()
    }
}
