package com.cargopull.executor_driver.presentation.vehicleoptions;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;

import com.cargopull.executor_driver.R;
import com.cargopull.executor_driver.ViewModelThreadTestRule;
import com.cargopull.executor_driver.backend.analytics.ErrorReporter;
import com.cargopull.executor_driver.backend.web.NoNetworkException;
import com.cargopull.executor_driver.entity.EmptyListException;
import com.cargopull.executor_driver.entity.Option;
import com.cargopull.executor_driver.entity.OptionBoolean;
import com.cargopull.executor_driver.entity.OptionNumeric;
import com.cargopull.executor_driver.gateway.DataMappingException;
import com.cargopull.executor_driver.interactor.services.ServicesUseCase;
import com.cargopull.executor_driver.interactor.vehicle.VehicleOptionsUseCase;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.SingleSubject;

@RunWith(MockitoJUnitRunner.class)
public class VehicleOptionsViewModelTest {

  @ClassRule
  public static final ViewModelThreadTestRule classRule = new ViewModelThreadTestRule();
  @Rule
  public TestRule rule = new InstantTaskExecutorRule();
  private VehicleOptionsViewModel viewModel;
  @Mock
  private ErrorReporter errorReporter;
  @Mock
  private VehicleOptionsUseCase vehicleOptionsUseCase;
  @Mock
  private ServicesUseCase servicesUseCase;

  @Mock
  private Observer<ViewState<VehicleOptionsViewActions>> viewStateObserver;

  @Mock
  private Observer<String> navigateObserver;

  private PublishSubject<List<Option>> publishSubject;
  private SingleSubject<List<Option>> singleSubject;

  @Before
  public void setUp() {
    publishSubject = PublishSubject.create();
    singleSubject = SingleSubject.create();
    when(vehicleOptionsUseCase.getVehicleOptions()).thenReturn(publishSubject);
    when(vehicleOptionsUseCase.getDriverOptions()).thenReturn(singleSubject);
    when(vehicleOptionsUseCase.setSelectedVehicleAndOptions(anyList(), anyList()))
        .thenReturn(Completable.never());
    when(servicesUseCase.autoAssignServices()).thenReturn(Completable.never());
    viewModel = new VehicleOptionsViewModelImpl(errorReporter, vehicleOptionsUseCase,
        servicesUseCase);
  }

  /* Проверяем отправку ошибок в репортер */

  /**
   * Должет отправить ошибку преобразования данных ТС.
   */
  @Test
  public void reportVehicleDataMappingError() {
    // Action:
    publishSubject.onError(new DataMappingException());

    // Effect:
    verify(errorReporter, only()).reportError(any(DataMappingException.class));
  }

  /**
   * Должет отправить ошибку преобразования данных исполнителя.
   */
  @Test
  public void reportDriverDataMappingError() {
    // Action:
    singleSubject.onError(new DataMappingException());

    // Effect:
    verify(errorReporter, only()).reportError(any(DataMappingException.class));
  }

  /**
   * Не должет отправлять ошибку данных ТС.
   */
  @Test
  public void doNotReportVehicleDataError() {
    // Action:
    publishSubject.onError(new Exception());

    // Effect:
    verifyNoInteractions(errorReporter);
  }

  /**
   * Не должет отправлять ошибку данных исполнителя.
   */
  @Test
  public void doNotReportDriverDataError() {
    // Action:
    singleSubject.onError(new Exception());

    // Effect:
    verifyNoInteractions(errorReporter);
  }

  /**
   * Должет отправить ошибку неверных данных.
   */
  @Test
  public void reportStateError() {
    // Given:
    when(vehicleOptionsUseCase.setSelectedVehicleAndOptions(anyList(), anyList()))
        .thenReturn(Completable.error(IllegalStateException::new));

    // Action:
    viewModel.setOptions(new VehicleOptionsListItems(Arrays.asList(
        new VehicleOptionsListItem<>(new OptionBoolean(1, "name", "description", false)),
        new VehicleOptionsListItem<>(new OptionBoolean(2, "emacs", "description", true))
    ), Arrays.asList(
        new VehicleOptionsListItem<>(new OptionNumeric(3, "names", "description", 5, 0, 10)),
        new VehicleOptionsListItem<>(new OptionNumeric(4, "nam", "description", 1, -1, 2))
    )));

    // Effect:
    verify(errorReporter, only()).reportError(any(IllegalStateException.class));
  }

