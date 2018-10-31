package com.cargopull.executor_driver.presentation.choosevehicle;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;
import com.cargopull.executor_driver.R;
import com.cargopull.executor_driver.ViewModelThreadTestRule;
import com.cargopull.executor_driver.backend.analytics.ErrorReporter;
import com.cargopull.executor_driver.entity.EmptyListException;
import com.cargopull.executor_driver.entity.Vehicle;
import com.cargopull.executor_driver.gateway.DataMappingException;
import com.cargopull.executor_driver.interactor.vehicle.VehicleChoiceUseCase;
import com.cargopull.executor_driver.presentation.ViewState;
import io.reactivex.Completable;
import io.reactivex.subjects.SingleSubject;
import java.util.Arrays;
import java.util.List;
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
public class ChooseVehicleViewModelTest {

  @ClassRule
  public static final ViewModelThreadTestRule classRule = new ViewModelThreadTestRule();
  @Rule
  public TestRule rule = new InstantTaskExecutorRule();
  private ChooseVehicleViewModel viewModel;
  @Mock
  private ErrorReporter errorReporter;
  @Mock
  private VehicleChoiceUseCase vehicleChoiceUseCase;

  @Mock
  private Observer<ViewState<ChooseVehicleViewActions>> viewStateObserver;

  @Mock
  private Observer<String> navigateObserver;

  private SingleSubject<List<Vehicle>> vehicleSingleSubject;

  @Before
  public void setUp() {
    vehicleSingleSubject = SingleSubject.create();
    when(vehicleChoiceUseCase.getVehicles()).thenReturn(vehicleSingleSubject);
    when(vehicleChoiceUseCase.selectVehicle(any())).thenReturn(Completable.never());
    viewModel = new ChooseVehicleViewModelImpl(errorReporter, vehicleChoiceUseCase);
  }

  /* Проверяем отправку ошибок в репортер */

  /**
   * Должен отправить ошибку.
   */
  @Test
  public void reportError() {
    // Действие:
    vehicleSingleSubject.onError(new DataMappingException());

    // Результат:
    verify(errorReporter, only()).reportError(any(DataMappingException.class));
  }

  /**
   * Должен отправить ошибку, если выбраного ТС нет в списке.
   */
  @Test
  public void reportOutOfBoundsError() {
    // Дано:
    when(vehicleChoiceUseCase.selectVehicle(any()))
        .thenReturn(Completable.error(IndexOutOfBoundsException::new));

    // Действие:
    viewModel.selectItem(new ChooseVehicleListItem(new Vehicle(3, "m", "m", "co", "l", false)));

    // Результат:
    verify(errorReporter, only()).reportError(any(IndexOutOfBoundsException.class));
  }

  /**
   * Должен отправить ошибку, если выбрано занятое ТС или его нет в списке.
   */
  @Test
  public void reportArgumentError() {
    // Дано:
    when(vehicleChoiceUseCase.selectVehicle(any()))
        .thenReturn(Completable.error(IllegalArgumentException::new));

    // Действие:
    viewModel.selectItem(new ChooseVehicleListItem(new Vehicle(3, "m", "m", "co", "l", false)));

    // Результат:
    verify(errorReporter, only()).reportError(any(IllegalArgumentException.class));
  }

  /**
   * Должен отправить ошибку, если список пуст.
   */
  @Test
  public void reportEmptyListError() {
    // Дано:
    when(vehicleChoiceUseCase.selectVehicle(any()))
        .thenReturn(Completable.error(EmptyListException::new));

    // Действие:
    viewModel.selectItem(new ChooseVehicleListItem(new Vehicle(3, "m", "m", "co", "l", false)));

    // Результат:
    verify(errorReporter, only()).reportError(any(EmptyListException.class));
  }

  /**
   * Должен отправить ошибку, если список пуст.
   */
  @Test
  public void doNotReportOtherError() {
    // Дано:
    when(vehicleChoiceUseCase.selectVehicle(any()))
        .thenReturn(Completable.error(Exception::new));

    // Действие:
    viewModel.selectItem(new ChooseVehicleListItem(new Vehicle(3, "m", "m", "co", "l", false)));

    // Результат:
    verifyZeroInteractions(errorReporter);
  }

  /* Тетсируем работу с юзкейсом выбора ТС. */

  /**
   * Должен просить юзкейс получить список ТС изначально.
   */
  @Test
  public void askChooseVehicleUseCaseForVehicles() {
    // Результат:
    verify(vehicleChoiceUseCase, only()).getVehicles();
  }

  /**
   * Не должен трогать юзкейс на подписках.
   */
  @Test
  public void DoNotTouchChooseVehicleUseCaseDuringLoadingVehicles() {
    // Действие:
    vehicleSingleSubject.onSuccess(Arrays.asList(
        new Vehicle(1, "m", "m", "c", "l", false),
        new Vehicle(2, "ma", "m", "co", "l", true),
        new Vehicle(3, "m", "m", "co", "l", false),
        new Vehicle(4, "ma", "m", "c", "l", true)
    ));
    viewModel.getViewStateLiveData();
    viewModel.getViewStateLiveData();
    viewModel.getNavigationLiveData();
    viewModel.getViewStateLiveData();

    // Результат:
    verify(vehicleChoiceUseCase, only()).getVehicles();
  }

