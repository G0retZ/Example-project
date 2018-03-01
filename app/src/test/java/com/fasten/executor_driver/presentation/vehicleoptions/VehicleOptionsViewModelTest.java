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
import com.fasten.executor_driver.presentation.options.OptionsListItem;
import com.fasten.executor_driver.presentation.options.OptionsListItems;
import com.fasten.executor_driver.presentation.options.OptionsViewActions;
import com.fasten.executor_driver.presentation.options.OptionsViewModel;
import com.fasten.executor_driver.presentation.options.OptionsViewStateError;
import com.fasten.executor_driver.presentation.options.OptionsViewStateInitial;
import com.fasten.executor_driver.presentation.options.OptionsViewStatePending;
import com.fasten.executor_driver.presentation.options.OptionsViewStateReady;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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

// TODO: написать недостающие тесты.
@RunWith(MockitoJUnitRunner.class)
public class VehicleOptionsViewModelTest {

  @Rule
  public TestRule rule = new InstantTaskExecutorRule();
  private OptionsViewModel vehicleOptionsViewModel;
  @Mock
  private VehicleOptionsUseCase vehicleOptionsUseCase;

  @Mock
  private Observer<ViewState<OptionsViewActions>> viewStateObserver;

  @Mock
  private Observer<String> navigateObserver;

  @Before
  public void setUp() throws Exception {
    RxJavaPlugins.setSingleSchedulerHandler(scheduler -> Schedulers.trampoline());
    RxAndroidPlugins.setInitMainThreadSchedulerHandler(scheduler -> Schedulers.trampoline());
    when(vehicleOptionsUseCase.getVehicleOptions()).thenReturn(Observable.never());
    when(vehicleOptionsUseCase.getDriverOptions()).thenReturn(Observable.never());
    when(vehicleOptionsUseCase.setSelectedVehicleAndOptions(anyList(), anyList()))
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
    verify(vehicleOptionsUseCase).getVehicleOptions();
    verify(vehicleOptionsUseCase).getDriverOptions();
    verifyNoMoreInteractions(vehicleOptionsUseCase);
  }

