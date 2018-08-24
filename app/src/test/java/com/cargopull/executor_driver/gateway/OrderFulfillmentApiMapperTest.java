package com.cargopull.executor_driver.gateway;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import com.cargopull.executor_driver.backend.web.incoming.ApiOptionItem;
import com.cargopull.executor_driver.backend.websocket.incoming.ApiRoutePoint;
import com.cargopull.executor_driver.entity.Option;
import com.cargopull.executor_driver.entity.OptionBoolean;
import com.cargopull.executor_driver.entity.OptionNumeric;
import com.cargopull.executor_driver.entity.Order;
import com.cargopull.executor_driver.entity.RoutePoint;
import java.util.ArrayList;
import java.util.Arrays;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import ua.naiksoftware.stomp.client.StompMessage;

@RunWith(MockitoJUnitRunner.class)
public class OrderFulfillmentApiMapperTest {

  @Rule
  public final ApiOrderRule rule = new ApiOrderRule();

  private Mapper<StompMessage, Order> mapper;

  @Mock
  private Mapper<ApiOptionItem, Option> apiOptionMapper;
  @Mock
  private Mapper<ApiRoutePoint, RoutePoint> routePointMapper;
  @Mock
  private StompMessage stompMessage;
  @Mock
  private OptionBoolean optionBoolean;
  @Mock
  private OptionNumeric optionNumeric;
  @Mock
  private RoutePoint routePoint;
  @Mock
  private RoutePoint routePoint2;

  @Before
  public void setUp() throws Exception {
    when(apiOptionMapper.map(any(ApiOptionItem.class)))
        .thenReturn(optionNumeric, optionBoolean, optionBoolean, optionNumeric);
    when(routePointMapper.map(any(ApiRoutePoint.class))).thenReturn(routePoint, routePoint2);
    mapper = new OrderFulfillmentApiMapper(apiOptionMapper, routePointMapper);
  }

