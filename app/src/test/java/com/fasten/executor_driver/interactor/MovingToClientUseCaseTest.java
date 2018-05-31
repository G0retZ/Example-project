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
public class MovingToClientUseCaseTest {

  private MovingToClientUseCase movingToClientUseCase;

  @Mock
  private MovingToClientGateway movingToClientGateway;

  @Before
  public void setUp() {
    when(movingToClientGateway.reportArrival()).thenReturn(Completable.never());
    movingToClientUseCase = new MovingToClientUseCaseImpl(movingToClientGateway);
  }

  /* Проверяем работу с гейтвеем */

  /**
   * Должен сообщить гейтвею о прибытии к клиенту.
   */
  @Test
  public void askGatewayToReportArrivalForOrder() {
    // Действие:
    movingToClientUseCase.reportArrival().test();

    // Результат:
    verify(movingToClientGateway, only()).reportArrival();
  }

  /* Проверяем ответы на сообщение о прибытии к клиенту */

  /**
   * Должен ответить ошибкой сети на сообщение о прибытии к клиенту.
   */
  @Test
  public void answerNoNetworkErrorForReportArrival() {
    // Дано:
    when(movingToClientGateway.reportArrival())
        .thenReturn(Completable.error(new NoNetworkException()));

    // Действие:
    TestObserver<Void> test = movingToClientUseCase.reportArrival().test();

    // Результат:
    test.assertError(NoNetworkException.class);
    test.assertNoValues();
    test.assertNotComplete();
  }

  /**
   * Должен ответить успехом отправки сообщения о прибытии к клиенту.
   */
  @Test
  public void answerSendReportArrivalSuccessful() {
    // Дано:
    when(movingToClientGateway.reportArrival()).thenReturn(Completable.complete());

    // Действие:
    TestObserver<Void> test = movingToClientUseCase.reportArrival().test();

    // Результат:
    test.assertComplete();
    test.assertNoErrors();
  }
}