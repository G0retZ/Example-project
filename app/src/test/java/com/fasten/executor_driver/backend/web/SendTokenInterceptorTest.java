package com.fasten.executor_driver.backend.web;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import okhttp3.Interceptor;
import okhttp3.Request;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
	 * Должен запросить у хранителя токенов сохраненный токен
	 *
	 * @throws Throwable error
	 */
	@Test
	public void askTokenKeeperToSaveToken() throws Throwable {
		// given:
		when(chain.request()).thenReturn(
				new Request.Builder()
						.url("http://www.fasten.com")
						.build()
		);

		// when:
		sendTokenInterceptor.intercept(chain);

		// then:
		verify(tokenKeeper, only()).getToken();
	}

	/**
	 * Должен не подсовывать нулевой токен
	 *
	 * @throws Throwable error
	 */
	@Test
	public void doNotInjectTokenIfNull() throws Throwable {
		// given:
		when(chain.request()).thenReturn(new Request.Builder().url("http://www.fasten.com").build());

		// when:
		sendTokenInterceptor.intercept(chain);

		// then:
		verify(chain).proceed(request.capture());
		assertEquals(request.getValue().headers("Authorization").size(), 0);
	}

	/**
	 * Должен не подсунуть токен
	 *
	 * @throws Throwable error
	 */
	@Test
	public void injectToken() throws Throwable {
		// given:
		when(chain.request()).thenReturn(new Request.Builder().url("http://www.fasten.com").build());
		when(tokenKeeper.getToken()).thenReturn("token");

		// when:
		sendTokenInterceptor.intercept(chain);

		// then:
		verify(chain).proceed(request.capture());
		assertEquals(request.getValue().headers("Authorization").size(), 1);
		assertEquals(request.getValue().headers("Authorization").get(0), "token");
	}

}