  /**
   * Должен попросить юзкейс занять ТС с указанными настройкам.
   *
   * @throws Exception error
   */
  @Test
  public void askVehicleOptionsUseCaseToOccupyVehicleWithOptions() throws Exception {
    // Дано:
    when(vehicleOptionsUseCase.setSelectedVehicleAndOptions(anyList(), anyList()))
        .thenReturn(Completable.complete());

    // Действие:
    vehicleOptionsViewModel.setOptions(new OptionsListItems(
        Collections.singletonList(
            new OptionsListItem<>(
                new OptionBoolean(1, "name", "description", true, false)
            )
        ), Collections.singletonList(
        new OptionsListItem<>(
            new OptionNumeric(3, "names", "description", true, 5, 0, 10)
        )
    )));
    vehicleOptionsViewModel.setOptions(new OptionsListItems(Arrays.asList(
        new OptionsListItem<>(
            new OptionBoolean(1, "name", "description", true, false)
        ),
        new OptionsListItem<>(
            new OptionBoolean(2, "emacs", "description", true, true)
        )
    ), Collections.singletonList(
        new OptionsListItem<>(
            new OptionNumeric(3, "names", "description", true, 5, 0, 10)
        )
    )));
    vehicleOptionsViewModel.setOptions(new OptionsListItems(Arrays.asList(
        new OptionsListItem<>(
            new OptionBoolean(1, "name", "description", true, false)
        ),
        new OptionsListItem<>(
            new OptionBoolean(2, "emacs", "description", true, true)
        )
    ), Arrays.asList(
        new OptionsListItem<>(
            new OptionNumeric(3, "names", "description", true, 5, 0, 10)
        ),
        new OptionsListItem<>(
            new OptionNumeric(4, "nam", "description", false, 1, -1, 2)
        )
    )));

    // Результат:
    verify(vehicleOptionsUseCase).setSelectedVehicleAndOptions(Collections.singletonList(
        new OptionBoolean(1, "name", "description", true, false)
    ), Collections.singletonList(
        new OptionNumeric(3, "names", "description", true, 5, 0, 10)
    ));
    verify(vehicleOptionsUseCase).setSelectedVehicleAndOptions(Arrays.asList(
        new OptionBoolean(1, "name", "description", true, false),
        new OptionBoolean(2, "emacs", "description", true, true)
    ), Collections.singletonList(
        new OptionNumeric(3, "names", "description", true, 5, 0, 10)
    ));
    verify(vehicleOptionsUseCase).setSelectedVehicleAndOptions(Arrays.asList(
        new OptionBoolean(1, "name", "description", true, false),
        new OptionBoolean(2, "emacs", "description", true, true)
    ), Arrays.asList(
        new OptionNumeric(3, "names", "description", true, 5, 0, 10),
        new OptionNumeric(4, "nam", "description", false, 1, -1, 2)
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
    vehicleOptionsViewModel.setOptions(new OptionsListItems(
        Collections.singletonList(
            new OptionsListItem<>(
                new OptionBoolean(1, "name", "description", true, false)
            )
        ), Collections.singletonList(
        new OptionsListItem<>(
            new OptionNumeric(3, "names", "description", true, 5, 0, 10)
        )
    )));
    vehicleOptionsViewModel.setOptions(new OptionsListItems(Arrays.asList(
        new OptionsListItem<>(
            new OptionBoolean(1, "name", "description", true, false)
        ),
        new OptionsListItem<>(
            new OptionBoolean(2, "emacs", "description", true, true)
        )
    ), Collections.singletonList(
        new OptionsListItem<>(
            new OptionNumeric(3, "names", "description", true, 5, 0, 10)
        )
    )));
    vehicleOptionsViewModel.setOptions(new OptionsListItems(Arrays.asList(
        new OptionsListItem<>(
            new OptionBoolean(1, "name", "description", true, false)
        ),
        new OptionsListItem<>(
            new OptionBoolean(2, "emacs", "description", true, true)
        )
    ), Arrays.asList(
        new OptionsListItem<>(
            new OptionNumeric(3, "names", "description", true, 5, 0, 10)
        ),
        new OptionsListItem<>(
            new OptionNumeric(4, "nam", "description", false, 1, -1, 2)
        )
    )));

    // Результат:
    verify(vehicleOptionsUseCase).setSelectedVehicleAndOptions(Collections.singletonList(
        new OptionBoolean(1, "name", "description", true, false)
    ), Collections.singletonList(
        new OptionNumeric(3, "names", "description", true, 5, 0, 10)
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
    inOrder.verify(viewStateObserver).onChanged(any(OptionsViewStateInitial.class));
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
    PublishSubject<List<Option>> publishSubject1 = PublishSubject.create();
    PublishSubject<List<Option>> publishSubject2 = PublishSubject.create();
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(vehicleOptionsUseCase.getVehicleOptions()).thenReturn(publishSubject1);
    when(vehicleOptionsUseCase.getDriverOptions()).thenReturn(publishSubject2);
    vehicleOptionsViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject1.onNext(Arrays.asList(
        new OptionBoolean(1, "name", "description", true, false),
        new OptionBoolean(2, "emacs", "descriptions", true, true)
    ));
    publishSubject2.onNext(Arrays.asList(
        new OptionNumeric(3, "names", "desc", true, 5, 0, 10),
        new OptionNumeric(4, "nam", "script", true, 1, -1, 2)
    ));

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(OptionsViewStateInitial.class));
    inOrder.verify(viewStateObserver).onChanged(new OptionsViewStateReady(
        new OptionsListItems(Arrays.asList(
            new OptionsListItem<>(
                new OptionBoolean(1, "name", "description", true, false)
            ),
            new OptionsListItem<>(
                new OptionBoolean(2, "emacs", "descriptions", true, true)
            )
        ), Arrays.asList(
            new OptionsListItem<>(
                new OptionNumeric(3, "names", "desc", true, 5, 0, 10)
            ),
            new OptionsListItem<>(
                new OptionNumeric(4, "nam", "script", true, 1, -1, 2)
            )
        ))
    ));
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
    PublishSubject<List<Option>> publishSubject1 = PublishSubject.create();
    PublishSubject<List<Option>> publishSubject2 = PublishSubject.create();
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(vehicleOptionsUseCase.getVehicleOptions()).thenReturn(publishSubject1);
    when(vehicleOptionsUseCase.getDriverOptions()).thenReturn(publishSubject2);
    when(vehicleOptionsUseCase.setSelectedVehicleAndOptions(anyList(), anyList()))
        .thenReturn(Completable.complete());
    vehicleOptionsViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject1.onNext(Arrays.asList(
        new OptionBoolean(1, "name", "description", true, false),
        new OptionBoolean(2, "emacs", "descriptions", true, true)
    ));
    publishSubject2.onNext(Arrays.asList(
        new OptionNumeric(3, "names", "desc", true, 5, 0, 10),
        new OptionNumeric(4, "nam", "script", true, 1, -1, 2)
    ));
    vehicleOptionsViewModel.setOptions(
        new OptionsListItems(new ArrayList<>(), new ArrayList<>())
    );

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(OptionsViewStateInitial.class));
    inOrder.verify(viewStateObserver).onChanged(new OptionsViewStateReady(
        new OptionsListItems(Arrays.asList(
            new OptionsListItem<>(
                new OptionBoolean(1, "name", "description", true, false)
            ),
            new OptionsListItem<>(
                new OptionBoolean(2, "emacs", "descriptions", true, true)
            )
        ), Arrays.asList(
            new OptionsListItem<>(
                new OptionNumeric(3, "names", "desc", true, 5, 0, 10)
            ),
            new OptionsListItem<>(
                new OptionNumeric(4, "nam", "script", true, 1, -1, 2)
            )
        ))
    ));
    inOrder.verify(viewStateObserver).onChanged(any(OptionsViewStatePending.class));
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
    PublishSubject<List<Option>> publishSubject1 = PublishSubject.create();
    PublishSubject<List<Option>> publishSubject2 = PublishSubject.create();
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(vehicleOptionsUseCase.getVehicleOptions()).thenReturn(publishSubject1);
    when(vehicleOptionsUseCase.getDriverOptions()).thenReturn(publishSubject2);
    when(vehicleOptionsUseCase.setSelectedVehicleAndOptions(anyList(), anyList()))
        .thenReturn(Completable.error(NoNetworkException::new));
    vehicleOptionsViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject1.onNext(Arrays.asList(
        new OptionBoolean(1, "name", "description", true, false),
        new OptionBoolean(2, "emacs", "descriptions", true, true)
    ));
    publishSubject2.onNext(Arrays.asList(
        new OptionNumeric(3, "names", "desc", true, 5, 0, 10),
        new OptionNumeric(4, "nam", "script", true, 1, -1, 2)
    ));
    vehicleOptionsViewModel.setOptions(
        new OptionsListItems(new ArrayList<>(), new ArrayList<>())
    );

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(OptionsViewStateInitial.class));
    inOrder.verify(viewStateObserver).onChanged(new OptionsViewStateReady(
        new OptionsListItems(Arrays.asList(
            new OptionsListItem<>(
                new OptionBoolean(1, "name", "description", true, false)
            ),
            new OptionsListItem<>(
                new OptionBoolean(2, "emacs", "descriptions", true, true)
            )
        ), Arrays.asList(
            new OptionsListItem<>(
                new OptionNumeric(3, "names", "desc", true, 5, 0, 10)
            ),
            new OptionsListItem<>(
                new OptionNumeric(4, "nam", "script", true, 1, -1, 2)
            )
        ))
    ));
    inOrder.verify(viewStateObserver).onChanged(any(OptionsViewStatePending.class));
    inOrder.verify(viewStateObserver)
        .onChanged(new OptionsViewStateError(R.string.no_network_connection));
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
    when(vehicleOptionsUseCase.setSelectedVehicleAndOptions(anyList(), anyList()))
        .thenReturn(Completable.error(new NoNetworkException()));
    vehicleOptionsViewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Действие:
    vehicleOptionsViewModel.setOptions(
        new OptionsListItems(new ArrayList<>(), new ArrayList<>())
    );

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
    when(vehicleOptionsUseCase.setSelectedVehicleAndOptions(anyList(), anyList()))
        .thenReturn(Completable.complete());
    vehicleOptionsViewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Действие:
    vehicleOptionsViewModel.setOptions(
        new OptionsListItems(new ArrayList<>(), new ArrayList<>())
    );

    // Результат:
    verify(navigateObserver, only()).onChanged(VehicleOptionsNavigate.SERVICES);
  }
}