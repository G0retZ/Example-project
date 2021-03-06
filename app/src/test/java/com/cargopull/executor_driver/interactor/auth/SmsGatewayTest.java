package com.cargopull.executor_driver.interactor.auth;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cargopull.executor_driver.GatewayThreadTestRule;
import com.cargopull.executor_driver.backend.web.ApiService;
import com.cargopull.executor_driver.backend.web.NoNetworkException;
import com.cargopull.executor_driver.gateway.SmsGatewayImpl;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import io.reactivex.Completable;

@RunWith(MockitoJUnitRunner.class)
public class SmsGatewayTest {

  @ClassRule
  public static final GatewayThreadTestRule classRule = new GatewayThreadTestRule();

  private SmsGateway gateway;

  @Mock
  private ApiService api;

  @Before
  public void setUp() {
    gateway = new SmsGatewayImpl(api);
    when(api.sendMeCode(anyString())).thenReturn(Completable.never());
  }

  /* Проверяем работу с АПИ */

  /**
   * Должен запросить у АПИ completable на запрос входящего СМС с кодом.
   */
  @Test
  public void smsMeCompletableRequested() {
    // Action:
    gateway.sendMeCode("012345");

    // Effect:
    verify(api, only()).sendMeCode("012345");
  }

  /* Проверяем ответы на АПИ */

  /**
   * Должен ответить ошибкой сети.
   */
  @Test
  public void answerNoNetworkError() {
    // Action:
    when(api.sendMeCode(anyString())).thenReturn(Completable.error(new NoNetworkException()));

    // Effect:
    gateway.sendMeCode("01234").test().assertError(NoNetworkException.class);
  }

  /**
   * Должен ответить успехом.
   */
  @Test
  public void answerSmsSuccessful() {
    // Action:
    when(api.sendMeCode(anyString())).thenReturn(Completable.complete());

    // Effect:
    gateway.sendMeCode("012345").test().assertComplete();
  }
}
