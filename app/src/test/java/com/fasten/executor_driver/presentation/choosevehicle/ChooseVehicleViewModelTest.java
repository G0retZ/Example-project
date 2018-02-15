package com.fasten.executor_driver.presentation.choosevehicle;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.Observer;
import com.fasten.executor_driver.R;
import com.fasten.executor_driver.backend.web.NoNetworkException;
import com.fasten.executor_driver.entity.NoVehiclesAvailableException;
import com.fasten.executor_driver.entity.OnlyOneVehicleAvailableException;
import com.fasten.executor_driver.entity.Vehicle;
import com.fasten.executor_driver.interactor.vehicle.VehicleChoiceUseCase;
import com.fasten.executor_driver.presentation.ViewState;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.SingleSubject;
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
    when(vehicleChoiceUseCase.getVehicles()).thenReturn(Single.never());
    when(vehicleChoiceUseCase.setSelectedVehicle(anyInt())).thenReturn(Completable.never());
    chooseVehicleViewModel = new ChooseVehicleViewModelImpl(vehicleChoiceUseCase);
  }

  /* Тетсируем работу с юзкейсом выбора ТС. */

  /**
   * Должен просить юзкейс получить список ТС, при первой и только при первой подписке.
   *
   * @throws Exception error.
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
   * Должен попросить юзкейс выбрать ТС по указанному индексу.
   *
   * @throws Exception error.
   */
  @Test
  public void askChooseVehicleUseCaseToSelectVehicle() throws Exception {
    // Дано:
    when(vehicleChoiceUseCase.setSelectedVehicle(anyInt())).thenReturn(Completable.complete());

    // Действие:
    chooseVehicleViewModel.setSelection(12);
    chooseVehicleViewModel.setSelection(123);
    chooseVehicleViewModel.setSelection(1234);

    // Результат:
    verify(vehicleChoiceUseCase).setSelectedVehicle(12);
    verify(vehicleChoiceUseCase).setSelectedVehicle(123);
    verify(vehicleChoiceUseCase).setSelectedVehicle(1234);
    verifyNoMoreInteractions(vehicleChoiceUseCase);
  }

  /**
   * Не должен трогать юзкейс, если предыдущий запрос списка ТС еще не завершился.
   *
   * @throws Exception error.
   */
  @Test
  public void DoNotTouchChooseVehicleUseCaseDuringVehicleGetting() throws Exception {
    // Действие:
    chooseVehicleViewModel.getViewStateLiveData();
    chooseVehicleViewModel.getViewStateLiveData();
    chooseVehicleViewModel.getViewStateLiveData();
    chooseVehicleViewModel.setSelection(12);
    chooseVehicleViewModel.setSelection(123);
    chooseVehicleViewModel.setSelection(1234);

    // Результат:
    verify(vehicleChoiceUseCase, only()).getVehicles();
  }

  /**
   * Не должен трогать юзкейс, если предыдущий запрос выбора ТС еще не завершился.
   *
   * @throws Exception error.
   */
  @Test
  public void DoNotTouchChooseVehicleUseCaseDuringVehicleChoosing() throws Exception {
    // Действие:
    chooseVehicleViewModel.setSelection(12);
    chooseVehicleViewModel.setSelection(123);
    chooseVehicleViewModel.setSelection(1234);
    chooseVehicleViewModel.getViewStateLiveData();
    chooseVehicleViewModel.getViewStateLiveData();
    chooseVehicleViewModel.getViewStateLiveData();

    // Результат:
    verify(vehicleChoiceUseCase, only()).setSelectedVehicle(12);
  }

  /* Тетсируем переключение состояний. */

  /**
   * Должен вернуть состояние вида "В процессе" изначально.
   *
   * @throws Exception error.
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
   * Должен вернуть состояние вида "Ошибка" сети
   *
   * @throws Exception error
   */
  @Test
  public void setNetworkErrorViewStateToLiveData() throws Exception {
    // Дано:
    SingleSubject<List<Vehicle>> singleSubject = SingleSubject.create();
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(vehicleChoiceUseCase.getVehicles()).thenReturn(singleSubject);
    chooseVehicleViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    singleSubject.onError(new NoNetworkException());

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(ChooseVehicleViewStatePending.class));
    inOrder.verify(viewStateObserver)
        .onChanged(new ChooseVehicleViewStateError(R.string.no_network_connection));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида "Ошибка" нет доступных ТС
   *
   * @throws Exception error
   */
  @Test
  public void setEmptyErrorViewStateToLiveData() throws Exception {
    // Дано:
    SingleSubject<List<Vehicle>> singleSubject = SingleSubject.create();
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(vehicleChoiceUseCase.getVehicles()).thenReturn(singleSubject);
    chooseVehicleViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    singleSubject.onError(new NoVehiclesAvailableException());

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(ChooseVehicleViewStatePending.class));
    inOrder.verify(viewStateObserver)
        .onChanged(new ChooseVehicleViewStateError(R.string.no_vehicles_available));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида "Готово" со списком ТС для выбора
   *
   * @throws Exception error
   */
  @Test
  public void setSuccessViewStateToLiveDataPending() throws Exception {
    // Дано:
    SingleSubject<List<Vehicle>> singleSubject = SingleSubject.create();
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(vehicleChoiceUseCase.getVehicles()).thenReturn(singleSubject);
    chooseVehicleViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    singleSubject.onSuccess(Arrays.asList(
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
   * Должен вернуть "перейти к опциям автоматически" если была ошибка "только одно авто"
   *
   * @throws Exception error
   */
  @Test
  public void setNavigateAutoSetVehicleOptionsToLiveData() throws Exception {
    // Дано:
    SingleSubject<List<Vehicle>> singleSubject = SingleSubject.create();
    when(vehicleChoiceUseCase.getVehicles()).thenReturn(singleSubject);
    chooseVehicleViewModel.getViewStateLiveData();
    chooseVehicleViewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Действие:
    singleSubject.onError(new OnlyOneVehicleAvailableException());

    // Результат:
    verify(navigateObserver, only()).onChanged(ChooseVehicleNavigate.AUTO_VEHICLE_OPTIONS);
  }

  /**
   * Должен игнорировать неуспешниые выборы
   *
   * @throws Exception error
   */
  @Test
  public void setNothingToLiveData() throws Exception {
    // Дано:
    when(vehicleChoiceUseCase.setSelectedVehicle(anyInt()))
        .thenReturn(Completable.error(new IndexOutOfBoundsException()));
    chooseVehicleViewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Действие:
    chooseVehicleViewModel.setSelection(1);

    // Результат:
    verifyZeroInteractions(navigateObserver);
  }

  /**
   * Должен вернуть "перейти к опциям" если выбор был успешным
   *
   * @throws Exception error
   */
  @Test
  public void setNavigateToSetVehicleOptionsToLiveData() throws Exception {
    // Дано:
    when(vehicleChoiceUseCase.setSelectedVehicle(anyInt())).thenReturn(Completable.complete());
    chooseVehicleViewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Действие:
    chooseVehicleViewModel.setSelection(1);

    // Результат:
    verify(navigateObserver, only()).onChanged(ChooseVehicleNavigate.VEHICLE_OPTIONS);
  }
}