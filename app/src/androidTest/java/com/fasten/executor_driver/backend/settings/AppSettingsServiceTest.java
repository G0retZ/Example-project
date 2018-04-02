package com.fasten.executor_driver.backend.settings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class AppSettingsServiceTest {

  private final byte[] raw = new byte[]{
      -124, -13, -49, -125, -18, -50, 29, 57, 91, 47, 117, 61, -61, 68, -11, -46
  };
  private AppSettingsService appSettingsService;

  @Before
  public void createService() {
    appSettingsService = new AppPreferences(InstrumentationRegistry.getTargetContext(), true);
  }

  @Test
  public void returnNullValueByDefault() {
    assertNull(appSettingsService.getData("key"));
  }

  @Test
  public void saveAndReadValue() {
    // given:
    appSettingsService.saveData("key", "value");

    // then:
    assertEquals(appSettingsService.getData("key"), "value");
  }

  @Test
  public void saveAndReadNullValue() {
    // given:
    appSettingsService.saveData("key1", "value1");

    // then:
    assertEquals(appSettingsService.getData("key1"), "value1");
    appSettingsService.saveData("key1", null);
    assertNull(appSettingsService.getData("key1"));
  }

  @Test
  public void returnNullEncryptedValueByDefault() {
    assertNull(appSettingsService.getEncryptedData(raw, "key"));
  }

  @Test
  public void saveAndReadEncryptedValue() {
    // given:
    appSettingsService.saveEncryptedData(raw, "key", "value");

    // then:
    assertEquals(appSettingsService.getEncryptedData(raw, "key"), "value");
  }

  @Test
  public void saveAndReadEncryptedNullValue() {
    // given:
    appSettingsService.saveEncryptedData(raw, "key", "value");

    // then:
    assertEquals(appSettingsService.getEncryptedData(raw, "key"), "value");
    appSettingsService.saveEncryptedData(raw, "key", null);
    assertNull(appSettingsService.getEncryptedData(raw, "key"));
  }
}