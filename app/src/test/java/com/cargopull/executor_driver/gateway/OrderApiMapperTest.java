package com.cargopull.executor_driver.gateway;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import com.cargopull.executor_driver.backend.web.incoming.ApiOptionItem;
import com.cargopull.executor_driver.backend.web.incoming.ApiOrder;
import com.cargopull.executor_driver.backend.web.incoming.ApiRoutePoint;
import com.cargopull.executor_driver.entity.Option;
import com.cargopull.executor_driver.entity.OptionBoolean;
import com.cargopull.executor_driver.entity.OptionNumeric;
import com.cargopull.executor_driver.entity.Order;
import com.cargopull.executor_driver.entity.PaymentType;
import com.cargopull.executor_driver.entity.RoutePoint;
import com.cargopull.executor_driver.entity.RouteType;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.Arrays;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class OrderApiMapperTest {

  @Rule
  public final ApiOrderRule rule = new ApiOrderRule();

  private Mapper<ApiOrder, Order> mapper;

  @Mock
  private Mapper<ApiOptionItem, Option> apiOptionMapper;
  @Mock
  private Mapper<ApiRoutePoint, RoutePoint> routePointMapper;
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
    mapper = new OrderApiMapper(apiOptionMapper, routePointMapper);
  }

  /**
   * Должен успешно преобразовать JSON в заказ.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingJsonStringToOrderSuccess() throws Exception {
    // Дано:
    ApiOrder apiOrder = new Gson().fromJson(rule.getFullOrder(), ApiOrder.class);

    // Действие:
    Order order = mapper.map(apiOrder);

    // Результат:
    assertEquals(order.getId(), 7);
    assertEquals(order.getPaymentType(), PaymentType.CASH);
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
    assertEquals(order.getRouteType(), RouteType.POLYGON);
    assertEquals(order.getServiceName(), "service");
    assertEquals(order.getDistance(), 546);
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
    ApiOrder apiOrder = new Gson().fromJson(rule.getOrderWithoutId(), ApiOrder.class);

    // Действие:
    Order order = mapper.map(apiOrder);

    // Результат:
    assertEquals(order.getId(), 0);
    assertEquals(order.getPaymentType(), PaymentType.CASH);
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
    assertEquals(order.getRouteType(), RouteType.POLYGON);
    assertEquals(order.getServiceName(), "service");
    assertEquals(order.getDistance(), 546);
    assertEquals(order.getRoutePath(), Arrays.asList(routePoint, routePoint2, routePoint2));
    assertEquals(order.getOptions(),
        Arrays.asList(optionNumeric, optionBoolean, optionBoolean, optionNumeric)
    );
  }

  /**
   * Должен дать ошибку, если пришел JSON без типа оплаты.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingJsonStringWithoutPaymentTypeToOrderFail() throws Exception {
    // Дано:
    ApiOrder apiOrder = new Gson().fromJson(rule.getOrderWithoutPaymentType(), ApiOrder.class);

    // Действие:
    Order order = mapper.map(apiOrder);

    // Результат:
    assertEquals(order.getId(), 7);
    assertEquals(order.getPaymentType(), PaymentType.CASH);
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
    assertEquals(order.getRouteType(), RouteType.POLYGON);
    assertEquals(order.getServiceName(), "service");
    assertEquals(order.getDistance(), 546);
    assertEquals(order.getRoutePath(), Arrays.asList(routePoint, routePoint2, routePoint2));
    assertEquals(order.getOptions(),
        Arrays.asList(optionNumeric, optionBoolean, optionBoolean, optionNumeric)
    );
  }

  /**
   * Должен дать ошибку, если пришел JSON с пустым типом оплаты.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingJsonStringWithEmptyPaymentTypeToOrderFail() throws Exception {
    // Дано:
    ApiOrder apiOrder = new Gson().fromJson(rule.getOrderWithEmptyPaymentType(), ApiOrder.class);

    // Действие:
    Order order = mapper.map(apiOrder);

    // Результат:
    assertEquals(order.getId(), 7);
    assertEquals(order.getPaymentType(), PaymentType.CASH);
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
    assertEquals(order.getRouteType(), RouteType.POLYGON);
    assertEquals(order.getServiceName(), "service");
    assertEquals(order.getDistance(), 546);
    assertEquals(order.getRoutePath(), Arrays.asList(routePoint, routePoint2, routePoint2));
    assertEquals(order.getOptions(),
        Arrays.asList(optionNumeric, optionBoolean, optionBoolean, optionNumeric)
    );
  }

  /**
   * Должен дать ошибку, если пришел JSON с неверным типом оплаты.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingJsonStringWithWrongPaymentTypeToOrderFail() throws Exception {
    // Дано:
    ApiOrder apiOrder = new Gson().fromJson(rule.getOrderWithWrongPaymentType(), ApiOrder.class);

    // Действие:
    Order order = mapper.map(apiOrder);

    // Результат:
    assertEquals(order.getId(), 7);
    assertEquals(order.getPaymentType(), PaymentType.CASH);
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
    assertEquals(order.getRouteType(), RouteType.POLYGON);
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
    ApiOrder apiOrder = new Gson().fromJson(rule.getOrderWithoutComment(), ApiOrder.class);

    // Действие:
    Order order = mapper.map(apiOrder);

    // Результат:
    assertEquals(order.getId(), 7);
    assertEquals(order.getPaymentType(), PaymentType.CASH);
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
    assertEquals(order.getRouteType(), RouteType.POLYGON);
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
    ApiOrder apiOrder = new Gson()
        .fromJson(rule.getOrderWithoutEstimatedAmountText(), ApiOrder.class);

    // Действие:
    Order order = mapper.map(apiOrder);

    // Результат:
    assertEquals(order.getId(), 7);
    assertEquals(order.getPaymentType(), PaymentType.CASH);
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
    assertEquals(order.getRouteType(), RouteType.POLYGON);
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
    ApiOrder apiOrder = new Gson().fromJson(rule.getOrderWithoutEstimatedAmount(), ApiOrder.class);

    // Действие:
    Order order = mapper.map(apiOrder);

    // Результат:
    assertEquals(order.getId(), 7);
    assertEquals(order.getPaymentType(), PaymentType.CASH);
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
    assertEquals(order.getRouteType(), RouteType.POLYGON);
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
    ApiOrder apiOrder = new Gson().fromJson(rule.getOrderWithoutEstimatedTime(), ApiOrder.class);

    // Действие:
    Order order = mapper.map(apiOrder);

    // Результат:
    assertEquals(order.getId(), 7);
    assertEquals(order.getPaymentType(), PaymentType.CASH);
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
    assertEquals(order.getRouteType(), RouteType.POLYGON);
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
    ApiOrder apiOrder = new Gson()
        .fromJson(rule.getOrderWithoutEstimatedRouteDistance(), ApiOrder.class);

    // Действие:
    Order order = mapper.map(apiOrder);

    // Результат:
    assertEquals(order.getId(), 7);
    assertEquals(order.getPaymentType(), PaymentType.CASH);
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
    assertEquals(order.getRouteType(), RouteType.POLYGON);
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
    ApiOrder apiOrder = new Gson().fromJson(rule.getOrderWithoutCost(), ApiOrder.class);

    // Действие:
    Order order = mapper.map(apiOrder);

    // Результат:
    assertEquals(order.getId(), 7);
    assertEquals(order.getPaymentType(), PaymentType.CASH);
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
    assertEquals(order.getRouteType(), RouteType.POLYGON);
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
    ApiOrder apiOrder = new Gson().fromJson(rule.getOrderWithoutTimeout(), ApiOrder.class);

    // Действие:
    Order order = mapper.map(apiOrder);

    // Результат:
    assertEquals(order.getId(), 7);
    assertEquals(order.getPaymentType(), PaymentType.CASH);
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
    assertEquals(order.getRouteType(), RouteType.POLYGON);
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
    ApiOrder apiOrder = new Gson().fromJson(rule.getOrderWithoutEta(), ApiOrder.class);

    // Действие:
    Order order = mapper.map(apiOrder);

    // Результат:
    assertEquals(order.getId(), 7);
    assertEquals(order.getPaymentType(), PaymentType.CASH);
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
    assertEquals(order.getRouteType(), RouteType.POLYGON);
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
    ApiOrder apiOrder = new Gson().fromJson(rule.getOrderWithoutConfirmationTime(), ApiOrder.class);

    // Действие:
    Order order = mapper.map(apiOrder);

    // Результат:
    assertEquals(order.getId(), 7);
    assertEquals(order.getPaymentType(), PaymentType.CASH);
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
    assertEquals(order.getRouteType(), RouteType.POLYGON);
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
    ApiOrder apiOrder = new Gson().fromJson(rule.getOrderWithoutStartTime(), ApiOrder.class);

    // Действие:
    Order order = mapper.map(apiOrder);

    // Результат:
    assertEquals(order.getId(), 7);
    assertEquals(order.getPaymentType(), PaymentType.CASH);
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
    assertEquals(order.getRouteType(), RouteType.POLYGON);
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
    ApiOrder apiOrder = new Gson()
        .fromJson(rule.getOrderWithoutScheduledStartTime(), ApiOrder.class);

    // Действие:
    Order order = mapper.map(apiOrder);

    // Результат:
    assertEquals(order.getId(), 7);
    assertEquals(order.getPaymentType(), PaymentType.CASH);
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
    assertEquals(order.getRouteType(), RouteType.POLYGON);
    assertEquals(order.getServiceName(), "service");
    assertEquals(order.getDistance(), 546);
    assertEquals(order.getRoutePath(), Arrays.asList(routePoint, routePoint2, routePoint2));
    assertEquals(order.getOptions(),
        Arrays.asList(optionNumeric, optionBoolean, optionBoolean, optionNumeric)
    );
  }

  /**
   * Должен дать ошибку, если пришел JSON без типа маршрута.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingJsonStringWithoutRouteTypeToOrderFail() throws Exception {
    // Дано:
    ApiOrder apiOrder = new Gson().fromJson(rule.getOrderWithoutRouteType(), ApiOrder.class);

    // Действие:
    Order order = mapper.map(apiOrder);

    // Результат:
    assertEquals(order.getId(), 7);
    assertEquals(order.getPaymentType(), PaymentType.CASH);
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
    assertEquals(order.getRouteType(), RouteType.POLYGON);
    assertEquals(order.getServiceName(), "service");
    assertEquals(order.getDistance(), 546);
    assertEquals(order.getRoutePath(), Arrays.asList(routePoint, routePoint2, routePoint2));
    assertEquals(order.getOptions(),
        Arrays.asList(optionNumeric, optionBoolean, optionBoolean, optionNumeric)
    );
  }

  /**
   * Должен дать ошибку, если пришел JSON с пустым типом маршрута.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingJsonStringWithEmptyRouteTypeToOrderFail() throws Exception {
    // Дано:
    ApiOrder apiOrder = new Gson().fromJson(rule.getOrderWithEmptyRouteType(), ApiOrder.class);

    // Действие:
    Order order = mapper.map(apiOrder);

    // Результат:
    assertEquals(order.getId(), 7);
    assertEquals(order.getPaymentType(), PaymentType.CASH);
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
    assertEquals(order.getRouteType(), RouteType.POLYGON);
    assertEquals(order.getServiceName(), "service");
    assertEquals(order.getDistance(), 546);
    assertEquals(order.getRoutePath(), Arrays.asList(routePoint, routePoint2, routePoint2));
    assertEquals(order.getOptions(),
        Arrays.asList(optionNumeric, optionBoolean, optionBoolean, optionNumeric)
    );
  }

  /**
   * Должен дать ошибку, если пришел JSON с неверным типом маршрута.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingJsonStringWithWrongRouteTypeToOrderFail() throws Exception {
    // Дано:
    ApiOrder apiOrder = new Gson().fromJson(rule.getOrderWithWrongRouteType(), ApiOrder.class);

    // Действие:
    Order order = mapper.map(apiOrder);

    // Результат:
    assertEquals(order.getId(), 7);
    assertEquals(order.getPaymentType(), PaymentType.CASH);
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
    assertEquals(order.getRouteType(), RouteType.POLYGON);
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
    ApiOrder apiOrder = new Gson().fromJson(rule.getOrderWithoutDistanceId(), ApiOrder.class);

    // Действие:
    Order order = mapper.map(apiOrder);

    // Результат:
    assertEquals(order.getId(), 7);
    assertEquals(order.getPaymentType(), PaymentType.CASH);
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
    assertEquals(order.getRouteType(), RouteType.POLYGON);
    assertEquals(order.getServiceName(), "service");
    assertEquals(order.getDistance(), 546);
    assertEquals(order.getRoutePath(), Arrays.asList(routePoint, routePoint2, routePoint2));
    assertEquals(order.getOptions(),
        Arrays.asList(optionNumeric, optionBoolean, optionBoolean, optionNumeric)
    );
  }

  /**
   * Должен успешно преобразовать JSON без значения дистанции.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingJsonStringWithoutDistanceValueToOrderSuccess() throws Exception {
    // Дано:
    ApiOrder apiOrder = new Gson().fromJson(rule.getOrderWithoutDistanceValue(), ApiOrder.class);

    // Действие:
    Order order = mapper.map(apiOrder);

    // Результат:
    assertEquals(order.getId(), 7);
    assertEquals(order.getPaymentType(), PaymentType.CASH);
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
    assertEquals(order.getRouteType(), RouteType.POLYGON);
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
    ApiOrder apiOrder = new Gson().fromJson(rule.getOrderWithoutDistance(), ApiOrder.class);

    // Действие:
    Order order = mapper.map(apiOrder);

    // Результат:
    assertEquals(order.getId(), 7);
    assertEquals(order.getPaymentType(), PaymentType.CASH);
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
    assertEquals(order.getRouteType(), RouteType.POLYGON);
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
    ApiOrder apiOrder = new Gson().fromJson(rule.getOrderWithoutServiceId(), ApiOrder.class);

    // Действие:
    Order order = mapper.map(apiOrder);

    // Результат:
    assertEquals(order.getId(), 7);
    assertEquals(order.getPaymentType(), PaymentType.CASH);
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
    assertEquals(order.getRouteType(), RouteType.POLYGON);
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
    ApiOrder apiOrder = new Gson().fromJson(rule.getOrderWithoutServiceName(), ApiOrder.class);

    // Действие:
    Order order = mapper.map(apiOrder);

    // Результат:
    assertEquals(order.getId(), 7);
    assertEquals(order.getPaymentType(), PaymentType.CASH);
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
    assertEquals(order.getRouteType(), RouteType.POLYGON);
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
    ApiOrder apiOrder = new Gson().fromJson(rule.getOrderWithoutServicePrice(), ApiOrder.class);

    // Действие:
    Order order = mapper.map(apiOrder);

    // Результат:
    assertEquals(order.getId(), 7);
    assertEquals(order.getPaymentType(), PaymentType.CASH);
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
    assertEquals(order.getRouteType(), RouteType.POLYGON);
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
    ApiOrder apiOrder = new Gson().fromJson(rule.getOrderWithoutService(), ApiOrder.class);

    // Действие:
    Order order = mapper.map(apiOrder);

    // Результат:
    assertEquals(order.getId(), 7);
    assertEquals(order.getPaymentType(), PaymentType.CASH);
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
    assertEquals(order.getRouteType(), RouteType.POLYGON);
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
    ApiOrder apiOrder = new Gson().fromJson(rule.getOrderWithEmptyRoute(), ApiOrder.class);

    // Действие:
    Order order = mapper.map(apiOrder);

    // Результат:
    assertEquals(order.getId(), 7);
    assertEquals(order.getPaymentType(), PaymentType.CASH);
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
    assertEquals(order.getRouteType(), RouteType.POLYGON);
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
    ApiOrder apiOrder = new Gson().fromJson(rule.getOrderWithoutRoute(), ApiOrder.class);

    // Действие:
    Order order = mapper.map(apiOrder);

    // Результат:
    assertEquals(order.getId(), 7);
    assertEquals(order.getPaymentType(), PaymentType.CASH);
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
    assertEquals(order.getRouteType(), RouteType.POLYGON);
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
    ApiOrder apiOrder = new Gson().fromJson(rule.getOrderWithEmptyOptions(), ApiOrder.class);

    // Действие:
    Order order = mapper.map(apiOrder);

    // Результат:
    assertEquals(order.getId(), 7);
    assertEquals(order.getPaymentType(), PaymentType.CASH);
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
    assertEquals(order.getRouteType(), RouteType.POLYGON);
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
    ApiOrder apiOrder = new Gson().fromJson(rule.getOrderWithoutOptions(), ApiOrder.class);

    // Действие:
    Order order = mapper.map(apiOrder);

    // Результат:
    assertEquals(order.getId(), 7);
    assertEquals(order.getPaymentType(), PaymentType.CASH);
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
    assertEquals(order.getRouteType(), RouteType.POLYGON);
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
    ApiOrder apiOrder = new Gson().fromJson(rule.getFullOrder(), ApiOrder.class);
    doThrow(new DataMappingException()).when(routePointMapper).map(any(ApiRoutePoint.class));

    // Действие:
    mapper.map(apiOrder);
  }

  /**
   * Должен дать ошибку, если была ошибка маппинга опции.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingFailForOptionMappingError() throws Exception {
    // Дано:
    ApiOrder apiOrder = new Gson().fromJson(rule.getFullOrder(), ApiOrder.class);
    doThrow(new DataMappingException()).when(apiOptionMapper).map(any(ApiOptionItem.class));

    // Действие:
    mapper.map(apiOrder);
  }
}