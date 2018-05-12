package com.fasten.executor_driver.presentation.driverorderconfirmation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.Observer;
import com.fasten.executor_driver.backend.web.NoNetworkException;
import com.fasten.executor_driver.entity.NoOrdersAvailableException;
import com.fasten.executor_driver.entity.Order;
import com.fasten.executor_driver.gateway.DataMappingException;
import com.fasten.executor_driver.interactor.DriverOrderConfirmationUseCase;
import com.fasten.executor_driver.presentation.ViewState;
import com.fasten.executor_driver.utils.TimeUtils;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
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
public class DriverOrderConfirmationViewModelTest {

  @Rule
  public TestRule rule = new InstantTaskExecutorRule();
  private DriverOrderConfirmationViewModel driverOrderConfirmationViewModel;
  @Mock
  private DriverOrderConfirmationUseCase driverOrderConfirmationUseCase;
  @Mock
  private TimeUtils timeUtils;
  @Mock
  private Order order;
  @Mock
  private Order order1;
  @Mock
  private Order order2;

  @Mock
  private Observer<ViewState<DriverOrderConfirmationViewActions>> viewStateObserver;

  @Before
  public void setUp() {
    RxJavaPlugins.setSingleSchedulerHandler(scheduler -> Schedulers.trampoline());
    RxAndroidPlugins.setInitMainThreadSchedulerHandler(scheduler -> Schedulers.trampoline());
    when(driverOrderConfirmationUseCase.getOrders()).thenReturn(Flowable.never());
    when(driverOrderConfirmationUseCase.sendDecision(anyBoolean())).thenReturn(Completable.never());
    driverOrderConfirmationViewModel = new DriverOrderConfirmationViewModelImpl(
        driverOrderConfirmationUseCase, timeUtils);
  }

  /* Тетсируем работу с юзкейсом заказа. */

  /**
   * Должен просить юзкейс получать заказы, при первой и только при первой подписке.
   */
  @Test
  public void askUseCaseForOrdersInitially() {
    // Действие:
    driverOrderConfirmationViewModel.getViewStateLiveData();
    driverOrderConfirmationViewModel.getViewStateLiveData();
    driverOrderConfirmationViewModel.getViewStateLiveData();

    // Результат:
    verify(driverOrderConfirmationUseCase, only()).getOrders();
  }

  /**
   * Должен попросить юзкейс передать принятие заказа.
   */
  @Test
  public void askUseCaseToSendOrderAccepted() {
    // Дано:
    when(driverOrderConfirmationUseCase.sendDecision(anyBoolean()))
        .thenReturn(Completable.complete());

    // Действие:
    driverOrderConfirmationViewModel.acceptOrder();

    // Результат:
    verify(driverOrderConfirmationUseCase, only()).sendDecision(true);
  }

  /**
   * Должен попросить юзкейс передать отказ от заказа.
   */
  @Test
  public void askUseCaseToSendOrderDeclined() {
    // Дано:
    when(driverOrderConfirmationUseCase.sendDecision(anyBoolean()))
        .thenReturn(Completable.complete());

    // Действие:
    driverOrderConfirmationViewModel.declineOrder();

    // Результат:
    verify(driverOrderConfirmationUseCase, only()).sendDecision(false);
  }

