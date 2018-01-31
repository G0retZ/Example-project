package com.fasten.executor_driver.presentation.phone;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@RunWith(MockitoJUnitRunner.class)
public class PhoneViewStateReadyTest {

	private PhoneViewStateReady viewState;

	@Mock
	private PhoneViewActions phoneViewActions;

	@Before
	public void setUp() throws Exception {
		viewState = new PhoneViewStateReady();
	}

	@Test
	public void testActions() throws Exception {
		// Действие:
		viewState.apply(phoneViewActions);

		// Результат:
		verify(phoneViewActions).showPending(false);
		verify(phoneViewActions).showError(null);
		verify(phoneViewActions).setInputEditable(true);
		verify(phoneViewActions).enableButton(true);
		verifyNoMoreInteractions(phoneViewActions);
	}
}