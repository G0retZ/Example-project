package com.cargopull.executor_driver.backend.web;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cargopull.executor_driver.BuildConfig;
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
public class SendVersionInterceptorTest {

  private Interceptor sendVersionInterceptor;

  @Mock
  private Interceptor.Chain chain;

  @Captor
  private ArgumentCaptor<Request> request;

  @Before
  public void setUp() {
    sendVersionInterceptor = new SendVersionInterceptor();
  }

  /**
   * Должен подсунуть хедер.
   *
   * @throws Exception error
   */
  @Test
  public void injectVersion() throws Exception {
    // Дано:
    when(chain.request()).thenReturn(new Request.Builder().url("http://www.cargopull.com").build());

    // Действие:
    sendVersionInterceptor.intercept(chain);

    // Результат:
    verify(chain).proceed(request.capture());
    assertEquals(request.getValue().headers("X-app-version").size(), 1);
    assertEquals(request.getValue().headers("X-app-version").get(0),
        "android-" + BuildConfig.VERSION_NAME);
  }
}