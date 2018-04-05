package com.fasten.executor_driver.interactor.vehicle;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasten.executor_driver.backend.settings.AppSettingsService;
import com.fasten.executor_driver.entity.Vehicle;
import com.fasten.executor_driver.gateway.LastUsedVehicleGatewayImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class LastUsedVehicleGatewayTest {

  private LastUsedVehicleGateway lastUsedVehicleGateway;

  @Mock
  private AppSettingsService appSettings;

  @Before
  public void setUp() {
    lastUsedVehicleGateway = new LastUsedVehicleGatewayImpl(appSettings);
  }

  /**
   * Должен запросить у настроек данные по ключу "lastUsedVehicle" при первом запросе (лениво).
   */
  @Test
  public void askSettingsForLasUsedVehicleId() {
    // Действие:
    lastUsedVehicleGateway.getLastUsedVehicleId().test();

    // Результат:
    verify(appSettings, only()).getData("lastUsedVehicle");
  }

  /**
   * Не должен запрашивать у настроек данных при повторных запросах.
   */
  @Test
  public void doNotAskSettingsForLasUsedVehicleIdAfterFirstRequest() {
    // Действие:
    lastUsedVehicleGateway.getLastUsedVehicleId().test();
    lastUsedVehicleGateway.getLastUsedVehicleId().test();
    lastUsedVehicleGateway.getLastUsedVehicleId().test();
    lastUsedVehicleGateway.getLastUsedVehicleId().test();
    lastUsedVehicleGateway.getLastUsedVehicleId().test();

    // Результат:
    verify(appSettings, only()).getData("lastUsedVehicle");
  }

  /**
   * Должен запросить у настроек сохранить данные без изменений по ключу "lastUsedVehicle".
   */
  @Test
  public void askSettingsForSaveLasUsedVehicleId() {
    // Действие:
    lastUsedVehicleGateway.saveLastUsedVehicleId(
        new Vehicle(123456, "manufacturer", "model", "color", "license", false)
    ).test();

    // Результат:
    verify(appSettings, only()).saveData(eq("lastUsedVehicle"), eq("123456"));
  }

  /**
   * Не должен запрашивать у настроек данных после сохранения.
   */
  @Test
  public void doNotAskSettingsForLasUsedVehicleIdAfterSave() {
    // Действие:
    lastUsedVehicleGateway.saveLastUsedVehicleId(
        new Vehicle(123456, "manufacturer", "model", "color", "license", false)
    ).test();
    lastUsedVehicleGateway.getLastUsedVehicleId().test();
    lastUsedVehicleGateway.getLastUsedVehicleId().test();
    lastUsedVehicleGateway.getLastUsedVehicleId().test();
    lastUsedVehicleGateway.getLastUsedVehicleId().test();
    lastUsedVehicleGateway.getLastUsedVehicleId().test();

    // Результат:
    verify(appSettings, only()).saveData(eq("lastUsedVehicle"), eq("123456"));
  }

  /**
   * Должен вернуть значение без изменений.
   */
  @Test
  public void returnValueUnchanged() {
    // Дано:
    when(appSettings.getData("lastUsedVehicle")).thenReturn("654321");

    // Действие и Результат:
    lastUsedVehicleGateway.getLastUsedVehicleId().test().assertValue(new Long(654321));
  }

  /**
   * Должен вернуть -1.
   */
  @Test
  public void returnDefaultValueIfNoSuch() {
    // Действие и Результат:
    lastUsedVehicleGateway.getLastUsedVehicleId().test().assertValue(new Long(-1));
  }

  /**
   * Должен вернуть -1.
   */
  @Test
  public void returnDefaultValueIfEmpty() {
    // Дано:
    when(appSettings.getData("lastUsedVehicle")).thenReturn("");

    // Действие и Результат:
    lastUsedVehicleGateway.getLastUsedVehicleId().test().assertValue(new Long(-1));
  }

  /**
   * Должен вернуть -1.
   */
  @Test
  public void returnDefaultValueIfMalformed() {
    // Дано:
    when(appSettings.getData("lastUsedVehicle")).thenReturn("as32as");

    // Действие и Результат:
    lastUsedVehicleGateway.getLastUsedVehicleId().test().assertValue(new Long(-1));
  }

  /**
   * Должен завершится успешно.
   */
  @Test
  public void returnComplete() {
    // Действие и Результат:
    lastUsedVehicleGateway.saveLastUsedVehicleId(
        new Vehicle(123456, "manufacturer", "model", "color", "license", false)
    ).test().assertComplete();
  }
}