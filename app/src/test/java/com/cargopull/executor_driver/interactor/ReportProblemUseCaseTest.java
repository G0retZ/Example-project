package com.cargopull.executor_driver.interactor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.cargopull.executor_driver.UseCaseThreadTestRule;
import com.cargopull.executor_driver.backend.web.NoNetworkException;
import com.cargopull.executor_driver.entity.Problem;
import com.cargopull.executor_driver.gateway.DataMappingException;
import io.reactivex.Completable;
import io.reactivex.Single;
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
public class ReportProblemUseCaseTest {

  @ClassRule
  public static final UseCaseThreadTestRule classRule = new UseCaseThreadTestRule();

  private ReportProblemUseCase useCase;

  @Mock
  private ReportProblemGateway gateway;
  @Mock
  private Problem problem;
  @Mock
  private Problem problem1;
  @Mock
  private Problem problem2;
  @Mock
  private Problem problem3;

  @Before
  public void setUp() {
    when(gateway.getProblems()).thenReturn(Single.never());
    when(gateway.reportProblem(any())).thenReturn(Completable.never());
    useCase = new ReportProblemUseCaseImpl(gateway);
  }

  /* Проверяем работу с гейтвеем */

  /**
   * Должен запросить у гейтвея список причин для отказа только раз.
   */
  @Test
  public void askGatewayForReportProblems() {
    // Действие:
    useCase.reportProblem(problem).test().isDisposed();
    useCase.reportProblem(problem1).test().isDisposed();
    useCase.reportProblem(problem2).test().isDisposed();

    // Результат:
    verify(gateway, only()).getProblems();
  }

  /**
   * Не должен просить гейтвей отправить проблему без причин отказа.
   */
  @Test
  public void doNotAskGatewayToReportProblemWithoutReportProblems() {
    // Действие:
    useCase.reportProblem(problem).test().isDisposed();
    useCase.reportProblem(problem1).test().isDisposed();
    useCase.reportProblem(problem2).test().isDisposed();

    // Результат:
    verify(gateway, never()).reportProblem(any(Problem.class));
  }

  /**
   * Не должен просить гейтвей отправить проблему, если выбор неверный.
   */
  @Test
  public void doNotAskGatewayToReportProblemIfSelectionInvalid() {
    // Дано:
    when(gateway.getProblems()).thenReturn(Single.just(
        new ArrayList<>(Arrays.asList(problem, problem2, problem3))
    ));

    // Действие:
    useCase.reportProblem(problem1).test().isDisposed();

    // Результат:
    verify(gateway, never()).reportProblem(any(Problem.class));
  }

  /**
   * Должен просить гейтвей отправить указанную проблему.
   */
  @Test
  public void askGatewayToCancelOrderWithSelectedReason() {
    // Дано:
    when(gateway.getProblems()).thenReturn(Single.just(
        new ArrayList<>(Arrays.asList(problem, problem1, problem3))
    ));

    // Действие:
    useCase.reportProblem(problem1).test().isDisposed();

    // Результат:
    verify(gateway).getProblems();
    verify(gateway).reportProblem(problem1);
    verifyNoMoreInteractions(gateway);
  }

  /* Проверяем ответы */

  /**
   * Должен вернуть ошибку получения проблем.
   */
  @Test
  public void answerWithErrorIfGetSelectedReasonsError() {
    when(gateway.getProblems()).thenReturn(Single.error(DataMappingException::new));

    // Действие:
    TestObserver<Void> testObserver = useCase.reportProblem(problem2).test();

    // Результат:
    testObserver.assertError(DataMappingException.class);
    testObserver.assertNoValues();
  }

  /**
   * Должен ответить ошибкой, если выбраной проблемы нет в списке.
   */
  @Test
  public void answerOutOfBoundsError() {
    when(gateway.getProblems()).thenReturn(Single.just(
        Arrays.asList(problem, problem1, problem3)
    ));

    // Действие:
    TestObserver<Void> testObserver = useCase.reportProblem(problem2).test();

    // Результат:
    testObserver.assertError(IndexOutOfBoundsException.class);
  }

  /**
   * Должен вернуть ошибку, если отправка проблемы обломалась.
   */
  @Test
  public void answerWithErrorIfReportProblemFailed() {
    when(gateway.getProblems()).thenReturn(Single.just(
        Arrays.asList(problem, problem1, problem2, problem3)
    ));
    when(gateway.reportProblem(any())).thenReturn(Completable.error(NoNetworkException::new));

    // Действие:
    TestObserver<Void> testObserver = useCase.reportProblem(problem2).test();

    // Результат:
    testObserver.assertError(NoNetworkException.class);
    testObserver.assertNoValues();
  }

  /**
   * Должен ответить успехом.
   */
  @Test
  public void answerReportProblemSuccess() {
    when(gateway.getProblems()).thenReturn(Single.just(
        Arrays.asList(problem, problem1, problem2, problem3)
    ));
    when(gateway.reportProblem(any())).thenReturn(Completable.complete());

    // Действие:
    TestObserver<Void> testObserver2 = useCase.reportProblem(problem2).test();
    TestObserver<Void> testObserver = useCase.reportProblem(problem).test();

    // Результат:
    testObserver2.assertComplete();
    testObserver.assertComplete();
  }
}