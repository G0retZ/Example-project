package com.cargopull.executor_driver.presentation.reportproblem;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;

import com.cargopull.executor_driver.ViewModelThreadTestRule;
import com.cargopull.executor_driver.backend.analytics.ErrorReporter;
import com.cargopull.executor_driver.backend.web.AuthorizationException;
import com.cargopull.executor_driver.backend.web.NoNetworkException;
import com.cargopull.executor_driver.entity.Problem;
import com.cargopull.executor_driver.gateway.DataMappingException;
import com.cargopull.executor_driver.interactor.ReportProblemUseCase;
import com.cargopull.executor_driver.presentation.CommonNavigate;
import com.cargopull.executor_driver.presentation.ViewState;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.subjects.SingleSubject;

@RunWith(MockitoJUnitRunner.class)
public class ReportProblemViewModelTest {

  @ClassRule
  public static final ViewModelThreadTestRule classRule = new ViewModelThreadTestRule();
  @Rule
  public TestRule rule = new InstantTaskExecutorRule();
  private ReportProblemViewModel viewModel;
  @Mock
  private ErrorReporter errorReporter;
  @Mock
  private ReportProblemUseCase useCase;
  @Mock
  private Problem problem;
  @Mock
  private Problem problem1;
  @Mock
  private Problem problem2;
  @Mock
  private Observer<String> navigateObserver;
  @Mock
  private Observer<ViewState<ReportProblemViewActions>> viewStateObserver;

  private SingleSubject<List<Problem>> singleSubject;

  @Before
  public void setUp() {
    singleSubject = SingleSubject.create();
    when(useCase.getAvailableProblems()).thenReturn(singleSubject);
    when(useCase.reportProblem(any())).thenReturn(Completable.never());
    viewModel = new ReportProblemViewModelImpl(errorReporter, useCase);
  }

  /* Проверяем отправку ошибок в репортер */

  /**
   * Должен отправить ошибку получения причин отказа.
   */
  @Test
  public void reportGetSelectedProblemError() {
    when(useCase.reportProblem(problem1))
        .thenReturn(Completable.error(DataMappingException::new));

    // Action:
    viewModel.selectItem(problem1);

    // Effect:
    verify(errorReporter, only()).reportError(any(DataMappingException.class));
  }

  /**
   * Должен отправить ошибку, если выбраной причины нет в списке.
   */
  @Test
  public void reportOutOfBoundsError() {
    when(useCase.reportProblem(problem2))
        .thenReturn(Completable.error(IndexOutOfBoundsException::new));

    // Action:
    viewModel.selectItem(problem2);

    // Effect:
    verify(errorReporter, only()).reportError(any(IndexOutOfBoundsException.class));
  }

  /* Тетсируем работу с юзкейсом. */

  /**
   * Должен просить юзкейс получать список проблем только при создании.
   */
  @Test
  public void askUseCaseForReportProblemsInitially() {
    // Effect:
    verify(useCase, only()).getAvailableProblems();
  }

  /**
   * Не должен более трогать юзкейс на подписках.
   */
  @Test
  public void doNotTouchUseCaseOnSubscriptions() {
    // Action:
    viewModel.getViewStateLiveData();
    viewModel.getNavigationLiveData();
    viewModel.getViewStateLiveData();
    viewModel.getNavigationLiveData();

    // Effect:
    verify(useCase, only()).getAvailableProblems();
  }

  /**
   * Должен попросить юзкейс сообщить о проблеме.
   */
  @Test
  public void askUseCaseToReportProblem() {
    // Action:
    viewModel.selectItem(problem1);

    // Effect:
    verify(useCase).getAvailableProblems();
    verify(useCase).reportProblem(problem1);
    verifyNoMoreInteractions(useCase);
  }

  /**
   * Не должен более трогать юзкейс, если предыдущий запрос отказа от заказа еще не завершился.
   */
  @Test
  public void DoNotTouchUseCaseDuringReportProblem() {
    // Given:
    viewModel.selectItem(problem);
    viewModel.selectItem(problem1);
    viewModel.selectItem(problem2);

    // Effect:
    verify(useCase).getAvailableProblems();
    verify(useCase).reportProblem(problem);
    verifyNoMoreInteractions(useCase);
  }

  /* Тетсируем переключение состояний. */


  /**
   * Не должен давать иных состояний вида если была ошибка.
   */
  @Test
  public void doNotSetAnyViewStateToLiveDataForError() {
    // Given:
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    singleSubject.onError(new Exception());

    // Effect:
    verify(viewStateObserver, only()).onChanged(new ReportProblemViewStatePending(null));
  }

