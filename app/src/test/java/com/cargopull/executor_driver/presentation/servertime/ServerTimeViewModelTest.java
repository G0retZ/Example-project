package com.cargopull.executor_driver.presentation.servertime;

import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.Observer;
import com.cargopull.executor_driver.gateway.DataMappingException;
import com.cargopull.executor_driver.interactor.ServerTimeUseCase;
import com.cargopull.executor_driver.presentation.CommonNavigate;
import com.cargopull.executor_driver.presentation.ViewState;
import io.reactivex.Completable;
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
public class ServerTimeViewModelTest {

  @Rule
  public TestRule rule = new InstantTaskExecutorRule();
  private ServerTimeViewModel currentCostPollingViewModel;
  @Mock
  private Observer<String> navigationObserver;
  @Mock
  private Observer<ViewState<Runnable>> viewStateObserver;

  @Mock
  private ServerTimeUseCase useCase;

  @Before
  public void setUp() {
    RxJavaPlugins.setSingleSchedulerHandler(scheduler -> Schedulers.trampoline());
    RxAndroidPlugins.setInitMainThreadSchedulerHandler(scheduler -> Schedulers.trampoline());
    when(useCase.getServerTime()).thenReturn(Completable.never());
    currentCostPollingViewModel = new ServerTimeViewModelImpl(useCase);
  }

  /* Тетсируем работу с юзкейсом. */

  /**
   * Должен просить у юзкейса получать текущеее времея сервера, даже если уже подписан.
   */
  @Test
  public void askUseCaseToSubscribeToServerTime() {
    // Действие:
    currentCostPollingViewModel.initializeServerTime();
    currentCostPollingViewModel.initializeServerTime();
    currentCostPollingViewModel.initializeServerTime();

    // Результат:
    verify(useCase, times(3)).getServerTime();
    verifyNoMoreInteractions(useCase);
  }

  /**
   * Не должен просить у юзкейса получать текущеее времея сервера снова после завершения.
   */
  @Test
  public void askUseCaseToSubscribeToServerTimeAfterComplete() {
    // Дано:
    when(useCase.getServerTime()).thenReturn(
        Completable.complete(),
        Completable.complete(),
        Completable.never()
    );

    // Действие:
    currentCostPollingViewModel.initializeServerTime();

    // Результат:
    verify(useCase, only()).getServerTime();
  }

  /* Тетсируем переключение состояния. */

  /**
   * Не должен трогать вид по завершению.
   */
  @Test
  public void doNotTouchViewActionsOnComplete() {
    // Дано:
    when(useCase.getServerTime()).thenReturn(
        Completable.complete(),
        Completable.never()
    );

    // Действие:
    currentCostPollingViewModel.getViewStateLiveData().observeForever(viewStateObserver);
    currentCostPollingViewModel.initializeServerTime();

    // Результат:
    verifyZeroInteractions(viewStateObserver);
  }

  /**
   * Не должен трогать вид при ошибке сети.
   */
  @Test
  public void doNotTouchViewActionsOnNetworkError() {
    // Дано:
    when(useCase.getServerTime()).thenReturn(Completable.error(IllegalStateException::new));

    // Действие:
    currentCostPollingViewModel.getViewStateLiveData().observeForever(viewStateObserver);
    currentCostPollingViewModel.initializeServerTime();

    // Результат:
    verifyZeroInteractions(viewStateObserver);
  }

  /**
   * Не должен трогать вид при ошибке маппинга.
   */
  @Test
  public void doNotTouchViewActionsOnMappingError() {
    // Дано:
    when(useCase.getServerTime()).thenReturn(Completable.error(DataMappingException::new));

    // Действие:
    currentCostPollingViewModel.getViewStateLiveData().observeForever(viewStateObserver);
    currentCostPollingViewModel.initializeServerTime();

    // Результат:
    verifyZeroInteractions(viewStateObserver);
  }

  /**
   * Не должен трогать вид при другой ошибке.
   */
  @Test
  public void doNotTouchViewActionsOnOtherError() {
    // Дано:
    when(useCase.getServerTime()).thenReturn(Completable.error(Exception::new));

    // Действие:
    currentCostPollingViewModel.getViewStateLiveData().observeForever(viewStateObserver);
    currentCostPollingViewModel.initializeServerTime();

    // Результат:
    verifyZeroInteractions(viewStateObserver);
  }

  /* Тетсируем навигацию. */

  /**
   * Должен вернуть "перейти к ошибке формата данных от сервера".
   */
  @Test
  public void navigateToServerDataError() {
    // Дано:
    when(useCase.getServerTime()).thenReturn(Completable.error(DataMappingException::new));

    // Действие:
    currentCostPollingViewModel.getNavigationLiveData().observeForever(navigationObserver);
    currentCostPollingViewModel.initializeServerTime();

    // Результат:
    verify(navigationObserver, only()).onChanged(CommonNavigate.SERVER_DATA_ERROR);
  }

  /**
   * Не должен ничего возвращать.
   */
  @Test
  public void doNotNavigateForOtherError() {
    // Дано:
    when(useCase.getServerTime()).thenReturn(Completable.error(InterruptedException::new));

    // Действие:
    currentCostPollingViewModel.getNavigationLiveData().observeForever(navigationObserver);
    currentCostPollingViewModel.initializeServerTime();

    // Результат:
    verifyZeroInteractions(navigationObserver);
  }

  /**
   * Не должен ничего возвращать.
   */
  @Test
  public void doNotNavigateForComplete() {
    // Дано:
    when(useCase.getServerTime()).thenReturn(
        Completable.complete(),
        Completable.never()
    );

    // Действие:
    currentCostPollingViewModel.getNavigationLiveData().observeForever(navigationObserver);
    currentCostPollingViewModel.initializeServerTime();

    // Результат:
    verifyZeroInteractions(navigationObserver);
  }
}