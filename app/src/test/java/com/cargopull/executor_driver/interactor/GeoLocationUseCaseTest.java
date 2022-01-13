package com.cargopull.executor_driver.interactor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.cargopull.executor_driver.UseCaseThreadTestRule;
import com.cargopull.executor_driver.backend.web.ConnectionClosedException;
import com.cargopull.executor_driver.entity.ExecutorState;
import com.cargopull.executor_driver.entity.GeoLocation;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.net.ConnectException;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.functions.Action;
import io.reactivex.subscribers.TestSubscriber;

@RunWith(MockitoJUnitRunner.class)
public class GeoLocationUseCaseTest {

  @ClassRule
  public static final UseCaseThreadTestRule classRule = new UseCaseThreadTestRule();

  private GeoLocationUseCase useCase;

  @Mock
  private GeoLocationGateway geoLocationGateway;
  @Mock
  private GeoTrackingGateway geoTrackingGateway;
  @Mock
  private ExecutorStateUseCase executorStateUseCase;
  @Mock
  private Action action;

  @Before
  public void setUp() {
    when(geoLocationGateway.getGeoLocations(anyLong())).thenReturn(Flowable.never());
    when(geoTrackingGateway.sendGeoLocation(any())).thenReturn(Completable.never());
    when(executorStateUseCase.getExecutorStates()).thenReturn(Flowable.never());
    useCase = new GeoLocationUseCaseImpl(
        geoLocationGateway, geoTrackingGateway, executorStateUseCase
    );
  }

  /* Проверяем работу с юзкейсом состояний */

  /**
   * Должен запросить получение смены состояний исполнителя.
   */
  @Test
  public void getExecutorStates() {
    // Action:
    useCase.getGeoLocations().test().isDisposed();

    // Effect:
    verify(executorStateUseCase, only()).getExecutorStates();
  }

  /**
   * Не должен запрашивать получение смены состояний исполнителя вновь, если ошибок не было.
   */
  @Test
  public void getExecutorStatesAgainAfterReload() {
    // Action:
    useCase.getGeoLocations().test().isDisposed();
    useCase.getGeoLocations().test().isDisposed();

    // Effect:
    verify(executorStateUseCase, only()).getExecutorStates();
  }

  /**
   * Должен запросить получение смены состояний исполнителя вновь, после ошибки.
   */
  @Test
  public void getExecutorStatesAgainAfterError() {
    // Given:
    when(executorStateUseCase.getExecutorStates())
        .thenReturn(Flowable.error(new ConnectionClosedException()));

    // Action:
    useCase.getGeoLocations().test().isDisposed();
    useCase.getGeoLocations().test().isDisposed();

    // Effect:
    verify(executorStateUseCase, times(2)).getExecutorStates();
    verifyNoMoreInteractions(executorStateUseCase);
  }

  /**
   * Должен запросить получение смены состояний исполнителя вновь, после окончания.
   */
  @Test
  public void getExecutorStatesAgainAfterComplete() {
    // Given:
    when(executorStateUseCase.getExecutorStates()).thenReturn(Flowable.empty());

    // Action:
    useCase.getGeoLocations().test().isDisposed();
    useCase.getGeoLocations().test().isDisposed();

    // Effect:
    verify(executorStateUseCase, times(2)).getExecutorStates();
    verifyNoMoreInteractions(executorStateUseCase);
  }

  /* Проверяем работу с гейтвеем геолокации в ответ на смену состояний */

  /**
   * Не должен трогать гейтвей, до получения статуса.
   */
  @Test
  public void doNotTouchGatewayIfNoStatus() {
    // Action:
    useCase.getGeoLocations().test().isDisposed();

    // Effect:
    verifyNoInteractions(geoLocationGateway);
  }

