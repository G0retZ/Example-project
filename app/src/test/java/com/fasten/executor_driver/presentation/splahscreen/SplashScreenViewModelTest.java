package com.fasten.executor_driver.presentation.splahscreen;

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
public class SplashScreenViewModelTest {

  @Rule
  public TestRule rule = new InstantTaskExecutorRule();
  private SplashScreenViewModel splashScreenViewModel;
  @Mock
  private Observer<String> navigationObserver;

  @Mock
  private ExecutorStateUseCase executorStateUseCase;

  @Before
  public void setUp() throws Exception {
    RxJavaPlugins.setSingleSchedulerHandler(scheduler -> Schedulers.trampoline());
    RxAndroidPlugins.setInitMainThreadSchedulerHandler(scheduler -> Schedulers.trampoline());
    when(executorStateUseCase.getExecutorStates()).thenReturn(Flowable.never());
    splashScreenViewModel = new SplashScreenViewModelImpl(executorStateUseCase);
  }

  /* Тетсируем работу с юзкейсом. */

  /**
   * Должен попросить у юзкейса статусы исполнителя.
   *
   * @throws Exception error
   */
  @Test
  public void askDataReceiverToSubscribeToLocationUpdates() throws Exception {
    // Действие:
    splashScreenViewModel.initializeApp();

    // Результат:
    verify(executorStateUseCase, only()).getExecutorStates();
  }

  /**
   * Не должен просить у юзкейса загрузить статусы исполнителя, если запрос уже выполняется.
   *
   * @throws Exception error
   */
  @Test
  public void doNotTouchUseCaseBeforeAfterFirstRequestComplete() throws Exception {
    // Действие:
    splashScreenViewModel.initializeApp();
    splashScreenViewModel.initializeApp();
    splashScreenViewModel.initializeApp();

    // Результат:
    verify(executorStateUseCase, only()).getExecutorStates();
  }

  /* Тетсируем навигацию. */

  /**
   * Должен вернуть "перейти к отсутствию сети".
   *
   * @throws Exception error
   */
  @Test
  public void navigateToNoNetwork() throws Exception {
    // Дано:
    when(executorStateUseCase.getExecutorStates())
        .thenReturn(Flowable.error(NoNetworkException::new));

    // Действие:
    splashScreenViewModel.getNavigationLiveData().observeForever(navigationObserver);
    splashScreenViewModel.initializeApp();

    // Результат:
    verify(navigationObserver, only()).onChanged(SplashScreenNavigate.NO_NETWORK);
  }

  /**
   * Должен вернуть "перейти к авторизации".
   *
   * @throws Exception error
   */
  @Test
  public void navigateToAuthorize() throws Exception {
    // Дано:
    when(executorStateUseCase.getExecutorStates())
        .thenReturn(Flowable.error(AuthenticatorException::new));

    // Действие:
    splashScreenViewModel.getNavigationLiveData().observeForever(navigationObserver);
    splashScreenViewModel.initializeApp();

    // Результат:
    verify(navigationObserver, only()).onChanged(SplashScreenNavigate.NO_NETWORK);
  }

  /**
   * Должен вернуть "перейти к карте".
   *
   * @throws Exception error
   */
  @Test
  public void navigateToShiftClosed() throws Exception {
    // Дано:
    when(executorStateUseCase.getExecutorStates())
        .thenReturn(Flowable.just(ExecutorState.SHIFT_CLOSED));

    // Действие:
    splashScreenViewModel.getNavigationLiveData().observeForever(navigationObserver);
    splashScreenViewModel.initializeApp();

    // Результат:
    verify(navigationObserver, only()).onChanged(SplashScreenNavigate.MAP_SHIFT_CLOSED);
  }

  /**
   * Должен вернуть "перейти к получению заказа".
   *
   * @throws Exception error
   */
  @Test
  public void navigateToShiftOpened() throws Exception {
    // Дано:
    when(executorStateUseCase.getExecutorStates())
        .thenReturn(Flowable.just(ExecutorState.SHIFT_OPENED));

    // Действие:
    splashScreenViewModel.getNavigationLiveData().observeForever(navigationObserver);
    splashScreenViewModel.initializeApp();

    // Результат:
    verify(navigationObserver, only()).onChanged(SplashScreenNavigate.MAP_SHIFT_OPENED);
  }

  /**
   * Должен вернуть "перейти к получению заказа".
   *
   * @throws Exception error
   */
  @Test
  public void navigateToOnline() throws Exception {
    // Дано:
    when(executorStateUseCase.getExecutorStates()).thenReturn(Flowable.just(ExecutorState.ONLINE));

    // Действие:
    splashScreenViewModel.getNavigationLiveData().observeForever(navigationObserver);
    splashScreenViewModel.initializeApp();

    // Результат:
    verify(navigationObserver, only()).onChanged(SplashScreenNavigate.MAP_ONLINE);
  }
}