package com.cargopull.executor_driver.interactor;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cargopull.executor_driver.GatewayThreadTestRule;
import com.cargopull.executor_driver.backend.web.ApiService;
import com.cargopull.executor_driver.entity.ExecutorState;
import com.cargopull.executor_driver.gateway.WaitingForClientGatewayImpl;
import io.reactivex.Completable;
import io.reactivex.observers.TestObserver;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class WaitingForClientGatewayTest {

  @ClassRule
  public static final GatewayThreadTestRule classRule = new GatewayThreadTestRule();

  private WaitingForClientGateway gateway;
  @Mock
  private ApiService apiService;

  @Before
  public void setUp() {
    ExecutorState.MOVING_TO_CLIENT.setData(null);
    when(apiService.setOrderStatus(anyString())).thenReturn(Completable.never());
    gateway = new WaitingForClientGatewayImpl(apiService);
  }

  /* Проверяем работу с клиентом STOMP */

  /**
   * Должен запросить у клиента STOMP отправку "начать погрузку".
   */
  @Test
  public void askStompClientToSendStartOrder() {
    // Действие:
    gateway.startTheOrder().test().isDisposed();

    // Результат:
    verify(apiService, only()).setOrderStatus("\"START_ORDER\"");
  }

  /* Проверяем результаты обработки сообщений от сервера по статусам */

  /**
   * Должен ответить успехом.
   */
  @Test
  public void answerStartOrderSuccess() {
    // Дано:
    when(apiService.setOrderStatus(anyString())).thenReturn(Completable.complete());

    // Действие:
    TestObserver<Void> testObserver = gateway.startTheOrder().test();

    // Результат:
    testObserver.assertNoErrors();
    testObserver.assertComplete();
  }

  /**
   * Должен ответить ошибкой.
   */
  @Test
  public void answerStartOrderError() {
    // Дано:
    when(apiService.setOrderStatus(anyString()))
        .thenReturn(Completable.error(new IllegalArgumentException()));

    // Действие:
    TestObserver<Void> testObserver = gateway.startTheOrder().test();

    // Результат:
    testObserver.assertNotComplete();
    testObserver.assertError(IllegalArgumentException.class);
  }
}