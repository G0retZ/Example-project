package com.cargopull.executor_driver.interactor;

import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.cargopull.executor_driver.UseCaseThreadTestRule;
import com.cargopull.executor_driver.gateway.DataMappingException;
import com.cargopull.executor_driver.utils.TimeUtils;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import io.reactivex.Flowable;
import io.reactivex.observers.TestObserver;

@RunWith(MockitoJUnitRunner.class)
public class ServerTimeUseCaseTest {

  @ClassRule
  public static final UseCaseThreadTestRule classRule = new UseCaseThreadTestRule();

  private ServerTimeUseCase useCase;

  @Mock
  private CommonGateway<Long> gateway;
  @Mock
  private TimeUtils timeUtils;

  @Before
  public void setUp() {
    when(gateway.getData()).thenReturn(Flowable.never());
    useCase = new ServerTimeUseCaseImpl(gateway, timeUtils);
  }

  /* Проверяем работу с гейтвеем */

  /**
   * Должен запросить у гейтвея текущие временные метки сервера.
   */
  @Test
  public void askGatewayForServerTime() {
    // Action:
    useCase.getServerTime().test().isDisposed();

    // Effect:
    verify(gateway, only()).getData();
  }

  /* Проверяем работу с временем */

  /**
   * Не должен задавать текущее время сервера.
   */
  @Test
  public void doNotSetServerTimeIfNoData() {
    // Action:
    useCase.getServerTime().test().isDisposed();

    // Effect:
    verifyNoInteractions(timeUtils);
  }

  /**
   * Не должен запрпрашивать у гейтвея текущие временные метки сервера.
   */
  @Test
  public void doNotSetServerTimeIfError() {
    // Given:
    when(gateway.getData()).thenReturn(Flowable.error(DataMappingException::new));

    // Action:
    useCase.getServerTime().test().isDisposed();

    // Effect:
    verifyNoInteractions(timeUtils);
  }

  /**
   * Не должен задавать текущее время сервера.
   */
  @Test
  public void doNotSetServerTime() {
    // Given:
    InOrder inOrder = Mockito.inOrder(timeUtils);
    when(gateway.getData()).thenReturn(Flowable.just(1L, 2L, 3L));

    // Action:
    useCase.getServerTime().test().isDisposed();

    // Effect:
    inOrder.verify(timeUtils).setServerCurrentTime(1L);
    inOrder.verify(timeUtils).setServerCurrentTime(2L);
    inOrder.verify(timeUtils).setServerCurrentTime(3L);
    verifyNoMoreInteractions(timeUtils);
  }

  /* Проверяем ответы */

  /**
   * Должен вернуть текущие временные метки сервера.
   */
  @Test
  public void answerWithServerTimes() {
    // Given:
    when(gateway.getData()).thenReturn(Flowable.just(1L, 2L, 3L));

    // Action:
    TestObserver<Void> testObserver = useCase.getServerTime().test();

    // Effect:
    testObserver.assertNoErrors();
    testObserver.assertComplete();
  }

  /**
   * Должен вернуть ошибку.
   */
  @Test
  public void answerWithError() {
    // Given:
    when(gateway.getData()).thenReturn(Flowable.error(DataMappingException::new));

    // Action:
    TestObserver<Void> testObserver = useCase.getServerTime().test();

    // Effect:
    testObserver.assertError(DataMappingException.class);
    testObserver.assertNotComplete();
  }

  /**
   * Должен завершить получение текущих временных меток сервера.
   */
  @Test
  public void answerComplete() {
    // Given:
    when(gateway.getData()).thenReturn(Flowable.empty());

    // Action:
    TestObserver<Void> testObserver = useCase.getServerTime().test();

    // Effect:
    testObserver.assertComplete();
    testObserver.assertNoErrors();
  }
}