package com.fasten.executor_driver.presentation.code;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.Observer;
import com.fasten.executor_driver.backend.web.NoNetworkException;
import com.fasten.executor_driver.backend.web.ValidationException;
import com.fasten.executor_driver.interactor.auth.PasswordUseCase;
import com.fasten.executor_driver.presentation.ViewState;
import io.reactivex.Completable;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.CompletableSubject;
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

@RunWith(MockitoJUnitRunner.class)
public class CodeViewModelTest {

  private CodeViewModel codeViewModel;

  @Rule
  public TestRule rule = new InstantTaskExecutorRule();

  @Mock
  private PasswordUseCase passwordUseCase;

  @Mock
  private Observer<ViewState<CodeViewActions>> viewStateObserver;

  @Captor
  private ArgumentCaptor<Completable> afterValidationCaptor;

  @Before
  public void setUp() throws Exception {
    RxJavaPlugins.setSingleSchedulerHandler(scheduler -> Schedulers.trampoline());
    RxAndroidPlugins.setInitMainThreadSchedulerHandler(scheduler -> Schedulers.trampoline());
    when(passwordUseCase.authorize(anyString(), any(Completable.class)))
        .thenReturn(Completable.never());
    codeViewModel = new CodeViewModelImpl(passwordUseCase);
  }

	/* Тетсируем работу с юзкейсом кода. */

  /**
   * Не должен просить юзкейс авторизироваться, если предыдущий запрос еще не завершился.
   *
   * @throws Exception error.
   */
  @Test
  public void DoNotAskPasswordUseCaseToAuthorize() throws Exception {
    // Действие:
    codeViewModel.setCode("12");
    codeViewModel.setCode("123");
    codeViewModel.setCode("1234");

    // Результат:
    verify(passwordUseCase, only()).authorize(eq("12"), afterValidationCaptor.capture());
  }

  /**
   * Должен попросить юзкейс авторизироваться.
   *
   * @throws Exception error.
   */
  @Test
  public void askPasswordUseCaseToAuthorize() throws Exception {
    // Дано:
    when(passwordUseCase.authorize(anyString(), any(Completable.class)))
        .thenReturn(Completable.error(new ValidationException()));

    // Действие:
    codeViewModel.setCode("12");
    codeViewModel.setCode("123");
    codeViewModel.setCode("1234");

    // Результат:
    verify(passwordUseCase).authorize(eq("12"), afterValidationCaptor.capture());
    verify(passwordUseCase).authorize(eq("123"), afterValidationCaptor.capture());
    verify(passwordUseCase).authorize(eq("1234"), afterValidationCaptor.capture());
    verifyNoMoreInteractions(passwordUseCase);
  }

	/* Тетсируем переключение состояний. */

  /**
   * Должен вернуть состояние вида "Начало" изначально.
   *
   * @throws Exception error.
   */
  @Test
  public void setInitialViewStateToLiveData() throws Exception {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);

    // Действие:
    codeViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Результат:
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
    when(passwordUseCase.authorize(anyString(), any(Completable.class)))
        .thenReturn(Completable.error(new ValidationException()));

    // Действие:
    codeViewModel.setCode("");
    codeViewModel.setCode("12");
    codeViewModel.setCode("1245");
    codeViewModel.setCode("12457");

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(CodeViewStateInitial.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида "В процессе" после валидации
   *
   * @throws Exception error
   */
  @Test
  public void setPendingViewStateToLiveDataAfterValidationSuccess() throws Exception {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    codeViewModel.getViewStateLiveData().observeForever(viewStateObserver);
    when(passwordUseCase.authorize(anyString(), any(Completable.class)))
        .thenReturn(Completable.error(new ValidationException()));
    when(passwordUseCase.authorize(eq("12457"), any(Completable.class)))
        .thenReturn(Completable.never());

    // Действие:
    codeViewModel.setCode("");
    codeViewModel.setCode("12");
    codeViewModel.setCode("1245");
    codeViewModel.setCode("12457");

    // Результат:
    verify(passwordUseCase).authorize(eq("12457"), afterValidationCaptor.capture());
    afterValidationCaptor.getValue().test();
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
    when(passwordUseCase.authorize(anyString(), any(Completable.class)))
        .thenReturn(completableSubject);

    // Действие:
    codeViewModel.setCode("1245");

    // Результат:
    verify(passwordUseCase).authorize(eq("1245"), afterValidationCaptor.capture());
    afterValidationCaptor.getValue().subscribe(
        () -> completableSubject.onError(new NoNetworkException()),
        e -> completableSubject.onComplete()
    );
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
    when(passwordUseCase.authorize(anyString(), any(Completable.class)))
        .thenReturn(completableSubject);

    // Действие:
    codeViewModel.setCode("1245");
    verify(passwordUseCase).authorize(eq("1245"), afterValidationCaptor.capture());
    afterValidationCaptor.getValue().subscribe(
        () -> completableSubject.onError(new NoNetworkException()),
        e -> completableSubject.onComplete()
    );
    when(passwordUseCase.authorize(anyString(), any(Completable.class)))
        .thenReturn(Completable.error(new ValidationException()));
    codeViewModel.setCode("124");

    // Результат:
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
    when(passwordUseCase.authorize(anyString(), any(Completable.class)))
        .thenReturn(completableSubject);

    // Действие:
    codeViewModel.setCode("1245");

    // Результат:
    verify(passwordUseCase).authorize(eq("1245"), afterValidationCaptor.capture());
    afterValidationCaptor.getValue().subscribe(
        completableSubject::onComplete,
        e -> completableSubject.onComplete()
    );
    inOrder.verify(viewStateObserver).onChanged(any(CodeViewStateInitial.class));
    inOrder.verify(viewStateObserver).onChanged(any(CodeViewStatePending.class));
    inOrder.verify(viewStateObserver).onChanged(any(CodeViewStateSuccess.class));
    verifyNoMoreInteractions(viewStateObserver);
  }
}