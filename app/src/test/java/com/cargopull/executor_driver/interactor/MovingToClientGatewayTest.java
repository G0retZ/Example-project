package com.cargopull.executor_driver.interactor;

import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cargopull.executor_driver.GatewayThreadTestRule;
import com.cargopull.executor_driver.backend.web.ApiService;
import com.cargopull.executor_driver.entity.ExecutorState;
import com.cargopull.executor_driver.gateway.MovingToClientGatewayImpl;
import io.reactivex.Completable;
import io.reactivex.observers.TestObserver;
import java.util.Collections;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class MovingToClientGatewayTest {

  @ClassRule
  public static final GatewayThreadTestRule classRule = new GatewayThreadTestRule();

  private MovingToClientGateway gateway;
  @Mock
  private ApiService apiService;

  @Before
  public void setUp() {
    ExecutorState.MOVING_TO_CLIENT.setData(null);
    when(apiService.changeOrderStatus(Collections.singletonMap("status", "DRIVER_ARRIVED")))
        .thenReturn(Completable.never());
    gateway = new MovingToClientGatewayImpl(apiService);
  }

  /* Проверяем работу с клиентом STOMP */

  /**
   * Должен запросить у клиента STOMP отправку "я на месте".
   */
  @Test
  public void askStompClientToSendReportArrival() {
    // Действие:
    gateway.reportArrival().test().isDisposed();

    // Результат:
    verify(apiService, only())
        .changeOrderStatus(Collections.singletonMap("status", "DRIVER_ARRIVED"));
  }

  /* Проверяем результаты обработки сообщений от сервера по статусам */

  /**
   * Должен ответить успехом.
   */
  @Test
  public void answerReportArrivalSuccessIfConnected() {
    // Дано:
    when(apiService.changeOrderStatus(Collections.singletonMap("status", "DRIVER_ARRIVED")))
        .thenReturn(Completable.complete());

    // Действие:
    TestObserver<Void> testObserver = gateway.reportArrival().test();

    // Результат:
    testObserver.assertNoErrors();
    testObserver.assertComplete();
  }

  /**
   * Должен ответить ошибкой.
   */
  @Test
  public void answerReportArrivalErrorIfConnected() {
    // Дано:
    when(apiService.changeOrderStatus(Collections.singletonMap("status", "DRIVER_ARRIVED")))
        .thenReturn(Completable.error(new IllegalArgumentException()));

    // Действие:
    TestObserver<Void> testObserver = gateway.reportArrival().test();

    // Результат:
    testObserver.assertNotComplete();
    testObserver.assertError(IllegalArgumentException.class);
  }
}