package com.fasten.executor_driver.presentation.paymentoptions;

import android.support.annotation.StringDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Направления навигации из способов оплаты.
 */
@SuppressWarnings("SpellCheckingInspection")
@StringDef({
    PaymentOptionsNavigate.QIWI,
    PaymentOptionsNavigate.SBERBANK_ONLINE
})
@Retention(RetentionPolicy.SOURCE)
public @interface PaymentOptionsNavigate {

  // Переход к оплате через Киви.
  String QIWI = "PaymentOptions.to.Qiwi";

  // Переход к оплате через Сбербанк-онлайн.
  String SBERBANK_ONLINE = "PaymentOptions.to.SberbankOnline";
}
