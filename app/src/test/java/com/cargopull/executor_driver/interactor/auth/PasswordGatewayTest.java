package com.cargopull.executor_driver.interactor.auth;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cargopull.executor_driver.GatewayThreadTestRule;
import com.cargopull.executor_driver.backend.web.ApiService;
import com.cargopull.executor_driver.backend.web.NoNetworkException;
import com.cargopull.executor_driver.backend.web.outgoing.ApiLogin;
import com.cargopull.executor_driver.entity.LoginData;
import com.cargopull.executor_driver.gateway.PasswordGatewayImpl;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import io.reactivex.Completable;

@RunWith(MockitoJUnitRunner.class)
public class PasswordGatewayTest {

  @ClassRule
  public static final GatewayThreadTestRule classRule = new GatewayThreadTestRule();

  private PasswordGateway gateway;

  @Mock
  private ApiService api;

  @Before
  public void setUp() {
    gateway = new PasswordGatewayImpl(api);
    when(api.authorize(any(ApiLogin.class))).thenReturn(Completable.never());
  }

  /* Проверяем работу с АПИ */

  /**
   * Должен запросить у АПИ completable на вход с заданными параметрами.
   */
  @Test
  public void authCompletableRequested() {
    // Action:
    gateway.authorize(new LoginData("Login", "Password"));

    // Effect:
    verify(api, only()).authorize(new ApiLogin("Login", "Password"));
  }

  /* Проверяем ответы на АПИ */

  /**
   * Должен ответить ошибкой сети.
   */
  @Test
  public void answerNoNetworkError() {
    // Action:
    when(api.authorize(any(ApiLogin.class)))
        .thenReturn(Completable.error(new NoNetworkException()));

    // Effect:
    gateway.authorize(new LoginData("Login", "Password"))
        .test().assertError(NoNetworkException.class);
  }

  /**
   * Должен ответить успехом.
   */
  @Test
  public void answerAuthSuccessful() {
    // Action:
    when(api.authorize(any(ApiLogin.class))).thenReturn(Completable.complete());

    // Effect:
    gateway.authorize(new LoginData("Login", "Password"))
        .test().assertComplete();
  }
}