package com.cargopull.executor_driver.presentation.reportproblem;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import com.cargopull.executor_driver.presentation.ViewState;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ReportProblemViewStatePendingTest {

  private ReportProblemViewStatePending viewState;

  @Mock
  private ReportProblemViewActions viewActions;
  @Mock
  private ViewState<ReportProblemViewActions> parentViewState;
  @Mock
  private ViewState<ReportProblemViewActions> parentViewState1;

  @Test
  public void testActions() {
    // Given:
    viewState = new ReportProblemViewStatePending(parentViewState);

      // Action:
    viewState.apply(viewActions);

      // Effect:
    verify(viewActions).showReportProblemPending(true);
    verify(viewActions).showAvailableProblems(true);
    verify(parentViewState, only()).apply(viewActions);
    verifyNoMoreInteractions(viewActions);
  }

  @Test
  public void testActionsWithNull() {
      // Given:
    viewState = new ReportProblemViewStatePending(null);

      // Action:
    viewState.apply(viewActions);

      // Effect:
    verify(viewActions).showReportProblemPending(true);
    verify(viewActions).showAvailableProblems(false);
    verifyNoMoreInteractions(viewActions);
  }

  @Test
  public void testEquals() {
    viewState = new ReportProblemViewStatePending(parentViewState);
    assertEquals(viewState, new ReportProblemViewStatePending(parentViewState));
    assertNotEquals(viewState, new ReportProblemViewStatePending(parentViewState1));
    assertNotEquals(viewState, new ReportProblemViewStatePending(null));
  }
}