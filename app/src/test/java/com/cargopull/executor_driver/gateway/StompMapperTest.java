package com.cargopull.executor_driver.gateway;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import com.cargopull.executor_driver.backend.stomp.StompFrame;
import com.cargopull.executor_driver.backend.web.incoming.ApiOrder;
import com.cargopull.executor_driver.entity.Order;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class StompMapperTest {

  @Rule
  public final ApiOrderRule rule = new ApiOrderRule();

  private Mapper<StompFrame, Order> mapper;

  @Mock
  private Mapper<ApiOrder, Order> apiOrderMapper;
  @Mock
  private StompFrame stompFrame;
  @Mock
  private Order order;

  @Before
  public void setUp() throws Exception {
    when(apiOrderMapper.map(any(ApiOrder.class))).thenReturn(order);
    mapper = new StompMapper<>(apiOrderMapper, ApiOrder.class);
  }

  /**
   * Должен успешно преобразовать JSON в заказ.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingJsonStringToOrderSuccess() throws Exception {
    // Given:
    when(stompFrame.getBody()).thenReturn(rule.getFullOrder());

      // Action:
    Order order = mapper.map(stompFrame);

      // Effect:
    assertEquals(this.order, order);
  }

  /**
   * Должен дать ошибку, если была ошибка маппинга заказа.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingFailForOrderMappingError() throws Exception {
      // Given:
    when(stompFrame.getBody()).thenReturn(rule.getFullOrder());
    doThrow(new DataMappingException()).when(apiOrderMapper).map(any(ApiOrder.class));

      // Action:
    mapper.map(stompFrame);
  }

  /**
   * Должен дать ошибку, если строка пустая.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingEmptyFail() throws Exception {
      // Given:
    when(stompFrame.getBody()).thenReturn("\n");

      // Action:
    mapper.map(stompFrame);
  }

  /**
   * Должен дать ошибку, если пришла просто строка.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingStringFail() throws Exception {
      // Given:
    when(stompFrame.getBody()).thenReturn("dasie");

      // Action:
    mapper.map(stompFrame);
  }

  /**
   * Должен дать ошибку, если пришло просто число.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingNumberFail() throws Exception {
      // Given:
    when(stompFrame.getBody()).thenReturn("12");

      // Action:
    mapper.map(stompFrame);
  }

  /**
   * Должен дать ошибку, если пришел массив.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingArrayFail() throws Exception {
      // Given:
    when(stompFrame.getBody()).thenReturn("[]");

      // Action:
    mapper.map(stompFrame);
  }

  /**
   * Должен дать ошибку, если пришел null.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingNullFail() throws Exception {
      // Action:
    mapper.map(stompFrame);
  }
}