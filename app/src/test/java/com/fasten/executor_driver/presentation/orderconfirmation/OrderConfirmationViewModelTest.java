package com.fasten.executor_driver.presentation.orderconfirmation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.Observer;
import com.fasten.executor_driver.backend.web.NoNetworkException;
import com.fasten.executor_driver.interactor.OrderConfirmationUseCase;
import com.fasten.executor_driver.presentation.ViewState;
import io.reactivex.Completable;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
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
public class OrderConfirmationViewModelTest {

  @Rule
  public TestRule rule = new InstantTaskExecutorRule();
  private OrderConfirmationViewModel orderConfirmationViewModel;
  @Mock
  private OrderConfirmationUseCase orderConfirmationUseCase;

  @Mock
  private Observer<ViewState<OrderConfirmationViewActions>> viewStateObserver;

  @Before
  public void setUp() {
    RxJavaPlugins.setSingleSchedulerHandler(scheduler -> Schedulers.trampoline());
    RxAndroidPlugins.setInitMainThreadSchedulerHandler(scheduler -> Schedulers.trampoline());
    when(orderConfirmationUseCase.sendDecision(anyBoolean())).thenReturn(Completable.never());
    orderConfirmationViewModel = new OrderConfirmationViewModelImpl(orderConfirmationUseCase);
  }

  /* Тетсируем работу с юзкейсом принятия заказа. */

  /**
   * Должен попросить юзкейс передать принятие заказа.
   */
  @Test
  public void askUseCaseToSendOrderAccepted() {
    // Дано:
    when(orderConfirmationUseCase.sendDecision(anyBoolean()))
        .thenReturn(Completable.complete());

    // Действие:
    orderConfirmationViewModel.acceptOrder();

    // Результат:
    verify(orderConfirmationUseCase, only()).sendDecision(true);
  }

  /**
   * Должен попросить юзкейс передать отказ от заказа.
   */
  @Test
  public void askUseCaseToSendOrderDeclined() {
    // Дано:
    when(orderConfirmationUseCase.sendDecision(anyBoolean()))
        .thenReturn(Completable.complete());

    // Действие:
    orderConfirmationViewModel.declineOrder();

    // Результат:
    verify(orderConfirmationUseCase, only()).sendDecision(false);
  }

  /**
   * Не должен трогать юзкейс, если предыдущий запрос передачи решения еще не завершился.
   */
  @Test
  public void DoNotTouchUseCaseDuringOrderSetting() {
    // Действие:
    orderConfirmationViewModel.acceptOrder();
    orderConfirmationViewModel.declineOrder();
    orderConfirmationViewModel.acceptOrder();

    // Результат:
    verify(orderConfirmationUseCase, only()).sendDecision(true);
  }

  /* Тетсируем переключение состояний. */

  /**
   * Должен вернуть состояние вида бездействия изначально.
   */
  @Test
  public void setPendingViewStateToLiveDataInitially() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);

    // Действие:
    orderConfirmationViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(OrderConfirmationViewStateIdle.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида "В процессе".
   */
  @Test
  public void setPendingViewStateWithoutOrderToLiveDataForAccept() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    orderConfirmationViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    orderConfirmationViewModel.acceptOrder();

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(OrderConfirmationViewStateIdle.class));
    inOrder.verify(viewStateObserver).onChanged(any(OrderConfirmationViewStatePending.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида "В процессе".
   */
  @Test
  public void setPendingViewStateWithoutOrderToLiveDataForDecline() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    orderConfirmationViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    orderConfirmationViewModel.declineOrder();

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(OrderConfirmationViewStateIdle.class));
    inOrder.verify(viewStateObserver).onChanged(any(OrderConfirmationViewStatePending.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида "Ошибка" сети.
   */
  @Test
  public void setNoNetworkErrorViewStateWithoutOrderToLiveDataForAccept() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(orderConfirmationUseCase.sendDecision(anyBoolean()))
        .thenReturn(Completable.error(NoNetworkException::new));
    orderConfirmationViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    orderConfirmationViewModel.acceptOrder();

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(OrderConfirmationViewStateIdle.class));
    inOrder.verify(viewStateObserver).onChanged(any(OrderConfirmationViewStatePending.class));
    inOrder.verify(viewStateObserver).onChanged(any(OrderConfirmationViewStateError.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида "Ошибка" сети.
   */
  @Test
  public void setNoNetworkErrorViewStateWithoutOrderToLiveDataForDecline() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(orderConfirmationUseCase.sendDecision(anyBoolean()))
        .thenReturn(Completable.error(NoNetworkException::new));
    orderConfirmationViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    orderConfirmationViewModel.declineOrder();

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(OrderConfirmationViewStateIdle.class));
    inOrder.verify(viewStateObserver).onChanged(any(OrderConfirmationViewStatePending.class));
    inOrder.verify(viewStateObserver).onChanged(any(OrderConfirmationViewStateError.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Не должен возвращать никакого состояния вида.
   */
  @Test
  public void setNoViewStateToLiveDataForAcceptWithoutOrder() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(orderConfirmationUseCase.sendDecision(anyBoolean()))
        .thenReturn(Completable.complete());
    orderConfirmationViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    orderConfirmationViewModel.acceptOrder();

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(OrderConfirmationViewStateIdle.class));
    inOrder.verify(viewStateObserver).onChanged(any(OrderConfirmationViewStatePending.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Не должен возвращать никакого состояния вида.
   */
  @Test
  public void setNoViewStateToLiveDataForDeclineWithoutOrder() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(orderConfirmationUseCase.sendDecision(anyBoolean()))
        .thenReturn(Completable.complete());
    orderConfirmationViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    orderConfirmationViewModel.declineOrder();

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(OrderConfirmationViewStateIdle.class));
    inOrder.verify(viewStateObserver).onChanged(any(OrderConfirmationViewStatePending.class));
    verifyNoMoreInteractions(viewStateObserver);
  }
}