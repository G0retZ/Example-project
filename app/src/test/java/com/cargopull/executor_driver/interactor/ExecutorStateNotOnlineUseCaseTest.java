package com.cargopull.executor_driver.interactor;

import static com.cargopull.executor_driver.entity.ExecutorState.BLOCKED;
import static com.cargopull.executor_driver.entity.ExecutorState.CLIENT_ORDER_CONFIRMATION;
import static com.cargopull.executor_driver.entity.ExecutorState.DRIVER_ORDER_CONFIRMATION;
import static com.cargopull.executor_driver.entity.ExecutorState.DRIVER_PRELIMINARY_ORDER_CONFIRMATION;
import static com.cargopull.executor_driver.entity.ExecutorState.MOVING_TO_CLIENT;
import static com.cargopull.executor_driver.entity.ExecutorState.ONLINE;
import static com.cargopull.executor_driver.entity.ExecutorState.ORDER_FULFILLMENT;
import static com.cargopull.executor_driver.entity.ExecutorState.PAYMENT_CONFIRMATION;
import static com.cargopull.executor_driver.entity.ExecutorState.SHIFT_CLOSED;
import static com.cargopull.executor_driver.entity.ExecutorState.SHIFT_OPENED;
import static com.cargopull.executor_driver.entity.ExecutorState.WAITING_FOR_CLIENT;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.cargopull.executor_driver.UseCaseThreadTestRule;
import com.cargopull.executor_driver.entity.ExecutorState;
import com.cargopull.executor_driver.utils.Pair;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.Arrays;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.observers.TestObserver;

@RunWith(Parameterized.class)
public class ExecutorStateNotOnlineUseCaseTest {

  @ClassRule
  public static final UseCaseThreadTestRule classRule = new UseCaseThreadTestRule();
  private final boolean expectedArgumentException;
  private final ExecutorState conditionExecutorState;
  private final boolean expectedGatewayInvocation;
  @Rule
  public MockitoRule rule = MockitoJUnit.rule();
  private ExecutorStateNotOnlineUseCase useCase;
  @Mock
  private ExecutorStateSwitchGateway gateway;
  @Mock
  private ExecutorStateUseCase executorStateUseCase;

  // Each parameter should be placed as an argument here
  // Every time runner triggers, it will pass the arguments
  // from parameters we defined in primeNumbers() method

  public ExecutorStateNotOnlineUseCaseTest(
      Pair<ExecutorState, Pair<Boolean, Boolean>> conditions) {
    conditionExecutorState = conditions.first;
    expectedArgumentException = conditions.second.first;
    expectedGatewayInvocation = conditions.second.second;
  }

  @Parameterized.Parameters
  public static Iterable<Pair<ExecutorState, Pair<Boolean, Boolean>>> primeNumbers() {
    // Соответствия значений статуса эксепшенам и действиям гейтвея
    return Arrays.asList(
            new Pair<>(BLOCKED, new Pair<>(true, false)),
            new Pair<>(SHIFT_CLOSED, new Pair<>(true, false)),
            new Pair<>(SHIFT_OPENED, new Pair<>(true, false)),
            new Pair<>(ONLINE, new Pair<>(false, true)),
            new Pair<>(DRIVER_ORDER_CONFIRMATION, new Pair<>(true, false)),
            new Pair<>(DRIVER_PRELIMINARY_ORDER_CONFIRMATION, new Pair<>(true, false)),
            new Pair<>(CLIENT_ORDER_CONFIRMATION, new Pair<>(true, false)),
            new Pair<>(MOVING_TO_CLIENT, new Pair<>(true, false)),
            new Pair<>(WAITING_FOR_CLIENT, new Pair<>(true, false)),
            new Pair<>(ORDER_FULFILLMENT, new Pair<>(true, false)),
            new Pair<>(PAYMENT_CONFIRMATION, new Pair<>(false, true))
    );
  }

  @Before
  public void setUp() {
    when(gateway.sendNewExecutorState(ExecutorState.SHIFT_OPENED))
        .thenReturn(Completable.complete());
    when(executorStateUseCase.getExecutorStates()).thenReturn(Flowable.never());
    useCase = new ExecutorStateNotOnlineUseCaseImpl(gateway, executorStateUseCase,
        ExecutorState.ONLINE, ExecutorState.PAYMENT_CONFIRMATION);
  }

  /* Проверяем работу с юзкейсом состояний */

  /**
   * Должен запросить получение смены состояний исполнителя.
   */
  @Test
  public void getExecutorStates() {
    // Действие:
    useCase.setExecutorNotOnline().test().isDisposed();

    // Результат:
    verify(executorStateUseCase, only()).getExecutorStates();
  }

  /* Проверяем работу с гейтвеем */

  /**
   * Не должен трогать гейтвей передачи статусов, если статуса еще нет.
   */
  @Test
  public void DoNotTouchGatewayWithoutStatus() {
    // Действие:
    useCase.setExecutorNotOnline().test().isDisposed();

    // Результат:
    verifyNoInteractions(gateway);
  }

  @Test
  public void touchOrNotGateway() {
    // Дано:
    when(executorStateUseCase.getExecutorStates())
        .thenReturn(Flowable.just(conditionExecutorState).concatWith(Flowable.never()));

    // Действие:
    useCase.setExecutorNotOnline().test().isDisposed();

    // Результат:
    if (expectedGatewayInvocation) {
      // Должен отправить статус "смена открыта" через гейтвей передачи статусов.
      verify(gateway, only()).sendNewExecutorState(ExecutorState.SHIFT_OPENED);
    } else {
      // Не должен трогать гейтвей передачи статусов
      verifyNoInteractions(gateway);
    }
  }

  /* Проверяем ответы */

  @Test
  public void answerIllegalArgumentErrorOrComplete() {
    // Дано:
    when(executorStateUseCase.getExecutorStates())
        .thenReturn(Flowable.just(conditionExecutorState).concatWith(Flowable.never()));

    // Действие:
    TestObserver<Void> testObserver = useCase.setExecutorNotOnline().test();

    // Результат:
    testObserver.assertNoValues();
    if (expectedArgumentException) {
      // Должен вернуть ошибку неподходящего статуса
      testObserver.assertNotComplete();
      testObserver.assertError(IllegalArgumentException.class);
    } else {
      // Должен ответить завершением
      testObserver.assertNoErrors();
      testObserver.assertComplete();
    }
  }

  /**
   * Должен вернуть ошибку при ошибке отправки статуса.
   */
  @Test
  public void answerWithErrorIfSendFailed() {
    // Дано:
    when(executorStateUseCase.getExecutorStates())
        .thenReturn(Flowable.just(conditionExecutorState).concatWith(Flowable.never()));
    when(gateway.sendNewExecutorState(ExecutorState.SHIFT_OPENED))
        .thenReturn(Completable.error(new Exception()));

    // Действие:
    TestObserver<Void> testObserver = useCase.setExecutorNotOnline().test();

    // Результат:
    testObserver.assertNoValues();
    testObserver.assertNotComplete();
    testObserver
        .assertError(expectedArgumentException ? IllegalArgumentException.class : Exception.class);
  }
}