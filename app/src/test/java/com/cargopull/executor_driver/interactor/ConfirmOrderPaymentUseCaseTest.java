package com.cargopull.executor_driver.interactor;

import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cargopull.executor_driver.UseCaseThreadTestRule;
import com.cargopull.executor_driver.backend.web.NoNetworkException;
import io.reactivex.Completable;
import io.reactivex.observers.TestObserver;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ConfirmOrderPaymentUseCaseTest {

  @ClassRule
  public static final UseCaseThreadTestRule classRule = new UseCaseThreadTestRule();

  private ConfirmOrderPaymentUseCase useCase;

  @Mock
  private ConfirmOrderPaymentGateway gateway;

  @Before
  public void setUp() {
    when(gateway.confirmOrderPayment()).thenReturn(Completable.never());
    useCase = new ConfirmOrderPaymentUseCaseImpl(gateway);
  }

  /* Проверяем работу с гейтвеем */

  /**
   * Должен запросить у гейтвея подтверждения оплаты заказа.
   */
  @Test
  public void askGatewayToToCallClientForOrder() {
    // Действие:
    useCase.confirmPayment().test();

    // Результат:
    verify(gateway, only()).confirmOrderPayment();
  }

  /* Проверяем ответы на запрос звонка клиенту */

  /**
   * Должен ответить ошибкой сети на запрос подтверждения оплаты заказа.
   */
  @Test
  public void answerNoNetworkErrorForCallClient() {
    // Дано:
    when(gateway.confirmOrderPayment())
        .thenReturn(Completable.error(new NoNetworkException()));

    // Действие:
    TestObserver<Void> test = useCase.confirmPayment().test();

    // Результат:
    test.assertError(NoNetworkException.class);
    test.assertNoValues();
    test.assertNotComplete();
  }

  /**
   * Должен ответить успехом запроса подтверждения оплаты заказа.
   */
  @Test
  public void answerSendCallClientSuccessful() {
    // Дано:
    when(gateway.confirmOrderPayment()).thenReturn(Completable.complete());

    // Действие:
    TestObserver<Void> test = useCase.confirmPayment().test();

    // Результат:
    test.assertComplete();
    test.assertNoErrors();
  }
}