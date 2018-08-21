package com.cargopull.executor_driver.presentation.services;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.Observer;
import com.cargopull.executor_driver.R;
import com.cargopull.executor_driver.ViewModelThreadTestRule;
import com.cargopull.executor_driver.backend.web.NoNetworkException;
import com.cargopull.executor_driver.entity.EmptyListException;
import com.cargopull.executor_driver.entity.Service;
import com.cargopull.executor_driver.gateway.DataMappingException;
import com.cargopull.executor_driver.interactor.services.ServicesUseCase;
import com.cargopull.executor_driver.presentation.ViewState;
import io.reactivex.Completable;
import io.reactivex.subjects.SingleSubject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
public class ServicesViewModelTest {

  @ClassRule
  public static final ViewModelThreadTestRule classRule = new ViewModelThreadTestRule();
  @Rule
  public TestRule rule = new InstantTaskExecutorRule();
  private ServicesViewModel viewModel;
  @Mock
  private ServicesUseCase servicesUseCase;
  @Mock
  private ServicesSliderViewModel servicesSliderViewModel;
  @Mock
  private ServicesListItems servicesListItems;

  @Mock
  private Observer<ViewState<ServicesViewActions>> viewStateObserver;

  private SingleSubject<List<Service>> serviceSingleSubject;

  @Before
  public void setUp() {
    when(servicesUseCase.loadServices()).thenReturn(serviceSingleSubject = SingleSubject.create());
    when(servicesUseCase.setSelectedServices(anyList())).thenReturn(Completable.never());
    when(servicesListItems.getServicesListItems()).thenReturn(
        Arrays.asList(
            new ServicesListItem(new Service(0, "n1", 100, false)),
            new ServicesListItem(new Service(2, "n3", 130, false))
        )
    );
    viewModel = new ServicesViewModelImpl(servicesUseCase, servicesSliderViewModel,
        servicesListItems);
  }

  /* Тетсируем работу с юзкейсом выбора услуг. */

  /**
   * Должен просить юзкейс получить список услуг сразу после создания.
   */
  @Test
  public void askServicesUseCaseForServicesInitially() {
    // Результат:
    verify(servicesUseCase, only()).loadServices();
  }

  /**
   * Не должен трогать юзкейс при подписках.
   */
  @Test
  public void doNotTouchUseCase() {
    // Действие:
    serviceSingleSubject.onSuccess(
        Arrays.asList(
            new Service(0, "n1", 100, false),
            new Service(1, "n2", 10, false),
            new Service(2, "n3", 130, false)
        )
    );
    viewModel.getViewStateLiveData();
    viewModel.getNavigationLiveData();
    viewModel.getViewStateLiveData();

    // Результат:
    verify(servicesUseCase, only()).loadServices();
  }

