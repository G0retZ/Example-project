package com.fasten.executor_driver.backend.web.incoming;

import android.support.annotation.Nullable;
import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * ответ от API содержащий данные о ТС.
 */
public class ApiVehicle {

  @SerializedName("id")
  private long id;
  @Nullable
  @SerializedName("markName")
  private String markName;
  @Nullable
  @SerializedName("modelName")
  private String modelName;
  @Nullable
  @SerializedName("licensePlate")
  private String licensePlate;
  @Nullable
  @SerializedName("color")
  private String color;
  @SuppressWarnings("SpellCheckingInspection")
  @SerializedName("busy")
  private boolean busy;
  @SerializedName("options")
  private List<ApiOptionItem> vehicleOptionItems;

  /**
   * Конструктор без параметров желателен для безопасной работы Gson.
   */
  @SuppressWarnings({"unused", "SpellCheckingInspection"})
  public ApiVehicle() {
  }

  public ApiVehicle(long id, @Nullable String markName, @Nullable String modelName,
      @Nullable String licensePlate, @Nullable String color, boolean busy,
      List<ApiOptionItem> vehicleOptionItems) {
    this.id = id;
    this.markName = markName;
    this.modelName = modelName;
    this.licensePlate = licensePlate;
    this.color = color;
    this.busy = busy;
    this.vehicleOptionItems = vehicleOptionItems;
  }

  public long getId() {
    return id;
  }

  @Nullable
  public String getMarkName() {
    return markName;
  }

  @Nullable
  public String getModelName() {
    return modelName;
  }

  @Nullable
  public String getLicensePlate() {
    return licensePlate;
  }

  @Nullable
  public String getColor() {
    return color;
  }

  public boolean isBusy() {
    return busy;
  }

  public List<ApiOptionItem> getVehicleOptionItems() {
    return vehicleOptionItems;
  }

  @Override
  public String toString() {
    return "ApiVehicle{" +
        "id=" + id +
        ", markName='" + markName + '\'' +
        ", modelName='" + modelName + '\'' +
        ", licensePlate='" + licensePlate + '\'' +
        ", color='" + color + '\'' +
        ", busy=" + busy +
        ", vehicleOptionItems=" + vehicleOptionItems +
        '}';
  }

  @SuppressWarnings("SimplifiableIfStatement")
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    ApiVehicle that = (ApiVehicle) o;

    if (id != that.id) {
      return false;
    }
    if (busy != that.busy) {
      return false;
    }
    if (markName != null ? !markName.equals(that.markName) : that.markName != null) {
      return false;
    }
    if (modelName != null ? !modelName.equals(that.modelName) : that.modelName != null) {
      return false;
    }
    if (licensePlate != null ? !licensePlate.equals(that.licensePlate)
        : that.licensePlate != null) {
      return false;
    }
    if (color != null ? !color.equals(that.color) : that.color != null) {
      return false;
    }
    return vehicleOptionItems != null ? vehicleOptionItems.equals(that.vehicleOptionItems)
        : that.vehicleOptionItems == null;
  }

  @Override
  public int hashCode() {
    int result = (int) (id ^ (id >>> 32));
    result = 31 * result + (markName != null ? markName.hashCode() : 0);
    result = 31 * result + (modelName != null ? modelName.hashCode() : 0);
    result = 31 * result + (licensePlate != null ? licensePlate.hashCode() : 0);
    result = 31 * result + (color != null ? color.hashCode() : 0);
    result = 31 * result + (busy ? 1 : 0);
    result = 31 * result + (vehicleOptionItems != null ? vehicleOptionItems.hashCode() : 0);
    return result;
  }
}
