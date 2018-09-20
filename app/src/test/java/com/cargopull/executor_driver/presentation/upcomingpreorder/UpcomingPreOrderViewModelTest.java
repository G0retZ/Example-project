package com.cargopull.executor_driver.presentation.upcomingpreorder;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.Observer;
import com.cargopull.executor_driver.ViewModelThreadTestRule;
import com.cargopull.executor_driver.entity.Order;
import com.cargopull.executor_driver.entity.OrderCancelledException;
import com.cargopull.executor_driver.entity.OrderOfferDecisionException;
import com.cargopull.executor_driver.entity.OrderOfferExpiredException;
import com.cargopull.executor_driver.gateway.DataMappingException;
import com.cargopull.executor_driver.interactor.OrderUseCase;
import com.cargopull.executor_driver.presentation.CommonNavigate;
import com.cargopull.executor_driver.presentation.ViewState;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
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
public class UpcomingPreOrderViewModelTest {

  @ClassRule
  public static final ViewModelThreadTestRule classRule = new ViewModelThreadTestRule();
  @Rule
  public TestRule rule = new InstantTaskExecutorRule();
  private UpcomingPreOrderViewModel viewModel;
  @Mock
  private OrderUseCase orderUseCase;
  @Mock
  private Order order;
  @Mock
  private Order order1;
  @Mock
  private Order order2;
  private FlowableEmitter<Order> emitter;

  @Mock
  private Observer<ViewState<UpcomingPreOrderViewActions>> viewStateObserver;
  @Mock
  private Observer<String> navigateObserver;

  @Before
  public void setUp() {
    when(orderUseCase.getOrders()).thenReturn(
        Flowable.create(e -> emitter = e, BackpressureStrategy.BUFFER)
    );
    viewModel = new UpcomingPreOrderViewModelImpl(orderUseCase);
  }

  /* Тетсируем работу с юзкейсом заказа. */

  /**
   * Должен просить юзкейс получать заказы при создании.
   */
  @Test
  public void askUseCaseForOrdersInitially() {
    // Результат:
    verify(orderUseCase, only()).getOrders();
  }

  /**
   * Не должен просить юзкейс получать заказы на подписках.
   */
  @Test
  public void doNotAskUseCaseForOrdersOnSubscriptions() {
    // Действие:
    viewModel.getViewStateLiveData();
    viewModel.getNavigationLiveData();
    viewModel.getViewStateLiveData();
    viewModel.getNavigationLiveData();

    // Результат:
    verify(orderUseCase, only()).getOrders();
  }

  /* Тетсируем переключение состояний. */

  /**
   * Должен вернуть состояние вида недоступности изначально.
   */
  @Test
  public void setUnAvailableViewStateToLiveDataInitially() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);

    // Действие:
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(UpcomingPreOrderViewStateUnAvailable.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Не должен давать иных состояний вида если была ошибка.
   */
  @Test
  public void doNotSetAnyViewStateToLiveDataForError() {
    // Дано:
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    emitter.onError(new Exception());

    // Результат:
    verify(viewStateObserver, only()).onChanged(any(UpcomingPreOrderViewStateUnAvailable.class));
  }

  /**
   * Должен вернуть состояния вида доступности полученными заказами.
   */
  @Test
  public void setIdleViewStateToLiveData() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    emitter.onNext(order);
    emitter.onNext(order1);
    emitter.onNext(order2);

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(UpcomingPreOrderViewStateUnAvailable.class));
    inOrder.verify(viewStateObserver, times(3))
        .onChanged(any(UpcomingPreOrderViewStateAvailable.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояния вида "заказ истек" после ошибки принятия решения по заказу.
   */
  @Test
  public void setExpiredViewStateToLiveDataForOrderOfferDecision() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel = new UpcomingPreOrderViewModelImpl(orderUseCase);
    viewModel.getNavigationLiveData().observeForever(navigateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    emitter.onNext(order);
    emitter.onError(new OrderOfferDecisionException());
    emitter.onNext(order2);

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(UpcomingPreOrderViewStateUnAvailable.class));
    inOrder.verify(viewStateObserver).onChanged(any(UpcomingPreOrderViewStateAvailable.class));
    inOrder.verify(viewStateObserver).onChanged(any(UpcomingPreOrderViewStateUnAvailable.class));
    inOrder.verify(viewStateObserver).onChanged(any(UpcomingPreOrderViewStateAvailable.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояния вида "заказ истек" после ошибки актуальности заказа.
   */
  @Test
  public void setExpiredViewStateToLiveData() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel = new UpcomingPreOrderViewModelImpl(orderUseCase);
    viewModel.getNavigationLiveData().observeForever(navigateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    emitter.onNext(order);
    emitter.onError(new OrderOfferExpiredException(""));
    emitter.onNext(order2);

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(UpcomingPreOrderViewStateUnAvailable.class));
    inOrder.verify(viewStateObserver).onChanged(any(UpcomingPreOrderViewStateAvailable.class));
    inOrder.verify(viewStateObserver).onChanged(any(UpcomingPreOrderViewStateUnAvailable.class));
    inOrder.verify(viewStateObserver).onChanged(any(UpcomingPreOrderViewStateAvailable.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояния вида "заказ истек" после ошибки отмены заказа.
   */
  @Test
  public void setCancelledViewStateToLiveData() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel = new UpcomingPreOrderViewModelImpl(orderUseCase);
    viewModel.getNavigationLiveData().observeForever(navigateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    emitter.onNext(order);
    emitter.onError(new OrderCancelledException(""));
    emitter.onNext(order2);

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(UpcomingPreOrderViewStateUnAvailable.class));
    inOrder.verify(viewStateObserver).onChanged(any(UpcomingPreOrderViewStateAvailable.class));
    inOrder.verify(viewStateObserver).onChanged(any(UpcomingPreOrderViewStateUnAvailable.class));
    inOrder.verify(viewStateObserver).onChanged(any(UpcomingPreOrderViewStateAvailable.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /* Тестируем навигацию. */

  /**
   * Должен вернуть "перейти к ошибке данных сервера".
   */
  @Test
  public void setNavigateToServerDataError() {
    // Дано:
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);
    viewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Действие:
    emitter.onError(new DataMappingException());

    // Результат:
    verify(navigateObserver, only()).onChanged(CommonNavigate.SERVER_DATA_ERROR);
  }

  /**
   * Должен перейти к подтверждению заказа.
   */
  @Test
  public void setNavigateToCloseForMessageConsumed() {
    // Дано:
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);
    viewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Действие:
    viewModel.preOrderConsumed();

    // Результат:
    verify(navigateObserver, only()).onChanged(UpcomingPreOrderNavigate.UPCOMING_PRE_ORDER);
  }
}