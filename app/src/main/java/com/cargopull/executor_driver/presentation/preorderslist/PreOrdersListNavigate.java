package com.cargopull.executor_driver.presentation.preorderslist;

import androidx.annotation.StringDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Направления навигации из окна списка предзаказов.
 */
@StringDef({
    PreOrdersListNavigate.PRE_ORDER
})
@Retention(RetentionPolicy.SOURCE)
public @interface PreOrdersListNavigate {

  // Переход к выбранному предзаказу.
  String PRE_ORDER = "PreOrdersList.to.PreOrder";
}
