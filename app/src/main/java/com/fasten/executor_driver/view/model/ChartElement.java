package com.fasten.executor_driver.view.model;

public class ChartElement {

  private float value;
  private int sectorColor;
  private int textColor;
  private boolean valueVisible;

  public ChartElement(float value, int sectorColor) {
    this(value, sectorColor, 0xffFFFFFF, true);
  }

  @SuppressWarnings("unused")
  public ChartElement(float value, int sectorColor, int textColor) {
    this(value, sectorColor, textColor, true);
  }

  public ChartElement(float value, int sectorColor, boolean valueVisible) {
    this(value, sectorColor, 0xffFFFFFF, valueVisible);
  }

  @SuppressWarnings("WeakerAccess")
  public ChartElement(float value, int sectorColor, int textColor, boolean valueVisible) {
    this.value = value;
    this.sectorColor = sectorColor;
    this.textColor = textColor;
    this.valueVisible = valueVisible;
  }

  public float getValue() {
    return value;
  }

  public void setValue(float value) {
    this.value = value;
  }

  public int getSectorColor() {
    return sectorColor;
  }

  @SuppressWarnings("unused")
  public void setSectorColor(int sectorColor) {
    this.sectorColor = sectorColor;
  }

  public int getTextColor() {
    return textColor;
  }

  @SuppressWarnings("unused")
  public void setTextColor(int textColor) {
    this.textColor = textColor;
  }

  public boolean isValueVisible() {
    return valueVisible;
  }

  @SuppressWarnings("unused")
  public void setValueVisible(boolean valueVisible) {
    this.valueVisible = valueVisible;
  }
}