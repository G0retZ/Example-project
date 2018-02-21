package com.fasten.executor_driver.presentation.choosevehicle;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.Observer;
import com.fasten.executor_driver.R;
import com.fasten.executor_driver.entity.NoVehiclesAvailableException;
import com.fasten.executor_driver.entity.Vehicle;
import com.fasten.executor_driver.interactor.vehicle.VehicleChoiceUseCase;
import com.fasten.executor_driver.presentation.ViewState;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import java.util.Arrays;
import java.util.List;
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
public class ChooseVehicleViewModelTest {

  private ChooseVehicleViewModel chooseVehicleViewModel;

  @Rule
  public TestRule rule = new InstantTaskExecutorRule();

  @Mock
  private VehicleChoiceUseCase vehicleChoiceUseCase;

  @Mock
  private Observer<ViewState<ChooseVehicleViewActions>> viewStateObserver;

  @Mock
  private Observer<String> navigateObserver;

  @Before
  public void setUp() throws Exception {
    RxJavaPlugins.setSingleSchedulerHandler(scheduler -> Schedulers.trampoline());
    RxAndroidPlugins.setInitMainThreadSchedulerHandler(scheduler -> Schedulers.trampoline());
    when(vehicleChoiceUseCase.getVehicles()).thenReturn(Observable.never());
    when(vehicleChoiceUseCase.selectVehicle(any())).thenReturn(Completable.never());
    chooseVehicleViewModel = new ChooseVehicleViewModelImpl(vehicleChoiceUseCase);
  }

  /* Тетсируем работу с юзкейсом выбора ТС. */

  /**
   * Должен просить юзкейс получить список ТС, при первой и только при первой подписке.
   *
   * @throws Exception error
   */
  @Test
  public void askChooseVehicleUseCaseForVehiclesInitially() throws Exception {
    // Действие:
    chooseVehicleViewModel.getViewStateLiveData();
    chooseVehicleViewModel.getViewStateLiveData();
    chooseVehicleViewModel.getViewStateLiveData();

    // Результат:
    verify(vehicleChoiceUseCase, only()).getVehicles();
  }

  /**
   * Должен попросить юзкейс выбрать ТС по из указанного элемента.
   *
   * @throws Exception error
   */
  @Test
  public void askChooseVehicleUseCaseToSelectVehicle() throws Exception {
    // Дано:
    when(vehicleChoiceUseCase.selectVehicle(any())).thenReturn(Completable.complete());

    // Действие:
    chooseVehicleViewModel
        .selectItem(new ChooseVehicleListItem(new Vehicle(1, "m", "m", "c", "l", false)));
    chooseVehicleViewModel
        .selectItem(new ChooseVehicleListItem(new Vehicle(2, "ma", "m", "co", "l", true)));
    chooseVehicleViewModel
        .selectItem(new ChooseVehicleListItem(new Vehicle(3, "m", "m", "co", "l", false)));

    // Результат:
    verify(vehicleChoiceUseCase).selectVehicle(new Vehicle(1, "m", "m", "c", "l", false));
    verify(vehicleChoiceUseCase).selectVehicle(new Vehicle(2, "ma", "m", "co", "l", true));
    verify(vehicleChoiceUseCase).selectVehicle(new Vehicle(3, "m", "m", "co", "l", false));
    verifyNoMoreInteractions(vehicleChoiceUseCase);
  }

  /**
   * Не должен трогать юзкейс, если предыдущий запрос выбора ТС еще не завершился.
   *
   * @throws Exception error
   */
  @Test
  public void DoNotTouchChooseVehicleUseCaseDuringVehicleChoosing() throws Exception {
    // Действие:
    chooseVehicleViewModel
        .selectItem(new ChooseVehicleListItem(new Vehicle(1, "m", "m", "c", "l", false)));
    chooseVehicleViewModel
        .selectItem(new ChooseVehicleListItem(new Vehicle(2, "ma", "m", "co", "l", true)));
    chooseVehicleViewModel
        .selectItem(new ChooseVehicleListItem(new Vehicle(3, "m", "m", "co", "l", false)));

    // Результат:
    verify(vehicleChoiceUseCase, only()).selectVehicle(new Vehicle(1, "m", "m", "c", "l", false));
  }

