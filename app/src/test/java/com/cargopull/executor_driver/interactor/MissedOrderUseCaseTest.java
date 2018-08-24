package com.cargopull.executor_driver.interactor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import com.cargopull.executor_driver.UseCaseThreadTestRule;
import com.cargopull.executor_driver.backend.web.NoNetworkException;
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
public class MissedOrderUseCaseTest {

  @ClassRule
  public static final UseCaseThreadTestRule classRule = new UseCaseThreadTestRule();

  private MissedOrderUseCase useCase;

  @Mock
  private ErrorReporter errorReporter;
  @Mock
  private MissedOrderGateway gateway;
  @Mock
  private DataReceiver<String> loginReceiver;
  @Mock
  private Action action;

  @Before
  public void setUp() {
    when(gateway.loadMissedOrdersMessages()).thenReturn(Flowable.never());
    when(loginReceiver.get()).thenReturn(Observable.never());
    useCase = new MissedOrderUseCaseImpl(errorReporter, gateway, loginReceiver);
  }

  /* Проверяем работу с гейтвеем */

  /**
   * Должен запросить у гейтвея заказы упущенные исполнителем.
   */
  @Test
  public void askGatewayForMissedOrders() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(gateway);
    when(loginReceiver.get()).thenReturn(Observable.just(
        "1234567890", "0987654321", "123454321", "09876567890"
    ));

    // Действие:
    useCase.getMissedOrders().test();

    // Результат:
    inOrder.verify(gateway, times(4)).loadMissedOrdersMessages();
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
    when(loginReceiver.get()).thenReturn(Observable.just(
        "1234567890", "0987654321", "123454321", "09876567890"
    ));
    when(gateway.loadMissedOrdersMessages())
        .thenReturn(Flowable.<String>never().doOnCancel(action));

    // Действие:
    useCase.getMissedOrders().test();

    // Результат:
    verify(action, times(3)).run();
  }

  /**
   * Не должен запрпрашивать у гейтвея заказы упущенные исполнителем.
   */
  @Test
  public void doNotAskGatewayForMissedOrdersIfSocketError() {
    // Действие:
    useCase.getMissedOrders().test();

    // Результат:
    verifyZeroInteractions(gateway);
  }

  /**
   * Не должен запрпрашивать у гейтвея заказы упущенные исполнителем.
   */
  @Test
  public void doNotAskGatewayForMissedOrders() {
    // Дано:
    when(loginReceiver.get()).thenReturn(Observable.error(NoNetworkException::new));

    // Действие:
    useCase.getMissedOrders().test();

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
    useCase.getMissedOrders().test();

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
    when(gateway.loadMissedOrdersMessages())
        .thenReturn(Flowable.error(DataMappingException::new));

    // Действие:
    useCase.getMissedOrders().test();

    // Результат:
    verify(errorReporter, only()).reportError(any(DataMappingException.class));
  }

  /* Проверяем ответы */

  /**
   * Должен вернуть заказы упущенные исполнителем.
   */
  @SuppressWarnings("unchecked")
  @Test
  public void answerWithMissedOrders() {
    // Дано:
    when(loginReceiver.get()).thenReturn(Observable.just("1234567890"));
    when(gateway.loadMissedOrdersMessages())
        .thenReturn(Flowable.just("1", "2", "3"));

    // Действие:
    TestSubscriber<String> testSubscriber = useCase.getMissedOrders().test();

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
    TestSubscriber<String> testSubscriber = useCase.getMissedOrders().test();

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
    when(gateway.loadMissedOrdersMessages())
        .thenReturn(Flowable.error(DataMappingException::new));

    // Действие:
    TestSubscriber<String> testSubscriber = useCase.getMissedOrders().test();

    // Результат:
    testSubscriber.assertError(DataMappingException.class);
    testSubscriber.assertNoValues();
  }

  /**
   * Должен завершить получение заказов упущенных исполнителем.
   */
  @Test
  public void answerComplete() {
    // Дано:
    when(loginReceiver.get()).thenReturn(Observable.just("1234567890"));
    when(gateway.loadMissedOrdersMessages()).thenReturn(Flowable.empty());

    // Действие:
    TestSubscriber<String> testSubscriber = useCase.getMissedOrders().test();

    // Результат:
    testSubscriber.assertComplete();
    testSubscriber.assertNoValues();
    testSubscriber.assertNoErrors();
  }
}