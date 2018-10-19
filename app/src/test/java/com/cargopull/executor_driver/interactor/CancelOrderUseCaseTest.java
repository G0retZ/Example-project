package com.cargopull.executor_driver.interactor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import com.cargopull.executor_driver.UseCaseThreadTestRule;
import com.cargopull.executor_driver.backend.web.NoNetworkException;
import com.cargopull.executor_driver.entity.CancelOrderReason;
import com.cargopull.executor_driver.gateway.DataMappingException;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.observers.TestObserver;
import java.util.ArrayList;
import java.util.Arrays;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CancelOrderUseCaseTest {

  @ClassRule
  public static final UseCaseThreadTestRule classRule = new UseCaseThreadTestRule();

  private CancelOrderUseCase useCase;

  @Mock
  private CancelOrderReasonsUseCase cancelOrderReasonsUseCase;
  @Mock
  private CancelOrderGateway gateway;
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
    when(cancelOrderReasonsUseCase.getCancelOrderReasons()).thenReturn(Flowable.never());
    when(gateway.cancelOrder(any())).thenReturn(Completable.never());
    useCase = new CancelOrderUseCaseImpl(cancelOrderReasonsUseCase, gateway);
  }

  /* Проверяем работу с юзейсом причин отказа */

  /**
   * Должен запросить у юзкейса список причин для отказа.
   */
  @Test
  public void askCancelOrderReasonsUseCaseForCancelReasons() {
    // Действие:
    useCase.cancelOrder(cancelOrderReason).test().isDisposed();
    useCase.cancelOrder(cancelOrderReason1).test().isDisposed();
    useCase.cancelOrder(cancelOrderReason2).test().isDisposed();

    // Результат:
    verify(cancelOrderReasonsUseCase, times(3)).getCancelOrderReasons();
    verifyNoMoreInteractions(gateway);
  }

  /* Проверяем работу с гейтвеем */

  /**
   * Не должен трогать гейтвей без ответа от юзейса причин отказа.
   */
  @Test
  public void doNotTouchGatewayWithoutCancelReasons() {
    // Действие:
    useCase.cancelOrder(cancelOrderReason).test().isDisposed();
    useCase.cancelOrder(cancelOrderReason1).test().isDisposed();
    useCase.cancelOrder(cancelOrderReason2).test().isDisposed();

    // Результат:
    verifyZeroInteractions(gateway);
  }

  /**
   * Не должен трогать гейтвей на отправку, если выбор неверный.
   */
  @Test
  public void doNotAskGatewayToCancelIfSelectionInvalid() {
    // Дано:
    when(cancelOrderReasonsUseCase.getCancelOrderReasons()).thenReturn(Flowable.just(
        new ArrayList<>(Arrays.asList(cancelOrderReason, cancelOrderReason2, cancelOrderReason3))
    ));

    // Действие:
    useCase.cancelOrder(cancelOrderReason1).test().isDisposed();

    // Результат:
    verifyZeroInteractions(gateway);
  }

  /**
   * Должен запросить отмену заказа с указанной причиной.
   */
  @Test
  public void askGatewayToCancelOrderWithSelectedReason() {
    // Дано:
    when(cancelOrderReasonsUseCase.getCancelOrderReasons()).thenReturn(Flowable.just(
        new ArrayList<>(Arrays.asList(cancelOrderReason, cancelOrderReason1, cancelOrderReason3))
    ));

    // Действие:
    useCase.cancelOrder(cancelOrderReason1).test().isDisposed();

    // Результат:
    verify(gateway, only()).cancelOrder(cancelOrderReason1);
  }

  /* Проверяем ответы */

  /**
   * Должен вернуть ошибку получения причин отказа.
   */
  @Test
  public void answerWithErrorIfGetSelectedReasonsError() {
    when(cancelOrderReasonsUseCase.getCancelOrderReasons())
        .thenReturn(Flowable.error(DataMappingException::new));

    // Действие:
    TestObserver<Void> testObserver = useCase.cancelOrder(cancelOrderReason2).test();

    // Результат:
    testObserver.assertError(DataMappingException.class);
    testObserver.assertNoValues();
  }

  /**
   * Должен ответить ошибкой, если выбраной причины нет в списке.
   */
  @Test
  public void answerOutOfBoundsError() {
    when(cancelOrderReasonsUseCase.getCancelOrderReasons()).thenReturn(Flowable.just(
        Arrays.asList(cancelOrderReason, cancelOrderReason1, cancelOrderReason3)
    ));

    // Действие:
    TestObserver<Void> testObserver = useCase.cancelOrder(cancelOrderReason2).test();

    // Результат:
    testObserver.assertError(IndexOutOfBoundsException.class);
  }

  /**
   * Должен вернуть ошибку, если отправка отмены заказа обломалась.
   */
  @Test
  public void answerWithErrorIfCancelOrderFailed() {
    when(cancelOrderReasonsUseCase.getCancelOrderReasons()).thenReturn(Flowable.just(
        Arrays.asList(cancelOrderReason, cancelOrderReason1, cancelOrderReason2, cancelOrderReason3)
    ));
    when(gateway.cancelOrder(any())).thenReturn(Completable.error(NoNetworkException::new));

    // Действие:
    TestObserver<Void> testObserver = useCase.cancelOrder(cancelOrderReason2).test();

    // Результат:
    testObserver.assertError(NoNetworkException.class);
    testObserver.assertNoValues();
  }

  /**
   * Должен ответить успехом.
   */
  @Test
  public void answerCancelOrderSuccess() {
    when(cancelOrderReasonsUseCase.getCancelOrderReasons()).thenReturn(Flowable.just(
        Arrays.asList(cancelOrderReason, cancelOrderReason1, cancelOrderReason2, cancelOrderReason3)
    ));
    when(gateway.cancelOrder(any())).thenReturn(Completable.complete());

    // Действие:
    TestObserver<Void> testObserver2 = useCase.cancelOrder(cancelOrderReason2).test();
    TestObserver<Void> testObserver = useCase.cancelOrder(cancelOrderReason).test();

    // Результат:
    testObserver2.assertComplete();
    testObserver.assertComplete();
  }
}