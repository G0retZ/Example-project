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
import com.fasten.executor_driver.entity.NoVehicleOptionsAvailableException;
import com.fasten.executor_driver.entity.VehicleOption;
import com.fasten.executor_driver.entity.VehicleOptionBoolean;
import com.fasten.executor_driver.entity.VehicleOptionNumeric;
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
public class VehicleOptionsViewModelTest {

  private VehicleOptionsViewModel vehicleOptionsViewModel;

  @Rule
  public TestRule rule = new InstantTaskExecutorRule();

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
            new VehicleOptionBoolean(1, "name", true, false)
        ),
        new VehicleOptionsListItem<>(
            new VehicleOptionBoolean(2, "emacs", true, true)
        )
    ));
    vehicleOptionsViewModel.setVehicleOptions(Arrays.asList(
        new VehicleOptionsListItem<>(
            new VehicleOptionBoolean(1, "name", true, false)
        ),
        new VehicleOptionsListItem<>(
            new VehicleOptionBoolean(2, "emacs", true, true)
        ),
        new VehicleOptionsListItem<>(
            new VehicleOptionNumeric(3, "names", true, 5, 0, 10)
        )
    ));
    vehicleOptionsViewModel.setVehicleOptions(Arrays.asList(
        new VehicleOptionsListItem<>(
            new VehicleOptionBoolean(1, "name", true, false)
        ),
        new VehicleOptionsListItem<>(
            new VehicleOptionBoolean(2, "emacs", true, true)
        ),
        new VehicleOptionsListItem<>(
            new VehicleOptionNumeric(3, "names", true, 5, 0, 10)
        ),
        new VehicleOptionsListItem<>(
            new VehicleOptionNumeric(4, "nam", false, 1, -1, 2)
        )
    ));

    // Результат:
    verify(vehicleOptionsUseCase).setSelectedVehicleOptions(Arrays.asList(
        new VehicleOptionBoolean(1, "name", true, false),
        new VehicleOptionBoolean(2, "emacs", true, true)
    ));
    verify(vehicleOptionsUseCase).setSelectedVehicleOptions(Arrays.asList(
        new VehicleOptionBoolean(1, "name", true, false),
        new VehicleOptionBoolean(2, "emacs", true, true),
        new VehicleOptionNumeric(3, "names", true, 5, 0, 10)
    ));
    verify(vehicleOptionsUseCase).setSelectedVehicleOptions(Arrays.asList(
        new VehicleOptionBoolean(1, "name", true, false),
        new VehicleOptionBoolean(2, "emacs", true, true),
        new VehicleOptionNumeric(3, "names", true, 5, 0, 10),
        new VehicleOptionNumeric(4, "nam", false, 1, -1, 2)
    ));
    verifyNoMoreInteractions(vehicleOptionsUseCase);
  }

  /**
   * Должен автоматом попросить юзкейс занять ТС по без настроек.
   *
   * @throws Exception error
   */
  @Test
  public void askVehicleOptionsUseCaseToOccupyVehicleWithoutOptions() throws Exception {
    // Дано:
    when(vehicleOptionsUseCase.getVehicleOptions())
        .thenReturn(Observable.error(NoVehicleOptionsAvailableException::new));

    // Действие:
    vehicleOptionsViewModel.getViewStateLiveData();

    // Результат:
    verify(vehicleOptionsUseCase).getVehicleOptions();
    verify(vehicleOptionsUseCase).setSelectedVehicleOptions(new ArrayList<>());
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
            new VehicleOptionBoolean(1, "name", true, false)
        ),
        new VehicleOptionsListItem<>(
            new VehicleOptionBoolean(2, "emacs", true, true)
        )
    ));
    vehicleOptionsViewModel.setVehicleOptions(Arrays.asList(
        new VehicleOptionsListItem<>(
            new VehicleOptionBoolean(1, "name", true, false)
        ),
        new VehicleOptionsListItem<>(
            new VehicleOptionBoolean(2, "emacs", true, true)
        ),
        new VehicleOptionsListItem<>(
            new VehicleOptionNumeric(3, "names", true, 5, 0, 10)
        )
    ));
    vehicleOptionsViewModel.setVehicleOptions(Arrays.asList(
        new VehicleOptionsListItem<>(
            new VehicleOptionBoolean(1, "name", true, false)
        ),
        new VehicleOptionsListItem<>(
            new VehicleOptionBoolean(2, "emacs", true, true)
        ),
        new VehicleOptionsListItem<>(
            new VehicleOptionNumeric(3, "names", true, 5, 0, 10)
        ),
        new VehicleOptionsListItem<>(
            new VehicleOptionNumeric(4, "nam", false, 1, -1, 2)
        )
    ));

    // Результат:
    verify(vehicleOptionsUseCase, only()).setSelectedVehicleOptions(Arrays.asList(
        new VehicleOptionBoolean(1, "name", true, false),
        new VehicleOptionBoolean(2, "emacs", true, true)
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
    PublishSubject<List<VehicleOption>> publishSubject = PublishSubject.create();
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(vehicleOptionsUseCase.getVehicleOptions()).thenReturn(publishSubject);
    vehicleOptionsViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(Arrays.asList(
        new VehicleOptionBoolean(1, "name", true, false),
        new VehicleOptionBoolean(2, "emacs", true, true),
        new VehicleOptionNumeric(3, "names", true, 5, 0, 10),
        new VehicleOptionNumeric(4, "nam", true, 1, -1, 2)
    ));

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(VehicleOptionsViewStateInitial.class));
    inOrder.verify(viewStateObserver).onChanged(new VehicleOptionsViewStateReady(Arrays.asList(
        new VehicleOptionsListItem<>(
            new VehicleOptionBoolean(1, "name", true, false)
        ),
        new VehicleOptionsListItem<>(
            new VehicleOptionBoolean(2, "emacs", true, true)
        ),
        new VehicleOptionsListItem<>(
            new VehicleOptionNumeric(3, "names", true, 5, 0, 10)
        ),
        new VehicleOptionsListItem<>(
            new VehicleOptionNumeric(4, "nam", true, 1, -1, 2)
        )
    )));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида "В процессе" автоматом, если не было опций для изменений.
   *
   * @throws Exception error
   */
  @Test
  public void setPendingViewStateToLiveDataAutomatically() throws Exception {
    // Дано:
    PublishSubject<List<VehicleOption>> publishSubject = PublishSubject.create();
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(vehicleOptionsUseCase.getVehicleOptions()).thenReturn(publishSubject);
    vehicleOptionsViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onError(new NoVehicleOptionsAvailableException());

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(VehicleOptionsViewStateInitial.class));
    inOrder.verify(viewStateObserver).onChanged(any(VehicleOptionsViewStatePending.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида "В процессе" автоматом без начального состояния, если не было
   * опций для изменений, и ответ был синхронным.
   *
   * @throws Exception error
   */
  @Test
  public void setSyncPendingViewStateToLiveDataAutomatically() throws Exception {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(vehicleOptionsUseCase.getVehicleOptions())
        .thenReturn(Observable.error(NoVehicleOptionsAvailableException::new));

    // Действие:
    vehicleOptionsViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(VehicleOptionsViewStatePending.class));
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
    PublishSubject<List<VehicleOption>> publishSubject = PublishSubject.create();
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(vehicleOptionsUseCase.getVehicleOptions()).thenReturn(publishSubject);
    when(vehicleOptionsUseCase.setSelectedVehicleOptions(anyList()))
        .thenReturn(Completable.complete());
    vehicleOptionsViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(Arrays.asList(
        new VehicleOptionBoolean(1, "name", true, false),
        new VehicleOptionBoolean(2, "emacs", true, true),
        new VehicleOptionNumeric(3, "names", true, 5, 0, 10),
        new VehicleOptionNumeric(4, "nam", false, 1, -1, 2)
    ));
    vehicleOptionsViewModel.setVehicleOptions(new ArrayList<>());

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(VehicleOptionsViewStateInitial.class));
    inOrder.verify(viewStateObserver).onChanged(new VehicleOptionsViewStateReady(Arrays.asList(
        new VehicleOptionsListItem<>(
            new VehicleOptionBoolean(1, "name", true, false)
        ),
        new VehicleOptionsListItem<>(
            new VehicleOptionBoolean(2, "emacs", true, true)
        ),
        new VehicleOptionsListItem<>(
            new VehicleOptionNumeric(3, "names", true, 5, 0, 10)
        ),
        new VehicleOptionsListItem<>(
            new VehicleOptionNumeric(4, "nam", false, 1, -1, 2)
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
    PublishSubject<List<VehicleOption>> publishSubject = PublishSubject.create();
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(vehicleOptionsUseCase.getVehicleOptions()).thenReturn(publishSubject);
    when(vehicleOptionsUseCase.setSelectedVehicleOptions(anyList()))
        .thenReturn(Completable.error(NoNetworkException::new));
    vehicleOptionsViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(Arrays.asList(
        new VehicleOptionBoolean(1, "name", true, false),
        new VehicleOptionBoolean(2, "emacs", true, true),
        new VehicleOptionNumeric(3, "names", true, 5, 0, 10),
        new VehicleOptionNumeric(4, "nam", false, 1, -1, 2)
    ));
    vehicleOptionsViewModel.setVehicleOptions(new ArrayList<>());

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(VehicleOptionsViewStateInitial.class));
    inOrder.verify(viewStateObserver).onChanged(new VehicleOptionsViewStateReady(Arrays.asList(
        new VehicleOptionsListItem<>(
            new VehicleOptionBoolean(1, "name", true, false)
        ),
        new VehicleOptionsListItem<>(
            new VehicleOptionBoolean(2, "emacs", true, true)
        ),
        new VehicleOptionsListItem<>(
            new VehicleOptionNumeric(3, "names", true, 5, 0, 10)
        ),
        new VehicleOptionsListItem<>(
            new VehicleOptionNumeric(4, "nam", false, 1, -1, 2)
        )
    )));
    inOrder.verify(viewStateObserver).onChanged(any(VehicleOptionsViewStatePending.class));
    inOrder.verify(viewStateObserver)
        .onChanged(new VehicleOptionsViewStateError(R.string.no_network_connection));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /* Тетсируем навигацию. */

  /**
   * Должен вернуть "перейти к ожиданию заказов автоматически" если была ошибка "нет опций для
   * изменений".
   *
   * @throws Exception error
   */
  @Test
  public void setNavigateAutoReadyForOrdersToLiveData() throws Exception {
    // Дано:
    PublishSubject<List<VehicleOption>> publishSubject = PublishSubject.create();
    when(vehicleOptionsUseCase.getVehicleOptions()).thenReturn(publishSubject);
    when(vehicleOptionsUseCase.setSelectedVehicleOptions(anyList()))
        .thenReturn(Completable.complete());
    vehicleOptionsViewModel.getViewStateLiveData();
    vehicleOptionsViewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Действие:
    publishSubject.onError(new NoVehicleOptionsAvailableException());

    // Результат:
    verify(navigateObserver, only()).onChanged(VehicleOptionsNavigate.READY_FOR_ORDERS);
  }

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