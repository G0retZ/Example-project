package com.cargopull.executor_driver.interactor;

import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cargopull.executor_driver.GatewayThreadTestRule;
import com.cargopull.executor_driver.backend.web.ApiService;
import com.cargopull.executor_driver.entity.ExecutorState;
import com.cargopull.executor_driver.gateway.CallToClientGatewayImpl;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;

import io.reactivex.Completable;
import io.reactivex.observers.TestObserver;

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
    when(apiService.callToClient(Collections.singletonMap("status", "CALL_TO_CLIENT")))
        .thenReturn(Completable.never());
    gateway = new CallToClientGatewayImpl(apiService);
  }

  /* Проверяем работу с клиентом STOMP */

  /**
   * Должен запросить у клиента STOMP отправку "звонок клиенту".
   */
  @Test
  public void askStompClientToSendCallToClient() {
    // Action:
    gateway.callToClient().test().isDisposed();

    // Effect:
    verify(apiService, only())
        .callToClient(Collections.singletonMap("status", "CALL_TO_CLIENT"));
  }

  /* Проверяем результаты обработки сообщений от сервера по статусам */

  /**
   * Должен ответить успехом.
   */
  @Test
  public void answerCallToClientSuccess() {
    // Given:
    when(apiService.callToClient(Collections.singletonMap("status", "CALL_TO_CLIENT")))
        .thenReturn(Completable.complete());

    // Action:
    TestObserver<Void> testObserver = gateway.callToClient().test();

    // Effect:
    testObserver.assertNoErrors();
    testObserver.assertComplete();
  }

  /**
   * Должен ответить ошибкой.
   */
  @Test
  public void answerCallToClientError() {
    // Given:
    when(apiService.callToClient(Collections.singletonMap("status", "CALL_TO_CLIENT")))
        .thenReturn(Completable.error(new IllegalArgumentException()));

    // Action:
    TestObserver<Void> testObserver = gateway.callToClient().test();

    // Effect:
    testObserver.assertNotComplete();
    testObserver.assertError(IllegalArgumentException.class);
  }
}