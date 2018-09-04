package com.cargopull.executor_driver.presentation.preorderslist;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.Observer;
import com.cargopull.executor_driver.ViewModelThreadTestRule;
import com.cargopull.executor_driver.entity.Order;
import com.cargopull.executor_driver.gateway.DataMappingException;
import com.cargopull.executor_driver.interactor.OrdersUseCase;
import com.cargopull.executor_driver.presentation.CommonNavigate;
import com.cargopull.executor_driver.presentation.ViewState;
import io.reactivex.BackpressureStrategy;
import io.reactivex.subjects.PublishSubject;
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
public class PreOrdersListViewModelTest {

  @ClassRule
  public static final ViewModelThreadTestRule classRule = new ViewModelThreadTestRule();
  @Rule
  public TestRule rule = new InstantTaskExecutorRule();
  private PreOrdersListViewModel viewModel;
  @Mock
  private OrdersUseCase useCase;
  @Mock
  private PreOrdersListItemsMapper preOrdersListItemsMapper;
  @Mock
  private Observer<ViewState<PreOrdersListViewActions>> viewStateObserver;
  @Mock
  private Observer<String> navigateObserver;
  @Mock
  private List<Order> ordersList;
  @Mock
  private List<Order> ordersList1;
  @Mock
  private List<Order> ordersList2;
  @Mock
  private List<PreOrdersListItem> preOrdersListItems;
  @Mock
  private List<PreOrdersListItem> preOrdersListItems1;
  @Mock
  private List<PreOrdersListItem> preOrdersListItems2;
  private PublishSubject<List<Order>> publishSubject;

  @Before
  public void setUp() {
    publishSubject = PublishSubject.create();
    when(preOrdersListItemsMapper.apply(ordersList)).thenReturn(preOrdersListItems);
    when(preOrdersListItemsMapper.apply(ordersList1)).thenReturn(preOrdersListItems1);
    when(preOrdersListItemsMapper.apply(ordersList2)).thenReturn(preOrdersListItems2);
    when(useCase.getOrdersList())
        .thenReturn(publishSubject.toFlowable(BackpressureStrategy.BUFFER));
    viewModel = new PreOrdersListViewModelImpl(useCase, preOrdersListItemsMapper);
  }

  /* Тетсируем работу с юзкейсом. */

  /**
   * Должен просить юзкейс получить список предзаказов изначально.
   */
  @Test
  public void askUseCaseForPreOrdersListInitially() {
    // Результат:
    verify(useCase, only()).getOrdersList();
  }

  /**
   * Не должен трогать юзкейс при подписках.
   */
  @Test
  public void doNotTouchUseCaseOnSubscriptions() {
    // Дано:
    publishSubject.onNext(ordersList);

    // Действие:
    viewModel.getViewStateLiveData();
    viewModel.getNavigationLiveData();
    viewModel.getViewStateLiveData();
    viewModel.getNavigationLiveData();

    // Результат:
    verify(useCase, only()).getOrdersList();
  }

  /* Тетсируем работу с маппером. */

  /**
   * Должен просить трогать маппер без результатов.
   */
  @Test
  public void doNotTouchMapperWithoutResults() {
    // Результат:
    verifyZeroInteractions(preOrdersListItemsMapper);
  }

  /**
   * Должен запросить маппинг полученного списка.
   */
  @Test
  public void askMapperToMapTheList() {
    // Действие:
    publishSubject.onNext(ordersList);

    // Результат:
    verify(preOrdersListItemsMapper, only()).apply(ordersList);
  }

  /**
   * Не должен трогать мапппер при подписках.
   */
  @Test
  public void doNotTouchMapperOnSubscriptions() {
    // Дано:
    publishSubject.onNext(ordersList);

    // Действие:
    viewModel.getViewStateLiveData();
    viewModel.getNavigationLiveData();
    viewModel.getViewStateLiveData();
    viewModel.getNavigationLiveData();

    // Результат:
    verify(preOrdersListItemsMapper, only()).apply(ordersList);
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
    inOrder.verify(viewStateObserver).onChanged(new PreOrdersListViewStatePending(null));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояния вида "Готово" со списком предзаказов.
   */
  @Test
  public void setReadyViewStateToLiveData() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(ordersList);
    publishSubject.onNext(ordersList1);
    publishSubject.onNext(ordersList2);

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(new PreOrdersListViewStatePending(null));
    inOrder.verify(viewStateObserver)
        .onChanged(new PreOrdersListViewStateReady(preOrdersListItems));
    inOrder.verify(viewStateObserver)
        .onChanged(new PreOrdersListViewStateReady(preOrdersListItems1));
    inOrder.verify(viewStateObserver)
        .onChanged(new PreOrdersListViewStateReady(preOrdersListItems2));
    verifyZeroInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояния вида "Пусто".
   */
  @Test
  public void setEmptyViewStateToLiveData() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(preOrdersListItems.isEmpty()).thenReturn(true);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(ordersList);

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(new PreOrdersListViewStatePending(null));
    inOrder.verify(viewStateObserver).onChanged(any(PreOrdersListViewStateEmpty.class));
    verifyZeroInteractions(viewStateObserver);
  }

  /* Тетсируем навигацию. */

  /**
   * Не должен никуда переходить при получении данных.
   */
  @Test
  public void setNothingToLiveDataForData() {
    // Дано:
    viewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Действие:
    publishSubject.onNext(ordersList);
    publishSubject.onNext(ordersList1);
    publishSubject.onNext(ordersList2);

    // Результат:
    verifyZeroInteractions(navigateObserver);
  }

  /**
   * Не должен никуда переходить при завершении получения данных.
   */
  @Test
  public void setNothingToLiveDataForComplete() {
    // Дано:
    viewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Действие:
    publishSubject.onComplete();

    // Результат:
    verifyZeroInteractions(navigateObserver);
  }

  /**
   * Должен вернуть "перейти к ошибке сети".
   */
  @Test
  public void setNavigateToNoConnectionToLiveData() {
    // Дано:
    viewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Действие:
    publishSubject.onError(new Exception());

    // Результат:
    verify(navigateObserver, only()).onChanged(CommonNavigate.NO_CONNECTION);
  }

  /**
   * Должен вернуть "перейти к ошибке в данных от сервера".
   */
  @Test
  public void setNavigateToServerDataErrorToLiveData() {
    // Дано:
    viewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Действие:
    publishSubject.onError(new DataMappingException());

    // Результат:
    verify(navigateObserver, only()).onChanged(CommonNavigate.SERVER_DATA_ERROR);
  }
}