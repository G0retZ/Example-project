package com.cargopull.executor_driver.interactor;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import com.cargopull.executor_driver.backend.web.NoNetworkException;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.functions.Action;
import io.reactivex.observers.TestObserver;
import java.net.ConnectException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CurrentCostPollingUseCaseTest {

  private CurrentCostPollingUseCase currentCostPollingUseCase;

  @Mock
  private CurrentCostPollingGateway gateway;

  @Mock
  private DataReceiver<String> loginReceiver;

  @Mock
  private Action action;

  @Before
  public void setUp() {
    when(gateway.startPolling(anyString())).thenReturn(Completable.never());
    when(loginReceiver.get()).thenReturn(Observable.never());
    currentCostPollingUseCase = new CurrentCostPollingUseCaseImpl(gateway, loginReceiver);
  }

  /* Проверяем работу с публикатором логина */

  /**
   * Должен запросить у публикатора логин исполнителя.
   */
  @Test
  public void askLoginPublisherForLogin() {
    // Действие:
    currentCostPollingUseCase.listenForPolling().test();
    currentCostPollingUseCase.listenForPolling().test();
    currentCostPollingUseCase.listenForPolling().test();

    // Результат:
    verify(loginReceiver, times(3)).get();
    verifyNoMoreInteractions(loginReceiver);
  }

  /* Проверяем работу с гейтвеем */

  /**
   * Должен запросить у гейтвея таймеры заказа.
   */
  @Test
  public void askGatewayForPollingTimers() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(gateway);
    when(loginReceiver.get()).thenReturn(Observable.just(
        "1234567890", "0987654321", "123454321", "09876567890"
    ));

    // Действие:
    currentCostPollingUseCase.listenForPolling().test();
    currentCostPollingUseCase.listenForPolling().test();

    // Результат:
    inOrder.verify(gateway).startPolling("1234567890");
    inOrder.verify(gateway).startPolling("0987654321");
    inOrder.verify(gateway).startPolling("123454321");
    inOrder.verify(gateway).startPolling("09876567890");
    inOrder.verify(gateway).startPolling("1234567890");
    inOrder.verify(gateway).startPolling("0987654321");
    inOrder.verify(gateway).startPolling("123454321");
    inOrder.verify(gateway).startPolling("09876567890");
    verifyNoMoreInteractions(gateway);
  }

  /**
   * Должен отписаться от предыдущих запросов таймеров заказа.
   *
   * @throws Exception error
   */
  @Test
  public void ubSubscribeFromPreviousPollingTimersRequests() throws Exception {
    // Дано:
    when(loginReceiver.get()).thenReturn(Observable.just(
        "1234567890", "0987654321", "123454321", "09876567890"
    ));
    when(gateway.startPolling(anyString()))
        .thenReturn(Completable.never().doOnDispose(action));

    // Действие:
    currentCostPollingUseCase.listenForPolling().test();

    // Результат:
    verify(action, times(3)).run();
  }

  /**
   * Не должен запрпрашивать у гейтвея таймеры заказа.
   */
  @Test
  public void doNotAskGatewayForPollingTimersIfSocketError() {
    // Действие:
    currentCostPollingUseCase.listenForPolling().test();

    // Результат:
    verifyZeroInteractions(gateway);
  }

  /**
   * Не должен запрпрашивать у гейтвея таймеры заказа.
   */
  @Test
  public void doNotAskGatewayForPollingTimers() {
    // Дано:
    when(loginReceiver.get()).thenReturn(Observable.error(NoNetworkException::new));

    // Действие:
    currentCostPollingUseCase.listenForPolling().test();

    // Результат:
    verifyZeroInteractions(gateway);
  }

  /* Проверяем ответы */

  /**
   * Должен ждать завершения.
   */
  @Test
  public void waitForCompletionOrError() {
    // Дано:
    when(loginReceiver.get()).thenReturn(Observable.just(
        "1234567890", "0987654321", "123454321", "09876567890"
    ));
    // Действие:
    TestObserver<Void> testObserver = currentCostPollingUseCase.listenForPolling().test();

    // Результат:
    testObserver.assertNoValues();
    testObserver.assertNoErrors();
    testObserver.assertNotComplete();
  }

  /**
   * Должен вернуть ошибку, если была ошибка получения логина.
   */
  @Test
  public void answerWithErrorIfGetLoginFailed() {
    when(loginReceiver.get()).thenReturn(Observable.error(ConnectException::new));

    // Действие:
    TestObserver<Void> testObserver = currentCostPollingUseCase.listenForPolling().test();

    // Результат:
    testObserver.assertError(ConnectException.class);
    testObserver.assertNoValues();
    testObserver.assertNotComplete();
  }

  /**
   * Должен вернуть ошибку, если подписка обломалась.
   */
  @Test
  public void answerWithErrorIfSubscriptionFailed() {
    when(loginReceiver.get()).thenReturn(Observable.just("1234567890"));
    when(gateway.startPolling("1234567890")).thenReturn(Completable.error(ConnectException::new));

    // Действие:
    TestObserver<Void> testObserver =
        currentCostPollingUseCase.listenForPolling().test();

    // Результат:
    testObserver.assertError(ConnectException.class);
    testObserver.assertNoValues();
    testObserver.assertNotComplete();
  }

  /**
   * Должен завершить полинг.
   */
  @Test
  public void answerComplete() {
    when(loginReceiver.get()).thenReturn(Observable.just("1234567890"));
    when(gateway.startPolling("1234567890")).thenReturn(Completable.complete());

    // Действие:
    TestObserver<Void> testObserver =
        currentCostPollingUseCase.listenForPolling().test();

    // Результат:
    testObserver.assertComplete();
    testObserver.assertNoValues();
    testObserver.assertNoErrors();
  }
}