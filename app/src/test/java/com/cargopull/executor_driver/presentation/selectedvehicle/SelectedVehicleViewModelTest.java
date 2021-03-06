package com.cargopull.executor_driver.presentation.selectedvehicle;

import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;

import com.cargopull.executor_driver.ViewModelThreadTestRule;
import com.cargopull.executor_driver.entity.Vehicle;
import com.cargopull.executor_driver.interactor.vehicle.SelectedVehicleUseCase;
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

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

@RunWith(MockitoJUnitRunner.class)
public class SelectedVehicleViewModelTest {

  @ClassRule
  public static final ViewModelThreadTestRule classRule = new ViewModelThreadTestRule();
  @Rule
  public TestRule rule = new InstantTaskExecutorRule();
  private SelectedVehicleViewModel viewModel;
  @Mock
  private Observer<ViewState<SelectedVehicleViewActions>> viewStateObserver;

  @Mock
  private SelectedVehicleUseCase selectedVehicleUseCase;

  @Mock
  private Observer<String> navigateObserver;

  @Before
  public void setUp() {
    when(selectedVehicleUseCase.getSelectedVehicle()).thenReturn(Observable.never());
    viewModel = new SelectedVehicleViewModelImpl(selectedVehicleUseCase);
  }


  /* Тетсируем работу с юзкейсом выборанной ТС. */

  /**
   * Должен просить юзкейс получить список ТС, при первой и только при первой подписке.
   */
  @Test
  public void askSelectedVehicleUseCaseForVehiclesInitially() {
    // Action:
    viewModel.getViewStateLiveData();
    viewModel.getViewStateLiveData();
    viewModel.getViewStateLiveData();

    // Effect:
    verify(selectedVehicleUseCase, only()).getSelectedVehicle();
  }

  /**
   * Не должен трогать юзкейс.
   */
  @Test
  public void DoNotTouchSelectedVehicleUseCaseDuringVehicleChoosing() {
    // Action:
    viewModel.changeVehicle();
    viewModel.getNavigationLiveData();
    viewModel.changeVehicle();

    // Effect:
    verifyNoInteractions(selectedVehicleUseCase);
  }

  /* Тетсируем переключение состояний. */

  /**
   * Должен вернуть состояние вида без имени изначально.
   */
  @Test
  public void setViewStateWithoutNameToLiveData() {
    // Given:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);

    // Action:
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Effect:
    inOrder.verify(viewStateObserver).onChanged(new SelectedVehicleViewState(""));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида с именем.
   */
  @Test
  public void setViewStateWithNameToLiveData() {
    // Given:
    PublishSubject<Vehicle> publishSubject = PublishSubject.create();
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(selectedVehicleUseCase.getSelectedVehicle()).thenReturn(publishSubject);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    publishSubject.onNext(new Vehicle(1, "m", "m", "c", "l", false));
    publishSubject.onNext(new Vehicle(2, "ma", "m", "co", "l", true));
    publishSubject.onNext(new Vehicle(3, "m", "mo", "co", "l", false));
    publishSubject.onNext(new Vehicle(4, "ma", "mo", "c", "l", true));

    // Effect:
    inOrder.verify(viewStateObserver).onChanged(new SelectedVehicleViewState(""));
    inOrder.verify(viewStateObserver).onChanged(new SelectedVehicleViewState("m m (l)"));
    inOrder.verify(viewStateObserver).onChanged(new SelectedVehicleViewState("ma m (l)"));
    inOrder.verify(viewStateObserver).onChanged(new SelectedVehicleViewState("m mo (l)"));
    inOrder.verify(viewStateObserver).onChanged(new SelectedVehicleViewState("ma mo (l)"));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида без имени после ошибки.
   */
  @SuppressWarnings("unchecked")
  @Test
  public void setViewStateWithoutNameToLiveDataOnError() {
    // Given:
    PublishSubject<Vehicle> publishSubject = PublishSubject.create();
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(selectedVehicleUseCase.getSelectedVehicle())
        .thenReturn(publishSubject, PublishSubject.never());
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    publishSubject.onNext(new Vehicle(1, "m", "m", "c", "l", false));
    publishSubject.onNext(new Vehicle(2, "ma", "m", "co", "l", true));
    publishSubject.onNext(new Vehicle(3, "m", "mo", "co", "l", false));
    publishSubject.onNext(new Vehicle(4, "ma", "mo", "c", "l", true));
    publishSubject.onError(new IllegalArgumentException());

    // Effect:
    inOrder.verify(viewStateObserver).onChanged(new SelectedVehicleViewState(""));
    inOrder.verify(viewStateObserver).onChanged(new SelectedVehicleViewState("m m (l)"));
    inOrder.verify(viewStateObserver).onChanged(new SelectedVehicleViewState("ma m (l)"));
    inOrder.verify(viewStateObserver).onChanged(new SelectedVehicleViewState("m mo (l)"));
    inOrder.verify(viewStateObserver).onChanged(new SelectedVehicleViewState("ma mo (l)"));
    inOrder.verify(viewStateObserver).onChanged(new SelectedVehicleViewState(""));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /* Тетсируем навигацию. */

  /**
   * Должен вернуть "перейти к списку ТС".
   */
  @Test
  public void setNavigateToVehiclesToLiveData() {
    // Given:
    viewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Action:
    viewModel.changeVehicle();

    // Effect:
    verify(navigateObserver, only()).onChanged(SelectedVehicleNavigate.VEHICLES);
  }
}