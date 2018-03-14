package com.fasten.executor_driver.presentation.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import com.fasten.executor_driver.R;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class PersistenceViewStateStartTest {

  private PersistenceViewStateStart viewState;

  @Mock
  private PersistenceViewActions codeViewActions;

  @Before
  public void setUp() throws Exception {
    viewState = new PersistenceViewStateStart(R.string.online, R.string.wait_for_orders);
  }

  @Test
  public void testActions() throws Exception {
    // Действие:
    viewState.apply(codeViewActions);

    // Результат:
    verify(codeViewActions).startService(R.string.online, R.string.wait_for_orders);
    verifyNoMoreInteractions(codeViewActions);
  }

  @Test
  public void testEquals() throws Exception {
    assertEquals(viewState,
        new PersistenceViewStateStart(R.string.online, R.string.wait_for_orders));
    assertNotEquals(viewState, new PersistenceViewStateStart(0, R.string.wait_for_orders));
    assertNotEquals(viewState, new PersistenceViewStateStart(R.string.online, 0));
  }
}