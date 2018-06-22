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
import com.fasten.executor_driver.entity.ValidationException;
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

  @Rule
  public TestRule rule = new InstantTaskExecutorRule();
  private CodeViewModel viewModel;
  @Mock
  private PasswordUseCase passwordUseCase;

  @Mock
  private Observer<ViewState<CodeViewActions>> viewStateObserver;

  @Mock
  private Observer<String> navigateObserver;

  @Captor
  private ArgumentCaptor<Completable> afterValidationCaptor;

  @Before
  public void setUp() {
    RxJavaPlugins.setSingleSchedulerHandler(scheduler -> Schedulers.trampoline());
    RxAndroidPlugins.setInitMainThreadSchedulerHandler(scheduler -> Schedulers.trampoline());
    when(passwordUseCase.authorize(anyString(), any(Completable.class)))
        .thenReturn(Completable.never());
    viewModel = new CodeViewModelImpl(passwordUseCase);
  }

  /* Тетсируем работу с юзкейсом кода. */

  /**
   * Не должен просить юзкейс авторизироваться, если предыдущий запрос еще не завершился.
   */
  @Test
  public void DoNotAskPasswordUseCaseToAuthorize() {
    // Действие:
    viewModel.setCode("1   2   ");
    viewModel.setCode("1   2   3   ");
    viewModel.setCode("1   2   3   4");

    // Результат:
    verify(passwordUseCase, only()).authorize(eq("12"), afterValidationCaptor.capture());
  }

  /**
   * Должен попросить юзкейс авторизироваться.
   */
  @Test
  public void askPasswordUseCaseToAuthorize() {
    // Дано:
    when(passwordUseCase.authorize(anyString(), any(Completable.class)))
        .thenReturn(Completable.error(new ValidationException()));

    // Действие:
    viewModel.setCode("1   2   ");
    viewModel.setCode("1   2   3   ");
    viewModel.setCode("1   2   3   4");

    // Результат:
    verify(passwordUseCase).authorize(eq("12"), afterValidationCaptor.capture());
    verify(passwordUseCase).authorize(eq("123"), afterValidationCaptor.capture());
    verify(passwordUseCase).authorize(eq("1234"), afterValidationCaptor.capture());
    verifyNoMoreInteractions(passwordUseCase);
  }

  /* Тетсируем переключение состояний. */

  /**
   * Должен вернуть состояние вида "Начало" изначально.
   */
  @Test
  public void setInitialViewStateToLiveData() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);

    // Действие:
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(CodeViewStateInitial.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Не должен менять состояние вида, если код не валидируется.
   */
  @Test
  public void setNoNewViewStateToLiveData() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);
    when(passwordUseCase.authorize(anyString(), any(Completable.class)))
        .thenReturn(Completable.error(new ValidationException()));

    // Действие:
    viewModel.setCode("");
    viewModel.setCode("1   2   ");
    viewModel.setCode("1   2   3   ");
    viewModel.setCode("1   2   3   4");

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(CodeViewStateInitial.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида "В процессе" после валидации.
   */
  @Test
  public void setPendingViewStateToLiveDataAfterValidationSuccess() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);
    when(passwordUseCase.authorize(anyString(), any(Completable.class)))
        .thenReturn(Completable.error(new ValidationException()));
    when(passwordUseCase.authorize(eq("12457"), any(Completable.class)))
        .thenReturn(Completable.never());

    // Действие:
    viewModel.setCode("");
    viewModel.setCode("1   2   ");
    viewModel.setCode("1   2   4   5   ");
    viewModel.setCode("1   2   4   5   7   ");

    // Результат:
    verify(passwordUseCase).authorize(eq("12457"), afterValidationCaptor.capture());
    afterValidationCaptor.getValue().test();
    inOrder.verify(viewStateObserver).onChanged(any(CodeViewStateInitial.class));
    inOrder.verify(viewStateObserver).onChanged(any(CodeViewStatePending.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида "Ошибка кода".
   */
  @Test
  public void setErrorViewStateToLiveData() {
    // Дано:
    CompletableSubject completableSubject = CompletableSubject.create();
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);
    when(passwordUseCase.authorize(anyString(), any(Completable.class)))
        .thenReturn(completableSubject);

    // Действие:
    viewModel.setCode("1   2   4   5");

    // Результат:
    verify(passwordUseCase).authorize(eq("1245"), afterValidationCaptor.capture());
    afterValidationCaptor.getValue().subscribe(
        () -> completableSubject.onError(new IllegalArgumentException()),
        e -> completableSubject.onComplete()
    );
    inOrder.verify(viewStateObserver).onChanged(any(CodeViewStateInitial.class));
    inOrder.verify(viewStateObserver).onChanged(any(CodeViewStatePending.class));
    inOrder.verify(viewStateObserver).onChanged(any(CodeViewStateError.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть вернуть начальное состояние вида после "Ошибка кода".
   */
  @Test
  public void setInitialViewStateToLiveDataAfterError() {
    // Дано:
    CompletableSubject completableSubject = CompletableSubject.create();
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);
    when(passwordUseCase.authorize(anyString(), any(Completable.class)))
        .thenReturn(completableSubject);

    // Действие:
    viewModel.setCode("1   2   4   5");
    verify(passwordUseCase).authorize(eq("1245"), afterValidationCaptor.capture());
    afterValidationCaptor.getValue().subscribe(
        () -> completableSubject.onError(new IllegalArgumentException()),
        e -> completableSubject.onComplete()
    );
    when(passwordUseCase.authorize(anyString(), any(Completable.class)))
        .thenReturn(Completable.error(new ValidationException()));
    viewModel.setCode("1   2   4   ");

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(CodeViewStateInitial.class));
    inOrder.verify(viewStateObserver).onChanged(any(CodeViewStatePending.class));
    inOrder.verify(viewStateObserver).onChanged(any(CodeViewStateError.class));
    inOrder.verify(viewStateObserver).onChanged(any(CodeViewStateInitial.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида "Ошибка сети".
   */
  @Test
  public void setNetworkErrorViewStateToLiveData() {
    // Дано:
    CompletableSubject completableSubject = CompletableSubject.create();
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);
    when(passwordUseCase.authorize(anyString(), any(Completable.class)))
        .thenReturn(completableSubject);

    // Действие:
    viewModel.setCode("1   2   4   5");

    // Результат:
    verify(passwordUseCase).authorize(eq("1245"), afterValidationCaptor.capture());
    afterValidationCaptor.getValue().subscribe(
        () -> completableSubject.onError(new NoNetworkException()),
        e -> completableSubject.onComplete()
    );
    inOrder.verify(viewStateObserver).onChanged(any(CodeViewStateInitial.class));
    inOrder.verify(viewStateObserver).onChanged(any(CodeViewStatePending.class));
    inOrder.verify(viewStateObserver).onChanged(any(CodeViewStateNetworkError.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /* Тетсируем навигацию. */

  /**
   * Должен вернуть "перейти к карте" если проверка была успешной.
   */
  @Test
  public void setNavigateToMapToLiveData() {
    // Дано:
    CompletableSubject completableSubject = CompletableSubject.create();
    viewModel.getNavigationLiveData().observeForever(navigateObserver);
    when(passwordUseCase.authorize(anyString(), any(Completable.class)))
        .thenReturn(completableSubject);

    // Действие:
    viewModel.setCode("1   2   4   5");

    // Результат:
    verify(passwordUseCase).authorize(eq("1245"), afterValidationCaptor.capture());
    afterValidationCaptor.getValue().subscribe(
        completableSubject::onComplete,
        e -> completableSubject.onComplete()
    );
    verify(navigateObserver, only()).onChanged(CodeNavigate.ENTER_APP);
  }
}