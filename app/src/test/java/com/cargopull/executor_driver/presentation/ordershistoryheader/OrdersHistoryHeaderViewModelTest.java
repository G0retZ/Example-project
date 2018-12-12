package com.cargopull.executor_driver.presentation.ordershistoryheader;

import static org.mockito.ArgumentMatchers.any;
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
import org.joda.time.DateTime;
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
  private SingleEmitter<OrdersHistorySummary> singleEmitter;

  @Mock
  private Observer<ViewState<OrdersHistoryHeaderViewActions>> viewStateObserver;

  @Before
  public void setUp() {
    when(gateway.getOrdersHistorySummary(anyLong(), anyLong()))
        .thenReturn(Single.create(emitter -> singleEmitter = emitter));
    when(timeUtils.currentTimeMillis()).thenReturn(
        DateTime.now().withDate(1974, 8, 10).withTime(7, 53, 22, 0).getMillis()
    );
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
    verify(gateway, only()).getOrdersHistorySummary(
        DateTime.now().withDate(1974, 8, 1).withMillisOfDay(0).getMillis(),
        DateTime.now().withDate(1974, 8, 10).withTime(7, 53, 22, 0).getMillis()
    );
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
    verify(gateway, only()).getOrdersHistorySummary(
        DateTime.now().withDate(1974, 8, 1).withMillisOfDay(0).getMillis(),
        DateTime.now().withDate(1974, 8, 10).withTime(7, 53, 22, 0).getMillis()
    );
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
    inOrder.verify(gateway).getOrdersHistorySummary(
        DateTime.now().withDate(1974, 8, 1).withMillisOfDay(0).getMillis(),
        DateTime.now().withDate(1974, 8, 10).withTime(7, 53, 22, 0).getMillis()
    );
    inOrder.verify(gateway).getOrdersHistorySummary(
        DateTime.now().withDate(1974, 7, 1).withMillisOfDay(0).getMillis(),
        DateTime.now().withDate(1974, 7, 31).withTime(23, 59, 59, 999).getMillis()
    );
    inOrder.verify(gateway).getOrdersHistorySummary(
        DateTime.now().withDate(1974, 5, 1).withMillisOfDay(0).getMillis(),
        DateTime.now().withDate(1974, 5, 31).withTime(23, 59, 59, 999).getMillis()
    );
    inOrder.verify(gateway).getOrdersHistorySummary(
        DateTime.now().withDate(1974, 3, 1).withMillisOfDay(0).getMillis(),
        DateTime.now().withDate(1974, 3, 31).withTime(23, 59, 59, 999).getMillis()
    );
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
    verify(gateway, times(4)).getOrdersHistorySummary(
        DateTime.now().withDate(1974, 8, 1).withMillisOfDay(0).getMillis(),
        DateTime.now().withDate(1974, 8, 10).withTime(7, 53, 22, 0).getMillis()
    );
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
   * Должен вернуть состояния вида "Загружено" с полученной сводкой истории заказов.
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
        new OrdersHistoryHeaderViewStateLoaded(ordersHistorySummary)
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
}