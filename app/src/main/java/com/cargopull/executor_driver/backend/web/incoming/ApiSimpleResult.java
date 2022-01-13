package com.cargopull.executor_driver.backend.web.incoming;

import androidx.annotation.Nullable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Объект простого ответа из АПИ об успешной или неуспешной операции с текстом сообщения.
 */
@SuppressWarnings("unused")
public class ApiSimpleResult<T> {

  @SerializedName("code")
  @Expose
  @Nullable
  private String code;
  @SerializedName("message")
  @Expose
  @Nullable
  private String message;
  @SerializedName("status")
  @Expose
  @Nullable
  private String status;
  @SerializedName("data")
  @Expose
  @Nullable
  private T data;

  @SuppressWarnings({"unused"})
  public ApiSimpleResult() {
  }

  public ApiSimpleResult(@Nullable String code, @Nullable String message, @Nullable String status,
      @Nullable T data) {
    this.code = code;
    this.message = message;
    this.status = status;
    this.data = data;
  }

  @Nullable
  public String getCode() {
    return code;
  }

  @Nullable
  public String getMessage() {
    return message;
  }

  @Nullable
  public String getStatus() {
    return status;
  }

  @Nullable
  public T getData() {
    return data;
  }
}
