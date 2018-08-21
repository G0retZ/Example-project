package com.cargopull.executor_driver.interactor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import com.cargopull.executor_driver.UseCaseThreadTestRule;
import com.cargopull.executor_driver.gateway.DataMappingException;
import com.cargopull.executor_driver.utils.ErrorReporter;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.functions.Action;
import io.reactivex.subscribers.TestSubscriber;
import java.io.IOException;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class UpdateMessageUseCaseTest {

  @ClassRule
  public static final UseCaseThreadTestRule classRule = new UseCaseThreadTestRule();

  private UpdateMessageUseCase useCase;

  @Mock
  private ErrorReporter errorReporter;
  @Mock
  private UpdateMessageGateway gateway;
  @Mock
  private DataReceiver<String> loginReceiver;
  @Mock
  private Action action;

  @Before
  public void setUp() {
    when(gateway.loadUpdateMessages(anyString())).thenReturn(Flowable.never());
    when(loginReceiver.get()).thenReturn(Observable.never());
    useCase = new UpdateMessageUseCaseImpl(errorReporter, gateway, loginReceiver);
  }

  /* Проверяем работу с гейтвеем */

  /**
   * Должен запросить у гейтвея сообщения о новой версии.
   */
  @Test
  public void askGatewayForUpdateMessages() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(gateway);
    when(loginReceiver.get()).thenReturn(Observable.just(
        "1234567890", "0987654321", "123454321", "09876567890"
    ));

    // Действие:
    useCase.getUpdateMessages().test();

    // Результат:
    inOrder.verify(gateway).loadUpdateMessages("1234567890");
    inOrder.verify(gateway).loadUpdateMessages("0987654321");
    inOrder.verify(gateway).loadUpdateMessages("123454321");
    inOrder.verify(gateway).loadUpdateMessages("09876567890");
    verifyNoMoreInteractions(gateway);
  }

  /**
   * Должен отписаться от предыдущих запросов сообщений о новых версиях.
   *
   * @throws Exception error
   */
  @Test
  public void ubSubscribeFromPreviousRequestsToGateway() throws Exception {
    // Дано:
    when(loginReceiver.get()).thenReturn(Observable.just(
        "1234567890", "0987654321", "123454321", "09876567890"
    ));
    when(gateway.loadUpdateMessages(anyString()))
        .thenReturn(Flowable.<String>never().doOnCancel(action));

    // Действие:
    useCase.getUpdateMessages().test();

    // Результат:
    verify(action, times(3)).run();
  }

  /**
   * Не должен запрпрашивать у гейтвея сообщения о новой версии.
   */
  @Test
  public void doNotAskGatewayForUpdateMessagesIfNoLogin() {
    // Действие:
    useCase.getUpdateMessages().test();

    // Результат:
    verifyZeroInteractions(gateway);
  }

  /**
   * Не должен запрпрашивать у гейтвея сообщения о новой версии.
   */
  @Test
  public void doNotAskGatewayForUpdateMessagesIfLoginError() {
    // Дано:
    when(loginReceiver.get()).thenReturn(Observable.error(IOException::new));

    // Действие:
    useCase.getUpdateMessages().test();

    // Результат:
    verifyZeroInteractions(gateway);
  }

  /* Проверяем отправку ошибок в репортер */

  /**
   * Должен отправить ошибку, если была ошибка получения логина.
   */
  @Test
  public void reportGetLoginFailed() {
    // Дано:
    when(loginReceiver.get()).thenReturn(Observable.error(IOException::new));

    // Действие:
    useCase.getUpdateMessages().test();

    // Результат:
    verify(errorReporter, only()).reportError(any(IOException.class));
  }

  /**
   * Должен отправить ошибку, если подписка обломалась.
   */
  @Test
  public void reportSubscriptionFailed() {
    // Дано:
    when(loginReceiver.get()).thenReturn(Observable.just("1234567890"));
    when(gateway.loadUpdateMessages("1234567890"))
        .thenReturn(Flowable.error(DataMappingException::new));

    // Действие:
    useCase.getUpdateMessages().test();

    // Результат:
    verify(errorReporter, only()).reportError(any(DataMappingException.class));
  }

  /* Проверяем ответы */

  /**
   * Должен вернуть сообщения о новой версии.
   */
  @SuppressWarnings("unchecked")
  @Test
  public void answerWithUpdateMessages() {
    // Дано:
    when(loginReceiver.get()).thenReturn(Observable.just("1234567890"));
    when(gateway.loadUpdateMessages("1234567890"))
        .thenReturn(Flowable.just("1", "2", "3"));

    // Действие:
    TestSubscriber<String> testSubscriber = useCase.getUpdateMessages().test();

    // Результат:
    testSubscriber.assertValues("1", "2", "3");
    testSubscriber.assertNoErrors();
  }

  /**
   * Должен вернуть ошибку, если была ошибка получения логина.
   */
  @Test
  public void answerWithErrorIfGetLoginFailed() {
    // Дано:
    when(loginReceiver.get()).thenReturn(Observable.error(IOException::new));

    // Действие:
    TestSubscriber<String> testSubscriber = useCase.getUpdateMessages().test();

    // Результат:
    testSubscriber.assertError(IOException.class);
    testSubscriber.assertNoValues();
  }

  /**
   * Должен вернуть ошибку, если подписка обломалась.
   */
  @Test
  public void answerWithErrorIfSubscriptionFailed() {
    // Дано:
    when(loginReceiver.get()).thenReturn(Observable.just("1234567890"));
    when(gateway.loadUpdateMessages("1234567890"))
        .thenReturn(Flowable.error(DataMappingException::new));

    // Действие:
    TestSubscriber<String> testSubscriber = useCase.getUpdateMessages().test();

    // Результат:
    testSubscriber.assertError(DataMappingException.class);
    testSubscriber.assertNoValues();
  }

  /**
   * Должен завершить получение сообщений о новой версии.
   */
  @Test
  public void answerComplete() {
    // Дано:
    when(loginReceiver.get()).thenReturn(Observable.just("1234567890"));
    when(gateway.loadUpdateMessages("1234567890")).thenReturn(Flowable.empty());

    // Действие:
    TestSubscriber<String> testSubscriber = useCase.getUpdateMessages().test();

    // Результат:
    testSubscriber.assertComplete();
    testSubscriber.assertNoValues();
    testSubscriber.assertNoErrors();
  }
}