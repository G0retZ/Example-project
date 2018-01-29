package com.fasten.executor_driver.interactor.auth;

import com.fasten.executor_driver.backend.web.ApiService;
import com.fasten.executor_driver.backend.web.NoNetworkException;
import com.fasten.executor_driver.gateway.LoginGatewayImpl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import io.reactivex.Completable;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LoginGatewayTest {

	private LoginGateway loginGateway;

	@Mock
	private ApiService api;

	@Before
	public void setUp() throws Exception {
		RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());
		RxJavaPlugins.setSingleSchedulerHandler(scheduler -> Schedulers.trampoline());
		loginGateway = new LoginGatewayImpl(api);
		when(api.checkLogin(anyString())).thenReturn(Completable.never());
	}

	/* Проверяем работу с АПИ */

	/**
	 * Должен запросить у АПИ completable на вход с заданными параметрами
	 *
	 * @throws Exception error
	 */
	@Test
	public void authCompletableRequested() throws Exception {
		// Действие:
		loginGateway.checkLogin("Login");

		// Результат:
		verify(api, only()).checkLogin("Login");
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
		// Действие:
		when(api.checkLogin(anyString())).thenReturn(Completable.error(new NoNetworkException()));

		// Результат:
		loginGateway.checkLogin("Login").test().assertError(NoNetworkException.class);
	}

	/**
	 * Должен ответить успехом
	 *
	 * @throws Exception error
	 */
	@Test
	public void answerLoginSuccessful() throws Exception {
		// Действие:
		when(api.checkLogin(anyString())).thenReturn(Completable.complete());

		// Результат:
		loginGateway.checkLogin("Login").test().assertComplete();
	}
}