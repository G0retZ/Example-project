package com.fasten.executor_driver.interactor;

import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasten.executor_driver.backend.web.NoNetworkException;
import io.reactivex.Completable;
import io.reactivex.observers.TestObserver;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class WaitingForClientUseCaseTest {

  private WaitingForClientUseCase movingToClientUseCase;

  @Mock
  private WaitingForClientGateway movingToClientGateway;

  @Before
  public void setUp() {
    when(movingToClientGateway.startTheOrder()).thenReturn(Completable.never());
    movingToClientUseCase = new WaitingForClientUseCaseImpl(movingToClientGateway);
  }

  /* Проверяем работу с гейтвеем */

  /**
   * Должен сообщить гейтвею о начале погрузки.
   */
  @Test
  public void askGatewayToStartOrder() {
    // Действие:
    movingToClientUseCase.startTheOrder().test();

    // Результат:
    verify(movingToClientGateway, only()).startTheOrder();
  }

  /* Проверяем ответы на начало погрузки */

  /**
   * Должен ответить ошибкой сети на начала погрузки.
   */
  @Test
  public void answerNoNetworkErrorForStartOrder() {
    // Дано:
    when(movingToClientGateway.startTheOrder())
        .thenReturn(Completable.error(new NoNetworkException()));

    // Действие:
    TestObserver<Void> test = movingToClientUseCase.startTheOrder().test();

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
    when(movingToClientGateway.startTheOrder()).thenReturn(Completable.complete());

    // Действие:
    TestObserver<Void> test = movingToClientUseCase.startTheOrder().test();

    // Результат:
    test.assertComplete();
    test.assertNoErrors();
  }
}