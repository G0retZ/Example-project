package com.cargopull.executor_driver.interactor;

import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cargopull.executor_driver.UseCaseThreadTestRule;
import com.cargopull.executor_driver.gateway.DataMappingException;
import io.reactivex.Completable;
import io.reactivex.observers.TestObserver;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CurrentCostPollingUseCaseTest {

  @ClassRule
  public static final UseCaseThreadTestRule classRule = new UseCaseThreadTestRule();

  private CurrentCostPollingUseCase currentCostPollingUseCase;

  @Mock
  private CurrentCostPollingGateway gateway;

  @Before
  public void setUp() {
    when(gateway.startPolling()).thenReturn(Completable.never());
    currentCostPollingUseCase = new CurrentCostPollingUseCaseImpl(gateway);
  }

  /* Проверяем работу с гейтвеем */

  /**
   * Должен запросить у гейтвея таймеры заказа.
   */
  @Test
  public void askGatewayForPollingTimers() {
    // Действие:
    currentCostPollingUseCase.listenForPolling().test().isDisposed();
    currentCostPollingUseCase.listenForPolling().test().isDisposed();
    currentCostPollingUseCase.listenForPolling().test().isDisposed();
    currentCostPollingUseCase.listenForPolling().test().isDisposed();

    // Результат:
    verify(gateway, only()).startPolling();
  }

  /* Проверяем ответы */

  /**
   * Должен ждать завершения.
   */
  @Test
  public void waitForCompletionOrError() {
    // Действие:
    TestObserver<Void> testObserver = currentCostPollingUseCase.listenForPolling().test();

    // Результат:
    testObserver.assertNoValues();
    testObserver.assertNoErrors();
    testObserver.assertNotComplete();
  }

  /**
   * Должен вернуть ошибку.
   */
  @Test
  public void answerWithErrorIfSubscriptionFailed() {
    // Дано:
    when(gateway.startPolling()).thenReturn(Completable.error(DataMappingException::new));

    // Действие:
    TestObserver<Void> testObserver = currentCostPollingUseCase.listenForPolling().test();

    // Результат:
    testObserver.assertError(DataMappingException.class);
    testObserver.assertNotComplete();
    testObserver.assertNoValues();
  }

  /**
   * Должен завершить полинг.
   */
  @Test
  public void answerComplete() {
    // Дано:
    when(gateway.startPolling()).thenReturn(Completable.complete());

    // Действие:
    TestObserver<Void> testObserver = currentCostPollingUseCase.listenForPolling().test();

    // Результат:
    testObserver.assertComplete();
    testObserver.assertNoValues();
    testObserver.assertNoErrors();
  }
}