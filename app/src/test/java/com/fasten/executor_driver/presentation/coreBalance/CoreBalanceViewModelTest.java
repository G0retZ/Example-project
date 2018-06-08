package com.fasten.executor_driver.presentation.coreBalance;

import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import android.accounts.AuthenticatorException;
import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.Observer;
import com.fasten.executor_driver.backend.web.NoNetworkException;
import com.fasten.executor_driver.entity.ExecutorBalance;
import com.fasten.executor_driver.entity.ExecutorState;
import com.fasten.executor_driver.gateway.DataMappingException;
import com.fasten.executor_driver.interactor.ExecutorBalanceUseCase;
import com.fasten.executor_driver.presentation.ViewState;
import io.reactivex.Flowable;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CoreBalanceViewModelTest {

  @Rule
  public TestRule rule = new InstantTaskExecutorRule();
  private CoreBalanceViewModel coreBalanceViewModel;
  @Mock
  private Observer<String> navigationObserver;
  @Mock
  private Observer<ViewState<CoreBalanceViewActions>> viewStateObserver;
  @Mock
  private ExecutorBalanceUseCase executorBalanceUseCase;
  @Mock
  private ExecutorBalance executorBalance;

  @Before
  public void setUp() {
    ExecutorState.ONLINE.setData(null);
    RxJavaPlugins.setSingleSchedulerHandler(scheduler -> Schedulers.trampoline());
    RxAndroidPlugins.setInitMainThreadSchedulerHandler(scheduler -> Schedulers.trampoline());
    when(executorBalanceUseCase.getExecutorBalance(anyBoolean())).thenReturn(Flowable.never());
    coreBalanceViewModel = new CoreBalanceViewModelImpl(executorBalanceUseCase);
  }

  /* Тетсируем работу с юзкейсом. */

  /**
   * Должен попросить у юзкейса баланс исполнителя без сброса кеша.
   */
  @Test
  public void askUseCaseToSubscribeToBalanceUpdates() {
    // Действие:
    coreBalanceViewModel.initializeExecutorBalance(false);

    // Результат:
    verify(executorBalanceUseCase, only()).getExecutorBalance(false);
  }

  /**
   * Должен попросить у юзкейса баланс исполнителя со сбросом кеша.
   */
  @Test
  public void askUseCaseToSubscribeToBalanceUpdatesWithCacheReset() {
    // Действие:
    coreBalanceViewModel.initializeExecutorBalance(true);

    // Результат:
    verify(executorBalanceUseCase, only()).getExecutorBalance(true);
  }

  /**
   * Должен просить у юзкейса загрузить баланс исполнителя, без сброса кеша, даже если уже
   * подписан.
   */
  @Test
  public void doNotTouchUseCaseBeforeFirstRequestComplete() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(executorBalanceUseCase);

    // Действие:
    coreBalanceViewModel.initializeExecutorBalance(false);
    coreBalanceViewModel.initializeExecutorBalance(true);
    coreBalanceViewModel.initializeExecutorBalance(false);

    // Результат:
    inOrder.verify(executorBalanceUseCase).getExecutorBalance(false);
    inOrder.verify(executorBalanceUseCase).getExecutorBalance(true);
    inOrder.verify(executorBalanceUseCase).getExecutorBalance(false);
    verifyNoMoreInteractions(executorBalanceUseCase);
  }

  /* Тетсируем сообщение. */

  /**
   * Не должен ничего показывать.
   */
  @Test
  public void showNothing() {
    // Дано:
    when(executorBalanceUseCase.getExecutorBalance(anyBoolean()))
        .thenReturn(Flowable.just(executorBalance));

    // Действие:
    coreBalanceViewModel.getViewStateLiveData().observeForever(viewStateObserver);
    coreBalanceViewModel.initializeExecutorBalance(false);

    // Результат:
    verifyZeroInteractions(viewStateObserver);
  }

  /* Тетсируем навигацию. */

  /**
   * Не должен ничего вернуть.
   */
  @Test
  public void navigateToNothingForError() {
    // Дано:
    when(executorBalanceUseCase.getExecutorBalance(anyBoolean()))
        .thenReturn(Flowable.error(NoNetworkException::new));

    // Действие:
    coreBalanceViewModel.getNavigationLiveData().observeForever(navigationObserver);
    coreBalanceViewModel.initializeExecutorBalance(false);

    // Результат:
    verifyZeroInteractions(navigationObserver);
  }

  /**
   * Не должен ничего вернуть.
   */
  @Test
  public void navigateToNothingForAuthorize() {
    // Дано:
    when(executorBalanceUseCase.getExecutorBalance(anyBoolean()))
        .thenReturn(Flowable.error(AuthenticatorException::new));

    // Действие:
    coreBalanceViewModel.getNavigationLiveData().observeForever(navigationObserver);
    coreBalanceViewModel.initializeExecutorBalance(true);

    // Результат:
    verifyZeroInteractions(navigationObserver);
  }

  /**
   * Не должен ничего вернуть.
   */
  @Test
  public void navigateToNothingForData() {
    // Дано:
    when(executorBalanceUseCase.getExecutorBalance(anyBoolean()))
        .thenReturn(Flowable.just(executorBalance));

    // Действие:
    coreBalanceViewModel.getNavigationLiveData().observeForever(navigationObserver);
    coreBalanceViewModel.initializeExecutorBalance(false);

    // Результат:
    verifyZeroInteractions(navigationObserver);
  }

  /**
   * Должен вернуть ошибку данных сервера.
   */
  @Test
  public void navigateToServerDataError() {
    // Дано:
    when(executorBalanceUseCase.getExecutorBalance(anyBoolean()))
        .thenReturn(Flowable.error(DataMappingException::new));

    // Действие:
    coreBalanceViewModel.getNavigationLiveData().observeForever(navigationObserver);
    coreBalanceViewModel.initializeExecutorBalance(true);

    // Результат:
    verify(navigationObserver, only()).onChanged(CoreBalanceNavigate.SERVER_DATA_ERROR);
  }
}