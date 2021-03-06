package com.cargopull.executor_driver.presentation.movingtoclienttimer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import androidx.annotation.NonNull;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;

import com.cargopull.executor_driver.ViewModelThreadTestRule;
import com.cargopull.executor_driver.backend.analytics.ErrorReporter;
import com.cargopull.executor_driver.entity.Order;
import com.cargopull.executor_driver.entity.OrderCancelledException;
import com.cargopull.executor_driver.entity.OrderOfferDecisionException;
import com.cargopull.executor_driver.entity.OrderOfferExpiredException;
import com.cargopull.executor_driver.gateway.DataMappingException;
import com.cargopull.executor_driver.interactor.OrderUseCase;
import com.cargopull.executor_driver.presentation.FragmentViewActions;
import com.cargopull.executor_driver.presentation.ViewState;
import com.cargopull.executor_driver.utils.TimeUtils;

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

import java.util.concurrent.TimeUnit;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.TestScheduler;
import io.reactivex.subjects.PublishSubject;

@RunWith(MockitoJUnitRunner.class)
public class MovingToClientTimerViewModelTest {

  @ClassRule
  public static final ViewModelThreadTestRule classRule = new ViewModelThreadTestRule();
  @Rule
  public TestRule rule = new InstantTaskExecutorRule();
  private MovingToClientTimerViewModel viewModel;
  private TestScheduler testScheduler;
  @Mock
  private Observer<ViewState<FragmentViewActions>> viewStateObserver;
  @Mock
  private ErrorReporter errorReporter;
  @Mock
  private OrderUseCase orderUseCase;
  @Mock
  private TimeUtils timeUtils;
  @Mock
  private Order order;
  @Mock
  private Order order1;
  @Mock
  private Order order2;
  @Mock
  private Order order3;
  private PublishSubject<Order> publishSubject;

  @Before
  public void setUp() {
    testScheduler = new TestScheduler();
    RxJavaPlugins.setComputationSchedulerHandler(scheduler -> testScheduler);
    publishSubject = PublishSubject.create();
    when(orderUseCase.getOrders())
        .thenReturn(publishSubject.toFlowable(BackpressureStrategy.BUFFER));
    viewModel = new MovingToClientTimerViewModelImpl(errorReporter, orderUseCase, timeUtils);
  }

  /* ?????????????????? ???????????????? ???????????? ?? ???????????????? */

  /**
   * ???????????? ?????????????????? ????????????.
   */
  @Test
  public void reportError() {
    // Action:
    publishSubject.onError(new DataMappingException());

    // Effect:
    verify(errorReporter, only()).reportError(any(DataMappingException.class));
  }

  /* ?????????????????? ???????????? ?? ???????????????? ????????????. */

  /**
   * ???????????? ?????????????? ???????????? ???????????????? ???????????? ?????? ????????????????.
   */
  @Test
  public void askUseCaseForOrdersInitially() {
    // Effect:
    verify(orderUseCase, only()).getOrders();
  }

  /**
   * ???? ???????????? ?????????????? ???????????? ???????????????? ???????????? ???? ??????????????????.
   */
  @Test
  public void doNotAskUseCaseForOrdersOnSubscriptions() {
    // Action:
    viewModel.getViewStateLiveData();
    viewModel.getNavigationLiveData();
    viewModel.getViewStateLiveData();
    viewModel.getNavigationLiveData();

    // Effect:
    verify(orderUseCase, only()).getOrders();
  }


  /* ?????????????????? ???????????????????????? ??????????????????. */

