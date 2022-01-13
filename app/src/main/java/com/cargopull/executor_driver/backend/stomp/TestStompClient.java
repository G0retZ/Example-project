package com.cargopull.executor_driver.backend.stomp;

import com.cargopull.executor_driver.backend.ApiOrderTestFactory;
import com.cargopull.executor_driver.entity.PaymentType;
import com.cargopull.executor_driver.entity.RouteType;
import com.cargopull.executor_driver.utils.TimeUtils;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class TestStompClient implements StompClient {

  private final TimeUtils timeUtils;
  private final Flowable<StompFrame> subject;

  public TestStompClient(TimeUtils timeUtils, Flowable<StompFrame> subject) {
    this.timeUtils = timeUtils;
    this.subject = subject;
  }

  @Override
  public Flowable<Boolean> getConnectionState() {
    return Flowable.<Boolean>never().startWith(true);
  }

  @Override
  public Flowable<StompFrame> listenToDestination(String destination, int ms, float fraction) {
    Flowable<StompFrame> inject = Flowable.intervalRange(0, 7, 30, 60, TimeUnit.SECONDS)
        .map(index -> {
          HashMap<String, String> headers = new HashMap<>();
          switch (index.intValue()) {
            case 0:
              return new StompFrame(
                  Command.MESSAGE,
                  Collections.singletonMap("Status", "DRIVER_ORDER_CONFIRMATION"),
                  ApiOrderTestFactory.getOrder(
                      index,
                      timeUtils.currentTimeMillis() + TimeUnit.MINUTES.toMillis(30),
                      timeUtils.currentTimeMillis() + TimeUnit.HOURS.toMillis(5),
                      PaymentType.CASH, RouteType.ORDER_ZONE)
              );
            case 1:
              return new StompFrame(
                  Command.MESSAGE,
                  Collections.singletonMap("Status", "DRIVER_ORDER_CONFIRMATION"),
                  ApiOrderTestFactory.getOrder(
                      index,
                      timeUtils.currentTimeMillis() + TimeUnit.MINUTES.toMillis(35),
                      timeUtils.currentTimeMillis() + TimeUnit.HOURS.toMillis(5),
                      PaymentType.CASH, RouteType.ORDER_ZONE)
              );
            case 2:
              return new StompFrame(
                  Command.MESSAGE,
                  Collections.singletonMap("Preliminary", "true"),
                  ApiOrderTestFactory.getOrder(
                      index,
                      timeUtils.currentTimeMillis() + TimeUnit.MINUTES.toMillis(30),
                      timeUtils.currentTimeMillis() + TimeUnit.HOURS.toMillis(5),
                      PaymentType.CASH, RouteType.ORDER_ZONE)
              );
            case 3:
              return new StompFrame(
                  Command.MESSAGE,
                  Collections.singletonMap("Preliminary", "true"),
                  ApiOrderTestFactory.getOrder(
                      index,
                      timeUtils.currentTimeMillis() + TimeUnit.MINUTES.toMillis(25),
                      timeUtils.currentTimeMillis() + TimeUnit.HOURS.toMillis(5),
                      PaymentType.CASH, RouteType.ORDER_ZONE)
              );
            case 4:
              return new StompFrame(
                  Command.MESSAGE,
                  Collections.singletonMap("Preliminary", "true"),
                  ApiOrderTestFactory.getOrder(
                      index,
                      timeUtils.currentTimeMillis() + TimeUnit.MINUTES.toMillis(35),
                      timeUtils.currentTimeMillis() + TimeUnit.HOURS.toMillis(6),
                      PaymentType.CASH, RouteType.ORDER_ZONE)
              );
            case 5:
              headers.put("PreliminaryReminder", "true");
              headers.put("OrderId", "3");
              headers.put("ETA", "" + TimeUnit.MINUTES.toMillis(25));
              return new StompFrame(
                  Command.MESSAGE,
                  headers,
                  "MESSAGE"
              );
            default:
              headers.put("PreliminaryReminder", "true");
              headers.put("OrderId", "4");
              headers.put("ETA", "" + TimeUnit.MINUTES.toMillis(35));
              return new StompFrame(
                  Command.MESSAGE,
                  headers,
                  "MESSAGE 2"
              );
          }
        });
    return Flowable.just(
        new StompFrame(
            Command.MESSAGE,
            Collections.singletonMap("Status", "ONLINE"),
            ""
        ),
        new StompFrame(
            Command.MESSAGE,
            Collections.singletonMap("Balance", "true"),
            "{\"mainAccount\": 10100, \"bonusAccount\": 5300, \"nonCashAccount\": 25700}"
        )
    ).mergeWith(inject).mergeWith(subject);
  }

  @Override
  public Completable send(String destination, String data) {
    return Completable.complete();
  }
}