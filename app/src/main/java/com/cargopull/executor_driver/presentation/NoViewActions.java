package com.cargopull.executor_driver.presentation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public abstract class NoViewActions implements ViewActions {

  @Override
  public void blockWithPending(@NonNull String blockerId) {

  }

  @Override
  public void unblockWithPending(@NonNull String blockerId) {

  }

  @Override
  public void setVisible(int id, boolean visible) {

  }

  @Override
  public void setText(int id, @NonNull String text) {

  }

  @Override
  public void setText(int id, int stringId) {

  }

  @Override
  public void setFormattedText(int id, int stringId, Object... formatArgs) {

  }

  @Override
  public void setTextColor(int id, int colorId) {

  }

  @Override
  public void setImage(int id, int drawableId) {

  }

  @Override
  public void setImage(int id, @NonNull String drawableUrl) {

  }

  @Override
  public void dismissDialog() {

  }

  @Override
  public void showPersistentDialog(int stringId, @Nullable Runnable okAction) {

  }

  @Override
  public void showPersistentDialog(@NonNull String message, @Nullable Runnable okAction) {

  }
}