  /**
   * Должен отправить ошибку сети.
   */
  @Test
  public void reportNoNetworkError() {
    // Given:
    when(vehicleOptionsUseCase.setSelectedVehicleAndOptions(anyList(), anyList()))
        .thenReturn(Completable.error(NoNetworkException::new));

    // Action:
    viewModel.setOptions(new VehicleOptionsListItems(Arrays.asList(
        new VehicleOptionsListItem<>(new OptionBoolean(1, "name", "description", false)),
        new VehicleOptionsListItem<>(new OptionBoolean(2, "emacs", "description", true))
    ), Arrays.asList(
        new VehicleOptionsListItem<>(new OptionNumeric(3, "names", "description", 5, 0, 10)),
        new VehicleOptionsListItem<>(new OptionNumeric(4, "nam", "description", 1, -1, 2))
    )));

    // Effect:
    verify(errorReporter, only()).reportError(any(NoNetworkException.class));
  }

  /**
   * Должен отправить ошибку аргумента.
   */
  @Test
  public void reportArgumentError() {
    // Given:
    when(vehicleOptionsUseCase.setSelectedVehicleAndOptions(anyList(), anyList()))
        .thenReturn(Completable.error(IllegalArgumentException::new));

    // Action:
    viewModel.setOptions(new VehicleOptionsListItems(Arrays.asList(
        new VehicleOptionsListItem<>(new OptionBoolean(1, "name", "description", false)),
        new VehicleOptionsListItem<>(new OptionBoolean(2, "emacs", "description", true))
    ), Arrays.asList(
        new VehicleOptionsListItem<>(new OptionNumeric(3, "names", "description", 5, 0, 10)),
        new VehicleOptionsListItem<>(new OptionNumeric(4, "nam", "description", 1, -1, 2))
    )));

    // Effect:
    verify(errorReporter, only()).reportError(any(IllegalArgumentException.class));
  }

  /**
   * Должен отправить ошибку пустого списка.
   */
  @Test
  public void reportEmptyListError() {
    // Given:
    when(vehicleOptionsUseCase.setSelectedVehicleAndOptions(anyList(), anyList()))
        .thenReturn(Completable.complete());
    when(servicesUseCase.autoAssignServices())
        .thenReturn(Completable.error(EmptyListException::new));

    // Action:
    viewModel.setOptions(new VehicleOptionsListItems(Arrays.asList(
        new VehicleOptionsListItem<>(new OptionBoolean(1, "name", "description", false)),
        new VehicleOptionsListItem<>(new OptionBoolean(2, "emacs", "description", true))
    ), Arrays.asList(
        new VehicleOptionsListItem<>(new OptionNumeric(3, "names", "description", 5, 0, 10)),
        new VehicleOptionsListItem<>(new OptionNumeric(4, "nam", "description", 1, -1, 2))
    )));

    // Effect:
    verify(errorReporter, only()).reportError(any(EmptyListException.class));
  }

  /**
   * Должен отправить ошибку сети.
   */
  @Test
  public void reportNoNetworkErrorAgain() {
    // Given:
    when(vehicleOptionsUseCase.setSelectedVehicleAndOptions(anyList(), anyList()))
        .thenReturn(Completable.complete());
    when(servicesUseCase.autoAssignServices())
        .thenReturn(Completable.error(NoNetworkException::new));

    // Action:
    viewModel.setOptions(new VehicleOptionsListItems(Arrays.asList(
        new VehicleOptionsListItem<>(new OptionBoolean(1, "name", "description", false)),
        new VehicleOptionsListItem<>(new OptionBoolean(2, "emacs", "description", true))
    ), Arrays.asList(
        new VehicleOptionsListItem<>(new OptionNumeric(3, "names", "description", 5, 0, 10)),
        new VehicleOptionsListItem<>(new OptionNumeric(4, "nam", "description", 1, -1, 2))
    )));

    // Effect:
    verify(errorReporter, only()).reportError(any(NoNetworkException.class));
  }

  /* Тетсируем работу с юзкейсом выбора опций ТС. */

  /**
   * Должен просить юзкейс получить список опций ТС изначально.
   */
  @Test
  public void askVehicleOptionsUseCaseForOptionsInitially() {
    // Effect:
    verify(vehicleOptionsUseCase).getVehicleOptions();
    verify(vehicleOptionsUseCase).getDriverOptions();
    verifyNoMoreInteractions(vehicleOptionsUseCase);
  }

