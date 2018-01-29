package com.fasten.executor_driver.presentation.code;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.Observer;

import com.fasten.executor_driver.backend.web.NoNetworkException;
import com.fasten.executor_driver.backend.web.ValidationException;
import com.fasten.executor_driver.entity.LoginData;
import com.fasten.executor_driver.interactor.auth.PasswordUseCase;
import com.fasten.executor_driver.interactor.auth.PhoneCallUseCase;
import com.fasten.executor_driver.interactor.auth.SmsUseCase;
import com.fasten.executor_driver.presentation.ViewState;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import io.reactivex.Completable;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.CompletableSubject;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CodeViewModelTest {

	private CodeViewModel codeViewModel;

	@Rule
	public TestRule rule = new InstantTaskExecutorRule();

	@Mock
	private PasswordUseCase passwordUseCase;

	@Mock
	private PhoneCallUseCase phoneCallUseCase;

	@Mock
	private SmsUseCase smsUseCase;

	@Mock
	private Observer<ViewState<CodeViewActions>> viewStateObserver;

	@Captor
	private ArgumentCaptor<Completable> afterValidationCaptor;

	private CompletableSubject callRequestSubject;

	@Before
	public void setUp() throws Exception {
		RxJavaPlugins.setSingleSchedulerHandler(scheduler -> Schedulers.trampoline());
		RxAndroidPlugins.setInitMainThreadSchedulerHandler(scheduler -> Schedulers.trampoline());
		when(passwordUseCase.authorize(any(LoginData.class), any(Completable.class))).thenReturn(Completable.never());
		when(phoneCallUseCase.callMe(anyString())).thenReturn(callRequestSubject = CompletableSubject.create());
		when(smsUseCase.sendMeCode(anyString())).thenReturn(Completable.never());
		codeViewModel = new CodeViewModelImpl("1234567890", passwordUseCase, smsUseCase, phoneCallUseCase);
	}

	/* Тетсируем работу с юзкейсом звонка. */

	/**
	 * Должен попросить юзкейс совершить входящий вызов на номер сразу же при создании вьюмодели.
	 *
	 * @throws Exception error.
	 */
	@Test
	public void askPhoneCallUseCaseToCallMe() throws Exception {
		// Результат:
		verify(phoneCallUseCase, only()).callMe("1234567890");
	}

	/**
	 * Не должен трогать другие юзкейсы, пока запрос входящего звонка не завершен.
	 *
	 * @throws Exception error.
	 */
	@Test
	public void doNotTouchOtherUseCasesUntilCallRequestFinished() throws Exception {
		// Действие:
		codeViewModel.sendMeSms();
		codeViewModel.setCode("12");
		codeViewModel.sendMeSms();
		codeViewModel.sendMeSms();
		codeViewModel.setCode("132");
		codeViewModel.setCode("152");

		// Результат:
		verify(phoneCallUseCase, only()).callMe("1234567890");
		verifyZeroInteractions(smsUseCase, passwordUseCase);
	}

	/* Тетсируем работу с юзкейсом СМС. */

	/**
	 * Не должен просить юзкейс отправить СМС с кодом на номер, если предыдущий запрос еще не завершился.
	 *
	 * @throws Exception error.
	 */
	@Test
	public void DoNotAskSmsUseCaseToSendMeCode() throws Exception {
		// Дано:
		callRequestSubject.onComplete();

		// Действие:
		codeViewModel.sendMeSms();
		codeViewModel.sendMeSms();
		codeViewModel.sendMeSms();

		// Результат:
		verify(smsUseCase, only()).sendMeCode("1234567890");
	}

	/**
	 * Должен попросить юзкейс отправить СМС с кодом на номер.
	 *
	 * @throws Exception error.
	 */
	@Test
	public void askSmsUseCaseToSendMeCode() throws Exception {
		// Дано:
		callRequestSubject.onComplete();
		when(smsUseCase.sendMeCode(anyString())).thenReturn(Completable.error(new NoNetworkException()));

		// Действие:
		codeViewModel.sendMeSms();
		codeViewModel.sendMeSms();
		codeViewModel.sendMeSms();

		// Результат:
		verify(smsUseCase, times(3)).sendMeCode("1234567890");
		verifyNoMoreInteractions(smsUseCase);
	}

	/**
	 * Не должен трогать другие юзкейсы, пока запрос СМС еще не завершился.
	 *
	 * @throws Exception error.
	 */
	@Test
	public void doNotTouchOtherUseCasesUntilSmsRequestFinished() throws Exception {
		// Дано:
		callRequestSubject.onComplete();

		// Действие:
		codeViewModel.sendMeSms();
		codeViewModel.setCode("12");
		codeViewModel.sendMeSms();
		codeViewModel.sendMeSms();
		codeViewModel.setCode("132");
		codeViewModel.setCode("152");

		// Результат:
		verify(phoneCallUseCase, only()).callMe("1234567890");
		verify(smsUseCase, only()).sendMeCode("1234567890");
		verifyZeroInteractions(passwordUseCase);
	}

	/* Тетсируем работу с юзкейсом кода. */

	/**
	 * Не должен просить юзкейс авторизироваться, если предыдущий запрос еще не завершился.
	 *
	 * @throws Exception error.
	 */
	@Test
	public void DoNotAskPasswordUseCaseToAuthorize() throws Exception {
		// Дано:
		callRequestSubject.onComplete();

		// Действие:
		codeViewModel.setCode("12");
		codeViewModel.setCode("123");
		codeViewModel.setCode("1234");

		// Результат:
		verify(passwordUseCase, only()).authorize(
				eq(new LoginData("1234567890", "12")),
				afterValidationCaptor.capture()
		);
	}

	/**
	 * Должен попросить юзкейс авторизироваться.
	 *
	 * @throws Exception error.
	 */
	@Test
	public void askPasswordUseCaseToAuthorize() throws Exception {
		// Дано:
		callRequestSubject.onComplete();
		when(passwordUseCase.authorize(any(LoginData.class), any(Completable.class)))
				.thenReturn(Completable.error(new ValidationException()));

		// Действие:
		codeViewModel.setCode("12");
		codeViewModel.setCode("123");
		codeViewModel.setCode("1234");

		// Результат:
		verify(passwordUseCase).authorize(
				eq(new LoginData("1234567890", "12")), afterValidationCaptor.capture()
		);
		verify(passwordUseCase).authorize(
				eq(new LoginData("1234567890", "123")), afterValidationCaptor.capture()
		);
		verify(passwordUseCase).authorize(
				eq(new LoginData("1234567890", "1234")), afterValidationCaptor.capture()
		);
		verifyNoMoreInteractions(passwordUseCase);
	}

	/**
	 * Не должен трогать другие юзкейсы, пока запрос авторизации еще не завершился.
	 *
	 * @throws Exception error.
	 */
	@Test
	public void doNotTouchOtherUseCasesUntilAuthRequestFinished() throws Exception {
		// Дано:
		callRequestSubject.onComplete();

		// Действие:
		codeViewModel.setCode("12");
		codeViewModel.sendMeSms();
		codeViewModel.setCode("132");
		codeViewModel.setCode("152");
		codeViewModel.sendMeSms();
		codeViewModel.sendMeSms();

		// Результат:
		verify(phoneCallUseCase, only()).callMe("1234567890");
		verify(passwordUseCase, only()).authorize(
				eq(new LoginData("1234567890", "12")), afterValidationCaptor.capture()
		);
		verifyZeroInteractions(smsUseCase);
	}

	/* Тетсируем переключение состояний. */

	/**
	 * Должен вернуть состояние вида "Ожидание" изначально по причине запроса входящего звонка.
	 *
	 * @throws Exception error.
	 */
	@Test
	public void setPendingViewStateToLiveDataInitially() throws Exception {
		// Действие:
		codeViewModel.getViewStateLiveData().observeForever(viewStateObserver);

		// Результат:
		verify(viewStateObserver, only()).onChanged(any(CodeViewStatePending.class));
	}

	/**
	 * Должен вернуть состояние вида "Ошибка", если начальный запрос входящего звонка обломался.
	 *
	 * @throws Exception error.
	 */
	@Test
	public void setErrorViewStateToLiveDataIfCallRequestFailed() throws Exception {
		// Дано:
		InOrder inOrder = Mockito.inOrder(viewStateObserver);

		// Действие:
		codeViewModel.getViewStateLiveData().observeForever(viewStateObserver);
		callRequestSubject.onError(new NoNetworkException());

		// Результат:
		inOrder.verify(viewStateObserver).onChanged(any(CodeViewStatePending.class));
		inOrder.verify(viewStateObserver).onChanged(new CodeViewStateError(new NoNetworkException()));
		verifyNoMoreInteractions(viewStateObserver);
	}

	/**
	 * Должен вернуть состояние вида "Начало", если начальный запрос входящего звонка прошел успешно.
	 *
	 * @throws Exception error.
	 */
	@Test
	public void setInitialViewStateToLiveDataIfCallRequestSuccess() throws Exception {
		// Дано:
		InOrder inOrder = Mockito.inOrder(viewStateObserver);

		// Действие:
		codeViewModel.getViewStateLiveData().observeForever(viewStateObserver);
		callRequestSubject.onComplete();

		// Результат:
		inOrder.verify(viewStateObserver).onChanged(any(CodeViewStatePending.class));
		inOrder.verify(viewStateObserver).onChanged(any(CodeViewStateInitial.class));
		verifyNoMoreInteractions(viewStateObserver);
	}

	/**
	 * Не должен менять состояние вида, если код не валидируется
	 *
	 * @throws Exception error
	 */
	@Test
	public void setNoNewViewStateToLiveData() throws Exception {
		// Дано:
		InOrder inOrder = Mockito.inOrder(viewStateObserver);
		codeViewModel.getViewStateLiveData().observeForever(viewStateObserver);
		when(passwordUseCase.authorize(any(LoginData.class), any(Completable.class)))
				.thenReturn(Completable.error(new ValidationException()));
		callRequestSubject.onComplete();

		// Действие:
		codeViewModel.setCode("");
		codeViewModel.setCode("12");
		codeViewModel.setCode("1245");
		codeViewModel.setCode("12457");

		// Результат:
		inOrder.verify(viewStateObserver).onChanged(any(CodeViewStatePending.class));
		inOrder.verify(viewStateObserver).onChanged(any(CodeViewStateInitial.class));
		verifyNoMoreInteractions(viewStateObserver);
	}

	/**
	 * Должен вернуть состояние вида "В процессе" после валидации
	 *
	 * @throws Exception error
	 */
	@Test
	public void setPendingViewStateToLiveDataAfterValidationSucces() throws Exception {
		// Дано:
		InOrder inOrder = Mockito.inOrder(viewStateObserver);
		codeViewModel.getViewStateLiveData().observeForever(viewStateObserver);
		callRequestSubject.onComplete();
		when(passwordUseCase.authorize(any(LoginData.class), any(Completable.class)))
				.thenReturn(Completable.error(new ValidationException()));
		when(passwordUseCase.authorize(
				eq(new LoginData("1234567890", "12457")), any(Completable.class))
		).thenReturn(Completable.never());

		// Действие:
		codeViewModel.setCode("");
		codeViewModel.setCode("12");
		codeViewModel.setCode("1245");
		codeViewModel.setCode("12457");

		// Результат:
		verify(passwordUseCase).authorize(
				eq(new LoginData("1234567890", "12457")), afterValidationCaptor.capture()
		);
		afterValidationCaptor.getValue().test();
		inOrder.verify(viewStateObserver).onChanged(any(CodeViewStatePending.class));
		inOrder.verify(viewStateObserver).onChanged(any(CodeViewStateInitial.class));
		inOrder.verify(viewStateObserver).onChanged(any(CodeViewStatePending.class));
		verifyNoMoreInteractions(viewStateObserver);
	}

	/**
	 * Должен вернуть состояние вида "Ошибка"
	 *
	 * @throws Exception error
	 */
	@Test
	public void setErrorViewStateToLiveData() throws Exception {
		// Дано:
		CompletableSubject completableSubject = CompletableSubject.create();
		InOrder inOrder = Mockito.inOrder(viewStateObserver);
		codeViewModel.getViewStateLiveData().observeForever(viewStateObserver);
		callRequestSubject.onComplete();
		when(passwordUseCase.authorize(any(LoginData.class), any(Completable.class)))
				.thenReturn(completableSubject);

		// Действие:
		codeViewModel.setCode("1245");

		// Результат:
		verify(passwordUseCase).authorize(
				eq(new LoginData("1234567890", "1245")), afterValidationCaptor.capture()
		);
		afterValidationCaptor.getValue().subscribe(
				() -> completableSubject.onError(new NoNetworkException()),
				e -> completableSubject.onComplete()
		);
		inOrder.verify(viewStateObserver).onChanged(any(CodeViewStatePending.class));
		inOrder.verify(viewStateObserver).onChanged(any(CodeViewStateInitial.class));
		inOrder.verify(viewStateObserver).onChanged(any(CodeViewStatePending.class));
		inOrder.verify(viewStateObserver).onChanged(new CodeViewStateError(new NoNetworkException()));
		verifyNoMoreInteractions(viewStateObserver);
	}

	/**
	 * Должен вернуть вернуть начальное состояние вида после "Ошибка"
	 *
	 * @throws Exception error
	 */
	@Test
	public void setInitialViewStateToLiveDataAfterError() throws Exception {
		// Дано:
		CompletableSubject completableSubject = CompletableSubject.create();
		InOrder inOrder = Mockito.inOrder(viewStateObserver);
		codeViewModel.getViewStateLiveData().observeForever(viewStateObserver);
		callRequestSubject.onComplete();
		when(passwordUseCase.authorize(any(LoginData.class), any(Completable.class)))
				.thenReturn(completableSubject);

		// Действие:
		codeViewModel.setCode("1245");
		verify(passwordUseCase).authorize(
				eq(new LoginData("1234567890", "1245")), afterValidationCaptor.capture()
		);
		afterValidationCaptor.getValue().subscribe(
				() -> completableSubject.onError(new NoNetworkException()),
				e -> completableSubject.onComplete()
		);
		when(passwordUseCase.authorize(any(LoginData.class), any(Completable.class)))
				.thenReturn(Completable.error(new ValidationException()));
		codeViewModel.setCode("124");

		// Результат:
		inOrder.verify(viewStateObserver).onChanged(any(CodeViewStatePending.class));
		inOrder.verify(viewStateObserver).onChanged(any(CodeViewStateInitial.class));
		inOrder.verify(viewStateObserver).onChanged(any(CodeViewStatePending.class));
		inOrder.verify(viewStateObserver).onChanged(new CodeViewStateError(new NoNetworkException()));
		inOrder.verify(viewStateObserver).onChanged(any(CodeViewStateInitial.class));
		verifyNoMoreInteractions(viewStateObserver);
	}

	/**
	 * Должен вернуть состояние вида "Успешно"
	 *
	 * @throws Exception error
	 */
	@Test
	public void setSuccessViewStateToLiveDataPending() throws Exception {
		// Дано:
		CompletableSubject completableSubject = CompletableSubject.create();
		InOrder inOrder = Mockito.inOrder(viewStateObserver);
		codeViewModel.getViewStateLiveData().observeForever(viewStateObserver);
		callRequestSubject.onComplete();
		when(passwordUseCase.authorize(any(LoginData.class), any(Completable.class)))
				.thenReturn(completableSubject);

		// Действие:
		codeViewModel.setCode("1245");

		// Результат:
		verify(passwordUseCase).authorize(
				eq(new LoginData("1234567890", "1245")), afterValidationCaptor.capture()
		);
		afterValidationCaptor.getValue().subscribe(
				completableSubject::onComplete,
				e -> completableSubject.onComplete()
		);
		inOrder.verify(viewStateObserver).onChanged(any(CodeViewStatePending.class));
		inOrder.verify(viewStateObserver).onChanged(any(CodeViewStateInitial.class));
		inOrder.verify(viewStateObserver).onChanged(any(CodeViewStatePending.class));
		inOrder.verify(viewStateObserver).onChanged(any(CodeViewStateSuccess.class));
		verifyNoMoreInteractions(viewStateObserver);
	}
}