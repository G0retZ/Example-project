package com.fasten.executor_driver.presentation.services;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.Observer;
import com.fasten.executor_driver.R;
import com.fasten.executor_driver.backend.web.NoNetworkException;
import com.fasten.executor_driver.entity.NoServicesAvailableException;
import com.fasten.executor_driver.entity.Service;
import com.fasten.executor_driver.gateway.DataMappingException;
import com.fasten.executor_driver.interactor.services.ServicesUseCase;
import com.fasten.executor_driver.presentation.ViewState;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
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
public class ServicesViewModelTest {

  @Rule
  public TestRule rule = new InstantTaskExecutorRule();
  private ServicesViewModel servicesViewModel;
  @Mock
  private ServicesUseCase servicesUseCase;

  @Mock
  private Observer<ViewState<ServicesViewActions>> viewStateObserver;

  @Before
  public void setUp() {
    RxJavaPlugins.setSingleSchedulerHandler(scheduler -> Schedulers.trampoline());
    RxAndroidPlugins.setInitMainThreadSchedulerHandler(scheduler -> Schedulers.trampoline());
    when(servicesUseCase.loadServices()).thenReturn(Single.never());
    when(servicesUseCase.setSelectedServices(anyList())).thenReturn(Completable.never());
    servicesViewModel = new ServicesViewModelImpl(servicesUseCase);
  }

  /* Тетсируем работу с юзкейсом выбора услуг. */

  /**
   * Должен просить юзкейс получить список услуг, при первой и только при первой подписке.
   */
  @Test
  public void askVehicleOptionsUseCaseForOptionsInitially() {
    // Дано:
    when(servicesUseCase.loadServices()).thenReturn(Single.just(
        Arrays.asList(
            new Service(0, "n1", 100, false),
            new Service(1, "n2", 10, false),
            new Service(2, "n3", 130, false)
        )
    ));

    // Действие:
    servicesViewModel.getViewStateLiveData();
    servicesViewModel.getViewStateLiveData();
    servicesViewModel.getViewStateLiveData();

    // Результат:
    verify(servicesUseCase, only()).loadServices();
  }

  /**
   * Должен попросить юзкейс запомнить указанные услуги.
   */
  @Test
  public void askVehicleOptionsUseCaseToOccupyVehicleWithOptions() {
    // Дано:
    when(servicesUseCase.setSelectedServices(anyList())).thenReturn(Completable.complete());

    // Действие:
    servicesViewModel.setServices(Collections.singletonList(
        new ServicesListItem(new Service(0, "n1", 100, false))
    ));
    servicesViewModel.setServices(Arrays.asList(
        new ServicesListItem(new Service(0, "n1", 100, false)),
        new ServicesListItem(new Service(1, "n2", 10, false))
    ));
    servicesViewModel.setServices(Arrays.asList(
        new ServicesListItem(new Service(0, "n1", 100, false)),
        new ServicesListItem(new Service(1, "n2", 10, false)),
        new ServicesListItem(new Service(2, "n3", 130, false))
    ));

    // Результат:
    verify(servicesUseCase).setSelectedServices(Collections.singletonList(
        new Service(0, "n1", 100, false)
    ));
    verify(servicesUseCase).setSelectedServices(Arrays.asList(
        new Service(0, "n1", 100, false),
        new Service(1, "n2", 10, false)
    ));
    verify(servicesUseCase).setSelectedServices(Arrays.asList(
        new Service(0, "n1", 100, false),
        new Service(1, "n2", 10, false),
        new Service(2, "n3", 130, false)
    ));
    verifyNoMoreInteractions(servicesUseCase);
  }

  /**
   * Не должен трогать юзкейс, если предыдущий запрос запоминания указанных услуг еще не завершился.
   */
  @Test
  public void DoNotTouchVehicleOptionsUseCaseDuringVehicleOccupying() {
    // Действие:
    servicesViewModel.setServices(Collections.singletonList(
        new ServicesListItem(new Service(0, "n1", 100, false))
    ));
    servicesViewModel.setServices(Arrays.asList(
        new ServicesListItem(new Service(0, "n1", 100, false)),
        new ServicesListItem(new Service(1, "n2", 10, false))
    ));
    servicesViewModel.setServices(Arrays.asList(
        new ServicesListItem(new Service(0, "n1", 100, false)),
        new ServicesListItem(new Service(1, "n2", 10, false)),
        new ServicesListItem(new Service(2, "n3", 130, false))
    ));

    // Результат:
    verify(servicesUseCase, only()).setSelectedServices(Collections.singletonList(
        new Service(0, "n1", 100, false)
    ));
  }

