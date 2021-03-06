package com.cargopull.executor_driver.presentation.map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;

import com.cargopull.executor_driver.ViewModelThreadTestRule;
import com.cargopull.executor_driver.backend.analytics.ErrorReporter;
import com.cargopull.executor_driver.interactor.map.HeatMapUseCase;
import com.cargopull.executor_driver.presentation.ViewState;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.subjects.PublishSubject;

@RunWith(MockitoJUnitRunner.class)
public class MapViewModelTest {

  @ClassRule
  public static final ViewModelThreadTestRule classRule = new ViewModelThreadTestRule();
  @Rule
  public TestRule rule = new InstantTaskExecutorRule();
  private MapViewModel viewModel;
  @Mock
  private ErrorReporter errorReporter;
  @Mock
  private Observer<ViewState<MapViewActions>> viewStateObserver;

  @Mock
  private HeatMapUseCase heatMapUseCase;

  @Before
  public void setUp() {
    when(heatMapUseCase.loadHeatMap()).thenReturn(Flowable.never());
    viewModel = new MapViewModelImpl(errorReporter, heatMapUseCase);
  }

  /* Проверяем отправку ошибок в репортер */

  /**
   * Не должен отправлять ошибок без действий.
   */
  @Test
  public void doNotTouchErrorReporter() {
    // Effect:
    verifyNoInteractions(errorReporter);
  }

  /**
   * Должен отправить ошибку.
   */
  @Test
  public void reportError() {
    // Action:
    when(heatMapUseCase.loadHeatMap()).thenReturn(Flowable.error(new Exception()));

    // Action:
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Effect:
    verify(errorReporter, only()).reportError(any(Exception.class));
  }

  /* Тетсируем работу с юзкейсом тепловой карты. */

  /**
   * Должен попросить у юзкейса подписку на обновления тепловой карты.
   */
  @Test
  public void askUseCaseToSubscribeToHeatMapUpdates() {
    // Action:
    viewModel.getViewStateLiveData();

    // Effect:
    verify(heatMapUseCase, only()).loadHeatMap();
  }

  /**
   * Не должен просить у юзкейс подписку (после поворотов), если подписка уже была.
   */
  @Test
  public void DoNotTouchUseCaseAfterFirstSubscription() {
    // Action:
    viewModel.getViewStateLiveData();
    viewModel.getViewStateLiveData();
    viewModel.getViewStateLiveData();

    // Effect:
    verify(heatMapUseCase, only()).loadHeatMap();
  }

  /* Тетсируем переключение состояний */

  /**
   * Должен вернуть начально состояние вида без тепловой карты.
   */
  @Test
  public void setViewStateWithNullToLiveData() {
    // Action:
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Effect:
    verify(viewStateObserver, only()).onChanged(new MapViewState(null));
  }

  /**
   * Должен вернуть состояния с обновлениями тепловой карты.
   */
  @Test
  public void setViewStatesWithDataToLiveData() {
    // Given:
    PublishSubject<String> publishSubject = PublishSubject.create();
    when(heatMapUseCase.loadHeatMap())
        .thenReturn(publishSubject.toFlowable(BackpressureStrategy.BUFFER));
    InOrder inOrder = Mockito.inOrder(viewStateObserver);

    // Action:
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);
    publishSubject.onNext("heat map 1");
    publishSubject.onNext("heat map 2");
    publishSubject.onNext("heat map 3");
    publishSubject.onNext("heat map 4");

    // Effect:
    inOrder.verify(viewStateObserver).onChanged(new MapViewState(null));
    inOrder.verify(viewStateObserver).onChanged(new MapViewState("heat map 1"));
    inOrder.verify(viewStateObserver).onChanged(new MapViewState("heat map 2"));
    inOrder.verify(viewStateObserver).onChanged(new MapViewState("heat map 3"));
    inOrder.verify(viewStateObserver).onChanged(new MapViewState("heat map 4"));
    verifyNoMoreInteractions(viewStateObserver);
  }
}