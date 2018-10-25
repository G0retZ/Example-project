package com.cargopull.executor_driver.presentation.geolocationstate;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;
import com.cargopull.executor_driver.ViewModelThreadTestRule;
import com.cargopull.executor_driver.interactor.CommonGateway;
import com.cargopull.executor_driver.presentation.ViewState;
import io.reactivex.BackpressureStrategy;
import io.reactivex.subjects.PublishSubject;
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

@RunWith(MockitoJUnitRunner.class)
public class GeoLocationStateViewModelTest {

  @ClassRule
  public static final ViewModelThreadTestRule classRule = new ViewModelThreadTestRule();
  @Rule
  public TestRule rule = new InstantTaskExecutorRule();
  private GeoLocationStateViewModel viewModel;
  @Mock
  private CommonGateway<Boolean> gateway;
  @Mock
  private Observer<ViewState<GeoLocationStateViewActions>> viewStateObserver;
  @Captor
  private ArgumentCaptor<ViewState<GeoLocationStateViewActions>> viewStateCaptor;
  @Mock
  private GeoLocationStateViewActions viewActions;

  private PublishSubject<Boolean> publishSubject;

  @Before
  public void setUp() {
    publishSubject = PublishSubject.create();
    when(gateway.getData()).thenReturn(publishSubject.toFlowable(BackpressureStrategy.BUFFER));
    viewModel = new GeoLocationStateViewModelImpl(gateway);
  }

  /* Тетсируем работу с гейтвеем. */

  /**
   * Должен запросить у гейтвея состояния сервисов местоположения изначально.
   */
  @Test
  public void askGatewayForGeolocationStateInitially() {
    // Результат:
    verify(gateway, only()).getData();
  }

  /**
   * Не должен трогать гейтвей на подписках.
   */
  @Test
  public void doNotTouchGatewayOnSubscriptions() {
    // Действие:
    viewModel.getViewStateLiveData();
    viewModel.getNavigationLiveData();
    viewModel.getViewStateLiveData();
    viewModel.getNavigationLiveData();

    // Результат:
    verify(gateway, only()).getData();
  }

  /* Тетсируем смену состояний. */

  /**
   * Должен показать сообщение об упущенном заказе.
   */
  @Test
  public void showGeolocationStates() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewActions);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(true);
    publishSubject.onNext(false);
    publishSubject.onNext(true);

    // Результат:
    verify(viewStateObserver, times(3)).onChanged(viewStateCaptor.capture());
    assertEquals(viewStateCaptor.getAllValues().size(), 3);
    viewStateCaptor.getAllValues().get(0).apply(viewActions);
    viewStateCaptor.getAllValues().get(1).apply(viewActions);
    viewStateCaptor.getAllValues().get(2).apply(viewActions);
    inOrder.verify(viewActions).showGeolocationState(true);
    inOrder.verify(viewActions).showGeolocationState(false);
    inOrder.verify(viewActions).showGeolocationState(true);
    verifyNoMoreInteractions(viewActions);
  }
}