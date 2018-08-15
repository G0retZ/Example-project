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
import com.cargopull.executor_driver.entity.Order;
import com.cargopull.executor_driver.gateway.DataMappingException;
import com.cargopull.executor_driver.utils.ErrorReporter;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.functions.Action;
import io.reactivex.subscribers.TestSubscriber;
import java.io.IOException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class PreOrderUseCaseTest {

  private OrderUseCase useCase;

  @Mock
  private ErrorReporter errorReporter;
  @Mock
  private PreOrderGateway gateway;
  @Mock
  private DataReceiver<String> loginReceiver;
  @Mock
  private Action action;
  @Mock
  private Order order;
  @Mock
  private Order order2;

  @Before
  public void setUp() {
    when(gateway.getPreOrders(anyString())).thenReturn(Flowable.never());
    when(loginReceiver.get()).thenReturn(Observable.never());
    useCase = new PreOrderUseCaseImpl(errorReporter, gateway, loginReceiver);
  }

  /* Проверяем работу с публикатором логина */

  /**
   * Должен запросить у публикатора логин исполнителя.
   */
  @Test
  public void askLoginPublisherForLogin() {
    // Действие:
    useCase.getOrders().test();
    useCase.getOrders().test();
    useCase.getOrders().test();

    // Результат:
    verify(loginReceiver, times(3)).get();
    verifyNoMoreInteractions(loginReceiver);
  }

  /* Проверяем работу с гейтвеем */

  /**
   * Должен запросить у гейтвея получение предзаказа.
   */
  @Test
  public void askGatewayForOrders() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(gateway);
    when(loginReceiver.get()).thenReturn(Observable.just(
        "1234567890", "0987654321", "123454321", "09876567890"
    ));

    // Действие:
    useCase.getOrders().test();

    // Результат:
    inOrder.verify(gateway).getPreOrders("1234567890");
    inOrder.verify(gateway).getPreOrders("0987654321");
    inOrder.verify(gateway).getPreOrders("123454321");
    inOrder.verify(gateway).getPreOrders("09876567890");
    verifyNoMoreInteractions(gateway);
  }

  /**
   * Должен отписаться от предыдущих запросов предзаказа.
   *
   * @throws Exception error
   */
  @Test
  public void ubSubscribeFromPreviousRequestsToGateway() throws Exception {
    // Дано:
    when(loginReceiver.get()).thenReturn(Observable.just(
        "1234567890", "0987654321", "123454321", "09876567890"
    ));
    when(gateway.getPreOrders(anyString()))
        .thenReturn(Flowable.<Order>never().doOnCancel(action));

    // Действие:
    useCase.getOrders().test();

    // Результат:
    verify(action, times(3)).run();
  }

  /**
   * Не должен запрпрашивать у гейтвея предзаказы.
   */
  @Test
  public void doNotAskGatewayForStatus() {
    // Дано:
    when(loginReceiver.get()).thenReturn(Observable.error(NoNetworkException::new));

    // Действие:
    useCase.getOrders().test();

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
    useCase.getOrders().test();

    // Результат:
    verify(errorReporter).reportError(any(IOException.class));
  }

  /**
   * Должен отправить ошибку маппинга.
   */
  @Test
  public void reportDataMappingError() {
    // Дано:
    when(loginReceiver.get()).thenReturn(Observable.just("1234567890"));
    when(gateway.getPreOrders("1234567890")).thenReturn(Flowable.error(new DataMappingException()));

    // Действие:
    useCase.getOrders().test();

    // Результат:
    verify(errorReporter, only()).reportError(any(DataMappingException.class));
  }

  /* Проверяем ответы на запрос заказов */

  /**
   * Должен ответить предзаказами.
   */
  @Test
  public void answerWithOrders() {
    // Дано:
    when(loginReceiver.get()).thenReturn(Observable.just("1234567890"));
    when(gateway.getPreOrders("1234567890")).thenReturn(Flowable.just(order, order2));

    // Действие:
    TestSubscriber<Order> test = useCase.getOrders().test();

    // Результат:
    test.assertValues(order, order2);
    test.assertComplete();
    test.assertNoErrors();
  }

  /**
   * Должен вернуть ошибку, если была ошибка получения логина.
   */
  @Test
  public void answerWithErrorIfGetLoginFailed() {
    when(loginReceiver.get()).thenReturn(Observable.error(IOException::new));

    // Действие:
    TestSubscriber<Order> testSubscriber = useCase.getOrders().test();

    // Результат:
    testSubscriber.assertError(IOException.class);
    testSubscriber.assertNoValues();
  }

  /**
   * Должен ответить ошибкой маппинга.
   */
  @Test
  public void answerDataMappingError() {
    // Дано:
    when(loginReceiver.get()).thenReturn(Observable.just("1234567890"));
    when(gateway.getPreOrders("1234567890")).thenReturn(Flowable.error(new DataMappingException()));

    // Действие:
    TestSubscriber<Order> test = useCase.getOrders().test();

    // Результат:
    test.assertError(DataMappingException.class);
    test.assertNoValues();
    test.assertNotComplete();
  }

  /**
   * Должен завершить получение статусов.
   */
  @Test
  public void answerComplete() {
    when(loginReceiver.get()).thenReturn(Observable.just("1234567890"));
    when(gateway.getPreOrders("1234567890")).thenReturn(Flowable.empty());

    // Действие:
    TestSubscriber<Order> testSubscriber = useCase.getOrders().test();

    // Результат:
    testSubscriber.assertComplete();
    testSubscriber.assertNoValues();
    testSubscriber.assertNoErrors();
  }
}