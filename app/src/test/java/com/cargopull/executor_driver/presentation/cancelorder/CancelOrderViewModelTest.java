package com.cargopull.executor_driver.presentation.cancelorder;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;
import com.cargopull.executor_driver.ViewModelThreadTestRule;
import com.cargopull.executor_driver.backend.analytics.ErrorReporter;
import com.cargopull.executor_driver.entity.CancelOrderReason;
import com.cargopull.executor_driver.gateway.DataMappingException;
import com.cargopull.executor_driver.interactor.CancelOrderUseCase;
import com.cargopull.executor_driver.presentation.CommonNavigate;
import com.cargopull.executor_driver.presentation.ViewState;
import io.reactivex.Completable;
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
public class CancelOrderViewModelTest {

  @ClassRule
  public static final ViewModelThreadTestRule classRule = new ViewModelThreadTestRule();
  @Rule
  public TestRule rule = new InstantTaskExecutorRule();
  private CancelOrderViewModel viewModel;
  @Mock
  private ErrorReporter errorReporter;
  @Mock
  private CancelOrderUseCase useCase;
  @Mock
  private CancelOrderReason cancelOrderReason;
  @Mock
  private CancelOrderReason cancelOrderReason1;
  @Mock
  private CancelOrderReason cancelOrderReason2;
  @Mock
  private Observer<String> navigateObserver;
  @Mock
  private CancelOrderViewActions viewActions;
  @Mock
  private Observer<ViewState<CancelOrderViewActions>> viewStateObserver;
  @Captor
  private ArgumentCaptor<ViewState<CancelOrderViewActions>> viewStateCaptor;

  @Before
  public void setUp() {
    when(useCase.cancelOrder(any())).thenReturn(Completable.never());
    viewModel = new CancelOrderViewModelImpl(errorReporter, useCase);
  }

  /* Проверяем отправку ошибок в репортер */

  /**
   * Должен отправить ошибку получения причин отказа.
   */
  @Test
  public void reportGetSelectedReasonsError() {
    when(useCase.cancelOrder(cancelOrderReason1))
        .thenReturn(Completable.error(DataMappingException::new));

    // Действие:
    viewModel.selectItem(cancelOrderReason1);

    // Результат:
    verify(errorReporter, only()).reportError(any(DataMappingException.class));
  }

  /**
   * Должен отправить ошибку, если выбраной причины нет в списке.
   */
  @Test
  public void reportOutOfBoundsError() {
    when(useCase.cancelOrder(cancelOrderReason2))
        .thenReturn(Completable.error(IndexOutOfBoundsException::new));

    // Действие:
    viewModel.selectItem(cancelOrderReason2);

    // Результат:
    verify(errorReporter, only()).reportError(any(IndexOutOfBoundsException.class));
  }

  /* Тетсируем работу с юзкейсом. */

  /**
   * Должен попросить юзкейс отказаться от заказа.
   */
  @Test
  public void askUseCaseToCancelOrder() {
    // Действие:
    viewModel.selectItem(cancelOrderReason1);

    // Результат:
    verify(useCase, only()).cancelOrder(cancelOrderReason1);
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
    verify(useCase, only()).cancelOrder(cancelOrderReason);
  }

  /* Тетсируем переключение состояний. */

  /**
   * Должен вернуть состояние вида "В процессе".
   */
  @Test
  public void setPendingViewStateStateToLiveDataForCancelOrder() {
    // Дано:
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    viewModel.selectItem(cancelOrderReason1);

    // Результат:
    verify(viewStateObserver, only()).onChanged(viewStateCaptor.capture());
    for (ViewState<CancelOrderViewActions> viewState : viewStateCaptor.getAllValues()) {
      viewState.apply(viewActions);
    }
    verify(viewActions, only()).showCancelOrderPending(true);
  }

  /**
   * Должен вернуть состояние вида "не в процессе" при ошибке.
   */
  @Test
  public void setCancelOrderViewStateToLiveDataAfterPendingForCancelOrderError() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewActions);
    when(useCase.cancelOrder(any())).thenReturn(Completable.error(Exception::new));
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    viewModel.selectItem(cancelOrderReason1);

    // Результат:
    verify(viewStateObserver, times(2)).onChanged(viewStateCaptor.capture());
    verifyNoMoreInteractions(viewStateObserver);
    for (ViewState<CancelOrderViewActions> viewState : viewStateCaptor.getAllValues()) {
      viewState.apply(viewActions);
    }
    inOrder.verify(viewActions).showCancelOrderPending(true);
    inOrder.verify(viewActions).showCancelOrderPending(false);
    verifyNoMoreInteractions(viewActions);
  }

  /**
   * Должен вернуть состояние вида "не в процессе" при успехе.
   */
  @Test
  public void setCancelOrderViewStateToLiveDataAfterPendingForCancelOrderSuccess() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewActions);
    when(useCase.cancelOrder(any())).thenReturn(Completable.complete());
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    viewModel.selectItem(cancelOrderReason1);

    // Результат:
    verify(viewStateObserver, times(2)).onChanged(viewStateCaptor.capture());
    verifyNoMoreInteractions(viewStateObserver);
    for (ViewState<CancelOrderViewActions> viewState : viewStateCaptor.getAllValues()) {
      viewState.apply(viewActions);
    }
    inOrder.verify(viewActions).showCancelOrderPending(true);
    inOrder.verify(viewActions).showCancelOrderPending(false);
    verifyNoMoreInteractions(viewActions);
  }

  /* Тетсируем навигацию. */

  /**
   * Не игнорировать другие ошибки.
   */
  @Test
  public void setNothingToLiveDataForOtherError() {
    // Дано:
    when(useCase.cancelOrder(any())).thenReturn(Completable.error(new DataMappingException()));
    viewModel.getNavigationLiveData().observeForever(navigateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    viewModel.selectItem(cancelOrderReason);

    // Результат:
    verifyZeroInteractions(navigateObserver);
  }

  /**
   * Должен игнорировать неуспешные выборы.
   */
  @Test
  public void setNothingToLiveDataForWrongChoice() {
    // Дано:
    when(useCase.cancelOrder(any())).thenReturn(Completable.error(new IndexOutOfBoundsException()));
    viewModel.getNavigationLiveData().observeForever(navigateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

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
    when(useCase.cancelOrder(any())).thenReturn(Completable.error(new IllegalStateException()));
    viewModel.getNavigationLiveData().observeForever(navigateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

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
    when(useCase.cancelOrder(any())).thenReturn(Completable.complete());
    viewModel.getNavigationLiveData().observeForever(navigateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    viewModel.selectItem(cancelOrderReason);

    // Результат:
    verify(navigateObserver, only()).onChanged(CancelOrderNavigate.ORDER_CANCELED);
  }
}