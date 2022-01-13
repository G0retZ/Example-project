package com.cargopull.executor_driver.interactor;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cargopull.executor_driver.GatewayThreadTestRule;
import com.cargopull.executor_driver.backend.stomp.StompClient;
import com.cargopull.executor_driver.entity.GeoLocation;
import com.cargopull.executor_driver.gateway.GeoTrackingGatewayImpl;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import io.reactivex.Completable;
import io.reactivex.observers.TestObserver;

@RunWith(MockitoJUnitRunner.class)
public class GeoTrackingGatewayTest {

  @ClassRule
  public static final GatewayThreadTestRule classRule = new GatewayThreadTestRule();

  private GeoTrackingGateway gateway;

  @Mock
  private StompClient stompClient;

  @Before
  public void setUp() {
    gateway = new GeoTrackingGatewayImpl(stompClient);
    when(stompClient.send(anyString(), anyString())).thenReturn(Completable.never());
  }

  /* Проверяем работу с клиентом STOMP */

  /**
   * Должен запросить у клиента STOMP отправку сообщения.
   */
  @Test
  public void askStompClientToSendMessage() {
    // Action:
    gateway.sendGeoLocation(new GeoLocation(1, 2, 3)).test().isDisposed();

    // Effect:
    verify(stompClient, only())
        .send("/mobile/online", "{\"latitude\":1.0,\"longitude\":2.0,\"regDate\":3}");
  }

  /* Проверяем ответы на попытку отправки сообщения */

  /**
   * Должен ответить успехом.
   */
  @Test
  public void answerSuccess() {
    // Given:
    when(stompClient.send(anyString(), anyString())).thenReturn(Completable.complete());

    // Action:
    TestObserver<Void> testObserver = gateway.sendGeoLocation(new GeoLocation(1, 2, 3)).test();

    // Effect:
    testObserver.assertComplete();
  }

  /**
   * Должен ответить ошибкой.
   */
  @Test
  public void answerError() {
    // Given:
    when(stompClient.send(anyString(), anyString()))
        .thenReturn(Completable.error(new IllegalArgumentException()));

    // Action:
    TestObserver<Void> testObserver = gateway.sendGeoLocation(new GeoLocation(1, 2, 3)).test();

    // Effect:
    testObserver.assertError(IllegalArgumentException.class);
  }
}