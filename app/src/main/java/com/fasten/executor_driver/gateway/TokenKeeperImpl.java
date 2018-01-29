package com.fasten.executor_driver.gateway;

import android.support.annotation.NonNull;

import com.fasten.executor_driver.backend.settings.AppSettingsService;
import com.fasten.executor_driver.backend.web.TokenKeeper;

public class TokenKeeperImpl implements TokenKeeper {

	private static final String TOKEN = "token";
	private static final byte[] key = {
			14, -55, -48, 7, -65, -87, -23, 113, -69, -22, -68, -24, -96, 103, 16, 114
	};

	@NonNull
	private final AppSettingsService appSettingsService;

	public TokenKeeperImpl(@NonNull AppSettingsService appSettingsService) {
		this.appSettingsService = appSettingsService;
	}

	@Override
	public void saveToken(String token) {
		appSettingsService.saveEncryptedData(key, TOKEN, token);
	}

	@Override
	public String getToken() {
		return appSettingsService.getEncryptedData(key, TOKEN);
	}
}
