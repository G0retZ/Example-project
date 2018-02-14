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
  @SerializedName("mark")
  private ApiParam mark;
  @Nullable
  @SerializedName("model")
  private ApiParam model;
  @Nullable
  @SerializedName("licensePlate")
  private String licensePlate;
  @Nullable
  @SerializedName("color")
  private ApiParam color;
  @SuppressWarnings("SpellCheckingInspection")
  @SerializedName("buzy")
  private boolean busy;
  @SerializedName("vehicleOptionItems")
  private List<ApiVehicleOptionItem> vehicleOptionItems;

  /**
   * Конструктор без параметров желателен для безопасной работы Gson.
   */
  @SuppressWarnings({"unused", "SpellCheckingInspection"})
  public ApiVehicle() {
  }

  ApiVehicle(long id, @Nullable ApiParam mark,
      @Nullable ApiParam model, @Nullable String licensePlate,
      @Nullable ApiParam color, boolean busy,
      List<ApiVehicleOptionItem> vehicleOptionItems) {
    this.id = id;
    this.mark = mark;
    this.model = model;
    this.licensePlate = licensePlate;
    this.color = color;
    this.busy = busy;
    this.vehicleOptionItems = vehicleOptionItems;
  }

  long getId() {
    return id;
  }

  @Nullable
  ApiParam getMark() {
    return mark;
  }

  @Nullable
  ApiParam getModel() {
    return model;
  }

  @Nullable
  String getLicensePlate() {
    return licensePlate;
  }

  @Nullable
  ApiParam getColor() {
    return color;
  }

  boolean isBusy() {
    return busy;
  }

  List<ApiVehicleOptionItem> getVehicleOptionItems() {
    return vehicleOptionItems;
  }

  @Override
  public String toString() {
    return "ApiVehicle{" +
        "id=" + id +
        ", mark=" + mark +
        ", model=" + model +
        ", licensePlate='" + licensePlate + '\'' +
        ", color=" + color +
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
    if (mark != null ? !mark.equals(that.mark) : that.mark != null) {
      return false;
    }
    if (model != null ? !model.equals(that.model) : that.model != null) {
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
    result = 31 * result + (mark != null ? mark.hashCode() : 0);
    result = 31 * result + (model != null ? model.hashCode() : 0);
    result = 31 * result + (licensePlate != null ? licensePlate.hashCode() : 0);
    result = 31 * result + (color != null ? color.hashCode() : 0);
    result = 31 * result + (busy ? 1 : 0);
    result = 31 * result + (vehicleOptionItems != null ? vehicleOptionItems.hashCode() : 0);
    return result;
  }
}
