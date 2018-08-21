package com.cargopull.executor_driver.backend.web;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.when;

import okhttp3.Interceptor;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class DeprecatedVersionInterceptorTest {

  private DeprecatedVersionInterceptor authorizationInterceptor;

  @Mock
  private Interceptor.Chain chain;

  @Before
  public void setUp() {
    authorizationInterceptor = new DeprecatedVersionInterceptor();
  }

  /**
   * Не должен кидать исключение.
   *
   * @throws Exception error
   */
  @Test
  public void doNotThrowUnauthorizedError() throws Exception {
    // Дано:
    when(chain.proceed(nullable(Request.class))).thenReturn(
        new Response.Builder()
            .code(200)
            .protocol(Protocol.HTTP_1_1)
            .message("")
            .request(new Request.Builder()
                .url("http://www.cargopull.com")
                .build()
            ).build()
    );

    // Действие:
    Response response = authorizationInterceptor.intercept(chain);

    // Результат:
    assertEquals(response, chain.proceed(chain.request()));
  }

  /**
   * Должен кинуть исключение устаревшей версии.
   *
   * @throws Exception error
   */
  @Test(expected = DeprecatedVersionException.class)
  public void throwUnauthorizedErrorWithoutCodeHeader() throws Exception {
    // Дано:
    when(chain.proceed(nullable(Request.class))).thenReturn(
        new Response.Builder()
            .code(403)
            .protocol(Protocol.HTTP_1_1)
            .message("")
            .request(new Request.Builder()
                .url("http://www.cargopull.com")
                .build()
            ).build()
    );

    // Действие и Результат:
    authorizationInterceptor.intercept(chain);
  }
}