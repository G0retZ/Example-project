package com.cargopull.executor_driver.gateway;

import android.support.annotation.NonNull;
import com.cargopull.executor_driver.backend.websocket.TopicListener;
import com.cargopull.executor_driver.entity.CancelOrderReason;
import com.cargopull.executor_driver.interactor.CancelOrderReasonsGateway;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import java.util.List;
import javax.inject.Inject;
import ua.naiksoftware.stomp.client.StompMessage;

public class CancelOrderReasonsGatewayImpl implements CancelOrderReasonsGateway {

  @NonNull
  private final TopicListener topicListener;
  @NonNull
  private final Mapper<StompMessage, List<CancelOrderReason>> mapper;

  @Inject
  public CancelOrderReasonsGatewayImpl(@NonNull TopicListener topicListener,
      @NonNull Mapper<StompMessage, List<CancelOrderReason>> mapper) {
    this.topicListener = topicListener;
    this.mapper = mapper;
  }

  @NonNull
  @Override
  public Flowable<List<CancelOrderReason>> loadCancelOrderReasons() {
    return topicListener.getAcknowledgedMessages()
        .observeOn(Schedulers.io())
        .filter(stompMessage -> stompMessage.findHeader("CancelReason") != null)
        .map(mapper::map);
  }
}
