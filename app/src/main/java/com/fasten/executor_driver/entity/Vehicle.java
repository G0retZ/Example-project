package com.fasten.executor_driver.entity;

import android.support.annotation.NonNull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Бизнес сущность средства исполнения. Неизменный.
 * Создается через конструктор с не нулевыми полями.
 * Список опций неизменен, но его содержимое может изменяться - дополняться или заменяться.
 */
public class Vehicle {

  private final long id;
  @NonNull
  private final String manufacturer;
  @NonNull
  private final String model;
  @NonNull
  private final String color;
  @NonNull
  private final String licensePlate;
  private final boolean busy;
  @NonNull
  private final List<VehicleOption> vehicleOptions = new ArrayList<>();

  public Vehicle(long id, @NonNull String manufacturer, @NonNull String model,
      @NonNull String color, @NonNull String licensePlate, boolean busy) {
    this.id = id;
    this.manufacturer = manufacturer;
    this.model = model;
    this.color = color;
    this.licensePlate = licensePlate;
    this.busy = busy;
  }

  public long getId() {
    return id;
  }

  @NonNull
  public String getManufacturer() {
    return manufacturer;
  }

  @NonNull
  public String getModel() {
    return model;
  }

  @NonNull
  public String getColor() {
    return color;
  }

  @NonNull
  public String getLicensePlate() {
    return licensePlate;
  }

  public boolean isBusy() {
    return busy;
  }

  @NonNull
  public List<VehicleOption> getVehicleOptions() {
    return vehicleOptions;
  }

  void setVehicleOptions(@NonNull VehicleOption... vehicleOptions) {
    this.vehicleOptions.clear();
    addVehicleOptions(vehicleOptions);
  }

  public void addVehicleOptions(@NonNull VehicleOption... vehicleOptions) {
    this.vehicleOptions.addAll(Arrays.asList(vehicleOptions));
  }

  @Override
  public String toString() {
    return "Vehicle{" +
        "id=" + id +
        ", manufacturer='" + manufacturer + '\'' +
        ", model='" + model + '\'' +
        ", color='" + color + '\'' +
        ", licensePlate='" + licensePlate + '\'' +
        ", busy=" + busy +
        ", vehicleOptions=" + vehicleOptions +
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

    Vehicle vehicle = (Vehicle) o;

    if (id != vehicle.id) {
      return false;
    }
    if (busy != vehicle.busy) {
      return false;
    }
    if (!manufacturer.equals(vehicle.manufacturer)) {
      return false;
    }
    if (!model.equals(vehicle.model)) {
      return false;
    }
    if (!color.equals(vehicle.color)) {
      return false;
    }
    if (!licensePlate.equals(vehicle.licensePlate)) {
      return false;
    }
    return vehicleOptions.equals(vehicle.vehicleOptions);
  }

  @Override
  public int hashCode() {
    int result = (int) (id ^ (id >>> 32));
    result = 31 * result + manufacturer.hashCode();
    result = 31 * result + model.hashCode();
    result = 31 * result + color.hashCode();
    result = 31 * result + licensePlate.hashCode();
    result = 31 * result + (busy ? 1 : 0);
    result = 31 * result + vehicleOptions.hashCode();
    return result;
  }
}
