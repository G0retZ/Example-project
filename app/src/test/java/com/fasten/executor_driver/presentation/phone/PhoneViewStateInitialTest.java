package com.fasten.executor_driver.presentation.phone;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@RunWith(MockitoJUnitRunner.class)
public class PhoneViewStateInitialTest {

	private PhoneViewStateInitial viewState;

	@Mock
	private PhoneViewActions phoneViewActions;

	@Before
	public void setUp() throws Exception {
		viewState = new PhoneViewStateInitial();
	}

	@Test
	public void testActions() throws Exception {
		// Действие:
		viewState.apply(phoneViewActions);

		// Результат:
		verify(phoneViewActions).showPending(false);
		verify(phoneViewActions).showError(null);
		verify(phoneViewActions).enableButton(false);
		verifyNoMoreInteractions(phoneViewActions);
	}
}