package com.fasten.executor_driver.interactor.map;

import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasten.executor_driver.backend.web.ApiService;
import com.fasten.executor_driver.backend.web.NoNetworkException;
import com.fasten.executor_driver.gateway.HeatMapGatewayImpl;
import io.reactivex.Single;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class HeatMapGatewayTest {

  private HeatMapGateway heatMapGateway;

  @Mock
  private ApiService api;

  @Before
  public void setUp() throws Exception {
    RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());
    RxJavaPlugins.setSingleSchedulerHandler(scheduler -> Schedulers.trampoline());
    heatMapGateway = new HeatMapGatewayImpl(api);
    when(api.getHeatMap()).thenReturn(Single.never());
  }

	/* Проверяем работу с АПИ */

  /**
   * Должен запросить у АПИ тепловую карту
   *
   * @throws Exception error
   */
  @Test
  public void authCompletableRequested() throws Exception {
    // Действие:
    heatMapGateway.getHeatMap();

    // Результат:
    verify(api, only()).getHeatMap();
  }

	/* Проверяем правильность потоков (добавить) */

	/* Проверяем ответы на АПИ */

  /**
   * Должен ответить ошибкой сети
   *
   * @throws Exception error
   */
  @Test
  public void answerNoNetworkError() throws Exception {
    // Действие:
    when(api.getHeatMap()).thenReturn(Single.error(new NoNetworkException()));

    // Результат:
    heatMapGateway.getHeatMap().test().assertError(NoNetworkException.class);
  }

  /**
   * Должен вернуть строку тепловой карты
   *
   * @throws Exception error
   */
  @Test
  public void answerAuthSuccessful() throws Exception {
    // Действие:
    when(api.getHeatMap()).thenReturn(Single.just("12"));

    // Результат:
    heatMapGateway.getHeatMap().test().assertComplete();
    heatMapGateway.getHeatMap().test().assertValue("12");
  }
}