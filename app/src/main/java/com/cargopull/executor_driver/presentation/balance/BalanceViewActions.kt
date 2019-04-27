package com.cargopull.executor_driver.presentation.balance

import com.cargopull.executor_driver.presentation.FragmentViewActions

/**
 * Действия для смены состояния вида окна баланса.
 */
interface BalanceViewActions : FragmentViewActions {


    /**
     * Вернуть необходимость отображения копеек/центов.
     */
    val isShowCents: Boolean

    /**
     * Вернуть формат отображения валюты.
     */
    val currencyFormat: String
}