  /**
   * Должен попросить юзкейс выбрать ТС по из указанного элемента.
   */
  @Test
  public void askChooseVehicleUseCaseToSelectVehicle() {
    // Дано:
    when(vehicleChoiceUseCase.selectVehicle(any())).thenReturn(Completable.complete());

    // Действие:
    viewModel.selectItem(new ChooseVehicleListItem(new Vehicle(1, "m", "m", "c", "l", false)));
    viewModel.selectItem(new ChooseVehicleListItem(new Vehicle(2, "ma", "m", "co", "l", true)));
    viewModel.selectItem(new ChooseVehicleListItem(new Vehicle(3, "m", "m", "co", "l", false)));

    // Результат:
    verify(vehicleChoiceUseCase).getVehicles();
    verify(vehicleChoiceUseCase).selectVehicle(new Vehicle(1, "m", "m", "c", "l", false));
    verify(vehicleChoiceUseCase).selectVehicle(new Vehicle(2, "ma", "m", "co", "l", true));
    verify(vehicleChoiceUseCase).selectVehicle(new Vehicle(3, "m", "m", "co", "l", false));
    verifyNoMoreInteractions(vehicleChoiceUseCase);
  }

  /**
   * Не должен трогать юзкейс, если предыдущий запрос выбора ТС еще не завершился.
   */
  @Test
  public void DoNotTouchChooseVehicleUseCaseDuringVehicleChoosing() {
    // Действие:
    viewModel.selectItem(new ChooseVehicleListItem(new Vehicle(1, "m", "m", "c", "l", false)));
    viewModel.selectItem(new ChooseVehicleListItem(new Vehicle(2, "ma", "m", "co", "l", true)));
    viewModel.selectItem(new ChooseVehicleListItem(new Vehicle(3, "m", "m", "co", "l", false)));

    // Результат:
    verify(vehicleChoiceUseCase).getVehicles();
    verify(vehicleChoiceUseCase).selectVehicle(new Vehicle(1, "m", "m", "c", "l", false));
    verifyNoMoreInteractions(vehicleChoiceUseCase);
  }

  /* Тетсируем переключение состояний. */

  /**
   * Должен вернуть состояние вида "В процессе" изначально.
   */
  @Test
  public void setInitialViewStateToLiveData() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);

    // Действие:
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(ChooseVehicleViewStatePending.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида "Ошибка" сети.
   */
  @Test
  public void setNetworkErrorViewStateToLiveData() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    vehicleSingleSubject.onError(new Exception());

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(ChooseVehicleViewStatePending.class));
    inOrder.verify(viewStateObserver)
        .onChanged(new ChooseVehicleViewStateError(R.string.error));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида "Ошибка" нет доступных ТС.
   */
  @Test
  public void setEmptyErrorViewStateToLiveData() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    vehicleSingleSubject.onError(new EmptyListException());

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(ChooseVehicleViewStatePending.class));
    inOrder.verify(viewStateObserver)
        .onChanged(new ChooseVehicleViewStateError(R.string.no_vehicles_message));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида "Готово" со списком ТС для выбора.
   */
  @Test
  public void setSuccessViewStateToLiveDataPending() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    vehicleSingleSubject.onSuccess(Arrays.asList(
        new Vehicle(1, "m", "m", "c", "l", false),
        new Vehicle(2, "ma", "m", "co", "l", true),
        new Vehicle(3, "m", "m", "co", "l", false),
        new Vehicle(4, "ma", "m", "c", "l", true)
    ));

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(ChooseVehicleViewStatePending.class));
    inOrder.verify(viewStateObserver).onChanged(new ChooseVehicleViewStateReady(Arrays.asList(
        new ChooseVehicleListItem(new Vehicle(1, "m", "m", "c", "l", false)),
        new ChooseVehicleListItem(new Vehicle(2, "ma", "m", "co", "l", true)),
        new ChooseVehicleListItem(new Vehicle(3, "m", "m", "co", "l", false)),
        new ChooseVehicleListItem(new Vehicle(4, "ma", "m", "c", "l", true))
    )));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /* Тетсируем навигацию. */

  /**
   * Должен игнорировать неуспешниые выборы.
   */
  @Test
  public void setNothingToLiveData() {
    // Дано:
    when(vehicleChoiceUseCase.selectVehicle(any()))
        .thenReturn(Completable.error(new IndexOutOfBoundsException()));
    viewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Действие:
    viewModel.selectItem(new ChooseVehicleListItem(new Vehicle(1, "m", "m", "c", "l", false)));

    // Результат:
    verifyZeroInteractions(navigateObserver);
  }

  /**
   * Должен вернуть "перейти к опциям" если выбор был успешным.
   */
  @Test
  public void setNavigateToSetVehicleOptionsToLiveData() {
    // Дано:
    when(vehicleChoiceUseCase.selectVehicle(any())).thenReturn(Completable.complete());
    viewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Действие:
    viewModel.selectItem(new ChooseVehicleListItem(new Vehicle(1, "m", "m", "c", "l", false)));

    // Результат:
    verify(navigateObserver, only()).onChanged(ChooseVehicleNavigate.VEHICLE_OPTIONS);
  }
}