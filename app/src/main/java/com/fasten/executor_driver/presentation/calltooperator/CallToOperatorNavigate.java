package com.fasten.executor_driver.presentation.calltooperator;

import android.support.annotation.StringDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Направления навигации из окна связи с оператором.
 */
@StringDef({
    CallToOperatorNavigate.FINISHED
})
@Retention(RetentionPolicy.SOURCE)
public @interface CallToOperatorNavigate {

  // Переход к окончанию звонка.
  String FINISHED = "CallToOperator.finish";
}
