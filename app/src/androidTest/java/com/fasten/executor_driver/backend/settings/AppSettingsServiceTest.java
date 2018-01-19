package com.fasten.executor_driver.backend.settings;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(AndroidJUnit4.class)
public class AppSettingsServiceTest {

	private AppSettingsService appSettingsService;
	private final byte[] raw = new byte[]{
			-124, -13, -49, -125, -18, -50, 29, 57, 91, 47, 117, 61, -61, 68, -11, -46
	};

	@Before
	public void createService() {
		appSettingsService = new AppPreferences(InstrumentationRegistry.getTargetContext(), true);
	}

	@Test
	public void returnNullValueByDefault() throws Exception {
		assertNull(appSettingsService.getData("key"));
	}

	@Test
	public void saveAndReadValue() throws Exception {
		// given:
		appSettingsService.saveData("key", "value");

		// then:
		assertEquals(appSettingsService.getData("key"), "value");
	}

	@Test
	public void saveAndReadNullValue() throws Exception {
		// given:
		appSettingsService.saveData("key", "value");

		// then:
		assertEquals(appSettingsService.getData("key"), "value");
		appSettingsService.saveData("key", null);
		assertNull(appSettingsService.getData("key"));
	}

	@Test
	public void returnNullEncryptedValueByDefault() throws Exception {
		assertNull(appSettingsService.getEncryptedData(raw, "key"));
	}

	@Test
	public void saveAndReadEncryptedValue() throws Exception {
		// given:
		appSettingsService.saveEncryptedData(raw, "key", "value");

		// then:
		assertEquals(appSettingsService.getEncryptedData(raw, "key"), "value");
	}

	@Test
	public void saveAndReadEncryptedNullValue() throws Exception {
		// given:
		appSettingsService.saveEncryptedData(raw, "key", "value");

		// then:
		assertEquals(appSettingsService.getEncryptedData(raw, "key"), "value");
		appSettingsService.saveEncryptedData(raw, "key", null);
		assertNull(appSettingsService.getEncryptedData(raw, "key"));
	}

}