package com.fasten.executor_driver.presentation.code;

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
public class CodeViewStateSuccessTest {

	private CodeViewStateSuccess viewState;

	@Mock
	private CodeViewActions codeViewActions;

	@Before
	public void setUp() throws Exception {
		viewState = new CodeViewStateSuccess(12);
	}

	@Test
	public void testActions() throws Exception {
		// when:
		viewState.apply(codeViewActions);

		// then:
		verify(codeViewActions).setInputMessage(12);
		verify(codeViewActions).showPending(false);
		verify(codeViewActions).showError(null);
		verify(codeViewActions).letIn();
		verifyNoMoreInteractions(codeViewActions);
	}

	@Test
	public void testEquals() throws Exception {
		assertEquals(viewState, new CodeViewStateSuccess(12));
		assertEquals(viewState, new CodeViewStateSuccess(new CodeViewStateCommon(12)));
		assertEquals(new CodeViewStateSuccess(viewState), new CodeViewStateSuccess(12));
		assertNotEquals(viewState, new CodeViewStateSuccess(11));
	}
}
