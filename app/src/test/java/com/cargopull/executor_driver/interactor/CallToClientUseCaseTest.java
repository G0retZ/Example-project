package com.cargopull.executor_driver.interactor;

import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cargopull.executor_driver.UseCaseThreadTestRule;
import com.cargopull.executor_driver.backend.web.NoNetworkException;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import io.reactivex.Completable;
import io.reactivex.observers.TestObserver;

@RunWith(MockitoJUnitRunner.class)
public class CallToClientUseCaseTest {

  @ClassRule
  public static final UseCaseThreadTestRule classRule = new UseCaseThreadTestRule();

  private CallToClientUseCase useCase;

  @Mock
  private CallToClientGateway gateway;

  @Before
  public void setUp() {
    when(gateway.callToClient()).thenReturn(Completable.never());
    useCase = new CallToClientUseCaseImpl(gateway);
  }

  /* Проверяем работу с гейтвеем */

  /**
   * Должен запросить у гейтвея звонок клиенту.
   */
  @Test
  public void askGatewayToToCallClientForOrder() {
    // Action:
    useCase.callToClient().test().isDisposed();

    // Effect:
    verify(gateway, only()).callToClient();
  }

  /* Проверяем ответы на запрос звонка клиенту */

  /**
   * Должен ответить ошибкой сети на запрос звонка клиенту.
   */
  @Test
  public void answerNoNetworkErrorForCallClient() {
    // Given:
    when(gateway.callToClient())
        .thenReturn(Completable.error(new NoNetworkException()));

    // Action:
    TestObserver<Void> test = useCase.callToClient().test();

    // Effect:
    test.assertError(NoNetworkException.class);
    test.assertNoValues();
    test.assertNotComplete();
  }

  /**
   * Должен ответить успехом запроса звонка клиенту.
   */
  @Test
  public void answerSendCallClientSuccessful() {
    // Given:
    when(gateway.callToClient()).thenReturn(Completable.complete());

    // Action:
    TestObserver<Void> test = useCase.callToClient().test();

    // Effect:
    test.assertComplete();
    test.assertNoErrors();
  }
}