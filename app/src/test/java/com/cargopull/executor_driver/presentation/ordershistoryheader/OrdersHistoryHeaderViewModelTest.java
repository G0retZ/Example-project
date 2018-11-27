package com.cargopull.executor_driver.presentation.ordershistoryheader;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;
import com.cargopull.executor_driver.UseCaseThreadTestRule;
import com.cargopull.executor_driver.ViewModelThreadTestRule;
import com.cargopull.executor_driver.backend.analytics.ErrorReporter;
import com.cargopull.executor_driver.entity.OrdersHistorySummary;
import com.cargopull.executor_driver.gateway.DataMappingException;
import com.cargopull.executor_driver.interactor.OrdersHistorySummaryGateway;
import com.cargopull.executor_driver.presentation.ViewState;
import com.cargopull.executor_driver.utils.TimeUtils;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class OrdersHistoryHeaderViewModelTest {

  @ClassRule
  public static final ViewModelThreadTestRule classRule = new ViewModelThreadTestRule();
  @ClassRule
  public static final UseCaseThreadTestRule classRule2 = new UseCaseThreadTestRule();
  @Rule
  public TestRule rule = new InstantTaskExecutorRule();
  private OrdersHistoryHeaderViewModel viewModel;
  @Mock
  private ErrorReporter errorReporter;
  @Mock
  private TimeUtils timeUtils;
  @Mock
  private OrdersHistorySummaryGateway gateway;
  @Mock
  private OrdersHistorySummary ordersHistorySummary;
  @Mock
  private OrdersHistoryHeaderViewActions ordersHistoryHeaderViewActions;
  @Captor
  private ArgumentCaptor<ViewState<OrdersHistoryHeaderViewActions>> viewStateCaptor;
  @Captor
  private ArgumentCaptor<Runnable> runnableCaptor;
  private SingleEmitter<OrdersHistorySummary> singleEmitter;

  @Mock
  private Observer<ViewState<OrdersHistoryHeaderViewActions>> viewStateObserver;

  @Before
  public void setUp() {
    when(ordersHistoryHeaderViewActions.getCurrencyFormat()).thenReturn("");
    when(gateway.getOrdersHistorySummary(anyLong(), anyLong()))
        .thenReturn(Single.create(emitter -> singleEmitter = emitter));
    when(timeUtils.currentTimeMillis()).thenReturn(145353202000L);
    viewModel = new OrdersHistoryHeaderViewModelImpl(0, errorReporter, timeUtils, gateway);
  }

  /* Проверяем отправку ошибок в репортер */

  /**
   * Должен отправить ошибку.
   */
  @Test
  public void reportError() {
    // Действие:
    singleEmitter.onError(new DataMappingException());

    // Результат:
    verify(errorReporter, only()).reportError(any(DataMappingException.class));
  }

  /* Тетсируем работу со временем. */

  /**
   * Должен запросить текущий таймстамп изначально.
   */
  @Test
  public void askForCurrentTimeStampInitially() {
    // Результат:
    verify(timeUtils, only()).currentTimeMillis();
  }

  /**
   * Не должен запрашивать текущий таймстамп на подписках.
   */
  @Test
  public void doNotAskForCurrentTimeStampOnErrors() {
    // Действие:
    viewModel.getViewStateLiveData();
    viewModel.getNavigationLiveData();
    viewModel.getViewStateLiveData();
    viewModel.getNavigationLiveData();

    // Результат:
    verify(timeUtils, only()).currentTimeMillis();
  }

  /**
   * Должен запросить текущий таймстамп повторно при повторе загрузки.
   */
  @Test
  public void askForCurrentTimeStampAgainIfLoggedIn() {
    // Действие:
    singleEmitter.onError(new DataMappingException());
    viewModel.retry();
    singleEmitter.onError(new DataMappingException());
    viewModel.retry();
    singleEmitter.onError(new DataMappingException());
    viewModel.retry();

    // Результат:
    verify(timeUtils, times(4)).currentTimeMillis();
    verifyNoMoreInteractions(timeUtils);
  }

  /* Тетсируем работу с гейтвеем сводки истории заказов. */

  /**
   * Должен просить гейтвей получить сводку истории заказов за текущий месяц.
   */
  @Test
  public void askUseCaseForOrdersInitially() {
    // Результат:
    verify(gateway, only()).getOrdersHistorySummary(144536400000L, 145353202000L);
  }

  /**
   * Не должен просить гейтвей получать сводки истории заказов на подписках.
   */
  @Test
  public void doNotAskUseCaseForOrdersOnSubscriptions() {
    // Действие:
    viewModel.getViewStateLiveData();
    viewModel.getNavigationLiveData();
    viewModel.getViewStateLiveData();
    viewModel.getNavigationLiveData();

    // Результат:
    verify(gateway, only()).getOrdersHistorySummary(144536400000L, 145353202000L);
  }

  /**
   * Должен просить гейтвей получить сводки истории заказов за выбранные месяцы в прошлом.
   */
  @Test
  public void askUseCaseForOrdersForSelectedMonths() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(gateway);

    // Действие:
    new OrdersHistoryHeaderViewModelImpl(1, errorReporter, timeUtils, gateway);
    new OrdersHistoryHeaderViewModelImpl(3, errorReporter, timeUtils, gateway);
    new OrdersHistoryHeaderViewModelImpl(5, errorReporter, timeUtils, gateway);

    // Результат:
    inOrder.verify(gateway).getOrdersHistorySummary(144536400000L, 145353202000L);
    inOrder.verify(gateway).getOrdersHistorySummary(141858000000L, 144536399999L);
    inOrder.verify(gateway).getOrdersHistorySummary(136587600000L, 139265999999L);
    inOrder.verify(gateway).getOrdersHistorySummary(131317200000L, 133995599999L);
    verifyNoMoreInteractions(gateway);
  }

  /**
   * Должен просить гейтвей получать сводки истории заказов повторно только если была ошибка
   * загрузки.
   */
  @Test
  public void askUseCaseForOrdersAgainAfterFailOnly() {
    // Действие:
    singleEmitter.onError(new Exception());
    viewModel.retry();
    singleEmitter.onError(new Exception());
    viewModel.retry();
    singleEmitter.onError(new Exception());
    viewModel.retry();
    singleEmitter.onSuccess(ordersHistorySummary);
    viewModel.retry();
    viewModel.retry();
    viewModel.retry();

    // Результат:
    verify(gateway, times(4)).getOrdersHistorySummary(144536400000L, 145353202000L);
    verifyNoMoreInteractions(gateway);
  }

  /* Тетсируем переключение состояний. */

  /**
   * Должен вернуть состояние вида ожидания изначально.
   */
  @Test
  public void setPendingViewStateToLiveDataInitially() {
    // Дано:
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Результат:
    verify(viewStateObserver, only()).onChanged(any(OrdersHistoryHeaderViewStatePending.class));
  }

  /**
   * Должен вернуть состояния вида "Минимальный" с полученной сводкой истории заказов.
   */
  @Test
  public void setIdleViewStateToLiveData() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    singleEmitter.onSuccess(ordersHistorySummary);

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(OrdersHistoryHeaderViewStatePending.class));
    inOrder.verify(viewStateObserver).onChanged(
        new OrdersHistoryHeaderViewStateMinimized(ordersHistorySummary, () -> {
        })
    );
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояния вида "ошибка" при получении ошибки.
   */
  @Test
  public void setErrorViewStateToLiveData() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    singleEmitter.onError(new DataMappingException());

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(OrdersHistoryHeaderViewStatePending.class));
    inOrder.verify(viewStateObserver).onChanged(any(OrdersHistoryHeaderViewStateError.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен переключать состояния вида между "Минимальный" и "Максимальный".
   */
  @Test
  public void switchViewStatesBetweenMaximizedAndMinimized() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver, ordersHistoryHeaderViewActions);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    singleEmitter.onSuccess(ordersHistorySummary);

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(OrdersHistoryHeaderViewStatePending.class));
    inOrder.verify(viewStateObserver).onChanged(viewStateCaptor.capture());
    assertEquals(viewStateCaptor.getValue(),
        new OrdersHistoryHeaderViewStateMinimized(ordersHistorySummary, () -> {
        })
    );
    viewStateCaptor.getValue().apply(ordersHistoryHeaderViewActions);
    inOrder.verify(ordersHistoryHeaderViewActions).setClickAction(anyInt(), runnableCaptor.capture());
    runnableCaptor.getValue().run();
    inOrder.verify(viewStateObserver).onChanged(viewStateCaptor.capture());
    assertEquals(viewStateCaptor.getValue(),
        new OrdersHistoryHeaderViewStateMaximized(ordersHistorySummary, () -> {
        })
    );
    viewStateCaptor.getValue().apply(ordersHistoryHeaderViewActions);
    inOrder.verify(ordersHistoryHeaderViewActions).setClickAction(anyInt(), runnableCaptor.capture());
    runnableCaptor.getValue().run();
    inOrder.verify(viewStateObserver).onChanged(viewStateCaptor.capture());
    assertEquals(viewStateCaptor.getValue(),
        new OrdersHistoryHeaderViewStateMinimized(ordersHistorySummary, () -> {
        })
    );
    viewStateCaptor.getValue().apply(ordersHistoryHeaderViewActions);
    inOrder.verify(ordersHistoryHeaderViewActions).setClickAction(anyInt(), runnableCaptor.capture());
    runnableCaptor.getValue().run();
    inOrder.verify(viewStateObserver).onChanged(viewStateCaptor.capture());
    assertEquals(viewStateCaptor.getValue(),
        new OrdersHistoryHeaderViewStateMaximized(ordersHistorySummary, () -> {
        })
    );
    viewStateCaptor.getValue().apply(ordersHistoryHeaderViewActions);
    inOrder.verify(ordersHistoryHeaderViewActions).setClickAction(anyInt(), runnableCaptor.capture());
    runnableCaptor.getValue().run();
    inOrder.verify(viewStateObserver).onChanged(viewStateCaptor.capture());
    assertEquals(viewStateCaptor.getValue(),
        new OrdersHistoryHeaderViewStateMinimized(ordersHistorySummary, () -> {
        })
    );
    viewStateCaptor.getValue().apply(ordersHistoryHeaderViewActions);
    inOrder.verify(ordersHistoryHeaderViewActions).setClickAction(anyInt(), runnableCaptor.capture());
    runnableCaptor.getValue().run();
    inOrder.verify(viewStateObserver).onChanged(viewStateCaptor.capture());
    assertEquals(viewStateCaptor.getValue(),
        new OrdersHistoryHeaderViewStateMaximized(ordersHistorySummary, () -> {
        })
    );
    verifyNoMoreInteractions(viewStateObserver);
  }
}