package com.cargopull.executor_driver.presentation.missedorder;

import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.Observer;
import com.cargopull.executor_driver.ViewModelThreadTestRule;
import com.cargopull.executor_driver.interactor.MissedOrderUseCase;
import com.cargopull.executor_driver.presentation.ViewState;
import io.reactivex.Flowable;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class MissedOrderViewModelTest {

  @ClassRule
  public static final ViewModelThreadTestRule classRule = new ViewModelThreadTestRule();
  @Rule
  public TestRule rule = new InstantTaskExecutorRule();
  private MissedOrderViewModel viewModel;
  @Mock
  private Observer<ViewState<MissedOrderViewActions>> viewStateObserver;
  @Captor
  private ArgumentCaptor<ViewState<MissedOrderViewActions>> viewStateCaptor;
  @Mock
  private MissedOrderViewActions viewActions;

  @Mock
  private MissedOrderUseCase missedOrderUseCase;

  @Before
  public void setUp() {
    when(missedOrderUseCase.getMissedOrders()).thenReturn(Flowable.never());
    viewModel = new MissedOrderViewModelImpl(missedOrderUseCase);
  }

  /* Тетсируем работу с юзкейсом. */

  /**
   * Должен попросить у юзкейса загрузить сообщения об упущенных заказах.
   */
  @Test
  public void askDataReceiverToSubscribeToMissedOrdersMessages() {
    // Действие:
    viewModel.initializeMissedOrderMessages();

    // Результат:
    verify(missedOrderUseCase, only()).getMissedOrders();
  }

  /**
   * Должен просить у юзкейса загрузить сообщения об упущенных заказах, даже если уже подписан.
   */
  @Test
  public void askDataReceiverToSubscribeToMissedOrdersMessagesIfAlreadyAsked() {
    // Действие:
    viewModel.initializeMissedOrderMessages();
    viewModel.initializeMissedOrderMessages();
    viewModel.initializeMissedOrderMessages();

    // Результат:
    verify(missedOrderUseCase, times(3)).getMissedOrders();
    verifyNoMoreInteractions(missedOrderUseCase);
  }

  /* Тетсируем сообщение. */

  /**
   * Должен показать сообщение об упущенном заказе.
   */
  @Test
  public void showMissedOrderMessage() {
    // Дано:
    when(missedOrderUseCase.getMissedOrders()).thenReturn(Flowable.just("Message"));

    // Действие:
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);
    viewModel.initializeMissedOrderMessages();

    // Результат:
    verify(viewStateObserver, only()).onChanged(viewStateCaptor.capture());
    viewStateCaptor.getValue().apply(viewActions);
    verify(viewActions, only()).showMissedOrderMessage("Message");
  }

  /**
   * Не должен показывать пустое сообщение.
   */
  @Test
  public void doNotShowEmptyMessage() {
    // Дано:
    when(missedOrderUseCase.getMissedOrders()).thenReturn(Flowable.just(""));

    // Действие:
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);
    viewModel.initializeMissedOrderMessages();

    // Результат:
    verifyZeroInteractions(viewStateObserver);
  }

  /**
   * Не должен показывать сообщение из пробелов.
   */
  @Test
  public void doNotShowSpaceMessage() {
    // Дано:
    when(missedOrderUseCase.getMissedOrders()).thenReturn(Flowable.just("\n"));

    // Действие:
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);
    viewModel.initializeMissedOrderMessages();

    // Результат:
    verifyZeroInteractions(viewStateObserver);
  }
}