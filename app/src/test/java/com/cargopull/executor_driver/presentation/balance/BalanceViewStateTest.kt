package com.cargopull.executor_driver.presentation.balance

import com.cargopull.executor_driver.R
import com.cargopull.executor_driver.entity.ExecutorBalance
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner
import java.text.DecimalFormat

@RunWith(MockitoJUnitRunner::class)
class BalanceViewStateTest {

    private lateinit var viewState: BalanceViewState

    @Mock
    private lateinit var viewActions: BalanceViewActions
    @Mock
    private lateinit var executorBalance: ExecutorBalance
    @Mock
    private lateinit var executorBalance1: ExecutorBalance

    @Before
    fun setUp() {
        viewState = BalanceViewState(executorBalance)
    }

    @Test
    fun testActionsWithCentsWithPositive() {
        // Дано:
        `when`(viewActions.isShowCents).thenReturn(true)
        `when`(viewActions.currencyFormat).thenReturn("##,###,###.## ₽")
        `when`(executorBalance.summary).thenReturn(1231233)
        `when`(executorBalance.mainAccount).thenReturn(32901)
        `when`(executorBalance.cashlessAccount).thenReturn(32180)
        `when`(executorBalance.bonusAccount).thenReturn(12093)

        // Действие:
        viewState.apply(viewActions)

        // Результат:
        verify(viewActions).setTextColor(R.id.balanceSummaryTitle, R.color.textColorSecondary)
        verify(viewActions).setTextColor(R.id.balanceSummary, R.color.textColorPrimary)
        verify(viewActions).setTextColor(R.id.balanceAmountTitle, R.color.textColorPrimary)
        verify(viewActions).setTextColor(R.id.balanceAmount, R.color.textColorPrimary)
        verify(viewActions).currencyFormat
        verify(viewActions).isShowCents
        val decimalFormat = DecimalFormat("##,###,###.## ₽")
        decimalFormat.maximumFractionDigits = 2
        decimalFormat.minimumFractionDigits = 2
        verify(viewActions).setText(R.id.balanceSummary, decimalFormat.format(12312.33))
        verify(viewActions).setText(R.id.balanceAmount, decimalFormat.format(329.01))
        verify(viewActions).setText(R.id.cashlessAmount, decimalFormat.format(321.80))
        verify(viewActions).setText(R.id.bonusAmount, decimalFormat.format(120.93))
        verify(viewActions).unblockWithPending("BalanceViewState")
        verifyNoMoreInteractions(viewActions)
    }

    @Test
    fun testActionsWithoutCentsWithPositive() {
        // Дано:
        `when`(viewActions.isShowCents).thenReturn(false)
        `when`(viewActions.currencyFormat).thenReturn("##,###,### ₽")
        `when`(executorBalance.summary).thenReturn(1231233)
        `when`(executorBalance.mainAccount).thenReturn(32901)
        `when`(executorBalance.cashlessAccount).thenReturn(32180)
        `when`(executorBalance.bonusAccount).thenReturn(12093)

        // Действие:
        viewState.apply(viewActions)

        // Результат:
        verify(viewActions).setTextColor(R.id.balanceSummaryTitle, R.color.textColorSecondary)
        verify(viewActions).setTextColor(R.id.balanceSummary, R.color.textColorPrimary)
        verify(viewActions).setTextColor(R.id.balanceAmountTitle, R.color.textColorPrimary)
        verify(viewActions).setTextColor(R.id.balanceAmount, R.color.textColorPrimary)
        verify(viewActions).currencyFormat
        verify(viewActions).isShowCents
        val decimalFormat = DecimalFormat("##,###,### ₽")
        decimalFormat.maximumFractionDigits = 0
        decimalFormat.minimumFractionDigits = 0
        verify(viewActions).setText(R.id.balanceSummary, decimalFormat.format(12312))
        verify(viewActions).setText(R.id.balanceAmount, decimalFormat.format(329))
        verify(viewActions).setText(R.id.cashlessAmount, decimalFormat.format(322))
        verify(viewActions).setText(R.id.bonusAmount, decimalFormat.format(121))
        verify(viewActions).unblockWithPending("BalanceViewState")
        verifyNoMoreInteractions(viewActions)
    }

    @Test
    fun testActionsWithCentsWithNegative() {
        // Дано:
        `when`(viewActions.isShowCents).thenReturn(true)
        `when`(viewActions.currencyFormat).thenReturn("##,###,###.## ₽")
        `when`(executorBalance.summary).thenReturn(1231233)
        `when`(executorBalance.mainAccount).thenReturn(-32901)
        `when`(executorBalance.cashlessAccount).thenReturn(32180)
        `when`(executorBalance.bonusAccount).thenReturn(12093)

        // Действие:
        viewState.apply(viewActions)

        // Результат:
        verify(viewActions).setTextColor(R.id.balanceSummaryTitle, R.color.textColorSecondary)
        verify(viewActions).setTextColor(R.id.balanceSummary, R.color.textColorPrimary)
        verify(viewActions).setTextColor(R.id.balanceAmountTitle, R.color.colorError)
        verify(viewActions).setTextColor(R.id.balanceAmount, R.color.colorError)
        verify(viewActions).currencyFormat
        verify(viewActions).isShowCents
        val decimalFormat = DecimalFormat("##,###,###.## ₽")
        decimalFormat.maximumFractionDigits = 2
        decimalFormat.minimumFractionDigits = 2
        verify(viewActions).setText(R.id.balanceSummary, decimalFormat.format(12312.33))
        verify(viewActions).setText(R.id.balanceAmount, decimalFormat.format(-329.01))
        verify(viewActions).setText(R.id.cashlessAmount, decimalFormat.format(321.80))
        verify(viewActions).setText(R.id.bonusAmount, decimalFormat.format(120.93))
        verify(viewActions).unblockWithPending("BalanceViewState")
        verifyNoMoreInteractions(viewActions)
    }

