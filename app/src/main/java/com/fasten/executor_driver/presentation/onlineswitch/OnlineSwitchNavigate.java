package com.fasten.executor_driver.presentation.onlineswitch;

import android.support.annotation.StringDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Направления навигации при старте приложения.
 */
@StringDef({
    OnlineSwitchNavigate.SERVICES
})
@Retention(RetentionPolicy.SOURCE)
public @interface OnlineSwitchNavigate {

  // Переход к выбору услуг.
  String SERVICES = "OnlineSwitch.to.Services";
}
