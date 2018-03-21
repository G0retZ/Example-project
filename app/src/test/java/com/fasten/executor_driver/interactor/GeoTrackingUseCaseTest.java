package com.fasten.executor_driver.interactor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import com.fasten.executor_driver.entity.GeoLocation;
import io.reactivex.Completable;
import io.reactivex.Observable;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class GeoTrackingUseCaseTest {

  private GeoTrackingUseCase geoTrackingUseCase;

  @Mock
  private GeoTrackingGateway gateway;

  @Mock
  private DataReceiver<GeoLocation> geoLocationReceiver;

  @Before
  public void setUp() throws Exception {
    when(gateway.sendGeoLocation(any())).thenReturn(Completable.never());
    when(geoLocationReceiver.get()).thenReturn(Observable.never());
    geoTrackingUseCase = new GeoTrackingUseCaseImpl(gateway, geoLocationReceiver);
  }

  /* Проверяем работу с публикатором геопозиции */

  /**
   * Должен получить смену геопозиции исполнителя.
   *
   * @throws Exception error
   */
  @Test
  public void getExecutorGeoLocations() throws Exception {
    // Действие:
    geoTrackingUseCase.reload().test();

    // Результат:
    verify(geoLocationReceiver, only()).get();
  }

  /**
   * Не должен получать смену геопозиции исполнителя вновь.
   *
   * @throws Exception error
   */
  @Test
  public void doNotGetExecutorGeoLocationsAgain() throws Exception {
    // Действие:
    geoTrackingUseCase.reload().test();
    geoTrackingUseCase.reload().test();

    // Результат:
    verify(geoLocationReceiver, only()).get();
  }

  /**
   * Должен переполучить смену геопозиции исполнителя, если была ошибка.
   *
   * @throws Exception error
   */
  @SuppressWarnings("unchecked")
  @Test
  public void getExecutorGeoLocationsAgainAfterError() throws Exception {
    // Дано:
    when(geoLocationReceiver.get())
        .thenReturn(Observable.error(new Exception()), Observable.never());

    // Действие:
    geoTrackingUseCase.reload().test();

    // Результат:
    verify(geoLocationReceiver, times(2)).get();
  }

  /**
   * Должен переполучить смену геопозиции исполнителя, если передача была завершена.
   *
   * @throws Exception error
   */
  @SuppressWarnings("unchecked")
  @Test
  public void getExecutorGeoLocationsAgainAfterComplete() throws Exception {
    // Дано:
    when(geoLocationReceiver.get())
        .thenReturn(Observable.just(new GeoLocation(1, 2, 3)), Observable.never());

    // Действие:
    geoTrackingUseCase.reload().test();

    // Результат:
    verify(geoLocationReceiver, times(2)).get();
  }

  /* Проверяем работу с гейтвеем в ответ на смену геопозиции */

  /**
   * Должен передать гейтвею новую геопозицию для отправки на сервер.
   *
   * @throws Exception error
   */
  @SuppressWarnings("unchecked")
  @Test
  public void passNewGeoLocationToGateway() throws Exception {
    // Дано:
    when(geoLocationReceiver.get())
        .thenReturn(Observable.just(new GeoLocation(1, 2, 3)), Observable.never());

    // Действие:
    geoTrackingUseCase.reload().test();

    // Результат:
    verify(gateway, only()).sendGeoLocation(new GeoLocation(1, 2, 3));
  }

  /**
   * Не должен трогать гейтвей при ошибках.
   *
   * @throws Exception error
   */
  @SuppressWarnings("unchecked")
  @Test
  public void passNothingToGatewayOnError() throws Exception {
    // Дано:
    when(geoLocationReceiver.get())
        .thenReturn(Observable.error(new Exception()), Observable.never());

    // Действие:
    geoTrackingUseCase.reload().test();

    // Результат:
    verifyZeroInteractions(gateway);
  }
}