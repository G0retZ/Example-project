package com.fasten.executor_driver.presentation.cancelorder;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.Observer;
import com.fasten.executor_driver.backend.web.NoNetworkException;
import com.fasten.executor_driver.entity.CancelOrderReason;
import com.fasten.executor_driver.gateway.DataMappingException;
import com.fasten.executor_driver.interactor.CancelOrderUseCase;
import com.fasten.executor_driver.presentation.ViewState;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Completable;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
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
public class CancelOrderViewModelTest {

  @Rule
  public TestRule rule = new InstantTaskExecutorRule();
  private CancelOrderViewModel cancelOrderViewModel;
  @Mock
  private CancelOrderUseCase cancelOrderUseCase;
  @Mock
  private CancelOrderReason cancelOrderReason;
  @Mock
  private CancelOrderReason cancelOrderReason1;
  @Mock
  private CancelOrderReason cancelOrderReason2;
  @Mock
  private Observer<String> navigateObserver;

  private PublishSubject<List<CancelOrderReason>> publishSubject;

  @Mock
  private Observer<ViewState<CancelOrderViewActions>> viewStateObserver;

  @Before
  public void setUp() {
    publishSubject = PublishSubject.create();
    RxJavaPlugins.setSingleSchedulerHandler(scheduler -> Schedulers.trampoline());
    RxAndroidPlugins.setInitMainThreadSchedulerHandler(scheduler -> Schedulers.trampoline());
    when(cancelOrderUseCase.getCancelOrderReasons(anyBoolean()))
        .thenReturn(publishSubject.toFlowable(BackpressureStrategy.BUFFER));
    when(cancelOrderUseCase.cancelOrder(any())).thenReturn(Completable.never());
    cancelOrderViewModel = new CancelOrderViewModelImpl(cancelOrderUseCase);
  }

  /* Тетсируем работу с юзкейсом заказа. */

  /**
   * Должен просить юзкейс получать список причин отказа только при создании.
   */
  @Test
  public void askUseCaseForCancelOrderReasonsInitially() {
    // Результат:
    verify(cancelOrderUseCase, only()).getCancelOrderReasons(false);
  }

  /**
   * Не должен трогать юзкейс на подписках.
   */
  @Test
  public void doNotTouchUseCaseOnSubscriptions() {
    // Действие:
    cancelOrderViewModel.getViewStateLiveData();
    cancelOrderViewModel.getNavigationLiveData();
    cancelOrderViewModel.getViewStateLiveData();
    cancelOrderViewModel.getNavigationLiveData();

    // Результат:
    verify(cancelOrderUseCase, only()).getCancelOrderReasons(false);
  }

  /**
   * Должен попросить юзкейс отказаться от заказа.
   */
  @Test
  public void askUseCaseToCancelOrder() {
    // Действие:
    cancelOrderViewModel.selectItem(cancelOrderReason1);

    // Результат:
    verify(cancelOrderUseCase).getCancelOrderReasons(false);
    verify(cancelOrderUseCase).cancelOrder(cancelOrderReason1);
    verifyNoMoreInteractions(cancelOrderUseCase);
  }

  /**
   * Не должен трогать юзкейс, если предыдущий запрос отказа от заказа еще не завершился.
   */
  @Test
  public void DoNotTouchUseCaseDuringCancelOrder() {
    // Дано:
    cancelOrderViewModel.selectItem(cancelOrderReason);
    cancelOrderViewModel.selectItem(cancelOrderReason1);
    cancelOrderViewModel.selectItem(cancelOrderReason2);

    // Результат:
    verify(cancelOrderUseCase).getCancelOrderReasons(false);
    verify(cancelOrderUseCase).cancelOrder(cancelOrderReason);
    verifyNoMoreInteractions(cancelOrderUseCase);
  }

  /* Тетсируем переключение состояний от сервера. */

