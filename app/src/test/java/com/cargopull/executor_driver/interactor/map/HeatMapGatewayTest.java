package com.cargopull.executor_driver.interactor.map;

import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cargopull.executor_driver.backend.web.ApiService;
import com.cargopull.executor_driver.backend.web.NoNetworkException;
import com.cargopull.executor_driver.gateway.HeatMapGatewayImpl;
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

  private HeatMapGateway gateway;

  @Mock
  private ApiService api;

  @Before
  public void setUp() {
    RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());
    RxJavaPlugins.setSingleSchedulerHandler(scheduler -> Schedulers.trampoline());
    gateway = new HeatMapGatewayImpl(api);
    when(api.getHeatMap()).thenReturn(Single.never());
  }

  /* Проверяем работу с АПИ */

  /**
   * Должен запросить у АПИ тепловую карту.
   */
  @Test
  public void askGatewayForHeatMap() {
    // Действие:
    gateway.getHeatMap();

    // Результат:
    verify(api, only()).getHeatMap();
  }

  /* Проверяем правильность потоков (добавить) */

  /* Проверяем ответы на АПИ */

  /**
   * Должен ответить ошибкой сети.
   */
  @Test
  public void answerNoNetworkError() {
    // Действие:
    when(api.getHeatMap()).thenReturn(Single.error(new NoNetworkException()));

    // Результат:
    gateway.getHeatMap().test().assertError(NoNetworkException.class);
  }

  /**
   * Должен вернуть строку тепловой карты.
   */
  @Test
  public void answerWithHeatMapData() {
    // Действие:
    when(api.getHeatMap()).thenReturn(Single.just("12"));

    // Результат:
    gateway.getHeatMap().test().assertComplete();
    gateway.getHeatMap().test().assertValue("12");
  }
}