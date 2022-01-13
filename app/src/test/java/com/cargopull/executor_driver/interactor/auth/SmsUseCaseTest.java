package com.cargopull.executor_driver.interactor.auth;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.cargopull.executor_driver.UseCaseThreadTestRule;
import com.cargopull.executor_driver.backend.web.NoNetworkException;
import com.cargopull.executor_driver.entity.ValidationException;
import com.cargopull.executor_driver.entity.Validator;
import com.cargopull.executor_driver.interactor.DataReceiver;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;

@RunWith(MockitoJUnitRunner.class)
public class SmsUseCaseTest {

  @ClassRule
  public static final UseCaseThreadTestRule classRule = new UseCaseThreadTestRule();

  private SmsUseCase useCase;

  @Mock
  private SmsGateway gateway;

  @Mock
  private Validator<String> phoneNumberValidator;

  @Mock
  private DataReceiver<String> phoneNumberReceiver;

  @Before
  public void setUp() throws Exception {
    when(gateway.sendMeCode(anyString())).thenReturn(Completable.never());
    doThrow(new ValidationException()).when(phoneNumberValidator).validate(anyString());
    doNothing().when(phoneNumberValidator).validate("0123456");
    when(phoneNumberReceiver.get()).thenReturn(Observable.never());
    useCase = new SmsUseCaseImpl(gateway, phoneNumberReceiver, phoneNumberValidator);
  }

  /* Проверяем работу с публикатором номера телефона */

  /**
   * Не должен взаимодействовать с публиктором в любых иных случаях.
   */
  @Test
  public void doNotTouchDataSharer() {
    // Action:
    useCase.sendMeCode().test().isDisposed();

    // Effect:
    verify(phoneNumberReceiver, only()).get();
  }

  /* Проверяем работу с валидаторами */

  /**
   * Должен запросить у валидатора номера телефона проверку.
   *
   * @throws Exception error
   */
  @Test
  public void askPhoneNumberValidatorForResult() throws Exception {
    // Given:
    when(phoneNumberReceiver.get()).thenReturn(Observable.just("1", "2", "3"));

    // Action:
    useCase.sendMeCode().test().isDisposed();

    // Effect:
    verify(phoneNumberValidator, only()).validate("1");
  }

  /* Проверяем ответы валидатора */

  /**
   * Должен ответить ошибкой, если номер телефона неверный.
   */
  @Test
  public void answerErrorIfPhoneNumberInvalid() {
    // Given:
    when(phoneNumberReceiver.get()).thenReturn(Observable.just("1", "2", "3"));

    // Action:
    TestObserver<Void> testObserver = useCase.sendMeCode().test();

    // Effect:
    testObserver.assertNoValues();
    testObserver.assertNotComplete();
    testObserver.assertError(ValidationException.class);
  }

  /**
   * Не должно быть ошибок, если номер телефона соответствует формату.
   *
   * @throws Exception error
   */
  @Test
  public void answerSuccessIfPhoneNumberValid() throws Exception {
    // Given:
    when(phoneNumberReceiver.get()).thenReturn(Observable.just("1", "2", "3"));
    doNothing().when(phoneNumberValidator).validate(anyString());

    // Action:
    TestObserver<Void> testObserver = useCase.sendMeCode().test();

    // Effect:
    testObserver.assertNoValues();
    testObserver.assertNotComplete();
    testObserver.assertNoErrors();
  }

  /* Проверяем работу с гейтвеем */

  /**
   * Не должен запрашивать у гейтвея СМС, если валидация не прошла.
   */
  @Test
  public void doNotAskGatewayForSms() {
    // Given:
    when(phoneNumberReceiver.get()).thenReturn(Observable.just("012345", "2", "3"));

    // Action:
    useCase.sendMeCode().test().isDisposed();

    // Effect:
    verifyNoInteractions(gateway);
  }

  /**
   * Должен запросить у гейтвея СМС.
   */
  @Test
  public void askGatewayForSms() {
    // Given:
    when(phoneNumberReceiver.get()).thenReturn(Observable.just("0123456", "2", "3"));

    // Action:
    useCase.sendMeCode().test().isDisposed();

    // Effect:
    verify(gateway, only()).sendMeCode("0123456");
  }

  /* Проверяем ответы на запрос СМС */

  /**
   * Должен ответить ошибкой сети.
   */
  @Test
  public void answerNoNetworkError() {
    // Given:
    when(phoneNumberReceiver.get()).thenReturn(Observable.just("0123456", "2", "3"));
    when(gateway.sendMeCode(anyString())).thenReturn(Completable.error(new NoNetworkException()));

    // Action:
    TestObserver<Void> testObserver = useCase.sendMeCode().test();

    // Effect:
    testObserver.assertNoValues();
    testObserver.assertNotComplete();
    testObserver.assertError(NoNetworkException.class);
  }

  /**
   * Должен ответить успехом.
   */
  @Test
  public void answerSmsSendSuccessful() {
    // Given:
    when(phoneNumberReceiver.get()).thenReturn(Observable.just("0123456", "2", "3"));
    when(gateway.sendMeCode(anyString())).thenReturn(Completable.complete());

    // Action:
    TestObserver<Void> testObserver = useCase.sendMeCode().test();

    // Effect:
    testObserver.assertNoValues();
    testObserver.assertNoErrors();
    testObserver.assertComplete();
  }
}
