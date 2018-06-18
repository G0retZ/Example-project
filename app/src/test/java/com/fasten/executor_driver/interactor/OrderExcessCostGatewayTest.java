package com.fasten.executor_driver.interactor;

import static org.mockito.Mockito.verifyZeroInteractions;

import com.fasten.executor_driver.gateway.Mapper;
import com.fasten.executor_driver.gateway.OrderExcessCostGatewayImpl;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import ua.naiksoftware.stomp.client.StompClient;
import ua.naiksoftware.stomp.client.StompMessage;

@RunWith(MockitoJUnitRunner.class)
public class OrderExcessCostGatewayTest {

  private OrderExcessCostGateway executorStateGateway;

  @Mock
  private StompClient stompClient;
  @Mock
  private Mapper<StompMessage, Integer> mapper;

  @Before
  public void setUp() {
    RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());
    RxJavaPlugins.setSingleSchedulerHandler(scheduler -> Schedulers.trampoline());
    executorStateGateway = new OrderExcessCostGatewayImpl(stompClient, mapper);
  }

  @Test
  public void askStompClientForExcessiveCost() {
    executorStateGateway.getOrderExcessCost().test();
    verifyZeroInteractions(stompClient);
  }
}