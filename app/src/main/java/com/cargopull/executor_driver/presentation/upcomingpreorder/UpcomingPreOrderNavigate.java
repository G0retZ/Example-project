package com.cargopull.executor_driver.presentation.upcomingpreorder;

import android.support.annotation.StringDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Направления навигации с карточки предстоящего заказа.
 */
@StringDef({
    UpcomingPreOrderNavigate.UPCOMING_PRE_ORDER
})
@Retention(RetentionPolicy.SOURCE)
public @interface UpcomingPreOrderNavigate {

  // Переход к закрыти карточки заказа.
  String UPCOMING_PRE_ORDER = "UpcomingPreOrder.to.UpcomingPreOrder";
}
