package com.cargopull.executor_driver.backend.web.incoming;

import android.support.annotation.Nullable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Объект простого ответа из АПИ об успешной или неуспешной операции с текстом сообщения.
 */
@SuppressWarnings("unused")
public class ApiSimpleResult {

  @SerializedName("code")
  @Expose
  @Nullable
  private String code;
  @SerializedName("message")
  @Expose
  @Nullable
  private String message;

  @Nullable
  public String getCode() {
    return code;
  }

  @Nullable
  public String getMessage() {
    return message;
  }
}
