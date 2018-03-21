package com.fasten.executor_driver.interactor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import com.fasten.executor_driver.entity.ExecutorState;
import com.fasten.executor_driver.entity.GeoLocation;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.functions.Action;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class GeoLocationUseCaseTest {

  private GeoLocationUseCase geoLocationUseCase;

  @Mock
  private GeoLocationGateway gateway;

  @Mock
  private DataReceiver<ExecutorState> executorStateReceiver;

  @Mock
  private Observer<GeoLocation> geoLocationObserver;

  @Mock
  private Action action;

  @Before
  public void setUp() throws Exception {
    when(gateway.getGeoLocations(anyLong())).thenReturn(Flowable.never());
    when(executorStateReceiver.get()).thenReturn(Observable.never());
    geoLocationUseCase = new GeoLocationUseCaseImpl(
        gateway, executorStateReceiver, geoLocationObserver
    );
  }

  /* Проверяем работу с публикатором состояний */

  /**
   * Должен получить смену состояний исполнителя.
   *
   * @throws Exception error
   */
  @Test
  public void getExecutorStates() throws Exception {
    // Действие:
    geoLocationUseCase.reload().test();

    // Результат:
    verify(executorStateReceiver, only()).get();
  }

  /**
   * Должен получить смену состояний исполнителя вновь, отписавшись от прошлого запроса.
   *
   * @throws Exception error
   */
  @Test
  public void getExecutorStatesDisposingFromThePrevious() throws Exception {
    // Дано:
    when(executorStateReceiver.get())
        .thenReturn(Observable.<ExecutorState>never().doOnDispose(action));

    // Действие:
    geoLocationUseCase.reload().test();
    geoLocationUseCase.reload().test();

    // Результат:
    verify(executorStateReceiver, times(2)).get();
    verifyNoMoreInteractions(executorStateReceiver);
    verify(action, only()).run();
  }

  /* Проверяем работу с гейтвеем в ответ на смену состояний */

  /**
   * Не должен трогать гейтвей, при переходе в состояние "Не авторизован".
   *
   * @throws Exception error
   */
  @SuppressWarnings("unchecked")
  @Test
  public void doNotTouchGatewayIfGoToNotAuth() throws Exception {
    // Дано:
    when(executorStateReceiver.get())
        .thenReturn(Observable.just(ExecutorState.UNAUTHORIZED), Observable.never());

    // Действие:
    geoLocationUseCase.reload().test();

    // Результат:
    verifyZeroInteractions(gateway);
  }

  /**
   * Должен запросить гейтвей получать локации с интервалом 1 час,
   * при переходе в состояние "Смена закрыта".
   *
   * @throws Exception error
   */
  @SuppressWarnings("unchecked")
  @Test
  public void doNotTouchGatewayIfGoToShiftClosed() throws Exception {
    // Дано:
    when(executorStateReceiver.get())
        .thenReturn(Observable.just(ExecutorState.SHIFT_CLOSED), Observable.never());

    // Действие:
    geoLocationUseCase.reload().test();

    // Результат:
    verify(gateway, only()).getGeoLocations(3600000);
  }

  /**
   * Должен запросить гейтвей получать локации с интервалом 180 сек,
   * при переходе в состояние "Смена открыта".
   *
   * @throws Exception error
   */
  @SuppressWarnings("unchecked")
  @Test
  public void askGatewayForLocationsEvery180secIfGoToShiftOpened() throws Exception {
    // Дано:
    when(executorStateReceiver.get())
        .thenReturn(Observable.just(ExecutorState.SHIFT_OPENED), Observable.never());

    // Действие:
    geoLocationUseCase.reload().test();

    // Результат:
    verify(gateway, only()).getGeoLocations(180000);
  }

  /**
   * Должен запросить гейтвей получать локации с интервалом 15 сек,
   * при переходе в состояние "На линии".
   *
   * @throws Exception error
   */
  @SuppressWarnings("unchecked")
  @Test
  public void askGatewayForLocationsEvery15secIfGoToOnline() throws Exception {
    // Дано:
    when(executorStateReceiver.get())
        .thenReturn(Observable.just(ExecutorState.ONLINE), Observable.never());

    // Действие:
    geoLocationUseCase.reload().test();

    // Результат:
    verify(gateway, only()).getGeoLocations(15000);
  }

  /**
   * Должен отписаться от прошлого запроса к гейтвею, при переходе в состояние "Смена открыта" из
   * состояния "Смена закрыта".
   *
   * @throws Exception error
   */
  @SuppressWarnings("unchecked")
  @Test
  public void disposePreviousGatewayQueryIfGoToShiftOpenedFromShiftClosed() throws Exception {
    // Дано:
    when(gateway.getGeoLocations(anyLong()))
        .thenReturn(Flowable.<GeoLocation>never().doOnCancel(action));
    when(executorStateReceiver.get()).thenReturn(
        Observable.just(ExecutorState.SHIFT_CLOSED),
        Observable.just(ExecutorState.SHIFT_OPENED),
        Observable.never()
    );

    // Действие:
    geoLocationUseCase.reload().test();
    geoLocationUseCase.reload().test();

    // Результат:
    verify(action, only()).run();
  }

  /**
   * Должен отписаться от прошлого запроса к гейтвею, при переходе в состояние "На линии" из
   * состояния "Смена открыта".
   *
   * @throws Exception error
   */
  @SuppressWarnings("unchecked")
  @Test
  public void disposePreviousGatewayQueryIfGoToOnlineFromShiftOpened() throws Exception {
    // Дано:
    when(gateway.getGeoLocations(anyLong()))
        .thenReturn(Flowable.<GeoLocation>never().doOnCancel(action));
    when(executorStateReceiver.get()).thenReturn(
        Observable.just(ExecutorState.SHIFT_OPENED),
        Observable.just(ExecutorState.ONLINE),
        Observable.never()
    );

    // Действие:
    geoLocationUseCase.reload().test();
    geoLocationUseCase.reload().test();

    // Результат:
    verify(action, only()).run();
  }

  /**
   * Должен отписаться от прошлого запроса к гейтвею, при переходе в состояние "Смена открыта" из
   * состояния "На линии".
   *
   * @throws Exception error
   */
  @SuppressWarnings("unchecked")
  @Test
  public void disposePreviousGatewayQueryIfGoToShiftOpenedFromOnline() throws Exception {
    // Дано:
    when(gateway.getGeoLocations(anyLong()))
        .thenReturn(Flowable.<GeoLocation>never().doOnCancel(action));
    when(executorStateReceiver.get()).thenReturn(
        Observable.just(ExecutorState.ONLINE),
        Observable.just(ExecutorState.SHIFT_OPENED),
        Observable.never()
    );

    // Действие:
    geoLocationUseCase.reload().test();
    geoLocationUseCase.reload().test();

    // Результат:
    verify(action, only()).run();
  }

  /**
   * Должен отписаться от прошлого запроса к гейтвею, при переходе в состояние "Смена закрыта" из
   * состояния "Смена открыта".
   *
   * @throws Exception error
   */
  @SuppressWarnings("unchecked")
  @Test
  public void disposePreviousGatewayQueryIfGoToShiftClosedFromShiftOpened() throws Exception {
    // Дано:
    when(gateway.getGeoLocations(anyLong()))
        .thenReturn(Flowable.<GeoLocation>never().doOnCancel(action));
    when(executorStateReceiver.get()).thenReturn(
        Observable.just(ExecutorState.SHIFT_OPENED),
        Observable.just(ExecutorState.SHIFT_CLOSED),
        Observable.never()
    );

    // Действие:
    geoLocationUseCase.reload().test();
    geoLocationUseCase.reload().test();

    // Результат:
    verify(action, only()).run();
  }

  /* Проверяем работу с публикатором геопозиции в ответ на ответы гейтвея */

  /**
   * Должен опубликовать полученную геопозицию.
   *
   * @throws Exception error
   */
  @SuppressWarnings("unchecked")
  @Test
  public void publishNewGeoLocation() throws Exception {
    // Дано:
    when(executorStateReceiver.get())
        .thenReturn(Observable.just(ExecutorState.ONLINE), Observable.never());
    when(gateway.getGeoLocations(anyLong())).thenReturn(Flowable.just(new GeoLocation(1, 2, 3)));

    // Действие:
    geoLocationUseCase.reload().test();

    // Результат:
    verify(geoLocationObserver, only()).onNext(new GeoLocation(1, 2, 3));
  }

  /**
   * Должен опубликовать ошибку.
   *
   * @throws Exception error
   */
  @SuppressWarnings("unchecked")
  @Test
  public void publishError() throws Exception {
    // Дано:
    when(executorStateReceiver.get())
        .thenReturn(Observable.just(ExecutorState.ONLINE), Observable.never());
    when(gateway.getGeoLocations(anyLong())).thenReturn(Flowable.error(new Exception()));

    // Действие:
    geoLocationUseCase.reload().test();

    // Результат:
    verify(geoLocationObserver, only()).onError(any(Exception.class));
  }
}