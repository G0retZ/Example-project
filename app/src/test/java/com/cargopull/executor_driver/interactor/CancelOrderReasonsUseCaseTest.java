package com.cargopull.executor_driver.interactor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cargopull.executor_driver.UseCaseThreadTestRule;
import com.cargopull.executor_driver.entity.CancelOrderReason;
import com.cargopull.executor_driver.gateway.DataMappingException;
import com.cargopull.executor_driver.utils.ErrorReporter;
import io.reactivex.Flowable;
import io.reactivex.subscribers.TestSubscriber;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CancelOrderReasonsUseCaseTest {

  @ClassRule
  public static final UseCaseThreadTestRule classRule = new UseCaseThreadTestRule();

  private CancelOrderReasonsUseCase useCase;

  @Mock
  private ErrorReporter errorReporter;
  @Mock
  private CancelOrderReasonsGateway gateway;
  @Mock
  private CancelOrderReason cancelOrderReason;
  @Mock
  private CancelOrderReason cancelOrderReason1;
  @Mock
  private CancelOrderReason cancelOrderReason2;
  @Mock
  private CancelOrderReason cancelOrderReason3;

  @Before
  public void setUp() {
    when(gateway.loadCancelOrderReasons()).thenReturn(Flowable.never());
    useCase = new CancelOrderReasonsUseCaseImpl(errorReporter, gateway);
  }

  /* Проверяем работу с гейтвеем */

  /**
   * Должен запросить у гейтвея список причин для отказа только раз.
   */
  @Test
  public void askGatewayForCancelReasons() {
    // Действие:
    useCase.getCancelOrderReasons().test();
    useCase.getCancelOrderReasons().test();
    useCase.getCancelOrderReasons().test();
    useCase.getCancelOrderReasons().test();

    // Результат:
    verify(gateway, only()).loadCancelOrderReasons();
  }

  /* Проверяем отправку ошибок в репортер */

  /**
   * Должен отправить ошибку.
   */
  @Test
  public void reportError() {
    when(gateway.loadCancelOrderReasons()).thenReturn(Flowable.error(DataMappingException::new));

    // Действие:
    useCase.getCancelOrderReasons().test();

    // Результат:
    verify(errorReporter, only()).reportError(any(DataMappingException.class));
  }

  /* Проверяем ответы */

  /**
   * Должен вернуть список причин для отказа.
   */
  @SuppressWarnings("unchecked")
  @Test
  public void answerWithCancelOrderReasons() {
    // Дано:
    when(gateway.loadCancelOrderReasons()).thenReturn(Flowable.just(
        new ArrayList<>(Arrays.asList(cancelOrderReason, cancelOrderReason2, cancelOrderReason3)),
        new ArrayList<>(Arrays.asList(cancelOrderReason, cancelOrderReason1, cancelOrderReason3)),
        new ArrayList<>(Arrays.asList(cancelOrderReason1, cancelOrderReason2, cancelOrderReason3))
    ));

    // Действие:
    TestSubscriber<List<CancelOrderReason>> testSubscriber = useCase.getCancelOrderReasons().test();

    // Результат:
    testSubscriber.assertValues(
        new ArrayList<>(Arrays.asList(cancelOrderReason, cancelOrderReason2, cancelOrderReason3)),
        new ArrayList<>(Arrays.asList(cancelOrderReason, cancelOrderReason1, cancelOrderReason3)),
        new ArrayList<>(Arrays.asList(cancelOrderReason1, cancelOrderReason2, cancelOrderReason3))
    );
    testSubscriber.assertNoErrors();
  }

  /**
   * Должен вернуть ошибку.
   */
  @Test
  public void answerWithError() {
    when(gateway.loadCancelOrderReasons()).thenReturn(Flowable.error(DataMappingException::new));

    // Действие:
    TestSubscriber<List<CancelOrderReason>> testSubscriber = useCase.getCancelOrderReasons().test();

    // Результат:
    testSubscriber.assertError(DataMappingException.class);
    testSubscriber.assertNotComplete();
    testSubscriber.assertNoValues();
  }

  /**
   * Должен завершить получение списка причин для отказа.
   */
  @Test
  public void answerComplete() {
    when(gateway.loadCancelOrderReasons()).thenReturn(Flowable.empty());

    // Действие:
    TestSubscriber<List<CancelOrderReason>> testSubscriber = useCase.getCancelOrderReasons().test();

    // Результат:
    testSubscriber.assertComplete();
    testSubscriber.assertNoValues();
    testSubscriber.assertNoErrors();
  }
}