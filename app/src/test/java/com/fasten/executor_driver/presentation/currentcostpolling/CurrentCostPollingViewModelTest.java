package com.fasten.executor_driver.presentation.currentcostpolling;

import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.Observer;
import com.fasten.executor_driver.gateway.DataMappingException;
import com.fasten.executor_driver.interactor.CurrentCostPollingUseCase;
import com.fasten.executor_driver.presentation.CommonNavigate;
import com.fasten.executor_driver.presentation.ViewState;
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
public class CurrentCostPollingViewModelTest {

  @Rule
  public TestRule rule = new InstantTaskExecutorRule();
  private CurrentCostPollingViewModel currentCostPollingViewModel;
  @Mock
  private Observer<String> navigationObserver;
  @Mock
  private Observer<ViewState<CurrentCostPollingViewActions>> viewStateObserver;

  @Mock
  private CurrentCostPollingUseCase useCase;

  @Before
  public void setUp() {
    RxJavaPlugins.setSingleSchedulerHandler(scheduler -> Schedulers.trampoline());
    RxAndroidPlugins.setInitMainThreadSchedulerHandler(scheduler -> Schedulers.trampoline());
    when(useCase.listenForPolling()).thenReturn(Completable.never());
    currentCostPollingViewModel = new CurrentCostPollingViewModelImpl(useCase);
  }

  /* Тетсируем работу с юзкейсом. */

  /**
   * Должен просить у юзкейса начать поллинг, даже если уже подписан.
   */
  @Test
  public void askUseCaseToSubscribeToCurrentCostPolling() {
    // Действие:
    currentCostPollingViewModel.initializeCurrentCostPolling();
    currentCostPollingViewModel.initializeCurrentCostPolling();
    currentCostPollingViewModel.initializeCurrentCostPolling();

    // Результат:
    verify(useCase, times(3)).listenForPolling();
    verifyNoMoreInteractions(useCase);
  }

  /**
   * Должен просить у юзкейса начать поллинг снова после завершения.
   */
  @Test
  public void askUseCaseToSubscribeToCurrentCostPollingAfterComplete() {
    // Дано:
    when(useCase.listenForPolling()).thenReturn(
        Completable.complete(),
        Completable.complete(),
        Completable.never()
    );

    // Действие:
    currentCostPollingViewModel.initializeCurrentCostPolling();

    // Результат:
    verify(useCase, times(3)).listenForPolling();
    verifyNoMoreInteractions(useCase);
  }

  /* Тетсируем переключение состояния. */

  /**
   * Не должен трогать вид по завершению.
   */
  @Test
  public void doNotTouchViewActionsOnComplete() {
    // Дано:
    when(useCase.listenForPolling()).thenReturn(
        Completable.complete(),
        Completable.never()
    );

    // Действие:
    currentCostPollingViewModel.getViewStateLiveData().observeForever(viewStateObserver);
    currentCostPollingViewModel.initializeCurrentCostPolling();

    // Результат:
    verifyZeroInteractions(viewStateObserver);
  }

  /**
   * Не должен трогать вид при ошибке сети.
   */
  @Test
  public void doNotTouchViewActionsOnNetworkError() {
    // Дано:
    when(useCase.listenForPolling()).thenReturn(Completable.error(IllegalStateException::new));

    // Действие:
    currentCostPollingViewModel.getViewStateLiveData().observeForever(viewStateObserver);
    currentCostPollingViewModel.initializeCurrentCostPolling();

    // Результат:
    verifyZeroInteractions(viewStateObserver);
  }

  /**
   * Не должен трогать вид при ошибке маппинга.
   */
  @Test
  public void doNotTouchViewActionsOnMappingError() {
    // Дано:
    when(useCase.listenForPolling()).thenReturn(Completable.error(DataMappingException::new));

    // Действие:
    currentCostPollingViewModel.getViewStateLiveData().observeForever(viewStateObserver);
    currentCostPollingViewModel.initializeCurrentCostPolling();

    // Результат:
    verifyZeroInteractions(viewStateObserver);
  }

  /**
   * Не должен трогать вид при другой ошибке.
   */
  @Test
  public void doNotTouchViewActionsOnOtherError() {
    // Дано:
    when(useCase.listenForPolling()).thenReturn(Completable.error(Exception::new));

    // Действие:
    currentCostPollingViewModel.getViewStateLiveData().observeForever(viewStateObserver);
    currentCostPollingViewModel.initializeCurrentCostPolling();

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
    when(useCase.listenForPolling()).thenReturn(Completable.error(DataMappingException::new));

    // Действие:
    currentCostPollingViewModel.getNavigationLiveData().observeForever(navigationObserver);
    currentCostPollingViewModel.initializeCurrentCostPolling();

    // Результат:
    verify(navigationObserver, only()).onChanged(CommonNavigate.SERVER_DATA_ERROR);
  }

  /**
   * Не должен ничего возвращать.
   */
  @Test
  public void doNotNavigateForOtherError() {
    // Дано:
    when(useCase.listenForPolling()).thenReturn(Completable.error(InterruptedException::new));

    // Действие:
    currentCostPollingViewModel.getNavigationLiveData().observeForever(navigationObserver);
    currentCostPollingViewModel.initializeCurrentCostPolling();

    // Результат:
    verifyZeroInteractions(navigationObserver);
  }

  /**
   * Не должен ничего возвращать.
   */
  @Test
  public void doNotNavigateForComplete() {
    // Дано:
    when(useCase.listenForPolling()).thenReturn(
        Completable.complete(),
        Completable.never()
    );

    // Действие:
    currentCostPollingViewModel.getNavigationLiveData().observeForever(navigationObserver);
    currentCostPollingViewModel.initializeCurrentCostPolling();

    // Результат:
    verifyZeroInteractions(navigationObserver);
  }
}