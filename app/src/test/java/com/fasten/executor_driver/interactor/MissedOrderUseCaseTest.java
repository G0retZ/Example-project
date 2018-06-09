package com.fasten.executor_driver.interactor;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import com.fasten.executor_driver.backend.web.NoNetworkException;
import io.reactivex.Completable;
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
public class MissedOrderUseCaseTest {

  private MissedOrderUseCase missedOrderUseCase;

  @Mock
  private MissedOrderGateway gateway;
  @Mock
  private SocketGateway socketGateway;
  @Mock
  private DataReceiver<String> loginReceiver;
  @Mock
  private Action action;

  @Before
  public void setUp() {
    when(gateway.loadMissedOrdersMessages(anyString())).thenReturn(Flowable.never());
    when(loginReceiver.get()).thenReturn(Observable.never());
    when(socketGateway.openSocket()).thenReturn(Completable.never());
    missedOrderUseCase = new MissedOrderUseCaseImpl(gateway, socketGateway, loginReceiver);
  }

  /* Проверяем работу с гейтвеем сокета */

  /**
   * Должен запросить у гейтвея соединение.
   */
  @Test
  public void askSocketGatewayForConnection() {
    // Действие:
    missedOrderUseCase.getMissedOrders().test();

    // Результат:
    verify(socketGateway, only()).openSocket();
  }

  /* Проверяем работу с публикатором логина */

  /**
   * Должен запросить у публикатора логин исполнителя.
   */
  @Test
  public void askLoginPublisherForLogin() {
    // Дано:
    when(socketGateway.openSocket()).thenReturn(Completable.complete());

    // Действие:
    missedOrderUseCase.getMissedOrders().test();

    // Результат:
    verify(loginReceiver, only()).get();
  }

  /* Проверяем работу с гейтвеем */

  /**
   * Должен запросить у гейтвея заказы упущенные исполнителем.
   */
  @Test
  public void askGatewayForMissedOrders() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(gateway);
    when(socketGateway.openSocket()).thenReturn(Completable.complete());
    when(loginReceiver.get()).thenReturn(Observable.just(
        "1234567890", "0987654321", "123454321", "09876567890"
    ));

    // Действие:
    missedOrderUseCase.getMissedOrders().test();

    // Результат:
    inOrder.verify(gateway).loadMissedOrdersMessages("1234567890");
    inOrder.verify(gateway).loadMissedOrdersMessages("0987654321");
    inOrder.verify(gateway).loadMissedOrdersMessages("123454321");
    inOrder.verify(gateway).loadMissedOrdersMessages("09876567890");
    verifyNoMoreInteractions(gateway);
  }

  /**
   * Должен отписаться от предыдущих запросов заказов упущенных исполнителем.
   *
   * @throws Exception error
   */
  @Test
  public void ubSubscribeFromPreviousRequestsToGateway() throws Exception {
    // Дано:
    when(socketGateway.openSocket()).thenReturn(Completable.complete());
    when(loginReceiver.get()).thenReturn(Observable.just(
        "1234567890", "0987654321", "123454321", "09876567890"
    ));
    when(gateway.loadMissedOrdersMessages(anyString()))
        .thenReturn(Flowable.<String>never().doOnCancel(action));

    // Действие:
    missedOrderUseCase.getMissedOrders().test();

    // Результат:
    verify(action, times(3)).run();
  }

  /**
   * Не должен запрпрашивать у гейтвея заказы упущенные исполнителем.
   */
  @Test
  public void doNotAskGatewayForMissedOrdersIfSocketError() {
    // Дано:
    when(socketGateway.openSocket()).thenReturn(Completable.error(NoNetworkException::new));

    // Действие:
    missedOrderUseCase.getMissedOrders().test();

    // Результат:
    verifyZeroInteractions(gateway);
  }

  /**
   * Не должен запрпрашивать у гейтвея заказы упущенные исполнителем.
   */
  @Test
  public void doNotAskGatewayForMissedOrders() {
    // Дано:
    when(socketGateway.openSocket()).thenReturn(Completable.complete());
    when(loginReceiver.get()).thenReturn(Observable.error(NoNetworkException::new));

    // Действие:
    missedOrderUseCase.getMissedOrders().test();

    // Результат:
    verifyZeroInteractions(gateway);
  }

  /* Проверяем ответы */

  /**
   * Должен вернуть заказы упущенные исполнителем.
   */
  @SuppressWarnings("unchecked")
  @Test
  public void answerWithMissedOrders() {
    // Дано:
    when(socketGateway.openSocket()).thenReturn(Completable.complete());
    when(loginReceiver.get()).thenReturn(Observable.just("1234567890"));
    when(gateway.loadMissedOrdersMessages("1234567890"))
        .thenReturn(Flowable.just("1", "2", "3"));

    // Действие:
    TestSubscriber<String> testSubscriber = missedOrderUseCase.getMissedOrders().test();

    // Результат:
    testSubscriber.assertValues("1", "2", "3");
    testSubscriber.assertNoErrors();
  }

  /**
   * Должен вернуть ошибку, если открытие сокета обломалось.
   */
  @Test
  public void answerConnectionClosedError() {
    // Дано:
    when(socketGateway.openSocket()).thenReturn(Completable.error(ConnectException::new));

    // Действие:
    TestSubscriber<String> testSubscriber = missedOrderUseCase.getMissedOrders().test();

    // Результат:
    testSubscriber.assertError(ConnectException.class);
    testSubscriber.assertNoValues();
  }

  /**
   * Должен вернуть ошибку, если была ошибка получения логина.
   */
  @Test
  public void answerWithErrorIfGetLoginFailed() {
    // Дано:
    when(socketGateway.openSocket()).thenReturn(Completable.complete());
    when(loginReceiver.get()).thenReturn(Observable.error(ConnectException::new));

    // Действие:
    TestSubscriber<String> testSubscriber = missedOrderUseCase.getMissedOrders().test();

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
    when(socketGateway.openSocket()).thenReturn(Completable.complete());
    when(loginReceiver.get()).thenReturn(Observable.just("1234567890"));
    when(gateway.loadMissedOrdersMessages("1234567890"))
        .thenReturn(Flowable.error(ConnectException::new));

    // Действие:
    TestSubscriber<String> testSubscriber = missedOrderUseCase.getMissedOrders().test();

    // Результат:
    testSubscriber.assertError(ConnectException.class);
    testSubscriber.assertNoValues();
  }

  /**
   * Должен завершить получение заказов упущенных исполнителем.
   */
  @Test
  public void answerComplete() {
    // Дано:
    when(socketGateway.openSocket()).thenReturn(Completable.complete());
    when(loginReceiver.get()).thenReturn(Observable.just("1234567890"));
    when(gateway.loadMissedOrdersMessages("1234567890")).thenReturn(Flowable.empty());

    // Действие:
    TestSubscriber<String> testSubscriber = missedOrderUseCase.getMissedOrders().test();

    // Результат:
    testSubscriber.assertComplete();
    testSubscriber.assertNoValues();
    testSubscriber.assertNoErrors();
  }
}