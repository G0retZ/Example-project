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
import io.reactivex.Single;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.SingleSubject;
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

@RunWith(MockitoJUnitRunner.class)
public class VehicleVehicleOptionsViewModelTest {

  @Rule
  public TestRule rule = new InstantTaskExecutorRule();
  private VehicleOptionsViewModel vehicleVehicleOptionsViewModel;
  @Mock
  private VehicleOptionsUseCase vehicleOptionsUseCase;

  @Mock
  private Observer<ViewState<VehicleOptionsViewActions>> viewStateObserver;

  @Mock
  private Observer<String> navigateObserver;

  @Before
  public void setUp() {
    RxJavaPlugins.setSingleSchedulerHandler(scheduler -> Schedulers.trampoline());
    RxAndroidPlugins.setInitMainThreadSchedulerHandler(scheduler -> Schedulers.trampoline());
    when(vehicleOptionsUseCase.getVehicleOptions()).thenReturn(Observable.never());
    when(vehicleOptionsUseCase.getDriverOptions()).thenReturn(Single.never());
    when(vehicleOptionsUseCase.setSelectedVehicleAndOptions(anyList(), anyList()))
        .thenReturn(Completable.never());
    vehicleVehicleOptionsViewModel = new VehicleOptionsViewModelImpl(vehicleOptionsUseCase);
  }

  /* Тетсируем работу с юзкейсом выбора опций ТС. */

  /**
   * Должен просить юзкейс получить список опций ТС, при первой и только при первой подписке.
   */
  @Test
  public void askVehicleOptionsUseCaseForOptionsInitially() {
    // Дано:
    when(vehicleOptionsUseCase.getVehicleOptions()).thenReturn(Observable.just(
        Arrays.asList(
            new OptionBoolean(1, "name", "description", true, false),
            new OptionBoolean(2, "emacs", "descriptions", true, true)
        )
    ));
    when(vehicleOptionsUseCase.getDriverOptions()).thenReturn(Single.just(
        Arrays.asList(
            new OptionNumeric(3, "names", "desc", true, 5, 0, 10),
            new OptionNumeric(4, "nam", "script", true, 1, -1, 2)
        )
    ));

    // Действие:
    vehicleVehicleOptionsViewModel.getViewStateLiveData();
    vehicleVehicleOptionsViewModel.getViewStateLiveData();
    vehicleVehicleOptionsViewModel.getViewStateLiveData();

    // Результат:
    verify(vehicleOptionsUseCase).getVehicleOptions();
    verify(vehicleOptionsUseCase).getDriverOptions();
    verifyNoMoreInteractions(vehicleOptionsUseCase);
  }

