package com.cargopull.executor_driver.interactor;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import com.cargopull.executor_driver.backend.web.NoNetworkException;
import com.cargopull.executor_driver.entity.ExecutorBalance;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.functions.Action;
import io.reactivex.subscribers.TestSubscriber;
import java.net.ConnectException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ExecutorBalanceUseCaseTest {

  private ExecutorBalanceUseCase useCase;

  @Mock
  private ExecutorBalanceGateway gateway;
  @Mock
  private DataReceiver<String> loginReceiver;
  @Mock
  private ExecutorBalance executorBalance;
  @Mock
  private ExecutorBalance executorBalance1;
  @Mock
  private ExecutorBalance executorBalance2;
  @Mock
  private Action action;

  @Before
  public void setUp() {
    when(gateway.loadExecutorBalance(anyString())).thenReturn(Flowable.never());
    when(loginReceiver.get()).thenReturn(Observable.never());
    useCase = new ExecutorBalanceUseCaseImpl(gateway, loginReceiver);
  }

  /* Проверяем работу с публикатором логина */

  /**
   * Не должен запрашивать у публикатора логин исполнителя, если не было сброса.
   */
  @Test
  public void doNotTouchLoginPublisherWithoutReset() {
    // Действие:
    useCase.getExecutorBalance(false).test();
    useCase.getExecutorBalance(false).test();
    useCase.getExecutorBalance(false).test();

    // Результат:
    verifyZeroInteractions(loginReceiver);
  }

  /**
   * Должен запросивать у публикатора логин исполнителя.
   */
  @Test
  public void askLoginPublisherForLogin() {
    // Действие:
    useCase.getExecutorBalance(true).test();
    useCase.getExecutorBalance(true).test();
    useCase.getExecutorBalance(true).test();

    // Результат:
    verify(loginReceiver, times(3)).get();
    verifyNoMoreInteractions(loginReceiver);
  }

  /* Проверяем работу с гейтвеем */

  /**
   * Должен запросить у гейтвея баланс исполнителя.
   */
  @Test
  public void askGatewayForExecutorBalance() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(gateway);
    when(loginReceiver.get()).thenReturn(Observable.just(
        "1234567890", "0987654321", "123454321", "09876567890"
    ));

    // Действие:
    useCase.getExecutorBalance(true).test();

    // Результат:
    inOrder.verify(gateway).loadExecutorBalance("1234567890");
    inOrder.verify(gateway).loadExecutorBalance("0987654321");
    inOrder.verify(gateway).loadExecutorBalance("123454321");
    inOrder.verify(gateway).loadExecutorBalance("09876567890");
    verifyNoMoreInteractions(gateway);
  }

  /**
   * Должен отписаться от предыдущих запросов баланса исполнителя.
   *
   * @throws Exception error
   */
  @Test
  public void ubSubscribeFromPreviousRequestsToGateway() throws Exception {
    // Дано:
    when(loginReceiver.get()).thenReturn(Observable.just(
        "1234567890", "0987654321", "123454321", "09876567890"
    ));
    when(gateway.loadExecutorBalance(anyString()))
        .thenReturn(Flowable.<ExecutorBalance>never().doOnCancel(action));

    // Действие:
    useCase.getExecutorBalance(true).test();

    // Результат:
    verify(action, times(3)).run();
  }

  /**
   * Не должен запрпрашивать у гейтвея баланс исполнителя.
   */
  @Test
  public void doNotAskGatewayForExecutorBalanceIfSocketError() {
    // Действие:
    useCase.getExecutorBalance(true).test();

    // Результат:
    verifyZeroInteractions(gateway);
  }

  /**
   * Не должен запрпрашивать у гейтвея баланс исполнителя.
   */
  @Test
  public void doNotAskGatewayForExecutorBalance() {
    // Дано:
    when(loginReceiver.get()).thenReturn(Observable.error(NoNetworkException::new));

    // Действие:
    useCase.getExecutorBalance(true).test();

    // Результат:
    verifyZeroInteractions(gateway);
  }

  /**
   * Не должен трогать гейтвей без сброса.
   */
  @Test
  public void doNotTouchGatewayWithoutReset() {
    // Действие:
    useCase.getExecutorBalance(false).test();
    useCase.getExecutorBalance(false).test();
    useCase.getExecutorBalance(false).test();

    // Результат:
    verifyZeroInteractions(gateway);
  }

  /* Проверяем ответы */

  /**
   * Должен вернуть баланс исполнителя.
   */
  @SuppressWarnings("unchecked")
  @Test
  public void answerWithExecutorBalance() {
    // Дано:
    when(loginReceiver.get()).thenReturn(Observable.just("1234567890"));
    when(gateway.loadExecutorBalance("1234567890"))
        .thenReturn(Flowable.just(executorBalance, executorBalance2, executorBalance1));

    // Действие:
    TestSubscriber<ExecutorBalance> testSubscriber =
        useCase.getExecutorBalance(true).test();

    // Результат:
    testSubscriber.assertValues(executorBalance, executorBalance2, executorBalance1);
    testSubscriber.assertNoErrors();
  }

  /**
   * Должен вернуть ошибку, если была ошибка получения логина.
   */
  @Test
  public void answerWithErrorIfGetLoginFailed() {
    // Дано:
    when(loginReceiver.get()).thenReturn(Observable.error(ConnectException::new));

    // Действие:
    TestSubscriber<ExecutorBalance> testSubscriber =
        useCase.getExecutorBalance(true).test();

    // Результат:
    testSubscriber.assertError(ConnectException.class);
    testSubscriber.assertNoValues();
  }

  /**
   * Должен вернуть ошибку, если подписка обломалась.
   */
  @Test
  public void answerWithErrorIfSubscriptionFailed() {
    // Дано:
    when(loginReceiver.get()).thenReturn(Observable.just("1234567890"));
    when(gateway.loadExecutorBalance("1234567890"))
        .thenReturn(Flowable.error(ConnectException::new));

    // Действие:
    TestSubscriber<ExecutorBalance> testSubscriber =
        useCase.getExecutorBalance(true).test();

    // Результат:
    testSubscriber.assertError(ConnectException.class);
    testSubscriber.assertNoValues();
  }

  /**
   * Должен завершить получение баланса исполнителя.
   */
  @Test
  public void answerComplete() {
    // Дано:
    when(loginReceiver.get()).thenReturn(Observable.just("1234567890"));
    when(gateway.loadExecutorBalance("1234567890")).thenReturn(Flowable.empty());

    // Действие:
    TestSubscriber<ExecutorBalance> testSubscriber =
        useCase.getExecutorBalance(true).test();

    // Результат:
    testSubscriber.assertComplete();
    testSubscriber.assertNoValues();
    testSubscriber.assertNoErrors();
  }

  /**
   * Должен ответить завершением без сброса.
   */
  @Test
  public void answerCompleteWithoutReset() {
    // Действие:
    TestSubscriber<ExecutorBalance> testSubscriber =
        useCase.getExecutorBalance(false).test();

    // Результат:
    testSubscriber.assertComplete();
    testSubscriber.assertNoValues();
    testSubscriber.assertNoErrors();
  }
}