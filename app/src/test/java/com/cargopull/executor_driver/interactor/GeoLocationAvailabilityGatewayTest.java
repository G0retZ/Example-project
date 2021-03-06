package com.cargopull.executor_driver.interactor;

import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cargopull.executor_driver.GatewayThreadTestRule;
import com.cargopull.executor_driver.backend.geolocation.GeolocationCenter;
import com.cargopull.executor_driver.gateway.GeoLocationAvailabilityGatewayImpl;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import io.reactivex.Flowable;
import io.reactivex.subscribers.TestSubscriber;

@RunWith(MockitoJUnitRunner.class)
public class GeoLocationAvailabilityGatewayTest {

  @ClassRule
  public static final GatewayThreadTestRule classRule = new GatewayThreadTestRule();

  private CommonGateway<Boolean> gateway;

  @Mock
  private GeolocationCenter geolocationCenter;

  @Before
  public void setUp() {
    gateway = new GeoLocationAvailabilityGatewayImpl(geolocationCenter);
    when(geolocationCenter.getLocationsAvailability()).thenReturn(Flowable.never());
  }

  /* Проверяем работу с центром геолокаций */

  /**
   * Должен запросить у центра геолокаций состояния.
   */
  @Test
  public void askGeolocationCenterForLocations() {
    // Action:
    gateway.getData();

    // Effect:
    verify(geolocationCenter, only()).getLocationsAvailability();
  }

  /* Проверяем ответы центра геолокаций */

  /**
   * Должен вернуть строку тепловой карты.
   */
  @Test
  public void answerWithGeoLocationData() {
    // Given:
    when(geolocationCenter.getLocationsAvailability()).thenReturn(Flowable.just(true, false, true));

    // Action:
    TestSubscriber<Boolean> testSubscriber = gateway.getData().test();

    // Effect:
    testSubscriber.assertValues(true, false, true);
    testSubscriber.assertComplete();
  }
}