  /**
   * Должен успешно преобразовать JSON в заказ.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingJsonStringToOrderSuccess() throws Exception {
    // Дано:
    when(stompMessage.getPayload()).thenReturn(rule.getFullOrder());

    // Действие:
    Order order = mapper.map(stompMessage);

    // Результат:
    assertEquals(order.getId(), 7);
    assertEquals(order.getComment(), "some comment");
    assertEquals(order.getEstimatedPriceText(), "over 9999 BTC");
    assertEquals(order.getEstimatedPrice(), 9999);
    assertEquals(order.getEstimatedTime(), 234_532_000);
    assertEquals(order.getEstimatedRouteLength(), 35_213);
    assertEquals(order.getTotalCost(), 10_352);
    assertEquals(order.getTimeout(), 25);
    assertEquals(order.getEtaToStartPoint(), 1234567890);
    assertEquals(order.getConfirmationTime(), 9876543210L);
    assertEquals(order.getStartTime(), 9876598760L);
    assertEquals(order.getScheduledStartTime(), 128937981273L);
    assertEquals(order.getServiceName(), "service");
    assertEquals(order.getDistance(), 546);
    assertEquals(order.getServiceName(), "service");
    assertEquals(order.getRoutePath(), Arrays.asList(routePoint, routePoint2, routePoint2));
    assertEquals(order.getOptions(),
        Arrays.asList(optionNumeric, optionBoolean, optionBoolean, optionNumeric)
    );
  }

  /**
   * Должен успешно преобразовать JSON без ИД в заказ.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingJsonStringWithoutIdToOrderSuccess() throws Exception {
    // Дано:
    when(stompMessage.getPayload()).thenReturn(rule.getOrderWithoutId());

    // Действие:
    Order order = mapper.map(stompMessage);

    // Результат:
    assertEquals(order.getId(), 0);
    assertEquals(order.getComment(), "some comment");
    assertEquals(order.getEstimatedPriceText(), "over 9999 BTC");
    assertEquals(order.getEstimatedPrice(), 9999);
    assertEquals(order.getEstimatedTime(), 234_532_000);
    assertEquals(order.getEstimatedRouteLength(), 35_213);
    assertEquals(order.getTotalCost(), 10_352);
    assertEquals(order.getTimeout(), 25);
    assertEquals(order.getEtaToStartPoint(), 1234567890);
    assertEquals(order.getConfirmationTime(), 9876543210L);
    assertEquals(order.getStartTime(), 9876598760L);
    assertEquals(order.getScheduledStartTime(), 128937981273L);
    assertEquals(order.getServiceName(), "service");
    assertEquals(order.getDistance(), 546);
    assertEquals(order.getRoutePath(), Arrays.asList(routePoint, routePoint2, routePoint2));
    assertEquals(order.getOptions(),
        Arrays.asList(optionNumeric, optionBoolean, optionBoolean, optionNumeric)
    );
  }

  /**
   * Должен успешно преобразовать JSON без комментария в заказ.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingJsonStringWithoutCommentToOrderSuccess() throws Exception {
    // Дано:
    when(stompMessage.getPayload()).thenReturn(rule.getOrderWithoutComment());

    // Действие:
    Order order = mapper.map(stompMessage);

    // Результат:
    assertEquals(order.getId(), 7);
    assertEquals(order.getComment(), "");
    assertEquals(order.getEstimatedPriceText(), "over 9999 BTC");
    assertEquals(order.getEstimatedPrice(), 9999);
    assertEquals(order.getEstimatedTime(), 234_532_000);
    assertEquals(order.getEstimatedRouteLength(), 35_213);
    assertEquals(order.getTotalCost(), 10_352);
    assertEquals(order.getTimeout(), 25);
    assertEquals(order.getEtaToStartPoint(), 1234567890);
    assertEquals(order.getConfirmationTime(), 9876543210L);
    assertEquals(order.getStartTime(), 9876598760L);
    assertEquals(order.getScheduledStartTime(), 128937981273L);
    assertEquals(order.getServiceName(), "service");
    assertEquals(order.getDistance(), 546);
    assertEquals(order.getRoutePath(), Arrays.asList(routePoint, routePoint2, routePoint2));
    assertEquals(order.getOptions(),
        Arrays.asList(optionNumeric, optionBoolean, optionBoolean, optionNumeric)
    );
  }

  /**
   * Должен успешно преобразовать JSON без описания цены в заказ.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingJsonStringWithoutEstimatedAmountTextToOrderSuccess() throws Exception {
    // Дано:
    when(stompMessage.getPayload()).thenReturn(rule.getOrderWithoutEstimatedAmountText());

    // Действие:
    Order order = mapper.map(stompMessage);

    // Результат:
    assertEquals(order.getId(), 7);
    assertEquals(order.getComment(), "some comment");
    assertEquals(order.getEstimatedPriceText(), "");
    assertEquals(order.getEstimatedPrice(), 9999);
    assertEquals(order.getEstimatedTime(), 234_532_000);
    assertEquals(order.getEstimatedRouteLength(), 35_213);
    assertEquals(order.getTotalCost(), 10_352);
    assertEquals(order.getTimeout(), 25);
    assertEquals(order.getEtaToStartPoint(), 1234567890);
    assertEquals(order.getConfirmationTime(), 9876543210L);
    assertEquals(order.getStartTime(), 9876598760L);
    assertEquals(order.getScheduledStartTime(), 128937981273L);
    assertEquals(order.getServiceName(), "service");
    assertEquals(order.getDistance(), 546);
    assertEquals(order.getRoutePath(), Arrays.asList(routePoint, routePoint2, routePoint2));
    assertEquals(order.getOptions(),
        Arrays.asList(optionNumeric, optionBoolean, optionBoolean, optionNumeric)
    );
  }

  /**
   * Должен успешно преобразовать JSON без цены в заказ.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingJsonStringWithoutEstimatedAmountToOrderSuccess() throws Exception {
    // Дано:
    when(stompMessage.getPayload()).thenReturn(rule.getOrderWithoutEstimatedAmount());

    // Действие:
    Order order = mapper.map(stompMessage);

    // Результат:
    assertEquals(order.getId(), 7);
    assertEquals(order.getComment(), "some comment");
    assertEquals(order.getEstimatedPriceText(), "over 9999 BTC");
    assertEquals(order.getEstimatedPrice(), 0);
    assertEquals(order.getEstimatedTime(), 234_532_000);
    assertEquals(order.getEstimatedRouteLength(), 35_213);
    assertEquals(order.getTotalCost(), 10_352);
    assertEquals(order.getTimeout(), 25);
    assertEquals(order.getEtaToStartPoint(), 1234567890);
    assertEquals(order.getConfirmationTime(), 9876543210L);
    assertEquals(order.getStartTime(), 9876598760L);
    assertEquals(order.getScheduledStartTime(), 128937981273L);
    assertEquals(order.getServiceName(), "service");
    assertEquals(order.getDistance(), 546);
    assertEquals(order.getRoutePath(), Arrays.asList(routePoint, routePoint2, routePoint2));
    assertEquals(order.getOptions(),
        Arrays.asList(optionNumeric, optionBoolean, optionBoolean, optionNumeric)
    );
  }

  /**
   * Должен успешно преобразовать JSON без расчетного времени заказа в заказ.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingJsonStringWithoutEstimatedTimeToOrderSuccess() throws Exception {
    // Дано:
    when(stompMessage.getPayload()).thenReturn(rule.getOrderWithoutEstimatedTime());

    // Действие:
    Order order = mapper.map(stompMessage);

    // Результат:
    assertEquals(order.getId(), 7);
    assertEquals(order.getComment(), "some comment");
    assertEquals(order.getEstimatedPriceText(), "over 9999 BTC");
    assertEquals(order.getEstimatedPrice(), 9999);
    assertEquals(order.getEstimatedTime(), 0);
    assertEquals(order.getEstimatedRouteLength(), 35_213);
    assertEquals(order.getTotalCost(), 10_352);
    assertEquals(order.getTimeout(), 25);
    assertEquals(order.getEtaToStartPoint(), 1234567890);
    assertEquals(order.getConfirmationTime(), 9876543210L);
    assertEquals(order.getStartTime(), 9876598760L);
    assertEquals(order.getScheduledStartTime(), 128937981273L);
    assertEquals(order.getServiceName(), "service");
    assertEquals(order.getDistance(), 546);
    assertEquals(order.getRoutePath(), Arrays.asList(routePoint, routePoint2, routePoint2));
    assertEquals(order.getOptions(),
        Arrays.asList(optionNumeric, optionBoolean, optionBoolean, optionNumeric)
    );
  }

  /**
   * Должен успешно преобразовать JSON без расчетной длины маршрута в заказ.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingJsonStringWithoutEstimatedRouteLengthToOrderSuccess() throws Exception {
    // Дано:
    when(stompMessage.getPayload()).thenReturn(rule.getOrderWithoutEstimatedRouteDistance());

    // Действие:
    Order order = mapper.map(stompMessage);

    // Результат:
    assertEquals(order.getId(), 7);
    assertEquals(order.getComment(), "some comment");
    assertEquals(order.getEstimatedPriceText(), "over 9999 BTC");
    assertEquals(order.getEstimatedPrice(), 9999);
    assertEquals(order.getEstimatedTime(), 234_532_000);
    assertEquals(order.getEstimatedRouteLength(), 0);
    assertEquals(order.getTotalCost(), 10_352);
    assertEquals(order.getTimeout(), 25);
    assertEquals(order.getEtaToStartPoint(), 1234567890);
    assertEquals(order.getConfirmationTime(), 9876543210L);
    assertEquals(order.getStartTime(), 9876598760L);
    assertEquals(order.getScheduledStartTime(), 128937981273L);
    assertEquals(order.getServiceName(), "service");
    assertEquals(order.getDistance(), 546);
    assertEquals(order.getRoutePath(), Arrays.asList(routePoint, routePoint2, routePoint2));
    assertEquals(order.getOptions(),
        Arrays.asList(optionNumeric, optionBoolean, optionBoolean, optionNumeric)
    );
  }

  /**
   * Должен успешно преобразовать JSON без рассчетной стоимости в заказ.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingJsonStringWithoutCostToOrderSuccess() throws Exception {
    // Дано:
    when(stompMessage.getPayload()).thenReturn(rule.getOrderWithoutCost());

    // Действие:
    Order order = mapper.map(stompMessage);

    // Результат:
    assertEquals(order.getId(), 7);
    assertEquals(order.getComment(), "some comment");
    assertEquals(order.getEstimatedPriceText(), "over 9999 BTC");
    assertEquals(order.getEstimatedPrice(), 9999);
    assertEquals(order.getEstimatedTime(), 234_532_000);
    assertEquals(order.getEstimatedRouteLength(), 35_213);
    assertEquals(order.getTotalCost(), 0);
    assertEquals(order.getTimeout(), 25);
    assertEquals(order.getEtaToStartPoint(), 1234567890);
    assertEquals(order.getConfirmationTime(), 9876543210L);
    assertEquals(order.getStartTime(), 9876598760L);
    assertEquals(order.getScheduledStartTime(), 128937981273L);
    assertEquals(order.getServiceName(), "service");
    assertEquals(order.getDistance(), 546);
    assertEquals(order.getRoutePath(), Arrays.asList(routePoint, routePoint2, routePoint2));
    assertEquals(order.getOptions(),
        Arrays.asList(optionNumeric, optionBoolean, optionBoolean, optionNumeric)
    );
  }

  /**
   * Должен успешно преобразовать JSON без таймаута в заказ.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingJsonStringWithoutTimeoutToOrderSuccess() throws Exception {
    // Дано:
    when(stompMessage.getPayload()).thenReturn(rule.getOrderWithoutTimeout());

    // Действие:
    Order order = mapper.map(stompMessage);

    // Результат:
    assertEquals(order.getId(), 7);
    assertEquals(order.getComment(), "some comment");
    assertEquals(order.getEstimatedPriceText(), "over 9999 BTC");
    assertEquals(order.getEstimatedPrice(), 9999);
    assertEquals(order.getEstimatedTime(), 234_532_000);
    assertEquals(order.getEstimatedRouteLength(), 35_213);
    assertEquals(order.getTotalCost(), 10_352);
    assertEquals(order.getTimeout(), 0);
    assertEquals(order.getEtaToStartPoint(), 1234567890);
    assertEquals(order.getConfirmationTime(), 9876543210L);
    assertEquals(order.getStartTime(), 9876598760L);
    assertEquals(order.getScheduledStartTime(), 128937981273L);
    assertEquals(order.getServiceName(), "service");
    assertEquals(order.getDistance(), 546);
    assertEquals(order.getRoutePath(), Arrays.asList(routePoint, routePoint2, routePoint2));
    assertEquals(order.getOptions(),
        Arrays.asList(optionNumeric, optionBoolean, optionBoolean, optionNumeric)
    );
  }

  /**
   * Должен успешно преобразовать JSON без ETA в заказ.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingJsonStringWithoutEtaToOrderSuccess() throws Exception {
    // Дано:
    when(stompMessage.getPayload()).thenReturn(rule.getOrderWithoutEta());

    // Действие:
    Order order = mapper.map(stompMessage);

    // Результат:
    assertEquals(order.getId(), 7);
    assertEquals(order.getComment(), "some comment");
    assertEquals(order.getEstimatedPriceText(), "over 9999 BTC");
    assertEquals(order.getEstimatedPrice(), 9999);
    assertEquals(order.getEstimatedTime(), 234_532_000);
    assertEquals(order.getEstimatedRouteLength(), 35_213);
    assertEquals(order.getTotalCost(), 10_352);
    assertEquals(order.getTimeout(), 25);
    assertEquals(order.getEtaToStartPoint(), 0);
    assertEquals(order.getConfirmationTime(), 9876543210L);
    assertEquals(order.getStartTime(), 9876598760L);
    assertEquals(order.getScheduledStartTime(), 128937981273L);
    assertEquals(order.getServiceName(), "service");
    assertEquals(order.getDistance(), 546);
    assertEquals(order.getRoutePath(), Arrays.asList(routePoint, routePoint2, routePoint2));
    assertEquals(order.getOptions(),
        Arrays.asList(optionNumeric, optionBoolean, optionBoolean, optionNumeric)
    );
  }

  /**
   * Должен успешно преобразовать JSON без времени подтверждения в заказ.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingJsonStringWithoutConfirmationTimeToOrderSuccess() throws Exception {
    // Дано:
    when(stompMessage.getPayload()).thenReturn(rule.getOrderWithoutConfirmationTime());

    // Действие:
    Order order = mapper.map(stompMessage);

    // Результат:
    assertEquals(order.getId(), 7);
    assertEquals(order.getComment(), "some comment");
    assertEquals(order.getEstimatedPriceText(), "over 9999 BTC");
    assertEquals(order.getEstimatedPrice(), 9999);
    assertEquals(order.getEstimatedTime(), 234_532_000);
    assertEquals(order.getEstimatedRouteLength(), 35_213);
    assertEquals(order.getTotalCost(), 10_352);
    assertEquals(order.getTimeout(), 25);
    assertEquals(order.getEtaToStartPoint(), 1234567890);
    assertEquals(order.getConfirmationTime(), 0);
    assertEquals(order.getStartTime(), 9876598760L);
    assertEquals(order.getScheduledStartTime(), 128937981273L);
    assertEquals(order.getServiceName(), "service");
    assertEquals(order.getDistance(), 546);
    assertEquals(order.getRoutePath(), Arrays.asList(routePoint, routePoint2, routePoint2));
    assertEquals(order.getOptions(),
        Arrays.asList(optionNumeric, optionBoolean, optionBoolean, optionNumeric)
    );
  }

  /**
   * Должен успешно преобразовать JSON без времени начала в заказ.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingJsonStringWithoutStartTimeToOrderSuccess() throws Exception {
    // Дано:
    when(stompMessage.getPayload()).thenReturn(rule.getOrderWithoutStartTime());

    // Действие:
    Order order = mapper.map(stompMessage);

    // Результат:
    assertEquals(order.getId(), 7);
    assertEquals(order.getComment(), "some comment");
    assertEquals(order.getEstimatedPriceText(), "over 9999 BTC");
    assertEquals(order.getEstimatedPrice(), 9999);
    assertEquals(order.getEstimatedTime(), 234_532_000);
    assertEquals(order.getEstimatedRouteLength(), 35_213);
    assertEquals(order.getTotalCost(), 10_352);
    assertEquals(order.getTimeout(), 25);
    assertEquals(order.getEtaToStartPoint(), 1234567890);
    assertEquals(order.getConfirmationTime(), 9876543210L);
    assertEquals(order.getStartTime(), 0);
    assertEquals(order.getScheduledStartTime(), 128937981273L);
    assertEquals(order.getServiceName(), "service");
    assertEquals(order.getDistance(), 546);
    assertEquals(order.getRoutePath(), Arrays.asList(routePoint, routePoint2, routePoint2));
    assertEquals(order.getOptions(),
        Arrays.asList(optionNumeric, optionBoolean, optionBoolean, optionNumeric)
    );
  }

  /**
   * Должен успешно преобразовать JSON без планируемого времени начала в заказ.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingJsonStringWithoutScheduledStartTimeToOrderSuccess() throws Exception {
    // Дано:
    when(stompMessage.getPayload()).thenReturn(rule.getOrderWithoutScheduledStartTime());

    // Действие:
    Order order = mapper.map(stompMessage);

    // Результат:
    assertEquals(order.getId(), 7);
    assertEquals(order.getComment(), "some comment");
    assertEquals(order.getEstimatedPriceText(), "over 9999 BTC");
    assertEquals(order.getEstimatedPrice(), 9999);
    assertEquals(order.getEstimatedTime(), 234_532_000);
    assertEquals(order.getEstimatedRouteLength(), 35_213);
    assertEquals(order.getTotalCost(), 10_352);
    assertEquals(order.getTimeout(), 25);
    assertEquals(order.getEtaToStartPoint(), 1234567890);
    assertEquals(order.getConfirmationTime(), 9876543210L);
    assertEquals(order.getStartTime(), 9876598760L);
    assertEquals(order.getScheduledStartTime(), 0);
    assertEquals(order.getServiceName(), "service");
    assertEquals(order.getDistance(), 546);
    assertEquals(order.getRoutePath(), Arrays.asList(routePoint, routePoint2, routePoint2));
    assertEquals(order.getOptions(),
        Arrays.asList(optionNumeric, optionBoolean, optionBoolean, optionNumeric)
    );
  }

  /**
   * Должен успешно преобразовать JSON без ИД в дистанции в заказ.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingJsonStringWithoutDistanceIdToOrderSuccess() throws Exception {
    // Дано:
    when(stompMessage.getPayload()).thenReturn(rule.getOrderWithoutDistanceId());

    // Действие:
    Order order = mapper.map(stompMessage);

    // Результат:
    assertEquals(order.getId(), 7);
    assertEquals(order.getComment(), "some comment");
    assertEquals(order.getEstimatedPriceText(), "over 9999 BTC");
    assertEquals(order.getEstimatedPrice(), 9999);
    assertEquals(order.getEstimatedTime(), 234_532_000);
    assertEquals(order.getEstimatedRouteLength(), 35_213);
    assertEquals(order.getTotalCost(), 10_352);
    assertEquals(order.getTimeout(), 25);
    assertEquals(order.getEtaToStartPoint(), 1234567890);
    assertEquals(order.getConfirmationTime(), 9876543210L);
    assertEquals(order.getStartTime(), 9876598760L);
    assertEquals(order.getScheduledStartTime(), 128937981273L);
    assertEquals(order.getServiceName(), "service");
    assertEquals(order.getDistance(), 546);
    assertEquals(order.getRoutePath(), Arrays.asList(routePoint, routePoint2, routePoint2));
    assertEquals(order.getOptions(),
        Arrays.asList(optionNumeric, optionBoolean, optionBoolean, optionNumeric)
    );
  }

  /**
   * Должен дать ошибку, если пришел JSON без значения дистанции.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingJsonStringWithoutDistanceValueToOrderFail() throws Exception {
    // Дано:
    when(stompMessage.getPayload()).thenReturn(rule.getOrderWithoutDistanceValue());

    // Действие:
    Order order = mapper.map(stompMessage);

    // Результат:
    assertEquals(order.getId(), 7);
    assertEquals(order.getComment(), "some comment");
    assertEquals(order.getEstimatedPriceText(), "over 9999 BTC");
    assertEquals(order.getEstimatedPrice(), 9999);
    assertEquals(order.getEstimatedTime(), 234_532_000);
    assertEquals(order.getEstimatedRouteLength(), 35_213);
    assertEquals(order.getTotalCost(), 10_352);
    assertEquals(order.getTimeout(), 25);
    assertEquals(order.getEtaToStartPoint(), 1234567890);
    assertEquals(order.getConfirmationTime(), 9876543210L);
    assertEquals(order.getStartTime(), 9876598760L);
    assertEquals(order.getScheduledStartTime(), 128937981273L);
    assertEquals(order.getServiceName(), "service");
    assertEquals(order.getDistance(), 0);
    assertEquals(order.getRoutePath(), Arrays.asList(routePoint, routePoint2, routePoint2));
    assertEquals(order.getOptions(),
        Arrays.asList(optionNumeric, optionBoolean, optionBoolean, optionNumeric)
    );
  }

  /**
   * Должен успешно преобразовать JSON без дистанции в заказ.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingJsonStringWithoutDistanceToOrderSuccess() throws Exception {
    // Дано:
    when(stompMessage.getPayload()).thenReturn(rule.getOrderWithoutDistance());

    // Действие:
    Order order = mapper.map(stompMessage);

    // Результат:
    assertEquals(order.getId(), 7);
    assertEquals(order.getComment(), "some comment");
    assertEquals(order.getEstimatedPriceText(), "over 9999 BTC");
    assertEquals(order.getEstimatedPrice(), 9999);
    assertEquals(order.getEstimatedTime(), 234_532_000);
    assertEquals(order.getEstimatedRouteLength(), 35_213);
    assertEquals(order.getTotalCost(), 10_352);
    assertEquals(order.getTimeout(), 25);
    assertEquals(order.getEtaToStartPoint(), 1234567890);
    assertEquals(order.getConfirmationTime(), 9876543210L);
    assertEquals(order.getStartTime(), 9876598760L);
    assertEquals(order.getScheduledStartTime(), 128937981273L);
    assertEquals(order.getServiceName(), "service");
    assertEquals(order.getDistance(), 0);
    assertEquals(order.getRoutePath(), Arrays.asList(routePoint, routePoint2, routePoint2));
    assertEquals(order.getOptions(),
        Arrays.asList(optionNumeric, optionBoolean, optionBoolean, optionNumeric)
    );
  }

  /**
   * Должен успешно преобразовать JSON без ИД в услуге в заказ.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingJsonStringWithoutServiceIdToOrderSuccess() throws Exception {
    // Дано:
    when(stompMessage.getPayload()).thenReturn(rule.getOrderWithoutServiceId());

    // Действие:
    Order order = mapper.map(stompMessage);

    // Результат:
    assertEquals(order.getId(), 7);
    assertEquals(order.getComment(), "some comment");
    assertEquals(order.getEstimatedPriceText(), "over 9999 BTC");
    assertEquals(order.getEstimatedPrice(), 9999);
    assertEquals(order.getEstimatedTime(), 234_532_000);
    assertEquals(order.getEstimatedRouteLength(), 35_213);
    assertEquals(order.getTotalCost(), 10_352);
    assertEquals(order.getTimeout(), 25);
    assertEquals(order.getEtaToStartPoint(), 1234567890);
    assertEquals(order.getConfirmationTime(), 9876543210L);
    assertEquals(order.getStartTime(), 9876598760L);
    assertEquals(order.getScheduledStartTime(), 128937981273L);
    assertEquals(order.getServiceName(), "service");
    assertEquals(order.getDistance(), 546);
    assertEquals(order.getRoutePath(), Arrays.asList(routePoint, routePoint2, routePoint2));
    assertEquals(order.getOptions(),
        Arrays.asList(optionNumeric, optionBoolean, optionBoolean, optionNumeric)
    );
  }

  /**
   * Должен дать ошибку, если пришел JSON без имени услуги.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingJsonStringWithoutServiceNameToOrderFail() throws Exception {
    // Дано:
    when(stompMessage.getPayload()).thenReturn(rule.getOrderWithoutServiceName());

    // Действие:
    Order order = mapper.map(stompMessage);

    // Результат:
    assertEquals(order.getId(), 7);
    assertEquals(order.getComment(), "some comment");
    assertEquals(order.getEstimatedPriceText(), "over 9999 BTC");
    assertEquals(order.getEstimatedPrice(), 9999);
    assertEquals(order.getEstimatedTime(), 234_532_000);
    assertEquals(order.getEstimatedRouteLength(), 35_213);
    assertEquals(order.getTotalCost(), 10_352);
    assertEquals(order.getTimeout(), 25);
    assertEquals(order.getEtaToStartPoint(), 1234567890);
    assertEquals(order.getConfirmationTime(), 9876543210L);
    assertEquals(order.getStartTime(), 9876598760L);
    assertEquals(order.getScheduledStartTime(), 128937981273L);
    assertEquals(order.getServiceName(), "");
    assertEquals(order.getDistance(), 546);
    assertEquals(order.getRoutePath(), Arrays.asList(routePoint, routePoint2, routePoint2));
    assertEquals(order.getOptions(),
        Arrays.asList(optionNumeric, optionBoolean, optionBoolean, optionNumeric)
    );
  }

  /**
   * Должен успешно преобразовать JSON без цены услуги в заказ.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingJsonStringWithoutServicePriceToOrderSuccess() throws Exception {
    // Дано:
    when(stompMessage.getPayload()).thenReturn(rule.getOrderWithoutServicePrice());

    // Действие:
    Order order = mapper.map(stompMessage);

    // Результат:
    assertEquals(order.getId(), 7);
    assertEquals(order.getComment(), "some comment");
    assertEquals(order.getEstimatedPriceText(), "over 9999 BTC");
    assertEquals(order.getEstimatedPrice(), 9999);
    assertEquals(order.getEstimatedTime(), 234_532_000);
    assertEquals(order.getEstimatedRouteLength(), 35_213);
    assertEquals(order.getTotalCost(), 10_352);
    assertEquals(order.getTimeout(), 25);
    assertEquals(order.getEtaToStartPoint(), 1234567890);
    assertEquals(order.getConfirmationTime(), 9876543210L);
    assertEquals(order.getStartTime(), 9876598760L);
    assertEquals(order.getScheduledStartTime(), 128937981273L);
    assertEquals(order.getServiceName(), "service");
    assertEquals(order.getDistance(), 546);
    assertEquals(order.getRoutePath(), Arrays.asList(routePoint, routePoint2, routePoint2));
    assertEquals(order.getOptions(),
        Arrays.asList(optionNumeric, optionBoolean, optionBoolean, optionNumeric)
    );
  }

  /**
   * Должен дать ошибку, если пришел JSON без услуги в заказ.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingJsonStringWithoutServiceToOrderFail() throws Exception {
    // Дано:
    when(stompMessage.getPayload()).thenReturn(rule.getOrderWithoutService());

    // Действие:
    Order order = mapper.map(stompMessage);

    // Результат:
    assertEquals(order.getId(), 7);
    assertEquals(order.getComment(), "some comment");
    assertEquals(order.getEstimatedPriceText(), "over 9999 BTC");
    assertEquals(order.getEstimatedPrice(), 9999);
    assertEquals(order.getEstimatedTime(), 234_532_000);
    assertEquals(order.getEstimatedRouteLength(), 35_213);
    assertEquals(order.getTotalCost(), 10_352);
    assertEquals(order.getTimeout(), 25);
    assertEquals(order.getEtaToStartPoint(), 1234567890);
    assertEquals(order.getConfirmationTime(), 9876543210L);
    assertEquals(order.getStartTime(), 9876598760L);
    assertEquals(order.getScheduledStartTime(), 128937981273L);
    assertEquals(order.getServiceName(), "");
    assertEquals(order.getDistance(), 546);
    assertEquals(order.getRoutePath(), Arrays.asList(routePoint, routePoint2, routePoint2));
    assertEquals(order.getOptions(),
        Arrays.asList(optionNumeric, optionBoolean, optionBoolean, optionNumeric)
    );
  }

  /**
   * Должен дать ошибку, если пришел JSON с пустым маршрутом.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingJsonStringWithEmptyRouteToOrderFail() throws Exception {
    // Дано:
    when(stompMessage.getPayload()).thenReturn(rule.getOrderWithEmptyRoute());

    // Действие:
    Order order = mapper.map(stompMessage);

    // Результат:
    assertEquals(order.getId(), 7);
    assertEquals(order.getComment(), "some comment");
    assertEquals(order.getEstimatedPriceText(), "over 9999 BTC");
    assertEquals(order.getEstimatedPrice(), 9999);
    assertEquals(order.getEstimatedTime(), 234_532_000);
    assertEquals(order.getEstimatedRouteLength(), 35_213);
    assertEquals(order.getTotalCost(), 10_352);
    assertEquals(order.getTimeout(), 25);
    assertEquals(order.getEtaToStartPoint(), 1234567890);
    assertEquals(order.getConfirmationTime(), 9876543210L);
    assertEquals(order.getStartTime(), 9876598760L);
    assertEquals(order.getScheduledStartTime(), 128937981273L);
    assertEquals(order.getServiceName(), "service");
    assertEquals(order.getDistance(), 546);
    assertEquals(order.getRoutePath(), new ArrayList<>());
    assertEquals(order.getOptions(),
        Arrays.asList(optionNumeric, optionBoolean, optionBoolean, optionNumeric)
    );
  }

  /**
   * Должен дать ошибку, если пришел JSON без маршрута.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingJsonStringWithoutRouteToOrderFail() throws Exception {
    // Дано:
    when(stompMessage.getPayload()).thenReturn(rule.getOrderWithoutRoute());

    // Действие:
    Order order = mapper.map(stompMessage);

    // Результат:
    assertEquals(order.getId(), 7);
    assertEquals(order.getComment(), "some comment");
    assertEquals(order.getEstimatedPriceText(), "over 9999 BTC");
    assertEquals(order.getEstimatedPrice(), 9999);
    assertEquals(order.getEstimatedTime(), 234_532_000);
    assertEquals(order.getEstimatedRouteLength(), 35_213);
    assertEquals(order.getTotalCost(), 10_352);
    assertEquals(order.getTimeout(), 25);
    assertEquals(order.getEtaToStartPoint(), 1234567890);
    assertEquals(order.getConfirmationTime(), 9876543210L);
    assertEquals(order.getStartTime(), 9876598760L);
    assertEquals(order.getScheduledStartTime(), 128937981273L);
    assertEquals(order.getServiceName(), "service");
    assertEquals(order.getDistance(), 546);
    assertEquals(order.getRoutePath(), new ArrayList<>());
    assertEquals(order.getOptions(),
        Arrays.asList(optionNumeric, optionBoolean, optionBoolean, optionNumeric)
    );
  }

  /**
   * Должен успешно преобразовать JSON с пустым списком опций в заказ.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingJsonStringWithEmptyOptionsToOrderSuccess() throws Exception {
    // Дано:
    when(stompMessage.getPayload()).thenReturn(rule.getOrderWithEmptyOptions());

    // Действие:
    Order order = mapper.map(stompMessage);

    // Результат:
    assertEquals(order.getId(), 7);
    assertEquals(order.getComment(), "some comment");
    assertEquals(order.getEstimatedPriceText(), "over 9999 BTC");
    assertEquals(order.getEstimatedPrice(), 9999);
    assertEquals(order.getEstimatedTime(), 234_532_000);
    assertEquals(order.getEstimatedRouteLength(), 35_213);
    assertEquals(order.getTotalCost(), 10_352);
    assertEquals(order.getTimeout(), 25);
    assertEquals(order.getEtaToStartPoint(), 1234567890);
    assertEquals(order.getConfirmationTime(), 9876543210L);
    assertEquals(order.getStartTime(), 9876598760L);
    assertEquals(order.getScheduledStartTime(), 128937981273L);
    assertEquals(order.getServiceName(), "service");
    assertEquals(order.getDistance(), 546);
    assertEquals(order.getRoutePath(), Arrays.asList(routePoint, routePoint2, routePoint2));
    assertEquals(order.getOptions(), new ArrayList<>());
  }

  /**
   * Должен успешно преобразовать JSON без опций в заказ.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingJsonStringWithoutOptionsToOrderSuccess() throws Exception {
    // Дано:
    when(stompMessage.getPayload()).thenReturn(rule.getOrderWithoutOptions());

    // Действие:
    Order order = mapper.map(stompMessage);

    // Результат:
    assertEquals(order.getId(), 7);
    assertEquals(order.getComment(), "some comment");
    assertEquals(order.getEstimatedPriceText(), "over 9999 BTC");
    assertEquals(order.getEstimatedPrice(), 9999);
    assertEquals(order.getEstimatedTime(), 234_532_000);
    assertEquals(order.getEstimatedRouteLength(), 35_213);
    assertEquals(order.getTotalCost(), 10_352);
    assertEquals(order.getTimeout(), 25);
    assertEquals(order.getEtaToStartPoint(), 1234567890);
    assertEquals(order.getConfirmationTime(), 9876543210L);
    assertEquals(order.getStartTime(), 9876598760L);
    assertEquals(order.getScheduledStartTime(), 128937981273L);
    assertEquals(order.getServiceName(), "service");
    assertEquals(order.getDistance(), 546);
    assertEquals(order.getRoutePath(), Arrays.asList(routePoint, routePoint2, routePoint2));
    assertEquals(order.getOptions(), new ArrayList<>());
  }

  /**
   * Должен дать ошибку, если была ошибка маппинга адреса.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingFailForRoutePointMappingError() throws Exception {
    // Дано:
    when(stompMessage.getPayload()).thenReturn(rule.getFullOrder());
    doThrow(new DataMappingException()).when(routePointMapper).map(any(ApiRoutePoint.class));

    // Действие:
    mapper.map(stompMessage);
  }

  /**
   * Должен дать ошибку, если была ошибка маппинга опции.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingFailForOptionMappingError() throws Exception {
    // Дано:
    when(stompMessage.getPayload()).thenReturn(rule.getFullOrder());
    doThrow(new DataMappingException()).when(apiOptionMapper).map(any(ApiOptionItem.class));

    // Действие:
    mapper.map(stompMessage);
  }

  /**
   * Должен дать ошибку, если строка пустая.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingEmptyFail() throws Exception {
    // Дано:
    when(stompMessage.getPayload()).thenReturn("");

    // Действие:
    mapper.map(stompMessage);
  }

  /**
   * Должен дать ошибку, если пришла просто строка.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingStringFail() throws Exception {
    // Дано:
    when(stompMessage.getPayload()).thenReturn("dasie");

    // Действие:
    mapper.map(stompMessage);
  }

  /**
   * Должен дать ошибку, если пришло просто число.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingNumberFail() throws Exception {
    // Дано:
    when(stompMessage.getPayload()).thenReturn("12");

    // Действие:
    mapper.map(stompMessage);
  }

  /**
   * Должен дать ошибку, если пришел массив.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingArrayFail() throws Exception {
    // Дано:
    when(stompMessage.getPayload()).thenReturn("[]");

    // Действие:
    mapper.map(stompMessage);
  }

  /**
   * Должен дать ошибку, если пришел null.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingNullFail() throws Exception {
    // Действие:
    mapper.map(stompMessage);
  }
}