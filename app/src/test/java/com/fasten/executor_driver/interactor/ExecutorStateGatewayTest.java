package com.fasten.executor_driver.interactor;

import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasten.executor_driver.backend.web.ApiService;
import com.fasten.executor_driver.backend.web.NoNetworkException;
import com.fasten.executor_driver.entity.ExecutorState;
import com.fasten.executor_driver.gateway.DataMappingException;
import com.fasten.executor_driver.gateway.ExecutorStateApiGatewayImpl;
import io.reactivex.Single;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ExecutorStateGatewayTest {

  private ExecutorStateGateway executorStateGateway;

  @Mock
  private ApiService api;

  @Before
  public void setUp() throws Exception {
    RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());
    RxJavaPlugins.setSingleSchedulerHandler(scheduler -> Schedulers.trampoline());
    executorStateGateway = new ExecutorStateApiGatewayImpl(api);
    when(api.getMyStatus()).thenReturn(Single.never());
  }

  /* Проверяем работу с АПИ */

  /**
   * Должен запросить у АПИ completable на запрос входящего СМС с кодом.
   *
   * @throws Exception error
   */
  @Test
  public void statusRequested() throws Exception {
    // Действие:
    executorStateGateway.getState();

    // Результат:
    verify(api, only()).getMyStatus();
  }

  /* Проверяем правильность потоков (добавить) */

  /* Проверяем ответы на АПИ */

  /**
   * Должен ответить ошибкой сети.
   *
   * @throws Exception error
   */
  @Test
  public void answerNoNetworkError() throws Exception {
    // Действие:
    when(api.getMyStatus()).thenReturn(Single.error(new NoNetworkException()));

    // Результат:
    executorStateGateway.getState().test().assertError(NoNetworkException.class);
  }

  /**
   * Должен ответить ошибкой преобразования статуса.
   *
   * @throws Exception error
   */
  @Test
  public void answerDataMappingError() throws Exception {
    // Действие:
    when(api.getMyStatus()).thenReturn(Single.just("READY"));

    // Результат:
    executorStateGateway.getState().test().assertError(DataMappingException.class);
  }

  /**
   * Должен ответить статусом "смена закрыта".
   *
   * @throws Exception error
   */
  @Test
  public void answerWithShiftClosed() throws Exception {
    // Действие:
    when(api.getMyStatus()).thenReturn(Single.just("\"SHIFT_CLOSED\""));

    // Результат:
    executorStateGateway.getState().test().assertValue(ExecutorState.SHIFT_CLOSED);
  }

  /**
   * Должен ответить статусом "смена открыта".
   *
   * @throws Exception error
   */
  @Test
  public void answerWithShiftOpened() throws Exception {
    // Действие:
    when(api.getMyStatus()).thenReturn(Single.just("\"SHIFT_OPENED\""));

    // Результат:
    executorStateGateway.getState().test().assertValue(ExecutorState.SHIFT_OPENED);
  }

  /**
   * Должен ответить статусом "онлайн".
   *
   * @throws Exception error
   */
  @Test
  public void answerWithOnline() throws Exception {
    // Действие:
    when(api.getMyStatus()).thenReturn(Single.just("\"ONLINE\""));

    // Результат:
    executorStateGateway.getState().test().assertValue(ExecutorState.ONLINE);
  }
}