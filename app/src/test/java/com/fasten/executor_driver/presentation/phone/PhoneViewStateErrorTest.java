package com.fasten.executor_driver.presentation.phone;

import com.fasten.executor_driver.utils.ThrowableUtils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@RunWith(MockitoJUnitRunner.class)
public class PhoneViewStateErrorTest {

	private PhoneViewStateError viewState;

	@Mock
	private PhoneViewActions phoneViewActions;

	@Captor
	private ArgumentCaptor<Throwable> throwableCaptor;

	@Before
	public void setUp() throws Exception {
		viewState = new PhoneViewStateError(new IllegalArgumentException("mess"));
	}

	@Test
	public void testActions() throws Exception {
		// Действие:
		viewState.apply(phoneViewActions);

		// Результат:
		verify(phoneViewActions).showPending(false);
		verify(phoneViewActions).setInputEditable(true);
		verify(phoneViewActions).enableButton(false);
		verify(phoneViewActions).showError(throwableCaptor.capture());
		verifyNoMoreInteractions(phoneViewActions);
		assertTrue(
				ThrowableUtils.throwableEquals(
						throwableCaptor.getValue(),
						new IllegalArgumentException("mess")
				)
		);
	}

	@Test
	public void testEquals() throws Exception {
		assertEquals(viewState, new PhoneViewStateError(new IllegalArgumentException("mess")));
		assertNotEquals(viewState, new PhoneViewStateError(new IllegalArgumentException("mes")));
		assertNotEquals(viewState, new PhoneViewStateError(new NullPointerException("mess")));
	}
}