  /**
   * ???????????? ?????????????? ?????????????????? ???????? ???????????????? ????????????????????.
   */
  @Test
  public void setPendingViewStateToLiveDataInitially() {
    // Given:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);

    // Action:
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Effect:
    inOrder.verify(viewStateObserver).onChanged(any(MovingToClientTimerViewStatePending.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * ???????????? ?????????????? ?????????????????? ???????? "??????????????" ?? ?????????????? ?????????????????? ?????????? ???????????????????????? ????????????.
   */
  @Test
  public void doNotSetAnyViewStateToLiveDataForError() {
    // Given:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    publishSubject.onError(new Exception());

    // Effect:
    inOrder.verify(viewStateObserver).onChanged(new MovingToClientTimerViewStatePending(null));
    inOrder.verify(viewStateObserver).onChanged(new MovingToClientTimerViewStateCounting(0));
  }

  /**
   * ???????????? ?????????????? ?????????????????? ???????? "??????????????" ?? ?????????????? ????????????????????.
   */
  @Test
  public void setIdleViewStateToLiveData() {
    // Given:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);
    when(timeUtils.currentTimeMillis()).thenReturn(12337827012L);
    when(order.getConfirmationTime()).thenReturn(12337811012L);
    when(order.getEtaToStartPoint()).thenReturn(4178929L);
    when(order1.getConfirmationTime()).thenReturn(12333655083L);
    when(order1.getEtaToStartPoint()).thenReturn(4171929L);
    when(order2.getConfirmationTime()).thenReturn(12333555083L);
    when(order2.getEtaToStartPoint()).thenReturn(4171929L);
    when(order3.getConfirmationTime()).thenReturn(12333555083L);
    when(order3.getEtaToStartPoint()).thenReturn(4171L);

    // Action:
    publishSubject.onNext(order);
    testScheduler.advanceTimeBy(1, TimeUnit.NANOSECONDS);
    publishSubject.onNext(order1);
    testScheduler.advanceTimeBy(1, TimeUnit.NANOSECONDS);
    publishSubject.onNext(order2);
    testScheduler.advanceTimeBy(1, TimeUnit.NANOSECONDS);
    publishSubject.onNext(order3);
    testScheduler.advanceTimeBy(1, TimeUnit.NANOSECONDS);

    // Effect:
    inOrder.verify(viewStateObserver).onChanged(new MovingToClientTimerViewStatePending(null));
    inOrder.verify(viewStateObserver).onChanged(new MovingToClientTimerViewStateCounting(4162929));
    inOrder.verify(viewStateObserver).onChanged(new MovingToClientTimerViewStateCounting(0));
    inOrder.verify(viewStateObserver).onChanged(new MovingToClientTimerViewStateCounting(-100_000));
    inOrder.verify(viewStateObserver).onChanged(new MovingToClientTimerViewStateCounting(-4267758));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * ???????????? ?????????????? ?????????????????? ???????? "??????????????" ?? ???????????? ????????????????????.
   */
  @Test
  public void setIdleViewStatesToLiveData() {
    // Given:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);
    when(timeUtils.currentTimeMillis()).thenReturn(12337827012L);
    when(order.getConfirmationTime()).thenReturn(12337811012L);
    when(order.getEtaToStartPoint()).thenReturn(4178929L);
    when(order1.getConfirmationTime()).thenReturn(12333655083L);
    when(order1.getEtaToStartPoint()).thenReturn(4171929L);
    when(order2.getConfirmationTime()).thenReturn(12333555083L);
    when(order2.getEtaToStartPoint()).thenReturn(4171929L);
    when(order3.getConfirmationTime()).thenReturn(12333555083L);
    when(order3.getEtaToStartPoint()).thenReturn(4171L);

    // Action:
    publishSubject.onNext(order);
    testScheduler.advanceTimeBy(5, TimeUnit.MINUTES);
    publishSubject.onNext(order1);
    testScheduler.advanceTimeBy(5, TimeUnit.MINUTES);
    publishSubject.onNext(order2);
    testScheduler.advanceTimeBy(5, TimeUnit.MINUTES);
    publishSubject.onNext(order3);
    testScheduler.advanceTimeBy(15, TimeUnit.MINUTES);

    // Effect:
    inOrder.verify(viewStateObserver).onChanged(new MovingToClientTimerViewStatePending(null));
    for (int i = 4162929; i >= 3862929; i -= 1000) {
      inOrder.verify(viewStateObserver).onChanged(new MovingToClientTimerViewStateCounting(i));
    }
    for (int i = 0; i >= -300_000; i -= 1000) {
      inOrder.verify(viewStateObserver).onChanged(new MovingToClientTimerViewStateCounting(i));
    }
    for (int i = -100_000; i >= -400_000; i -= 1000) {
      inOrder.verify(viewStateObserver).onChanged(new MovingToClientTimerViewStateCounting(i));
    }
    for (int i = -4267758; i >= -4866758; i -= 1000) {
      inOrder.verify(viewStateObserver).onChanged(new MovingToClientTimerViewStateCounting(i));
    }
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * ???????????? ?????????????? ?????????????????? ???????? "??????????????" ?? ?????????????? ?????????????????? ?????????? ????????????.
   */
  @Test
  public void setZeroViewStateToLiveDataForError() {
    // Given:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);
    when(timeUtils.currentTimeMillis()).thenReturn(12337827012L);
    when(order.getConfirmationTime()).thenReturn(12337811012L);
    when(order.getEtaToStartPoint()).thenReturn(4178929L);

    // Action:
    publishSubject.onNext(order);
    testScheduler.advanceTimeBy(5, TimeUnit.MINUTES);
    publishSubject.onError(new Exception());

    // Effect:
    inOrder.verify(viewStateObserver).onChanged(new MovingToClientTimerViewStatePending(null));
    for (int i = 4162929; i >= 3862929; i -= 1000) {
      inOrder.verify(viewStateObserver).onChanged(new MovingToClientTimerViewStateCounting(i));
    }
    inOrder.verify(viewStateObserver).onChanged(new MovingToClientTimerViewStateCounting(0));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * ???? ???????????? ???????????????????? ???????? ?????????????????? ???????? ?????????? ?????????????????? ???????????? "?????????? ??????????".
   */
  @Test
  public void setZeroViewStateToLiveDataForExpiredError() {
    // Given:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    PublishSubject<Order> publishSubject = PublishSubject.create();
    when(orderUseCase.getOrders()).thenReturn(
        Observable.create(
            new ObservableOnSubscribe<Order>() {
              private boolean run;

              @Override
              public void subscribe(@NonNull ObservableEmitter<Order> emitter) {
                if (!run) {
                  run = true;
                  emitter.onNext(order);
                  testScheduler.advanceTimeBy(2, TimeUnit.SECONDS);
                  emitter.onError(new OrderOfferExpiredException("message"));
                } else {
                  testScheduler.advanceTimeBy(5, TimeUnit.MINUTES);
                  emitter.onNext(order2);
                  testScheduler.advanceTimeBy(1, TimeUnit.NANOSECONDS);
                }
              }
            }
        ).startWith(publishSubject)
            .toFlowable(BackpressureStrategy.BUFFER)
    );
    when(timeUtils.currentTimeMillis()).thenReturn(12337827012L);
    when(order.getConfirmationTime()).thenReturn(12337811012L);
    when(order.getEtaToStartPoint()).thenReturn(4178929L);
    when(order2.getConfirmationTime()).thenReturn(12333555083L);
    when(order2.getEtaToStartPoint()).thenReturn(4171929L);
    viewModel = new MovingToClientTimerViewModelImpl(errorReporter, orderUseCase, timeUtils);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    publishSubject.onComplete();

    // Effect:
    inOrder.verify(viewStateObserver).onChanged(new MovingToClientTimerViewStatePending(null));
    inOrder.verify(viewStateObserver).onChanged(new MovingToClientTimerViewStateCounting(4162929));
    inOrder.verify(viewStateObserver).onChanged(new MovingToClientTimerViewStateCounting(4161929));
    inOrder.verify(viewStateObserver).onChanged(new MovingToClientTimerViewStateCounting(4160929));
    inOrder.verify(viewStateObserver).onChanged(new MovingToClientTimerViewStateCounting(0));
    inOrder.verify(viewStateObserver).onChanged(new MovingToClientTimerViewStateCounting(-100_000));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * ???? ???????????? ???????????????????? ???????? ?????????????????? ???????? ?????????? ?????????????????? ???????????? "?????????? ??????????????".
   */
  @Test
  public void setZeroViewStateToLiveDataForCancelledError() {
    // Given:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    PublishSubject<Order> publishSubject = PublishSubject.create();
    when(orderUseCase.getOrders()).thenReturn(
        Observable.create(
            new ObservableOnSubscribe<Order>() {
              private boolean run;

              @Override
              public void subscribe(@NonNull ObservableEmitter<Order> emitter) {
                if (!run) {
                  run = true;
                  emitter.onNext(order);
                  testScheduler.advanceTimeBy(2, TimeUnit.SECONDS);
                  emitter.onError(new OrderCancelledException());
                } else {
                  testScheduler.advanceTimeBy(5, TimeUnit.MINUTES);
                  emitter.onNext(order2);
                  testScheduler.advanceTimeBy(1, TimeUnit.NANOSECONDS);
                }
              }
            }
        ).startWith(publishSubject)
            .toFlowable(BackpressureStrategy.BUFFER)
    );
    when(timeUtils.currentTimeMillis()).thenReturn(12337827012L);
    when(order.getConfirmationTime()).thenReturn(12337811012L);
    when(order.getEtaToStartPoint()).thenReturn(4178929L);
    when(order2.getConfirmationTime()).thenReturn(12333555083L);
    when(order2.getEtaToStartPoint()).thenReturn(4171929L);
    viewModel = new MovingToClientTimerViewModelImpl(errorReporter, orderUseCase, timeUtils);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    publishSubject.onComplete();

    // Effect:
    inOrder.verify(viewStateObserver).onChanged(new MovingToClientTimerViewStatePending(null));
    inOrder.verify(viewStateObserver).onChanged(new MovingToClientTimerViewStateCounting(4162929));
    inOrder.verify(viewStateObserver).onChanged(new MovingToClientTimerViewStateCounting(4161929));
    inOrder.verify(viewStateObserver).onChanged(new MovingToClientTimerViewStateCounting(4160929));
    inOrder.verify(viewStateObserver).onChanged(new MovingToClientTimerViewStateCounting(0));
    inOrder.verify(viewStateObserver).onChanged(new MovingToClientTimerViewStateCounting(-100_000));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * ???? ???????????? ???????????????????? ???????? ?????????????????? ???????? ?????????? ?????????????????? ???????????? ?? ???????????????? ?????????????? ???? ????????????.
   */
  @Test
  public void setZeroViewStateToLiveDataForDecisionError() {
    // Given:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    PublishSubject<Order> publishSubject = PublishSubject.create();
    when(orderUseCase.getOrders()).thenReturn(
        Observable.create(
            new ObservableOnSubscribe<Order>() {
              private boolean run;

              @Override
              public void subscribe(@NonNull ObservableEmitter<Order> emitter) {
                if (!run) {
                  run = true;
                  emitter.onNext(order);
                  testScheduler.advanceTimeBy(2, TimeUnit.SECONDS);
                  emitter.onError(new OrderOfferDecisionException());
                } else {
                  testScheduler.advanceTimeBy(5, TimeUnit.MINUTES);
                  emitter.onNext(order2);
                  testScheduler.advanceTimeBy(1, TimeUnit.NANOSECONDS);
                }
              }
            }
        ).startWith(publishSubject)
            .toFlowable(BackpressureStrategy.BUFFER)
    );
    when(timeUtils.currentTimeMillis()).thenReturn(12337827012L);
    when(order.getConfirmationTime()).thenReturn(12337811012L);
    when(order.getEtaToStartPoint()).thenReturn(4178929L);
    when(order2.getConfirmationTime()).thenReturn(12333555083L);
    when(order2.getEtaToStartPoint()).thenReturn(4171929L);
    viewModel = new MovingToClientTimerViewModelImpl(errorReporter, orderUseCase, timeUtils);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    publishSubject.onComplete();

    // Effect:
    inOrder.verify(viewStateObserver).onChanged(new MovingToClientTimerViewStatePending(null));
    inOrder.verify(viewStateObserver).onChanged(new MovingToClientTimerViewStateCounting(4162929));
    inOrder.verify(viewStateObserver).onChanged(new MovingToClientTimerViewStateCounting(4161929));
    inOrder.verify(viewStateObserver).onChanged(new MovingToClientTimerViewStateCounting(4160929));
    inOrder.verify(viewStateObserver).onChanged(new MovingToClientTimerViewStateCounting(0));
    inOrder.verify(viewStateObserver).onChanged(new MovingToClientTimerViewStateCounting(-100_000));
    verifyNoMoreInteractions(viewStateObserver);
  }
}