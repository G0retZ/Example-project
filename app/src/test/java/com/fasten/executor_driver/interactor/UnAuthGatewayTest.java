package com.fasten.executor_driver.interactor;

import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.when;

import com.fasten.executor_driver.backend.web.AuthorizationInterceptor;
import io.reactivex.observers.TestObserver;
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
public class UnAuthGatewayTest {

  private AuthorizationInterceptor authorizationInterceptor;

  @Mock
  private Interceptor.Chain chain;

  @Before
  public void setUp() throws Exception {
    RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());
    RxJavaPlugins.setSingleSchedulerHandler(scheduler -> Schedulers.trampoline());
    authorizationInterceptor = new AuthorizationInterceptor();
  }

  /**
   * Не должен сообщать о выходе из системы.
   *
   * @throws Exception error
   */
  @Test
  public void doNotInformAboutUnauthorized() throws Exception {
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
    TestObserver testObserver = authorizationInterceptor.waitForUnauthorized().test();
    authorizationInterceptor.intercept(chain);

    // Результат:
    testObserver.assertNotComplete();
  }

  /**
   * Не должен сообщать о выходе из системы если есть хедер Code = 401.0.
   *
   * @throws Exception error
   */
  @Test
  public void doNotInformAboutUnauthorizedWithCodeHeader401_0() throws Exception {
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
    TestObserver testObserver = authorizationInterceptor.waitForUnauthorized().test();
    authorizationInterceptor.intercept(chain);

    // Результат:
    testObserver.assertNotComplete();
  }

  /**
   * Должен сообщить о выходе из системы.
   *
   * @throws Exception error
   */
  @Test
  public void informAboutUnauthorizedWithAnyOtherCodeHeader() throws Exception {
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

    // Действие:
    TestObserver testObserver = authorizationInterceptor.waitForUnauthorized().test();
    authorizationInterceptor.intercept(chain);

    // Результат:
    testObserver.assertComplete();
  }

  /**
   * Должен сообщить о выходе из системы.
   *
   * @throws Exception error
   */
  @Test
  public void informAboutUnauthorizedWithoutCodeHeader() throws Exception {
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
    TestObserver testObserver = authorizationInterceptor.waitForUnauthorized().test();
    authorizationInterceptor.intercept(chain);

    // Результат:
    testObserver.assertComplete();
  }
}