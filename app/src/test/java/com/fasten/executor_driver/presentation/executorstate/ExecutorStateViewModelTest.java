package com.fasten.executor_driver.presentation.executorstate;

import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.accounts.AuthenticatorException;
import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.Observer;
import com.fasten.executor_driver.backend.web.NoNetworkException;
import com.fasten.executor_driver.entity.ExecutorState;
import com.fasten.executor_driver.interactor.ExecutorStateUseCase;
import io.reactivex.Flowable;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ExecutorStateViewModelTest {

  @Rule
  public TestRule rule = new InstantTaskExecutorRule();
  private ExecutorStateViewModel executorStateViewModel;
  @Mock
  private Observer<String> navigationObserver;

  @Mock
  private ExecutorStateUseCase executorStateUseCase;

  @Before
  public void setUp() {
    RxJavaPlugins.setSingleSchedulerHandler(scheduler -> Schedulers.trampoline());
    RxAndroidPlugins.setInitMainThreadSchedulerHandler(scheduler -> Schedulers.trampoline());
    when(executorStateUseCase.getExecutorStates(anyBoolean())).thenReturn(Flowable.never());
    executorStateViewModel = new ExecutorStateViewModelImpl(executorStateUseCase);
  }

  /* Тетсируем работу с юзкейсом. */

  /**
   * Должен попросить у юзкейса статусы исполнителя без сброса кеша.
   */
  @Test
  public void askDataReceiverToSubscribeToLocationUpdates() {
    // Действие:
    executorStateViewModel.initializeExecutorState(false);

    // Результат:
    verify(executorStateUseCase, only()).getExecutorStates(false);
  }

  /**
   * Должен попросить у юзкейса статусы исполнителя со сбросом кеша.
   */
  @Test
  public void askDataReceiverToSubscribeToLocationUpdatesWithCacheReset() {
    // Действие:
    executorStateViewModel.initializeExecutorState(true);

    // Результат:
    verify(executorStateUseCase, only()).getExecutorStates(true);
  }

  /**
   * Не должен просить у юзкейса загрузить статусы исполнителя, если запрос уже выполняется.
   */
  @Test
  public void doNotTouchUseCaseBeforeAfterFirstRequestComplete() {
    // Действие:
    executorStateViewModel.initializeExecutorState(false);
    executorStateViewModel.initializeExecutorState(true);
    executorStateViewModel.initializeExecutorState(false);

    // Результат:
    verify(executorStateUseCase, only()).getExecutorStates(false);
  }

  /* Тетсируем навигацию. */

  /**
   * Должен вернуть "перейти к отсутствию сети".
   */
  @Test
  public void navigateToNoNetwork() {
    // Дано:
    when(executorStateUseCase.getExecutorStates(anyBoolean()))
        .thenReturn(Flowable.error(NoNetworkException::new));

    // Действие:
    executorStateViewModel.getNavigationLiveData().observeForever(navigationObserver);
    executorStateViewModel.initializeExecutorState(false);

    // Результат:
    verify(navigationObserver, only()).onChanged(ExecutorStateNavigate.NO_NETWORK);
  }

  /**
   * Должен вернуть "перейти к авторизации".
   */
  @Test
  public void navigateToAuthorize() {
    // Дано:
    when(executorStateUseCase.getExecutorStates(anyBoolean()))
        .thenReturn(Flowable.error(AuthenticatorException::new));

    // Действие:
    executorStateViewModel.getNavigationLiveData().observeForever(navigationObserver);
    executorStateViewModel.initializeExecutorState(true);

    // Результат:
    verify(navigationObserver, only()).onChanged(ExecutorStateNavigate.NO_NETWORK);
  }

  /**
   * Должен вернуть "перейти к карте".
   */
  @Test
  public void navigateToShiftClosed() {
    // Дано:
    when(executorStateUseCase.getExecutorStates(anyBoolean()))
        .thenReturn(Flowable.just(ExecutorState.SHIFT_CLOSED));

    // Действие:
    executorStateViewModel.getNavigationLiveData().observeForever(navigationObserver);
    executorStateViewModel.initializeExecutorState(false);

    // Результат:
    verify(navigationObserver, only()).onChanged(ExecutorStateNavigate.MAP_SHIFT_CLOSED);
  }

  /**
   * Должен вернуть "перейти к получению заказа".
   */
  @Test
  public void navigateToShiftOpened() {
    // Дано:
    when(executorStateUseCase.getExecutorStates(anyBoolean()))
        .thenReturn(Flowable.just(ExecutorState.SHIFT_OPENED));

    // Действие:
    executorStateViewModel.getNavigationLiveData().observeForever(navigationObserver);
    executorStateViewModel.initializeExecutorState(true);

    // Результат:
    verify(navigationObserver, only()).onChanged(ExecutorStateNavigate.MAP_SHIFT_OPENED);
  }

  /**
   * Должен вернуть "перейти к получению заказа".
   */
  @Test
  public void navigateToOnline() {
    // Дано:
    when(executorStateUseCase.getExecutorStates(anyBoolean()))
        .thenReturn(Flowable.just(ExecutorState.ONLINE));

    // Действие:
    executorStateViewModel.getNavigationLiveData().observeForever(navigationObserver);
    executorStateViewModel.initializeExecutorState(false);

    // Результат:
    verify(navigationObserver, only()).onChanged(ExecutorStateNavigate.MAP_ONLINE);
  }

  /**
   * Должен вернуть "перейти к подтверждению заказа".
   */
  @Test
  public void navigateToOfferConfirmation() {
    // Дано:
    when(executorStateUseCase.getExecutorStates(anyBoolean()))
        .thenReturn(Flowable.just(ExecutorState.ORDER_CONFIRMATION));

    // Действие:
    executorStateViewModel.getNavigationLiveData().observeForever(navigationObserver);
    executorStateViewModel.initializeExecutorState(true);

    // Результат:
    verify(navigationObserver, only()).onChanged(ExecutorStateNavigate.OFFER_CONFIRMATION);
  }

  /**
   * Должен вернуть "перейти к движению к точке погрузки".
   */
  @Test
  public void navigateToApproachingLoadPoint() {
    // Дано:
    when(executorStateUseCase.getExecutorStates(anyBoolean()))
        .thenReturn(Flowable.just(ExecutorState.IN_PROGRESS));

    // Действие:
    executorStateViewModel.getNavigationLiveData().observeForever(navigationObserver);
    executorStateViewModel.initializeExecutorState(false);

    // Результат:
    verify(navigationObserver, only()).onChanged(ExecutorStateNavigate.APPROACHING_LOAD_POINT);
  }
}