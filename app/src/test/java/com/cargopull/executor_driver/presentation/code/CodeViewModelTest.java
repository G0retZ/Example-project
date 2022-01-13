package com.cargopull.executor_driver.presentation.code;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;

import com.cargopull.executor_driver.ViewModelThreadTestRule;
import com.cargopull.executor_driver.backend.analytics.EventLogger;
import com.cargopull.executor_driver.backend.web.NoNetworkException;
import com.cargopull.executor_driver.entity.ValidationException;
import com.cargopull.executor_driver.interactor.auth.PasswordUseCase;
import com.cargopull.executor_driver.presentation.ViewState;
import com.cargopull.executor_driver.utils.TimeUtils;

import org.junit.Before;
import org.junit.ClassRule;
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

import java.util.HashMap;

import io.reactivex.Completable;
import io.reactivex.subjects.CompletableSubject;

@RunWith(MockitoJUnitRunner.class)
public class CodeViewModelTest {

  @ClassRule
  public static final ViewModelThreadTestRule classRule = new ViewModelThreadTestRule();
  @Rule
  public TestRule rule = new InstantTaskExecutorRule();
  private CodeViewModel viewModel;
  @Mock
  private PasswordUseCase passwordUseCase;
  @Mock
  private TimeUtils timeUtils;
  @Mock
  private EventLogger eventLogger;

  @Mock
  private Observer<ViewState<CodeViewActions>> viewStateObserver;

  @Mock
  private Observer<String> navigateObserver;

  @Captor
  private ArgumentCaptor<Completable> afterValidationCaptor;

  @Before
  public void setUp() {
    when(passwordUseCase.authorize(anyString(), any(Completable.class)))
        .thenReturn(Completable.never());
    viewModel = new CodeViewModelImpl(passwordUseCase, timeUtils, eventLogger);
  }

  /* Тетсируем работу с юзкейсом кода. */

  /**
   * Не должен просить юзкейс авторизироваться, если предыдущий запрос еще не завершился.
   */
  @Test
  public void DoNotAskPasswordUseCaseToAuthorize() {
    // Action:
    viewModel.setCode("1   2   ");
    viewModel.setCode("1   2   3   ");
    viewModel.setCode("1   2   3   4");

    // Effect:
    verify(passwordUseCase, only()).authorize(eq("12"), afterValidationCaptor.capture());
  }

  /**
   * Должен попросить юзкейс авторизироваться.
   */
  @Test
  public void askPasswordUseCaseToAuthorize() {
    // Given:
    when(passwordUseCase.authorize(anyString(), any(Completable.class)))
        .thenReturn(Completable.error(new ValidationException()));

    // Action:
    viewModel.setCode("1   2   ");
    viewModel.setCode("1   2   3   ");
    viewModel.setCode("1   2   3   4");

    // Effect:
    verify(passwordUseCase).authorize(eq("12"), afterValidationCaptor.capture());
    verify(passwordUseCase).authorize(eq("123"), afterValidationCaptor.capture());
    verify(passwordUseCase).authorize(eq("1234"), afterValidationCaptor.capture());
    verifyNoMoreInteractions(passwordUseCase);
  }

  /* Тетсируем работу со временем. */

  /**
   * Должен запросить текущий таймстамп изначально.
   */
  @Test
  public void askForCurrentTimeStampInitially() {
    // Effect:
    verify(timeUtils, only()).currentTimeMillis();
  }

  /**
   * Должен запросить текущий таймстамп повторно при успешном вводе кода.
   */
  @Test
  public void askForCurrentTimeStampAgainIfLoggedIn() {
    // Given:
    when(passwordUseCase.authorize(anyString(), any(Completable.class)))
        .thenReturn(Completable.complete());

    // Action:
    viewModel.setCode("1   2   ");
    viewModel.setCode("1   2   3   ");
    viewModel.setCode("1   2   3   4");

    // Effect:
    verify(timeUtils, times(4)).currentTimeMillis();
    verifyNoMoreInteractions(timeUtils);
  }

