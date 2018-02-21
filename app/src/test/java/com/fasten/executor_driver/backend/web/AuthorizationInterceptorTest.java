package com.fasten.executor_driver.backend.web;

import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import io.reactivex.subjects.Subject;
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

  private Interceptor authorizationInterceptor;

  @Mock
  private Subject<String> testSubscriber;

  @Mock
  private Interceptor.Chain chain;

  @Before
  public void setUp() throws Exception {
    authorizationInterceptor = new AuthorizationInterceptor(testSubscriber);
  }

  /**
   * Должен не трогать предмет выхода из системы.
   *
   * @throws Exception error
   */
  @Test
  public void doNotTouchLogoutSubject() throws Exception {
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
    authorizationInterceptor.intercept(chain);

    // Результат:
    verifyZeroInteractions(testSubscriber);
  }

  /**
   * Должен не трогать предмет выхода из системы если есть хедер Code
   *
   * @throws Exception error
   */
  @Test
  public void doNotTouchLogoutSubjectWithCodeHeader() throws Exception {
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
    authorizationInterceptor.intercept(chain);

    // Результат:
    verifyZeroInteractions(testSubscriber);
  }

  /**
   * Должен сообщить предмету о выходе из системы.
   *
   * @throws Exception error
   */
  @Test
  public void askLogoutSubjectForLogout() throws Exception {
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

    // Действие:
    authorizationInterceptor.intercept(chain);

    // Результат:
    verify(testSubscriber, only()).onNext("");
  }
}