  /**
   * Должен вернуть состояние вида "списка проблем".
   */
  @Test
  public void setReportProblemViewStateToLiveData() {
    // Given:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    singleSubject.onSuccess(Arrays.asList(problem, problem1, problem2));

    // Effect:
    inOrder.verify(viewStateObserver).onChanged(new ReportProblemViewStatePending(null));
    inOrder.verify(viewStateObserver).onChanged(new ReportProblemViewState(
        Arrays.asList(problem, problem1, problem2)
    ));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида "В процессе".
   */
  @Test
  public void setPendingViewStateStateToLiveDataForReportProblem() {
    // Given:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    singleSubject.onSuccess(Arrays.asList(problem, problem1, problem2));
    viewModel.selectItem(problem1);

    // Effect:
    inOrder.verify(viewStateObserver).onChanged(new ReportProblemViewStatePending(null));
    inOrder.verify(viewStateObserver).onChanged(new ReportProblemViewState(
        Arrays.asList(problem, problem1, problem2)
    ));
    inOrder.verify(viewStateObserver).onChanged(new ReportProblemViewStatePending(
        new ReportProblemViewState(
            Arrays.asList(problem, problem1, problem2)
        )
    ));
  }

  /**
   * Должен вернуть состояние вида "не в процессе" при ошибке.
   */
  @Test
  public void setReportProblemViewStateToLiveDataAfterPendingForReportProblemError() {
    // Given:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(useCase.reportProblem(any())).thenReturn(Completable.error(Exception::new));
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    singleSubject.onSuccess(Arrays.asList(problem, problem1, problem2));
    viewModel.selectItem(problem1);

    // Effect:
    inOrder.verify(viewStateObserver).onChanged(new ReportProblemViewStatePending(null));
    inOrder.verify(viewStateObserver).onChanged(new ReportProblemViewState(
        Arrays.asList(problem, problem1, problem2)
    ));
    inOrder.verify(viewStateObserver).onChanged(new ReportProblemViewStatePending(
        new ReportProblemViewState(
            Arrays.asList(problem, problem1, problem2)
        )
    ));
    inOrder.verify(viewStateObserver).onChanged(new ReportProblemViewState(
        Arrays.asList(problem, problem1, problem2)
    ));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида "не в процессе" при успехе.
   */
  @Test
  public void setReportProblemViewStateToLiveDataAfterPendingForReportProblemSuccess() {
    // Given:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(useCase.reportProblem(any())).thenReturn(Completable.complete());
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    singleSubject.onSuccess(Arrays.asList(problem, problem1, problem2));
    viewModel.selectItem(problem1);

    // Effect:
    inOrder.verify(viewStateObserver).onChanged(new ReportProblemViewStatePending(null));
    inOrder.verify(viewStateObserver).onChanged(new ReportProblemViewState(
        Arrays.asList(problem, problem1, problem2)
    ));
    inOrder.verify(viewStateObserver).onChanged(new ReportProblemViewStatePending(
        new ReportProblemViewState(
            Arrays.asList(problem, problem1, problem2)
        )
    ));
    inOrder.verify(viewStateObserver).onChanged(new ReportProblemViewState(
        Arrays.asList(problem, problem1, problem2)
    ));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /* Тетсируем навигацию. */

  /**
   * Должен игнорировать данные от сервера.
   */
  @Test
  public void setNothingToLiveDataForReportProblems() {
    // Given:
    viewModel.getNavigationLiveData().observeForever(navigateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    singleSubject.onSuccess(Arrays.asList(problem, problem1, problem2));

    // Effect:
    verifyNoInteractions(navigateObserver);
  }

  /**
   * Не должен ничего вернуть.
   */
  @Test
  public void navigateToNothingForError() {
    // Given:
    viewModel.getNavigationLiveData().observeForever(navigateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    singleSubject.onError(new NoNetworkException());

    // Effect:
    verifyNoInteractions(navigateObserver);
  }

  /**
   * Не должен ничего вернуть.
   */
  @Test
  public void navigateToNothingForAuthorize() {
    // Given:
    viewModel.getNavigationLiveData().observeForever(navigateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    singleSubject.onError(new AuthorizationException());

    // Effect:
    verifyNoInteractions(navigateObserver);
  }

  /**
   * Должен вернуть "перейти к ошибке данных сервера".
   */
  @Test
  public void setNavigateToServerDataError() {
    // Given:
    viewModel.getNavigationLiveData().observeForever(navigateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    singleSubject.onError(new DataMappingException());

    // Effect:
    verify(navigateObserver, only()).onChanged(CommonNavigate.SERVER_DATA_ERROR);
  }

  /**
   * Не игнорировать другие ошибки.
   */
  @Test
  public void setNothingToLiveDataForOtherError() {
    // Given:
    when(useCase.reportProblem(any())).thenReturn(Completable.error(new DataMappingException()));
    viewModel.getNavigationLiveData().observeForever(navigateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    viewModel.selectItem(problem);

    // Effect:
    verifyNoInteractions(navigateObserver);
  }

  /**
   * Должен игнорировать неуспешные выборы.
   */
  @Test
  public void setNothingToLiveDataForWrongChoice() {
    // Given:
    when(useCase.reportProblem(any()))
        .thenReturn(Completable.error(new IndexOutOfBoundsException()));
    viewModel.getNavigationLiveData().observeForever(navigateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    viewModel.selectItem(problem);

    // Effect:
    verifyNoInteractions(navigateObserver);
  }

  /**
   * Должен вернуть "перейти к ошибке сети" если была ошибка сети.
   */
  @Test
  public void setNoConnectionToLiveData() {
    // Given:
    when(useCase.reportProblem(any())).thenReturn(Completable.error(new IllegalStateException()));
    viewModel.getNavigationLiveData().observeForever(navigateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    viewModel.selectItem(problem);

    // Effect:
    verify(navigateObserver, only()).onChanged(CommonNavigate.NO_CONNECTION);
  }

  /**
   * Должен вернуть "перейти к заказ отменен" если выбор был успешным.
   */
  @Test
  public void setNavigateToOrderCanceledToLiveData() {
    // Given:
    when(useCase.reportProblem(any())).thenReturn(Completable.complete());
    viewModel.getNavigationLiveData().observeForever(navigateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    viewModel.selectItem(problem);

    // Effect:
    verify(navigateObserver, only()).onChanged(ReportProblemNavigate.ORDER_CANCELED);
  }
}