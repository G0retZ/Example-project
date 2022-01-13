package com.cargopull.executor_driver.interactor;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.cargopull.executor_driver.UseCaseThreadTestRule;
import com.cargopull.executor_driver.gateway.DataMappingException;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import io.reactivex.Flowable;
import io.reactivex.subscribers.TestSubscriber;

@RunWith(MockitoJUnitRunner.class)
public class UpdateMessageUseCaseTest {

  @ClassRule
  public static final UseCaseThreadTestRule classRule = new UseCaseThreadTestRule();

  private UpdateMessageUseCase useCase;

  @Mock
  private CommonGateway<String> gateway;

  @Before
  public void setUp() {
    when(gateway.getData()).thenReturn(Flowable.never());
    useCase = new UpdateMessageUseCaseImpl(gateway);
  }

  /* Проверяем работу с гейтвеем */

  /**
   * Должен запросить у гейтвея сообщения о новой версии.
   */
  @Test
  public void askGatewayForUpdateMessages() {
    // Action:
    useCase.getUpdateMessages().test().isDisposed();
    useCase.getUpdateMessages().test().isDisposed();
    useCase.getUpdateMessages().test().isDisposed();
    useCase.getUpdateMessages().test().isDisposed();

    // Effect:
    verify(gateway, times(4)).getData();
    verifyNoMoreInteractions(gateway);
  }

  /* Проверяем ответы */

  /**
   * Должен вернуть сообщения о новой версии.
   */
  @Test
  public void answerWithUpdateMessages() {
    // Given:
    when(gateway.getData()).thenReturn(Flowable.just("1", "2", "3"));

    // Action:
    TestSubscriber<String> testSubscriber = useCase.getUpdateMessages().test();

    // Effect:
    testSubscriber.assertValues("1", "2", "3");
    testSubscriber.assertNoErrors();
  }

  /**
   * Должен вернуть ошибку.
   */
  @Test
  public void answerWithError() {
    // Given:
    when(gateway.getData()).thenReturn(Flowable.error(DataMappingException::new));

    // Action:
    TestSubscriber<String> testSubscriber = useCase.getUpdateMessages().test();

    // Effect:
    testSubscriber.assertError(DataMappingException.class);
    testSubscriber.assertNotComplete();
    testSubscriber.assertNoValues();
  }

  /**
   * Должен завершить получение сообщений о новой версии.
   */
  @Test
  public void answerComplete() {
    // Given:
    when(gateway.getData()).thenReturn(Flowable.empty());

    // Action:
    TestSubscriber<String> testSubscriber = useCase.getUpdateMessages().test();

    // Effect:
    testSubscriber.assertComplete();
    testSubscriber.assertNoValues();
    testSubscriber.assertNoErrors();
  }
}