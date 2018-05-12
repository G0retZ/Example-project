package com.fasten.executor_driver.presentation.clientorderconfirmation;

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
import com.fasten.executor_driver.interactor.ClientOrderConfirmationUseCase;
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
public class ClientOrderConfirmationViewModelTest {

  @Rule
  public TestRule rule = new InstantTaskExecutorRule();
  private ClientOrderConfirmationViewModel clientOrderConfirmationViewModel;
  @Mock
  private ClientOrderConfirmationUseCase clientOrderConfirmationUseCase;
  @Mock
  private Order order;
  @Mock
  private Order order1;
  @Mock
  private Order order2;

  @Mock
  private Observer<ViewState<ClientOrderConfirmationViewActions>> viewStateObserver;

  @Before
  public void setUp() {
    RxJavaPlugins.setSingleSchedulerHandler(scheduler -> Schedulers.trampoline());
    RxAndroidPlugins.setInitMainThreadSchedulerHandler(scheduler -> Schedulers.trampoline());
    when(clientOrderConfirmationUseCase.getOrders()).thenReturn(Flowable.never());
    when(clientOrderConfirmationUseCase.cancelOrder()).thenReturn(Completable.never());
    clientOrderConfirmationViewModel = new ClientOrderConfirmationViewModelImpl(
        clientOrderConfirmationUseCase);
  }

  /* Тетсируем работу с юзкейсом заказа. */

  /**
   * Должен просить юзкейс получать заказы, при первой и только при первой подписке.
   */
  @Test
  public void askOrderConfirmationUseCaseForOffersInitially() {
    // Действие:
    clientOrderConfirmationViewModel.getViewStateLiveData();
    clientOrderConfirmationViewModel.getViewStateLiveData();
    clientOrderConfirmationViewModel.getViewStateLiveData();

    // Результат:
    verify(clientOrderConfirmationUseCase, only()).getOrders();
  }

  /**
   * Должен попросить юзкейс передать отказ от заказа.
   */
  @Test
  public void askOrderConfirmationUseCaseToSendOrderConfirmationCanceled() {
    // Дано:
    when(clientOrderConfirmationUseCase.cancelOrder()).thenReturn(Completable.complete());

    // Действие:
    clientOrderConfirmationViewModel.cancelOrder();

    // Результат:
    verify(clientOrderConfirmationUseCase, only()).cancelOrder();
  }

