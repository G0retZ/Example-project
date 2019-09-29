package com.cargopull.executor_driver.interactor.auth

import com.cargopull.executor_driver.UseCaseThreadTestRule
import com.cargopull.executor_driver.backend.web.NoNetworkException
import com.cargopull.executor_driver.entity.ValidationException
import com.cargopull.executor_driver.entity.Validator
import com.cargopull.executor_driver.interactor.DataReceiver
import io.reactivex.Completable
import io.reactivex.Observable
import org.junit.Before
import org.junit.ClassRule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class CodeUseCaseTest {

    companion object {

        @ClassRule
        @JvmField
        val classRule = UseCaseThreadTestRule()
    }

    private lateinit var useCase: CodeUseCase

    @Mock
    private lateinit var smsGateway: CodeGateway

    @Mock
    private lateinit var callGateway: CodeGateway

    @Mock
    private lateinit var phoneNumberValidator: Validator<String>

    @Mock
    private lateinit var phoneNumberReceiver: DataReceiver<String>

    @Before
    @Throws(Exception::class)
    fun setUp() {
        `when`(smsGateway.sendMeCode(anyString())).thenReturn(Completable.never())
        doThrow(ValidationException()).`when`<Validator<String>>(phoneNumberValidator).validate(anyString())
        doNothing().`when`<Validator<String>>(phoneNumberValidator).validate("0123456")
        `when`(phoneNumberReceiver.get()).thenReturn(Observable.never())
        useCase = CodeUseCaseImpl(smsGateway, callGateway, phoneNumberReceiver, phoneNumberValidator)
    }

    /* Проверяем работу с публикатором номера телефона */

    /**
     * Не должен взаимодействовать с публиктором в любых иных случаях.
     */
    @Test
    fun doNotTouchDataSharer() {
        // Действие:
        useCase.sendMeCode().test().isDisposed

        // Результат:
        verify<DataReceiver<String>>(phoneNumberReceiver, only()).get()
    }

    /* Проверяем работу с валидаторами */

    /**
     * Должен запросить у валидатора номера телефона проверку.
     *
     * @throws Exception error
     */
    @Test
    @Throws(Exception::class)
    fun askPhoneNumberValidatorForResult() {
        // Дано:
        `when`(phoneNumberReceiver.get()).thenReturn(Observable.just("1", "2", "3"))

        // Действие:
        useCase.sendMeCode().test().isDisposed

        // Результат:
        verify<Validator<String>>(phoneNumberValidator, only()).validate("1")
    }

    /* Проверяем ответы валидатора */

    /**
     * Должен ответить ошибкой, если номер телефона неверный.
     */
    @Test
    fun answerErrorIfPhoneNumberInvalid() {
        // Дано:
        `when`(phoneNumberReceiver.get()).thenReturn(Observable.just("1", "2", "3"))

        // Действие:
        val testObserver = useCase.sendMeCode().test()

        // Результат:
        testObserver.assertNoValues()
        testObserver.assertNotComplete()
        testObserver.assertError(ValidationException::class.java)
    }

    /**
     * Не должно быть ошибок, если номер телефона соответствует формату.
     *
     * @throws Exception error
     */
    @Test
    @Throws(Exception::class)
    fun answerSuccessIfPhoneNumberValid() {
        // Дано:
        `when`(phoneNumberReceiver.get()).thenReturn(Observable.just("1", "2", "3"))
        doNothing().`when`<Validator<String>>(phoneNumberValidator).validate(anyString())

        // Действие:
        val testObserver = useCase.sendMeCode().test()

        // Результат:
        testObserver.assertNoValues()
        testObserver.assertNotComplete()
        testObserver.assertNoErrors()
    }

    /* Проверяем работу с гейтвеем */

    /**
     * Не должен запрашивать у гейтвея СМС, если валидация не прошла.
     */
    @Test
    fun doNotAskGatewayForSms() {
        // Дано:
        `when`(phoneNumberReceiver.get()).thenReturn(Observable.just("012345", "2", "3"))

        // Действие:
        useCase.sendMeCode().test().isDisposed

        // Результат:
        verifyZeroInteractions(smsGateway)
    }

    /**
     * Должен запросить у гейтвея СМС.
     */
    @Test
    fun askGatewayForSms() {
        // Дано:
        `when`(phoneNumberReceiver.get()).thenReturn(Observable.just("0123456", "2", "3"))

        // Действие:
        useCase.sendMeCode().test().isDisposed

        // Результат:
        verify<CodeGateway>(smsGateway, only()).sendMeCode("0123456")
    }

    /* Проверяем ответы на запрос СМС */

    /**
     * Должен ответить ошибкой сети.
     */
    @Test
    fun answerNoNetworkError() {
        // Дано:
        `when`(phoneNumberReceiver.get()).thenReturn(Observable.just("0123456", "2", "3"))
        `when`(smsGateway.sendMeCode(anyString())).thenReturn(Completable.error(NoNetworkException()))

        // Действие:
        val testObserver = useCase.sendMeCode().test()

        // Результат:
        testObserver.assertNoValues()
        testObserver.assertNotComplete()
        testObserver.assertError(NoNetworkException::class.java)
    }

    /**
     * Должен ответить успехом.
     */
    @Test
    fun answerSmsSendSuccessful() {
        // Дано:
        `when`(phoneNumberReceiver.get()).thenReturn(Observable.just("0123456", "2", "3"))
        `when`(smsGateway.sendMeCode(anyString())).thenReturn(Completable.complete())

        // Действие:
        val testObserver = useCase.sendMeCode().test()

        // Результат:
        testObserver.assertNoValues()
        testObserver.assertNoErrors()
        testObserver.assertComplete()
    }
}