  /**
   * Не должен запрашивать текущий таймстамп повторно при ошибках.
   */
  @Test
  public void doNotAskForCurrentTimeStampOnErrors() {
    // Given:
    when(passwordUseCase.authorize(anyString(), any(Completable.class)))
        .thenReturn(Completable.error(new Exception()));

    // Action:
    viewModel.setCode("1   2   ");
    viewModel.setCode("1   2   3   ");
    viewModel.setCode("1   2   3   4");

    // Effect:
    verify(timeUtils, only()).currentTimeMillis();
  }

  /* Тетсируем работу с логгером событий. */

  /**
   * Не должен трогать логгер изначально.
   */
  @Test
  public void doNotTouchEventLoggerInitially() {
    // Effect:
    verifyNoInteractions(eventLogger);
  }

  /**
   * Должен передать данные для лога при успешном вводе кода.
   */
  @Test
  public void askLoggerToLogEventIfLoggedIn() {
    // Given:
    InOrder inOrder = Mockito.inOrder(eventLogger);
    when(timeUtils.currentTimeMillis()).thenReturn(12345L, 67890L, 1234567890L);
    when(passwordUseCase.authorize(anyString(), any(Completable.class)))
        .thenReturn(Completable.complete());

    // Action:
    viewModel.setCode("1   2   ");
    viewModel.setCode("1   2   3   ");
    viewModel.setCode("1   2   3   4");

    // Effect:
    HashMap<String, String> hashMap = new HashMap<>();
    hashMap.put("login_delay", "12345");
    inOrder.verify(eventLogger).reportEvent("executor_login", hashMap);
    hashMap.clear();
    hashMap.put("login_delay", "67890");
    inOrder.verify(eventLogger).reportEvent("executor_login", hashMap);
    hashMap.clear();
    hashMap.put("login_delay", "1234567890");
    inOrder.verify(eventLogger).reportEvent("executor_login", hashMap);
    verifyNoMoreInteractions(eventLogger);
  }

  /**
   * Не должен передать данные для лога при ошибках.
   */
  @Test
  public void doNotTouchLoggerOnErrors() {
    // Given:
    when(passwordUseCase.authorize(anyString(), any(Completable.class)))
        .thenReturn(Completable.error(new Exception()));

    // Action:
    viewModel.setCode("1   2   ");
    viewModel.setCode("1   2   3   ");
    viewModel.setCode("1   2   3   4");

    // Effect:
    verifyNoInteractions(eventLogger);
  }

  /* Тетсируем переключение состояний. */