  /**
   * Не должен трогать юзкейс при подписках.
   */
  @Test
  public void doNotTouchVehicleOptionsUseCaseOnSubscriptions() {
    // Given:
    publishSubject.onNext(Arrays.asList(
        new OptionBoolean(1, "name", "description", false),
        new OptionBoolean(2, "emacs", "descriptions", true)
    ));
    singleSubject.onSuccess(Arrays.asList(
        new OptionNumeric(3, "names", "desc", 5, 0, 10),
        new OptionNumeric(4, "nam", "script", 1, -1, 2)
    ));

    // Action:
    viewModel.getViewStateLiveData();
    viewModel.getNavigationLiveData();
    viewModel.getViewStateLiveData();

    // Effect:
    verify(vehicleOptionsUseCase).getVehicleOptions();
    verify(vehicleOptionsUseCase).getDriverOptions();
    verifyNoMoreInteractions(vehicleOptionsUseCase);
  }

  /**
   * Должен попросить юзкейс занять ТС с указанными настройкам.
   */
  @Test
  public void askVehicleOptionsUseCaseToOccupyVehicleWithOptions() {
    // Given:
    when(vehicleOptionsUseCase.setSelectedVehicleAndOptions(anyList(), anyList()))
        .thenReturn(Completable.complete());
    when(servicesUseCase.autoAssignServices()).thenReturn(Completable.complete());

    // Action:
    viewModel.setOptions(new VehicleOptionsListItems(
        Collections.singletonList(
            new VehicleOptionsListItem<>(new OptionBoolean(1, "name", "description", false))
        ), Collections.singletonList(
        new VehicleOptionsListItem<>(new OptionNumeric(3, "names", "description", 5, 0, 10))
    )));
    viewModel.setOptions(new VehicleOptionsListItems(Arrays.asList(
        new VehicleOptionsListItem<>(new OptionBoolean(1, "name", "description", false)),
        new VehicleOptionsListItem<>(new OptionBoolean(2, "emacs", "description", true))
    ), Collections.singletonList(
        new VehicleOptionsListItem<>(new OptionNumeric(3, "names", "description", 5, 0, 10))
    )));
    viewModel.setOptions(new VehicleOptionsListItems(Arrays.asList(
        new VehicleOptionsListItem<>(new OptionBoolean(1, "name", "description", false)),
        new VehicleOptionsListItem<>(new OptionBoolean(2, "emacs", "description", true))
    ), Arrays.asList(
        new VehicleOptionsListItem<>(new OptionNumeric(3, "names", "description", 5, 0, 10)),
        new VehicleOptionsListItem<>(new OptionNumeric(4, "nam", "description", 1, -1, 2))
    )));

    // Effect:
    verify(vehicleOptionsUseCase).setSelectedVehicleAndOptions(Collections.singletonList(
        new OptionBoolean(1, "name", "description", false)
    ), Collections.singletonList(
        new OptionNumeric(3, "names", "description", 5, 0, 10)
    ));
    verify(vehicleOptionsUseCase).setSelectedVehicleAndOptions(Arrays.asList(
        new OptionBoolean(1, "name", "description", false),
        new OptionBoolean(2, "emacs", "description", true)
    ), Collections.singletonList(
        new OptionNumeric(3, "names", "description", 5, 0, 10)
    ));
    verify(vehicleOptionsUseCase).setSelectedVehicleAndOptions(Arrays.asList(
        new OptionBoolean(1, "name", "description", false),
        new OptionBoolean(2, "emacs", "description", true)
    ), Arrays.asList(
        new OptionNumeric(3, "names", "description", 5, 0, 10),
        new OptionNumeric(4, "nam", "description", 1, -1, 2)
    ));
    verify(vehicleOptionsUseCase).getVehicleOptions();
    verify(vehicleOptionsUseCase).getDriverOptions();
    verifyNoMoreInteractions(vehicleOptionsUseCase);
  }

