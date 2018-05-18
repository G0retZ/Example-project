package com.fasten.executor_driver.presentation.waitingforclient;

import static org.mockito.ArgumentMatchers.any;
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
import com.fasten.executor_driver.interactor.WaitingForClientUseCase;
import com.fasten.executor_driver.presentation.ViewState;
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
public class WaitingForClientViewModelTest {

  @Rule
  public TestRule rule = new InstantTaskExecutorRule();
  private WaitingForClientViewModel movingToClientViewModel;
  @Mock
  private WaitingForClientUseCase movingToClientUseCase;
  @Mock
  private Order order;
  @Mock
  private Order order1;
  @Mock
  private Order order2;

  @Mock
  private Observer<ViewState<WaitingForClientViewActions>> viewStateObserver;

  @Before
  public void setUp() {
    RxJavaPlugins.setSingleSchedulerHandler(scheduler -> Schedulers.trampoline());
    RxAndroidPlugins.setInitMainThreadSchedulerHandler(scheduler -> Schedulers.trampoline());
    when(movingToClientUseCase.getOrders()).thenReturn(Flowable.never());
    when(movingToClientUseCase.callToClient()).thenReturn(Completable.never());
    when(movingToClientUseCase.startTheOrder()).thenReturn(Completable.never());
    movingToClientViewModel = new WaitingForClientViewModelImpl(
        movingToClientUseCase);
  }

  /* Тетсируем работу с юзкейсом заказа. */

  /**
   * Должен просить юзкейс получать заказы, при первой и только при первой подписке.
   */
  @Test
  public void askUseCaseForOrdersInitially() {
    // Действие:
    movingToClientViewModel.getViewStateLiveData();
    movingToClientViewModel.getViewStateLiveData();
    movingToClientViewModel.getViewStateLiveData();

    // Результат:
    verify(movingToClientUseCase, only()).getOrders();
  }

  /**
   * Должен попросить юзкейс позвонить клиенту.
   */
  @Test
  public void askUseCaseToCallToClient() {
    // Дано:
    when(movingToClientUseCase.callToClient()).thenReturn(Completable.complete());

    // Действие:
    movingToClientViewModel.callToClient();

    // Результат:
    verify(movingToClientUseCase, only()).callToClient();
  }

  /**
   * Должен попросить юзкейс сообщить начале погрузки.
   */
  @Test
  public void askUseCaseToStartLoading() {
    // Дано:
    when(movingToClientUseCase.startTheOrder()).thenReturn(Completable.complete());

    // Действие:
    movingToClientViewModel.startLoading();

    // Результат:
    verify(movingToClientUseCase, only()).startTheOrder();
  }

  /**
   * Не должен трогать юзкейс, если предыдущий запрос звонка/начала еще не завершился.
   */
  @Test
  public void DoNotTouchUseCaseDuringOrderSetting() {
    // Действие:
    movingToClientViewModel.callToClient();
    movingToClientViewModel.startLoading();
    movingToClientViewModel.callToClient();

    // Результат:
    verify(movingToClientUseCase, only()).callToClient();
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
    movingToClientViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(WaitingForClientViewStatePending.class));
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
    when(movingToClientUseCase.getOrders())
        .thenReturn(publishSubject.toFlowable(BackpressureStrategy.BUFFER));
    movingToClientViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onError(new NoNetworkException());

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(new WaitingForClientViewStatePending(null));
    inOrder.verify(viewStateObserver)
        .onChanged(new WaitingForClientViewStateNetworkError(null));
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
    when(movingToClientUseCase.getOrders())
        .thenReturn(publishSubject.toFlowable(BackpressureStrategy.BUFFER));
    movingToClientViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onError(new DataMappingException());

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(new WaitingForClientViewStatePending(null));
    inOrder.verify(viewStateObserver)
        .onChanged(new WaitingForClientViewStateNetworkError(null));
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
    when(movingToClientUseCase.getOrders())
        .thenReturn(publishSubject.toFlowable(BackpressureStrategy.BUFFER));
    movingToClientViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onError(new NoOrdersAvailableException());

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(new WaitingForClientViewStatePending(null));
    inOrder.verify(viewStateObserver)
        .onChanged(new WaitingForClientViewStateUnavailableError(null));
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
    when(movingToClientUseCase.getOrders())
        .thenReturn(publishSubject.toFlowable(BackpressureStrategy.BUFFER));
    movingToClientViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(order);
    publishSubject.onNext(order1);
    publishSubject.onNext(order2);

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(new WaitingForClientViewStatePending(null));
    inOrder.verify(viewStateObserver).onChanged(new WaitingForClientViewStateIdle(
        new OrderItem(order))
    );
    inOrder.verify(viewStateObserver).onChanged(new WaitingForClientViewStateIdle(
        new OrderItem(order1))
    );
    inOrder.verify(viewStateObserver).onChanged(new WaitingForClientViewStateIdle(
        new OrderItem(order2))
    );
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида "В процессе".
   */
  @Test
  public void setPendingViewStateWithoutOrderToLiveDataForCallToClient() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    movingToClientViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    movingToClientViewModel.callToClient();

