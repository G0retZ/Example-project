package com.cargopull.executor_driver.presentation.clientorderconfirmationtime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;

import com.cargopull.executor_driver.ViewModelThreadTestRule;
import com.cargopull.executor_driver.backend.analytics.ErrorReporter;
import com.cargopull.executor_driver.entity.ExecutorState;
import com.cargopull.executor_driver.gateway.DataMappingException;
import com.cargopull.executor_driver.interactor.ExecutorStateUseCase;
import com.cargopull.executor_driver.presentation.CommonNavigate;
import com.cargopull.executor_driver.presentation.ViewState;

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
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.TestScheduler;
import io.reactivex.subjects.PublishSubject;

@RunWith(MockitoJUnitRunner.class)
public class ClientOrderConfirmationTimeViewModelTest {

  @ClassRule
  public static final ViewModelThreadTestRule classRule = new ViewModelThreadTestRule();
  @Rule
  public TestRule rule = new InstantTaskExecutorRule();
  private ClientOrderConfirmationTimeViewModel viewModel;
  @Mock
  private ErrorReporter errorReporter;
  @Mock
  private ExecutorStateUseCase useCase;
  @Mock
  private Observer<ViewState<ClientOrderConfirmationTimeViewActions>> viewStateObserver;
  @Mock
  private Observer<String> navigateObserver;
  private TestScheduler testScheduler;
  private PublishSubject<ExecutorState> publishSubject;

  @Before
  public void setUp() {
    testScheduler = new TestScheduler();
    publishSubject = PublishSubject.create();
    RxJavaPlugins.setComputationSchedulerHandler(scheduler -> testScheduler);
    when(useCase.getExecutorStates())
        .thenReturn(publishSubject.toFlowable(BackpressureStrategy.BUFFER));
    viewModel = new ClientOrderConfirmationTimeViewModelImpl(errorReporter, useCase);
  }

  /* Проверяем отправку ошибок в репортер */

  /**
   * Должен отправить ошибку.
   */
  @Test
  public void reportError() {
    // Action:
    publishSubject.onError(new DataMappingException());

    // Effect:
    verify(errorReporter, only()).reportError(any(DataMappingException.class));
  }

  /* Тетсируем работу с юзкейсом. */

  /**
   * Должен просить юзкейс получить актуальное время ожидания , при создании.
   */
  @Test
  public void askUseCaseForOrderTimeInitially() {
    // Effect:
    verify(useCase, only()).getExecutorStates();
  }

  /**
   * Не должен трогать юзкейс на подписках.
   */
  @Test
  public void doNotTouchUseCaseOnSubscriptions() {
    // Action:
    viewModel.getViewStateLiveData();
    viewModel.getNavigationLiveData();
    viewModel.getViewStateLiveData();
    viewModel.getNavigationLiveData();

    // Effect:
    verify(useCase, only()).getExecutorStates();
  }

  /* Тетсируем переключение состояний */

