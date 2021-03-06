package com.cargopull.executor_driver.backend.web;

import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import okhttp3.Interceptor;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;

@RunWith(MockitoJUnitRunner.class)
public class ReceiveTokenInterceptorTest {

  private Interceptor receiveTokenInterceptor;

  @Mock
  private TokenKeeper tokenKeeper;

  @Mock
  private Interceptor.Chain chain;

  @Before
  public void setUp() {
    receiveTokenInterceptor = new ReceiveTokenInterceptor(tokenKeeper);
  }

  /**
   * Должен не трогать хранителя токенов.
   *
   * @throws Exception error
   */
  @Test
  public void doNotTouchTokenKeeper() throws Exception {
    // Given:
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

    // Action:
    receiveTokenInterceptor.intercept(chain);

    // Effect:
    verifyNoInteractions(tokenKeeper);
  }


  /**
   * Должен запросить у хранителя токенов сохранение полученного токена.
   *
   * @throws Exception error
   */
  @Test
  public void askTokenKeeperToSaveToken() throws Exception {
    // Given:
    when(chain.proceed(nullable(Request.class))).thenReturn(
        new Response.Builder()
            .code(200)
            .header("Authorization", "123456")
            .protocol(Protocol.HTTP_1_1)
            .message("")
            .request(new Request.Builder()
                .url("http://www.cargopull.com")
                .build()
            ).build()
    );

    // Action:
    receiveTokenInterceptor.intercept(chain);

    // Effect:
    verify(tokenKeeper, only()).saveToken("123456");
  }
}