package com.fasten.executor_driver.interactor.auth;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import com.fasten.executor_driver.backend.web.NoNetworkException;
import com.fasten.executor_driver.entity.ValidationException;
import com.fasten.executor_driver.entity.Validator;
import com.fasten.executor_driver.interactor.DataSharer;
import io.reactivex.Completable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class SmsUseCaseTest {

  private SmsUseCase smsUseCase;

  @Mock
  private SmsGateway gateway;

  @Mock
  private Validator<String> phoneNumberValidator;

  @Mock
  private DataSharer<String> phoneNumberSharer;

  private Subject<String> subject;

  @Before
  public void setUp() throws Exception {
    when(gateway.sendMeCode(anyString())).thenReturn(Completable.never());
    doThrow(new ValidationException()).when(phoneNumberValidator).validate(anyString());
    doNothing().when(phoneNumberValidator).validate("0123456");
    when(phoneNumberSharer.get()).thenReturn(subject = PublishSubject.create());
    smsUseCase = new SmsUseCaseImpl(gateway, phoneNumberSharer, phoneNumberValidator);
  }

  /* Проверяем работу с публикатором номера телефона */

  /**
   * Должен подписаться при создании сразу же.
   *
   * @throws Exception error
   */
  @Test
  public void getFromDataSharerImmediately() throws Exception {
    // Результат:
    verify(phoneNumberSharer, only()).get();
  }

  /**
   * Не должен взаимодействовать с публиктором в любых иных случаях.
   *
   * @throws Exception error
   */
  @Test
  public void doNotTouchDataSharer() throws Exception {
    // Действие:
    smsUseCase.sendMeCode().test();

    // Результат:
    verify(phoneNumberSharer, only()).get();
  }

  /* Проверяем работу с валидаторами */

  /**
   * Должен запросить у валидатора номера телефона проверку.
   *
   * @throws Exception error.
   */
  @Test
  public void askPhoneNumberValidatorForResult() throws Exception {
    // Дано:
    subject.onNext("");

    // Действие:
    smsUseCase.sendMeCode().test();

    // Результат:
    verify(phoneNumberValidator, only()).validate("");
  }

  /* Проверяем ответы валидатора */

  /**
   * Должен ответить ошибкой, если номер телефона неверный.
   *
   * @throws Exception error.
   */
  @Test
  public void answerErrorIfPhoneNumberInvalid() throws Exception {
    // Дано:
    subject.onNext("");

    // Результат:
    smsUseCase.sendMeCode().test().assertError(ValidationException.class);
  }

  /**
   * Не должно быть ошибок, если номер телефона соответствует формату.
   *
   * @throws Exception error.
   */
  @Test
  public void answerSuccessIfPhoneNumberValid() throws Exception {
    // Дано:
    subject.onNext("");

    // Действие:
    doNothing().when(phoneNumberValidator).validate(anyString());

    // Результат:
    smsUseCase.sendMeCode().test().assertNoErrors();
  }

  /* Проверяем работу с гейтвеем */

  /**
   * Не должен запрашивать у гейтвея СМС, если валидация не прошла.
   *
   * @throws Exception error.
   */
  @Test
  public void doNotAskGatewayForSms() throws Exception {
    // Дано:
    subject.onNext("012345");

    // Действие:
    smsUseCase.sendMeCode().test();

    // Результат:
    verifyZeroInteractions(gateway);
  }

  /**
   * Должен запросить у гейтвея СМС.
   *
   * @throws Exception error.
   */
  @Test
  public void askGatewayForSms() throws Exception {
    // Дано:
    subject.onNext("0123456");

    // Действие:
    smsUseCase.sendMeCode().test();

    // Результат:
    verify(gateway, only()).sendMeCode("0123456");
  }

  /* Проверяем ответы на запрос СМС */

  /**
   * Должен ответить ошибкой сети.
   *
   * @throws Exception error.
   */
  @Test
  public void answerNoNetworkError() throws Exception {
    // Дано:
    subject.onNext("0123456");

    // Действие:
    when(gateway.sendMeCode(anyString())).thenReturn(Completable.error(new NoNetworkException()));

    // Результат:
    smsUseCase.sendMeCode().test().assertError(NoNetworkException.class);
  }

  /**
   * Должен ответить успехом.
   *
   * @throws Exception error.
   */
  @Test
  public void answerSmsSendSuccessful() throws Exception {
    // Дано:
    subject.onNext("0123456");

    // Действие:
    when(gateway.sendMeCode(anyString())).thenReturn(Completable.complete());

    // Результат:
    smsUseCase.sendMeCode().test().assertComplete();
  }
}
