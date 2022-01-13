package com.cargopull.executor_driver.presentation.reportproblem;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import com.cargopull.executor_driver.entity.Problem;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;

@RunWith(MockitoJUnitRunner.class)
public class ReportProblemViewStateTest {

  private ReportProblemViewState viewState;

  @Mock
  private ReportProblemViewActions viewActions;
  @Mock
  private Problem routePointItems;
  @Mock
  private Problem routePointItems1;

  @Test
  public void testActions() {
    // Given:
    viewState = new ReportProblemViewState(Collections.singletonList(routePointItems));

    // Action:
    viewState.apply(viewActions);

    // Effect:
    verify(viewActions).setAvailableProblems(Collections.singletonList(routePointItems));
    verify(viewActions).showAvailableProblems(true);
    verify(viewActions).showReportProblemPending(false);
    verifyNoMoreInteractions(viewActions);
  }

  @Test
  public void testEquals() {
    viewState = new ReportProblemViewState(Collections.singletonList(routePointItems));
    assertEquals(viewState, new ReportProblemViewState(Collections.singletonList(routePointItems)));
    assertNotEquals(viewState,
        new ReportProblemViewState(Collections.singletonList(routePointItems1)));
  }
}