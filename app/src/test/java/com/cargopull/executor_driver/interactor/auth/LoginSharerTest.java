package com.cargopull.executor_driver.interactor.auth;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.cargopull.executor_driver.backend.settings.AppSettingsService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class LoginSharerTest {

  private LoginSharer loginSharer;

  @Mock
  private AppSettingsService appSettings;

  @Before
  public void setUp() {
    loginSharer = new LoginSharer(appSettings);
  }

  /**
   * Должен запросить у настроек данные по ключу "authorizationLogin" сразу же после создания.
   */
  @Test
  public void askSettingsForLogin() {
    // Результат:
    verify(appSettings, only()).getData("authorizationLogin");
  }

  /**
   * Не должен запрашивать у настроек данных при подписказ.
   */
  @Test
  public void doNotAskSettingsForLogin() {
    // Действие:
    loginSharer.get().test().isDisposed();
    loginSharer.get().test().isDisposed();
    loginSharer.get().test().isDisposed();
    loginSharer.get().test().isDisposed();
    loginSharer.get().test().isDisposed();

    // Результат:
    verify(appSettings, only()).getData("authorizationLogin");
  }

  /**
   * Должен запросить у настроек сохранить данные без изменений по ключу "authorizationLogin".
   */
  @Test
  public void askSettingsForSaveLogin() {
    // Действие:
    loginSharer.onNext("123456");

    // Результат:
    verify(appSettings).getData("authorizationLogin");
    verify(appSettings).saveData(eq("authorizationLogin"), eq("123456"));
    verifyNoMoreInteractions(appSettings);
  }

  /**
   * Должен получить значение без изменений.
   */
  @Test
  public void valueUnchangedForRead() {
    // Дано:
    when(appSettings.getData("authorizationLogin")).thenReturn("654321");
    loginSharer = new LoginSharer(appSettings);

    // Результат:
    loginSharer.get().test().assertValue("654321");
  }
}