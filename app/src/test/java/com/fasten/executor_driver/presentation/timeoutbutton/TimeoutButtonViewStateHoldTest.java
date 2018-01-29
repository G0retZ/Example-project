package com.fasten.executor_driver.presentation.timeoutbutton;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@RunWith(MockitoJUnitRunner.class)
public class TimeoutButtonViewStateHoldTest {

	private TimeoutButtonViewStateHold viewState;

	@Mock
	private TimeoutButtonViewActions timeoutButtonViewActions;

	@Before
	public void setUp() throws Exception {
		viewState = new TimeoutButtonViewStateHold(12);
	}

	@Test
	public void testActions() throws Exception {
		// Действие:
		viewState.apply(timeoutButtonViewActions);

		// Результат:
		verify(timeoutButtonViewActions).showTimer(12L);
		verify(timeoutButtonViewActions).setResponsive(false);
		verifyNoMoreInteractions(timeoutButtonViewActions);
	}

	@Test
	public void testEquals() throws Exception {
		assertEquals(viewState, new TimeoutButtonViewStateHold(12));
		assertNotEquals(viewState, new TimeoutButtonViewStateHold(13));
		assertNotEquals(viewState, new TimeoutButtonViewStateHold(11));
	}
}
