package com.cargopull.executor_driver.backend.settings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class AppSettingsServiceTest {

  private final byte[] raw = new byte[]{
      -124, -13, -49, -125, -18, -50, 29, 57, 91, 47, 117, 61, -61, 68, -11, -46
  };
  private final byte[] salt = new byte[]{
      -18, -35, -57, 10, -44, -33, -58, 88, -72, 116, -91, -60, -13, 45, 79, -33
  };
  private AppSettingsService appSettingsService;

  @Before
  public void createService() {
    SharedPreferences preferences = InstrumentationRegistry.getInstrumentation().getTargetContext()
        .getSharedPreferences("settings", Context.MODE_PRIVATE);
    preferences.edit().clear().apply();
    appSettingsService = new AppPreferences(
        InstrumentationRegistry.getInstrumentation().getTargetContext());
  }

  @Test
  public void returnNullValueByDefault() {
    assertNull(appSettingsService.getData("key"));
  }

  @Test
  public void saveAndReadNumber() {
    // given:
    appSettingsService.saveNumber("key", 123456);

    // then:
    assertEquals(appSettingsService.getNumber("key"), 123456);
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
    assertNull(appSettingsService.getEncryptedData(raw, salt, "key"));
  }

  @Test
  public void saveAndReadEncryptedValue() {
    // given:
    appSettingsService.saveEncryptedData(raw, salt, "key", "value");

    // then:
    assertEquals(appSettingsService.getEncryptedData(raw, salt, "key"), "value");
  }

  @Test
  public void saveAndReadEncryptedNullValue() {
    // given:
    appSettingsService.saveEncryptedData(raw, salt, "key", "value");

    // then:
    assertEquals(appSettingsService.getEncryptedData(raw, salt, "key"), "value");
    appSettingsService.saveEncryptedData(raw, salt, "key", null);
    assertNull(appSettingsService.getEncryptedData(raw, salt, "key"));
  }
}