  /**
   * Должен вернуть состояние вида ожидания изначально.
   */
  @Test
  public void setPendingViewStateToLiveDataInitially() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);

    // Действие:
    cancelOrderViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(new CancelOrderViewStatePending(null));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида "Ошибка" если нет сети.
   */
  @Test
  public void setNoNetworkErrorViewStateToLiveData() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    cancelOrderViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onError(new NoNetworkException());

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(new CancelOrderViewStatePending(null));
    inOrder.verify(viewStateObserver).onChanged(new CancelOrderViewStateError(null));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида "Ошибка" если данные не смапились.
   */
  @Test
  public void setNoNetworkErrorViewStateToLiveDataForMappingError() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    cancelOrderViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onError(new DataMappingException());

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(new CancelOrderViewStatePending(null));
    inOrder.verify(viewStateObserver).onChanged(new CancelOrderViewStateError(null));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида "списка причин отказа".
   */
  @Test
  public void setCancelOrderViewStateToLiveData() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    cancelOrderViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(Collections.singletonList(cancelOrderReason));
    publishSubject.onNext(Arrays.asList(cancelOrderReason, cancelOrderReason1, cancelOrderReason2));
    publishSubject.onNext(Arrays.asList(cancelOrderReason1, cancelOrderReason, cancelOrderReason2));
    publishSubject.onNext(Arrays.asList(cancelOrderReason2, cancelOrderReason1, cancelOrderReason));

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(new CancelOrderViewStatePending(null));
    inOrder.verify(viewStateObserver).onChanged(new CancelOrderViewState(
        Collections.singletonList(cancelOrderReason)
    ));
    inOrder.verify(viewStateObserver).onChanged(new CancelOrderViewState(
        Arrays.asList(cancelOrderReason, cancelOrderReason1, cancelOrderReason2)
    ));
    inOrder.verify(viewStateObserver).onChanged(new CancelOrderViewState(
        Arrays.asList(cancelOrderReason1, cancelOrderReason, cancelOrderReason2)
    ));
    inOrder.verify(viewStateObserver).onChanged(new CancelOrderViewState(
        Arrays.asList(cancelOrderReason2, cancelOrderReason1, cancelOrderReason)
    ));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /* Тетсируем переключение состояний при отказе от заказа. */

  /**
   * Должен вернуть состояние вида "В процессе".
   */
  @Test
  public void setPendingViewStateStateToLiveDataForCancelOrder() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    cancelOrderViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(Arrays.asList(cancelOrderReason, cancelOrderReason1, cancelOrderReason2));
    cancelOrderViewModel.selectItem(cancelOrderReason1);

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(new CancelOrderViewStatePending(null));
    inOrder.verify(viewStateObserver).onChanged(new CancelOrderViewState(
        Arrays.asList(cancelOrderReason, cancelOrderReason1, cancelOrderReason2)
    ));
    inOrder.verify(viewStateObserver).onChanged(new CancelOrderViewStatePending(
        new CancelOrderViewState(
            Arrays.asList(cancelOrderReason, cancelOrderReason1, cancelOrderReason2)
        )
    ));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть предыдущее состояние вида.
   */
  @Test
  public void setCancelOrderViewStateToLiveDataAfterPendingForCancelOrderError() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(cancelOrderUseCase.cancelOrder(any())).thenReturn(Completable.error(Exception::new));
    cancelOrderViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(Arrays.asList(cancelOrderReason, cancelOrderReason1, cancelOrderReason2));
    cancelOrderViewModel.selectItem(cancelOrderReason1);

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(new CancelOrderViewStatePending(null));
    inOrder.verify(viewStateObserver).onChanged(new CancelOrderViewState(
        Arrays.asList(cancelOrderReason, cancelOrderReason1, cancelOrderReason2)
    ));
    inOrder.verify(viewStateObserver).onChanged(new CancelOrderViewStatePending(
        new CancelOrderViewState(
            Arrays.asList(cancelOrderReason, cancelOrderReason1, cancelOrderReason2)
        )
    ));
    inOrder.verify(viewStateObserver).onChanged(new CancelOrderViewState(
        Arrays.asList(cancelOrderReason, cancelOrderReason1, cancelOrderReason2)
    ));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть предыдущее состояние вида.
   */
  @Test
  public void setCancelOrderViewStateToLiveDataAfterPendingForCancelOrderSuccess() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(cancelOrderUseCase.cancelOrder(any())).thenReturn(Completable.complete());
    cancelOrderViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(Arrays.asList(cancelOrderReason, cancelOrderReason1, cancelOrderReason2));
    cancelOrderViewModel.selectItem(cancelOrderReason1);

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(new CancelOrderViewStatePending(null));
    inOrder.verify(viewStateObserver).onChanged(new CancelOrderViewState(
        Arrays.asList(cancelOrderReason, cancelOrderReason1, cancelOrderReason2)
    ));
    inOrder.verify(viewStateObserver).onChanged(new CancelOrderViewStatePending(
        new CancelOrderViewState(
            Arrays.asList(cancelOrderReason, cancelOrderReason1, cancelOrderReason2)
        )
    ));
    inOrder.verify(viewStateObserver).onChanged(new CancelOrderViewState(
        Arrays.asList(cancelOrderReason, cancelOrderReason1, cancelOrderReason2)
    ));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /* Тетсируем навигацию. */

  /**
   * Должен игнорировать данные от сервера.
   */
  @Test
  public void setNothingToLiveDataForNewReasons() {
    // Дано:
    cancelOrderViewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Действие:
    // Действие:
    publishSubject.onNext(Collections.singletonList(cancelOrderReason));
    publishSubject.onNext(Arrays.asList(cancelOrderReason, cancelOrderReason1, cancelOrderReason2));
    publishSubject.onNext(Arrays.asList(cancelOrderReason1, cancelOrderReason, cancelOrderReason2));
    publishSubject.onNext(Arrays.asList(cancelOrderReason2, cancelOrderReason1, cancelOrderReason));

    // Результат:
    verifyZeroInteractions(navigateObserver);
  }

  /**
   * Должен игнорировать неуспешные выборы.
   */
  @Test
  public void setNothingToLiveData() {
    // Дано:
    when(cancelOrderUseCase.cancelOrder(any()))
        .thenReturn(Completable.error(new IndexOutOfBoundsException()));
    cancelOrderViewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Действие:
    cancelOrderViewModel.selectItem(cancelOrderReason);

    // Результат:
    verifyZeroInteractions(navigateObserver);
  }

  /**
   * Должен вернуть "перейти к ошибке сети" если была ошибка сети.
   */
  @Test
  public void setNoConnectionToLiveData() {
    // Дано:
    when(cancelOrderUseCase.cancelOrder(any()))
        .thenReturn(Completable.error(new IllegalStateException()));
    cancelOrderViewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Действие:
    cancelOrderViewModel.selectItem(cancelOrderReason);

    // Результат:
    verify(navigateObserver, only()).onChanged(CancelOrderNavigate.NO_CONNECTION);
  }

  /**
   * Должен вернуть "перейти к заказ отменен" если выбор был успешным.
   */
  @Test
  public void setNavigateToOrderCanceledToLiveData() {
    // Дано:
    when(cancelOrderUseCase.cancelOrder(any())).thenReturn(Completable.complete());
    cancelOrderViewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Действие:
    cancelOrderViewModel.selectItem(cancelOrderReason);

    // Результат:
    verify(navigateObserver, only()).onChanged(CancelOrderNavigate.ORDER_CANCELED);
  }
}