  /**
   * Не должен трогать юзкейс, если предыдущий запрос занятия ТС еще не завершился.
   */
  @Test
  public void DoNotTouchVehicleOptionsUseCaseDuringVehicleOccupying() {
    // Action:
    viewModel.setOptions(new VehicleOptionsListItems(
        Collections.singletonList(
            new VehicleOptionsListItem<>(new OptionBoolean(1, "name", "description", false))
        ), Collections.singletonList(
        new VehicleOptionsListItem<>(new OptionNumeric(3, "names", "description", 5, 0, 10))
    )));
    viewModel.setOptions(new VehicleOptionsListItems(Arrays.asList(
        new VehicleOptionsListItem<>(new OptionBoolean(1, "name", "description", false)),
        new VehicleOptionsListItem<>(new OptionBoolean(2, "emacs", "description", true))
    ), Collections.singletonList(
        new VehicleOptionsListItem<>(new OptionNumeric(3, "names", "description", 5, 0, 10))
    )));
    viewModel.setOptions(new VehicleOptionsListItems(Arrays.asList(
        new VehicleOptionsListItem<>(new OptionBoolean(1, "name", "description", false)),
        new VehicleOptionsListItem<>(new OptionBoolean(2, "emacs", "description", true))
    ), Arrays.asList(
        new VehicleOptionsListItem<>(new OptionNumeric(3, "names", "description", 5, 0, 10)),
        new VehicleOptionsListItem<>(new OptionNumeric(4, "nam", "description", 1, -1, 2))
    )));

    // Effect:
    verify(vehicleOptionsUseCase).getVehicleOptions();
    verify(vehicleOptionsUseCase).getDriverOptions();
    verify(vehicleOptionsUseCase).setSelectedVehicleAndOptions(Collections.singletonList(
        new OptionBoolean(1, "name", "description", false)
    ), Collections.singletonList(
        new OptionNumeric(3, "names", "description", 5, 0, 10)
    ));
    verifyNoMoreInteractions(vehicleOptionsUseCase);
  }

  /* Тетсируем работу с юзкейсом услуг. */

  /**
   * Не должен трогать юзкейс изначально.
   */
  @Test
  public void doNotTouchServicesUseCaseForOptionsInitially() {
    // Effect:
    verifyNoInteractions(servicesUseCase);
  }

  /**
   * Не должен трогать юзкейс при подписках.
   */
  @Test
  public void doNotTouchServicesUseCaseOnSubscriptions() {
    // Given:
    publishSubject.onNext(Arrays.asList(
        new OptionBoolean(1, "name", "description", false),
        new OptionBoolean(2, "emacs", "descriptions", true)
    ));
    singleSubject.onSuccess(Arrays.asList(
        new OptionNumeric(3, "names", "desc", 5, 0, 10),
        new OptionNumeric(4, "nam", "script", 1, -1, 2)
    ));

    // Action:
    viewModel.getViewStateLiveData();
    viewModel.getNavigationLiveData();
    viewModel.getViewStateLiveData();

    // Effect:
    verifyNoInteractions(servicesUseCase);
  }

  /**
   * Должен попросить юзкейс автоназначить услуги.
   */
  @Test
  public void askServicesUseCaseToAutoAssignServices() {
    // Given:
    when(vehicleOptionsUseCase.setSelectedVehicleAndOptions(anyList(), anyList()))
        .thenReturn(Completable.complete());
    when(servicesUseCase.autoAssignServices()).thenReturn(Completable.complete());

    // Action:
    viewModel.setOptions(new VehicleOptionsListItems(
        Collections.singletonList(
            new VehicleOptionsListItem<>(new OptionBoolean(1, "name", "description", false))
        ), Collections.singletonList(
        new VehicleOptionsListItem<>(new OptionNumeric(3, "names", "description", 5, 0, 10))
    )));
    viewModel.setOptions(new VehicleOptionsListItems(Arrays.asList(
        new VehicleOptionsListItem<>(new OptionBoolean(1, "name", "description", false)),
        new VehicleOptionsListItem<>(new OptionBoolean(2, "emacs", "description", true))
    ), Collections.singletonList(
        new VehicleOptionsListItem<>(new OptionNumeric(3, "names", "description", 5, 0, 10))
    )));
    viewModel.setOptions(new VehicleOptionsListItems(Arrays.asList(
        new VehicleOptionsListItem<>(new OptionBoolean(1, "name", "description", false)),
        new VehicleOptionsListItem<>(new OptionBoolean(2, "emacs", "description", true))
    ), Arrays.asList(
        new VehicleOptionsListItem<>(new OptionNumeric(3, "names", "description", 5, 0, 10)),
        new VehicleOptionsListItem<>(new OptionNumeric(4, "nam", "description", 1, -1, 2))
    )));

    // Effect:
    verify(servicesUseCase, times(3)).autoAssignServices();
    verifyNoMoreInteractions(servicesUseCase);
  }

