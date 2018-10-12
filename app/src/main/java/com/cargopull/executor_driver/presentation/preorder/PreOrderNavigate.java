package com.cargopull.executor_driver.presentation.preorder;

import androidx.annotation.StringDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Направления навигации с карточки заказа.
 */
@StringDef({
    PreOrderNavigate.ORDER_APPROVAL
})
@Retention(RetentionPolicy.SOURCE)
public @interface PreOrderNavigate {

  // Переход к закрыти карточки заказа.
  String ORDER_APPROVAL = "PreOrder.to.OrderApproval";
}
