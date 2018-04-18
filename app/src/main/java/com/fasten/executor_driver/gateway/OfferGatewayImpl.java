package com.fasten.executor_driver.gateway;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.BuildConfig;
import com.fasten.executor_driver.backend.websocket.ConnectionClosedException;
import com.fasten.executor_driver.entity.ExecutorState;
import com.fasten.executor_driver.entity.NoOffersAvailableException;
import com.fasten.executor_driver.entity.Offer;
import com.fasten.executor_driver.interactor.ExecutorStateUseCase;
import com.fasten.executor_driver.interactor.OfferGateway;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Inject;
import ua.naiksoftware.stomp.client.StompClient;

public class OfferGatewayImpl implements OfferGateway {

  @NonNull
  private final ExecutorStateUseCase executorStateUseCase;
  @NonNull
  private final StompClient stompClient;
  @NonNull
  private final Mapper<String, Offer> mapper;

  @Inject
  public OfferGatewayImpl(
      @NonNull ExecutorStateUseCase executorStateUseCase,
      @NonNull StompClient stompClient,
      @NonNull Mapper<String, Offer> mapper) {
    this.executorStateUseCase = executorStateUseCase;
    this.stompClient = stompClient;
    this.mapper = mapper;
  }

  @NonNull
  @Override
  public Flowable<Offer> getOffers() {
    return executorStateUseCase
        .getExecutorStates()
        .subscribeOn(Schedulers.single())
        .observeOn(Schedulers.computation())
        .map(executorState -> {
          if (executorState != ExecutorState.ORDER_CONFIRMATION) {
            throw new NoOffersAvailableException();
          }
          return executorState;
        }).map(executorState -> {
          if (executorState.getData() == null) {
            throw new NoOffersAvailableException();
          }
          return mapper.map(executorState.getData());
        }).observeOn(Schedulers.single());
  }

  @NonNull
  @Override
  public Completable sendDecision(@NonNull Offer offer, boolean accepted) {
    if (stompClient.isConnected() || stompClient.isConnecting()) {
      return stompClient.send(
          BuildConfig.CONFIRM_OFFER_DESTINATION,
          "{\"id\":\"" + offer.getId() + "\", \"approved\":\"" + accepted + "\"}"
      )
          .subscribeOn(Schedulers.io())
          .observeOn(Schedulers.single());
    }
    return Completable.error(new ConnectionClosedException());
  }
}
