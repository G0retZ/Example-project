package com.cargopull.executor_driver.interactor.vehicle;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cargopull.executor_driver.backend.settings.AppSettingsService;
import com.cargopull.executor_driver.entity.Vehicle;
import com.cargopull.executor_driver.gateway.LastUsedVehicleGatewayImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class LastUsedVehicleGatewayTest {

  private LastUsedVehicleGateway gateway;

  @Mock
  private AppSettingsService appSettings;

  @Before
  public void setUp() {
    gateway = new LastUsedVehicleGatewayImpl(appSettings);
  }

  /**
   * Должен запросить у настроек данные по ключу "lastUsedVehicle" при первом запросе (лениво).
   */
  @Test
  public void askSettingsForLasUsedVehicleId() {
    // Действие:
    gateway.getLastUsedVehicleId().test().isDisposed();

    // Результат:
    verify(appSettings, only()).getData("lastUsedVehicle");
  }

  /**
   * Не должен запрашивать у настроек данных при повторных запросах.
   */
  @Test
  public void doNotAskSettingsForLasUsedVehicleIdAfterFirstRequest() {
    // Действие:
    gateway.getLastUsedVehicleId().test().isDisposed();
    gateway.getLastUsedVehicleId().test().isDisposed();
    gateway.getLastUsedVehicleId().test().isDisposed();
    gateway.getLastUsedVehicleId().test().isDisposed();
    gateway.getLastUsedVehicleId().test().isDisposed();

    // Результат:
    verify(appSettings, only()).getData("lastUsedVehicle");
  }

  /**
   * Должен запросить у настроек сохранить данные без изменений по ключу "lastUsedVehicle".
   */
  @Test
  public void askSettingsForSaveLasUsedVehicleId() {
    // Действие:
    gateway.saveLastUsedVehicleId(
        new Vehicle(123456, "manufacturer", "model", "color", "license", false)
    ).test().isDisposed();

    // Результат:
    verify(appSettings, only()).saveData(eq("lastUsedVehicle"), eq("123456"));
  }

  /**
   * Не должен запрашивать у настроек данных после сохранения.
   */
  @Test
  public void doNotAskSettingsForLasUsedVehicleIdAfterSave() {
    // Действие:
    gateway.saveLastUsedVehicleId(
        new Vehicle(123456, "manufacturer", "model", "color", "license", false)
    ).test().isDisposed();
    gateway.getLastUsedVehicleId().test().isDisposed();
    gateway.getLastUsedVehicleId().test().isDisposed();
    gateway.getLastUsedVehicleId().test().isDisposed();
    gateway.getLastUsedVehicleId().test().isDisposed();
    gateway.getLastUsedVehicleId().test().isDisposed();

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
    gateway.getLastUsedVehicleId().test().assertValue(654321L);
  }

  /**
   * Должен вернуть -1.
   */
  @Test
  public void returnDefaultValueIfNoSuch() {
    // Действие и Результат:
    gateway.getLastUsedVehicleId().test().assertValue(-1L);
  }

  /**
   * Должен вернуть -1.
   */
  @Test
  public void returnDefaultValueIfEmpty() {
    // Дано:
    when(appSettings.getData("lastUsedVehicle")).thenReturn("");

    // Действие и Результат:
    gateway.getLastUsedVehicleId().test().assertValue(-1L);
  }

  /**
   * Должен вернуть -1.
   */
  @Test
  public void returnDefaultValueIfMalformed() {
    // Дано:
    when(appSettings.getData("lastUsedVehicle")).thenReturn("as32as");

    // Действие и Результат:
    gateway.getLastUsedVehicleId().test().assertValue(-1L);
  }

  /**
   * Должен завершится успешно.
   */
  @Test
  public void returnComplete() {
    // Действие и Результат:
    gateway.saveLastUsedVehicleId(
        new Vehicle(123456, "manufacturer", "model", "color", "license", false)
    ).test().assertComplete();
  }
}