package com.cargopull.executor_driver.presentation.menu;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import com.cargopull.executor_driver.R;
import com.cargopull.executor_driver.presentation.FragmentViewActions;
import com.cargopull.executor_driver.presentation.ViewState;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class MenuViewStateFilterUnAvailableTest {

  @Mock
  private FragmentViewActions viewActions;

  @Test
  public void testActions() {
    // Дано:
    ViewState<FragmentViewActions> viewState = new MenuViewStateFilterUnAvailable();

    // Действие:
    viewState.apply(viewActions);

    // Результат:
    verify(viewActions).setEnabled(R.id.filter, false);
    verify(viewActions).setClickAction(R.id.filter, null);
    verifyNoMoreInteractions(viewActions);
  }
}