  /**
   * Не должен трогать юзкейс, если предыдущий запрос занятия ТС еще не завершился.
   */
  @Test
  public void DoNotTouchServicesUseCaseDuringVehicleOccupying() {
    // Action:
    viewModel.setOptions(new VehicleOptionsListItems(
        Collections.singletonList(
            new VehicleOptionsListItem<>(new OptionBoolean(1, "name", "description", false))
        ), Collections.singletonList(
        new VehicleOptionsListItem<>(new OptionNumeric(3, "names", "description", 5, 0, 10))
    )));
    viewModel.setOptions(new VehicleOptionsListItems(Arrays.asList(
        new VehicleOptionsListItem<>(new OptionBoolean(1, "name", "description", false)),
        new VehicleOptionsListItem<>(new OptionBoolean(2, "emacs", "description", true))
    ), Collections.singletonList(
        new VehicleOptionsListItem<>(new OptionNumeric(3, "names", "description", 5, 0, 10))
    )));
    viewModel.setOptions(new VehicleOptionsListItems(Arrays.asList(
        new VehicleOptionsListItem<>(new OptionBoolean(1, "name", "description", false)),
        new VehicleOptionsListItem<>(new OptionBoolean(2, "emacs", "description", true))
    ), Arrays.asList(
        new VehicleOptionsListItem<>(new OptionNumeric(3, "names", "description", 5, 0, 10)),
        new VehicleOptionsListItem<>(new OptionNumeric(4, "nam", "description", 1, -1, 2))
    )));

    // Effect:
    verify(servicesUseCase, only()).autoAssignServices();
  }

  /* Тетсируем переключение состояний. */

