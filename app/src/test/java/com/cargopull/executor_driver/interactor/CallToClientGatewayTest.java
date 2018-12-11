package com.cargopull.executor_driver.interactor;

import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cargopull.executor_driver.GatewayThreadTestRule;
import com.cargopull.executor_driver.backend.web.ApiService;
import com.cargopull.executor_driver.entity.ExecutorState;
import com.cargopull.executor_driver.gateway.CallToClientGatewayImpl;
import io.reactivex.Completable;
import io.reactivex.observers.TestObserver;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CallToClientGatewayTest {

  @ClassRule
  public static final GatewayThreadTestRule classRule = new GatewayThreadTestRule();

  private CallToClientGateway gateway;
  @Mock
  private ApiService apiService;

  @Before
  public void setUp() {
    ExecutorState.MOVING_TO_CLIENT.setData(null);
    when(apiService.callToClient()).thenReturn(Completable.never());
    gateway = new CallToClientGatewayImpl(apiService);
  }

  /* Проверяем работу с клиентом STOMP */

  /**
   * Должен запросить у клиента STOMP отправку "звонок клиенту".
   */
  @Test
  public void askStompClientToSendCallToClient() {
    // Действие:
    gateway.callToClient().test().isDisposed();

    // Результат:
    verify(apiService, only()).callToClient();
  }

  /* Проверяем результаты обработки сообщений от сервера по статусам */

  /**
   * Должен ответить успехом.
   */
  @Test
  public void answerCallToClientSuccess() {
    // Дано:
    when(apiService.callToClient()).thenReturn(Completable.complete());

    // Действие:
    TestObserver<Void> testObserver = gateway.callToClient().test();

    // Результат:
    testObserver.assertNoErrors();
    testObserver.assertComplete();
  }

  /**
   * Должен ответить ошибкой.
   */
  @Test
  public void answerCallToClientError() {
    // Дано:
    when(apiService.callToClient())
        .thenReturn(Completable.error(new IllegalArgumentException()));

    // Действие:
    TestObserver<Void> testObserver = gateway.callToClient().test();

    // Результат:
    testObserver.assertNotComplete();
    testObserver.assertError(IllegalArgumentException.class);
  }
}