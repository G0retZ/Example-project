package com.fasten.executor_driver.presentation.phone;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class PhoneViewStateErrorTest {

	private PhoneViewStateError viewState;

	@Mock
	private PhoneViewActions phoneViewActions;

	@Before
	public void setUp() throws Exception {
		viewState = new PhoneViewStateError(new IllegalArgumentException("mess"));
	}

	@Test
	public void testActions() throws Exception {
		// when:
		viewState.apply(phoneViewActions);

		// then:
		verify(phoneViewActions).showPending(false);
		verify(phoneViewActions).enableButton(false);
		verify(phoneViewActions).showError(any(Exception.class));
		assertEquals(viewState, new PhoneViewStateError(new IllegalArgumentException("mess")));
	}

	@Test
	public void testEquals() throws Exception {
		assertEquals(viewState, new PhoneViewStateError(new IllegalArgumentException("mess")));
		assertNotEquals(viewState, new PhoneViewStateError(new IllegalArgumentException("mes")));
		assertNotEquals(viewState, new PhoneViewStateError(new NullPointerException("mess")));
	}

}