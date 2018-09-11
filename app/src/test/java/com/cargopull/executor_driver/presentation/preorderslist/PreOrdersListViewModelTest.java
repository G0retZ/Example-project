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
import com.cargopull.executor_driver.interactor.SelectedOrderUseCase;
import com.cargopull.executor_driver.presentation.CommonNavigate;
import com.cargopull.executor_driver.presentation.ViewState;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Completable;
import io.reactivex.subjects.PublishSubject;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
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
  private SelectedOrderUseCase selectedOrderUseCase;
  @Mock
  private PreOrdersListItemsMapper preOrdersListItemsMapper;
  @Mock
  private Observer<ViewState<PreOrdersListViewActions>> viewStateObserver;
  @Mock
  private Observer<String> navigateObserver;
  @Mock
  private Order order;
  @Mock
  private Set<Order> orderSet;
  @Mock
  private Set<Order> orderSet1;
  @Mock
  private Set<Order> orderSet2;
  @Mock
  private List<PreOrdersListItem> preOrdersListItems;
  @Mock
  private List<PreOrdersListItem> preOrdersListItems1;
  @Mock
  private List<PreOrdersListItem> preOrdersListItems2;
  private PublishSubject<Set<Order>> publishSubject;

  @Before
  public void setUp() {
    publishSubject = PublishSubject.create();
    when(preOrdersListItemsMapper.apply(orderSet)).thenReturn(preOrdersListItems);
    when(preOrdersListItemsMapper.apply(orderSet1)).thenReturn(preOrdersListItems1);
    when(preOrdersListItemsMapper.apply(orderSet2)).thenReturn(preOrdersListItems2);
    when(useCase.getOrdersSet())
        .thenReturn(publishSubject.toFlowable(BackpressureStrategy.BUFFER));
    when(selectedOrderUseCase.setSelectedOrder(any())).thenReturn(Completable.never());
    viewModel = new PreOrdersListViewModelImpl(useCase, selectedOrderUseCase,
        preOrdersListItemsMapper);
  }

  /* Тетсируем работу с юзкейсом списка предзаказов. */

  /**
   * Должен просить юзкейс получить список предзаказов изначально.
   */
  @Test
  public void askOrdersUseCaseForPreOrdersListInitially() {
    // Результат:
    verify(useCase, only()).getOrdersSet();
  }

  /**
   * Не должен трогать юзкейс при подписках.
   */
  @Test
  public void doNotTouchOrdersUseCaseOnSubscriptions() {
    // Дано:
    publishSubject.onNext(orderSet);

    // Действие:
    viewModel.getViewStateLiveData();
    viewModel.getNavigationLiveData();
    viewModel.getViewStateLiveData();
    viewModel.getNavigationLiveData();

    // Результат:
    verify(useCase, only()).getOrdersSet();
  }

  /* Тетсируем работу с юзкейсом выбора предзаказа. */

  /**
   * Не должен трогать юзкейс при подписках.
   */
  @Test
  public void doNotTouchSelectedOrdersUseCaseOnSubscriptions() {
    // Дано:
    publishSubject.onNext(orderSet);

    // Действие:
    viewModel.getViewStateLiveData();
    viewModel.getNavigationLiveData();
    viewModel.getViewStateLiveData();
    viewModel.getNavigationLiveData();

    // Результат:
    verifyZeroInteractions(selectedOrderUseCase);
  }

  /**
   * Должен просить юзкейс передать выбранный предзаказ.
   */
  @Test
  public void askSelectedOrdersUseCaseForSetSelectedOrder() {
    // Действие:
    viewModel.setSelectedOrder(order);

    // Результат:
    verify(selectedOrderUseCase, only()).setSelectedOrder(order);
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
    publishSubject.onNext(orderSet);

    // Результат:
    verify(preOrdersListItemsMapper, only()).apply(orderSet);
  }

  /**
   * Не должен трогать мапппер при подписках.
   */
  @Test
  public void doNotTouchMapperOnSubscriptions() {
    // Дано:
    publishSubject.onNext(orderSet);

    // Действие:
    viewModel.getViewStateLiveData();
    viewModel.getNavigationLiveData();
    viewModel.getViewStateLiveData();
    viewModel.getNavigationLiveData();

    // Результат:
    verify(preOrdersListItemsMapper, only()).apply(orderSet);
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
    publishSubject.onNext(orderSet);
    publishSubject.onNext(orderSet1);
    publishSubject.onNext(orderSet2);

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(new PreOrdersListViewStatePending(null));
    inOrder.verify(viewStateObserver)
        .onChanged(new PreOrdersListViewStateReady(preOrdersListItems));
    inOrder.verify(viewStateObserver)
        .onChanged(new PreOrdersListViewStateReady(preOrdersListItems1));
    inOrder.verify(viewStateObserver)
        .onChanged(new PreOrdersListViewStateReady(preOrdersListItems2));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Не должен возвращать иных состояний вида для ошибки выбора заказа.
   */
  @Test
  public void setNoViewStateToLiveDataForSelectionFail() {
    // Дано:
    when(selectedOrderUseCase.setSelectedOrder(any()))
        .thenReturn(Completable.error(NoSuchElementException::new));
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    viewModel.setSelectedOrder(order);

    // Результат:
    verify(viewStateObserver, only()).onChanged(new PreOrdersListViewStatePending(null));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Не должен возвращать иных состояний вида для успешного выбора заказа.
   */
  @Test
  public void setNoViewStateToLiveDataForSelectionSuccess() {
    // Дано:
    when(selectedOrderUseCase.setSelectedOrder(any())).thenReturn(Completable.complete());
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    viewModel.setSelectedOrder(order);

    // Результат:
    verify(viewStateObserver, only()).onChanged(new PreOrdersListViewStatePending(null));
    verifyNoMoreInteractions(viewStateObserver);
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
    publishSubject.onNext(orderSet);

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
    publishSubject.onNext(orderSet);
    publishSubject.onNext(orderSet1);
    publishSubject.onNext(orderSet2);

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

  /**
   * Не должен никуда переходить при ошибке выбора.
   */
  @Test
  public void setNothingToLiveDataForSelectionFail() {
    // Дано:
    when(selectedOrderUseCase.setSelectedOrder(any()))
        .thenReturn(Completable.error(NoSuchElementException::new));
    viewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Действие:
    viewModel.setSelectedOrder(order);

    // Результат:
    verifyZeroInteractions(navigateObserver);
  }

  /**
   * Должен вернуть "перейти к предзаказу" при успешном выборе.
   */
  @Test
  public void setNothingToLiveDataForSelectionSuccess() {
    // Дано:
    when(selectedOrderUseCase.setSelectedOrder(any())).thenReturn(Completable.complete());
    viewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Действие:
    viewModel.setSelectedOrder(order);

    // Результат:
    verify(navigateObserver, only()).onChanged(PreOrdersListNavigate.PRE_ORDER);
  }
}