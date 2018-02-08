package com.fasten.executor_driver.interactor.online;

import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasten.executor_driver.backend.web.NoNetworkException;
import io.reactivex.Completable;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class OnlineUseCaseTest {

  private OnlineUseCase loginUseCase;

  @Mock
  private OnlineGateway gateway;

  @Before
  public void setUp() throws Exception {
    when(gateway.goOnline()).thenReturn(Completable.never());
    loginUseCase = new OnlineUseCaseImpl(gateway);
  }

	/* Проверяем работу с гейтвеем */

  /**
   * Должен запросить у гейтвея выход на линию
   *
   * @throws Exception error
   */
  @Test
  public void askGatewayForAuth() throws Exception {
    // Действие:
    loginUseCase.goOnline().test();

    // Результат:
    verify(gateway, only()).goOnline();
  }

	/* Проверяем ответы на запрос выхода на линию */

  /**
   * Должен ответить ошибкой сети
   *
   * @throws Exception error
   */
  @Test
  public void answerNoNetworkError() throws Exception {
    // Дано:
    when(gateway.goOnline()).thenReturn(Completable.error(new NoNetworkException()));

    // Действие и Результат:
    loginUseCase.goOnline().test().assertError(NoNetworkException.class);
  }

  /**
   * Должен ответить успехом
   *
   * @throws Exception error
   */
  @Test
  public void answerAuthSuccessful() throws Exception {
    // Дано:
    when(gateway.goOnline()).thenReturn(Completable.complete());

    // Действие и Результат:
    loginUseCase.goOnline().test().assertComplete();
  }
}