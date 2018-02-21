package com.fasten.executor_driver.backend.web;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasten.executor_driver.backend.settings.AppSettingsService;
import com.fasten.executor_driver.gateway.TokenKeeperImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class TokenKeeperTest {

  private TokenKeeper tokenKeeper;

  @Mock
  private AppSettingsService appSettings;

  @Captor
  private ArgumentCaptor<byte[]> rawCaptor;

  @Before
  public void setUp() throws Exception {
    tokenKeeper = new TokenKeeperImpl(appSettings);
  }

  /**
   * Должен запросить у настроек данные по ключу "token"
   *
   * @throws Exception error
   */
  @Test
  public void askSettingsForToken() throws Exception {
    // Действие:
    tokenKeeper.getToken();

    // Результат:
    verify(appSettings, only()).getEncryptedData(any(byte[].class), eq("token"));
  }

  /**
   * Должен запросить у настроек сохранить данные без изменений по ключу "token"
   *
   * @throws Exception error
   */
  @Test
  public void askSettingsForSaveToken() throws Exception {
    // Действие:
    tokenKeeper.saveToken("123456");

    // Результат:
    verify(appSettings, only()).saveEncryptedData(any(byte[].class), eq("token"), eq("123456"));
  }

  /**
   * Должен использовать один и тот же массив байтов шифрования для чтения и записи.
   *
   * @throws Exception error
   */
  @Test
  public void sameBytesForEncryption() throws Exception {
    // Действие:
    tokenKeeper.saveToken("123456");
    tokenKeeper.getToken();

    // Результат:
    verify(appSettings).saveEncryptedData(rawCaptor.capture(), eq("token"), eq("123456"));
    verify(appSettings).getEncryptedData(rawCaptor.capture(), eq("token"));
    assertEquals(rawCaptor.getAllValues().get(0), rawCaptor.getAllValues().get(1));
  }

  /**
   * Должен получить значение без изменений.
   *
   * @throws Exception error
   */
  @Test
  public void valueUnchangedForRead() throws Exception {
    // Действие:
    when(appSettings.getEncryptedData(any(byte[].class), eq("token"))).thenReturn("654321");

    // Результат:
    assertEquals(tokenKeeper.getToken(), "654321");
  }
}