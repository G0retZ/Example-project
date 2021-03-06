package com.cargopull.executor_driver.gateway;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.cargopull.executor_driver.CommonTestRule;

/**
 * Тестовое правило, которое создает JSON заказа для тестов мапперов.
 */

class ApiOrderTimersRule extends CommonTestRule {

  private final static String FIELD_DIVIDER = ",";
  private final static String OBJECT_START = "{";
  private final static String OBJECT_END = "}";
  private final static String OVER_PACKAGE_TIMER = "\"overPackageTimer\": %d";
  private final static String OVER_PACKAGE_PERIOD = "\"overPackagePeriod\": %d";

  @NonNull
  String getApiOrderTimers(@Nullable Long timer, @Nullable Long period) {
    return OBJECT_START
        + (timer == null ? "" : String.format(OVER_PACKAGE_TIMER, timer))
        + (timer == null ? "" : FIELD_DIVIDER)
        + (period == null ? "" : String.format(OVER_PACKAGE_PERIOD, period))
        + OBJECT_END;
  }
}