    // Результат:
    inOrder.verify(viewStateObserver, times(2))
        .onChanged(new WaitingForClientViewStatePending(null));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида "В процессе".
   */
  @Test
  public void setPendingViewStateWithoutOrderToLiveDataForStartLoading() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    movingToClientViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    movingToClientViewModel.startLoading();

    // Результат:
    inOrder.verify(viewStateObserver, times(2))
        .onChanged(new WaitingForClientViewStatePending(null));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида "Ошибка" сети.
   */
  @Test
  public void setNoNetworkErrorViewStateWithoutOrderToLiveDataForCallToClient() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(movingToClientUseCase.callToClient())
        .thenReturn(Completable.error(NoNetworkException::new));
    movingToClientViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    movingToClientViewModel.callToClient();

    // Результат:
    inOrder.verify(viewStateObserver, times(2))
        .onChanged(new WaitingForClientViewStatePending(null));
    inOrder.verify(viewStateObserver)
        .onChanged(new WaitingForClientViewStateNetworkError(null));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида "Ошибка" сети.
   */
  @Test
  public void setNoNetworkErrorViewStateWithoutOrderToLiveDataForStartLoading() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(movingToClientUseCase.startTheOrder())
        .thenReturn(Completable.error(NoNetworkException::new));
    movingToClientViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    movingToClientViewModel.startLoading();

    // Результат:
    inOrder.verify(viewStateObserver, times(2))
        .onChanged(new WaitingForClientViewStatePending(null));
    inOrder.verify(viewStateObserver)
        .onChanged(new WaitingForClientViewStateNetworkError(null));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида "Ошибка" нет доступных заказов.
   */
  @Test
  public void setNoOrdersAvailableErrorViewStateWithoutOrderToLiveDataForCallToClient() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(movingToClientUseCase.callToClient())
        .thenReturn(Completable.error(NoOrdersAvailableException::new));
    movingToClientViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    movingToClientViewModel.callToClient();

    // Результат:
    inOrder.verify(viewStateObserver, times(2))
        .onChanged(new WaitingForClientViewStatePending(null));
    inOrder.verify(viewStateObserver)
        .onChanged(new WaitingForClientViewStateUnavailableError(null));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида "Ошибка" нет доступных заказов.
   */
  @Test
  public void setNoOrdersAvailableErrorViewStateWithoutOrderToLiveDataForStartLoading() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(movingToClientUseCase.startTheOrder())
        .thenReturn(Completable.error(NoOrdersAvailableException::new));
    movingToClientViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    movingToClientViewModel.startLoading();

    // Результат:
    inOrder.verify(viewStateObserver, times(2))
        .onChanged(new WaitingForClientViewStatePending(null));
    inOrder.verify(viewStateObserver)
        .onChanged(new WaitingForClientViewStateUnavailableError(null));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Не должен возвращать никакого состояния вида.
   */
  @Test
  public void setIdleViewStateToLiveDataForCallToClientWithoutOrder() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(movingToClientUseCase.callToClient())
        .thenReturn(Completable.complete());
    movingToClientViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    movingToClientViewModel.callToClient();

    // Результат:
    inOrder.verify(viewStateObserver, times(2))
        .onChanged(new WaitingForClientViewStatePending(null));
    inOrder.verify(viewStateObserver).onChanged(new WaitingForClientViewStateIdle(null));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Не должен возвращать никакого состояния вида.
   */
  @Test
  public void setNoViewStateToLiveDataForStartLoadingWithoutOrder() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(movingToClientUseCase.startTheOrder())
        .thenReturn(Completable.complete());
    movingToClientViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    movingToClientViewModel.startLoading();