    @Test
    fun testActionsWithoutCentsWithNegative() {
        // Дано:
        `when`(viewActions.isShowCents).thenReturn(false)
        `when`(viewActions.currencyFormat).thenReturn("##,###,### ₽")
        `when`(executorBalance.summary).thenReturn(1231233)
        `when`(executorBalance.mainAccount).thenReturn(-32901)
        `when`(executorBalance.cashlessAccount).thenReturn(32180)
        `when`(executorBalance.bonusAccount).thenReturn(12093)

        // Действие:
        viewState.apply(viewActions)

        // Результат:
        verify(viewActions).setTextColor(R.id.balanceSummaryTitle, R.color.textColorSecondary)
        verify(viewActions).setTextColor(R.id.balanceSummary, R.color.textColorPrimary)
        verify(viewActions).setTextColor(R.id.balanceAmountTitle, R.color.colorError)
        verify(viewActions).setTextColor(R.id.balanceAmount, R.color.colorError)
        verify(viewActions).currencyFormat
        verify(viewActions).isShowCents
        val decimalFormat = DecimalFormat("##,###,### ₽")
        decimalFormat.maximumFractionDigits = 0
        decimalFormat.minimumFractionDigits = 0
        verify(viewActions).setText(R.id.balanceSummary, decimalFormat.format(12312))
        verify(viewActions).setText(R.id.balanceAmount, decimalFormat.format(-329))
        verify(viewActions).setText(R.id.cashlessAmount, decimalFormat.format(322))
        verify(viewActions).setText(R.id.bonusAmount, decimalFormat.format(121))
        verify(viewActions).unblockWithPending("BalanceViewState")
        verifyNoMoreInteractions(viewActions)
    }

    @Test
    fun testActionsWithCentsWithDoubleNegative() {
        // Дано:
        `when`(viewActions.isShowCents).thenReturn(true)
        `when`(viewActions.currencyFormat).thenReturn("##,###,###.## ₽")
        `when`(executorBalance.summary).thenReturn(-1231233)
        `when`(executorBalance.mainAccount).thenReturn(-32901)
        `when`(executorBalance.cashlessAccount).thenReturn(32180)
        `when`(executorBalance.bonusAccount).thenReturn(12093)

        // Действие:
        viewState.apply(viewActions)

        // Результат:
        verify(viewActions).setTextColor(R.id.balanceSummaryTitle, R.color.colorError)
        verify(viewActions).setTextColor(R.id.balanceSummary, R.color.colorError)
        verify(viewActions).setTextColor(R.id.balanceAmountTitle, R.color.colorError)
        verify(viewActions).setTextColor(R.id.balanceAmount, R.color.colorError)
        verify(viewActions).currencyFormat
        verify(viewActions).isShowCents
        val decimalFormat = DecimalFormat("##,###,###.## ₽")
        decimalFormat.maximumFractionDigits = 2
        decimalFormat.minimumFractionDigits = 2
        verify(viewActions).setText(R.id.balanceSummary, decimalFormat.format(-12312.33))
        verify(viewActions).setText(R.id.balanceAmount, decimalFormat.format(-329.01))
        verify(viewActions).setText(R.id.cashlessAmount, decimalFormat.format(321.80))
        verify(viewActions).setText(R.id.bonusAmount, decimalFormat.format(120.93))
        verify(viewActions).unblockWithPending("BalanceViewState")
        verifyNoMoreInteractions(viewActions)
    }

    @Test
    fun testActionsWithoutCentsWithDoubleNegative() {
        // Дано:
        `when`(viewActions.isShowCents).thenReturn(false)
        `when`(viewActions.currencyFormat).thenReturn("##,###,### ₽")
        `when`(executorBalance.summary).thenReturn(-1231233)
        `when`(executorBalance.mainAccount).thenReturn(-32901)
        `when`(executorBalance.cashlessAccount).thenReturn(32180)
        `when`(executorBalance.bonusAccount).thenReturn(12093)

        // Действие:
        viewState.apply(viewActions)

        // Результат:
        verify(viewActions).setTextColor(R.id.balanceSummaryTitle, R.color.colorError)
        verify(viewActions).setTextColor(R.id.balanceSummary, R.color.colorError)
        verify(viewActions).setTextColor(R.id.balanceAmountTitle, R.color.colorError)
        verify(viewActions).setTextColor(R.id.balanceAmount, R.color.colorError)
        verify(viewActions).currencyFormat
        verify(viewActions).isShowCents
        val decimalFormat = DecimalFormat("##,###,### ₽")
        decimalFormat.maximumFractionDigits = 0
        decimalFormat.minimumFractionDigits = 0
        verify(viewActions).setText(R.id.balanceSummary, decimalFormat.format(-12312))
        verify(viewActions).setText(R.id.balanceAmount, decimalFormat.format(-329))
        verify(viewActions).setText(R.id.cashlessAmount, decimalFormat.format(322))
        verify(viewActions).setText(R.id.bonusAmount, decimalFormat.format(121))
        verify(viewActions).unblockWithPending("BalanceViewState")
        verifyNoMoreInteractions(viewActions)
    }

    @Test
    fun testEquals() {
        assertEquals(viewState, BalanceViewState(executorBalance))
        assertNotEquals(viewState, BalanceViewState(executorBalance1))
    }
}