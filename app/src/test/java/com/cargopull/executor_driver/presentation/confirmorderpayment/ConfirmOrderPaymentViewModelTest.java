package com.cargopull.executor_driver.presentation.confirmorderpayment;

import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;
import com.cargopull.executor_driver.ViewModelThreadTestRule;
import com.cargopull.executor_driver.backend.web.NoNetworkException;
import com.cargopull.executor_driver.interactor.ConfirmOrderPaymentUseCase;
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
public class ConfirmOrderPaymentViewModelTest {

  @ClassRule
  public static final ViewModelThreadTestRule classRule = new ViewModelThreadTestRule();
  @Rule
  public TestRule rule = new InstantTaskExecutorRule();
  private ConfirmOrderPaymentViewModel viewModel;
  @Mock
  private ConfirmOrderPaymentUseCase useCase;

  @Mock
  private Observer<ViewState<ConfirmOrderPaymentViewActions>> viewStateObserver;
  @Mock
  private ConfirmOrderPaymentViewActions viewStateActions;
  @Mock
  private Observer<String> navigateObserver;
  @Captor
  private ArgumentCaptor<ViewState<ConfirmOrderPaymentViewActions>> viewStateCaptor;

  @Before
  public void setUp() {
    when(useCase.confirmPayment()).thenReturn(Completable.never());
    viewModel = new ConfirmOrderPaymentViewModelImpl(useCase);
  }

  /* Тетсируем работу с юзкейсом. */

  /**
   * Должен попросить юзкейс подтвердить оплату заказа.
   */
  @Test
  public void askUseCaseToConfirmOrderPayment() {
    // Дано:
    when(useCase.confirmPayment()).thenReturn(Completable.complete());

    // Действие:
    viewModel.confirmPayment();

    // Результат:
    verify(useCase, only()).confirmPayment();
  }

  /**
   * Не должен трогать юзкейс, если предыдущий запрос подтверждения оплаты заказа еще не
   * завершился.
   */
  @Test
  public void DoNotTouchUseCaseDuringOrderSetting() {
    // Действие:
    viewModel.confirmPayment();
    viewModel.confirmPayment();
    viewModel.confirmPayment();

    // Результат:
    verify(useCase, only()).confirmPayment();
  }

  /* Тетсируем переключение состояний. */

  /**
   * Должен вернуть состояние вида "бездействия" изначально.
   */
  @Test
  public void setIdleViewStateToLiveDataInitially() {
    // Действие:
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Результат:
    verify(viewStateObserver, only()).onChanged(viewStateCaptor.capture());
    viewStateCaptor.getValue().apply(viewStateActions);
    verify(viewStateActions, only()).ConfirmOrderPaymentPending(false);
  }

  /**
   * Должен вернуть состояние вида "В процессе".
   */
  @Test
  public void setPendingViewStateWhenConfirmOrderPayment() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver, viewStateActions);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    viewModel.confirmPayment();

    // Результат:
    inOrder.verify(viewStateObserver, times(2)).onChanged(viewStateCaptor.capture());
    viewStateCaptor.getAllValues().get(0).apply(viewStateActions);
    viewStateCaptor.getAllValues().get(1).apply(viewStateActions);
    inOrder.verify(viewStateActions).ConfirmOrderPaymentPending(false);
    inOrder.verify(viewStateActions).ConfirmOrderPaymentPending(true);
    verifyNoMoreInteractions(viewStateObserver, viewStateActions);
  }

  /**
   * Должен вернуть состояние вида "бездействия" при ошибке сети.
   */
  @Test
  public void setIdleViewStateToLiveDataForError() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver, viewStateActions);
    when(useCase.confirmPayment())
        .thenReturn(Completable.error(NoNetworkException::new));
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    viewModel.confirmPayment();

    // Результат:
    inOrder.verify(viewStateObserver, times(3)).onChanged(viewStateCaptor.capture());
    viewStateCaptor.getAllValues().get(0).apply(viewStateActions);
    viewStateCaptor.getAllValues().get(1).apply(viewStateActions);
    viewStateCaptor.getAllValues().get(2).apply(viewStateActions);
    inOrder.verify(viewStateActions).ConfirmOrderPaymentPending(false);
    inOrder.verify(viewStateActions).ConfirmOrderPaymentPending(true);
    inOrder.verify(viewStateActions).ConfirmOrderPaymentPending(false);
    verifyNoMoreInteractions(viewStateObserver, viewStateActions);
  }

  /**
   * Не должен возвращать новых состояний вида.
   */
  @Test
  public void setNoViewStateToLiveDataForComplete() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver, viewStateActions);
    when(useCase.confirmPayment()).thenReturn(Completable.complete());
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    viewModel.confirmPayment();

    // Результат:
    inOrder.verify(viewStateObserver, times(2)).onChanged(viewStateCaptor.capture());
    viewStateCaptor.getAllValues().get(0).apply(viewStateActions);
    viewStateCaptor.getAllValues().get(1).apply(viewStateActions);
    inOrder.verify(viewStateActions).ConfirmOrderPaymentPending(false);
    inOrder.verify(viewStateActions).ConfirmOrderPaymentPending(true);
    verifyNoMoreInteractions(viewStateObserver, viewStateActions);
  }

  /* Тетсируем навигацию. */

  /**
   * Не должен никуда переходить изначально.
   */
  @Test
  public void doNotSetNavigateInitially() {
    // Действие:
    viewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Результат:
    verifyZeroInteractions(navigateObserver);
  }

  /**
   * Не должен никуда переходить для вида "В процессе".
   */
  @Test
  public void doNotSetNavigateForPending() {
    // Дано:
    viewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Действие:
    viewModel.confirmPayment();

    // Результат:
    verifyZeroInteractions(navigateObserver);
  }

  /**
   * Должен вернуть "перейти к ошибке соединения".
   */
  @Test
  public void setNavigateForNoNetworkError() {
    // Дано:
    when(useCase.confirmPayment())
        .thenReturn(Completable.error(NoNetworkException::new));
    viewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Действие:
    viewModel.confirmPayment();

    // Результат:
    verify(navigateObserver, only()).onChanged(CommonNavigate.NO_CONNECTION);
  }

  /**
   * Не должен никуда переходить.
   */
  @Test
  public void doNotSetNavigateForIdle() {
    // Дано:
    when(useCase.confirmPayment()).thenReturn(Completable.complete());
    viewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Действие:
    viewModel.confirmPayment();

    // Результат:
    verifyZeroInteractions(navigateObserver);
  }
}