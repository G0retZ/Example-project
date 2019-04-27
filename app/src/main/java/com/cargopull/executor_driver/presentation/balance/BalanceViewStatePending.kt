package com.cargopull.executor_driver.presentation.balance

import com.cargopull.executor_driver.presentation.ViewState

/**
 * Состояние ожидания загрузки Баланса.
 */
internal class BalanceViewStatePending(private val parentViewState: ViewState<BalanceViewActions>?) : ViewState<BalanceViewActions> {

    override fun apply(balanceViewActions: BalanceViewActions) {
        parentViewState?.apply(balanceViewActions)
        balanceViewActions.blockWithPending("BalanceViewState")
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BalanceViewStatePending

        if (parentViewState != other.parentViewState) return false

        return true
    }

    override fun hashCode(): Int {
        return parentViewState?.hashCode() ?: 0
    }

}
