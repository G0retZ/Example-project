package com.cargopull.executor_driver.presentation.order;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;
import com.cargopull.executor_driver.ViewModelThreadTestRule;
import com.cargopull.executor_driver.backend.analytics.ErrorReporter;
import com.cargopull.executor_driver.entity.Order;
import com.cargopull.executor_driver.entity.OrderCancelledException;
import com.cargopull.executor_driver.entity.OrderOfferDecisionException;
import com.cargopull.executor_driver.entity.OrderOfferExpiredException;
import com.cargopull.executor_driver.gateway.DataMappingException;
import com.cargopull.executor_driver.interactor.OrderUseCase;
import com.cargopull.executor_driver.presentation.CommonNavigate;
import com.cargopull.executor_driver.presentation.ViewState;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.subjects.PublishSubject;
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
public class OrderViewModelTest {

  @ClassRule
  public static final ViewModelThreadTestRule classRule = new ViewModelThreadTestRule();
  @Rule
  public TestRule rule = new InstantTaskExecutorRule();
  private OrderViewModel viewModel;
  @Mock
  private ErrorReporter errorReporter;
  @Mock
  private OrderUseCase orderUseCase;
  @Mock
  private Order order;
  @Mock
  private Order order1;
  @Mock
  private Order order2;
  @Mock
  private OrderViewActions orderViewActions;
  @Captor
  private ArgumentCaptor<ViewState<OrderViewActions>> viewStateCaptor;
  @Captor
  private ArgumentCaptor<Runnable> runnableCaptor;
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
    viewModel = new OrderViewModelImpl(errorReporter, orderUseCase);
  }

  /* Проверяем отправку ошибок в репортер */

  /**
   * Должен отправить ошибку.
   */
  @Test
  public void reportError() {
    // Действие:
    publishSubject.onError(new DataMappingException());

    // Результат:
    verify(errorReporter, only()).reportError(any(DataMappingException.class));
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
    inOrder.verify(viewStateObserver).onChanged(new OrderViewStateIdle(order));
    inOrder.verify(viewStateObserver).onChanged(new OrderViewStateIdle(order1));
    inOrder.verify(viewStateObserver).onChanged(new OrderViewStateIdle(order2));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояния вида "заказ истек" при получении соответствующей ошибки.
   */
  @Test
  public void setExpiredViewStateToLiveDataForExpiredError() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    PublishSubject<Order> publishSubject = PublishSubject.create();
    when(orderUseCase.getOrders()).thenReturn(
        Observable.create(
            new ObservableOnSubscribe<Order>() {
              private boolean run;

              @Override
              public void subscribe(ObservableEmitter<Order> emitter) {
                if (!run) {
                  run = true;
                  emitter.onNext(order);
                  emitter.onError(new OrderOfferExpiredException("message"));
                } else {
                  emitter.onNext(order2);
                }
              }
            }
        ).startWith(publishSubject)
            .toFlowable(BackpressureStrategy.BUFFER)
    );
    viewModel = new OrderViewModelImpl(errorReporter, orderUseCase);
    viewModel.getNavigationLiveData().observeForever(navigateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onComplete();

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(new OrderViewStatePending(null));
    inOrder.verify(viewStateObserver).onChanged(new OrderViewStateIdle(order));
    inOrder.verify(viewStateObserver).onChanged(new OrderViewStateExpired(
            new OrderViewStateIdle(order),
            "message",
            () -> {
            }
        )
    );
    inOrder.verify(viewStateObserver).onChanged(new OrderViewStateIdle(order2));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояния вида "заказ отменен" при получении соответствующей ошибки.
   */
  @Test
  public void setCancelledViewStateToLiveDataForExpiredError() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    PublishSubject<Order> publishSubject = PublishSubject.create();
    when(orderUseCase.getOrders()).thenReturn(
        Observable.create(
            new ObservableOnSubscribe<Order>() {
              private boolean run;

              @Override
              public void subscribe(ObservableEmitter<Order> emitter) {
                if (!run) {
                  run = true;
                  emitter.onNext(order);
                  emitter.onError(new OrderCancelledException());
                } else {
                  emitter.onNext(order2);
                }
              }
            }
        ).startWith(publishSubject)
            .toFlowable(BackpressureStrategy.BUFFER)
    );
    viewModel = new OrderViewModelImpl(errorReporter, orderUseCase);
    viewModel.getNavigationLiveData().observeForever(navigateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onComplete();

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(new OrderViewStatePending(null));
    inOrder.verify(viewStateObserver).onChanged(new OrderViewStateIdle(order));
    inOrder.verify(viewStateObserver).onChanged(new OrderViewStateCancelled(
            new OrderViewStateIdle(order),
            () -> {
            }
        )
    );
    inOrder.verify(viewStateObserver).onChanged(new OrderViewStateIdle(order2));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Не должен возвращать иных состояний вида при получении ошибки о принятии решения по заказу.
   */
  @Test
  public void setIdleViewStateToLiveDataForExpiredErrorWithoutMessage() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    PublishSubject<Order> publishSubject = PublishSubject.create();
    when(orderUseCase.getOrders()).thenReturn(
        Observable.create(
            new ObservableOnSubscribe<Order>() {
              private boolean run;

              @Override
              public void subscribe(ObservableEmitter<Order> emitter) {
                if (!run) {
                  run = true;
                  emitter.onNext(order);
                  emitter.onError(new OrderOfferDecisionException());
                } else {
                  emitter.onNext(order2);
                }
              }
            }
        ).startWith(publishSubject)
            .toFlowable(BackpressureStrategy.BUFFER)
    );
    viewModel = new OrderViewModelImpl(errorReporter, orderUseCase);
    viewModel.getNavigationLiveData().observeForever(navigateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onComplete();

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(new OrderViewStatePending(null));
    inOrder.verify(viewStateObserver).onChanged(new OrderViewStateIdle(order));
    inOrder.verify(viewStateObserver).onChanged(new OrderViewStateIdle(order2));
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
   * Должен перейти к закрытию карточки.
   */
  @Test
  public void setNavigateToCloseForCancelledMessageConsumed() {
    // Дано:
    PublishSubject<Order> publishSubject = PublishSubject.create();
    when(orderUseCase.getOrders()).thenReturn(
        Observable.create(
            new ObservableOnSubscribe<Order>() {
              private boolean run;

              @Override
              public void subscribe(ObservableEmitter<Order> emitter) {
                if (!run) {
                  run = true;
                  emitter.onError(new OrderCancelledException());
                }
              }
            }
        ).startWith(publishSubject)
            .toFlowable(BackpressureStrategy.BUFFER)
    );
    viewModel = new OrderViewModelImpl(errorReporter, orderUseCase);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);
    viewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Действие:
    publishSubject.onComplete();
    verify(viewStateObserver, times(2)).onChanged(viewStateCaptor.capture());
    viewStateCaptor.getAllValues().get(1).apply(orderViewActions);
    verify(orderViewActions).showPersistentDialog(anyInt(), runnableCaptor.capture());
    runnableCaptor.getValue().run();

    // Результат:
    verify(navigateObserver, only()).onChanged(OrderNavigate.CLOSE);
  }

  /**
   * Должен перейти к закрытию карточки.
   */
  @Test
  public void setNavigateToCloseForExpiredMessageConsumed() {
    // Дано:
    PublishSubject<Order> publishSubject = PublishSubject.create();
    when(orderUseCase.getOrders()).thenReturn(
        Observable.create(
            new ObservableOnSubscribe<Order>() {
              private boolean run;

              @Override
              public void subscribe(ObservableEmitter<Order> emitter) {
                if (!run) {
                  run = true;
                  emitter.onError(new OrderOfferExpiredException("message"));
                }
              }
            }
        ).startWith(publishSubject)
            .toFlowable(BackpressureStrategy.BUFFER)
    );
    viewModel = new OrderViewModelImpl(errorReporter, orderUseCase);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);
    viewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Действие:
    publishSubject.onComplete();
    verify(viewStateObserver, times(2)).onChanged(viewStateCaptor.capture());
    viewStateCaptor.getAllValues().get(1).apply(orderViewActions);
    verify(orderViewActions).showPersistentDialog(anyString(), runnableCaptor.capture());
    runnableCaptor.getValue().run();

    // Результат:
    verify(navigateObserver, only()).onChanged(OrderNavigate.CLOSE);
  }
}