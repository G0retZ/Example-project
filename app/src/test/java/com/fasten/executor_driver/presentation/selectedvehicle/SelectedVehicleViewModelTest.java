package com.fasten.executor_driver.presentation.selectedvehicle;

import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.Observer;
import com.fasten.executor_driver.entity.Vehicle;
import com.fasten.executor_driver.interactor.vehicle.SelectedVehicleUseCase;
import com.fasten.executor_driver.presentation.ViewState;
import io.reactivex.Observable;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.schedulers.TestScheduler;
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
public class SelectedVehicleViewModelTest {

  private SelectedVehicleViewModel selectedVehicleViewModel;

  private TestScheduler testScheduler;

  @Rule
  public TestRule rule = new InstantTaskExecutorRule();

  @Mock
  private Observer<ViewState<SelectedVehicleViewActions>> viewStateObserver;

  @Mock
  private SelectedVehicleUseCase selectedVehicleUseCase;

  @Mock
  private Observer<String> navigateObserver;

  @Before
  public void setUp() throws Exception {
    testScheduler = new TestScheduler();
    RxJavaPlugins.setIoSchedulerHandler(scheduler -> testScheduler);
    RxJavaPlugins.setSingleSchedulerHandler(scheduler -> Schedulers.trampoline());
    RxAndroidPlugins.setInitMainThreadSchedulerHandler(scheduler -> Schedulers.trampoline());
    when(selectedVehicleUseCase.getSelectedVehicle()).thenReturn(Observable.never());
    selectedVehicleViewModel = new SelectedVehicleViewModelImpl(selectedVehicleUseCase);
  }


  /* Тетсируем работу с юзкейсом выборанной ТС. */

  /**
   * Должен просить юзкейс получить список ТС, при первой и только при первой подписке.
   *
   * @throws Exception error
   */
  @Test
  public void askSelectedVehicleUseCaseForVehiclesInitially() throws Exception {
    // Действие:
    selectedVehicleViewModel.getViewStateLiveData();
    selectedVehicleViewModel.getViewStateLiveData();
    selectedVehicleViewModel.getViewStateLiveData();

    // Результат:
    verify(selectedVehicleUseCase, only()).getSelectedVehicle();
  }

  /**
   * Не должен трогать юзкейс.
   *
   * @throws Exception error
   */
  @Test
  public void DoNotTouchSelectedVehicleUseCaseDuringVehicleChoosing() throws Exception {
    // Действие:
    selectedVehicleViewModel.changeVehicle();
    selectedVehicleViewModel.getNavigationLiveData();
    selectedVehicleViewModel.changeVehicle();

    // Результат:
    verifyZeroInteractions(selectedVehicleUseCase);
  }

  /* Тетсируем переключение состояний. */

  /**
   * Должен вернуть состояние вида без имени изначально.
   *
   * @throws Exception error
   */
  @Test
  public void setViewStateWithoutNameToLiveData() throws Exception {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);

    // Действие:
    selectedVehicleViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(new SelectedVehicleViewState(""));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида с именем.
   *
   * @throws Exception error
   */
  @Test
  public void setViewStateWithNameToLiveData() throws Exception {
    // Дано:
    PublishSubject<Vehicle> publishSubject = PublishSubject.create();
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(selectedVehicleUseCase.getSelectedVehicle()).thenReturn(publishSubject);
    selectedVehicleViewModel.getViewStateLiveData().observeForever(viewStateObserver);

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
   *
   * @throws Exception error
   */
  @Test
  public void setViewStateWithoutNameToLiveDataOnError() throws Exception {
    // Дано:
    PublishSubject<Vehicle> publishSubject = PublishSubject.create();
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(selectedVehicleUseCase.getSelectedVehicle()).thenReturn(publishSubject);
    selectedVehicleViewModel.getViewStateLiveData().observeForever(viewStateObserver);

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
   *
   * @throws Exception error
   */
  @Test
  public void setNavigateToVehiclesToLiveData() throws Exception {
    // Дано:
    selectedVehicleViewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Действие:
    selectedVehicleViewModel.changeVehicle();

    // Результат:
    verify(navigateObserver, only()).onChanged(SelectedVehicleNavigate.VEHICLES);
  }
}