  /**
   * Не должен трогать гейтвей, при ошибке в статусах.
   */
  @Test
  public void doNotTouchGatewayIfStatusError() {
    // Given:
    when(executorStateUseCase.getExecutorStates())
        .thenReturn(Flowable.error(new ConnectionClosedException()));

    // Action:
    useCase.getGeoLocations().test().isDisposed();

    // Effect:
    verifyNoInteractions(geoLocationGateway);
  }

  /**
   * Не должен трогать гейтвей, при завершении в статусах.
   */
  @Test
  public void doNotTouchGatewayIfStatusComplete() {
    // Given:
    when(executorStateUseCase.getExecutorStates()).thenReturn(Flowable.empty());

    // Action:
    useCase.getGeoLocations().test().isDisposed();

    // Effect:
    verifyNoInteractions(geoLocationGateway);
  }

  /**
   * Должен запросить гейтвей получать локации с интервалом 1 час, при переходе в состояние
   * "Заблокирован".
   */
  @Test
  public void askGatewayForLocationsEvery1HourIfGoToBlocked() {
    // Given:
    when(executorStateUseCase.getExecutorStates())
        .thenReturn(Flowable.just(ExecutorState.BLOCKED));

    // Action:
    useCase.getGeoLocations().test().isDisposed();

    // Effect:
    verify(geoLocationGateway, only()).getGeoLocations(3600000);
  }

  /**
   * Должен запросить гейтвей получать локации с интервалом 1 час, при переходе в состояние "Смена
   * закрыта".
   */
  @Test
  public void askGatewayForLocationsEvery1HourIfGoToShiftClosed() {
    // Given:
    when(executorStateUseCase.getExecutorStates())
        .thenReturn(Flowable.just(ExecutorState.SHIFT_CLOSED));

    // Action:
    useCase.getGeoLocations().test().isDisposed();

    // Effect:
    verify(geoLocationGateway, only()).getGeoLocations(3600000);
  }

  /**
   * Должен запросить гейтвей получать локации с интервалом 180 сек, при переходе в состояние "Смена
   * открыта".
   */
  @Test
  public void askGatewayForLocationsEvery180secIfGoToShiftOpened() {
    // Given:
    when(executorStateUseCase.getExecutorStates())
        .thenReturn(Flowable.just(ExecutorState.SHIFT_OPENED));

    // Action:
    useCase.getGeoLocations().test().isDisposed();

    // Effect:
    verify(geoLocationGateway, only()).getGeoLocations(180000);
  }

  /**
   * Должен запросить гейтвей получать локации с интервалом 15 сек, при переходе в состояние "На
   * линии".
   */
  @Test
  public void askGatewayForLocationsEvery15secIfGoToOnline() {
    // Given:
    when(executorStateUseCase.getExecutorStates())
        .thenReturn(Flowable.just(ExecutorState.ONLINE));

    // Action:
    useCase.getGeoLocations().test().isDisposed();

    // Effect:
    verify(geoLocationGateway, only()).getGeoLocations(15000);
  }

  /**
   * Должен запросить гейтвей получать локации с интервалом 15 сек, при переходе в состояние
   * "Подтверждение заказа".
   */
  @Test
  public void askGatewayForLocationsEvery15secIfGoToDriverOrderConfirmation() {
    // Given:
    when(executorStateUseCase.getExecutorStates())
        .thenReturn(Flowable.just(ExecutorState.DRIVER_ORDER_CONFIRMATION));

    // Action:
    useCase.getGeoLocations().test().isDisposed();

    // Effect:
    verify(geoLocationGateway, only()).getGeoLocations(15000);
  }

  /**
   * Должен запросить гейтвей получать локации с интервалом 15 сек, при переходе в состояние
   * "Подтверждение заказа клиентом".
   */
  @Test
  public void askGatewayForLocationsEvery15secIfGoToClientOrderConfirmation() {
    // Given:
    when(executorStateUseCase.getExecutorStates())
        .thenReturn(Flowable.just(ExecutorState.CLIENT_ORDER_CONFIRMATION));

    // Action:
    useCase.getGeoLocations().test().isDisposed();

    // Effect:
    verify(geoLocationGateway, only()).getGeoLocations(15000);
  }

