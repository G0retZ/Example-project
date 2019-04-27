package com.cargopull.executor_driver.presentation.balance

import com.cargopull.executor_driver.R
import com.cargopull.executor_driver.entity.ExecutorBalance
import com.cargopull.executor_driver.presentation.ViewState
import java.text.DecimalFormat

/**
 * Состояние вида баланса.
 */
internal class BalanceViewState(private val executorBalance: ExecutorBalance) : ViewState<BalanceViewActions> {

    override fun apply(balanceViewActions: BalanceViewActions) {
        if (executorBalance.summary < 0) {
            balanceViewActions.setTextColor(R.id.balanceSummaryTitle, R.color.colorError)
            balanceViewActions.setTextColor(R.id.balanceSummary, R.color.colorError)
        } else {
            balanceViewActions.setTextColor(R.id.balanceSummaryTitle, R.color.textColorSecondary)
            balanceViewActions.setTextColor(R.id.balanceSummary, R.color.textColorPrimary)
        }
        if (executorBalance.mainAccount < 0) {
            balanceViewActions.setTextColor(R.id.balanceAmountTitle, R.color.colorError)
            balanceViewActions.setTextColor(R.id.balanceAmount, R.color.colorError)
        } else {
            balanceViewActions.setTextColor(R.id.balanceAmountTitle, R.color.textColorPrimary)
            balanceViewActions.setTextColor(R.id.balanceAmount, R.color.textColorPrimary)
        }
        val decimalFormat = DecimalFormat(balanceViewActions.currencyFormat)
        if (balanceViewActions.isShowCents) {
            decimalFormat.maximumFractionDigits = 2
            decimalFormat.minimumFractionDigits = 2
            balanceViewActions.setText(R.id.balanceSummary, decimalFormat.format(executorBalance.summary * 0.01f))
            balanceViewActions.setText(R.id.balanceAmount, decimalFormat.format(executorBalance.mainAccount * 0.01f))
            balanceViewActions.setText(R.id.cashlessAmount, decimalFormat.format(executorBalance.cashlessAccount * 0.01f))
            balanceViewActions.setText(R.id.bonusAmount, decimalFormat.format(executorBalance.bonusAccount * 0.01f))
        } else {
            decimalFormat.maximumFractionDigits = 0
            decimalFormat.minimumFractionDigits = 0
            balanceViewActions.setText(R.id.balanceSummary, decimalFormat.format(Math.round(executorBalance.summary * 0.01f)))
            balanceViewActions.setText(R.id.balanceAmount, decimalFormat.format(Math.round(executorBalance.mainAccount * 0.01f)))
            balanceViewActions.setText(R.id.cashlessAmount, decimalFormat.format(Math.round(executorBalance.cashlessAccount * 0.01f)))
            balanceViewActions.setText(R.id.bonusAmount, decimalFormat.format(Math.round(executorBalance.bonusAccount * 0.01f)))
        }
        balanceViewActions.unblockWithPending("BalanceViewState")
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BalanceViewState

        if (executorBalance != other.executorBalance) return false

        return true
    }

    override fun hashCode(): Int {
        return executorBalance.hashCode()
    }

}