  /**
   * Не должен трогать юзкейс, если предыдущий запрос передачи решения еще не завершился.
   */
  @Test
  public void DoNotTouchUseCaseDuringOrderSetting() {
    // Действие:
    driverOrderConfirmationViewModel.acceptOrder();
    driverOrderConfirmationViewModel.declineOrder();
    driverOrderConfirmationViewModel.acceptOrder();

    // Результат:
    verify(driverOrderConfirmationUseCase, only()).sendDecision(true);
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
    driverOrderConfirmationViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(DriverOrderConfirmationViewStatePending.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида "Ошибка" нет сети.
   */
  @Test
  public void setNoNetworkErrorViewStateToLiveData() {
    // Дано:
    PublishSubject<Order> publishSubject = PublishSubject.create();
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(driverOrderConfirmationUseCase.getOrders())
        .thenReturn(publishSubject.toFlowable(BackpressureStrategy.BUFFER));
    driverOrderConfirmationViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onError(new NoNetworkException());

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(new DriverOrderConfirmationViewStatePending(null));
    inOrder.verify(viewStateObserver)
        .onChanged(new DriverOrderConfirmationViewStateNetworkError(null));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида "Ошибка" нет сети.
   */
  @Test
  public void setNoNetworkErrorViewStateToLiveDataForMappingError() {
    // Дано:
    PublishSubject<Order> publishSubject = PublishSubject.create();
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(driverOrderConfirmationUseCase.getOrders())
        .thenReturn(publishSubject.toFlowable(BackpressureStrategy.BUFFER));
    driverOrderConfirmationViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onError(new DataMappingException());

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(new DriverOrderConfirmationViewStatePending(null));
    inOrder.verify(viewStateObserver)
        .onChanged(new DriverOrderConfirmationViewStateNetworkError(null));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида "Ошибка" нет доступных заказов.
   */
  @Test
  public void setNoOrderAvailableErrorViewStateToLiveData() {
    // Дано:
    PublishSubject<Order> publishSubject = PublishSubject.create();
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(driverOrderConfirmationUseCase.getOrders())
        .thenReturn(publishSubject.toFlowable(BackpressureStrategy.BUFFER));
    driverOrderConfirmationViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onError(new NoOrdersAvailableException());

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(new DriverOrderConfirmationViewStatePending(null));
    inOrder.verify(viewStateObserver)
        .onChanged(new DriverOrderConfirmationViewStateUnavailableError(null));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояния вида "Бездействие" с полученнымы заказами.
   */
  @Test
  public void setIdleViewStateToLiveData() {
    // Дано:
    PublishSubject<Order> publishSubject = PublishSubject.create();
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(driverOrderConfirmationUseCase.getOrders())
        .thenReturn(publishSubject.toFlowable(BackpressureStrategy.BUFFER));
    driverOrderConfirmationViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(order);
    publishSubject.onNext(order1);
    publishSubject.onNext(order2);

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(new DriverOrderConfirmationViewStatePending(null));
    inOrder.verify(viewStateObserver).onChanged(new DriverOrderConfirmationViewStateIdle(
        new OrderItem(order, timeUtils))
    );
    inOrder.verify(viewStateObserver).onChanged(new DriverOrderConfirmationViewStateIdle(
        new OrderItem(order1, timeUtils))
    );
    inOrder.verify(viewStateObserver).onChanged(new DriverOrderConfirmationViewStateIdle(
        new OrderItem(order2, timeUtils))
    );
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида "В процессе".
   */
  @Test
  public void setPendingViewStateWithoutOrderToLiveDataForAccept() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    driverOrderConfirmationViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    driverOrderConfirmationViewModel.acceptOrder();

    // Результат:
    inOrder.verify(viewStateObserver, times(2))
        .onChanged(new DriverOrderConfirmationViewStatePending(null));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида "В процессе".
   */
  @Test
  public void setPendingViewStateWithoutOrderToLiveDataForDecline() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    driverOrderConfirmationViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    driverOrderConfirmationViewModel.declineOrder();

    // Результат:
    inOrder.verify(viewStateObserver, times(2))
        .onChanged(new DriverOrderConfirmationViewStatePending(null));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида "Ошибка" сети.
   */
  @Test
  public void setNoNetworkErrorViewStateWithoutOrderToLiveDataForAccept() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(driverOrderConfirmationUseCase.sendDecision(anyBoolean()))
        .thenReturn(Completable.error(NoNetworkException::new));
    driverOrderConfirmationViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    driverOrderConfirmationViewModel.acceptOrder();

    // Результат:
    inOrder.verify(viewStateObserver, times(2))
        .onChanged(new DriverOrderConfirmationViewStatePending(null));
    inOrder.verify(viewStateObserver)
        .onChanged(new DriverOrderConfirmationViewStateNetworkError(null));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида "Ошибка" сети.
   */
  @Test
  public void setNoNetworkErrorViewStateWithoutOrderToLiveDataForDecline() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(driverOrderConfirmationUseCase.sendDecision(anyBoolean()))
        .thenReturn(Completable.error(NoNetworkException::new));
    driverOrderConfirmationViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    driverOrderConfirmationViewModel.declineOrder();

    // Результат:
    inOrder.verify(viewStateObserver, times(2))
        .onChanged(new DriverOrderConfirmationViewStatePending(null));
    inOrder.verify(viewStateObserver)
        .onChanged(new DriverOrderConfirmationViewStateNetworkError(null));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида "Ошибка" нет доступных заказов.
   */
  @Test
  public void setNoOrdersAvailableErrorViewStateWithoutOrderToLiveDataForAccept() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(driverOrderConfirmationUseCase.sendDecision(anyBoolean()))
        .thenReturn(Completable.error(NoOrdersAvailableException::new));
    driverOrderConfirmationViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    driverOrderConfirmationViewModel.acceptOrder();

    // Результат:
    inOrder.verify(viewStateObserver, times(2))
        .onChanged(new DriverOrderConfirmationViewStatePending(null));
    inOrder.verify(viewStateObserver)
        .onChanged(new DriverOrderConfirmationViewStateUnavailableError(null));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида "Ошибка" нет доступных заказов.
   */
  @Test
  public void setNoOrdersAvailableErrorViewStateWithoutOrderToLiveDataForDecline() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(driverOrderConfirmationUseCase.sendDecision(anyBoolean()))
        .thenReturn(Completable.error(NoOrdersAvailableException::new));
    driverOrderConfirmationViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    driverOrderConfirmationViewModel.declineOrder();

    // Результат:
    inOrder.verify(viewStateObserver, times(2))
        .onChanged(new DriverOrderConfirmationViewStatePending(null));
    inOrder.verify(viewStateObserver)
        .onChanged(new DriverOrderConfirmationViewStateUnavailableError(null));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Не должен возвращать никакого состояния вида.
   */
  @Test
  public void setNoViewStateToLiveDataForAcceptWithoutOrder() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(driverOrderConfirmationUseCase.sendDecision(anyBoolean()))
        .thenReturn(Completable.complete());
    driverOrderConfirmationViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    driverOrderConfirmationViewModel.acceptOrder();

    // Результат:
    inOrder.verify(viewStateObserver, times(2))
        .onChanged(new DriverOrderConfirmationViewStatePending(null));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Не должен возвращать никакого состояния вида.
   */
  @Test
  public void setNoViewStateToLiveDataForDeclineWithoutOrder() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(driverOrderConfirmationUseCase.sendDecision(anyBoolean()))
        .thenReturn(Completable.complete());
    driverOrderConfirmationViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    driverOrderConfirmationViewModel.declineOrder();

    // Результат:
    inOrder.verify(viewStateObserver, times(2))
        .onChanged(new DriverOrderConfirmationViewStatePending(null));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида "В процессе".
   */
  @Test
  public void setPendingViewStateToLiveDataForAccept() {
    // Дано:
    PublishSubject<Order> publishSubject = PublishSubject.create();
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(driverOrderConfirmationUseCase.getOrders())
        .thenReturn(publishSubject.toFlowable(BackpressureStrategy.BUFFER));
    driverOrderConfirmationViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(order);
    driverOrderConfirmationViewModel.acceptOrder();

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(new DriverOrderConfirmationViewStatePending(null));
    inOrder.verify(viewStateObserver).onChanged(new DriverOrderConfirmationViewStateIdle(
        new OrderItem(order, timeUtils)
    ));
    inOrder.verify(viewStateObserver).onChanged(new DriverOrderConfirmationViewStatePending(
        new OrderItem(order, timeUtils)
    ));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида "В процессе".
   */
  @Test
  public void setPendingViewStateToLiveDataForDecline() {
    // Дано:
    PublishSubject<Order> publishSubject = PublishSubject.create();
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(driverOrderConfirmationUseCase.getOrders())
        .thenReturn(publishSubject.toFlowable(BackpressureStrategy.BUFFER));
    driverOrderConfirmationViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(order);
    driverOrderConfirmationViewModel.declineOrder();

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(new DriverOrderConfirmationViewStatePending(null));
    inOrder.verify(viewStateObserver).onChanged(new DriverOrderConfirmationViewStateIdle(
        new OrderItem(order, timeUtils)
    ));
    inOrder.verify(viewStateObserver).onChanged(new DriverOrderConfirmationViewStatePending(
        new OrderItem(order, timeUtils)
    ));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида "В процессе", и только 1 раз.
   */
  @Test
  public void setPendingViewStateToLiveDataForTimeout() {
    // Дано:
    PublishSubject<Order> publishSubject = PublishSubject.create();
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(driverOrderConfirmationUseCase.getOrders())
        .thenReturn(publishSubject.toFlowable(BackpressureStrategy.BUFFER));
    driverOrderConfirmationViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(order);
    driverOrderConfirmationViewModel.counterTimeOut();
    driverOrderConfirmationViewModel.counterTimeOut();
    driverOrderConfirmationViewModel.counterTimeOut();

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(new DriverOrderConfirmationViewStatePending(null));
    inOrder.verify(viewStateObserver).onChanged(new DriverOrderConfirmationViewStateIdle(
        new OrderItem(order, timeUtils)
    ));
    inOrder.verify(viewStateObserver).onChanged(new DriverOrderConfirmationViewStatePending(
        new OrderItem(order, timeUtils)
    ));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида "Ошибка" сети.
   */
  @Test
  public void setNoNetworkErrorViewStateToLiveDataForAccept() {
    // Дано:
    PublishSubject<Order> publishSubject = PublishSubject.create();
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(driverOrderConfirmationUseCase.getOrders())
        .thenReturn(publishSubject.toFlowable(BackpressureStrategy.BUFFER));
    when(driverOrderConfirmationUseCase.sendDecision(anyBoolean()))
        .thenReturn(Completable.error(NoNetworkException::new));
    driverOrderConfirmationViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(order);
    driverOrderConfirmationViewModel.acceptOrder();

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(new DriverOrderConfirmationViewStatePending(null));
    inOrder.verify(viewStateObserver).onChanged(new DriverOrderConfirmationViewStateIdle(
        new OrderItem(order, timeUtils)
    ));
    inOrder.verify(viewStateObserver).onChanged(new DriverOrderConfirmationViewStatePending(
        new OrderItem(order, timeUtils)
    ));
    inOrder.verify(viewStateObserver).onChanged(new DriverOrderConfirmationViewStateNetworkError(
        new OrderItem(order, timeUtils)
    ));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида "Ошибка" сети.
   */
  @Test
  public void setNoNetworkErrorViewStateToLiveDataForDecline() {
    // Дано:
    PublishSubject<Order> publishSubject = PublishSubject.create();
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(driverOrderConfirmationUseCase.getOrders())
        .thenReturn(publishSubject.toFlowable(BackpressureStrategy.BUFFER));
    when(driverOrderConfirmationUseCase.sendDecision(anyBoolean()))
        .thenReturn(Completable.error(NoNetworkException::new));
    driverOrderConfirmationViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(order);
    driverOrderConfirmationViewModel.declineOrder();

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(new DriverOrderConfirmationViewStatePending(null));
    inOrder.verify(viewStateObserver).onChanged(new DriverOrderConfirmationViewStateIdle(
        new OrderItem(order, timeUtils)
    ));
    inOrder.verify(viewStateObserver).onChanged(new DriverOrderConfirmationViewStatePending(
        new OrderItem(order, timeUtils)
    ));
    inOrder.verify(viewStateObserver).onChanged(new DriverOrderConfirmationViewStateNetworkError(
        new OrderItem(order, timeUtils)
    ));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида "Ошибка" нет доступных заказов.
   */
  @Test
  public void setNoOrdersAvailableErrorViewStateToLiveDataForAccept() {
    // Дано:
    PublishSubject<Order> publishSubject = PublishSubject.create();
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(driverOrderConfirmationUseCase.getOrders())
        .thenReturn(publishSubject.toFlowable(BackpressureStrategy.BUFFER));
    when(driverOrderConfirmationUseCase.sendDecision(anyBoolean()))
        .thenReturn(Completable.error(NoOrdersAvailableException::new));
    driverOrderConfirmationViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(order);
    driverOrderConfirmationViewModel.acceptOrder();

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(new DriverOrderConfirmationViewStatePending(null));
    inOrder.verify(viewStateObserver).onChanged(new DriverOrderConfirmationViewStateIdle(
        new OrderItem(order, timeUtils)
    ));
    inOrder.verify(viewStateObserver).onChanged(new DriverOrderConfirmationViewStatePending(
        new OrderItem(order, timeUtils)
    ));
    inOrder.verify(viewStateObserver)
        .onChanged(new DriverOrderConfirmationViewStateUnavailableError(
            new OrderItem(order, timeUtils)
        ));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида "Ошибка" нет доступных заказов.
   */
  @Test
  public void setNoOrdersAvailableErrorViewStateToLiveDataForDecline() {
    // Дано:
    PublishSubject<Order> publishSubject = PublishSubject.create();
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(driverOrderConfirmationUseCase.getOrders())
        .thenReturn(publishSubject.toFlowable(BackpressureStrategy.BUFFER));
    when(driverOrderConfirmationUseCase.sendDecision(anyBoolean()))
        .thenReturn(Completable.error(NoOrdersAvailableException::new));
    driverOrderConfirmationViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(order);
    driverOrderConfirmationViewModel.declineOrder();

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(new DriverOrderConfirmationViewStatePending(null));
    inOrder.verify(viewStateObserver).onChanged(new DriverOrderConfirmationViewStateIdle(
        new OrderItem(order, timeUtils)
    ));
    inOrder.verify(viewStateObserver).onChanged(new DriverOrderConfirmationViewStatePending(
        new OrderItem(order, timeUtils)
    ));
    inOrder.verify(viewStateObserver)
        .onChanged(new DriverOrderConfirmationViewStateUnavailableError(
            new OrderItem(order, timeUtils)
        ));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Не должен возвращать никакого состояния вида.
   */
  @Test
  public void setNoViewStateToLiveDataForAccept() {
    // Дано:
    PublishSubject<Order> publishSubject = PublishSubject.create();
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(driverOrderConfirmationUseCase.getOrders())
        .thenReturn(publishSubject.toFlowable(BackpressureStrategy.BUFFER));
    when(driverOrderConfirmationUseCase.sendDecision(anyBoolean()))
        .thenReturn(Completable.complete());
    driverOrderConfirmationViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(order);
    driverOrderConfirmationViewModel.acceptOrder();

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(new DriverOrderConfirmationViewStatePending(null));
    inOrder.verify(viewStateObserver).onChanged(new DriverOrderConfirmationViewStateIdle(
        new OrderItem(order, timeUtils)
    ));
    inOrder.verify(viewStateObserver).onChanged(new DriverOrderConfirmationViewStatePending(
        new OrderItem(order, timeUtils)
    ));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Не должен возвращать никакого состояния вида.
   */
  @Test
  public void setNoViewStateToLiveDataForDecline() {
    // Дано:
    PublishSubject<Order> publishSubject = PublishSubject.create();
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(driverOrderConfirmationUseCase.getOrders())
        .thenReturn(publishSubject.toFlowable(BackpressureStrategy.BUFFER));
    when(driverOrderConfirmationUseCase.sendDecision(anyBoolean()))
        .thenReturn(Completable.complete());
    driverOrderConfirmationViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(order);
    driverOrderConfirmationViewModel.declineOrder();

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(new DriverOrderConfirmationViewStatePending(null));
    inOrder.verify(viewStateObserver).onChanged(new DriverOrderConfirmationViewStateIdle(
        new OrderItem(order, timeUtils)
    ));
    inOrder.verify(viewStateObserver).onChanged(new DriverOrderConfirmationViewStatePending(
        new OrderItem(order, timeUtils)
    ));
    verifyNoMoreInteractions(viewStateObserver);
  }
}