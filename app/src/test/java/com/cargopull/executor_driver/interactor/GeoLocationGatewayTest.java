package com.cargopull.executor_driver.interactor;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.location.Location;
import com.cargopull.executor_driver.backend.geolocation.GeolocationCenter;
import com.cargopull.executor_driver.backend.web.NoNetworkException;
import com.cargopull.executor_driver.entity.GeoLocation;
import com.cargopull.executor_driver.gateway.GeoLocationGatewayImpl;
import io.reactivex.Flowable;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.TestSubscriber;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class GeoLocationGatewayTest {

  private GeoLocationGateway gateway;

  @Mock
  private GeolocationCenter geolocationCenter;

  @Before
  public void setUp() {
    RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());
    RxJavaPlugins.setSingleSchedulerHandler(scheduler -> Schedulers.trampoline());
    gateway = new GeoLocationGatewayImpl(geolocationCenter);
    when(geolocationCenter.getLocations(anyLong())).thenReturn(Flowable.never());
  }

  /* Проверяем работу с АПИ */

  /**
   * Должен запросить у АПИ тепловую карту.
   */
  @Test
  public void askGeolocationCenterForLocations() {
    // Действие:
    gateway.getGeoLocations(140);

    // Результат:
    verify(geolocationCenter, only()).getLocations(140);
  }

  /* Проверяем правильность потоков (добавить) */

  /* Проверяем ответы на АПИ */

  /**
   * Должен ответить ошибкой сети.
   */
  @Test
  public void answerNoNetworkError() {
    // Действие:
    when(geolocationCenter.getLocations(anyLong()))
        .thenReturn(Flowable.error(new NoNetworkException()));

    // Действие и Результат:
    gateway.getGeoLocations(123).test().assertError(NoNetworkException.class);
  }

  /**
   * Должен вернуть строку тепловой карты.
   */
  @Test
  public void answerWithGeoLocationData() {
    // Дано:
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

    // Действие:
    TestSubscriber<GeoLocation> testSubscriber = gateway.getGeoLocations(34).test();

    // Результат:
    testSubscriber.assertValue(new GeoLocation(123, 456, 789));
    testSubscriber.assertComplete();
  }
}