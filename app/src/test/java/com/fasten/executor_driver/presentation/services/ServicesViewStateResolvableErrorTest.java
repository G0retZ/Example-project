package com.fasten.executor_driver.presentation.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import com.fasten.executor_driver.entity.Service;
import java.util.ArrayList;
import java.util.Arrays;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ServicesViewStateResolvableErrorTest {

  private ServicesViewStateResolvableError viewState;

  @Mock
  private ServicesViewActions codeViewActions;

  @Before
  public void setUp() {
    viewState = new ServicesViewStateResolvableError(123, new ServicesViewStateReady(
        Arrays.asList(
            new ServicesListItem(new Service(0, "n1", 100, true)),
            new ServicesListItem(new Service(1, "n2", 10, false)),
            new ServicesListItem(new Service(2, "n3", 130, true))
        )
    ));
  }

  @Test
  public void testActions() {
    // Действие:
    viewState.apply(codeViewActions);

    // Результат:
    verify(codeViewActions).enableReadyButton(false);
    verify(codeViewActions).showServicesList(true);
    verify(codeViewActions).showServicesPending(false);
    verify(codeViewActions).showServicesListErrorMessage(false, 0);
    verify(codeViewActions).showServicesListResolvableErrorMessage(true, 123);
    verify(codeViewActions).setServicesListItems(Arrays.asList(
        new ServicesListItem(new Service(0, "n1", 100, true)),
        new ServicesListItem(new Service(1, "n2", 10, false)),
        new ServicesListItem(new Service(2, "n3", 130, true))
    ));
    verifyNoMoreInteractions(codeViewActions);
  }

  @Test
  public void testEquals() {
    assertEquals(viewState, new ServicesViewStateResolvableError(123, new ServicesViewState(
        Arrays.asList(
            new ServicesListItem(new Service(0, "n1", 100, true)),
            new ServicesListItem(new Service(1, "n2", 10, false)),
            new ServicesListItem(new Service(2, "n3", 130, true))
        )
    )));
    assertNotEquals(viewState, new ServicesViewStateResolvableError(0, new ServicesViewState(
        Arrays.asList(
            new ServicesListItem(new Service(0, "n1", 100, true)),
            new ServicesListItem(new Service(1, "n2", 10, false)),
            new ServicesListItem(new Service(2, "n3", 130, true))
        )
    )));
    assertNotEquals(viewState, new ServicesViewStateResolvableError(123, new ServicesViewState(
        new ArrayList<>()
    )));
    assertNotEquals(viewState, new ServicesViewStateResolvableError(123, new ServicesViewState(
        Arrays.asList(
            new ServicesListItem(new Service(0, "n1", 100, true)),
            new ServicesListItem(new Service(2, "n3", 130, true))
        )
    )));
    assertNotEquals(viewState, new ServicesViewStateResolvableError(123, new ServicesViewState(
        Arrays.asList(
            new ServicesListItem(new Service(0, "n1", 100, true)),
            new ServicesListItem(new Service(1, "n2", 10, false)),
            new ServicesListItem(new Service(3, "n3", 130, true))
        )
    )));
  }
}