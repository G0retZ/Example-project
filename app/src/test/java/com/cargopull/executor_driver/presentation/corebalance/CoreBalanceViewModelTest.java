package com.cargopull.executor_driver.presentation.corebalance;

import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import android.accounts.AuthenticatorException;
import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.Observer;
import com.cargopull.executor_driver.ViewModelThreadTestRule;
import com.cargopull.executor_driver.backend.web.NoNetworkException;
import com.cargopull.executor_driver.entity.ExecutorBalance;
import com.cargopull.executor_driver.entity.ExecutorState;
import com.cargopull.executor_driver.gateway.DataMappingException;
import com.cargopull.executor_driver.interactor.ExecutorBalanceUseCase;
import com.cargopull.executor_driver.presentation.CommonNavigate;
import com.cargopull.executor_driver.presentation.ViewState;
import io.reactivex.Flowable;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CoreBalanceViewModelTest {

  @ClassRule
  public static final ViewModelThreadTestRule classRule = new ViewModelThreadTestRule();
  @Rule
  public TestRule rule = new InstantTaskExecutorRule();
  private CoreBalanceViewModel viewModel;
  @Mock
  private Observer<String> navigationObserver;
  @Mock
  private Observer<ViewState<Runnable>> viewStateObserver;
  @Mock
  private ExecutorBalanceUseCase executorBalanceUseCase;
  @Mock
  private ExecutorBalance executorBalance;

  @Before
  public void setUp() {
    ExecutorState.ONLINE.setData(null);
    when(executorBalanceUseCase.getExecutorBalance(anyBoolean())).thenReturn(Flowable.never());
    viewModel = new CoreBalanceViewModelImpl(executorBalanceUseCase);
  }

  /* Тетсируем работу с юзкейсом. */

  /**
   * Должен попросить у юзкейса баланс исполнителя со сбросом кеша.
   */
  @Test
  public void askUseCaseToSubscribeToBalanceUpdatesWithCacheReset() {
    // Действие:
    viewModel.initializeExecutorBalance();

    // Результат:
    verify(executorBalanceUseCase, only()).getExecutorBalance(true);
  }

  /**
   * Должен просить у юзкейса загрузить баланс исполнителя, со сбросом кеша, даже если уже
   * подписан.
   */
  @Test
  public void doNotTouchUseCaseBeforeFirstRequestComplete() {
    // Действие:
    viewModel.initializeExecutorBalance();
    viewModel.initializeExecutorBalance();
    viewModel.initializeExecutorBalance();

    // Результат:
    verify(executorBalanceUseCase, times(3)).getExecutorBalance(true);
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
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);
    viewModel.initializeExecutorBalance();

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
    viewModel.getNavigationLiveData().observeForever(navigationObserver);
    viewModel.initializeExecutorBalance();

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
    viewModel.getNavigationLiveData().observeForever(navigationObserver);
    viewModel.initializeExecutorBalance();

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
    viewModel.getNavigationLiveData().observeForever(navigationObserver);
    viewModel.initializeExecutorBalance();

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
    viewModel.getNavigationLiveData().observeForever(navigationObserver);
    viewModel.initializeExecutorBalance();

    // Результат:
    verify(navigationObserver, only()).onChanged(CommonNavigate.SERVER_DATA_ERROR);
  }
}