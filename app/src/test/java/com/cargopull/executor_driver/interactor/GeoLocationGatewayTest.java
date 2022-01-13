package com.cargopull.executor_driver.interactor;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.location.Location;

import com.cargopull.executor_driver.GatewayThreadTestRule;
import com.cargopull.executor_driver.backend.geolocation.GeolocationCenter;
import com.cargopull.executor_driver.backend.web.NoNetworkException;
import com.cargopull.executor_driver.entity.GeoLocation;
import com.cargopull.executor_driver.gateway.GeoLocationGatewayImpl;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import io.reactivex.Flowable;
import io.reactivex.subscribers.TestSubscriber;

@RunWith(MockitoJUnitRunner.class)
public class GeoLocationGatewayTest {

  @ClassRule
  public static final GatewayThreadTestRule classRule = new GatewayThreadTestRule();

  private GeoLocationGateway gateway;

  @Mock
  private GeolocationCenter geolocationCenter;

  @Before
  public void setUp() {
    gateway = new GeoLocationGatewayImpl(geolocationCenter);
    when(geolocationCenter.getLocations(anyLong())).thenReturn(Flowable.never());
  }

  /* Проверяем работу с центром геолокаций */

  /**
   * Должен запросить у центра геолокаций геопозиции.
   */
  @Test
  public void askGeolocationCenterForLocations() {
    // Action:
    gateway.getGeoLocations(140);

    // Effect:
    verify(geolocationCenter, only()).getLocations(140);
  }

  /* Проверяем ответы центра геолокаций */

  /**
   * Должен ответить ошибкой сети.
   */
  @Test
  public void answerNoNetworkError() {
    // Action:
    when(geolocationCenter.getLocations(anyLong()))
        .thenReturn(Flowable.error(new NoNetworkException()));

    // Action и Effect:
    gateway.getGeoLocations(123).test().assertError(NoNetworkException.class);
  }

  /**
   * Должен вернуть геопозицию.
   */
  @Test
  public void answerWithGeoLocationData() {
    // Given:
    Location location = new Location("flp") {
      double latitude, longitude;
      long time;

      @Override
      public double getLatitude() {
        return latitude;
      }

      @Override
      public void setLatitude(double latitude) {
        this.latitude = latitude;
      }

      @Override
      public double getLongitude() {
        return longitude;
      }

      @Override
      public void setLongitude(double longitude) {
        this.longitude = longitude;
      }

      @Override
      public long getTime() {
        return time;
      }

      @Override
      public void setTime(long time) {
        this.time = time;
      }
    };
    location.setLatitude(123);
    location.setLongitude(456);
    location.setTime(789);
    when(geolocationCenter.getLocations(anyLong())).thenReturn(Flowable.just(location));

    // Action:
    TestSubscriber<GeoLocation> testSubscriber = gateway.getGeoLocations(34).test();

    // Effect:
    testSubscriber.assertValue(new GeoLocation(123, 456, 789));
    testSubscriber.assertComplete();
  }
}