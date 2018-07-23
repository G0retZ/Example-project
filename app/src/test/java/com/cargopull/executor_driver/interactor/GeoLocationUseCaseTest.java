package com.cargopull.executor_driver.interactor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import com.cargopull.executor_driver.backend.websocket.ConnectionClosedException;
import com.cargopull.executor_driver.entity.ExecutorState;
import com.cargopull.executor_driver.entity.GeoLocation;
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
    when(executorStateUseCase.getExecutorStates(anyBoolean())).thenReturn(Flowable.never());
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
    // Действие:
    useCase.getGeoLocations().test();

    // Результат:
    verify(executorStateUseCase, only()).getExecutorStates(false);
  }

  /**
   * Не должен запрашивать получение смены состояний исполнителя вновь, если ошибок не было.
   */
  @Test
  public void getExecutorStatesAgainAfterReload() {
    // Действие:
    useCase.getGeoLocations().test();
    useCase.getGeoLocations().test();

    // Результат:
    verify(executorStateUseCase, only()).getExecutorStates(false);
  }

  /**
   * Должен запросить получение смены состояний исполнителя вновь, после ошибки.
   */
  @Test
  public void getExecutorStatesAgainAfterError() {
    // Дано:
    when(executorStateUseCase.getExecutorStates(anyBoolean()))
        .thenReturn(Flowable.error(new ConnectionClosedException()));

    // Действие:
    useCase.getGeoLocations().test();
    useCase.getGeoLocations().test();

    // Результат:
    verify(executorStateUseCase, times(2)).getExecutorStates(false);
    verifyNoMoreInteractions(executorStateUseCase);
  }

  /**
   * Должен запросить получение смены состояний исполнителя вновь, после окончания.
   */
  @Test
  public void getExecutorStatesAgainAfterComplete() {
    // Дано:
    when(executorStateUseCase.getExecutorStates(anyBoolean())).thenReturn(Flowable.empty());

    // Действие:
    useCase.getGeoLocations().test();
    useCase.getGeoLocations().test();

    // Результат:
    verify(executorStateUseCase, times(2)).getExecutorStates(false);
    verifyNoMoreInteractions(executorStateUseCase);
  }

  /* Проверяем работу с гейтвеем геолокации в ответ на смену состояний */

  /**
   * Не должен трогать гейтвей, до получения статуса.
   */
  @Test
  public void doNotTouchGatewayIfNoStatus() {
    // Действие:
    useCase.getGeoLocations().test();

    // Результат:
    verifyZeroInteractions(geoLocationGateway);
  }

  /**
   * Не должен трогать гейтвей, при ошибке в статусах.
   */
  @Test
  public void doNotTouchGatewayIfStatusError() {
    // Дано:
    when(executorStateUseCase.getExecutorStates(anyBoolean()))
        .thenReturn(Flowable.error(new ConnectionClosedException()));

    // Действие:
    useCase.getGeoLocations().test();

    // Результат:
    verifyZeroInteractions(geoLocationGateway);
  }

  /**
   * Не должен трогать гейтвей, при завершении в статусах.
   */
  @Test
  public void doNotTouchGatewayIfStatusComplete() {
    // Дано:
    when(executorStateUseCase.getExecutorStates(anyBoolean())).thenReturn(Flowable.empty());

    // Действие:
    useCase.getGeoLocations().test();

    // Результат:
    verifyZeroInteractions(geoLocationGateway);
  }

  /**
   * Должен запросить гейтвей получать локации с интервалом 1 час,
   * при переходе в состояние "Смена закрыта".
   */
  @Test
  public void askGatewayForLocationsEvery1HourIfGoToShiftClosed() {
    // Дано:
    when(executorStateUseCase.getExecutorStates(anyBoolean()))
        .thenReturn(Flowable.just(ExecutorState.SHIFT_CLOSED));

    // Действие:
    useCase.getGeoLocations().test();

    // Результат:
    verify(geoLocationGateway, only()).getGeoLocations(3600000);
  }

  /**
   * Должен запросить гейтвей получать локации с интервалом 180 сек,
   * при переходе в состояние "Смена открыта".
   */
  @Test
  public void askGatewayForLocationsEvery180secIfGoToShiftOpened() {
    // Дано:
    when(executorStateUseCase.getExecutorStates(anyBoolean()))
        .thenReturn(Flowable.just(ExecutorState.SHIFT_OPENED));

    // Действие:
    useCase.getGeoLocations().test();

    // Результат:
    verify(geoLocationGateway, only()).getGeoLocations(180000);
  }

  /**
   * Должен запросить гейтвей получать локации с интервалом 15 сек,
   * при переходе в состояние "На линии".
   */
  @Test
  public void askGatewayForLocationsEvery15secIfGoToOnline() {
    // Дано:
    when(executorStateUseCase.getExecutorStates(anyBoolean()))
        .thenReturn(Flowable.just(ExecutorState.ONLINE));

    // Действие:
    useCase.getGeoLocations().test();

    // Результат:
    verify(geoLocationGateway, only()).getGeoLocations(15000);
  }

  /**
   * Должен запросить гейтвей получать локации с интервалом 15 сек,
   * при переходе в состояние "Подтверждение заказа".
   */
  @Test
  public void askGatewayForLocationsEvery15secIfGoToDriverOrderConfirmation() {
    // Дано:
    when(executorStateUseCase.getExecutorStates(anyBoolean()))
        .thenReturn(Flowable.just(ExecutorState.DRIVER_ORDER_CONFIRMATION));

    // Действие:
    useCase.getGeoLocations().test();

    // Результат:
    verify(geoLocationGateway, only()).getGeoLocations(15000);
  }

  /**
   * Должен запросить гейтвей получать локации с интервалом 15 сек,
   * при переходе в состояние "Подтверждение заказа клиентом".
   */
  @Test
  public void askGatewayForLocationsEvery15secIfGoToClientOrderConfirmation() {
    // Дано:
    when(executorStateUseCase.getExecutorStates(anyBoolean()))
        .thenReturn(Flowable.just(ExecutorState.CLIENT_ORDER_CONFIRMATION));

    // Действие:
    useCase.getGeoLocations().test();

    // Результат:
    verify(geoLocationGateway, only()).getGeoLocations(15000);
  }

  /**
   * Должен запросить гейтвей получать локации с интервалом 15 сек,
   * при переходе в состояние "На пути к клиету".
   */
  @Test
  public void askGatewayForLocationsEvery15secIfGoToMovingToClient() {
    // Дано:
    when(executorStateUseCase.getExecutorStates(anyBoolean()))
        .thenReturn(Flowable.just(ExecutorState.MOVING_TO_CLIENT));

    // Действие:
    useCase.getGeoLocations().test();

    // Результат:
    verify(geoLocationGateway, only()).getGeoLocations(15000);
  }

  /**
   * Должен запросить гейтвей получать локации с интервалом 15 сек,
   * при переходе в состояние "Ожидание клиета".
   */
  @Test
  public void askGatewayForLocationsEvery15secIfGoToWaitingForClient() {
    // Дано:
    when(executorStateUseCase.getExecutorStates(anyBoolean()))
        .thenReturn(Flowable.just(ExecutorState.WAITING_FOR_CLIENT));

    // Действие:
    useCase.getGeoLocations().test();

    // Результат:
    verify(geoLocationGateway, only()).getGeoLocations(15000);
  }

  /**
   * Должен запросить гейтвей получать локации с интервалом 15 сек,
   * при переходе в состояние "Выполнение заказа".
   */
  @Test
  public void askGatewayForLocationsEvery15secIfGoToOrderFulfillment() {
    // Дано:
    when(executorStateUseCase.getExecutorStates(anyBoolean()))
        .thenReturn(Flowable.just(ExecutorState.ORDER_FULFILLMENT));

    // Действие:
    useCase.getGeoLocations().test();

    // Результат:
    verify(geoLocationGateway, only()).getGeoLocations(15000);
  }

  /**
   * Должен запросить гейтвей получать локации с интервалом 15 сек,
   * при переходе в состояние "Прием оплаты".
   */
  @Test
  public void askGatewayForLocationsEvery15secIfGoToPaymentAcceptance() {
    // Дано:
    when(executorStateUseCase.getExecutorStates(anyBoolean()))
        .thenReturn(Flowable.just(ExecutorState.PAYMENT_CONFIRMATION));

    // Действие:
    useCase.getGeoLocations().test();

    // Результат:
    verify(geoLocationGateway, only()).getGeoLocations(15000);
  }

  /**
   * Должен запросить гейтвей получать локации с различным интервалом, при смене состояний.
   */
  @Test
  public void askGatewayForLocationsDependingOnNewStatesArrival() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(geoLocationGateway);
    when(executorStateUseCase.getExecutorStates(anyBoolean())).thenReturn(Flowable.fromArray(
        ExecutorState.SHIFT_CLOSED, ExecutorState.SHIFT_OPENED, ExecutorState.ONLINE,
        ExecutorState.DRIVER_ORDER_CONFIRMATION, ExecutorState.CLIENT_ORDER_CONFIRMATION,
        ExecutorState.MOVING_TO_CLIENT, ExecutorState.WAITING_FOR_CLIENT,
        ExecutorState.ORDER_FULFILLMENT, ExecutorState.PAYMENT_CONFIRMATION, ExecutorState.ONLINE,
        ExecutorState.SHIFT_OPENED, ExecutorState.SHIFT_CLOSED
    ));

    // Действие:
    useCase.getGeoLocations().test();

    // Результат:
    inOrder.verify(geoLocationGateway).getGeoLocations(3600000);
    inOrder.verify(geoLocationGateway).getGeoLocations(180000);
    inOrder.verify(geoLocationGateway, times(8)).getGeoLocations(15000);
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
    when(executorStateUseCase.getExecutorStates(anyBoolean())).thenReturn(Flowable.fromArray(
        ExecutorState.SHIFT_CLOSED, ExecutorState.SHIFT_OPENED, ExecutorState.ONLINE,
        ExecutorState.DRIVER_ORDER_CONFIRMATION, ExecutorState.CLIENT_ORDER_CONFIRMATION,
        ExecutorState.MOVING_TO_CLIENT, ExecutorState.WAITING_FOR_CLIENT,
        ExecutorState.ORDER_FULFILLMENT, ExecutorState.PAYMENT_CONFIRMATION, ExecutorState.ONLINE,
        ExecutorState.SHIFT_OPENED, ExecutorState.SHIFT_CLOSED
    ));
    when(geoLocationGateway.getGeoLocations(anyLong()))
        .thenReturn(Flowable.<GeoLocation>never().doOnCancel(action));

    // Действие:
    useCase.getGeoLocations().test();

    // Результат:
    verify(action, times(11)).run();
  }

  /* Проверяем работу с гейтвеем отправки геопозиции в ответ на ответы гейтвея геопозиции */

  /**
   * Не должен трогать гейтвей передачи геолокаций до получения геолокаций.
   */
  @Test
  public void doNotTouchTrackingGatewayOnEmptyStates() {
    // Дано:
    when(executorStateUseCase.getExecutorStates(anyBoolean())).thenReturn(Flowable.empty());

    // Действие:
    useCase.getGeoLocations().test();

    // Результат:
    verifyZeroInteractions(geoTrackingGateway);
  }

  /**
   * Должен отправить полученные геопозиции через гейтвей передачи геолокаций.
   */
  @Test
  public void askTrackingGatewayToSendNewGeoLocation() {
    // Дано:
    when(executorStateUseCase.getExecutorStates(anyBoolean()))
        .thenReturn(Flowable.just(ExecutorState.SHIFT_CLOSED));
    when(geoLocationGateway.getGeoLocations(anyLong()))
        .thenReturn(Flowable.just(
            new GeoLocation(1, 2, 3),
            new GeoLocation(3, 1, 2),
            new GeoLocation(2, 3, 1),
            new GeoLocation(0, 0, 0)
        ));

    // Действие:
    useCase.getGeoLocations().test();

    // Результат:
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
    // Дано:
    when(executorStateUseCase.getExecutorStates(anyBoolean()))
        .thenReturn(Flowable.error(ConnectException::new));

    // Действие:
    useCase.getGeoLocations().test();

    // Результат:
    verifyZeroInteractions(geoTrackingGateway);
  }

  /**
   * Не должен трогать гейтвей передачи геолокаций при ошибке получения геолокаций.
   */
  @Test
  public void doNotTouchTrackingGatewayOnGetGeolocationError() {
    // Дано:
    when(executorStateUseCase.getExecutorStates(anyBoolean()))
        .thenReturn(Flowable.just(ExecutorState.SHIFT_CLOSED));
    when(geoLocationGateway.getGeoLocations(anyLong())).thenReturn(Flowable.error(new Exception()));

    // Действие:
    useCase.getGeoLocations().test();

    // Результат:
    verifyZeroInteractions(geoTrackingGateway);
  }

  /* Проверяем ответы гейтвея геопозиции */

  /**
   * Должен вернуть полученные геопозиции.
   */
  @Test
  public void answerWithNewGeoLocations() {
    // Дано:
    when(executorStateUseCase.getExecutorStates(anyBoolean()))
        .thenReturn(Flowable.just(ExecutorState.SHIFT_CLOSED));
    when(geoLocationGateway.getGeoLocations(anyLong())).thenReturn(Flowable.just(
        new GeoLocation(1, 2, 3),
        new GeoLocation(4, 5, 6),
        new GeoLocation(7, 8, 9)
    ));
    when(geoTrackingGateway.sendGeoLocation(any())).thenReturn(Completable.complete());

    // Действие:
    TestSubscriber<GeoLocation> testSubscriber = useCase.getGeoLocations().test();

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
   */
  @Test
  public void answerWithErrorOnGetStateError() {
    // Дано:
    when(executorStateUseCase.getExecutorStates(anyBoolean()))
        .thenReturn(Flowable.error(ConnectException::new));

    // Действие:
    TestSubscriber<GeoLocation> testSubscriber = useCase.getGeoLocations().test();

    // Результат:
    testSubscriber.assertNoValues();
    testSubscriber.assertNoErrors();
    testSubscriber.assertComplete();
  }

  /**
   * Должен вернуть ошибку при ошибке получения местоположений.
   */
  @Test
  public void answerWithErrorOnGetGeolocationError() {
    // Дано:
    when(executorStateUseCase.getExecutorStates(anyBoolean()))
        .thenReturn(Flowable.just(ExecutorState.SHIFT_CLOSED));
    when(geoLocationGateway.getGeoLocations(anyLong())).thenReturn(Flowable.error(new Exception()));

    // Действие:
    TestSubscriber<GeoLocation> testSubscriber = useCase.getGeoLocations().test();

    // Результат:
    testSubscriber.assertNoValues();
    testSubscriber.assertError(Exception.class);
  }

  /**
   * Должен вернуть ошибку при ошибке отправки статуса.
   */
  @Test
  public void answerWithErrorOnSendGeolocationError() {
    // Дано:
    when(executorStateUseCase.getExecutorStates(anyBoolean()))
        .thenReturn(Flowable.just(ExecutorState.SHIFT_CLOSED));
    when(geoLocationGateway.getGeoLocations(anyLong())).thenReturn(Flowable.just(
        new GeoLocation(1, 2, 3),
        new GeoLocation(4, 5, 6),
        new GeoLocation(7, 8, 9)
    ));
    when(geoTrackingGateway.sendGeoLocation(any())).thenReturn(Completable.error(Exception::new));

    // Действие:
    TestSubscriber<GeoLocation> testSubscriber = useCase.getGeoLocations().test();

    // Результат:
    testSubscriber.assertNoValues();
    testSubscriber.assertError(Exception.class);
  }

  /**
   * Должен ответить завершением.
   */
  @Test
  public void answerWithComplete() {
    // Дано:
    when(executorStateUseCase.getExecutorStates(anyBoolean())).thenReturn(Flowable.empty());

    // Действие:
    TestSubscriber<GeoLocation> testSubscriber = useCase.getGeoLocations().test();

    // Результат:
    testSubscriber.assertNoValues();
    testSubscriber.assertNoErrors();
    testSubscriber.assertComplete();
  }
}