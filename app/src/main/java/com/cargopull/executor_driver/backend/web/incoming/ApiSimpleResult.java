package com.cargopull.executor_driver.backend.web.incoming;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Объект простого ответа из АПИ об успешной или неуспешной операции с текстом сообщения.
 */
@SuppressWarnings("unused")
public class ApiSimpleResult {

  @SerializedName("code")
  @Expose
  private String code;
  @SerializedName("message")
  @Expose
  private String message;

  public String getCode() {
    return code;
  }

  public String getMessage() {
    return message;
  }
}
