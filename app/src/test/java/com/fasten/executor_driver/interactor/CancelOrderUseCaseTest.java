package com.fasten.executor_driver.interactor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import com.fasten.executor_driver.backend.web.NoNetworkException;
import com.fasten.executor_driver.entity.CancelOrderReason;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.functions.Action;
import io.reactivex.observers.TestObserver;
import io.reactivex.subscribers.TestSubscriber;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CancelOrderUseCaseTest {

  private CancelOrderUseCase cancelOrderUseCase;

  @Mock
  private CancelOrderGateway gateway;
  @Mock
  private SocketGateway socketGateway;
  @Mock
  private DataReceiver<String> loginReceiver;
  @Mock
  private CancelOrderReason cancelOrderReason;
  @Mock
  private CancelOrderReason cancelOrderReason1;
  @Mock
  private CancelOrderReason cancelOrderReason2;
  @Mock
  private CancelOrderReason cancelOrderReason3;
  @Mock
  private Action action;

  @Before
  public void setUp() {
    when(gateway.loadCancelOrderReasons(anyString())).thenReturn(Flowable.never());
    when(loginReceiver.get()).thenReturn(Observable.never());
    when(gateway.cancelOrder(any())).thenReturn(Completable.never());
    when(socketGateway.openSocket()).thenReturn(Completable.never());
    cancelOrderUseCase = new CancelOrderUseCaseImpl(gateway, socketGateway, loginReceiver);
  }

  /* Проверяем работу с гейтвеем сокета */

  /**
   * Должен запросить у гейтвея соединение.
   */
  @Test
  public void askSocketGatewayForConnection() {
    // Действие:
    cancelOrderUseCase.getCancelOrderReasons(true).test();

    // Результат:
    verify(socketGateway, only()).openSocket();
  }

  /**
   * Не должен запрашивать у гейтвея соединений, если не было сброса.
   */
  @Test
  public void doNotAskSocketGatewayForConnectionAgain() {
    // Действие:
    cancelOrderUseCase.getCancelOrderReasons(false).test();
    cancelOrderUseCase.getCancelOrderReasons(false).test();
    cancelOrderUseCase.getCancelOrderReasons(false).test();

    // Результат:
    verifyZeroInteractions(socketGateway);
  }

  /**
   * Должен запросить у гейтвея повторное соединение после сброса.
   */
  @Test
  public void askSocketGatewayForConnectionAgainAfterReset() {
    // Действие:
    cancelOrderUseCase.getCancelOrderReasons(false).test();
    cancelOrderUseCase.getCancelOrderReasons(true).test();
    cancelOrderUseCase.getCancelOrderReasons(false).test();
    cancelOrderUseCase.getCancelOrderReasons(true).test();

    // Результат:
    verify(socketGateway, times(2)).openSocket();
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
    cancelOrderUseCase.getCancelOrderReasons(true).test();

    // Результат:
    verify(loginReceiver, only()).get();
  }

  /**
   * Не должен запрашивать у публикатора логин исполнителя, если не было сброса.
   */
  @Test
  public void doNotTouchLoginPublisherWithoutReset() {
    // Действие:
    cancelOrderUseCase.getCancelOrderReasons(false).test();
    cancelOrderUseCase.getCancelOrderReasons(false).test();
    cancelOrderUseCase.getCancelOrderReasons(false).test();

    // Результат:
    verifyZeroInteractions(loginReceiver);
  }

  /* Проверяем работу с гейтвеем */

  /**
   * Должен запросить у гейтвея список причин для отказа.
   */
  @Test
  public void askGatewayForCancelReasons() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(gateway);
    when(socketGateway.openSocket()).thenReturn(Completable.complete());
    when(loginReceiver.get()).thenReturn(Observable.just(
        "1234567890", "0987654321", "123454321", "09876567890"
    ));

    // Действие:
    cancelOrderUseCase.getCancelOrderReasons(true).test();

    // Результат:
    inOrder.verify(gateway).loadCancelOrderReasons("1234567890");
    inOrder.verify(gateway).loadCancelOrderReasons("0987654321");
    inOrder.verify(gateway).loadCancelOrderReasons("123454321");
    inOrder.verify(gateway).loadCancelOrderReasons("09876567890");
    verifyNoMoreInteractions(gateway);
  }

  /**
   * Должен отписаться от предыдущих запросов списка причин для отказа.
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
    when(gateway.loadCancelOrderReasons(anyString()))
        .thenReturn(Flowable.<List<CancelOrderReason>>never().doOnCancel(action));

    // Действие:
    cancelOrderUseCase.getCancelOrderReasons(true).test();

    // Результат:
    verify(action, times(3)).run();
  }

  /**
   * Не должен запрпрашивать у гейтвея список причин для отказа.
   */
  @Test
  public void doNotAskGatewayForCancelReasonsIfSocketError() {
    // Дано:
    when(socketGateway.openSocket()).thenReturn(Completable.error(NoNetworkException::new));

    // Действие:
    cancelOrderUseCase.getCancelOrderReasons(true).test();

    // Результат:
    verifyZeroInteractions(gateway);
  }

  /**
   * Не должен запрпрашивать у гейтвея список причин для отказа.
   */
  @Test
  public void doNotAskGatewayForCancelReasons() {
    // Дано:
    when(socketGateway.openSocket()).thenReturn(Completable.complete());
    when(loginReceiver.get()).thenReturn(Observable.error(NoNetworkException::new));

    // Действие:
    cancelOrderUseCase.getCancelOrderReasons(true).test();

    // Результат:
    verifyZeroInteractions(gateway);
  }

  /**
   * Не должен трогать гейтвей без сброса.
   */
  @Test
  public void doNotTouchGatewayWithoutReset() {
    // Действие:
    cancelOrderUseCase.getCancelOrderReasons(false).test();
    cancelOrderUseCase.getCancelOrderReasons(false).test();
    cancelOrderUseCase.getCancelOrderReasons(false).test();

    // Результат:
    verifyZeroInteractions(gateway);
  }

  /**
   * Не должен трогать гейтвей на отправку, если выбор неверный.
   */
  @Test
  public void doNotAskGatewayToCancelIfSelectionInvalid() {
    // Дано:
    when(socketGateway.openSocket()).thenReturn(Completable.complete());
    when(loginReceiver.get()).thenReturn(Observable.just("1234567890"));
    when(gateway.loadCancelOrderReasons("1234567890")).thenReturn(Flowable.just(
        new ArrayList<>(Arrays.asList(cancelOrderReason, cancelOrderReason2, cancelOrderReason3))
    ));

    // Действие:
    cancelOrderUseCase.getCancelOrderReasons(true).test();
    cancelOrderUseCase.cancelOrder(cancelOrderReason1).test();

    // Результат:
    verify(gateway, only()).loadCancelOrderReasons("1234567890");
  }

  /**
   * Должен запросить отмену заказа с указанной причиной.
   */
  @Test
  public void askGatewayToCancelOrderWithSelectedReason() {
    // Дано:
    when(socketGateway.openSocket()).thenReturn(Completable.complete());
    when(loginReceiver.get()).thenReturn(Observable.just("1234567890"));
    when(gateway.loadCancelOrderReasons("1234567890")).thenReturn(Flowable.just(
        new ArrayList<>(Arrays.asList(cancelOrderReason, cancelOrderReason1, cancelOrderReason3))
    ));

    // Действие:
    cancelOrderUseCase.getCancelOrderReasons(true).test();
    cancelOrderUseCase.cancelOrder(cancelOrderReason1).test();

    // Результат:
    verify(gateway).loadCancelOrderReasons("1234567890");
    verify(gateway).cancelOrder(cancelOrderReason1);
    verifyNoMoreInteractions(gateway);
  }

  /* Проверяем ответы */

  /**
   * Должен вернуть список причин для отказа.
   */
  @SuppressWarnings("unchecked")
  @Test
  public void answerWithCancelOrderReason() {
    // Дано:
    when(socketGateway.openSocket()).thenReturn(Completable.complete());
    when(loginReceiver.get()).thenReturn(Observable.just("1234567890"));
    when(gateway.loadCancelOrderReasons("1234567890")).thenReturn(Flowable.just(
        new ArrayList<>(Arrays.asList(cancelOrderReason, cancelOrderReason2, cancelOrderReason3)),
        new ArrayList<>(Arrays.asList(cancelOrderReason, cancelOrderReason1, cancelOrderReason3)),
        new ArrayList<>(Arrays.asList(cancelOrderReason1, cancelOrderReason2, cancelOrderReason3))
    ));

    // Действие:
    TestSubscriber<List<CancelOrderReason>> testSubscriber =
        cancelOrderUseCase.getCancelOrderReasons(true).test();

    // Результат:
    testSubscriber.assertValues(
        new ArrayList<>(Arrays.asList(cancelOrderReason, cancelOrderReason2, cancelOrderReason3)),
        new ArrayList<>(Arrays.asList(cancelOrderReason, cancelOrderReason1, cancelOrderReason3)),
        new ArrayList<>(Arrays.asList(cancelOrderReason1, cancelOrderReason2, cancelOrderReason3))
    );
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
    TestSubscriber<List<CancelOrderReason>> testSubscriber =
        cancelOrderUseCase.getCancelOrderReasons(true).test();

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
    TestSubscriber<List<CancelOrderReason>> testSubscriber =
        cancelOrderUseCase.getCancelOrderReasons(true).test();

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
    when(gateway.loadCancelOrderReasons("1234567890"))
        .thenReturn(Flowable.error(ConnectException::new));

    // Действие:
    TestSubscriber<List<CancelOrderReason>> testSubscriber =
        cancelOrderUseCase.getCancelOrderReasons(true).test();

    // Результат:
    testSubscriber.assertError(ConnectException.class);
    testSubscriber.assertNoValues();
  }

  /**
   * Должен завершить получение списка причин для отказа.
   */
  @Test
  public void answerComplete() {
    // Дано:
    when(socketGateway.openSocket()).thenReturn(Completable.complete());
    when(loginReceiver.get()).thenReturn(Observable.just("1234567890"));
    when(gateway.loadCancelOrderReasons("1234567890")).thenReturn(Flowable.empty());

    // Действие:
    TestSubscriber<List<CancelOrderReason>> testSubscriber =
        cancelOrderUseCase.getCancelOrderReasons(true).test();

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
    TestSubscriber<List<CancelOrderReason>> testSubscriber =
        cancelOrderUseCase.getCancelOrderReasons(false).test();

    // Результат:
    testSubscriber.assertComplete();
    testSubscriber.assertNoValues();
    testSubscriber.assertNoErrors();
  }

  /**
   * Должен ответить ошибкой, если выбраной причины нет в списке.
   */
  @Test
  public void answerOutOfBoundsError() {
    // Дано:
    when(socketGateway.openSocket()).thenReturn(Completable.complete());
    when(loginReceiver.get()).thenReturn(Observable.just("1234567890"));
    when(gateway.loadCancelOrderReasons("1234567890")).thenReturn(Flowable.just(
        new ArrayList<>(Arrays.asList(
            cancelOrderReason, cancelOrderReason1, cancelOrderReason3
        ))
    ));

    // Действие и Результат:
    cancelOrderUseCase.getCancelOrderReasons(true).test();
    cancelOrderUseCase.cancelOrder(cancelOrderReason2).test()
        .assertError(IndexOutOfBoundsException.class);
  }

  /**
   * Должен вернуть ошибку, если отправка отмены заказа обломалась.
   */
  @Test
  public void answerWithErrorIfCancelOrderFailed() {
    // Дано:
    when(socketGateway.openSocket()).thenReturn(Completable.complete());
    when(loginReceiver.get()).thenReturn(Observable.just("1234567890"));
    when(gateway.loadCancelOrderReasons("1234567890")).thenReturn(Flowable.just(
        new ArrayList<>(Arrays.asList(
            cancelOrderReason, cancelOrderReason1, cancelOrderReason2, cancelOrderReason3
        ))
    ));
    when(gateway.cancelOrder(any())).thenReturn(Completable.error(NoNetworkException::new));

    // Действие:
    cancelOrderUseCase.getCancelOrderReasons(true).test();
    TestObserver<Void> testSubscriber = cancelOrderUseCase.cancelOrder(cancelOrderReason2).test();

    // Результат:
    testSubscriber.assertError(NoNetworkException.class);
    testSubscriber.assertNoValues();
  }

  /**
   * Должен ответить успехом.
   */
  @Test
  public void answerCancelOrderSuccess() {
    // Дано:
    when(socketGateway.openSocket()).thenReturn(Completable.complete());
    when(loginReceiver.get()).thenReturn(Observable.just("1234567890"));
    when(gateway.loadCancelOrderReasons("1234567890")).thenReturn(Flowable.just(
        new ArrayList<>(Arrays.asList(
            cancelOrderReason, cancelOrderReason1, cancelOrderReason2, cancelOrderReason3
        ))
    ));
    when(gateway.cancelOrder(any())).thenReturn(Completable.complete());

    // Действие и Результат:
    cancelOrderUseCase.getCancelOrderReasons(true).test();
    cancelOrderUseCase.cancelOrder(cancelOrderReason2).test().assertComplete();
    cancelOrderUseCase.cancelOrder(cancelOrderReason).test().assertComplete();
  }
}