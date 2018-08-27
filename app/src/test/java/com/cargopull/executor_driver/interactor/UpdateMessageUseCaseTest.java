package com.cargopull.executor_driver.interactor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.cargopull.executor_driver.UseCaseThreadTestRule;
import com.cargopull.executor_driver.gateway.DataMappingException;
import com.cargopull.executor_driver.utils.ErrorReporter;
import io.reactivex.Flowable;
import io.reactivex.subscribers.TestSubscriber;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class UpdateMessageUseCaseTest {

  @ClassRule
  public static final UseCaseThreadTestRule classRule = new UseCaseThreadTestRule();

  private UpdateMessageUseCase useCase;

  @Mock
  private ErrorReporter errorReporter;
  @Mock
  private UpdateMessageGateway gateway;

  @Before
  public void setUp() {
    when(gateway.loadUpdateMessages()).thenReturn(Flowable.never());
    useCase = new UpdateMessageUseCaseImpl(errorReporter, gateway);
  }

  /* Проверяем работу с гейтвеем */

  /**
   * Должен запросить у гейтвея сообщения о новой версии.
   */
  @Test
  public void askGatewayForUpdateMessages() {
    // Действие:
    useCase.getUpdateMessages().test();
    useCase.getUpdateMessages().test();
    useCase.getUpdateMessages().test();
    useCase.getUpdateMessages().test();

    // Результат:
    verify(gateway, times(4)).loadUpdateMessages();
    verifyNoMoreInteractions(gateway);
  }

  /* Проверяем отправку ошибок в репортер */

  /**
   * Должен отправить ошибку.
   */
  @Test
  public void reportError() {
    // Дано:
    when(gateway.loadUpdateMessages()).thenReturn(Flowable.error(DataMappingException::new));

    // Действие:
    useCase.getUpdateMessages().test();

    // Результат:
    verify(errorReporter, only()).reportError(any(DataMappingException.class));
  }

  /* Проверяем ответы */

  /**
   * Должен вернуть сообщения о новой версии.
   */
  @Test
  public void answerWithUpdateMessages() {
    // Дано:
    when(gateway.loadUpdateMessages()).thenReturn(Flowable.just("1", "2", "3"));

    // Действие:
    TestSubscriber<String> testSubscriber = useCase.getUpdateMessages().test();

    // Результат:
    testSubscriber.assertValues("1", "2", "3");
    testSubscriber.assertNoErrors();
  }

  /**
   * Должен вернуть ошибку.
   */
  @Test
  public void answerWithError() {
    // Дано:
    when(gateway.loadUpdateMessages()).thenReturn(Flowable.error(DataMappingException::new));

    // Действие:
    TestSubscriber<String> testSubscriber = useCase.getUpdateMessages().test();

    // Результат:
    testSubscriber.assertError(DataMappingException.class);
    testSubscriber.assertNotComplete();
    testSubscriber.assertNoValues();
  }

  /**
   * Должен завершить получение сообщений о новой версии.
   */
  @Test
  public void answerComplete() {
    // Дано:
    when(gateway.loadUpdateMessages()).thenReturn(Flowable.empty());

    // Действие:
    TestSubscriber<String> testSubscriber = useCase.getUpdateMessages().test();

    // Результат:
    testSubscriber.assertComplete();
    testSubscriber.assertNoValues();
    testSubscriber.assertNoErrors();
  }
}