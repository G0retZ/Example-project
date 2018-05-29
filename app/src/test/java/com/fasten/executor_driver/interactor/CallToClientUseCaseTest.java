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
public class CallToClientUseCaseTest {

  private CallToClientUseCase movingToClientUseCase;

  @Mock
  private CallToClientGateway callToClientGateway;

  @Before
  public void setUp() {
    when(callToClientGateway.callToClient()).thenReturn(Completable.never());
    movingToClientUseCase = new CallToClientUseCaseImpl(callToClientGateway);
  }

  /* Проверяем работу с гейтвеем */

  /**
   * Должен запросить у гейтвея звонок клиенту.
   */
  @Test
  public void askGatewayToToCallClientForOrder() {
    // Действие:
    movingToClientUseCase.callToClient().test();

    // Результат:
    verify(callToClientGateway, only()).callToClient();
  }

  /* Проверяем ответы на запрос звонка клиенту */

  /**
   * Должен ответить ошибкой сети на запрос звонка клиенту.
   */
  @Test
  public void answerNoNetworkErrorForCallClient() {
    // Дано:
    when(callToClientGateway.callToClient())
        .thenReturn(Completable.error(new NoNetworkException()));

    // Действие:
    TestObserver<Void> test = movingToClientUseCase.callToClient().test();

    // Результат:
    test.assertError(NoNetworkException.class);
    test.assertNoValues();
    test.assertNotComplete();
  }

  /**
   * Должен ответить успехом запроса звонка клиенту.
   */
  @Test
  public void answerSendCallClientSuccessful() {
    // Дано:
    when(callToClientGateway.callToClient()).thenReturn(Completable.complete());

    // Действие:
    TestObserver<Void> test = movingToClientUseCase.callToClient().test();

    // Результат:
    test.assertComplete();
    test.assertNoErrors();
  }
}