  /**
   * Должен запросить гейтвей получать локации с интервалом 15 сек, при переходе в состояние "На
   * пути к клиету".
   */
  @Test
  public void askGatewayForLocationsEvery15secIfGoToMovingToClient() {
    // Given:
    when(executorStateUseCase.getExecutorStates())
        .thenReturn(Flowable.just(ExecutorState.MOVING_TO_CLIENT));

    // Action:
    useCase.getGeoLocations().test().isDisposed();

    // Effect:
    verify(geoLocationGateway, only()).getGeoLocations(15000);
  }

  /**
   * Должен запросить гейтвей получать локации с интервалом 15 сек, при переходе в состояние
   * "Ожидание клиета".
   */
  @Test
  public void askGatewayForLocationsEvery15secIfGoToWaitingForClient() {
    // Given:
    when(executorStateUseCase.getExecutorStates())
        .thenReturn(Flowable.just(ExecutorState.WAITING_FOR_CLIENT));

    // Action:
    useCase.getGeoLocations().test().isDisposed();

    // Effect:
    verify(geoLocationGateway, only()).getGeoLocations(15000);
  }

  /**
   * Должен запросить гейтвей получать локации с интервалом 15 сек, при переходе в состояние
   * "Выполнение заказа".
   */
  @Test
  public void askGatewayForLocationsEvery15secIfGoToOrderFulfillment() {
    // Given:
    when(executorStateUseCase.getExecutorStates())
        .thenReturn(Flowable.just(ExecutorState.ORDER_FULFILLMENT));

    // Action:
    useCase.getGeoLocations().test().isDisposed();

    // Effect:
    verify(geoLocationGateway, only()).getGeoLocations(15000);
  }

  /**
   * Должен запросить гейтвей получать локации с интервалом 15 сек, при переходе в состояние "Прием
   * оплаты".
   */
  @Test
  public void askGatewayForLocationsEvery15secIfGoToPaymentAcceptance() {
    // Given:
    when(executorStateUseCase.getExecutorStates())
        .thenReturn(Flowable.just(ExecutorState.PAYMENT_CONFIRMATION));

    // Action:
    useCase.getGeoLocations().test().isDisposed();

    // Effect:
    verify(geoLocationGateway, only()).getGeoLocations(15000);
  }

  /**
   * Должен запросить гейтвей получать локации с различным интервалом, при смене состояний.
   */
  @Test
  public void askGatewayForLocationsDependingOnNewStatesArrival() {
    // Given:
    InOrder inOrder = Mockito.inOrder(geoLocationGateway);
    when(executorStateUseCase.getExecutorStates()).thenReturn(Flowable.fromArray(
        ExecutorState.SHIFT_CLOSED, ExecutorState.SHIFT_OPENED, ExecutorState.ONLINE,
        ExecutorState.DRIVER_ORDER_CONFIRMATION, ExecutorState.CLIENT_ORDER_CONFIRMATION,
        ExecutorState.MOVING_TO_CLIENT, ExecutorState.WAITING_FOR_CLIENT,
        ExecutorState.ORDER_FULFILLMENT, ExecutorState.PAYMENT_CONFIRMATION, ExecutorState.ONLINE,
        ExecutorState.SHIFT_OPENED, ExecutorState.SHIFT_CLOSED, ExecutorState.BLOCKED
    ));

    // Action:
    useCase.getGeoLocations().test().isDisposed();

    // Effect:
    inOrder.verify(geoLocationGateway).getGeoLocations(3600000);
    inOrder.verify(geoLocationGateway).getGeoLocations(180000);
    inOrder.verify(geoLocationGateway, times(8)).getGeoLocations(15000);
    inOrder.verify(geoLocationGateway).getGeoLocations(180000);
    inOrder.verify(geoLocationGateway, times(2)).getGeoLocations(3600000);
    verifyNoMoreInteractions(geoLocationGateway);
  }

