package com.fasten.executor_driver.presentation.phone;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.Observer;

import com.fasten.executor_driver.backend.web.NoNetworkException;
import com.fasten.executor_driver.interactor.auth.LoginUseCase;
import com.fasten.executor_driver.presentation.ViewState;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import io.reactivex.Completable;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PhoneViewModelTest {

	private PhoneViewModel phoneViewModel;

	@Rule
	public TestRule rule = new InstantTaskExecutorRule();

	@Mock
	private LoginUseCase useCase;

	@Mock
	private Observer<ViewState<PhoneViewActions>> viewStateObserver;

	@Before
	public void setUp() throws Exception {
		phoneViewModel = new PhoneViewModelImpl(useCase);
		RxJavaPlugins.setSingleSchedulerHandler(scheduler -> Schedulers.trampoline());
		RxAndroidPlugins.setInitMainThreadSchedulerHandler(scheduler -> Schedulers.trampoline());
		when(useCase.validateLogin(anyString())).thenReturn(Completable.never());
		when(useCase.checkLogin(anyString())).thenReturn(Completable.never());
	}

	/* Тетсируем работу с юзкейсом */

	/**
	 * Должен попросить юзкейс валидировать логин
	 *
	 * @throws Exception error
	 */
	@Test
	public void askUseCaseToValidateLogin() throws Exception {
		when(useCase.validateLogin(anyString())).thenReturn(Completable.error(new IllegalArgumentException()));
		// when:
		phoneViewModel.phoneNumberChanged("12");
		phoneViewModel.phoneNumberChanged("123");
		phoneViewModel.phoneNumberChanged("1234");

		// then:
		verify(useCase).validateLogin("12");
		verify(useCase).validateLogin("123");
		verify(useCase).validateLogin("1234");
		verifyNoMoreInteractions(useCase);
	}

	/**
	 * Не должен попросить юзкейс проверить логин, если он не валидирован
	 *
	 * @throws Exception error
	 */
	@Test
	public void DoNotAskUseCaseToCheckLogin() throws Exception {
		// when:
		phoneViewModel.phoneNumberChanged("12");
		phoneViewModel.phoneNumberChanged("123");
		phoneViewModel.phoneNumberChanged("1234");

		// then:
		verify(useCase, never()).checkLogin(anyString());
	}

	/**
	 * Должен попросить юзкейс проверить логин
	 *
	 * @throws Exception error
	 */
	@Test
	public void askUseCaseToCheckLogin() throws Exception {
		// given:
		when(useCase.validateLogin(anyString())).thenReturn(Completable.error(new IllegalArgumentException()));
		when(useCase.validateLogin("1234")).thenReturn(Completable.complete());

		// when:
		phoneViewModel.phoneNumberChanged("12");
		phoneViewModel.phoneNumberChanged("123");
		phoneViewModel.phoneNumberChanged("1234");

		// then:
		verify(useCase).validateLogin("12");
		verify(useCase).validateLogin("123");
		verify(useCase).validateLogin("1234");
		verify(useCase).checkLogin("1234");
		verifyNoMoreInteractions(useCase);
	}

	/* Тетсируем переключение состояний */

	/**
	 * Должен вернуть начальное состояние вида
	 *
	 * @throws Exception error
	 */
	@Test
	public void setInitialViewStateToLiveData() throws Exception {
		// when:
		phoneViewModel.getViewStateLiveData().observeForever(viewStateObserver);

		// then:
		verify(viewStateObserver, only()).onChanged(any(PhoneViewStateInitial.class));
	}

	/**
	 * Не должен менять состояние вида, если логин не валидируется
	 *
	 * @throws Exception error
	 */
	@Test
	public void setNoNewViewStateToLiveData() throws Exception {
		// given:
		phoneViewModel.getViewStateLiveData().observeForever(viewStateObserver);
		when(useCase.validateLogin(anyString())).thenReturn(Completable.error(new IllegalArgumentException()));

		// when:
		phoneViewModel.phoneNumberChanged("");
		phoneViewModel.phoneNumberChanged("12");
		phoneViewModel.phoneNumberChanged("1245");
		phoneViewModel.phoneNumberChanged("12457");

		// then:
		verify(viewStateObserver, only()).onChanged(any(PhoneViewStateInitial.class));
	}

	/**
	 * Должен вернуть состояние вида "В процессе"
	 *
	 * @throws Exception error
	 */
	@Test
	public void setReadyViewStateToLiveData() throws Exception {
		// given:
		InOrder inOrder = Mockito.inOrder(viewStateObserver);
		phoneViewModel.getViewStateLiveData().observeForever(viewStateObserver);
		when(useCase.validateLogin(anyString())).thenReturn(Completable.error(new IllegalArgumentException()));
		when(useCase.validateLogin("12457")).thenReturn(Completable.complete());

		// when:
		phoneViewModel.phoneNumberChanged("");
		phoneViewModel.phoneNumberChanged("12");
		phoneViewModel.phoneNumberChanged("1245");
		phoneViewModel.phoneNumberChanged("12457");

		// then:
		inOrder.verify(viewStateObserver).onChanged(any(PhoneViewStateInitial.class));
		inOrder.verify(viewStateObserver).onChanged(any(PhoneViewStatePending.class));
		verifyNoMoreInteractions(viewStateObserver);
	}

	/**
	 * Должен вернуть состояние вида "Ошибка"
	 *
	 * @throws Exception error
	 */
	@Test
	public void setErrorViewStateToLiveData() throws Exception {
		// given:
		InOrder inOrder = Mockito.inOrder(viewStateObserver);
		phoneViewModel.getViewStateLiveData().observeForever(viewStateObserver);
		when(useCase.validateLogin(anyString())).thenReturn(Completable.complete());
		when(useCase.checkLogin(anyString())).thenReturn(Completable.error(new NoNetworkException()));

		// when:
		phoneViewModel.phoneNumberChanged("1245");

		// then:
		inOrder.verify(viewStateObserver).onChanged(any(PhoneViewStateInitial.class));
		inOrder.verify(viewStateObserver).onChanged(any(PhoneViewStatePending.class));
		inOrder.verify(viewStateObserver).onChanged(new PhoneViewStateError(new NoNetworkException()));
		verifyNoMoreInteractions(viewStateObserver);
	}

	/**
	 * Должен вернуть вернуть начальное состояние вида после "Ошибка"
	 *
	 * @throws Exception error
	 */
	@Test
	public void setInitialViewStateToLiveDataAfterError() throws Exception {
		// given:
		InOrder inOrder = Mockito.inOrder(viewStateObserver);
		phoneViewModel.getViewStateLiveData().observeForever(viewStateObserver);
		when(useCase.validateLogin(anyString())).thenReturn(Completable.complete());
		when(useCase.checkLogin(anyString())).thenReturn(Completable.error(new NoNetworkException()));

		// when:
		phoneViewModel.phoneNumberChanged("1245");
		when(useCase.validateLogin(anyString())).thenReturn(Completable.error(new IllegalArgumentException()));
		phoneViewModel.phoneNumberChanged("124");

		// then:
		inOrder.verify(viewStateObserver).onChanged(any(PhoneViewStateInitial.class));
		inOrder.verify(viewStateObserver).onChanged(any(PhoneViewStatePending.class));
		inOrder.verify(viewStateObserver).onChanged(new PhoneViewStateError(new NoNetworkException()));
		inOrder.verify(viewStateObserver).onChanged(any(PhoneViewStateInitial.class));
		verifyNoMoreInteractions(viewStateObserver);
	}

	/**
	 * Должен вернуть состояние вида "Готов"
	 *
	 * @throws Exception error
	 */
	@Test
	public void setPendingViewStateToLiveData() throws Exception {
		// given:
		InOrder inOrder = Mockito.inOrder(viewStateObserver);
		phoneViewModel.getViewStateLiveData().observeForever(viewStateObserver);
		when(useCase.validateLogin(anyString())).thenReturn(Completable.complete());
		when(useCase.checkLogin(anyString())).thenReturn(Completable.complete());

		// when:
		phoneViewModel.phoneNumberChanged("1245");

		// then:
		inOrder.verify(viewStateObserver).onChanged(any(PhoneViewStateInitial.class));
		inOrder.verify(viewStateObserver).onChanged(any(PhoneViewStatePending.class));
		inOrder.verify(viewStateObserver).onChanged(any(PhoneViewStateReady.class));
		verifyNoMoreInteractions(viewStateObserver);
	}

	/**
	 * Должен вернуть состояние вида "Продолжай" после "Готов"
	 *
	 * @throws Exception error
	 */
	@Test
	public void setProceedViewStateToLiveDataPending() throws Exception {
		// given:
		InOrder inOrder = Mockito.inOrder(viewStateObserver);
		phoneViewModel.getViewStateLiveData().observeForever(viewStateObserver);
		when(useCase.validateLogin(anyString())).thenReturn(Completable.complete());
		when(useCase.checkLogin(anyString())).thenReturn(Completable.complete());

		// when:
		phoneViewModel.phoneNumberChanged("(124)5");
		phoneViewModel.nextClicked();

		// then:
		inOrder.verify(viewStateObserver).onChanged(any(PhoneViewStateInitial.class));
		inOrder.verify(viewStateObserver).onChanged(any(PhoneViewStatePending.class));
		inOrder.verify(viewStateObserver).onChanged(any(PhoneViewStateReady.class));
		inOrder.verify(viewStateObserver).onChanged(new PhoneViewStateProceed("1245"));
		verifyNoMoreInteractions(viewStateObserver);
	}

}