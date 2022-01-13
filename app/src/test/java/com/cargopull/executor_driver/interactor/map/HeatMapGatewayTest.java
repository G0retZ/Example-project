package com.cargopull.executor_driver.interactor.map;

import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cargopull.executor_driver.GatewayThreadTestRule;
import com.cargopull.executor_driver.backend.web.ApiService;
import com.cargopull.executor_driver.backend.web.NoNetworkException;
import com.cargopull.executor_driver.gateway.HeatMapGatewayImpl;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import io.reactivex.Single;

@RunWith(MockitoJUnitRunner.class)
public class HeatMapGatewayTest {

  @ClassRule
  public static final GatewayThreadTestRule classRule = new GatewayThreadTestRule();

  private HeatMapGateway gateway;

  @Mock
  private ApiService api;

  @Before
  public void setUp() {
    gateway = new HeatMapGatewayImpl(api);
    when(api.getHeatMap()).thenReturn(Single.never());
  }

  /* Проверяем работу с АПИ */

  /**
   * Должен запросить у АПИ тепловую карту.
   */
  @Test
  public void askGatewayForHeatMap() {
    // Action:
    gateway.getHeatMap();

    // Effect:
    verify(api, only()).getHeatMap();
  }

  /* Проверяем ответы на АПИ */

  /**
   * Должен ответить ошибкой сети.
   */
  @Test
  public void answerNoNetworkError() {
    // Action:
    when(api.getHeatMap()).thenReturn(Single.error(new NoNetworkException()));

    // Effect:
    gateway.getHeatMap().test().assertError(NoNetworkException.class);
  }

  /**
   * Должен вернуть строку тепловой карты.
   */
  @Test
  public void answerWithHeatMapData() {
    // Action:
    when(api.getHeatMap()).thenReturn(Single.just("12"));

    // Effect:
    gateway.getHeatMap().test().assertComplete();
    gateway.getHeatMap().test().assertValue("12");
  }
}