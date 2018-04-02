package com.fasten.executor_driver.backend.web;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.when;

import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
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
public class AuthorizationInterceptorTest {

  private AuthorizationInterceptor authorizationInterceptor;

  @Mock
  private Interceptor.Chain chain;

  @Before
  public void setUp() {
    RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());
    RxJavaPlugins.setSingleSchedulerHandler(scheduler -> Schedulers.trampoline());
    authorizationInterceptor = new AuthorizationInterceptor();
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
                .url("http://www.fasten.com")
                .build()
            ).build()
    );

    // Действие:
    Response response = authorizationInterceptor.intercept(chain);

    // Результат:
    assertEquals(response, chain.proceed(chain.request()));
  }

  /**
   * Не должен кидать исключение если есть хедер Code = 401.0.
   *
   * @throws Exception error
   */
  @Test
  public void doNotThrowUnauthorizedErrorWithCodeHeader401_0() throws Exception {
    // Дано:
    when(chain.proceed(nullable(Request.class))).thenReturn(
        new Response.Builder()
            .code(401)
            .header("Code", "401.0")
            .protocol(Protocol.HTTP_1_1)
            .message("")
            .request(new Request.Builder()
                .url("http://www.fasten.com")
                .build()
            ).build()
    );

    // Действие:
    Response response = authorizationInterceptor.intercept(chain);

    // Результат:
    assertEquals(response, chain.proceed(chain.request()));
  }

  /**
   * Должен кинуть исключение авторизации.
   *
   * @throws Exception error
   */
  @Test(expected = AuthorizationException.class)
  public void throwUnauthorizedErrorWithAnyOtherCodeHeader() throws Exception {
    // Дано:
    when(chain.proceed(nullable(Request.class))).thenReturn(
        new Response.Builder()
            .code(401)
            .header("Code", "any value")
            .protocol(Protocol.HTTP_1_1)
            .message("")
            .request(new Request.Builder()
                .url("http://www.fasten.com")
                .build()
            ).build()
    );

    // Действие и Результат:
    authorizationInterceptor.intercept(chain);
  }

  /**
   * Должен кинуть исключение авторизации.
   *
   * @throws Exception error
   */
  @Test(expected = AuthorizationException.class)
  public void throwUnauthorizedErrorWithoutCodeHeader() throws Exception {
    // Дано:
    when(chain.proceed(nullable(Request.class))).thenReturn(
        new Response.Builder()
            .code(401)
            .protocol(Protocol.HTTP_1_1)
            .message("")
            .request(new Request.Builder()
                .url("http://www.fasten.com")
                .build()
            ).build()
    );

    // Действие и Результат:
    authorizationInterceptor.intercept(chain);
  }
}
