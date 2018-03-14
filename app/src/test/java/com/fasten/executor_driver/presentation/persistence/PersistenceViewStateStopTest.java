package com.fasten.executor_driver.presentation.persistence;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class PersistenceViewStateStopTest {

  private PersistenceViewStateStop viewState;

  @Mock
  private PersistenceViewActions persistenceViewActions;

  @Before
  public void setUp() throws Exception {
    viewState = new PersistenceViewStateStop();
  }

  @Test
  public void testActions() throws Exception {
    // Действие:
    viewState.apply(persistenceViewActions);

    // Результат:
    verify(persistenceViewActions).stopService();
    verifyNoMoreInteractions(persistenceViewActions);
  }
}