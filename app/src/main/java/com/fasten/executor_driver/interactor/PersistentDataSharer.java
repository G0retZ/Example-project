package com.fasten.executor_driver.interactor;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasten.executor_driver.backend.settings.AppSettingsService;
import io.reactivex.Observable;

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
    D data = deserialize(appSettingsService.getData(getKey()));
    if (data != null) {
      super.share(data);
    }
  }

  @Override
  public void share(@Nullable D data) {
    appSettingsService.saveData(getKey(), serialize(data));
    super.share(data);
  }

  @NonNull
  @Override
  public Observable<D> get() {
    return super.get();
  }

  /**
   * везвращает ключ для данных в настройках
   *
   * @return ключ
   */
  @SuppressWarnings("SameReturnValue")
  @NonNull
  protected abstract String getKey();

  /**
   * Сериализует данные в строку
   * @param data данные
   * @return сериализованные данные строкой
   */
  @Nullable
  protected abstract String serialize(D data);

  /**
   * Десериализует строку в данные
   * @param string строка
   * @return данные десериализованные из строки
   */
  @Nullable
  protected abstract D deserialize(@Nullable String string);
}
