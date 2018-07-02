package com.fasten.executor_driver.presentation.calltoclient;

import android.support.annotation.StringDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Направления навигации из окна звонка клиенту.
 */
@StringDef({
    CallToClientNavigate.NO_CONNECTION
})
@Retention(RetentionPolicy.SOURCE)
@interface CallToClientNavigate {

  // Переход к ошибке соединения.
  String NO_CONNECTION = "CallToClient.to.NoConnection";
}
