package com.fasten.executor_driver.interactor.auth;

import com.fasten.executor_driver.backend.web.ApiService;
import com.fasten.executor_driver.backend.web.NoNetworkException;
import com.fasten.executor_driver.backend.web.model.ApiLogin;
import com.fasten.executor_driver.entity.LoginData;
import com.fasten.executor_driver.gateway.PasswordGatewayImpl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import io.reactivex.Completable;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PasswordGatewayTest {

	private PasswordGateway passwordGateway;

	@Mock
	private ApiService api;

	@Before
	public void setUp() throws Exception {
		RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());
		RxJavaPlugins.setSingleSchedulerHandler(scheduler -> Schedulers.trampoline());
		passwordGateway = new PasswordGatewayImpl(api);
		when(api.authorize(any(ApiLogin.class))).thenReturn(Completable.never());
	}

	/* Проверяем работу с АПИ */

	/**
	 * Должен запросить у АПИ completable на вход с заданными параметрами
	 *
	 * @throws Exception error
	 */
	@Test
	public void authCompletableRequested() throws Exception {
		// when:
		passwordGateway.authorize(new LoginData("Login", "Password"));

		// then:
		verify(api, only()).authorize(new ApiLogin("Login", "Password"));
	}

	/* Проверяем правильность потоков (добавить) */

	/* Проверяем ответы на АПИ */

	/**
	 * Должен ответить ошибкой сети
	 *
	 * @throws Exception error
	 */
	@Test
	public void answerNoNetworkError() throws Exception {
		// when:
		when(api.authorize(any(ApiLogin.class))).thenReturn(Completable.error(new NoNetworkException()));

		// then:
		passwordGateway.authorize(new LoginData("Login", "Password"))
				.test().assertError(NoNetworkException.class);
	}

	/**
	 * Должен ответить успехом
	 *
	 * @throws Exception error
	 */
	@Test
	public void answerAuthSuccessful() throws Exception {
		// when:
		when(api.authorize(any(ApiLogin.class))).thenReturn(Completable.complete());

		// then:
		passwordGateway.authorize(new LoginData("Login", "Password"))
				.test().assertComplete();
	}

}