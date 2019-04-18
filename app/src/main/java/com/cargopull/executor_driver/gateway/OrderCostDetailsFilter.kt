package com.cargopull.executor_driver.gateway

import io.reactivex.functions.Predicate
import ua.naiksoftware.stomp.client.StompMessage

class OrderCostDetailsFilter : Predicate<StompMessage> {

    override fun test(stompMessage: StompMessage): Boolean {
        return "PAYMENT_CONFIRMATION" == stompMessage.findHeader("Status")
    }
}
