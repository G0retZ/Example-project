package com.cargopull.executor_driver.presentation.selectedvehicle;

import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;
import com.cargopull.executor_driver.ViewModelThreadTestRule;
import com.cargopull.executor_driver.entity.Vehicle;
import com.cargopull.executor_driver.interactor.vehicle.SelectedVehicleUseCase;
import com.cargopull.executor_driver.presentation.ViewState;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
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
    // Действие:
    viewModel.getViewStateLiveData();
    viewModel.getViewStateLiveData();
    viewModel.getViewStateLiveData();

    // Результат:
    verify(selectedVehicleUseCase, only()).getSelectedVehicle();
  }

  /**
   * Не должен трогать юзкейс.
   */
  @Test
  public void DoNotTouchSelectedVehicleUseCaseDuringVehicleChoosing() {
    // Действие:
    viewModel.changeVehicle();
    viewModel.getNavigationLiveData();
    viewModel.changeVehicle();

    // Результат:
    verifyZeroInteractions(selectedVehicleUseCase);
  }

  /* Тетсируем переключение состояний. */

  /**
   * Должен вернуть состояние вида без имени изначально.
   */
  @Test
  public void setViewStateWithoutNameToLiveData() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);

    // Действие:
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(new SelectedVehicleViewState(""));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида с именем.
   */
  @Test
  public void setViewStateWithNameToLiveData() {
    // Дано:
    PublishSubject<Vehicle> publishSubject = PublishSubject.create();
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(selectedVehicleUseCase.getSelectedVehicle()).thenReturn(publishSubject);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(new Vehicle(1, "m", "m", "c", "l", false));
    publishSubject.onNext(new Vehicle(2, "ma", "m", "co", "l", true));
    publishSubject.onNext(new Vehicle(3, "m", "mo", "co", "l", false));
    publishSubject.onNext(new Vehicle(4, "ma", "mo", "c", "l", true));

    // Результат:
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
    // Дано:
    PublishSubject<Vehicle> publishSubject = PublishSubject.create();
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(selectedVehicleUseCase.getSelectedVehicle())
        .thenReturn(publishSubject, PublishSubject.never());
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(new Vehicle(1, "m", "m", "c", "l", false));
    publishSubject.onNext(new Vehicle(2, "ma", "m", "co", "l", true));
    publishSubject.onNext(new Vehicle(3, "m", "mo", "co", "l", false));
    publishSubject.onNext(new Vehicle(4, "ma", "mo", "c", "l", true));
    publishSubject.onError(new IllegalArgumentException());

    // Результат:
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
    // Дано:
    viewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Действие:
    viewModel.changeVehicle();

    // Результат:
    verify(navigateObserver, only()).onChanged(SelectedVehicleNavigate.VEHICLES);
  }
}