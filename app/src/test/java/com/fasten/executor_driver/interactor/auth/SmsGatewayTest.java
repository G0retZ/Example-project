package com.fasten.executor_driver.interactor.auth;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasten.executor_driver.backend.web.ApiService;
import com.fasten.executor_driver.backend.web.NoNetworkException;
import com.fasten.executor_driver.gateway.SmsGatewayImpl;
import io.reactivex.Completable;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class SmsGatewayTest {

  private SmsGateway phoneCallGateway;

  @Mock
  private ApiService api;

  @Before
  public void setUp() throws Exception {
    RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());
    RxJavaPlugins.setSingleSchedulerHandler(scheduler -> Schedulers.trampoline());
    phoneCallGateway = new SmsGatewayImpl(api);
    when(api.sendMeCode(anyString())).thenReturn(Completable.never());
  }

  /* Проверяем работу с АПИ */

  /**
   * Должен запросить у АПИ completable на запрос входящего СМС с кодом
   *
   * @throws Exception error
   */
  @Test
  public void smsMeCompletableRequested() throws Exception {
    // Действие:
    phoneCallGateway.sendMeCode("012345");

    // Результат:
    verify(api, only()).sendMeCode("012345");
  }

  /* Проверяем правильность потоков (добавить) */

  /* Проверяем ответы на АПИ */

  /**
   * Должен ответить ошибкой сети
   *
   * @throws Exception error
   */
  @Test
  public void answerNoNetworkError() throws Exception {
    // Действие:
    when(api.sendMeCode(anyString())).thenReturn(Completable.error(new NoNetworkException()));

    // Результат:
    phoneCallGateway.sendMeCode("01234").test().assertError(NoNetworkException.class);
  }

  /**
   * Должен ответить успехом
   *
   * @throws Exception error
   */
  @Test
  public void answerSmsSuccessful() throws Exception {
    // Действие:
    when(api.sendMeCode(anyString())).thenReturn(Completable.complete());

    // Результат:
    phoneCallGateway.sendMeCode("012345").test().assertComplete();
  }
}
