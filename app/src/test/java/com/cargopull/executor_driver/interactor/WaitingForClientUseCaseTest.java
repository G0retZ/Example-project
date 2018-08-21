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
public class WaitingForClientUseCaseTest {

  @ClassRule
  public static final UseCaseThreadTestRule classRule = new UseCaseThreadTestRule();

  private WaitingForClientUseCase useCase;

  @Mock
  private WaitingForClientGateway gateway;

  @Before
  public void setUp() {
    when(gateway.startTheOrder()).thenReturn(Completable.never());
    useCase = new WaitingForClientUseCaseImpl(gateway);
  }

  /* Проверяем работу с гейтвеем */

  /**
   * Должен сообщить гейтвею о начале погрузки.
   */
  @Test
  public void askGatewayToStartOrder() {
    // Действие:
    useCase.startTheOrder().test();

    // Результат:
    verify(gateway, only()).startTheOrder();
  }

  /* Проверяем ответы на начало погрузки */

  /**
   * Должен ответить ошибкой сети на начала погрузки.
   */
  @Test
  public void answerNoNetworkErrorForStartOrder() {
    // Дано:
    when(gateway.startTheOrder())
        .thenReturn(Completable.error(new NoNetworkException()));

    // Действие:
    TestObserver<Void> test = useCase.startTheOrder().test();

    // Результат:
    test.assertError(NoNetworkException.class);
    test.assertNoValues();
    test.assertNotComplete();
  }

  /**
   * Должен ответить успехом начала погрузки.
   */
  @Test
  public void answerSendStartOrderSuccessful() {
    // Дано:
    when(gateway.startTheOrder()).thenReturn(Completable.complete());

    // Действие:
    TestObserver<Void> test = useCase.startTheOrder().test();

    // Результат:
    test.assertComplete();
    test.assertNoErrors();
  }
}