package com.fasten.executor_driver.presentation.timeoutbutton;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

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
		// when:
		viewState.apply(timeoutButtonViewActions);

		// then:
		verify(timeoutButtonViewActions).showTimer(12L);
		verify(timeoutButtonViewActions).setResponsive(false);
		verifyNoMoreInteractions(timeoutButtonViewActions);
	}
}
