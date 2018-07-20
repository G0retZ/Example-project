package com.cargopull.executor_driver.interactor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import com.cargopull.executor_driver.backend.web.NoNetworkException;
import com.cargopull.executor_driver.entity.CancelOrderReason;
import com.cargopull.executor_driver.gateway.DataMappingException;
import com.cargopull.executor_driver.utils.ErrorReporter;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.functions.Action;
import io.reactivex.observers.TestObserver;
import io.reactivex.subscribers.TestSubscriber;
import java.io.IOException;
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

  private CancelOrderUseCase useCase;

  @Mock
  private ErrorReporter errorReporter;
  @Mock
  private CancelOrderGateway gateway;
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
    useCase = new CancelOrderUseCaseImpl(errorReporter, gateway, loginReceiver);
  }

  /* Проверяем работу с публикатором логина */

  /**
   * Должен запросить у публикатора логин исполнителя.
   */
  @Test
  public void askLoginPublisherForLogin() {
    // Действие:
    useCase.getCancelOrderReasons(true).test();

    // Результат:
    verify(loginReceiver, only()).get();
  }

  /**
   * Не должен запрашивать у публикатора логин исполнителя, если не было сброса.
   */
  @Test
  public void doNotTouchLoginPublisherWithoutReset() {
    // Действие:
    useCase.getCancelOrderReasons(false).test();
    useCase.getCancelOrderReasons(false).test();
    useCase.getCancelOrderReasons(false).test();

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
    when(loginReceiver.get()).thenReturn(Observable.just(
        "1234567890", "0987654321", "123454321", "09876567890"
    ));

    // Действие:
    useCase.getCancelOrderReasons(true).test();

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
    when(loginReceiver.get()).thenReturn(Observable.just(
        "1234567890", "0987654321", "123454321", "09876567890"
    ));
    when(gateway.loadCancelOrderReasons(anyString()))
        .thenReturn(Flowable.<List<CancelOrderReason>>never().doOnCancel(action));

    // Действие:
    useCase.getCancelOrderReasons(true).test();

    // Результат:
    verify(action, times(3)).run();
  }

  /**
   * Не должен запрпрашивать у гейтвея список причин для отказа.
   */
  @Test
  public void doNotAskGatewayForCancelReasonsIfSocketError() {
    // Действие:
    useCase.getCancelOrderReasons(true).test();

    // Результат:
    verifyZeroInteractions(gateway);
  }

  /**
   * Не должен запрпрашивать у гейтвея список причин для отказа.
   */
  @Test
  public void doNotAskGatewayForCancelReasons() {
    // Дано:
    when(loginReceiver.get()).thenReturn(Observable.error(NoNetworkException::new));

    // Действие:
    useCase.getCancelOrderReasons(true).test();

    // Результат:
    verifyZeroInteractions(gateway);
  }

  /**
   * Не должен трогать гейтвей без сброса.
   */
  @Test
  public void doNotTouchGatewayWithoutReset() {
    // Действие:
    useCase.getCancelOrderReasons(false).test();
    useCase.getCancelOrderReasons(false).test();
    useCase.getCancelOrderReasons(false).test();

    // Результат:
    verifyZeroInteractions(gateway);
  }

  /**
   * Не должен трогать гейтвей на отправку, если выбор неверный.
   */
  @Test
  public void doNotAskGatewayToCancelIfSelectionInvalid() {
    // Дано:
    when(loginReceiver.get()).thenReturn(Observable.just("1234567890"));
    when(gateway.loadCancelOrderReasons("1234567890")).thenReturn(Flowable.just(
        new ArrayList<>(Arrays.asList(cancelOrderReason, cancelOrderReason2, cancelOrderReason3))
    ));

    // Действие:
    useCase.getCancelOrderReasons(true).test();
    useCase.cancelOrder(cancelOrderReason1).test();

    // Результат:
    verify(gateway, only()).loadCancelOrderReasons("1234567890");
  }

  /**
   * Должен запросить отмену заказа с указанной причиной.
   */
  @Test
  public void askGatewayToCancelOrderWithSelectedReason() {
    // Дано:
    when(loginReceiver.get()).thenReturn(Observable.just("1234567890"));
    when(gateway.loadCancelOrderReasons("1234567890")).thenReturn(Flowable.just(
        new ArrayList<>(Arrays.asList(cancelOrderReason, cancelOrderReason1, cancelOrderReason3))
    ));

    // Действие:
    useCase.getCancelOrderReasons(true).test();
    useCase.cancelOrder(cancelOrderReason1).test();

    // Результат:
    verify(gateway).loadCancelOrderReasons("1234567890");
    verify(gateway).cancelOrder(cancelOrderReason1);
    verifyNoMoreInteractions(gateway);
  }

  /* Проверяем отправку ошибок в репортер */

  /**
   * Должен отправить ошибку, если была ошибка получения логина.
   */
  @Test
  public void reportGetLoginFailed() {
    when(loginReceiver.get()).thenReturn(Observable.error(IOException::new));

    // Действие:
    useCase.getCancelOrderReasons(true).test();

    // Результат:
    verify(errorReporter, only()).reportError(any(IOException.class));
  }

  /**
   * Должен отправить ошибку, если подписка обломалась.
   */
  @Test
  public void reportSubscriptionFailed() {
    when(loginReceiver.get()).thenReturn(Observable.just("1234567890"));
    when(gateway.loadCancelOrderReasons("1234567890"))
        .thenReturn(Flowable.error(DataMappingException::new));

    // Действие:
    useCase.getCancelOrderReasons(true).test();

    // Результат:
    verify(errorReporter, only()).reportError(any(DataMappingException.class));
  }

  /**
   * Должен отправить ошибку, если выбраной причины нет в списке.
   */
  @Test
  public void reportOutOfBoundsError() {
    when(loginReceiver.get()).thenReturn(Observable.just("1234567890"));
    when(gateway.loadCancelOrderReasons("1234567890")).thenReturn(Flowable.just(
        new ArrayList<>(Arrays.asList(
            cancelOrderReason, cancelOrderReason1, cancelOrderReason3
        ))
    ));

    // Действие:
    useCase.getCancelOrderReasons(true).test();
    useCase.cancelOrder(cancelOrderReason2).test();

    // Результат:
    verify(errorReporter, only()).reportError(any(IndexOutOfBoundsException.class));
  }

  /**
   * Не должен отправлять ошибку, если отправка отмены заказа обломалась.
   */
  @Test
  public void doNotReportCancelOrderFailed() {
    when(loginReceiver.get()).thenReturn(Observable.just("1234567890"));
    when(gateway.loadCancelOrderReasons("1234567890")).thenReturn(Flowable.just(
        new ArrayList<>(Arrays.asList(
            cancelOrderReason, cancelOrderReason1, cancelOrderReason2, cancelOrderReason3
        ))
    ));
    when(gateway.cancelOrder(any())).thenReturn(Completable.error(NoNetworkException::new));

    // Действие:
    useCase.getCancelOrderReasons(true).test();
    useCase.cancelOrder(cancelOrderReason2).test();

    // Результат:
    verifyZeroInteractions(errorReporter);
  }

  /* Проверяем ответы */

  /**
   * Должен вернуть список причин для отказа.
   */
  @SuppressWarnings("unchecked")
  @Test
  public void answerWithCancelOrderReason() {
    // Дано:
    when(loginReceiver.get()).thenReturn(Observable.just("1234567890"));
    when(gateway.loadCancelOrderReasons("1234567890")).thenReturn(Flowable.just(
        new ArrayList<>(Arrays.asList(cancelOrderReason, cancelOrderReason2, cancelOrderReason3)),
        new ArrayList<>(Arrays.asList(cancelOrderReason, cancelOrderReason1, cancelOrderReason3)),
        new ArrayList<>(Arrays.asList(cancelOrderReason1, cancelOrderReason2, cancelOrderReason3))
    ));

    // Действие:
    TestSubscriber<List<CancelOrderReason>> testSubscriber =
        useCase.getCancelOrderReasons(true).test();

    // Результат:
    testSubscriber.assertValues(
        new ArrayList<>(Arrays.asList(cancelOrderReason, cancelOrderReason2, cancelOrderReason3)),
        new ArrayList<>(Arrays.asList(cancelOrderReason, cancelOrderReason1, cancelOrderReason3)),
        new ArrayList<>(Arrays.asList(cancelOrderReason1, cancelOrderReason2, cancelOrderReason3))
    );
    testSubscriber.assertNoErrors();
  }

  /**
   * Должен вернуть ошибку, если была ошибка получения логина.
   */
  @Test
  public void answerWithErrorIfGetLoginFailed() {
    when(loginReceiver.get()).thenReturn(Observable.error(IOException::new));

    // Действие:
    TestSubscriber<List<CancelOrderReason>> testSubscriber =
        useCase.getCancelOrderReasons(true).test();

    // Результат:
    testSubscriber.assertError(IOException.class);
    testSubscriber.assertNoValues();
  }

  /**
   * Должен вернуть ошибку, если подписка обломалась.
   */
  @Test
  public void answerWithErrorIfSubscriptionFailed() {
    when(loginReceiver.get()).thenReturn(Observable.just("1234567890"));
    when(gateway.loadCancelOrderReasons("1234567890"))
        .thenReturn(Flowable.error(DataMappingException::new));

    // Действие:
    TestSubscriber<List<CancelOrderReason>> testSubscriber =
        useCase.getCancelOrderReasons(true).test();

    // Результат:
    testSubscriber.assertError(DataMappingException.class);
    testSubscriber.assertNoValues();
  }

  /**
   * Должен завершить получение списка причин для отказа.
   */
  @Test
  public void answerComplete() {
    when(loginReceiver.get()).thenReturn(Observable.just("1234567890"));
    when(gateway.loadCancelOrderReasons("1234567890")).thenReturn(Flowable.empty());

    // Действие:
    TestSubscriber<List<CancelOrderReason>> testSubscriber =
        useCase.getCancelOrderReasons(true).test();

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
        useCase.getCancelOrderReasons(false).test();

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
    when(loginReceiver.get()).thenReturn(Observable.just("1234567890"));
    when(gateway.loadCancelOrderReasons("1234567890")).thenReturn(Flowable.just(
        new ArrayList<>(Arrays.asList(
            cancelOrderReason, cancelOrderReason1, cancelOrderReason3
        ))
    ));

    // Действие и Результат:
    useCase.getCancelOrderReasons(true).test();
    useCase.cancelOrder(cancelOrderReason2).test()
        .assertError(IndexOutOfBoundsException.class);
  }

  /**
   * Должен вернуть ошибку, если отправка отмены заказа обломалась.
   */
  @Test
  public void answerWithErrorIfCancelOrderFailed() {
    when(loginReceiver.get()).thenReturn(Observable.just("1234567890"));
    when(gateway.loadCancelOrderReasons("1234567890")).thenReturn(Flowable.just(
        new ArrayList<>(Arrays.asList(
            cancelOrderReason, cancelOrderReason1, cancelOrderReason2, cancelOrderReason3
        ))
    ));
    when(gateway.cancelOrder(any())).thenReturn(Completable.error(NoNetworkException::new));

    // Действие:
    useCase.getCancelOrderReasons(true).test();
    TestObserver<Void> testSubscriber = useCase.cancelOrder(cancelOrderReason2).test();

    // Результат:
    testSubscriber.assertError(NoNetworkException.class);
    testSubscriber.assertNoValues();
  }

  /**
   * Должен ответить успехом.
   */
  @Test
  public void answerCancelOrderSuccess() {
    when(loginReceiver.get()).thenReturn(Observable.just("1234567890"));
    when(gateway.loadCancelOrderReasons("1234567890")).thenReturn(Flowable.just(
        new ArrayList<>(Arrays.asList(
            cancelOrderReason, cancelOrderReason1, cancelOrderReason2, cancelOrderReason3
        ))
    ));
    when(gateway.cancelOrder(any())).thenReturn(Completable.complete());

    // Действие и Результат:
    useCase.getCancelOrderReasons(true).test();
    useCase.cancelOrder(cancelOrderReason2).test().assertComplete();
    useCase.cancelOrder(cancelOrderReason).test().assertComplete();
  }
}