  /**
   * Должен запросить гейтвей получать локации с различным интервалом, при смене состояний.
   *
   * @throws Exception error
   */
  @Test
  public void disposePreviousGatewayQueriesOnNewStatesArrival() throws Exception {
    // Given:
    when(executorStateUseCase.getExecutorStates()).thenReturn(Flowable.fromArray(
        ExecutorState.SHIFT_CLOSED, ExecutorState.SHIFT_OPENED, ExecutorState.ONLINE,
        ExecutorState.DRIVER_ORDER_CONFIRMATION, ExecutorState.CLIENT_ORDER_CONFIRMATION,
        ExecutorState.MOVING_TO_CLIENT, ExecutorState.WAITING_FOR_CLIENT,
        ExecutorState.ORDER_FULFILLMENT, ExecutorState.PAYMENT_CONFIRMATION, ExecutorState.ONLINE,
        ExecutorState.SHIFT_OPENED, ExecutorState.SHIFT_CLOSED, ExecutorState.BLOCKED
    ));
    when(geoLocationGateway.getGeoLocations(anyLong()))
        .thenReturn(Flowable.<GeoLocation>never().doOnCancel(action));

    // Action:
    useCase.getGeoLocations().test().isDisposed();

    // Effect:
    verify(action, times(12)).run();
  }

  /* Проверяем работу с гейтвеем отправки геопозиции в ответ на ответы гейтвея геопозиции */

  /**
   * Не должен трогать гейтвей передачи геолокаций до получения геолокаций.
   */
  @Test
  public void doNotTouchTrackingGatewayOnEmptyStates() {
    // Given:
    when(executorStateUseCase.getExecutorStates()).thenReturn(Flowable.empty());

    // Action:
    useCase.getGeoLocations().test().isDisposed();

    // Effect:
    verifyNoInteractions(geoTrackingGateway);
  }

  /**
   * Должен отправить полученные геопозиции через гейтвей передачи геолокаций.
   */
  @Test
  public void askTrackingGatewayToSendNewGeoLocation() {
    // Given:
    when(executorStateUseCase.getExecutorStates())
        .thenReturn(Flowable.just(ExecutorState.SHIFT_CLOSED));
    when(geoLocationGateway.getGeoLocations(anyLong()))
        .thenReturn(Flowable.just(
            new GeoLocation(1, 2, 3),
            new GeoLocation(3, 1, 2),
            new GeoLocation(2, 3, 1),
            new GeoLocation(0, 0, 0)
        ));

    // Action:
    useCase.getGeoLocations().test().isDisposed();

    // Effect:
    verify(geoTrackingGateway).sendGeoLocation(new GeoLocation(1, 2, 3));
    verify(geoTrackingGateway).sendGeoLocation(new GeoLocation(3, 1, 2));
    verify(geoTrackingGateway).sendGeoLocation(new GeoLocation(2, 3, 1));
    verify(geoTrackingGateway).sendGeoLocation(new GeoLocation(0, 0, 0));
    verifyNoMoreInteractions(geoTrackingGateway);
  }

  /**
   * Не должен трогать гейтвей передачи геолокаций при ошибке получения статусов.
   */
  @Test
  public void doNotTouchTrackingGatewayOnGetStateError() {
    // Given:
    when(executorStateUseCase.getExecutorStates())
        .thenReturn(Flowable.error(ConnectException::new));

    // Action:
    useCase.getGeoLocations().test().isDisposed();

    // Effect:
    verifyNoInteractions(geoTrackingGateway);
  }

  /**
   * Не должен трогать гейтвей передачи геолокаций при ошибке получения геолокаций.
   */
  @Test
  public void doNotTouchTrackingGatewayOnGetGeolocationError() {
    // Given:
    when(executorStateUseCase.getExecutorStates())
        .thenReturn(Flowable.just(ExecutorState.SHIFT_CLOSED));
    when(geoLocationGateway.getGeoLocations(anyLong())).thenReturn(Flowable.error(new Exception()));

    // Action:
    useCase.getGeoLocations().test().isDisposed();

    // Effect:
    verifyNoInteractions(geoTrackingGateway);
  }

