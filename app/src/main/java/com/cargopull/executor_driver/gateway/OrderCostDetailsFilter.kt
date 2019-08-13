package com.cargopull.executor_driver.gateway

import com.cargopull.executor_driver.backend.stomp.StompFrame
import io.reactivex.functions.Predicate

class OrderCostDetailsFilter : Predicate<StompFrame> {

    override fun test(stompFrame: StompFrame): Boolean {
        return "PAYMENT_CONFIRMATION" == stompFrame.headers.get("Status")
    }
}
