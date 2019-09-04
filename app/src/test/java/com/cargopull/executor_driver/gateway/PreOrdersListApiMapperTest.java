package com.cargopull.executor_driver.gateway;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cargopull.executor_driver.backend.stomp.StompFrame;
import com.cargopull.executor_driver.backend.web.incoming.ApiOrder;
import com.cargopull.executor_driver.entity.Order;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class PreOrdersListApiMapperTest {

  private Mapper<StompFrame, Set<Order>> mapper;

  @Mock
  private Mapper<ApiOrder, Order> apiOrderMapper;
  @Mock
  private StompFrame stompFrame;
  @Mock
  private Order order;
  @Mock
  private Order order2;

  @Before
  public void setUp() {
    mapper = new PreOrdersListApiMapper(apiOrderMapper);
  }

  /**
   * Должен запросить преобразование JSON в предзаказ.
   *
   * @throws Exception ошибка
   */
  @Test
  public void shouldAskForMappingJsonStringToPreOrder() throws Exception {
    // Дано:
    when(stompFrame.getBody()).thenReturn("[{}]");
    when(apiOrderMapper.map(any(ApiOrder.class))).thenReturn(order);

    // Действие:
    mapper.map(stompFrame);

    // Результат:
    verify(apiOrderMapper, only()).map(any(ApiOrder.class));
  }

  /**
   * Должен успешно преобразовать JSON в список предзаказов.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingJsonStringToPreOrdersListSuccess() throws Exception {
    // Дано:
    when(stompFrame.getBody()).thenReturn("[{}, {}, {}, {}]");
    when(apiOrderMapper.map(any(ApiOrder.class))).thenReturn(order, order2, order2, order);

    // Действие:
    Set<Order> preOrders = mapper.map(stompFrame);

    // Результат:
    assertEquals(2, preOrders.size());
    assertEquals(new HashSet<>(Arrays.asList(order, order2)), preOrders);
  }

  /**
   * Должен дать ошибку, если была ошибка маппинга заказа.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingFailForRoutePointMappingError() throws Exception {
    // Дано:
    when(stompFrame.getBody()).thenReturn("[{}, {}, {}, {}]");
    doThrow(new DataMappingException()).when(apiOrderMapper).map(any(ApiOrder.class));

    // Действие:
    mapper.map(stompFrame);
  }

  /**
   * Должен дать ошибку, если строка пустая.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingEmptyFail() throws Exception {
    // Дано:
    when(stompFrame.getBody()).thenReturn("\n");

    // Действие:
    mapper.map(stompFrame);
  }

  /**
   * Должен дать ошибку, если пришла просто строка.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingStringFail() throws Exception {
    // Дано:
    when(stompFrame.getBody()).thenReturn("dasie");

    // Действие:
    mapper.map(stompFrame);
  }

  /**
   * Должен дать ошибку, если пришло просто число.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingNumberFail() throws Exception {
    // Дано:
    when(stompFrame.getBody()).thenReturn("12");

    // Действие:
    mapper.map(stompFrame);
  }

  /**
   * Должен дать ошибку, если пришел объект.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingArrayFail() throws Exception {
    // Дано:
    when(stompFrame.getBody()).thenReturn("{}");

    // Действие:
    mapper.map(stompFrame);
  }

  /**
   * Должен дать ошибку, если пришел null.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingNullFail() throws Exception {
    // Действие:
    mapper.map(stompFrame);
  }
}