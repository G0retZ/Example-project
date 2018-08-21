package com.cargopull.executor_driver.interactor;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cargopull.executor_driver.GatewayThreadTestRule;
import com.cargopull.executor_driver.entity.ExecutorState;
import com.cargopull.executor_driver.gateway.ConfirmOrderPaymentGatewayImpl;
import io.reactivex.Completable;
import io.reactivex.observers.TestObserver;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import ua.naiksoftware.stomp.client.StompClient;

@RunWith(MockitoJUnitRunner.class)
public class ConfirmOrderPaymentGatewayTest {

  @ClassRule
  public static final GatewayThreadTestRule classRule = new GatewayThreadTestRule();

  private ConfirmOrderPaymentGateway gateway;
  @Mock
  private StompClient stompClient;

  @Before
  public void setUp() {
    ExecutorState.MOVING_TO_CLIENT.setData(null);
    when(stompClient.send(anyString(), anyString())).thenReturn(Completable.never());
    gateway = new ConfirmOrderPaymentGatewayImpl(stompClient);
  }

  /* Проверяем работу с клиентом STOMP */

  /**
   * Должен запросить у клиента STOMP отправку "подтвердить оплату заказа".
   */
  @Test
  public void askStompClientToSendConfirmOrderPayment() {
    // Действие:
    gateway.confirmOrderPayment().test();

    // Результат:
    verify(stompClient, only()).send("/mobile/trip", "\"COMPLETE_PAYMENT_CONFIRMATION\"");
  }

  /* Проверяем результаты обработки сообщений от сервера */

  /**
   * Должен ответить успехом.
   */
  @Test
  public void answerConfirmOrderPaymentSuccess() {
    // Дано:
    when(stompClient.send(anyString(), anyString())).thenReturn(Completable.complete());

    // Действие:
    TestObserver<Void> testObserver = gateway.confirmOrderPayment().test();

    // Результат:
    testObserver.assertNoErrors();
    testObserver.assertComplete();
  }

  /**
   * Должен ответить ошибкой.
   */
  @Test
  public void answerConfirmOrderPaymentError() {
    // Дано:
    when(stompClient.send(anyString(), anyString()))
        .thenReturn(Completable.error(new IllegalArgumentException()));

    // Действие:
    TestObserver<Void> testObserver = gateway.confirmOrderPayment().test();

    // Результат:
    testObserver.assertNotComplete();
    testObserver.assertError(IllegalArgumentException.class);
  }
}