  /**
   * Должен вернуть состояние вида с 0 изначально.
   */
  @Test
  public void setViewStateWithZeroToLiveData() {
    // Given:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);

    // Action:
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Effect:
    inOrder.verify(viewStateObserver)
        .onChanged(new ClientOrderConfirmationTimeViewStateCounting(0));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояния вида с временем заказа И без.
   */
  @Test
  public void setViewStateWithTimesToLiveData() {
    // Given:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    ExecutorState.WAITING_FOR_CLIENT.setCustomerTimer(123_000L);
    publishSubject.onNext(ExecutorState.WAITING_FOR_CLIENT);
    testScheduler.advanceTimeBy(1, TimeUnit.NANOSECONDS);
    ExecutorState.WAITING_FOR_CLIENT.setCustomerTimer(0L);
    publishSubject.onNext(ExecutorState.WAITING_FOR_CLIENT);
    testScheduler.advanceTimeBy(1, TimeUnit.NANOSECONDS);
    ExecutorState.WAITING_FOR_CLIENT.setCustomerTimer(4_728_000L);
    publishSubject.onNext(ExecutorState.WAITING_FOR_CLIENT);
    testScheduler.advanceTimeBy(1, TimeUnit.NANOSECONDS);
    ExecutorState.WAITING_FOR_CLIENT.setCustomerTimer(32_000L);
    publishSubject.onNext(ExecutorState.WAITING_FOR_CLIENT);
    testScheduler.advanceTimeBy(1, TimeUnit.NANOSECONDS);

    // Effect:
    inOrder.verify(viewStateObserver)
        .onChanged(new ClientOrderConfirmationTimeViewStateCounting(0));
    inOrder.verify(viewStateObserver)
        .onChanged(new ClientOrderConfirmationTimeViewStateCounting(123_000));
    inOrder.verify(viewStateObserver)
        .onChanged(any(ClientOrderConfirmationTimeViewStateNotCounting.class));
    inOrder.verify(viewStateObserver)
        .onChanged(new ClientOrderConfirmationTimeViewStateCounting(4_728_000));
    inOrder.verify(viewStateObserver)
        .onChanged(new ClientOrderConfirmationTimeViewStateCounting(32_000));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Не должен возвращать иных состояний вида при ошибке.
   */
  @Test
  public void setNoNewViewStateViewStateToLiveDataOnError() {
    // Given:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    ExecutorState.WAITING_FOR_CLIENT.setCustomerTimer(123_000L);
    publishSubject.onNext(ExecutorState.WAITING_FOR_CLIENT);
    testScheduler.advanceTimeBy(1, TimeUnit.NANOSECONDS);
    ExecutorState.WAITING_FOR_CLIENT.setCustomerTimer(0L);
    publishSubject.onNext(ExecutorState.WAITING_FOR_CLIENT);
    testScheduler.advanceTimeBy(1, TimeUnit.NANOSECONDS);
    ExecutorState.WAITING_FOR_CLIENT.setCustomerTimer(4_728_000L);
    publishSubject.onNext(ExecutorState.WAITING_FOR_CLIENT);
    testScheduler.advanceTimeBy(1, TimeUnit.NANOSECONDS);
    publishSubject.onError(new Exception());
    testScheduler.advanceTimeBy(1, TimeUnit.NANOSECONDS);

    // Effect:
    inOrder.verify(viewStateObserver)
        .onChanged(new ClientOrderConfirmationTimeViewStateCounting(0));
    inOrder.verify(viewStateObserver)
        .onChanged(new ClientOrderConfirmationTimeViewStateCounting(123_000));
    inOrder.verify(viewStateObserver)
        .onChanged(any(ClientOrderConfirmationTimeViewStateNotCounting.class));
    inOrder.verify(viewStateObserver)
        .onChanged(new ClientOrderConfirmationTimeViewStateCounting(4_728_000));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояния вида с отсчетом таймаута и остановиться после ошибки.
   */
  @Test
  public void setCountingViewStatesToLiveDataUntilError() {
    // Given:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    ExecutorState.WAITING_FOR_CLIENT.setCustomerTimer(20_000L);
    publishSubject.onNext(ExecutorState.WAITING_FOR_CLIENT);
    testScheduler.advanceTimeBy(10, TimeUnit.SECONDS);
    publishSubject.onError(new IllegalArgumentException());
    testScheduler.advanceTimeBy(30, TimeUnit.SECONDS);

    // Effect:
    inOrder.verify(viewStateObserver)
        .onChanged(new ClientOrderConfirmationTimeViewStateCounting(0));
    for (int i = 20_000; i > 9000; i -= 1000) {
      inOrder.verify(viewStateObserver)
          .onChanged(new ClientOrderConfirmationTimeViewStateCounting(i));
    }
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояния вида с отсчетом таймаута и состояние вида окончания отсчета после
   * таймаута.
   */
  @Test
  public void setCountingViewStatesToLiveDataUntilItEndsAndNotCountingAfter() {
    // Given:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    ExecutorState.WAITING_FOR_CLIENT.setCustomerTimer(20_000L);
    publishSubject.onNext(ExecutorState.WAITING_FOR_CLIENT);
    testScheduler.advanceTimeBy(30, TimeUnit.SECONDS);

    // Effect:
    inOrder.verify(viewStateObserver)
        .onChanged(new ClientOrderConfirmationTimeViewStateCounting(0));
    for (int i = 20_000; i > 0; i -= 1000) {
      inOrder.verify(viewStateObserver)
          .onChanged(new ClientOrderConfirmationTimeViewStateCounting(i));
    }
    inOrder.verify(viewStateObserver)
        .onChanged(any(ClientOrderConfirmationTimeViewStateNotCounting.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /* Тестируем навигацию. */

  /**
   * Должен вернуть "перейти к ошибке данных сервера".
   */
  @Test
  public void setNavigateToServerDataError() {
    // Given:
    viewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Action:
    publishSubject.onError(new DataMappingException());

    // Effect:
    verify(navigateObserver, only()).onChanged(CommonNavigate.SERVER_DATA_ERROR);
  }
}