    // Результат:
    inOrder.verify(viewStateObserver, times(2))
        .onChanged(new WaitingForClientViewStatePending(null));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида "В процессе".
   */
  @Test
  public void setPendingViewStateToLiveDataForCallToClient() {
    // Дано:
    PublishSubject<Order> publishSubject = PublishSubject.create();
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(movingToClientUseCase.getOrders())
        .thenReturn(publishSubject.toFlowable(BackpressureStrategy.BUFFER));
    movingToClientViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(order);
    movingToClientViewModel.callToClient();

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(new WaitingForClientViewStatePending(null));
    inOrder.verify(viewStateObserver).onChanged(new WaitingForClientViewStateIdle(
        new OrderItem(order)
    ));
    inOrder.verify(viewStateObserver).onChanged(new WaitingForClientViewStatePending(
        new OrderItem(order)
    ));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида "В процессе".
   */
  @Test
  public void setPendingViewStateToLiveDataForStartLoading() {
    // Дано:
    PublishSubject<Order> publishSubject = PublishSubject.create();
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(movingToClientUseCase.getOrders())
        .thenReturn(publishSubject.toFlowable(BackpressureStrategy.BUFFER));
    movingToClientViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(order);
    movingToClientViewModel.startLoading();

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(new WaitingForClientViewStatePending(null));
    inOrder.verify(viewStateObserver).onChanged(new WaitingForClientViewStateIdle(
        new OrderItem(order)
    ));
    inOrder.verify(viewStateObserver).onChanged(new WaitingForClientViewStatePending(
        new OrderItem(order)
    ));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида "Ошибка" сети.
   */
  @Test
  public void setNoNetworkErrorViewStateToLiveDataForCallToClient() {
    // Дано:
    PublishSubject<Order> publishSubject = PublishSubject.create();
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(movingToClientUseCase.getOrders())
        .thenReturn(publishSubject.toFlowable(BackpressureStrategy.BUFFER));
    when(movingToClientUseCase.callToClient())
        .thenReturn(Completable.error(NoNetworkException::new));
    movingToClientViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(order);
    movingToClientViewModel.callToClient();

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(new WaitingForClientViewStatePending(null));
    inOrder.verify(viewStateObserver).onChanged(new WaitingForClientViewStateIdle(
        new OrderItem(order)
    ));
    inOrder.verify(viewStateObserver).onChanged(new WaitingForClientViewStatePending(
        new OrderItem(order)
    ));
    inOrder.verify(viewStateObserver).onChanged(new WaitingForClientViewStateNetworkError(
        new OrderItem(order)
    ));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида "Ошибка" сети.
   */
  @Test
  public void setNoNetworkErrorViewStateToLiveDataForStartLoading() {
    // Дано:
    PublishSubject<Order> publishSubject = PublishSubject.create();
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(movingToClientUseCase.getOrders())
        .thenReturn(publishSubject.toFlowable(BackpressureStrategy.BUFFER));
    when(movingToClientUseCase.startTheOrder())
        .thenReturn(Completable.error(NoNetworkException::new));
    movingToClientViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(order);
    movingToClientViewModel.startLoading();

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(new WaitingForClientViewStatePending(null));
    inOrder.verify(viewStateObserver).onChanged(new WaitingForClientViewStateIdle(
        new OrderItem(order)
    ));
    inOrder.verify(viewStateObserver).onChanged(new WaitingForClientViewStatePending(
        new OrderItem(order)
    ));
    inOrder.verify(viewStateObserver).onChanged(new WaitingForClientViewStateNetworkError(
        new OrderItem(order)
    ));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Не должен возвращать никакого состояния вида.
   */
  @Test
  public void setIdleViewStateToLiveDataForCallToClient() {
    // Дано:
    PublishSubject<Order> publishSubject = PublishSubject.create();
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(movingToClientUseCase.getOrders())
        .thenReturn(publishSubject.toFlowable(BackpressureStrategy.BUFFER));
    when(movingToClientUseCase.callToClient()).thenReturn(Completable.complete());
    movingToClientViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(order);
    movingToClientViewModel.callToClient();

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(new WaitingForClientViewStatePending(null));
    inOrder.verify(viewStateObserver).onChanged(new WaitingForClientViewStateIdle(
        new OrderItem(order)
    ));
    inOrder.verify(viewStateObserver).onChanged(new WaitingForClientViewStatePending(
        new OrderItem(order)
    ));
    inOrder.verify(viewStateObserver).onChanged(new WaitingForClientViewStateIdle(
        new OrderItem(order)
    ));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Не должен возвращать никакого состояния вида.
   */
  @Test
  public void setNoViewStateToLiveDataForStartLoading() {
    // Дано:
    PublishSubject<Order> publishSubject = PublishSubject.create();
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(movingToClientUseCase.getOrders())
        .thenReturn(publishSubject.toFlowable(BackpressureStrategy.BUFFER));
    when(movingToClientUseCase.startTheOrder()).thenReturn(Completable.complete());
    movingToClientViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(order);
    movingToClientViewModel.startLoading();

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(new WaitingForClientViewStatePending(null));
    inOrder.verify(viewStateObserver).onChanged(new WaitingForClientViewStateIdle(
        new OrderItem(order)
    ));
    inOrder.verify(viewStateObserver).onChanged(new WaitingForClientViewStatePending(
        new OrderItem(order)
    ));
    verifyNoMoreInteractions(viewStateObserver);
  }
}