  /**
   * Должен вернуть исходное состояние вида изначально.
   */
  @Test
  public void setInitialViewStateToLiveData() {
    // Given:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);

    // Action:
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Effect:
    inOrder.verify(viewStateObserver).onChanged(any(VehicleOptionsViewStateInitial.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояния вида "Готово" со списком опций ТС.
   */
  @Test
  public void setReadyViewStateToLiveData() {
    // Given:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    publishSubject.onNext(Arrays.asList(
        new OptionBoolean(1, "name", "description", false),
        new OptionBoolean(2, "emacs", "descriptions", true)
    ));
    singleSubject.onSuccess(Arrays.asList(
        new OptionNumeric(3, "names", "desc", 5, 0, 10),
        new OptionNumeric(4, "nam", "script", 1, -1, 2)
    ));
    publishSubject.onNext(Arrays.asList(
        new OptionBoolean(5, "name1", "description1", true),
        new OptionBoolean(6, "emacs1", "descriptions1", false)
    ));
    publishSubject.onNext(Arrays.asList(
        new OptionBoolean(7, "name2", "description2", true),
        new OptionBoolean(8, "emacs2", "descriptions2", false)
    ));

    // Effect:
    inOrder.verify(viewStateObserver).onChanged(any(VehicleOptionsViewStateInitial.class));
    inOrder.verify(viewStateObserver).onChanged(new VehicleOptionsViewStateReady(
        new VehicleOptionsListItems(Arrays.asList(
            new VehicleOptionsListItem<>(new OptionBoolean(1, "name", "description", false)),
            new VehicleOptionsListItem<>(new OptionBoolean(2, "emacs", "descriptions", true))
        ), Arrays.asList(
            new VehicleOptionsListItem<>(new OptionNumeric(3, "names", "desc", 5, 0, 10)),
            new VehicleOptionsListItem<>(new OptionNumeric(4, "nam", "script", 1, -1, 2))
        ))
    ));
    inOrder.verify(viewStateObserver).onChanged(new VehicleOptionsViewStateReady(
        new VehicleOptionsListItems(Arrays.asList(
            new VehicleOptionsListItem<>(new OptionBoolean(5, "name1", "description1", true)),
            new VehicleOptionsListItem<>(new OptionBoolean(6, "emacs1", "descriptions1", false))
        ), Arrays.asList(
            new VehicleOptionsListItem<>(new OptionNumeric(3, "names", "desc", 5, 0, 10)),
            new VehicleOptionsListItem<>(new OptionNumeric(4, "nam", "script", 1, -1, 2))
        ))
    ));
    inOrder.verify(viewStateObserver).onChanged(new VehicleOptionsViewStateReady(
            new VehicleOptionsListItems(Arrays.asList(
                    new VehicleOptionsListItem<>(new OptionBoolean(7, "name2", "description2", true)),
                    new VehicleOptionsListItem<>(new OptionBoolean(8, "emacs2", "descriptions2", false))
            ), Arrays.asList(
                    new VehicleOptionsListItem<>(new OptionNumeric(3, "names", "desc", 5, 0, 10)),
                    new VehicleOptionsListItem<>(new OptionNumeric(4, "nam", "script", 1, -1, 2))
            ))
    ));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида "В процессе".
   */
  @Test
  public void setPendingViewStateToLiveData() {
    // Given:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(vehicleOptionsUseCase.setSelectedVehicleAndOptions(anyList(), anyList()))
        .thenReturn(Completable.complete());
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    publishSubject.onNext(Arrays.asList(
        new OptionBoolean(1, "name", "description", false),
        new OptionBoolean(2, "emacs", "descriptions", true)
    ));
    singleSubject.onSuccess(Arrays.asList(
        new OptionNumeric(3, "names", "desc", 5, 0, 10),
        new OptionNumeric(4, "nam", "script", 1, -1, 2)
    ));
    viewModel.setOptions(
        new VehicleOptionsListItems(new ArrayList<>(), new ArrayList<>())
    );

    // Effect:
    inOrder.verify(viewStateObserver).onChanged(any(VehicleOptionsViewStateInitial.class));
    inOrder.verify(viewStateObserver).onChanged(new VehicleOptionsViewStateReady(
        new VehicleOptionsListItems(Arrays.asList(
            new VehicleOptionsListItem<>(new OptionBoolean(1, "name", "description", false)),
            new VehicleOptionsListItem<>(new OptionBoolean(2, "emacs", "descriptions", true))
        ), Arrays.asList(
            new VehicleOptionsListItem<>(new OptionNumeric(3, "names", "desc", 5, 0, 10)),
            new VehicleOptionsListItem<>(new OptionNumeric(4, "nam", "script", 1, -1, 2))
        ))
    ));
    inOrder.verify(viewStateObserver).onChanged(any(VehicleOptionsViewStatePending.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида "Решаемая Ошибка" сети.
   */
  @Test
  public void setNetworkErrorViewStateToLiveData() {
    // Given:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(vehicleOptionsUseCase.setSelectedVehicleAndOptions(anyList(), anyList()))
        .thenReturn(Completable.error(NoNetworkException::new));
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    publishSubject.onNext(Arrays.asList(
        new OptionBoolean(1, "name", "description", false),
        new OptionBoolean(2, "emacs", "descriptions", true)
    ));
    singleSubject.onSuccess(Arrays.asList(
        new OptionNumeric(3, "names", "desc", 5, 0, 10),
        new OptionNumeric(4, "nam", "script", 1, -1, 2)
    ));
    viewModel.setOptions(
        new VehicleOptionsListItems(new ArrayList<>(), new ArrayList<>())
    );

    // Effect:
    inOrder.verify(viewStateObserver).onChanged(any(VehicleOptionsViewStateInitial.class));
    inOrder.verify(viewStateObserver).onChanged(new VehicleOptionsViewStateReady(
        new VehicleOptionsListItems(Arrays.asList(
            new VehicleOptionsListItem<>(new OptionBoolean(1, "name", "description", false)),
            new VehicleOptionsListItem<>(new OptionBoolean(2, "emacs", "descriptions", true))
        ), Arrays.asList(
            new VehicleOptionsListItem<>(new OptionNumeric(3, "names", "desc", 5, 0, 10)),
            new VehicleOptionsListItem<>(new OptionNumeric(4, "nam", "script", 1, -1, 2))
        ))
    ));
    inOrder.verify(viewStateObserver).onChanged(any(VehicleOptionsViewStatePending.class));
    inOrder.verify(viewStateObserver)
        .onChanged(new VehicleOptionsViewStateResolvableError(
                R.string.sms_network_error,
                new VehicleOptionsViewStateReady(
                    new VehicleOptionsListItems(Arrays.asList(
                        new VehicleOptionsListItem<>(
                            new OptionBoolean(1, "name", "description", false)),
                        new VehicleOptionsListItem<>(
                            new OptionBoolean(2, "emacs", "descriptions", true))
                    ), Arrays.asList(
                        new VehicleOptionsListItem<>(new OptionNumeric(3, "names", "desc", 5, 0, 10)),
                        new VehicleOptionsListItem<>(new OptionNumeric(4, "nam", "script", 1, -1, 2))
                    ))
                ),
                () -> {
                }
            )
        );
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида "Решаемая Ошибка" сети.
   */
  @Test
  public void setNetworkErrorViewStateToLiveDataForServicesAutoAssign() {
    // Given:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(vehicleOptionsUseCase.setSelectedVehicleAndOptions(anyList(), anyList()))
        .thenReturn(Completable.complete());
    when(servicesUseCase.autoAssignServices())
        .thenReturn(Completable.error(NoNetworkException::new));
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    publishSubject.onNext(Arrays.asList(
        new OptionBoolean(1, "name", "description", false),
        new OptionBoolean(2, "emacs", "descriptions", true)
    ));
    singleSubject.onSuccess(Arrays.asList(
        new OptionNumeric(3, "names", "desc", 5, 0, 10),
        new OptionNumeric(4, "nam", "script", 1, -1, 2)
    ));
    viewModel.setOptions(
        new VehicleOptionsListItems(new ArrayList<>(), new ArrayList<>())
    );

    // Effect:
    inOrder.verify(viewStateObserver).onChanged(any(VehicleOptionsViewStateInitial.class));
    inOrder.verify(viewStateObserver).onChanged(new VehicleOptionsViewStateReady(
        new VehicleOptionsListItems(Arrays.asList(
            new VehicleOptionsListItem<>(new OptionBoolean(1, "name", "description", false)),
            new VehicleOptionsListItem<>(new OptionBoolean(2, "emacs", "descriptions", true))
        ), Arrays.asList(
            new VehicleOptionsListItem<>(new OptionNumeric(3, "names", "desc", 5, 0, 10)),
            new VehicleOptionsListItem<>(new OptionNumeric(4, "nam", "script", 1, -1, 2))
        ))
    ));
    inOrder.verify(viewStateObserver).onChanged(any(VehicleOptionsViewStatePending.class));
    inOrder.verify(viewStateObserver)
        .onChanged(new VehicleOptionsViewStateResolvableError(
                R.string.sms_network_error,
                new VehicleOptionsViewStateReady(
                    new VehicleOptionsListItems(Arrays.asList(
                        new VehicleOptionsListItem<>(
                            new OptionBoolean(1, "name", "description", false)),
                        new VehicleOptionsListItem<>(
                            new OptionBoolean(2, "emacs", "descriptions", true))
                    ), Arrays.asList(
                        new VehicleOptionsListItem<>(new OptionNumeric(3, "names", "desc", 5, 0, 10)),
                        new VehicleOptionsListItem<>(new OptionNumeric(4, "nam", "script", 1, -1, 2))
                    ))
                ),
                () -> {
                }
            )
        );
    verifyNoMoreInteractions(viewStateObserver);
  }

  /* Тетсируем навигацию. */

  /**
   * Должен игнорировать ошибки.
   */
  @Test
  public void setNothingToLiveData() {
    // Given:
    when(vehicleOptionsUseCase.setSelectedVehicleAndOptions(anyList(), anyList()))
        .thenReturn(Completable.error(new NoNetworkException()));
    viewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Action:
    viewModel.setOptions(
        new VehicleOptionsListItems(new ArrayList<>(), new ArrayList<>())
    );

    // Effect:
    verifyNoInteractions(navigateObserver);
  }

  /**
   * Должен вернуть "перейти к ожиданию заказов" если занятие ТС было успешным.
   */
  @Test
  public void setNavigateToServicesToLiveData() {
    // Given:
    when(vehicleOptionsUseCase.setSelectedVehicleAndOptions(anyList(), anyList()))
        .thenReturn(Completable.complete());
    viewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Action:
    viewModel.setOptions(
        new VehicleOptionsListItems(new ArrayList<>(), new ArrayList<>())
    );

    // Effect:
    verifyNoInteractions(navigateObserver);
  }
}