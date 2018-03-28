package com.fasten.executor_driver.interactor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import com.fasten.executor_driver.backend.websocket.ConnectionClosedException;
import com.fasten.executor_driver.entity.ExecutorState;
import com.fasten.executor_driver.entity.GeoLocation;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.functions.Action;
import io.reactivex.subscribers.TestSubscriber;
import java.net.ConnectException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class GeoLocationUseCaseTest {

  private GeoLocationUseCase geoLocationUseCase;

  @Mock
  private GeoLocationGateway geoLocationGateway;

  @Mock
  private GeoTrackingGateway geoTrackingGateway;

  @Mock
  private ExecutorStateUseCase executorStateUseCase;

  @Mock
  private Action action;

  @Before
  public void setUp() throws Exception {
    when(geoLocationGateway.getGeoLocations(anyLong())).thenReturn(Flowable.never());
    when(geoTrackingGateway.sendGeoLocation(any())).thenReturn(Completable.complete());
    when(executorStateUseCase.getExecutorStates()).thenReturn(Flowable.never());
    geoLocationUseCase = new GeoLocationUseCaseImpl(
        geoLocationGateway, geoTrackingGateway, executorStateUseCase
    );
  }

  /* Проверяем работу с юзкейсом состояний */

  /**
   * Должен запросить получение смены состояний исполнителя.
   *
   * @throws Exception error
   */
  @Test
  public void getExecutorStates() throws Exception {
    // Действие:
    geoLocationUseCase.getGeoLocations().test();

    // Результат:
    verify(executorStateUseCase, only()).getExecutorStates();
  }

  /**
   * Не должен запрашивать получение смены состояний исполнителя вновь, если ошибок не было.
   *
   * @throws Exception error
   */
  @Test
  public void getExecutorStatesAgainAfterReload() throws Exception {
    // Действие:
    geoLocationUseCase.getGeoLocations().test();
    geoLocationUseCase.getGeoLocations().test();

    // Результат:
    verify(executorStateUseCase, only()).getExecutorStates();
  }

  /**
   * Должен запросить получение смены состояний исполнителя вновь, после ошибки.
   *
   * @throws Exception error
   */
  @Test
  public void getExecutorStatesAgainAfterError() throws Exception {
    // Дано:
    when(executorStateUseCase.getExecutorStates())
        .thenReturn(Flowable.error(new ConnectionClosedException()));

    // Действие:
    geoLocationUseCase.getGeoLocations().test();
    geoLocationUseCase.getGeoLocations().test();

    // Результат:
    verify(executorStateUseCase, times(2)).getExecutorStates();
    verifyNoMoreInteractions(executorStateUseCase);
  }

  /**
   * Должен запросить получение смены состояний исполнителя вновь, после окончания.
   *
   * @throws Exception error
   */
  @Test
  public void getExecutorStatesAgainAfterComplete() throws Exception {
    // Дано:
    when(executorStateUseCase.getExecutorStates()).thenReturn(Flowable.empty());

    // Действие:
    geoLocationUseCase.getGeoLocations().test();
    geoLocationUseCase.getGeoLocations().test();

    // Результат:
    verify(executorStateUseCase, times(2)).getExecutorStates();
    verifyNoMoreInteractions(executorStateUseCase);
  }

  /* Проверяем работу с гейтвеем геолокации в ответ на смену состояний */

  /**
   * Не должен трогать гейтвей, до получения статуса.
   *
   * @throws Exception error
   */
  @Test
  public void doNotTouchGatewayIfNoStatus() throws Exception {
    // Действие:
    geoLocationUseCase.getGeoLocations().test();

    // Результат:
    verifyZeroInteractions(geoLocationGateway);
  }

  /**
   * Не должен трогать гейтвей, при ошибке в статусах.
   *
   * @throws Exception error
   */
  @Test
  public void doNotTouchGatewayIfStatusError() throws Exception {
    // Дано:
    when(executorStateUseCase.getExecutorStates())
        .thenReturn(Flowable.error(new ConnectionClosedException()));

    // Действие:
    geoLocationUseCase.getGeoLocations().test();

    // Результат:
    verifyZeroInteractions(geoLocationGateway);
  }

  /**
   * Не должен трогать гейтвей, при завершении в статусах.
   *
   * @throws Exception error
   */
  @Test
  public void doNotTouchGatewayIfStatusComplete() throws Exception {
    // Дано:
    when(executorStateUseCase.getExecutorStates()).thenReturn(Flowable.empty());

    // Действие:
    geoLocationUseCase.getGeoLocations().test();

    // Результат:
    verifyZeroInteractions(geoLocationGateway);
  }

  /**
   * Должен запросить гейтвей получать локации с интервалом 1 час,
   * при переходе в состояние "Смена закрыта".
   *
   * @throws Exception error
   */
  @Test
  public void askGatewayForLocationsEvery1HourIfGoToShiftClosed() throws Exception {
    // Дано:
    when(executorStateUseCase.getExecutorStates())
        .thenReturn(Flowable.just(ExecutorState.SHIFT_CLOSED));

    // Действие:
    geoLocationUseCase.getGeoLocations().test();

    // Результат:
    verify(geoLocationGateway, only()).getGeoLocations(3600000);
  }

  /**
   * Должен запросить гейтвей получать локации с интервалом 180 сек,
   * при переходе в состояние "Смена открыта".
   *
   * @throws Exception error
   */
  @Test
  public void askGatewayForLocationsEvery180secIfGoToShiftOpened() throws Exception {
    // Дано:
    when(executorStateUseCase.getExecutorStates())
        .thenReturn(Flowable.just(ExecutorState.SHIFT_OPENED));

    // Действие:
    geoLocationUseCase.getGeoLocations().test();

    // Результат:
    verify(geoLocationGateway, only()).getGeoLocations(180000);
  }

  /**
   * Должен запросить гейтвей получать локации с интервалом 15 сек,
   * при переходе в состояние "На линии".
   *
   * @throws Exception error
   */
  @Test
  public void askGatewayForLocationsEvery15secIfGoToOnline() throws Exception {
    // Дано:
    when(executorStateUseCase.getExecutorStates()).thenReturn(Flowable.just(ExecutorState.ONLINE));

    // Действие:
    geoLocationUseCase.getGeoLocations().test();

    // Результат:
    verify(geoLocationGateway, only()).getGeoLocations(15000);
  }

  /**
   * Должен запросить гейтвей получать локации с различным интервалом, при смене состояний.
   *
   * @throws Exception error
   */
  @Test
  public void askGatewayForLocationsDependingOnNewStatesArrival() throws Exception {
    // Дано:
    InOrder inOrder = Mockito.inOrder(geoLocationGateway);
    when(executorStateUseCase.getExecutorStates()).thenReturn(Flowable.just(
        ExecutorState.SHIFT_CLOSED, ExecutorState.SHIFT_OPENED, ExecutorState.ONLINE,
        ExecutorState.SHIFT_OPENED, ExecutorState.SHIFT_CLOSED
    ));

    // Действие:
    geoLocationUseCase.getGeoLocations().test();

    // Результат:
    inOrder.verify(geoLocationGateway).getGeoLocations(3600000);
    inOrder.verify(geoLocationGateway).getGeoLocations(180000);
    inOrder.verify(geoLocationGateway).getGeoLocations(15000);
    inOrder.verify(geoLocationGateway).getGeoLocations(180000);
    inOrder.verify(geoLocationGateway).getGeoLocations(3600000);
    verifyNoMoreInteractions(geoLocationGateway);
  }

  /**
   * Должен запросить гейтвей получать локации с различным интервалом, при смене состояний.
   *
   * @throws Exception error
   */
  @Test
  public void disposePreviousGatewayQueriesOnNewStatesArrival() throws Exception {
    // Дано:
    when(executorStateUseCase.getExecutorStates()).thenReturn(Flowable.just(
        ExecutorState.SHIFT_CLOSED, ExecutorState.SHIFT_OPENED, ExecutorState.ONLINE,
        ExecutorState.SHIFT_OPENED, ExecutorState.SHIFT_CLOSED
    ));
    when(geoLocationGateway.getGeoLocations(anyLong()))
        .thenReturn(Flowable.<GeoLocation>never().doOnCancel(action));

    // Действие:
    geoLocationUseCase.getGeoLocations().test();

    // Результат:
    verify(action, times(4)).run();
  }

  /* Проверяем работу с гейтвеем отправки геопозиции в ответ на ответы гейтвея геопозиции */

  /**
   * Не должен трогать гейтвей передачи геолокаций до получения геолокаций.
   *
   * @throws Exception error
   */
  @Test
  public void doNotTouchTrackingGatewayOnEmptyStates() throws Exception {
    // Дано:
    when(executorStateUseCase.getExecutorStates()).thenReturn(Flowable.empty());

    // Действие:
    geoLocationUseCase.getGeoLocations().test();

    // Результат:
    verifyZeroInteractions(geoTrackingGateway);
  }

  /**
   * Должен отправить полученную геопозицию через гейтвей передачи геолокаций.
   *
   * @throws Exception error
   */
  @Test
  public void askTrackingGatewayToSendNewGeoLocation() throws Exception {
    // Дано:
    when(executorStateUseCase.getExecutorStates())
        .thenReturn(Flowable.just(ExecutorState.SHIFT_CLOSED));
    when(geoLocationGateway.getGeoLocations(anyLong()))
        .thenReturn(Flowable.just(new GeoLocation(1, 2, 3)));

    // Действие:
    geoLocationUseCase.getGeoLocations().test();

    // Результат:
    verify(geoTrackingGateway, only()).sendGeoLocation(new GeoLocation(1, 2, 3));
  }

  /**
   * Не должен трогать гейтвей передачи геолокаций при ошибке получения статусов.
   *
   * @throws Exception error
   */
  @Test
  public void doNotTouchTrackingGatewayOnGetStateError() throws Exception {
    // Дано:
    when(executorStateUseCase.getExecutorStates())
        .thenReturn(Flowable.error(ConnectException::new));

    // Действие:
    geoLocationUseCase.getGeoLocations().test();

    // Результат:
    verifyZeroInteractions(geoTrackingGateway);
  }

  /**
   * Не должен трогать гейтвей передачи геолокаций при ошибке получения геолокаций.
   *
   * @throws Exception error
   */
  @Test
  public void doNotTouchTrackingGatewayOnGetGeolocationError() throws Exception {
    // Дано:
    when(executorStateUseCase.getExecutorStates())
        .thenReturn(Flowable.just(ExecutorState.SHIFT_CLOSED));
    when(geoLocationGateway.getGeoLocations(anyLong())).thenReturn(Flowable.error(new Exception()));

    // Действие:
    geoLocationUseCase.getGeoLocations().test();

    // Результат:
    verifyZeroInteractions(geoTrackingGateway);
  }

  /* Проверяем ответы гейтвея геопозиции */

  /**
   * Должен вернуть полученные геопозиции.
   *
   * @throws Exception error
   */
  @Test
  public void answerWithNewGeoLocations() throws Exception {
    // Дано:
    when(executorStateUseCase.getExecutorStates())
        .thenReturn(Flowable.just(ExecutorState.SHIFT_CLOSED));
    when(geoLocationGateway.getGeoLocations(anyLong())).thenReturn(Flowable.just(
        new GeoLocation(1, 2, 3),
        new GeoLocation(4, 5, 6),
        new GeoLocation(7, 8, 9)
    ));

    // Действие:
    TestSubscriber<GeoLocation> testSubscriber = geoLocationUseCase.getGeoLocations().test();

    // Результат:
    testSubscriber.assertValues(
        new GeoLocation(1, 2, 3),
        new GeoLocation(4, 5, 6),
        new GeoLocation(7, 8, 9)
    );
    testSubscriber.assertNoErrors();
    testSubscriber.assertComplete();
  }

  /**
   * Должен вернуть ошибку при ошибке получения статусов.
   *
   * @throws Exception error
   */
  @Test
  public void answerWithErrorOnGetStateError() throws Exception {
    // Дано:
    when(executorStateUseCase.getExecutorStates())
        .thenReturn(Flowable.error(ConnectException::new));

    // Действие:
    TestSubscriber<GeoLocation> testSubscriber = geoLocationUseCase.getGeoLocations().test();

    // Результат:
    testSubscriber.assertNoValues();
    testSubscriber.assertNoErrors();
    testSubscriber.assertComplete();
  }

  /**
   * Должен вернуть ошибку при ошибке получения местоположений.
   *
   * @throws Exception error
   */
  @Test
  public void answerWithErrorOnGetGeolocationError() throws Exception {
    // Дано:
    when(executorStateUseCase.getExecutorStates())
        .thenReturn(Flowable.just(ExecutorState.SHIFT_CLOSED));
    when(geoLocationGateway.getGeoLocations(anyLong())).thenReturn(Flowable.error(new Exception()));

    // Действие:
    TestSubscriber<GeoLocation> testSubscriber = geoLocationUseCase.getGeoLocations().test();

    // Результат:
    testSubscriber.assertNoValues();
    testSubscriber.assertError(Exception.class);
  }

  /**
   * Должен ответить завершением.
   *
   * @throws Exception error
   */
  @Test
  public void answerWithComplete() throws Exception {
    // Дано:
    when(executorStateUseCase.getExecutorStates()).thenReturn(Flowable.empty());

    // Действие:
    TestSubscriber<GeoLocation> testSubscriber = geoLocationUseCase.getGeoLocations().test();

    // Результат:
    testSubscriber.assertNoValues();
    testSubscriber.assertNoErrors();
    testSubscriber.assertComplete();
  }
}