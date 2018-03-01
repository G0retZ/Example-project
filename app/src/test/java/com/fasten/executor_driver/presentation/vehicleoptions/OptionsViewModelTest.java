package com.fasten.executor_driver.presentation.vehicleoptions;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.Observer;
import com.fasten.executor_driver.R;
import com.fasten.executor_driver.backend.web.NoNetworkException;
import com.fasten.executor_driver.entity.Option;
import com.fasten.executor_driver.entity.OptionBoolean;
import com.fasten.executor_driver.entity.OptionNumeric;
import com.fasten.executor_driver.interactor.vehicle.VehicleOptionsUseCase;
import com.fasten.executor_driver.presentation.ViewState;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import java.util.ArrayList;
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
public class OptionsViewModelTest {

  @Rule
  public TestRule rule = new InstantTaskExecutorRule();
  private VehicleOptionsViewModel vehicleOptionsViewModel;
  @Mock
  private VehicleOptionsUseCase vehicleOptionsUseCase;

  @Mock
  private Observer<ViewState<VehicleOptionsViewActions>> viewStateObserver;

  @Mock
  private Observer<String> navigateObserver;

  @Before
  public void setUp() throws Exception {
    RxJavaPlugins.setSingleSchedulerHandler(scheduler -> Schedulers.trampoline());
    RxAndroidPlugins.setInitMainThreadSchedulerHandler(scheduler -> Schedulers.trampoline());
    when(vehicleOptionsUseCase.getVehicleOptions()).thenReturn(Observable.never());
    when(vehicleOptionsUseCase.setSelectedVehicleOptions(anyList()))
        .thenReturn(Completable.never());
    vehicleOptionsViewModel = new VehicleOptionsViewModelImpl(vehicleOptionsUseCase);
  }

  /* Тетсируем работу с юзкейсом выбора опций ТС. */

  /**
   * Должен просить юзкейс получить список опций ТС, при первой и только при первой подписке.
   *
   * @throws Exception error
   */
  @Test
  public void askVehicleOptionsUseCaseForOptionsInitially() throws Exception {
    // Действие:
    vehicleOptionsViewModel.getViewStateLiveData();
    vehicleOptionsViewModel.getViewStateLiveData();
    vehicleOptionsViewModel.getViewStateLiveData();

    // Результат:
    verify(vehicleOptionsUseCase, only()).getVehicleOptions();
  }

  /**
   * Должен попросить юзкейс занять ТС с указанными настройкам.
   *
   * @throws Exception error
   */
  @Test
  public void askVehicleOptionsUseCaseToOccupyVehicleWithOptions() throws Exception {
    // Дано:
    when(vehicleOptionsUseCase.setSelectedVehicleOptions(anyList()))
        .thenReturn(Completable.complete());

    // Действие:
    vehicleOptionsViewModel.setVehicleOptions(Arrays.asList(
        new VehicleOptionsListItem<>(
            new OptionBoolean(1, "name", "description", true, false)
        ),
        new VehicleOptionsListItem<>(
            new OptionBoolean(2, "emacs", "descriptions", true, true)
        )
    ));
    vehicleOptionsViewModel.setVehicleOptions(Arrays.asList(
        new VehicleOptionsListItem<>(
            new OptionBoolean(1, "name", "description", true, false)
        ),
        new VehicleOptionsListItem<>(
            new OptionBoolean(2, "emacs", "descriptions", true, true)
        ),
        new VehicleOptionsListItem<>(
            new OptionNumeric(3, "names", "desc", true, 5, 0, 10)
        )
    ));
    vehicleOptionsViewModel.setVehicleOptions(Arrays.asList(
        new VehicleOptionsListItem<>(
            new OptionBoolean(1, "name", "description", true, false)
        ),
        new VehicleOptionsListItem<>(
            new OptionBoolean(2, "emacs", "descriptions", true, true)
        ),
        new VehicleOptionsListItem<>(
            new OptionNumeric(3, "names", "desc", true, 5, 0, 10)
        ),
        new VehicleOptionsListItem<>(
            new OptionNumeric(4, "nam", "script", false, 1, -1, 2)
        )
    ));

    // Результат:
    verify(vehicleOptionsUseCase).setSelectedVehicleOptions(Arrays.asList(
        new OptionBoolean(1, "name", "description", true, false),
        new OptionBoolean(2, "emacs", "descriptions", true, true)
    ));
    verify(vehicleOptionsUseCase).setSelectedVehicleOptions(Arrays.asList(
        new OptionBoolean(1, "name", "description", true, false),
        new OptionBoolean(2, "emacs", "descriptions", true, true),
        new OptionNumeric(3, "names", "desc", true, 5, 0, 10)
    ));
    verify(vehicleOptionsUseCase).setSelectedVehicleOptions(Arrays.asList(
        new OptionBoolean(1, "name", "description", true, false),
        new OptionBoolean(2, "emacs", "descriptions", true, true),
        new OptionNumeric(3, "names", "desc", true, 5, 0, 10),
        new OptionNumeric(4, "nam", "script", false, 1, -1, 2)
    ));
    verifyNoMoreInteractions(vehicleOptionsUseCase);
  }

