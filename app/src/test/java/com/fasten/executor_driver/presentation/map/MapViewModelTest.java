package com.fasten.executor_driver.presentation.map;

import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.Observer;
import com.fasten.executor_driver.interactor.map.HeatMapUseCase;
import com.fasten.executor_driver.presentation.ViewState;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
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
public class MapViewModelTest {

  private MapViewModel mapViewModel;

  @Rule
  public TestRule rule = new InstantTaskExecutorRule();

  @Mock
  private Observer<ViewState<MapViewActions>> viewStateObserver;

  @Mock
  private HeatMapUseCase heatMapUseCase;

  @Before
  public void setUp() throws Exception {
    RxJavaPlugins.setSingleSchedulerHandler(scheduler -> Schedulers.trampoline());
    RxAndroidPlugins.setInitMainThreadSchedulerHandler(scheduler -> Schedulers.trampoline());
    when(heatMapUseCase.loadHeatMap()).thenReturn(Flowable.never());
    mapViewModel = new MapViewModelImpl(heatMapUseCase);
  }

  /* Тетсируем работу с юзкейсом тепловой карты. */

  /**
   * Должен попросить у юзкейса подписку на обновления тепловой карты.
   *
   * @throws Exception error.
   */
  @Test
  public void askUseCaseToSubscribeToHeatMapUpdates() throws Exception {
    // Действие:
    mapViewModel.getViewStateLiveData();

    // Результат:
    verify(heatMapUseCase, only()).loadHeatMap();
  }

  /**
   * Не должен просить у юзкейс подписку (после поворотов), если подписка уже была.
   *
   * @throws Exception error.
   */
  @Test
  public void DoNotTouchUseCaseAfterFirstSubscription() throws Exception {
    // Действие:
    mapViewModel.getViewStateLiveData();
    mapViewModel.getViewStateLiveData();
    mapViewModel.getViewStateLiveData();

    // Результат:
    verify(heatMapUseCase, only()).loadHeatMap();
  }

  /* Тетсируем переключение состояний */

  /**
   * Должен вернуть начально состояние вида без тепловой карты.
   *
   * @throws Exception error
   */
  @Test
  public void setViewStateWithNullToLiveData() throws Exception {
    // Действие:
    mapViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Результат:
    verify(viewStateObserver, only()).onChanged(new MapViewState(null));
  }

  /**
   * Должен вернуть состояния с обновлениями тепловой карты.
   *
   * @throws Exception error
   */
  @Test
  public void setViewStatesWithDataToLiveData() throws Exception {
    // Дано:
    PublishSubject<String> publishSubject = PublishSubject.create();
    when(heatMapUseCase.loadHeatMap())
        .thenReturn(publishSubject.toFlowable(BackpressureStrategy.BUFFER));
    InOrder inOrder = Mockito.inOrder(viewStateObserver);

    // Действие:
    mapViewModel.getViewStateLiveData().observeForever(viewStateObserver);
    publishSubject.onNext("heat map 1");
    publishSubject.onNext("heat map 2");
    publishSubject.onNext("heat map 3");
    publishSubject.onNext("heat map 4");

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(new MapViewState(null));
    inOrder.verify(viewStateObserver).onChanged(new MapViewState("heat map 1"));
    inOrder.verify(viewStateObserver).onChanged(new MapViewState("heat map 2"));
    inOrder.verify(viewStateObserver).onChanged(new MapViewState("heat map 3"));
    inOrder.verify(viewStateObserver).onChanged(new MapViewState("heat map 4"));
    verifyNoMoreInteractions(viewStateObserver);
  }
}