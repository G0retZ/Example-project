package com.cargopull.executor_driver.interactor.auth;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cargopull.executor_driver.backend.web.ApiService;
import com.cargopull.executor_driver.backend.web.NoNetworkException;
import com.cargopull.executor_driver.gateway.SmsGatewayImpl;
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

  private SmsGateway gateway;

  @Mock
  private ApiService api;

  @Before
  public void setUp() {
    RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());
    RxJavaPlugins.setSingleSchedulerHandler(scheduler -> Schedulers.trampoline());
    gateway = new SmsGatewayImpl(api);
    when(api.sendMeCode(anyString())).thenReturn(Completable.never());
  }

  /* Проверяем работу с АПИ */

  /**
   * Должен запросить у АПИ completable на запрос входящего СМС с кодом.
   */
  @Test
  public void smsMeCompletableRequested() {
    // Действие:
    gateway.sendMeCode("012345");

    // Результат:
    verify(api, only()).sendMeCode("012345");
  }

  /* Проверяем правильность потоков (добавить) */

  /* Проверяем ответы на АПИ */

  /**
   * Должен ответить ошибкой сети.
   */
  @Test
  public void answerNoNetworkError() {
    // Действие:
    when(api.sendMeCode(anyString())).thenReturn(Completable.error(new NoNetworkException()));

    // Результат:
    gateway.sendMeCode("01234").test().assertError(NoNetworkException.class);
  }

  /**
   * Должен ответить успехом.
   */
  @Test
  public void answerSmsSuccessful() {
    // Действие:
    when(api.sendMeCode(anyString())).thenReturn(Completable.complete());

    // Результат:
    gateway.sendMeCode("012345").test().assertComplete();
  }
}