  /**
   * Не должен трогать юзкейс, если предыдущий запрос занятия ТС еще не завершился.
   *
   * @throws Exception error
   */
  @Test
  public void DoNotTouchVehicleOptionsUseCaseDuringVehicleOccupying() throws Exception {
    // Действие:
    vehicleOptionsViewModel.setVehicleOptions(Arrays.asList(
        new VehicleOptionsListItem<>(
            new OptionBoolean(1, "name", "description", true, false)
        ),
        new VehicleOptionsListItem<>(
            new OptionBoolean(2, "emacs", "descriptions", true, true)
        )
    ));
    vehicleOptionsViewModel.setVehicleOptions(Arrays.asList(
        new VehicleOptionsListItem<>(
            new OptionBoolean(1, "name", "description", true, false)
        ),
        new VehicleOptionsListItem<>(
            new OptionBoolean(2, "emacs", "descriptions", true, true)
        ),
        new VehicleOptionsListItem<>(
            new OptionNumeric(3, "names", "desc", true, 5, 0, 10)
        )
    ));
    vehicleOptionsViewModel.setVehicleOptions(Arrays.asList(
        new VehicleOptionsListItem<>(
            new OptionBoolean(1, "name", "description", true, false)
        ),
        new VehicleOptionsListItem<>(
            new OptionBoolean(2, "emacs", "description", true, true)
        ),
        new VehicleOptionsListItem<>(
            new OptionNumeric(3, "names", "desc", true, 5, 0, 10)
        ),
        new VehicleOptionsListItem<>(
            new OptionNumeric(4, "nam", "script", false, 1, -1, 2)
        )
    ));

    // Результат:
    verify(vehicleOptionsUseCase, only()).setSelectedVehicleOptions(Arrays.asList(
        new OptionBoolean(1, "name", "description", true, false),
        new OptionBoolean(2, "emacs", "descriptions", true, true)
    ));
  }

  /* Тетсируем переключение состояний. */

  /**
   * Должен вернуть исходное состояние вида изначально.
   *
   * @throws Exception error
   */
  @Test
  public void setInitialViewStateToLiveData() throws Exception {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);