  /**
   * Должен попросить юзкейс запомнить указанные услуги.
   */
  @Test
  public void askServicesUseCaseToSaveSelectedServices() {
    // Дано:
    when(servicesUseCase.setSelectedServices(anyList())).thenReturn(Completable.complete());

    // Действие:
    viewModel.setServices(Collections.singletonList(
        new ServicesListItem(new Service(0, "n1", 100, false))
    ));
    viewModel.setServices(Arrays.asList(
        new ServicesListItem(new Service(0, "n1", 100, false)),
        new ServicesListItem(new Service(1, "n2", 10, false))
    ));
    viewModel.setServices(Arrays.asList(
        new ServicesListItem(new Service(0, "n1", 100, false)),
        new ServicesListItem(new Service(1, "n2", 10, false)),
        new ServicesListItem(new Service(2, "n3", 130, false))
    ));

    // Результат:
    verify(servicesUseCase).loadServices();
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
  public void DoNotTouchServicesUseCaseDuringServicesSetting() {
    // Действие:
    viewModel.setServices(Collections.singletonList(
        new ServicesListItem(new Service(0, "n1", 100, false))
    ));
    viewModel.setServices(Arrays.asList(
        new ServicesListItem(new Service(0, "n1", 100, false)),
        new ServicesListItem(new Service(1, "n2", 10, false))
    ));
    viewModel.setServices(Arrays.asList(
        new ServicesListItem(new Service(0, "n1", 100, false)),
        new ServicesListItem(new Service(1, "n2", 10, false)),
        new ServicesListItem(new Service(2, "n3", 130, false))
    ));

    // Результат:
    verify(servicesUseCase).loadServices();
    verify(servicesUseCase).setSelectedServices(Collections.singletonList(
        new Service(0, "n1", 100, false)
    ));
    verifyNoMoreInteractions(servicesUseCase);
  }

  /* Тетсируем работу с фильтром услуг. */

  /**
   * Должен передать фильтру список услуг и запросить фильтрованный по-умолчанию список изначально.
   */
  @Test
  public void askServicesFilterInitially() {
    // Действие:
    serviceSingleSubject.onSuccess(
        Arrays.asList(
            new Service(0, "n1", 100, false),
            new Service(1, "n2", 10, false),
            new Service(2, "n3", 130, false)
        )
    );

    // Результат:
    verify(servicesListItems).setServicesListItems(
        Arrays.asList(
            new ServicesListItem(new Service(0, "n1", 100, false)),
            new ServicesListItem(new Service(1, "n2", 10, false)),
            new ServicesListItem(new Service(2, "n3", 130, false))
        )
    );
    verify(servicesListItems).getServicesListItems();
    verifyNoMoreInteractions(servicesListItems);
  }

  /**
   * Не должен трогать фильтр, если была ошибка получения списка услуг.
   */
  @Test
  public void doNotTouchServicesFilter() {
    // Действие:
    serviceSingleSubject.onError(new Exception());

    // Результат:
    verifyZeroInteractions(servicesListItems);
  }

  /**
   * Не должен трогать фильтр при подписках.
   */
  @Test
  public void doNotTouchServicesFilterOnSubscriptions() {
    // Дано:
    serviceSingleSubject.onError(new Exception());

    // Действие:
    viewModel.getViewStateLiveData();
    viewModel.getNavigationLiveData();
    viewModel.getViewStateLiveData();

    // Результат:
    verifyZeroInteractions(servicesListItems);
  }

  /**
   * Должен запросить фильтрованный список услуг с заданным положением ползунка.
   */
  @Test
  public void askServicesFilterWithProgress() {
    // Действие:
    viewModel.setSliderPosition(50);
    viewModel.setSliderPosition(40);
    viewModel.setSliderPosition(30);

    // Результат:
    verify(servicesListItems).getServicesListItems(50);
    verify(servicesListItems).getServicesListItems(40);
    verify(servicesListItems).getServicesListItems(30);
    verifyNoMoreInteractions(servicesListItems);
  }

  /**
   * Должен запросить фильтрованный список услуг при потреблении ошибки.
   */
  @Test
  public void askServicesFilterOnErrorConsume() {
    // Действие:
    viewModel.errorConsumed();
    viewModel.errorConsumed();
    viewModel.errorConsumed();

    // Результат:
    verify(servicesListItems, times(3)).getServicesListItems();
    verifyNoMoreInteractions(servicesListItems);
  }

  /**
   * Должен запросить фильтрованный список услуг при ошибке установки услуг.
   */
  @Test
  public void askServicesFilterOnSetServices() {
    // Дано
    when(servicesUseCase.setSelectedServices(anyList()))
        .thenReturn(Completable.error(NoNetworkException::new));

    // Действие:
    viewModel.setServices(new ArrayList<>());

    // Результат:
    verify(servicesListItems, only()).getServicesListItems();
  }

  /* Тетсируем работу с моделью вида ползунка. */

  /**
   * Должен дернуть обновление модели вида ползунка после получения списка услуг.
   */
  @Test
  public void viewModelToRefresh() {
    // Действие:
    serviceSingleSubject.onSuccess(
        Arrays.asList(
            new Service(0, "n1", 100, false),
            new Service(1, "n2", 10, false),
            new Service(2, "n3", 130, false)
        )
    );

    // Результат:
    verify(servicesSliderViewModel, only()).refresh();
  }

  /**
   * Не должен трогать модель вида ползунка, если была ошибка получения списка услуг.
   */
  @Test
  public void viewModel() {
    // Действие:
    serviceSingleSubject.onError(new Exception());

    // Результат:
    verifyZeroInteractions(servicesSliderViewModel);
  }

  /**
   * Не должен трогать модель вида ползунка на подписках.
   */
  @Test
  public void viewModelOnSubscriptions() {
    // Действие:
    viewModel.getViewStateLiveData();
    viewModel.getNavigationLiveData();
    viewModel.getViewStateLiveData();

    // Результат:
    verifyZeroInteractions(servicesSliderViewModel);
  }

  /**
   * Не должен трогать модель вида ползунка, при работе с установкой выбранных услуг.
   */
  @Test
  public void viewModelForSetServices() {
    // Дано:
    when(servicesUseCase.setSelectedServices(anyList())).thenReturn(
        Completable.complete(),
        Completable.error(NoNetworkException::new),
        Completable.error(EmptyListException::new)
    );

    // Действие:
    viewModel.setServices(new ArrayList<>());
    viewModel.setServices(new ArrayList<>());
    viewModel.setServices(new ArrayList<>());

    // Результат:
    verifyZeroInteractions(servicesSliderViewModel);
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
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

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
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    serviceSingleSubject.onError(new NoNetworkException());

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
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    serviceSingleSubject.onError(new DataMappingException());

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
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    serviceSingleSubject.onError(new EmptyListException());

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
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    serviceSingleSubject.onSuccess(Arrays.asList(
        new Service(0, "n1", 100, false),
        new Service(1, "n2", 10, false),
        new Service(2, "n3", 130, false)
    ));

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(ServicesViewStatePending.class));
    inOrder.verify(viewStateObserver).onChanged(new ServicesViewStateReady(
        Arrays.asList(
            new ServicesListItem(new Service(0, "n1", 100, false)),
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
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(servicesUseCase.setSelectedServices(anyList())).thenReturn(Completable.complete());
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    serviceSingleSubject.onSuccess(Arrays.asList(
        new Service(0, "n1", 100, false),
        new Service(1, "n2", 10, false),
        new Service(2, "n3", 130, false)
    ));
    viewModel.setServices(new ArrayList<>());

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(ServicesViewStatePending.class));
    inOrder.verify(viewStateObserver).onChanged(new ServicesViewStateReady(
        Arrays.asList(
            new ServicesListItem(new Service(0, "n1", 100, false)),
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
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(servicesUseCase.setSelectedServices(anyList()))
        .thenReturn(Completable.error(NoNetworkException::new));
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    serviceSingleSubject.onSuccess(Arrays.asList(
        new Service(0, "n1", 100, false),
        new Service(1, "n2", 10, false),
        new Service(2, "n3", 130, false)
    ));
    viewModel.setServices(new ArrayList<>());

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(ServicesViewStatePending.class));
    inOrder.verify(viewStateObserver).onChanged(new ServicesViewStateReady(
        Arrays.asList(
            new ServicesListItem(new Service(0, "n1", 100, false)),
            new ServicesListItem(new Service(2, "n3", 130, false))
        )
    ));
    inOrder.verify(viewStateObserver).onChanged(any(ServicesViewStatePending.class));
    inOrder.verify(viewStateObserver)
        .onChanged(new ServicesViewStateResolvableError(R.string.no_network_connection,
            Arrays.asList(
                new ServicesListItem(new Service(0, "n1", 100, false)),
                new ServicesListItem(new Service(2, "n3", 130, false))
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
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(servicesUseCase.setSelectedServices(anyList()))
        .thenReturn(Completable.error(EmptyListException::new));
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    serviceSingleSubject.onSuccess(Arrays.asList(
        new Service(0, "n1", 100, false),
        new Service(1, "n2", 10, false),
        new Service(2, "n3", 130, false)
    ));
    viewModel.setServices(new ArrayList<>());

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(ServicesViewStatePending.class));
    inOrder.verify(viewStateObserver).onChanged(new ServicesViewStateReady(
        Arrays.asList(
            new ServicesListItem(new Service(0, "n1", 100, false)),
            new ServicesListItem(new Service(2, "n3", 130, false))
        )
    ));
    inOrder.verify(viewStateObserver).onChanged(any(ServicesViewStatePending.class));
    inOrder.verify(viewStateObserver)
        .onChanged(new ServicesViewStateResolvableError(R.string.no_services_selected,
            Arrays.asList(
                new ServicesListItem(new Service(0, "n1", 100, false)),
                new ServicesListItem(new Service(2, "n3", 130, false))
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
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(servicesUseCase.setSelectedServices(anyList()))
        .thenReturn(Completable.error(EmptyListException::new));
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    serviceSingleSubject.onSuccess(Arrays.asList(
        new Service(0, "n1", 100, false),
        new Service(1, "n2", 10, false),
        new Service(2, "n3", 130, false)
    ));
    viewModel.setServices(new ArrayList<>());
    viewModel.errorConsumed();

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(ServicesViewStatePending.class));
    inOrder.verify(viewStateObserver).onChanged(new ServicesViewStateReady(
        Arrays.asList(
            new ServicesListItem(new Service(0, "n1", 100, false)),
            new ServicesListItem(new Service(2, "n3", 130, false))
        )
    ));
    inOrder.verify(viewStateObserver).onChanged(any(ServicesViewStatePending.class));
    inOrder.verify(viewStateObserver)
        .onChanged(new ServicesViewStateResolvableError(R.string.no_services_selected,
            Arrays.asList(
                new ServicesListItem(new Service(0, "n1", 100, false)),
                new ServicesListItem(new Service(2, "n3", 130, false))
            )
        ));
    inOrder.verify(viewStateObserver).onChanged(new ServicesViewStateReady(
        Arrays.asList(
            new ServicesListItem(new Service(0, "n1", 100, false)),
            new ServicesListItem(new Service(2, "n3", 130, false))
        )
    ));
    verifyNoMoreInteractions(viewStateObserver);
  }
}