package com.fasten.executor_driver.interactor.vehicle;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.fasten.executor_driver.backend.settings.AppSettingsService;
import com.fasten.executor_driver.entity.Vehicle;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class LastUsedVehicleSharerTest {

  private LastUsedVehicleSharer lastUsedVehicleSharer;

  @Mock
  private AppSettingsService appSettings;

  @Before
  public void setUp() throws Exception {
    lastUsedVehicleSharer = new LastUsedVehicleSharer(appSettings);
  }

  /**
   * Должен запросить у настроек данные по ключу "lastUsedVehicle" сразу же после создания.
   *
   * @throws Exception error
   */
  @Test
  public void askSettingsForLogin() throws Exception {
    // Результат:
    verify(appSettings, only()).getData("lastUsedVehicle");
  }

  /**
   * Не должен запрашивать у настроек данных при подписказ.
   *
   * @throws Exception error
   */
  @Test
  public void doNotAskSettingsForLogin() throws Exception {
    // Действие:
    lastUsedVehicleSharer.get().test();
    lastUsedVehicleSharer.get().test();
    lastUsedVehicleSharer.get().test();
    lastUsedVehicleSharer.get().test();
    lastUsedVehicleSharer.get().test();

    // Результат:
    verify(appSettings, only()).getData("lastUsedVehicle");
  }

  /**
   * Должен запросить у настроек сохранить данные без изменений по ключу "authorizationLogin".
   *
   * @throws Exception error
   */
  @Test
  public void askSettingsForSaveLogin() throws Exception {
    // Действие:
    lastUsedVehicleSharer.share(
        new Vehicle(123456, "manufacturer", "model", "color", "license", false)
    );

    // Результат:
    verify(appSettings).getData("lastUsedVehicle");
    verify(appSettings).saveData(eq("lastUsedVehicle"), eq("123456"));
    verifyNoMoreInteractions(appSettings);
  }

  /**
   * Должен получить значение без изменений.
   *
   * @throws Exception error
   */
  @Test
  public void valueUnchangedForRead() throws Exception {
    // Дано:
    when(appSettings.getData("lastUsedVehicle")).thenReturn("654321");
    lastUsedVehicleSharer = new LastUsedVehicleSharer(appSettings);

    // Результат:
    lastUsedVehicleSharer.get().test().assertValue(
        new Vehicle(654321, "m", "m", "c", "l", false)
    );
  }
}