    // Действие:
    vehicleOptionsViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(VehicleOptionsViewStateInitial.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида "Готово" со списком опций ТС.
   *
   * @throws Exception error
   */
  @Test
  public void setReadyViewStateToLiveData() throws Exception {
    // Дано:
    PublishSubject<List<Option>> publishSubject = PublishSubject.create();
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(vehicleOptionsUseCase.getVehicleOptions()).thenReturn(publishSubject);
    vehicleOptionsViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(Arrays.asList(
        new OptionBoolean(1, "name", "description", true, false),
        new OptionBoolean(2, "emacs", "descriptions", true, true),
        new OptionNumeric(3, "names", "desc", true, 5, 0, 10),
        new OptionNumeric(4, "nam", "script", true, 1, -1, 2)
    ));

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(VehicleOptionsViewStateInitial.class));
    inOrder.verify(viewStateObserver).onChanged(new VehicleOptionsViewStateReady(Arrays.asList(
        new VehicleOptionsListItem<>(
            new OptionBoolean(1, "name", "description", true, false)
        ),
        new VehicleOptionsListItem<>(
            new OptionBoolean(2, "emacs", "descriptions", true, true)
        ),
        new VehicleOptionsListItem<>(
            new OptionNumeric(3, "names", "desc", true, 5, 0, 10)
        ),
        new VehicleOptionsListItem<>(
            new OptionNumeric(4, "nam", "script", true, 1, -1, 2)
        )
    )));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида "В процессе".
   *
   * @throws Exception error
   */
  @Test
  public void setPendingViewStateToLiveData() throws Exception {
    // Дано:
    PublishSubject<List<Option>> publishSubject = PublishSubject.create();
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(vehicleOptionsUseCase.getVehicleOptions()).thenReturn(publishSubject);
    when(vehicleOptionsUseCase.setSelectedVehicleOptions(anyList()))
        .thenReturn(Completable.complete());
    vehicleOptionsViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(Arrays.asList(
        new OptionBoolean(1, "name", "description", true, false),
        new OptionBoolean(2, "emacs", "descriptions", true, true),
        new OptionNumeric(3, "names", "desc", true, 5, 0, 10),
        new OptionNumeric(4, "nam", "script", true, 1, -1, 2)
    ));
    vehicleOptionsViewModel.setVehicleOptions(new ArrayList<>());

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(VehicleOptionsViewStateInitial.class));
    inOrder.verify(viewStateObserver).onChanged(new VehicleOptionsViewStateReady(Arrays.asList(
        new VehicleOptionsListItem<>(
            new OptionBoolean(1, "name", "description", true, false)
        ),
        new VehicleOptionsListItem<>(
            new OptionBoolean(2, "emacs", "descriptions", true, true)
        ),
        new VehicleOptionsListItem<>(
            new OptionNumeric(3, "names", "desc", true, 5, 0, 10)
        ),
        new VehicleOptionsListItem<>(
            new OptionNumeric(4, "nam", "script", true, 1, -1, 2)
        )
    )));
    inOrder.verify(viewStateObserver).onChanged(any(VehicleOptionsViewStatePending.class));
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
    PublishSubject<List<Option>> publishSubject = PublishSubject.create();
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(vehicleOptionsUseCase.getVehicleOptions()).thenReturn(publishSubject);
    when(vehicleOptionsUseCase.setSelectedVehicleOptions(anyList()))
        .thenReturn(Completable.error(NoNetworkException::new));
    vehicleOptionsViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(Arrays.asList(
        new OptionBoolean(1, "name", "description", true, false),
        new OptionBoolean(2, "emacs", "descriptions", true, true),
        new OptionNumeric(3, "names", "desc", true, 5, 0, 10),
        new OptionNumeric(4, "nam", "script", true, 1, -1, 2)
    ));
    vehicleOptionsViewModel.setVehicleOptions(new ArrayList<>());

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(VehicleOptionsViewStateInitial.class));
    inOrder.verify(viewStateObserver).onChanged(new VehicleOptionsViewStateReady(Arrays.asList(
        new VehicleOptionsListItem<>(
            new OptionBoolean(1, "name", "description", true, false)
        ),
        new VehicleOptionsListItem<>(
            new OptionBoolean(2, "emacs", "descriptions", true, true)
        ),
        new VehicleOptionsListItem<>(
            new OptionNumeric(3, "names", "desc", true, 5, 0, 10)
        ),
        new VehicleOptionsListItem<>(
            new OptionNumeric(4, "nam", "script", true, 1, -1, 2)
        )
    )));
    inOrder.verify(viewStateObserver).onChanged(any(VehicleOptionsViewStatePending.class));
    inOrder.verify(viewStateObserver)
        .onChanged(new VehicleOptionsViewStateError(R.string.no_network_connection));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /* Тетсируем навигацию. */

  /**
   * Должен игнорировать ошибки.
   *
   * @throws Exception error
   */
  @Test
  public void setNothingToLiveData() throws Exception {
    // Дано:
    when(vehicleOptionsUseCase.setSelectedVehicleOptions(anyList()))
        .thenReturn(Completable.error(new NoNetworkException()));
    vehicleOptionsViewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Действие:
    vehicleOptionsViewModel.setVehicleOptions(new ArrayList<>());

    // Результат:
    verifyZeroInteractions(navigateObserver);
  }

  /**
   * Должен вернуть "перейти к ожиданию заказов" если занятие ТС было успешным.
   *
   * @throws Exception error
   */
  @Test
  public void setNavigateToReadyForOrdersToLiveData() throws Exception {
    // Дано:
    when(vehicleOptionsUseCase.setSelectedVehicleOptions(anyList()))
        .thenReturn(Completable.complete());
    vehicleOptionsViewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Действие:
    vehicleOptionsViewModel.setVehicleOptions(new ArrayList<>());

    // Результат:
    verify(navigateObserver, only()).onChanged(VehicleOptionsNavigate.READY_FOR_ORDERS);
  }
}