  /**
   * Не должен трогать юзкейс, если предыдущий запрос отмены заказа еще не завершился.
   */
  @Test
  public void DoNotTouchOrderConfirmationUseCaseDuringOrderConfirmationSetting() {
    // Действие:
    clientOrderConfirmationViewModel.cancelOrder();
    clientOrderConfirmationViewModel.cancelOrder();
    clientOrderConfirmationViewModel.cancelOrder();

    // Результат:
    verify(clientOrderConfirmationUseCase, only()).cancelOrder();
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
    clientOrderConfirmationViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(ClientOrderConfirmationViewStatePending.class));
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
    when(clientOrderConfirmationUseCase.getOrders())
        .thenReturn(publishSubject.toFlowable(BackpressureStrategy.BUFFER));
    clientOrderConfirmationViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onError(new NoNetworkException());

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(new ClientOrderConfirmationViewStatePending(null));
    inOrder.verify(viewStateObserver)
        .onChanged(new ClientOrderConfirmationViewStateNetworkError(null));
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
    when(clientOrderConfirmationUseCase.getOrders())
        .thenReturn(publishSubject.toFlowable(BackpressureStrategy.BUFFER));
    clientOrderConfirmationViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onError(new DataMappingException());

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(new ClientOrderConfirmationViewStatePending(null));
    inOrder.verify(viewStateObserver)
        .onChanged(new ClientOrderConfirmationViewStateNetworkError(null));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида "Ошибка" нет доступных заказов.
   */
  @Test
  public void setNoOfferAvailableErrorViewStateToLiveData() {
    // Дано:
    PublishSubject<Order> publishSubject = PublishSubject.create();
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(clientOrderConfirmationUseCase.getOrders())
        .thenReturn(publishSubject.toFlowable(BackpressureStrategy.BUFFER));
    clientOrderConfirmationViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onError(new NoOrdersAvailableException());

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(new ClientOrderConfirmationViewStatePending(null));
    inOrder.verify(viewStateObserver)
        .onChanged(new ClientOrderConfirmationViewStateUnavailableError(null));
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
    when(clientOrderConfirmationUseCase.getOrders())
        .thenReturn(publishSubject.toFlowable(BackpressureStrategy.BUFFER));
    clientOrderConfirmationViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(order);
    publishSubject.onNext(order1);
    publishSubject.onNext(order2);

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(new ClientOrderConfirmationViewStatePending(null));
    inOrder.verify(viewStateObserver).onChanged(new ClientOrderConfirmationViewStateIdle(
        new OrderItem(order))
    );
    inOrder.verify(viewStateObserver).onChanged(new ClientOrderConfirmationViewStateIdle(
        new OrderItem(order1))
    );
    inOrder.verify(viewStateObserver).onChanged(new ClientOrderConfirmationViewStateIdle(
        new OrderItem(order2))
    );
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида "В процессе".
   */
  @Test
  public void setPendingViewStateWithoutOrderConfirmationToLiveDataForCancel() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    clientOrderConfirmationViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    clientOrderConfirmationViewModel.cancelOrder();

    // Результат:
    inOrder.verify(viewStateObserver, times(2))
        .onChanged(new ClientOrderConfirmationViewStatePending(null));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида "Ошибка" сети.
   */
  @Test
  public void setNoNetworkErrorViewStateWithoutOrderConfirmationToLiveDataForCancel() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(clientOrderConfirmationUseCase.cancelOrder())
        .thenReturn(Completable.error(NoNetworkException::new));
    clientOrderConfirmationViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    clientOrderConfirmationViewModel.cancelOrder();

    // Результат:
    inOrder.verify(viewStateObserver, times(2))
        .onChanged(new ClientOrderConfirmationViewStatePending(null));
    inOrder.verify(viewStateObserver)
        .onChanged(new ClientOrderConfirmationViewStateNetworkError(null));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида "Ошибка" нет доступных заказов.
   */
  @Test
  public void setNoOffersAvailableErrorViewStateWithoutOrderConfirmationToLiveDataForCancel() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(clientOrderConfirmationUseCase.cancelOrder())
        .thenReturn(Completable.error(NoOrdersAvailableException::new));
    clientOrderConfirmationViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    clientOrderConfirmationViewModel.cancelOrder();

    // Результат:
    inOrder.verify(viewStateObserver, times(2))
        .onChanged(new ClientOrderConfirmationViewStatePending(null));
    inOrder.verify(viewStateObserver)
        .onChanged(new ClientOrderConfirmationViewStateUnavailableError(null));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Не должен возвращать никакого состояния вида.
   */
  @Test
  public void setNoViewStateToLiveDataForCancelWithoutOffer() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(clientOrderConfirmationUseCase.cancelOrder()).thenReturn(Completable.complete());
    clientOrderConfirmationViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    clientOrderConfirmationViewModel.cancelOrder();

    // Результат:
    inOrder.verify(viewStateObserver, times(2))
        .onChanged(new ClientOrderConfirmationViewStatePending(null));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида "В процессе".
   */
  @Test
  public void setPendingViewStateToLiveDataForCancel() {
    // Дано:
    PublishSubject<Order> publishSubject = PublishSubject.create();
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(clientOrderConfirmationUseCase.getOrders())
        .thenReturn(publishSubject.toFlowable(BackpressureStrategy.BUFFER));
    clientOrderConfirmationViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(order);
    clientOrderConfirmationViewModel.cancelOrder();

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(new ClientOrderConfirmationViewStatePending(null));
    inOrder.verify(viewStateObserver).onChanged(new ClientOrderConfirmationViewStateIdle(
        new OrderItem(order)
    ));
    inOrder.verify(viewStateObserver).onChanged(new ClientOrderConfirmationViewStatePending(
        new OrderItem(order)
    ));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида "Ошибка" сети.
   */
  @Test
  public void setNoNetworkErrorViewStateToLiveDataForCancel() {
    // Дано:
    PublishSubject<Order> publishSubject = PublishSubject.create();
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(clientOrderConfirmationUseCase.getOrders())
        .thenReturn(publishSubject.toFlowable(BackpressureStrategy.BUFFER));
    when(clientOrderConfirmationUseCase.cancelOrder())
        .thenReturn(Completable.error(NoNetworkException::new));
    clientOrderConfirmationViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(order);
    clientOrderConfirmationViewModel.cancelOrder();

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(new ClientOrderConfirmationViewStatePending(null));
    inOrder.verify(viewStateObserver).onChanged(new ClientOrderConfirmationViewStateIdle(
        new OrderItem(order)
    ));
    inOrder.verify(viewStateObserver).onChanged(new ClientOrderConfirmationViewStatePending(
        new OrderItem(order)
    ));
    inOrder.verify(viewStateObserver).onChanged(new ClientOrderConfirmationViewStateNetworkError(
        new OrderItem(order)
    ));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида "Ошибка" нет доступных заказов.
   */
  @Test
  public void setNoOrderConfirmationsAvailableErrorViewStateToLiveDataForCancel() {
    // Дано:
    PublishSubject<Order> publishSubject = PublishSubject.create();
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(clientOrderConfirmationUseCase.getOrders())
        .thenReturn(publishSubject.toFlowable(BackpressureStrategy.BUFFER));
    when(clientOrderConfirmationUseCase.cancelOrder())
        .thenReturn(Completable.error(NoOrdersAvailableException::new));
    clientOrderConfirmationViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(order);
    clientOrderConfirmationViewModel.cancelOrder();

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(new ClientOrderConfirmationViewStatePending(null));
    inOrder.verify(viewStateObserver).onChanged(new ClientOrderConfirmationViewStateIdle(
        new OrderItem(order)
    ));
    inOrder.verify(viewStateObserver).onChanged(new ClientOrderConfirmationViewStatePending(
        new OrderItem(order)
    ));
    inOrder.verify(viewStateObserver)
        .onChanged(new ClientOrderConfirmationViewStateUnavailableError(
            new OrderItem(order)
        ));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Не должен возвращать никакого состояния вида.
   */
  @Test
  public void setNoViewStateToLiveDataForCancel() {
    // Дано:
    PublishSubject<Order> publishSubject = PublishSubject.create();
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(clientOrderConfirmationUseCase.getOrders())
        .thenReturn(publishSubject.toFlowable(BackpressureStrategy.BUFFER));
    when(clientOrderConfirmationUseCase.cancelOrder()).thenReturn(Completable.complete());
    clientOrderConfirmationViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(order);
    clientOrderConfirmationViewModel.cancelOrder();

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(new ClientOrderConfirmationViewStatePending(null));
    inOrder.verify(viewStateObserver).onChanged(new ClientOrderConfirmationViewStateIdle(
        new OrderItem(order)
    ));
    inOrder.verify(viewStateObserver).onChanged(new ClientOrderConfirmationViewStatePending(
        new OrderItem(order)
    ));
    verifyNoMoreInteractions(viewStateObserver);
  }
}