  /* Тетсируем переключение состояний. */

  /**
   * Должен вернуть состояние вида "В процессе" изначально.
   *
   * @throws Exception error
   */
  @Test
  public void setInitialViewStateToLiveData() throws Exception {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);

    // Действие:
    chooseVehicleViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(ChooseVehicleViewStatePending.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида "Ошибка" сети.
   *
   * @throws Exception error
   */
  @Test
  public void setNetworkErrorViewStateToLiveData() throws Exception {
    // Дано:
    PublishSubject<List<Vehicle>> publishSubject = PublishSubject.create();
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(vehicleChoiceUseCase.getVehicles()).thenReturn(publishSubject);
    chooseVehicleViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onError(new Exception());

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(ChooseVehicleViewStatePending.class));
    inOrder.verify(viewStateObserver)
        .onChanged(new ChooseVehicleViewStateError(R.string.error));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида "Ошибка" нет доступных ТС.
   *
   * @throws Exception error
   */
  @Test
  public void setEmptyErrorViewStateToLiveData() throws Exception {
    // Дано:
    PublishSubject<List<Vehicle>> publishSubject = PublishSubject.create();
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(vehicleChoiceUseCase.getVehicles()).thenReturn(publishSubject);
    chooseVehicleViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onError(new NoVehiclesAvailableException());

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(ChooseVehicleViewStatePending.class));
    inOrder.verify(viewStateObserver)
        .onChanged(new ChooseVehicleViewStateError(R.string.no_vehicles_available));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида "Готово" со списком ТС для выбора.
   *
   * @throws Exception error
   */
  @Test
  public void setSuccessViewStateToLiveDataPending() throws Exception {
    // Дано:
    PublishSubject<List<Vehicle>> publishSubject = PublishSubject.create();
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(vehicleChoiceUseCase.getVehicles()).thenReturn(publishSubject);
    chooseVehicleViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(Arrays.asList(
        new Vehicle(1, "m", "m", "c", "l", false),
        new Vehicle(2, "ma", "m", "co", "l", true),
        new Vehicle(3, "m", "m", "co", "l", false),
        new Vehicle(4, "ma", "m", "c", "l", true)
    ));

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(ChooseVehicleViewStatePending.class));
    inOrder.verify(viewStateObserver).onChanged(new ChooseVehicleViewStateReady(Arrays.asList(
        new ChooseVehicleListItem(
            new Vehicle(1, "m", "m", "c", "l", false)
        ),
        new ChooseVehicleListItem(
            new Vehicle(2, "ma", "m", "co", "l", true)
        ),
        new ChooseVehicleListItem(
            new Vehicle(3, "m", "m", "co", "l", false)
        ),
        new ChooseVehicleListItem(
            new Vehicle(4, "ma", "m", "c", "l", true)
        )
    )));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /* Тетсируем навигацию. */

  /**
   * Должен игнорировать неуспешниые выборы.
   *
   * @throws Exception error
   */
  @Test
  public void setNothingToLiveData() throws Exception {
    // Дано:
    when(vehicleChoiceUseCase.selectVehicle(any()))
        .thenReturn(Completable.error(new IndexOutOfBoundsException()));
    chooseVehicleViewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Действие:
    chooseVehicleViewModel
        .selectItem(new ChooseVehicleListItem(new Vehicle(1, "m", "m", "c", "l", false)));

    // Результат:
    verifyZeroInteractions(navigateObserver);
  }

  /**
   * Должен вернуть "перейти к опциям" если выбор был успешным.
   *
   * @throws Exception error
   */
  @Test
  public void setNavigateToSetVehicleOptionsToLiveData() throws Exception {
    // Дано:
    when(vehicleChoiceUseCase.selectVehicle(any())).thenReturn(Completable.complete());
    chooseVehicleViewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Действие:
    chooseVehicleViewModel
        .selectItem(new ChooseVehicleListItem(new Vehicle(1, "m", "m", "c", "l", false)));

    // Результат:
    verify(navigateObserver, only()).onChanged(ChooseVehicleNavigate.VEHICLE_OPTIONS);
  }
}