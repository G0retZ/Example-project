package com.fasten.executor_driver.backend.web;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import okhttp3.Interceptor;
import okhttp3.Request;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class SendTokenInterceptorTest {

  private Interceptor sendTokenInterceptor;

  @Mock
  private TokenKeeper tokenKeeper;

  @Mock
  private Interceptor.Chain chain;

  @Captor
  private ArgumentCaptor<Request> request;

  @Before
  public void setUp() throws Exception {
    sendTokenInterceptor = new SendTokenInterceptor(tokenKeeper);
  }

  /**
   * Должен запросить у хранителя токенов сохраненный токен.
   *
   * @throws Exception error
   */
  @Test
  public void askTokenKeeperToSaveToken() throws Exception {
    // Дано:
    when(chain.request()).thenReturn(
        new Request.Builder()
            .url("http://www.fasten.com")
            .build()
    );

    // Действие:
    sendTokenInterceptor.intercept(chain);

    // Результат:
    verify(tokenKeeper, only()).getToken();
  }

  /**
   * Должен не подсовывать нулевой токен.
   *
   * @throws Exception error
   */
  @Test
  public void doNotInjectTokenIfNull() throws Exception {
    // Дано:
    when(chain.request()).thenReturn(new Request.Builder().url("http://www.fasten.com").build());

    // Действие:
    sendTokenInterceptor.intercept(chain);

    // Результат:
    verify(chain).proceed(request.capture());
    assertEquals(request.getValue().headers("Authorization").size(), 0);
  }

  /**
   * Должен не подсовывать токен, если в пути есть "login".
   *
   * @throws Exception error
   */
  @Test
  public void doNotInjectTokenIfLogin() throws Exception {
    // Дано:
    when(chain.request())
        .thenReturn(new Request.Builder().url("http://www.fasten.com/login").build());

    // Действие:
    sendTokenInterceptor.intercept(chain);

    // Результат:
    verify(chain).proceed(request.capture());
    assertEquals(request.getValue().headers("Authorization").size(), 0);
  }

  /**
   * Должен подсунуть токен.
   *
   * @throws Exception error
   */
  @Test
  public void injectToken() throws Exception {
    // Дано:
    when(chain.request()).thenReturn(new Request.Builder().url("http://www.fasten.com").build());
    when(tokenKeeper.getToken()).thenReturn("token");

    // Действие:
    sendTokenInterceptor.intercept(chain);

    // Результат:
    verify(chain).proceed(request.capture());
    assertEquals(request.getValue().headers("Authorization").size(), 1);
    assertEquals(request.getValue().headers("Authorization").get(0), "token");
  }
}