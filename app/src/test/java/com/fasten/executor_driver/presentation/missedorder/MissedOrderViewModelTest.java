package com.fasten.executor_driver.presentation.missedorder;

import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.Observer;
import com.fasten.executor_driver.entity.ExecutorState;
import com.fasten.executor_driver.interactor.MissedOrderUseCase;
import com.fasten.executor_driver.presentation.ViewState;
import io.reactivex.Flowable;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import org.junit.Before;
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

  @Rule
  public TestRule rule = new InstantTaskExecutorRule();
  private MissedOrderViewModel missedOrderViewModel;
  @Mock
  private Observer<ViewState<MissedOrderViewActions>> viewStateObserver;
  @Captor
  private ArgumentCaptor<ViewState<MissedOrderViewActions>> viewStateCaptor;
  @Mock
  private MissedOrderViewActions missedOrderViewActions;

  @Mock
  private MissedOrderUseCase missedOrderUseCase;

  @Before
  public void setUp() {
    ExecutorState.ONLINE.setData(null);
    RxJavaPlugins.setSingleSchedulerHandler(scheduler -> Schedulers.trampoline());
    RxAndroidPlugins.setInitMainThreadSchedulerHandler(scheduler -> Schedulers.trampoline());
    when(missedOrderUseCase.getMissedOrders()).thenReturn(Flowable.never());
    missedOrderViewModel = new MissedOrderViewModelImpl(missedOrderUseCase);
  }

  /* Тетсируем работу с юзкейсом. */

  /**
   * Должен попросить у юзкейса загрузить сообщения об упущенных заказах.
   */
  @Test
  public void askDataReceiverToSubscribeToMissedOrdersMessages() {
    // Действие:
    missedOrderViewModel.initializeMissedOrderMessages();

    // Результат:
    verify(missedOrderUseCase, only()).getMissedOrders();
  }

  /**
   * Должен просить у юзкейса загрузить сообщения об упущенных заказах, даже если уже подписан.
   */
  @Test
  public void askDataReceiverToSubscribeToMissedOrdersMessagesIfAlreadyAsked() {
    // Действие:
    missedOrderViewModel.initializeMissedOrderMessages();
    missedOrderViewModel.initializeMissedOrderMessages();
    missedOrderViewModel.initializeMissedOrderMessages();

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
    missedOrderViewModel.getViewStateLiveData().observeForever(viewStateObserver);
    missedOrderViewModel.initializeMissedOrderMessages();

    // Результат:
    verify(viewStateObserver, only()).onChanged(viewStateCaptor.capture());
    viewStateCaptor.getValue().apply(missedOrderViewActions);
    verify(missedOrderViewActions, only()).showMissedOrderMessage("Message");
  }

  /**
   * Не должен показывать пустое сообщение.
   */
  @Test
  public void doNotShowEmptyMessage() {
    // Дано:
    when(missedOrderUseCase.getMissedOrders()).thenReturn(Flowable.just(""));

    // Действие:
    missedOrderViewModel.getViewStateLiveData().observeForever(viewStateObserver);
    missedOrderViewModel.initializeMissedOrderMessages();

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
    missedOrderViewModel.getViewStateLiveData().observeForever(viewStateObserver);
    missedOrderViewModel.initializeMissedOrderMessages();

    // Результат:
    verifyZeroInteractions(viewStateObserver);
  }
}