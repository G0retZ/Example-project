package com.cargopull.executor_driver.presentation.cancelorder;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.Observer;
import com.cargopull.executor_driver.ViewModelThreadTestRule;
import com.cargopull.executor_driver.entity.CancelOrderReason;
import com.cargopull.executor_driver.gateway.DataMappingException;
import com.cargopull.executor_driver.interactor.CancelOrderUseCase;
import com.cargopull.executor_driver.presentation.CommonNavigate;
import com.cargopull.executor_driver.presentation.ViewState;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Completable;
import io.reactivex.subjects.PublishSubject;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
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
public class CancelOrderViewModelTest {

  @ClassRule
  public static final ViewModelThreadTestRule classRule = new ViewModelThreadTestRule();
  @Rule
  public TestRule rule = new InstantTaskExecutorRule();
  private CancelOrderViewModel viewModel;
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
    when(cancelOrderUseCase.getCancelOrderReasons(anyBoolean()))
        .thenReturn(publishSubject.toFlowable(BackpressureStrategy.BUFFER));
    when(cancelOrderUseCase.cancelOrder(any())).thenReturn(Completable.never());
    viewModel = new CancelOrderViewModelImpl(cancelOrderUseCase);
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
    viewModel.getViewStateLiveData();
    viewModel.getNavigationLiveData();
    viewModel.getViewStateLiveData();
    viewModel.getNavigationLiveData();

    // Результат:
    verify(cancelOrderUseCase, only()).getCancelOrderReasons(false);
  }

  /**
   * Должен попросить юзкейс отказаться от заказа.
   */
  @Test
  public void askUseCaseToCancelOrder() {
    // Действие:
    viewModel.selectItem(cancelOrderReason1);

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
    viewModel.selectItem(cancelOrderReason);
    viewModel.selectItem(cancelOrderReason1);
    viewModel.selectItem(cancelOrderReason2);

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
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(new CancelOrderViewStatePending(null));
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
    verify(viewStateObserver, only()).onChanged(new CancelOrderViewStatePending(null));
  }

  /**
   * Должен вернуть состояние вида "списка причин отказа".
   */
  @Test
  public void setCancelOrderViewStateToLiveData() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

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
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(Arrays.asList(cancelOrderReason, cancelOrderReason1, cancelOrderReason2));
    viewModel.selectItem(cancelOrderReason1);

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
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(Arrays.asList(cancelOrderReason, cancelOrderReason1, cancelOrderReason2));
    viewModel.selectItem(cancelOrderReason1);

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
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(Arrays.asList(cancelOrderReason, cancelOrderReason1, cancelOrderReason2));
    viewModel.selectItem(cancelOrderReason1);

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
    viewModel.getNavigationLiveData().observeForever(navigateObserver);

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
   * Должен вернуть "перейти к ошибке данных сервера".
   */
  @Test
  public void setNavigateToServerDataError() {
    // Дано:
    viewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Действие:
    publishSubject.onError(new DataMappingException());

    // Результат:
    verify(navigateObserver, only()).onChanged(CommonNavigate.SERVER_DATA_ERROR);
  }

  /**
   * Должен игнорировать неуспешные выборы.
   */
  @Test
  public void setNothingToLiveData() {
    // Дано:
    when(cancelOrderUseCase.cancelOrder(any()))
        .thenReturn(Completable.error(new IndexOutOfBoundsException()));
    viewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Действие:
    viewModel.selectItem(cancelOrderReason);

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
    viewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Действие:
    viewModel.selectItem(cancelOrderReason);

    // Результат:
    verify(navigateObserver, only()).onChanged(CommonNavigate.NO_CONNECTION);
  }

  /**
   * Должен вернуть "перейти к заказ отменен" если выбор был успешным.
   */
  @Test
  public void setNavigateToOrderCanceledToLiveData() {
    // Дано:
    when(cancelOrderUseCase.cancelOrder(any())).thenReturn(Completable.complete());
    viewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Действие:
    viewModel.selectItem(cancelOrderReason);

    // Результат:
    verify(navigateObserver, only()).onChanged(CancelOrderNavigate.ORDER_CANCELED);
  }
}