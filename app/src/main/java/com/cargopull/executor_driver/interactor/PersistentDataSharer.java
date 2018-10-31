package com.cargopull.executor_driver.interactor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.cargopull.executor_driver.backend.settings.AppSettingsService;

/**
 * Для обмена данными типа {@link D} в слое ЮзКейсов с сохранением их независимо от жизни процесса.
 *
 * @param <D> - тип данных
 */
public abstract class PersistentDataSharer<D> extends MemoryDataSharer<D> {

  @NonNull
  private final AppSettingsService appSettingsService;

  protected PersistentDataSharer(@NonNull AppSettingsService appSettingsService) {
    super();
    this.appSettingsService = appSettingsService;
    String value = appSettingsService.getData(getKey());
    if (value != null) {
      super.onNext(deserialize(value));
    }
  }

  @Override
  public void onNext(@NonNull D data) {
    appSettingsService.saveData(getKey(), serialize(data));
    super.onNext(data);
  }

  /**
   * возвращает ключ для данных в настройках.
   *
   * @return ключ
   */
  @SuppressWarnings("SameReturnValue")
  @NonNull
  protected abstract String getKey();

  /**
   * Сериализует данные в строку.
   *
   * @param data данные
   * @return сериализованные данные строкой
   */
  @Nullable
  protected abstract String serialize(D data);

  /**
   * Десериализует строку в данные.
   *
   * @param string строка
   * @return данные десериализованные из строки
   */
  @NonNull
  protected abstract D deserialize(@NonNull String string);
}
