package com.cargopull.executor_driver.interactor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.cargopull.executor_driver.GatewayThreadTestRule;
import com.cargopull.executor_driver.backend.web.ApiService;
import com.cargopull.executor_driver.backend.web.incoming.ApiProblem;
import com.cargopull.executor_driver.entity.Problem;
import com.cargopull.executor_driver.gateway.DataMappingException;
import com.cargopull.executor_driver.gateway.Mapper;
import com.cargopull.executor_driver.gateway.ReportProblemGatewayImpl;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.observers.TestObserver;

@RunWith(MockitoJUnitRunner.class)
public class ReportProblemGatewayTest {

  @ClassRule
  public static final GatewayThreadTestRule classRule = new GatewayThreadTestRule();

  private ReportProblemGateway gateway;

  @Mock
  private ApiService apiService;
  @Mock
  private Mapper<ApiProblem, Problem> mapper;
  @Mock
  private ApiProblem apiProblem;
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
    gateway = new ReportProblemGatewayImpl(apiService, mapper);
    when(apiService.getReportProblems()).thenReturn(Single.never());
    when(apiService.reportProblem(any(ApiProblem.class))).thenReturn(Completable.never());
  }

  /* Проверяем работу с API сервисами */

  /**
   * Должен запросить у сервисов получить список проблем.
   */
  @Test
  public void askApiServicesForReportProblems() {
    // Action:
    gateway.getProblems().test().isDisposed();

    // Effect:
    verify(apiService, only()).getReportProblems();
  }

  /**
   * Должен запросить у сервисов сообщить о проблеме.
   */
  @Test
  public void askApiServicesToReportProblem() {
    // Given:
    when(problem.getId()).thenReturn(7);
    when(problem.getName()).thenReturn("seven");

    // Action:
    gateway.reportProblem(problem).test().isDisposed();

    // Effect:
    verify(apiService, only()).reportProblem(new ApiProblem(7, "seven", null));
  }

  /* Проверяем работу с маппером */

  /**
   * Должен запросить маппинг.
   *
   * @throws Exception error
   */
  @Test
  public void askMapperForForDataMapping() throws Exception {
    // Given:
    when(mapper.map(apiProblem)).thenReturn(problem);
    when(apiService.getReportProblems()).thenReturn(Single.just(
        Arrays.asList(apiProblem, apiProblem, apiProblem, apiProblem)
    ));

    // Action:
    gateway.getProblems().test().isDisposed();

    // Effect:
    verify(mapper, times(4)).map(apiProblem);
    verifyNoMoreInteractions(mapper);
  }

  /* Проверяем результаты обработки сообщений от сервера */

  /**
   * Должен ответить ошибкой маппинга.
   *
   * @throws Exception error
   */
  @Test
  public void answerWithDataMappingError() throws Exception {
    // Given:
    doThrow(new DataMappingException()).when(mapper).map(apiProblem);
    when(apiService.getReportProblems()).thenReturn(Single.just(
        Arrays.asList(apiProblem, apiProblem, apiProblem, apiProblem)
    ));

    // Action:
    TestObserver<List<Problem>> listTestObserver = gateway.getProblems().test();

    // Effect:
    listTestObserver.assertError(DataMappingException.class);
    listTestObserver.assertNoValues();
  }

  /**
   * Должен вернуть данные.
   *
   * @throws Exception error
   */
  @Test
  public void answerWithData() throws Exception {
    // Given:
    when(mapper.map(apiProblem)).thenReturn(problem, problem1, problem2, problem3);
    when(apiService.getReportProblems()).thenReturn(Single.just(
        Arrays.asList(apiProblem, apiProblem, apiProblem, apiProblem)
    ));

    // Action:
    TestObserver<List<Problem>> listTestObserver = gateway.getProblems().test();

    // Effect:
    listTestObserver.assertNoErrors();
    listTestObserver.assertValue(new ArrayList<>(
        Arrays.asList(problem, problem1, problem2, problem3)
    ));
  }

  /**
   * Должен ответить успехом.
   */
  @Test
  public void answerReportProblemSuccess() {
    // Given:
    when(problem.getId()).thenReturn(7);
    when(problem.getName()).thenReturn("seven");
    when(apiService.reportProblem(any(ApiProblem.class))).thenReturn(Completable.complete());

    // Action:
    TestObserver<Void> testObserver = gateway.reportProblem(problem).test();

    // Effect:
    testObserver.assertNoErrors();
    testObserver.assertComplete();
  }

  /**
   * Должен ответить ошибкой.
   */
  @Test
  public void answerReportProblemFail() {
    // Given:
    when(problem.getId()).thenReturn(7);
    when(problem.getName()).thenReturn("seven");
    when(apiService.reportProblem(any(ApiProblem.class)))
        .thenReturn(Completable.error(new IllegalArgumentException()));

    // Action:
    TestObserver<Void> testObserver = gateway.reportProblem(problem).test();

    // Effect:
    testObserver.assertNotComplete();
    testObserver.assertError(IllegalArgumentException.class);
  }
}