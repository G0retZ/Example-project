package com.cargopull.executor_driver.entity

import org.junit.Assert.assertEquals

import org.junit.Test

class ExecutorBalanceTest {

    @Test
    fun testConstructor() {
        val cancelOrderReason = ExecutorBalance(1, 2, 3)
        assertEquals(cancelOrderReason.mainAccount, 1)
        assertEquals(cancelOrderReason.bonusAccount, 2)
        assertEquals(cancelOrderReason.cashlessAccount, 3)
        assertEquals(cancelOrderReason.summary, 6)
    }
}