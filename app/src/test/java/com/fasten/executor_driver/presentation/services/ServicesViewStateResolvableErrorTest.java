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
  private ServicesViewActions viewActions;

  @Before
  public void setUp() {
    viewState = new ServicesViewStateResolvableError(123, Arrays.asList(
        new ServicesListItem(new Service(0, "n1", 100, true)),
        new ServicesListItem(new Service(1, "n2", 10, false)),
        new ServicesListItem(new Service(2, "n3", 130, true))
    ));
  }

  @Test
  public void testActions() {
    // Действие:
    viewState.apply(viewActions);

    // Результат:
    verify(viewActions).enableReadyButton(false);
    verify(viewActions).showServicesList(true);
    verify(viewActions).showServicesPending(false);
    verify(viewActions).showServicesListErrorMessage(false, 0);
    verify(viewActions).showServicesListResolvableErrorMessage(true, 123);
    verify(viewActions).setServicesListItems(Arrays.asList(
        new ServicesListItem(new Service(0, "n1", 100, true)),
        new ServicesListItem(new Service(1, "n2", 10, false)),
        new ServicesListItem(new Service(2, "n3", 130, true))
    ));
    verifyNoMoreInteractions(viewActions);
  }

  @Test
  public void testEquals() {
    assertEquals(viewState, new ServicesViewStateResolvableError(123, Arrays.asList(
        new ServicesListItem(new Service(0, "n1", 100, true)),
        new ServicesListItem(new Service(1, "n2", 10, false)),
        new ServicesListItem(new Service(2, "n3", 130, true))
    )));
    assertNotEquals(viewState, new ServicesViewStateResolvableError(0, Arrays.asList(
        new ServicesListItem(new Service(0, "n1", 100, true)),
        new ServicesListItem(new Service(1, "n2", 10, false)),
        new ServicesListItem(new Service(2, "n3", 130, true))
    )));
    assertNotEquals(viewState, new ServicesViewStateResolvableError(123, new ArrayList<>()));
    assertNotEquals(viewState, new ServicesViewStateResolvableError(123, Arrays.asList(
        new ServicesListItem(new Service(0, "n1", 100, true)),
        new ServicesListItem(new Service(2, "n3", 130, true))
    )));
    assertNotEquals(viewState, new ServicesViewStateResolvableError(123, Arrays.asList(
        new ServicesListItem(new Service(0, "n1", 100, true)),
        new ServicesListItem(new Service(1, "n2", 10, false)),
        new ServicesListItem(new Service(3, "n3", 130, true))
    )));
  }
}