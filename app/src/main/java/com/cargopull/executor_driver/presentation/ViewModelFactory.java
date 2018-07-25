package com.cargopull.executor_driver.presentation;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;
import javax.inject.Inject;

/**
 * Фабрика создания различных {@link ViewModel}
 *
 * @param <V> класс либо интерфейс, который воплощается классом, наследником от {@link ViewModel}
 */
public class ViewModelFactory<V> implements ViewModelProvider.Factory {

  @NonNull
  private final V viewModel;

  /**
   * Конструктор видимый для внедрения {@link V} зависимости
   *
   * @param viewModel класс либо интерфейс, наследник от {@link ViewModel}.
   */
  @Inject
  public ViewModelFactory(@NonNull V viewModel) {
    this.viewModel = viewModel;
  }

  @SuppressWarnings("unchecked")
  @NonNull
  @Override
  public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
    if (modelClass.isAssignableFrom(viewModel.getClass())) {
      return (T) viewModel;
    }
    throw new IllegalArgumentException("Unknown class name");
  }
}
