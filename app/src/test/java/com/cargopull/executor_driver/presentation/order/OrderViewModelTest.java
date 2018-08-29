package com.cargopull.executor_driver.presentation.order;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.Observer;
import com.cargopull.executor_driver.ViewModelThreadTestRule;
import com.cargopull.executor_driver.entity.Order;
import com.cargopull.executor_driver.gateway.DataMappingException;
import com.cargopull.executor_driver.interactor.OrderUseCase;
import com.cargopull.executor_driver.presentation.CommonNavigate;
import com.cargopull.executor_driver.presentation.ViewState;
import com.cargopull.executor_driver.utils.TimeUtils;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.subjects.PublishSubject;
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
public class OrderViewModelTest {

  @ClassRule
  public static final ViewModelThreadTestRule classRule = new ViewModelThreadTestRule();
  @Rule
  public TestRule rule = new InstantTaskExecutorRule();
  private OrderViewModel viewModel;
  @Mock
  private OrderUseCase orderUseCase;
  @Mock
  private TimeUtils timeUtils;
  @Mock
  private Order order;
  @Mock
  private Order order1;
  @Mock
  private Order order2;
  private PublishSubject<Order> publishSubject;

  @Mock
  private Observer<ViewState<OrderViewActions>> viewStateObserver;
  @Mock
  private Observer<String> navigateObserver;

  @Before
  public void setUp() {
    publishSubject = PublishSubject.create();
    when(orderUseCase.getOrders())
        .thenReturn(publishSubject.toFlowable(BackpressureStrategy.BUFFER));
    viewModel = new OrderViewModelImpl(orderUseCase, timeUtils);
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
   * Должен вернуть состояние вида ожидания изначально.
   */
  @Test
  public void setPendingViewStateToLiveDataInitially() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);

    // Действие:
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(OrderViewStatePending.class));
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
    publishSubject.onError(new Exception());

    // Результат:
    verify(viewStateObserver, only()).onChanged(new OrderViewStatePending(null));
  }

  /**
   * Должен вернуть состояния вида "Бездействие" с полученнымы заказами.
   */
  @Test
  public void setIdleViewStateToLiveData() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(order);
    publishSubject.onNext(order1);
    publishSubject.onNext(order2);

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(new OrderViewStatePending(null));
    inOrder.verify(viewStateObserver).onChanged(new OrderViewStateIdle(
        new OrderItem(order, timeUtils))
    );
    inOrder.verify(viewStateObserver).onChanged(new OrderViewStateIdle(
        new OrderItem(order1, timeUtils))
    );
    inOrder.verify(viewStateObserver).onChanged(new OrderViewStateIdle(
        new OrderItem(order2, timeUtils))
    );
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
    publishSubject.onError(new DataMappingException());

    // Результат:
    verify(navigateObserver, only()).onChanged(CommonNavigate.SERVER_DATA_ERROR);
  }

  /**
   * Должен вернуть "перейти к заказ истек".
   */
  @Test
  public void setNavigateToOrderExpired() {
    // Дано:
    when(orderUseCase.getOrders()).thenReturn(Flowable.create(
        new FlowableOnSubscribe<Order>() {
          private boolean run;

          @Override
          public void subscribe(FlowableEmitter<Order> emitter) {
            if (!run) {
              run = true;
              emitter.onNext(order);
              emitter.onComplete();
            }
          }
        },
        BackpressureStrategy.BUFFER
    ));
    viewModel = new OrderViewModelImpl(orderUseCase, timeUtils);
    viewModel.getNavigationLiveData().observeForever(navigateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:

    // Результат:
    verify(navigateObserver, only()).onChanged(OrderNavigate.ORDER_EXPIRED);
  }
}