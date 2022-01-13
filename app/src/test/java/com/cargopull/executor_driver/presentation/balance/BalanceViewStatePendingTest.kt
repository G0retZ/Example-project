package com.cargopull.executor_driver.presentation.balance

import com.cargopull.executor_driver.presentation.ViewState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class BalanceViewStatePendingTest {

    private lateinit var viewState: BalanceViewStatePending

    @Mock
    private lateinit var viewActions: BalanceViewActions
    @Mock
    private lateinit var parentViewState: ViewState<BalanceViewActions>
    @Mock
    private lateinit var parentViewState1: ViewState<BalanceViewActions>

    @Test
    fun testActions() {
        // Given:
        viewState = BalanceViewStatePending(parentViewState)

        // Action:
        viewState.apply(viewActions)

        // Effect:
        //    verify(viewActions).showBalancePending(true);
        verify<ViewState<BalanceViewActions>>(parentViewState, only()).apply(viewActions)
        verify(viewActions).blockWithPending("BalanceViewState")
        verifyNoMoreInteractions(viewActions)
    }

    @Test
    fun testActionsWithNull() {
        // Given:
        viewState = BalanceViewStatePending(null)

        // Action:
        viewState.apply(viewActions)

        // Effect:
        verify(viewActions).blockWithPending("BalanceViewState")
        verifyNoMoreInteractions(viewActions)
    }

    @Test
    fun testEquals() {
        viewState = BalanceViewStatePending(parentViewState)
        assertEquals(viewState, BalanceViewStatePending(parentViewState))
        assertNotEquals(viewState, BalanceViewStatePending(parentViewState1))
        assertNotEquals(viewState, BalanceViewStatePending(null))
    }
}