  /* Проверяем ответы гейтвея геопозиции */

  /**
   * Должен вернуть полученные геопозиции.
   */
  @Test
  public void answerWithNewGeoLocations() {
    // Given:
    when(executorStateUseCase.getExecutorStates())
        .thenReturn(Flowable.just(ExecutorState.SHIFT_CLOSED));
    when(geoLocationGateway.getGeoLocations(anyLong())).thenReturn(Flowable.just(
        new GeoLocation(1, 2, 3),
        new GeoLocation(4, 5, 6),
        new GeoLocation(7, 8, 9)
    ));
    when(geoTrackingGateway.sendGeoLocation(any())).thenReturn(Completable.complete());

    // Action:
    TestSubscriber<GeoLocation> testSubscriber = useCase.getGeoLocations().test();

    // Effect:
    testSubscriber.assertValues(
        new GeoLocation(1, 2, 3),
        new GeoLocation(4, 5, 6),
        new GeoLocation(7, 8, 9)
    );
    testSubscriber.assertNoErrors();
    testSubscriber.assertComplete();
  }

  /**
   * Не должен веозвращать ошибку при ошибке получения статусов.
   */
  @Test
  public void answerWithCompleteOnGetStateError() {
    // Given:
    when(executorStateUseCase.getExecutorStates())
        .thenReturn(Flowable.error(ConnectException::new));

    // Action:
    TestSubscriber<GeoLocation> testSubscriber = useCase.getGeoLocations().test();

    // Effect:
    testSubscriber.assertNoValues();
    testSubscriber.assertNoErrors();
    testSubscriber.assertComplete();
  }

  /**
   * Должен вернуть ошибку при ошибке получения местоположений.
   */
  @Test
  public void answerWithErrorOnGetGeolocationError() {
    // Given:
    when(executorStateUseCase.getExecutorStates())
        .thenReturn(Flowable.just(ExecutorState.SHIFT_CLOSED));
    when(geoLocationGateway.getGeoLocations(anyLong())).thenReturn(Flowable.error(new Exception()));

    // Action:
    TestSubscriber<GeoLocation> testSubscriber = useCase.getGeoLocations().test();

    // Effect:
    testSubscriber.assertNoValues();
    testSubscriber.assertError(Exception.class);
  }

  /**
   * Должен игнорировать ошибку отправки статуса в сокет.
   */
  @Test
  public void answerWithCompleteOnSendGeolocationError() {
    // Given:
    when(executorStateUseCase.getExecutorStates())
        .thenReturn(Flowable.just(ExecutorState.SHIFT_CLOSED));
    when(geoLocationGateway.getGeoLocations(anyLong())).thenReturn(Flowable.just(
        new GeoLocation(1, 2, 3),
        new GeoLocation(4, 5, 6),
        new GeoLocation(7, 8, 9)
    ));
    when(geoTrackingGateway.sendGeoLocation(any())).thenReturn(Completable.error(Exception::new));

    // Action:
    TestSubscriber<GeoLocation> testSubscriber = useCase.getGeoLocations().test();

    // Effect:
    testSubscriber.assertValues(
        new GeoLocation(1, 2, 3),
        new GeoLocation(4, 5, 6),
        new GeoLocation(7, 8, 9)
    );
    testSubscriber.assertNoErrors();
    testSubscriber.assertComplete();
  }

  /**
   * Должен ответить завершением.
   */
  @Test
  public void answerWithComplete() {
    // Given:
    when(executorStateUseCase.getExecutorStates()).thenReturn(Flowable.empty());

    // Action:
    TestSubscriber<GeoLocation> testSubscriber = useCase.getGeoLocations().test();

    // Effect:
    testSubscriber.assertNoValues();
    testSubscriber.assertNoErrors();
    testSubscriber.assertComplete();
  }
}