  /* Тетсируем переключение состояний. */

  /**
   * Должен вернуть состояние вида ожидания изначально.
   */
  @Test
  public void setPendingViewStateToLiveDataInitially() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);

    // Действие:
    servicesViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(ServicesViewStatePending.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида "Ошибка" нет сети.
   */
  @Test
  public void setNoNetworkErrorViewStateToLiveData() {
    // Дано:
    SingleSubject<List<Service>> singleSubject = SingleSubject.create();
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(servicesUseCase.loadServices()).thenReturn(singleSubject);
    servicesViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    singleSubject.onError(new NoNetworkException());

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(ServicesViewStatePending.class));
    inOrder.verify(viewStateObserver)
        .onChanged(new ServicesViewStateError(R.string.no_network_connection));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида "Ошибка" нет сети.
   */
  @Test
  public void setNoNetworkErrorViewStateToLiveDataForMappingError() {
    // Дано:
    SingleSubject<List<Service>> singleSubject = SingleSubject.create();
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(servicesUseCase.loadServices()).thenReturn(singleSubject);
    servicesViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    singleSubject.onError(new DataMappingException());

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(ServicesViewStatePending.class));
    inOrder.verify(viewStateObserver)
        .onChanged(new ServicesViewStateError(R.string.no_network_connection));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида "Ошибка" нет доступных услуг.
   */
  @Test
  public void setNoServicesAvailableErrorViewStateToLiveData() {
    // Дано:
    SingleSubject<List<Service>> singleSubject = SingleSubject.create();
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(servicesUseCase.loadServices()).thenReturn(singleSubject);
    servicesViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    singleSubject.onError(new NoServicesAvailableException());

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(ServicesViewStatePending.class));
    inOrder.verify(viewStateObserver)
        .onChanged(new ServicesViewStateError(R.string.no_services_available));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида "Готово" со списком услуг.
   */
  @Test
  public void setReadyViewStateToLiveData() {
    // Дано:
    SingleSubject<List<Service>> singleSubject = SingleSubject.create();
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(servicesUseCase.loadServices()).thenReturn(singleSubject);
    servicesViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    singleSubject.onSuccess(Arrays.asList(
        new Service(0, "n1", 100, false),
        new Service(1, "n2", 10, false),
        new Service(2, "n3", 130, false)
    ));

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(ServicesViewStatePending.class));
    inOrder.verify(viewStateObserver).onChanged(new ServicesViewStateReady(
        Arrays.asList(
            new ServicesListItem(new Service(0, "n1", 100, false)),
            new ServicesListItem(new Service(1, "n2", 10, false)),
            new ServicesListItem(new Service(2, "n3", 130, false))
        )
    ));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида "В процессе".
   */
  @Test
  public void setPendingViewStateToLiveData() {
    // Дано:
    SingleSubject<List<Service>> singleSubject = SingleSubject.create();
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(servicesUseCase.loadServices()).thenReturn(singleSubject);
    when(servicesUseCase.setSelectedServices(anyList())).thenReturn(Completable.complete());
    servicesViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    singleSubject.onSuccess(Arrays.asList(
        new Service(0, "n1", 100, false),
        new Service(1, "n2", 10, false),
        new Service(2, "n3", 130, false)
    ));
    servicesViewModel.setServices(
        new ArrayList<>()
    );

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(ServicesViewStatePending.class));
    inOrder.verify(viewStateObserver).onChanged(new ServicesViewStateReady(
        Arrays.asList(
            new ServicesListItem(new Service(0, "n1", 100, false)),
            new ServicesListItem(new Service(1, "n2", 10, false)),
            new ServicesListItem(new Service(2, "n3", 130, false))
        )
    ));
    inOrder.verify(viewStateObserver).onChanged(any(ServicesViewStatePending.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида "Ошибка" сети.
   */
  @Test
  public void setNoNetworkErrorViewStateToLiveDataForServicesSet() {
    // Дано:
    SingleSubject<List<Service>> singleSubject = SingleSubject.create();
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(servicesUseCase.loadServices()).thenReturn(singleSubject);
    when(servicesUseCase.setSelectedServices(anyList()))
        .thenReturn(Completable.error(NoNetworkException::new));
    servicesViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    singleSubject.onSuccess(Arrays.asList(
        new Service(0, "n1", 100, false),
        new Service(1, "n2", 10, false),
        new Service(2, "n3", 130, false)
    ));
    servicesViewModel.setServices(new ArrayList<>());

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(ServicesViewStatePending.class));
    inOrder.verify(viewStateObserver).onChanged(new ServicesViewStateReady(
        Arrays.asList(
            new ServicesListItem(new Service(0, "n1", 100, false)),
            new ServicesListItem(new Service(1, "n2", 10, false)),
            new ServicesListItem(new Service(2, "n3", 130, false))
        )
    ));
    inOrder.verify(viewStateObserver).onChanged(any(ServicesViewStatePending.class));
    inOrder.verify(viewStateObserver)
        .onChanged(new ServicesViewStateResolvableError(R.string.no_network_connection,
            new ServicesViewStateReady(
                Arrays.asList(
                    new ServicesListItem(new Service(0, "n1", 100, false)),
                    new ServicesListItem(new Service(1, "n2", 10, false)),
                    new ServicesListItem(new Service(2, "n3", 130, false))
                )
            )
        ));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида "Ошибка" нет выбранных услуг.
   */
  @Test
  public void setNoServicesSelectedErrorViewStateToLiveData() {
    // Дано:
    SingleSubject<List<Service>> singleSubject = SingleSubject.create();
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(servicesUseCase.loadServices()).thenReturn(singleSubject);
    when(servicesUseCase.setSelectedServices(anyList()))
        .thenReturn(Completable.error(NoServicesAvailableException::new));
    servicesViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    singleSubject.onSuccess(Arrays.asList(
        new Service(0, "n1", 100, false),
        new Service(1, "n2", 10, false),
        new Service(2, "n3", 130, false)
    ));
    servicesViewModel.setServices(new ArrayList<>());

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(ServicesViewStatePending.class));
    inOrder.verify(viewStateObserver).onChanged(new ServicesViewStateReady(
        Arrays.asList(
            new ServicesListItem(new Service(0, "n1", 100, false)),
            new ServicesListItem(new Service(1, "n2", 10, false)),
            new ServicesListItem(new Service(2, "n3", 130, false))
        )
    ));
    inOrder.verify(viewStateObserver).onChanged(any(ServicesViewStatePending.class));
    inOrder.verify(viewStateObserver)
        .onChanged(new ServicesViewStateResolvableError(R.string.no_services_selected,
            new ServicesViewStateReady(
                Arrays.asList(
                    new ServicesListItem(new Service(0, "n1", 100, false)),
                    new ServicesListItem(new Service(1, "n2", 10, false)),
                    new ServicesListItem(new Service(2, "n3", 130, false))
                )
            )
        ));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида "Готово" со списком услуг после потребления ошибки.
   */
  @Test
  public void setReadyViewStateToLiveDataAfterErrorConsumed() {
    // Дано:
    SingleSubject<List<Service>> singleSubject = SingleSubject.create();
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(servicesUseCase.loadServices()).thenReturn(singleSubject);
    when(servicesUseCase.setSelectedServices(anyList()))
        .thenReturn(Completable.error(NoServicesAvailableException::new));
    servicesViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    singleSubject.onSuccess(Arrays.asList(
        new Service(0, "n1", 100, false),
        new Service(1, "n2", 10, false),
        new Service(2, "n3", 130, false)
    ));
    servicesViewModel.setServices(new ArrayList<>());
    servicesViewModel.errorConsumed();

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(ServicesViewStatePending.class));
    inOrder.verify(viewStateObserver).onChanged(new ServicesViewStateReady(
        Arrays.asList(
            new ServicesListItem(new Service(0, "n1", 100, false)),
            new ServicesListItem(new Service(1, "n2", 10, false)),
            new ServicesListItem(new Service(2, "n3", 130, false))
        )
    ));
    inOrder.verify(viewStateObserver).onChanged(any(ServicesViewStatePending.class));
    inOrder.verify(viewStateObserver)
        .onChanged(new ServicesViewStateResolvableError(R.string.no_services_selected,
            new ServicesViewStateReady(
                Arrays.asList(
                    new ServicesListItem(new Service(0, "n1", 100, false)),
                    new ServicesListItem(new Service(1, "n2", 10, false)),
                    new ServicesListItem(new Service(2, "n3", 130, false))
                )
            )
        ));
    inOrder.verify(viewStateObserver).onChanged(new ServicesViewStateReady(
        Arrays.asList(
            new ServicesListItem(new Service(0, "n1", 100, false)),
            new ServicesListItem(new Service(1, "n2", 10, false)),
            new ServicesListItem(new Service(2, "n3", 130, false))
        )
    ));
    verifyNoMoreInteractions(viewStateObserver);
  }
}