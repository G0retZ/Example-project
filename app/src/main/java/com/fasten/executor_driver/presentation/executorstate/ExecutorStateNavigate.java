package com.fasten.executor_driver.presentation.executorstate;

import android.support.annotation.StringDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Направления навигации при изменении статуса исполнителя.
 */
@StringDef({
    ExecutorStateNavigate.NO_NETWORK,
    ExecutorStateNavigate.AUTHORIZE,
    ExecutorStateNavigate.MAP_SHIFT_CLOSED,
    ExecutorStateNavigate.MAP_SHIFT_OPENED,
    ExecutorStateNavigate.MAP_ONLINE,
    ExecutorStateNavigate.OFFER_CONFIRMATION,
    ExecutorStateNavigate.CLIENT_CONFIRMATION
})
@Retention(RetentionPolicy.SOURCE)
public @interface ExecutorStateNavigate {

  // Переход к решению проблем сети.
  String NO_NETWORK = "to.NoNetwork";
  // Переход к авторизации.
  String AUTHORIZE = "to.Authorization";
  // Переход к карте.
  String MAP_SHIFT_CLOSED = "to.Map.Shift.Closed";
  // Переход к карте.
  String MAP_SHIFT_OPENED = "to.Map.Shift.Opened";
  // Переход к получению заказов.
  String MAP_ONLINE = "to.Map.Online";
  // Переход к исполнению заказа.
  String OFFER_CONFIRMATION = "to.Offer.Confirmation";
  // Переход к ожиданию подтверждения клиентом.
  String CLIENT_CONFIRMATION = "to.Client.Confirmation";
}