  /**
   * Должен вернуть состояние вида "Начало" изначально.
   */
  @Test
  public void setInitialViewStateToLiveData() {
    // Given:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);

    // Action:
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Effect:
    inOrder.verify(viewStateObserver).onChanged(any(CodeViewStateEmpty.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен менять состояние вида, если код не валидируется.
   */
  @Test
  public void setNewViewStateToLiveData() {
    // Given:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);
    when(passwordUseCase.authorize(anyString(), any(Completable.class)))
        .thenReturn(Completable.error(new ValidationException()));

    // Action:
    viewModel.setCode("");
    viewModel.setCode("1   2   ");
    viewModel.setCode("1   2   3   ");
    viewModel.setCode("1   2   3   4");
    viewModel.setCode("");

    // Effect:
    inOrder.verify(viewStateObserver, times(2)).onChanged(any(CodeViewStateEmpty.class));
    inOrder.verify(viewStateObserver, times(3)).onChanged(any(CodeViewStateActive.class));
    inOrder.verify(viewStateObserver).onChanged(any(CodeViewStateEmpty.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида "В процессе" после валидации.
   */
  @Test
  public void setPendingViewStateToLiveDataAfterValidationSuccess() {
    // Given:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);
    when(passwordUseCase.authorize(anyString(), any(Completable.class)))
        .thenReturn(Completable.error(new ValidationException()));
    when(passwordUseCase.authorize(eq("12457"), any(Completable.class)))
        .thenReturn(Completable.never());

    // Action:
    viewModel.setCode("");
    viewModel.setCode("1   2   ");
    viewModel.setCode("1   2   4   5   ");
    viewModel.setCode("1   2   4   5   7   ");

    // Effect:
    verify(passwordUseCase).authorize(eq("12457"), afterValidationCaptor.capture());
    afterValidationCaptor.getValue().test().isDisposed();
    inOrder.verify(viewStateObserver, times(2)).onChanged(any(CodeViewStateEmpty.class));
    inOrder.verify(viewStateObserver, times(2)).onChanged(any(CodeViewStateActive.class));
    inOrder.verify(viewStateObserver).onChanged(any(CodeViewStatePending.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида "Ошибка кода".
   */
  @Test
  public void setErrorViewStateToLiveData() {
    // Given:
    CompletableSubject completableSubject = CompletableSubject.create();
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);
    when(passwordUseCase.authorize(anyString(), any(Completable.class)))
        .thenReturn(completableSubject);

    // Action:
    viewModel.setCode("1   2   4   5");

    // Effect:
    verify(passwordUseCase).authorize(eq("1245"), afterValidationCaptor.capture());
    afterValidationCaptor.getValue().subscribe(
        () -> completableSubject.onError(new IllegalArgumentException()),
        e -> completableSubject.onComplete()
    ).isDisposed();
    inOrder.verify(viewStateObserver).onChanged(any(CodeViewStateEmpty.class));
    inOrder.verify(viewStateObserver).onChanged(any(CodeViewStatePending.class));
    inOrder.verify(viewStateObserver).onChanged(any(CodeViewStateError.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть вернуть начальное состояние вида после "Ошибка кода".
   */
  @Test
  public void setInitialViewStateToLiveDataAfterError() {
    // Given:
    CompletableSubject completableSubject = CompletableSubject.create();
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);
    when(passwordUseCase.authorize(anyString(), any(Completable.class)))
        .thenReturn(completableSubject);

    // Action:
    viewModel.setCode("1   2   4   5");
    verify(passwordUseCase).authorize(eq("1245"), afterValidationCaptor.capture());
    afterValidationCaptor.getValue().subscribe(
        () -> completableSubject.onError(new IllegalArgumentException()),
        e -> completableSubject.onComplete()
    ).isDisposed();
    when(passwordUseCase.authorize(anyString(), any(Completable.class)))
        .thenReturn(Completable.error(new ValidationException()));
    viewModel.setCode("1   2   4   ");

    // Effect:
    inOrder.verify(viewStateObserver).onChanged(any(CodeViewStateEmpty.class));
    inOrder.verify(viewStateObserver).onChanged(any(CodeViewStatePending.class));
    inOrder.verify(viewStateObserver).onChanged(any(CodeViewStateError.class));
    inOrder.verify(viewStateObserver).onChanged(any(CodeViewStateActive.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида "Ошибка сети".
   */
  @Test
  public void setNetworkErrorViewStateToLiveData() {
    // Given:
    CompletableSubject completableSubject = CompletableSubject.create();
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);
    when(passwordUseCase.authorize(anyString(), any(Completable.class)))
        .thenReturn(completableSubject);

    // Action:
    viewModel.setCode("1   2   4   5");

    // Effect:
    verify(passwordUseCase).authorize(eq("1245"), afterValidationCaptor.capture());
    afterValidationCaptor.getValue().subscribe(
        () -> completableSubject.onError(new NoNetworkException()),
        e -> completableSubject.onComplete()
    ).isDisposed();
    inOrder.verify(viewStateObserver).onChanged(any(CodeViewStateEmpty.class));
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
    // Given:
    CompletableSubject completableSubject = CompletableSubject.create();
    viewModel.getNavigationLiveData().observeForever(navigateObserver);
    when(passwordUseCase.authorize(anyString(), any(Completable.class)))
        .thenReturn(completableSubject);

    // Action:
    viewModel.setCode("1   2   4   5");

    // Effect:
    verify(passwordUseCase).authorize(eq("1245"), afterValidationCaptor.capture());
    afterValidationCaptor.getValue().subscribe(
        completableSubject::onComplete,
        e -> completableSubject.onComplete()
    ).isDisposed();
    verify(navigateObserver, only()).onChanged(CodeNavigate.ENTER_APP);
  }
}