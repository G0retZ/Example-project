package com.cargopull.executor_driver.presentation.preorderslist;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;

import com.cargopull.executor_driver.ViewModelThreadTestRule;
import com.cargopull.executor_driver.backend.analytics.ErrorReporter;
import com.cargopull.executor_driver.entity.Order;
import com.cargopull.executor_driver.gateway.DataMappingException;
import com.cargopull.executor_driver.interactor.OrdersUseCase;
import com.cargopull.executor_driver.interactor.SelectedOrderUseCase;
import com.cargopull.executor_driver.presentation.CommonNavigate;
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

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Completable;
import io.reactivex.subjects.PublishSubject;

@RunWith(MockitoJUnitRunner.class)
public class PreOrdersListViewModelTest {

  @ClassRule
  public static final ViewModelThreadTestRule classRule = new ViewModelThreadTestRule();
  @Rule
  public TestRule rule = new InstantTaskExecutorRule();
  private PreOrdersListViewModel viewModel;
  @Mock
  private ErrorReporter errorReporter;
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
    viewModel = new PreOrdersListViewModelImpl(errorReporter, useCase, selectedOrderUseCase,
        preOrdersListItemsMapper);
  }

  /* Проверяем отправку ошибок в репортер */

  /**
   * Должен отправить ошибку.
   */
  @Test
  public void reportError() {
    // Action:
    publishSubject.onError(new DataMappingException());

    // Effect:
    verify(errorReporter, only()).reportError(any(DataMappingException.class));
  }

  /**
   * Должен отправить ошибку при установке выбора.
   */
  @Test
  public void reportErrorOnSet() {
    // Given:
    when(selectedOrderUseCase.setSelectedOrder(any()))
        .thenReturn(Completable.error(DataMappingException::new));

    // Action:
    viewModel.setSelectedOrder(order);

    // Effect:
    verify(errorReporter, only()).reportError(any(DataMappingException.class));
  }

  /**
   * Должен отправить ошибку при неверном выборе.
   */
  @Test
  public void reportErrorOnSetWrong() {
    // Given:
    when(selectedOrderUseCase.setSelectedOrder(any()))
        .thenReturn(Completable.error(NoSuchElementException::new));

    // Action:
    viewModel.setSelectedOrder(order);

    // Effect:
    verify(errorReporter, only()).reportError(any(NoSuchElementException.class));
  }

  /* Тетсируем работу с юзкейсом списка предзаказов. */

  /**
   * Должен просить юзкейс получить список предзаказов изначально.
   */
  @Test
  public void askOrdersUseCaseForPreOrdersListInitially() {
    // Effect:
    verify(useCase, only()).getOrdersSet();
  }

  /**
   * Не должен трогать юзкейс при подписках.
   */
  @Test
  public void doNotTouchOrdersUseCaseOnSubscriptions() {
    // Given:
    publishSubject.onNext(orderSet);

    // Action:
    viewModel.getViewStateLiveData();
    viewModel.getNavigationLiveData();
    viewModel.getViewStateLiveData();
    viewModel.getNavigationLiveData();

    // Effect:
    verify(useCase, only()).getOrdersSet();
  }

  /* Тетсируем работу с юзкейсом выбора предзаказа. */

  /**
   * Не должен трогать юзкейс при подписках.
   */
  @Test
  public void doNotTouchSelectedOrdersUseCaseOnSubscriptions() {
    // Given:
    publishSubject.onNext(orderSet);

    // Action:
    viewModel.getViewStateLiveData();
    viewModel.getNavigationLiveData();
    viewModel.getViewStateLiveData();
    viewModel.getNavigationLiveData();

    // Effect:
    verifyNoInteractions(selectedOrderUseCase);
  }

  /**
   * Должен просить юзкейс передать выбранный предзаказ.
   */
  @Test
  public void askSelectedOrdersUseCaseForSetSelectedOrder() {
    // Action:
    viewModel.setSelectedOrder(order);

    // Effect:
    verify(selectedOrderUseCase, only()).setSelectedOrder(order);
  }

  /* Тетсируем работу с маппером. */

  /**
   * Должен просить трогать маппер без результатов.
   */
  @Test
  public void doNotTouchMapperWithoutResults() {
    // Effect:
    verifyNoInteractions(preOrdersListItemsMapper);
  }

  /**
   * Должен запросить маппинг полученного списка.
   */
  @Test
  public void askMapperToMapTheList() {
    // Action:
    publishSubject.onNext(orderSet);

    // Effect:
    verify(preOrdersListItemsMapper, only()).apply(orderSet);
  }

  /**
   * Не должен трогать мапппер при подписках.
   */
  @Test
  public void doNotTouchMapperOnSubscriptions() {
    // Given:
    publishSubject.onNext(orderSet);

    // Action:
    viewModel.getViewStateLiveData();
    viewModel.getNavigationLiveData();
    viewModel.getViewStateLiveData();
    viewModel.getNavigationLiveData();

    // Effect:
    verify(preOrdersListItemsMapper, only()).apply(orderSet);
  }

  /* Тетсируем переключение состояний. */

  /**
   * Должен вернуть состояние вида ожидания изначально.
   */
  @Test
  public void setPendingViewStateToLiveDataInitially() {
    // Given:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);

    // Action:
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Effect:
    inOrder.verify(viewStateObserver).onChanged(new PreOrdersListViewStatePending(null));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояния вида "Готово" со списком предзаказов.
   */
  @Test
  public void setReadyViewStateToLiveData() {
    // Given:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    publishSubject.onNext(orderSet);
    publishSubject.onNext(orderSet1);
    publishSubject.onNext(orderSet2);

    // Effect:
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
    // Given:
    when(selectedOrderUseCase.setSelectedOrder(any()))
        .thenReturn(Completable.error(NoSuchElementException::new));
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    viewModel.setSelectedOrder(order);

    // Effect:
    verify(viewStateObserver, only()).onChanged(new PreOrdersListViewStatePending(null));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Не должен возвращать иных состояний вида для успешного выбора заказа.
   */
  @Test
  public void setNoViewStateToLiveDataForSelectionSuccess() {
    // Given:
    when(selectedOrderUseCase.setSelectedOrder(any())).thenReturn(Completable.complete());
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    viewModel.setSelectedOrder(order);

    // Effect:
    verify(viewStateObserver, only()).onChanged(new PreOrdersListViewStatePending(null));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояния вида "Пусто".
   */
  @Test
  public void setEmptyViewStateToLiveData() {
    // Given:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(preOrdersListItems.isEmpty()).thenReturn(true);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    publishSubject.onNext(orderSet);

    // Effect:
    inOrder.verify(viewStateObserver).onChanged(new PreOrdersListViewStatePending(null));
    inOrder.verify(viewStateObserver).onChanged(any(PreOrdersListViewStateEmpty.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /* Тетсируем навигацию. */

  /**
   * Не должен никуда переходить при получении данных.
   */
  @Test
  public void setNothingToLiveDataForData() {
    // Given:
    viewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Action:
    publishSubject.onNext(orderSet);
    publishSubject.onNext(orderSet1);
    publishSubject.onNext(orderSet2);

    // Effect:
    verifyNoInteractions(navigateObserver);
  }

  /**
   * Не должен никуда переходить при завершении получения данных.
   */
  @Test
  public void setNothingToLiveDataForComplete() {
    // Given:
    viewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Action:
    publishSubject.onComplete();

    // Effect:
    verifyNoInteractions(navigateObserver);
  }

  /**
   * Должен вернуть "перейти к ошибке сети".
   */
  @Test
  public void setNavigateToNoConnectionToLiveData() {
    // Given:
    viewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Action:
    publishSubject.onError(new Exception());

    // Effect:
    verify(navigateObserver, only()).onChanged(CommonNavigate.NO_CONNECTION);
  }

  /**
   * Должен вернуть "перейти к ошибке в данных от сервера".
   */
  @Test
  public void setNavigateToServerDataErrorToLiveData() {
    // Given:
    viewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Action:
    publishSubject.onError(new DataMappingException());

    // Effect:
    verify(navigateObserver, only()).onChanged(CommonNavigate.SERVER_DATA_ERROR);
  }

  /**
   * Не должен никуда переходить при ошибке выбора.
   */
  @Test
  public void setNothingToLiveDataForSelectionFail() {
    // Given:
    when(selectedOrderUseCase.setSelectedOrder(any()))
        .thenReturn(Completable.error(NoSuchElementException::new));
    viewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Action:
    viewModel.setSelectedOrder(order);

    // Effect:
    verifyNoInteractions(navigateObserver);
  }

  /**
   * Должен вернуть "перейти к предзаказу" при успешном выборе.
   */
  @Test
  public void setNothingToLiveDataForSelectionSuccess() {
    // Given:
    when(selectedOrderUseCase.setSelectedOrder(any())).thenReturn(Completable.complete());
    viewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Action:
    viewModel.setSelectedOrder(order);

    // Effect:
    verify(navigateObserver, only()).onChanged(PreOrdersListNavigate.PRE_ORDER);
  }
}