  /**
   * Должен попросить юзкейс занять ТС с указанными настройкам.
   */
  @Test
  public void askVehicleOptionsUseCaseToOccupyVehicleWithOptions() {
    // Дано:
    when(vehicleOptionsUseCase.setSelectedVehicleAndOptions(anyList(), anyList()))
        .thenReturn(Completable.complete());

    // Действие:
    vehicleVehicleOptionsViewModel.setOptions(new VehicleOptionsListItems(
        Collections.singletonList(
            new VehicleOptionsListItem<>(
                new OptionBoolean(1, "name", "description", true, false)
            )
        ), Collections.singletonList(
        new VehicleOptionsListItem<>(
            new OptionNumeric(3, "names", "description", true, 5, 0, 10)
        )
    )));
    vehicleVehicleOptionsViewModel.setOptions(new VehicleOptionsListItems(Arrays.asList(
        new VehicleOptionsListItem<>(
            new OptionBoolean(1, "name", "description", true, false)
        ),
        new VehicleOptionsListItem<>(
            new OptionBoolean(2, "emacs", "description", true, true)
        )
    ), Collections.singletonList(
        new VehicleOptionsListItem<>(
            new OptionNumeric(3, "names", "description", true, 5, 0, 10)
        )
    )));
    vehicleVehicleOptionsViewModel.setOptions(new VehicleOptionsListItems(Arrays.asList(
        new VehicleOptionsListItem<>(
            new OptionBoolean(1, "name", "description", true, false)
        ),
        new VehicleOptionsListItem<>(
            new OptionBoolean(2, "emacs", "description", true, true)
        )
    ), Arrays.asList(
        new VehicleOptionsListItem<>(
            new OptionNumeric(3, "names", "description", true, 5, 0, 10)
        ),
        new VehicleOptionsListItem<>(
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
   */
  @Test
  public void DoNotTouchVehicleOptionsUseCaseDuringVehicleOccupying() {
    // Действие:
    vehicleVehicleOptionsViewModel.setOptions(new VehicleOptionsListItems(
        Collections.singletonList(
            new VehicleOptionsListItem<>(
                new OptionBoolean(1, "name", "description", true, false)
            )
        ), Collections.singletonList(
        new VehicleOptionsListItem<>(
            new OptionNumeric(3, "names", "description", true, 5, 0, 10)
        )
    )));
    vehicleVehicleOptionsViewModel.setOptions(new VehicleOptionsListItems(Arrays.asList(
        new VehicleOptionsListItem<>(
            new OptionBoolean(1, "name", "description", true, false)
        ),
        new VehicleOptionsListItem<>(
            new OptionBoolean(2, "emacs", "description", true, true)
        )
    ), Collections.singletonList(
        new VehicleOptionsListItem<>(
            new OptionNumeric(3, "names", "description", true, 5, 0, 10)
        )
    )));
    vehicleVehicleOptionsViewModel.setOptions(new VehicleOptionsListItems(Arrays.asList(
        new VehicleOptionsListItem<>(
            new OptionBoolean(1, "name", "description", true, false)
        ),
        new VehicleOptionsListItem<>(
            new OptionBoolean(2, "emacs", "description", true, true)
        )
    ), Arrays.asList(
        new VehicleOptionsListItem<>(
            new OptionNumeric(3, "names", "description", true, 5, 0, 10)
        ),
        new VehicleOptionsListItem<>(
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
   */
  @Test
  public void setInitialViewStateToLiveData() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);

    // Действие:
    vehicleVehicleOptionsViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(VehicleOptionsViewStateInitial.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояния вида "Готово" со списком опций ТС.
   */
  @Test
  public void setReadyViewStateToLiveData() {
    // Дано:
    PublishSubject<List<Option>> publishSubject = PublishSubject.create();
    SingleSubject<List<Option>> singleSubject = SingleSubject.create();
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(vehicleOptionsUseCase.getVehicleOptions()).thenReturn(publishSubject);
    when(vehicleOptionsUseCase.getDriverOptions()).thenReturn(singleSubject);
    vehicleVehicleOptionsViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(Arrays.asList(
        new OptionBoolean(1, "name", "description", true, false),
        new OptionBoolean(2, "emacs", "descriptions", true, true)
    ));
    singleSubject.onSuccess(Arrays.asList(
        new OptionNumeric(3, "names", "desc", true, 5, 0, 10),
        new OptionNumeric(4, "nam", "script", true, 1, -1, 2)
    ));
    publishSubject.onNext(Arrays.asList(
        new OptionBoolean(5, "name1", "description1", true, true),
        new OptionBoolean(6, "emacs1", "descriptions1", true, false)
    ));
    publishSubject.onNext(Arrays.asList(
        new OptionBoolean(7, "name2", "description2", true, true),
        new OptionBoolean(8, "emacs2", "descriptions2", true, false)
    ));

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(VehicleOptionsViewStateInitial.class));
    inOrder.verify(viewStateObserver).onChanged(new VehicleOptionsViewStateReady(
        new VehicleOptionsListItems(Arrays.asList(
            new VehicleOptionsListItem<>(
                new OptionBoolean(1, "name", "description", true, false)
            ),
            new VehicleOptionsListItem<>(
                new OptionBoolean(2, "emacs", "descriptions", true, true)
            )
        ), Arrays.asList(
            new VehicleOptionsListItem<>(
                new OptionNumeric(3, "names", "desc", true, 5, 0, 10)
            ),
            new VehicleOptionsListItem<>(
                new OptionNumeric(4, "nam", "script", true, 1, -1, 2)
            )
        ))
    ));
    inOrder.verify(viewStateObserver).onChanged(new VehicleOptionsViewStateReady(
        new VehicleOptionsListItems(Arrays.asList(
            new VehicleOptionsListItem<>(
                new OptionBoolean(5, "name1", "description1", true, true)
            ),
            new VehicleOptionsListItem<>(
                new OptionBoolean(6, "emacs1", "descriptions1", true, false)
            )
        ), Arrays.asList(
            new VehicleOptionsListItem<>(
                new OptionNumeric(3, "names", "desc", true, 5, 0, 10)
            ),
            new VehicleOptionsListItem<>(
                new OptionNumeric(4, "nam", "script", true, 1, -1, 2)
            )
        ))
    ));
    inOrder.verify(viewStateObserver).onChanged(new VehicleOptionsViewStateReady(
        new VehicleOptionsListItems(Arrays.asList(
            new VehicleOptionsListItem<>(
                new OptionBoolean(7, "name2", "description2", true, true)
            ),
            new VehicleOptionsListItem<>(
                new OptionBoolean(8, "emacs2", "descriptions2", true, false)
            )
        ), Arrays.asList(
            new VehicleOptionsListItem<>(
                new OptionNumeric(3, "names", "desc", true, 5, 0, 10)
            ),
            new VehicleOptionsListItem<>(
                new OptionNumeric(4, "nam", "script", true, 1, -1, 2)
            )
        ))
    ));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида "В процессе".
   */
  @Test
  public void setPendingViewStateToLiveData() {
    // Дано:
    PublishSubject<List<Option>> publishSubject = PublishSubject.create();
    SingleSubject<List<Option>> singleSubject = SingleSubject.create();
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(vehicleOptionsUseCase.getVehicleOptions()).thenReturn(publishSubject);
    when(vehicleOptionsUseCase.getDriverOptions()).thenReturn(singleSubject);
    when(vehicleOptionsUseCase.setSelectedVehicleAndOptions(anyList(), anyList()))
        .thenReturn(Completable.complete());
    vehicleVehicleOptionsViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(Arrays.asList(
        new OptionBoolean(1, "name", "description", true, false),
        new OptionBoolean(2, "emacs", "descriptions", true, true)
    ));
    singleSubject.onSuccess(Arrays.asList(
        new OptionNumeric(3, "names", "desc", true, 5, 0, 10),
        new OptionNumeric(4, "nam", "script", true, 1, -1, 2)
    ));
    vehicleVehicleOptionsViewModel.setOptions(
        new VehicleOptionsListItems(new ArrayList<>(), new ArrayList<>())
    );

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(VehicleOptionsViewStateInitial.class));
    inOrder.verify(viewStateObserver).onChanged(new VehicleOptionsViewStateReady(
        new VehicleOptionsListItems(Arrays.asList(
            new VehicleOptionsListItem<>(
                new OptionBoolean(1, "name", "description", true, false)
            ),
            new VehicleOptionsListItem<>(
                new OptionBoolean(2, "emacs", "descriptions", true, true)
            )
        ), Arrays.asList(
            new VehicleOptionsListItem<>(
                new OptionNumeric(3, "names", "desc", true, 5, 0, 10)
            ),
            new VehicleOptionsListItem<>(
                new OptionNumeric(4, "nam", "script", true, 1, -1, 2)
            )
        ))
    ));
    inOrder.verify(viewStateObserver).onChanged(any(VehicleOptionsViewStatePending.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида "Ошибка" сети.
   */
  @Test
  public void setNetworkErrorViewStateToLiveData() {
    // Дано:
    PublishSubject<List<Option>> publishSubject = PublishSubject.create();
    SingleSubject<List<Option>> singleSubject = SingleSubject.create();
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(vehicleOptionsUseCase.getVehicleOptions()).thenReturn(publishSubject);
    when(vehicleOptionsUseCase.getDriverOptions()).thenReturn(singleSubject);
    when(vehicleOptionsUseCase.setSelectedVehicleAndOptions(anyList(), anyList()))
        .thenReturn(Completable.error(NoNetworkException::new));
    vehicleVehicleOptionsViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(Arrays.asList(
        new OptionBoolean(1, "name", "description", true, false),
        new OptionBoolean(2, "emacs", "descriptions", true, true)
    ));
    singleSubject.onSuccess(Arrays.asList(
        new OptionNumeric(3, "names", "desc", true, 5, 0, 10),
        new OptionNumeric(4, "nam", "script", true, 1, -1, 2)
    ));
    vehicleVehicleOptionsViewModel.setOptions(
        new VehicleOptionsListItems(new ArrayList<>(), new ArrayList<>())
    );

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(VehicleOptionsViewStateInitial.class));
    inOrder.verify(viewStateObserver).onChanged(new VehicleOptionsViewStateReady(
        new VehicleOptionsListItems(Arrays.asList(
            new VehicleOptionsListItem<>(
                new OptionBoolean(1, "name", "description", true, false)
            ),
            new VehicleOptionsListItem<>(
                new OptionBoolean(2, "emacs", "descriptions", true, true)
            )
        ), Arrays.asList(
            new VehicleOptionsListItem<>(
                new OptionNumeric(3, "names", "desc", true, 5, 0, 10)
            ),
            new VehicleOptionsListItem<>(
                new OptionNumeric(4, "nam", "script", true, 1, -1, 2)
            )
        ))
    ));
    inOrder.verify(viewStateObserver).onChanged(any(VehicleOptionsViewStatePending.class));
    inOrder.verify(viewStateObserver)
        .onChanged(new VehicleOptionsViewStateError(R.string.no_network_connection));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /* Тетсируем навигацию. */

  /**
   * Должен игнорировать ошибки.
   */
  @Test
  public void setNothingToLiveData() {
    // Дано:
    when(vehicleOptionsUseCase.setSelectedVehicleAndOptions(anyList(), anyList()))
        .thenReturn(Completable.error(new NoNetworkException()));
    vehicleVehicleOptionsViewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Действие:
    vehicleVehicleOptionsViewModel.setOptions(
        new VehicleOptionsListItems(new ArrayList<>(), new ArrayList<>())
    );

    // Результат:
    verifyZeroInteractions(navigateObserver);
  }

  /**
   * Должен вернуть "перейти к ожиданию заказов" если занятие ТС было успешным.
   */
  @Test
  public void setNavigateToReadyForOrdersToLiveData() {
    // Дано:
    when(vehicleOptionsUseCase.setSelectedVehicleAndOptions(anyList(), anyList()))
        .thenReturn(Completable.complete());
    vehicleVehicleOptionsViewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Действие:
    vehicleVehicleOptionsViewModel.setOptions(
        new VehicleOptionsListItems(new ArrayList<>(), new ArrayList<>())
    );

    // Результат:
    verify(navigateObserver, only()).onChanged(VehicleOptionsNavigate.SERVICES);
  }
}