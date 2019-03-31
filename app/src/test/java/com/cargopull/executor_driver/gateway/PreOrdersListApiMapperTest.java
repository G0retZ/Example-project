package com.cargopull.executor_driver.gateway;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import com.cargopull.executor_driver.backend.web.incoming.ApiOptionItem;
import com.cargopull.executor_driver.backend.web.incoming.ApiRoutePoint;
import com.cargopull.executor_driver.entity.Option;
import com.cargopull.executor_driver.entity.OptionBoolean;
import com.cargopull.executor_driver.entity.OptionNumeric;
import com.cargopull.executor_driver.entity.Order;
import com.cargopull.executor_driver.entity.RoutePoint;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import ua.naiksoftware.stomp.client.StompMessage;

@RunWith(MockitoJUnitRunner.class)
public class PreOrdersListApiMapperTest {

  @Rule
  public final ApiOrderRule rule = new ApiOrderRule();

  private Mapper<StompMessage, Set<Order>> mapper;

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
  private RoutePoint rPoint;
  @Mock
  private RoutePoint rPoint2;

  @Before
  public void setUp() throws Exception {
    when(apiOptionMapper.map(any(ApiOptionItem.class)))
        .thenReturn(optionNumeric, optionBoolean, optionBoolean, optionNumeric);
    when(routePointMapper.map(any(ApiRoutePoint.class))).thenReturn(rPoint, rPoint2);
    mapper = new PreOrdersListApiMapper(apiOptionMapper, routePointMapper);
  }

  /**
   * Должен успешно преобразовать JSON в список предзаказов.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingJsonStringToPreOrdersListSuccess() throws Exception {
    // Дано:
    when(stompMessage.getPayload()).thenReturn("[" + rule.getFullOrder() + "]");

    // Действие:
    List<Order> preOrders = new ArrayList<>(mapper.map(stompMessage));

    // Результат:
    assertEquals(preOrders.size(), 1);
    assertEquals(preOrders.get(0).getId(), 7);
    assertEquals(preOrders.get(0).getComment(), "some comment");
    assertEquals(preOrders.get(0).getEstimatedPriceText(), "over 9999 BTC");
    assertEquals(preOrders.get(0).getEstimatedPrice(), 9999);
    assertEquals(preOrders.get(0).getEstimatedTime(), 234_532_000);
    assertEquals(preOrders.get(0).getEstimatedRouteLength(), 35_213);
    assertEquals(preOrders.get(0).getTotalCost(), 10_352);
    assertEquals(preOrders.get(0).getTimeout(), 25);
    assertEquals(preOrders.get(0).getEtaToStartPoint(), 1234567890);
    assertEquals(preOrders.get(0).getConfirmationTime(), 9876543210L);
    assertEquals(preOrders.get(0).getStartTime(), 9876598760L);
    assertEquals(preOrders.get(0).getScheduledStartTime(), 128937981273L);
    assertEquals(preOrders.get(0).getServiceName(), "service");
    assertEquals(preOrders.get(0).getDistance(), 546);
    assertEquals(preOrders.get(0).getRoutePath(), Arrays.asList(rPoint, rPoint2, rPoint2));
    assertEquals(preOrders.get(0).getOptions(),
        Arrays.asList(optionNumeric, optionBoolean, optionBoolean, optionNumeric)
    );
  }

  /**
   * Должен успешно преобразовать JSON без ИД в список предзаказов.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingJsonStringWithoutIdToPreOrdersListSuccess() throws Exception {
    // Дано:
    when(stompMessage.getPayload()).thenReturn("[" + rule.getOrderWithoutId() + "]");

    // Действие:
    List<Order> preOrders = new ArrayList<>(mapper.map(stompMessage));

    // Результат:
    assertEquals(preOrders.size(), 1);
    assertEquals(preOrders.get(0).getId(), 0);
    assertEquals(preOrders.get(0).getComment(), "some comment");
    assertEquals(preOrders.get(0).getEstimatedPriceText(), "over 9999 BTC");
    assertEquals(preOrders.get(0).getEstimatedPrice(), 9999);
    assertEquals(preOrders.get(0).getEstimatedTime(), 234_532_000);
    assertEquals(preOrders.get(0).getEstimatedRouteLength(), 35_213);
    assertEquals(preOrders.get(0).getTotalCost(), 10_352);
    assertEquals(preOrders.get(0).getTimeout(), 25);
    assertEquals(preOrders.get(0).getEtaToStartPoint(), 1234567890);
    assertEquals(preOrders.get(0).getConfirmationTime(), 9876543210L);
    assertEquals(preOrders.get(0).getStartTime(), 9876598760L);
    assertEquals(preOrders.get(0).getScheduledStartTime(), 128937981273L);
    assertEquals(preOrders.get(0).getServiceName(), "service");
    assertEquals(preOrders.get(0).getDistance(), 546);
    assertEquals(preOrders.get(0).getRoutePath(), Arrays.asList(rPoint, rPoint2, rPoint2));
    assertEquals(preOrders.get(0).getOptions(),
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
    when(stompMessage.getPayload()).thenReturn("[" + rule.getOrderWithoutPaymentType() + "]");

    // Действие:
    List<Order> preOrders = new ArrayList<>(mapper.map(stompMessage));

    // Результат:
    assertEquals(preOrders.size(), 1);
    assertEquals(preOrders.get(0).getId(), 7);
    assertEquals(preOrders.get(0).getComment(), "some comment");
    assertEquals(preOrders.get(0).getEstimatedPriceText(), "over 9999 BTC");
    assertEquals(preOrders.get(0).getEstimatedPrice(), 9999);
    assertEquals(preOrders.get(0).getEstimatedTime(), 234_532_000);
    assertEquals(preOrders.get(0).getEstimatedRouteLength(), 35_213);
    assertEquals(preOrders.get(0).getTotalCost(), 10_352);
    assertEquals(preOrders.get(0).getTimeout(), 25);
    assertEquals(preOrders.get(0).getEtaToStartPoint(), 1234567890);
    assertEquals(preOrders.get(0).getConfirmationTime(), 9876543210L);
    assertEquals(preOrders.get(0).getStartTime(), 9876598760L);
    assertEquals(preOrders.get(0).getScheduledStartTime(), 128937981273L);
    assertEquals(preOrders.get(0).getServiceName(), "service");
    assertEquals(preOrders.get(0).getDistance(), 546);
    assertEquals(preOrders.get(0).getRoutePath(), Arrays.asList(rPoint, rPoint2, rPoint2));
    assertEquals(preOrders.get(0).getOptions(),
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
    when(stompMessage.getPayload()).thenReturn("[" + rule.getOrderWithEmptyPaymentType() + "]");

    // Действие:
    List<Order> preOrders = new ArrayList<>(mapper.map(stompMessage));

    // Результат:
    assertEquals(preOrders.size(), 1);
    assertEquals(preOrders.get(0).getId(), 7);
    assertEquals(preOrders.get(0).getComment(), "some comment");
    assertEquals(preOrders.get(0).getEstimatedPriceText(), "over 9999 BTC");
    assertEquals(preOrders.get(0).getEstimatedPrice(), 9999);
    assertEquals(preOrders.get(0).getEstimatedTime(), 234_532_000);
    assertEquals(preOrders.get(0).getEstimatedRouteLength(), 35_213);
    assertEquals(preOrders.get(0).getTotalCost(), 10_352);
    assertEquals(preOrders.get(0).getTimeout(), 25);
    assertEquals(preOrders.get(0).getEtaToStartPoint(), 1234567890);
    assertEquals(preOrders.get(0).getConfirmationTime(), 9876543210L);
    assertEquals(preOrders.get(0).getStartTime(), 9876598760L);
    assertEquals(preOrders.get(0).getScheduledStartTime(), 128937981273L);
    assertEquals(preOrders.get(0).getServiceName(), "service");
    assertEquals(preOrders.get(0).getDistance(), 546);
    assertEquals(preOrders.get(0).getRoutePath(), Arrays.asList(rPoint, rPoint2, rPoint2));
    assertEquals(preOrders.get(0).getOptions(),
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
    when(stompMessage.getPayload()).thenReturn("[" + rule.getOrderWithWrongPaymentType() + "]");

    // Действие:
    List<Order> preOrders = new ArrayList<>(mapper.map(stompMessage));

    // Результат:
    assertEquals(preOrders.size(), 1);
    assertEquals(preOrders.get(0).getId(), 7);
    assertEquals(preOrders.get(0).getComment(), "some comment");
    assertEquals(preOrders.get(0).getEstimatedPriceText(), "over 9999 BTC");
    assertEquals(preOrders.get(0).getEstimatedPrice(), 9999);
    assertEquals(preOrders.get(0).getEstimatedTime(), 234_532_000);
    assertEquals(preOrders.get(0).getEstimatedRouteLength(), 35_213);
    assertEquals(preOrders.get(0).getTotalCost(), 10_352);
    assertEquals(preOrders.get(0).getTimeout(), 25);
    assertEquals(preOrders.get(0).getEtaToStartPoint(), 1234567890);
    assertEquals(preOrders.get(0).getConfirmationTime(), 9876543210L);
    assertEquals(preOrders.get(0).getStartTime(), 9876598760L);
    assertEquals(preOrders.get(0).getScheduledStartTime(), 128937981273L);
    assertEquals(preOrders.get(0).getServiceName(), "service");
    assertEquals(preOrders.get(0).getDistance(), 546);
    assertEquals(preOrders.get(0).getRoutePath(), Arrays.asList(rPoint, rPoint2, rPoint2));
    assertEquals(preOrders.get(0).getOptions(),
        Arrays.asList(optionNumeric, optionBoolean, optionBoolean, optionNumeric)
    );
  }

  /**
   * Должен успешно преобразовать JSON без комментария в список предзаказов.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingJsonStringWithoutCommentToPreOrdersListSuccess() throws Exception {
    // Дано:
    when(stompMessage.getPayload()).thenReturn("[" + rule.getOrderWithoutComment() + "]");

    // Действие:
    List<Order> preOrders = new ArrayList<>(mapper.map(stompMessage));

    // Результат:
    assertEquals(preOrders.size(), 1);
    assertEquals(preOrders.get(0).getId(), 7);
    assertEquals(preOrders.get(0).getComment(), "");
    assertEquals(preOrders.get(0).getEstimatedPriceText(), "over 9999 BTC");
    assertEquals(preOrders.get(0).getEstimatedPrice(), 9999);
    assertEquals(preOrders.get(0).getEstimatedTime(), 234_532_000);
    assertEquals(preOrders.get(0).getEstimatedRouteLength(), 35_213);
    assertEquals(preOrders.get(0).getTotalCost(), 10_352);
    assertEquals(preOrders.get(0).getTimeout(), 25);
    assertEquals(preOrders.get(0).getEtaToStartPoint(), 1234567890);
    assertEquals(preOrders.get(0).getConfirmationTime(), 9876543210L);
    assertEquals(preOrders.get(0).getStartTime(), 9876598760L);
    assertEquals(preOrders.get(0).getScheduledStartTime(), 128937981273L);
    assertEquals(preOrders.get(0).getServiceName(), "service");
    assertEquals(preOrders.get(0).getDistance(), 546);
    assertEquals(preOrders.get(0).getRoutePath(), Arrays.asList(rPoint, rPoint2, rPoint2));
    assertEquals(preOrders.get(0).getOptions(),
        Arrays.asList(optionNumeric, optionBoolean, optionBoolean, optionNumeric)
    );
  }

  /**
   * Должен успешно преобразовать JSON без описания цены в список предзаказов.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingJsonStringWithoutEstimatedAmountTextToPreOrdersListSuccess() throws Exception {
    // Дано:
    when(stompMessage.getPayload())
        .thenReturn("[" + rule.getOrderWithoutEstimatedAmountText() + "]");

    // Действие:
    List<Order> preOrders = new ArrayList<>(mapper.map(stompMessage));

    // Результат:
    assertEquals(preOrders.size(), 1);
    assertEquals(preOrders.get(0).getId(), 7);
    assertEquals(preOrders.get(0).getComment(), "some comment");
    assertEquals(preOrders.get(0).getEstimatedPriceText(), "");
    assertEquals(preOrders.get(0).getEstimatedPrice(), 9999);
    assertEquals(preOrders.get(0).getEstimatedTime(), 234_532_000);
    assertEquals(preOrders.get(0).getEstimatedRouteLength(), 35_213);
    assertEquals(preOrders.get(0).getTotalCost(), 10_352);
    assertEquals(preOrders.get(0).getTimeout(), 25);
    assertEquals(preOrders.get(0).getEtaToStartPoint(), 1234567890);
    assertEquals(preOrders.get(0).getConfirmationTime(), 9876543210L);
    assertEquals(preOrders.get(0).getStartTime(), 9876598760L);
    assertEquals(preOrders.get(0).getScheduledStartTime(), 128937981273L);
    assertEquals(preOrders.get(0).getServiceName(), "service");
    assertEquals(preOrders.get(0).getDistance(), 546);
    assertEquals(preOrders.get(0).getRoutePath(), Arrays.asList(rPoint, rPoint2, rPoint2));
    assertEquals(preOrders.get(0).getOptions(),
        Arrays.asList(optionNumeric, optionBoolean, optionBoolean, optionNumeric)
    );
  }

  /**
   * Должен успешно преобразовать JSON без цены в список предзаказов.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingJsonStringWithoutEstimatedAmountToPreOrdersListSuccess() throws Exception {
    // Дано:
    when(stompMessage.getPayload()).thenReturn("[" + rule.getOrderWithoutEstimatedAmount() + "]");

    // Действие:
    List<Order> preOrders = new ArrayList<>(mapper.map(stompMessage));

    // Результат:
    assertEquals(preOrders.size(), 1);
    assertEquals(preOrders.get(0).getId(), 7);
    assertEquals(preOrders.get(0).getComment(), "some comment");
    assertEquals(preOrders.get(0).getEstimatedPriceText(), "over 9999 BTC");
    assertEquals(preOrders.get(0).getEstimatedPrice(), 0);
    assertEquals(preOrders.get(0).getEstimatedTime(), 234_532_000);
    assertEquals(preOrders.get(0).getEstimatedRouteLength(), 35_213);
    assertEquals(preOrders.get(0).getTotalCost(), 10_352);
    assertEquals(preOrders.get(0).getTimeout(), 25);
    assertEquals(preOrders.get(0).getEtaToStartPoint(), 1234567890);
    assertEquals(preOrders.get(0).getConfirmationTime(), 9876543210L);
    assertEquals(preOrders.get(0).getStartTime(), 9876598760L);
    assertEquals(preOrders.get(0).getScheduledStartTime(), 128937981273L);
    assertEquals(preOrders.get(0).getServiceName(), "service");
    assertEquals(preOrders.get(0).getDistance(), 546);
    assertEquals(preOrders.get(0).getRoutePath(), Arrays.asList(rPoint, rPoint2, rPoint2));
    assertEquals(preOrders.get(0).getOptions(),
        Arrays.asList(optionNumeric, optionBoolean, optionBoolean, optionNumeric)
    );
  }

  /**
   * Должен успешно преобразовать JSON без расчетного времени заказа в список предзаказов.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingJsonStringWithoutEstimatedTimeToPreOrdersListSuccess() throws Exception {
    // Дано:
    when(stompMessage.getPayload()).thenReturn("[" + rule.getOrderWithoutEstimatedTime() + "]");

    // Действие:
    List<Order> preOrders = new ArrayList<>(mapper.map(stompMessage));

    // Результат:
    assertEquals(preOrders.size(), 1);
    assertEquals(preOrders.get(0).getId(), 7);
    assertEquals(preOrders.get(0).getComment(), "some comment");
    assertEquals(preOrders.get(0).getEstimatedPriceText(), "over 9999 BTC");
    assertEquals(preOrders.get(0).getEstimatedPrice(), 9999);
    assertEquals(preOrders.get(0).getEstimatedTime(), 0);
    assertEquals(preOrders.get(0).getEstimatedRouteLength(), 35_213);
    assertEquals(preOrders.get(0).getTotalCost(), 10_352);
    assertEquals(preOrders.get(0).getTimeout(), 25);
    assertEquals(preOrders.get(0).getEtaToStartPoint(), 1234567890);
    assertEquals(preOrders.get(0).getConfirmationTime(), 9876543210L);
    assertEquals(preOrders.get(0).getStartTime(), 9876598760L);
    assertEquals(preOrders.get(0).getScheduledStartTime(), 128937981273L);
    assertEquals(preOrders.get(0).getServiceName(), "service");
    assertEquals(preOrders.get(0).getDistance(), 546);
    assertEquals(preOrders.get(0).getRoutePath(), Arrays.asList(rPoint, rPoint2, rPoint2));
    assertEquals(preOrders.get(0).getOptions(),
        Arrays.asList(optionNumeric, optionBoolean, optionBoolean, optionNumeric)
    );
  }

  /**
   * Должен успешно преобразовать JSON без расчетной длины маршрута в список предзаказов.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingJsonStringWithoutEstimatedRouteLengthToPreOrdersListSuccess()
      throws Exception {
    // Дано:
    when(stompMessage.getPayload())
        .thenReturn("[" + rule.getOrderWithoutEstimatedRouteDistance() + "]");

    // Действие:
    List<Order> preOrders = new ArrayList<>(mapper.map(stompMessage));

    // Результат:
    assertEquals(preOrders.size(), 1);
    assertEquals(preOrders.get(0).getId(), 7);
    assertEquals(preOrders.get(0).getComment(), "some comment");
    assertEquals(preOrders.get(0).getEstimatedPriceText(), "over 9999 BTC");
    assertEquals(preOrders.get(0).getEstimatedPrice(), 9999);
    assertEquals(preOrders.get(0).getEstimatedTime(), 234_532_000);
    assertEquals(preOrders.get(0).getEstimatedRouteLength(), 0);
    assertEquals(preOrders.get(0).getTotalCost(), 10_352);
    assertEquals(preOrders.get(0).getTimeout(), 25);
    assertEquals(preOrders.get(0).getEtaToStartPoint(), 1234567890);
    assertEquals(preOrders.get(0).getConfirmationTime(), 9876543210L);
    assertEquals(preOrders.get(0).getStartTime(), 9876598760L);
    assertEquals(preOrders.get(0).getScheduledStartTime(), 128937981273L);
    assertEquals(preOrders.get(0).getServiceName(), "service");
    assertEquals(preOrders.get(0).getDistance(), 546);
    assertEquals(preOrders.get(0).getRoutePath(), Arrays.asList(rPoint, rPoint2, rPoint2));
    assertEquals(preOrders.get(0).getOptions(),
        Arrays.asList(optionNumeric, optionBoolean, optionBoolean, optionNumeric)
    );
  }

  /**
   * Должен успешно преобразовать JSON без рассчетной стоимости в список предзаказов.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingJsonStringWithoutCostToPreOrdersListSuccess() throws Exception {
    // Дано:
    when(stompMessage.getPayload()).thenReturn("[" + rule.getOrderWithoutCost() + "]");

    // Действие:
    List<Order> preOrders = new ArrayList<>(mapper.map(stompMessage));

    // Результат:
    assertEquals(preOrders.size(), 1);
    assertEquals(preOrders.get(0).getId(), 7);
    assertEquals(preOrders.get(0).getComment(), "some comment");
    assertEquals(preOrders.get(0).getEstimatedPriceText(), "over 9999 BTC");
    assertEquals(preOrders.get(0).getEstimatedPrice(), 9999);
    assertEquals(preOrders.get(0).getEstimatedTime(), 234_532_000);
    assertEquals(preOrders.get(0).getEstimatedRouteLength(), 35_213);
    assertEquals(preOrders.get(0).getTotalCost(), 0);
    assertEquals(preOrders.get(0).getTimeout(), 25);
    assertEquals(preOrders.get(0).getEtaToStartPoint(), 1234567890);
    assertEquals(preOrders.get(0).getConfirmationTime(), 9876543210L);
    assertEquals(preOrders.get(0).getStartTime(), 9876598760L);
    assertEquals(preOrders.get(0).getScheduledStartTime(), 128937981273L);
    assertEquals(preOrders.get(0).getServiceName(), "service");
    assertEquals(preOrders.get(0).getDistance(), 546);
    assertEquals(preOrders.get(0).getRoutePath(), Arrays.asList(rPoint, rPoint2, rPoint2));
    assertEquals(preOrders.get(0).getOptions(),
        Arrays.asList(optionNumeric, optionBoolean, optionBoolean, optionNumeric)
    );
  }

  /**
   * Должен успешно преобразовать JSON без таймаута в список предзаказов.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingJsonStringWithoutTimeoutToPreOrdersListSuccess() throws Exception {
    // Дано:
    when(stompMessage.getPayload()).thenReturn("[" + rule.getOrderWithoutTimeout() + "]");

    // Действие:
    List<Order> preOrders = new ArrayList<>(mapper.map(stompMessage));

    // Результат:
    assertEquals(preOrders.size(), 1);
    assertEquals(preOrders.get(0).getId(), 7);
    assertEquals(preOrders.get(0).getComment(), "some comment");
    assertEquals(preOrders.get(0).getEstimatedPriceText(), "over 9999 BTC");
    assertEquals(preOrders.get(0).getEstimatedPrice(), 9999);
    assertEquals(preOrders.get(0).getEstimatedTime(), 234_532_000);
    assertEquals(preOrders.get(0).getEstimatedRouteLength(), 35_213);
    assertEquals(preOrders.get(0).getTotalCost(), 10_352);
    assertEquals(preOrders.get(0).getTimeout(), 0);
    assertEquals(preOrders.get(0).getEtaToStartPoint(), 1234567890);
    assertEquals(preOrders.get(0).getConfirmationTime(), 9876543210L);
    assertEquals(preOrders.get(0).getStartTime(), 9876598760L);
    assertEquals(preOrders.get(0).getScheduledStartTime(), 128937981273L);
    assertEquals(preOrders.get(0).getServiceName(), "service");
    assertEquals(preOrders.get(0).getDistance(), 546);
    assertEquals(preOrders.get(0).getRoutePath(), Arrays.asList(rPoint, rPoint2, rPoint2));
    assertEquals(preOrders.get(0).getOptions(),
        Arrays.asList(optionNumeric, optionBoolean, optionBoolean, optionNumeric)
    );
  }

  /**
   * Должен успешно преобразовать JSON без ETA в список предзаказов.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingJsonStringWithoutEtaToPreOrdersListSuccess() throws Exception {
    // Дано:
    when(stompMessage.getPayload()).thenReturn("[" + rule.getOrderWithoutEta() + "]");

    // Действие:
    List<Order> preOrders = new ArrayList<>(mapper.map(stompMessage));

    // Результат:
    assertEquals(preOrders.size(), 1);
    assertEquals(preOrders.get(0).getId(), 7);
    assertEquals(preOrders.get(0).getComment(), "some comment");
    assertEquals(preOrders.get(0).getEstimatedPriceText(), "over 9999 BTC");
    assertEquals(preOrders.get(0).getEstimatedPrice(), 9999);
    assertEquals(preOrders.get(0).getEstimatedTime(), 234_532_000);
    assertEquals(preOrders.get(0).getEstimatedRouteLength(), 35_213);
    assertEquals(preOrders.get(0).getTotalCost(), 10_352);
    assertEquals(preOrders.get(0).getTimeout(), 25);
    assertEquals(preOrders.get(0).getEtaToStartPoint(), 0);
    assertEquals(preOrders.get(0).getConfirmationTime(), 9876543210L);
    assertEquals(preOrders.get(0).getStartTime(), 9876598760L);
    assertEquals(preOrders.get(0).getScheduledStartTime(), 128937981273L);
    assertEquals(preOrders.get(0).getServiceName(), "service");
    assertEquals(preOrders.get(0).getDistance(), 546);
    assertEquals(preOrders.get(0).getRoutePath(), Arrays.asList(rPoint, rPoint2, rPoint2));
    assertEquals(preOrders.get(0).getOptions(),
        Arrays.asList(optionNumeric, optionBoolean, optionBoolean, optionNumeric)
    );
  }

  /**
   * Должен успешно преобразовать JSON без времени подтверждения в список предзаказов.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingJsonStringWithoutConfirmationTimeToPreOrdersListSuccess() throws Exception {
    // Дано:
    when(stompMessage.getPayload()).thenReturn("[" + rule.getOrderWithoutConfirmationTime() + "]");

    // Действие:
    List<Order> preOrders = new ArrayList<>(mapper.map(stompMessage));

    // Результат:
    assertEquals(preOrders.size(), 1);
    assertEquals(preOrders.get(0).getId(), 7);
    assertEquals(preOrders.get(0).getComment(), "some comment");
    assertEquals(preOrders.get(0).getEstimatedPriceText(), "over 9999 BTC");
    assertEquals(preOrders.get(0).getEstimatedPrice(), 9999);
    assertEquals(preOrders.get(0).getEstimatedTime(), 234_532_000);
    assertEquals(preOrders.get(0).getEstimatedRouteLength(), 35_213);
    assertEquals(preOrders.get(0).getTotalCost(), 10_352);
    assertEquals(preOrders.get(0).getTimeout(), 25);
    assertEquals(preOrders.get(0).getEtaToStartPoint(), 1234567890);
    assertEquals(preOrders.get(0).getConfirmationTime(), 0);
    assertEquals(preOrders.get(0).getStartTime(), 9876598760L);
    assertEquals(preOrders.get(0).getScheduledStartTime(), 128937981273L);
    assertEquals(preOrders.get(0).getServiceName(), "service");
    assertEquals(preOrders.get(0).getDistance(), 546);
    assertEquals(preOrders.get(0).getRoutePath(), Arrays.asList(rPoint, rPoint2, rPoint2));
    assertEquals(preOrders.get(0).getOptions(),
        Arrays.asList(optionNumeric, optionBoolean, optionBoolean, optionNumeric)
    );
  }

  /**
   * Должен успешно преобразовать JSON без времени начала в список предзаказов.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingJsonStringWithoutStartTimeToPreOrdersListSuccess() throws Exception {
    // Дано:
    when(stompMessage.getPayload()).thenReturn("[" + rule.getOrderWithoutStartTime() + "]");

    // Действие:
    List<Order> preOrders = new ArrayList<>(mapper.map(stompMessage));

    // Результат:
    assertEquals(preOrders.size(), 1);
    assertEquals(preOrders.get(0).getId(), 7);
    assertEquals(preOrders.get(0).getComment(), "some comment");
    assertEquals(preOrders.get(0).getEstimatedPriceText(), "over 9999 BTC");
    assertEquals(preOrders.get(0).getEstimatedPrice(), 9999);
    assertEquals(preOrders.get(0).getEstimatedTime(), 234_532_000);
    assertEquals(preOrders.get(0).getEstimatedRouteLength(), 35_213);
    assertEquals(preOrders.get(0).getTotalCost(), 10_352);
    assertEquals(preOrders.get(0).getTimeout(), 25);
    assertEquals(preOrders.get(0).getEtaToStartPoint(), 1234567890);
    assertEquals(preOrders.get(0).getConfirmationTime(), 9876543210L);
    assertEquals(preOrders.get(0).getStartTime(), 0);
    assertEquals(preOrders.get(0).getScheduledStartTime(), 128937981273L);
    assertEquals(preOrders.get(0).getServiceName(), "service");
    assertEquals(preOrders.get(0).getDistance(), 546);
    assertEquals(preOrders.get(0).getRoutePath(), Arrays.asList(rPoint, rPoint2, rPoint2));
    assertEquals(preOrders.get(0).getOptions(),
        Arrays.asList(optionNumeric, optionBoolean, optionBoolean, optionNumeric)
    );
  }

  /**
   * Должен успешно преобразовать JSON без планируемого времени начала в список предзаказов.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingJsonStringWithoutScheduledStartTimeToPreOrdersListSuccess() throws Exception {
    // Дано:
    when(stompMessage.getPayload())
        .thenReturn("[" + rule.getOrderWithoutScheduledStartTime() + "]");

    // Действие:
    List<Order> preOrders = new ArrayList<>(mapper.map(stompMessage));

    // Результат:
    assertEquals(preOrders.size(), 1);
    assertEquals(preOrders.get(0).getId(), 7);
    assertEquals(preOrders.get(0).getComment(), "some comment");
    assertEquals(preOrders.get(0).getEstimatedPriceText(), "over 9999 BTC");
    assertEquals(preOrders.get(0).getEstimatedPrice(), 9999);
    assertEquals(preOrders.get(0).getEstimatedTime(), 234_532_000);
    assertEquals(preOrders.get(0).getEstimatedRouteLength(), 35_213);
    assertEquals(preOrders.get(0).getTotalCost(), 10_352);
    assertEquals(preOrders.get(0).getTimeout(), 25);
    assertEquals(preOrders.get(0).getEtaToStartPoint(), 1234567890);
    assertEquals(preOrders.get(0).getConfirmationTime(), 9876543210L);
    assertEquals(preOrders.get(0).getStartTime(), 9876598760L);
    assertEquals(preOrders.get(0).getScheduledStartTime(), 0);
    assertEquals(preOrders.get(0).getServiceName(), "service");
    assertEquals(preOrders.get(0).getDistance(), 546);
    assertEquals(preOrders.get(0).getRoutePath(), Arrays.asList(rPoint, rPoint2, rPoint2));
    assertEquals(preOrders.get(0).getOptions(),
        Arrays.asList(optionNumeric, optionBoolean, optionBoolean, optionNumeric)
    );
  }

  /**
   * Должен успешно преобразовать JSON без ИД в дистанции в список предзаказов.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingJsonStringWithoutDistanceIdToPreOrdersListSuccess() throws Exception {
    // Дано:
    when(stompMessage.getPayload()).thenReturn("[" + rule.getOrderWithoutDistanceId() + "]");

    // Действие:
    List<Order> preOrders = new ArrayList<>(mapper.map(stompMessage));

    // Результат:
    assertEquals(preOrders.size(), 1);
    assertEquals(preOrders.get(0).getId(), 7);
    assertEquals(preOrders.get(0).getComment(), "some comment");
    assertEquals(preOrders.get(0).getEstimatedPriceText(), "over 9999 BTC");
    assertEquals(preOrders.get(0).getEstimatedPrice(), 9999);
    assertEquals(preOrders.get(0).getEstimatedTime(), 234_532_000);
    assertEquals(preOrders.get(0).getEstimatedRouteLength(), 35_213);
    assertEquals(preOrders.get(0).getTotalCost(), 10_352);
    assertEquals(preOrders.get(0).getTimeout(), 25);
    assertEquals(preOrders.get(0).getEtaToStartPoint(), 1234567890);
    assertEquals(preOrders.get(0).getConfirmationTime(), 9876543210L);
    assertEquals(preOrders.get(0).getStartTime(), 9876598760L);
    assertEquals(preOrders.get(0).getScheduledStartTime(), 128937981273L);
    assertEquals(preOrders.get(0).getServiceName(), "service");
    assertEquals(preOrders.get(0).getDistance(), 546);
    assertEquals(preOrders.get(0).getRoutePath(), Arrays.asList(rPoint, rPoint2, rPoint2));
    assertEquals(preOrders.get(0).getOptions(),
        Arrays.asList(optionNumeric, optionBoolean, optionBoolean, optionNumeric)
    );
  }

  /**
   * Должен дать ошибку, если пришел JSON без значения дистанции.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingJsonStringWithoutDistanceValueToPreOrdersListFail() throws Exception {
    // Дано:
    when(stompMessage.getPayload()).thenReturn("[" + rule.getOrderWithoutDistanceValue() + "]");

    // Действие:
    List<Order> preOrders = new ArrayList<>(mapper.map(stompMessage));

    // Результат:
    assertEquals(preOrders.size(), 1);
    assertEquals(preOrders.get(0).getId(), 7);
    assertEquals(preOrders.get(0).getComment(), "some comment");
    assertEquals(preOrders.get(0).getEstimatedPriceText(), "over 9999 BTC");
    assertEquals(preOrders.get(0).getEstimatedPrice(), 9999);
    assertEquals(preOrders.get(0).getEstimatedTime(), 234_532_000);
    assertEquals(preOrders.get(0).getEstimatedRouteLength(), 35_213);
    assertEquals(preOrders.get(0).getTotalCost(), 10_352);
    assertEquals(preOrders.get(0).getTimeout(), 25);
    assertEquals(preOrders.get(0).getEtaToStartPoint(), 1234567890);
    assertEquals(preOrders.get(0).getConfirmationTime(), 9876543210L);
    assertEquals(preOrders.get(0).getStartTime(), 9876598760L);
    assertEquals(preOrders.get(0).getScheduledStartTime(), 128937981273L);
    assertEquals(preOrders.get(0).getServiceName(), "service");
    assertEquals(preOrders.get(0).getDistance(), 0);
    assertEquals(preOrders.get(0).getRoutePath(), Arrays.asList(rPoint, rPoint2, rPoint2));
    assertEquals(preOrders.get(0).getOptions(),
        Arrays.asList(optionNumeric, optionBoolean, optionBoolean, optionNumeric)
    );
  }

  /**
   * Должен успешно преобразовать JSON без дистанции в список предзаказов.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingJsonStringWithoutDistanceToPreOrdersListSuccess() throws Exception {
    // Дано:
    when(stompMessage.getPayload()).thenReturn("[" + rule.getOrderWithoutDistance() + "]");

    // Действие:
    List<Order> preOrders = new ArrayList<>(mapper.map(stompMessage));

    // Результат:
    assertEquals(preOrders.size(), 1);
    assertEquals(preOrders.get(0).getId(), 7);
    assertEquals(preOrders.get(0).getComment(), "some comment");
    assertEquals(preOrders.get(0).getEstimatedPriceText(), "over 9999 BTC");
    assertEquals(preOrders.get(0).getEstimatedPrice(), 9999);
    assertEquals(preOrders.get(0).getEstimatedTime(), 234_532_000);
    assertEquals(preOrders.get(0).getEstimatedRouteLength(), 35_213);
    assertEquals(preOrders.get(0).getTotalCost(), 10_352);
    assertEquals(preOrders.get(0).getTimeout(), 25);
    assertEquals(preOrders.get(0).getEtaToStartPoint(), 1234567890);
    assertEquals(preOrders.get(0).getConfirmationTime(), 9876543210L);
    assertEquals(preOrders.get(0).getStartTime(), 9876598760L);
    assertEquals(preOrders.get(0).getScheduledStartTime(), 128937981273L);
    assertEquals(preOrders.get(0).getServiceName(), "service");
    assertEquals(preOrders.get(0).getDistance(), 0);
    assertEquals(preOrders.get(0).getRoutePath(), Arrays.asList(rPoint, rPoint2, rPoint2));
    assertEquals(preOrders.get(0).getOptions(),
        Arrays.asList(optionNumeric, optionBoolean, optionBoolean, optionNumeric)
    );
  }

  /**
   * Должен успешно преобразовать JSON без ИД в услуге в список предзаказов.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingJsonStringWithoutServiceIdToPreOrdersListSuccess() throws Exception {
    // Дано:
    when(stompMessage.getPayload()).thenReturn("[" + rule.getOrderWithoutServiceId() + "]");

    // Действие:
    List<Order> preOrders = new ArrayList<>(mapper.map(stompMessage));

    // Результат:
    assertEquals(preOrders.size(), 1);
    assertEquals(preOrders.get(0).getId(), 7);
    assertEquals(preOrders.get(0).getComment(), "some comment");
    assertEquals(preOrders.get(0).getEstimatedPriceText(), "over 9999 BTC");
    assertEquals(preOrders.get(0).getEstimatedPrice(), 9999);
    assertEquals(preOrders.get(0).getEstimatedTime(), 234_532_000);
    assertEquals(preOrders.get(0).getEstimatedRouteLength(), 35_213);
    assertEquals(preOrders.get(0).getTotalCost(), 10_352);
    assertEquals(preOrders.get(0).getTimeout(), 25);
    assertEquals(preOrders.get(0).getEtaToStartPoint(), 1234567890);
    assertEquals(preOrders.get(0).getConfirmationTime(), 9876543210L);
    assertEquals(preOrders.get(0).getStartTime(), 9876598760L);
    assertEquals(preOrders.get(0).getScheduledStartTime(), 128937981273L);
    assertEquals(preOrders.get(0).getServiceName(), "service");
    assertEquals(preOrders.get(0).getDistance(), 546);
    assertEquals(preOrders.get(0).getRoutePath(), Arrays.asList(rPoint, rPoint2, rPoint2));
    assertEquals(preOrders.get(0).getOptions(),
        Arrays.asList(optionNumeric, optionBoolean, optionBoolean, optionNumeric)
    );
  }

  /**
   * Должен дать ошибку, если пришел JSON без имени услуги.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingJsonStringWithoutServiceNameToPreOrdersListFail() throws Exception {
    // Дано:
    when(stompMessage.getPayload()).thenReturn("[" + rule.getOrderWithoutServiceName() + "]");

    // Действие:
    List<Order> preOrders = new ArrayList<>(mapper.map(stompMessage));

    // Результат:
    assertEquals(preOrders.size(), 1);
    assertEquals(preOrders.get(0).getId(), 7);
    assertEquals(preOrders.get(0).getComment(), "some comment");
    assertEquals(preOrders.get(0).getEstimatedPriceText(), "over 9999 BTC");
    assertEquals(preOrders.get(0).getEstimatedPrice(), 9999);
    assertEquals(preOrders.get(0).getEstimatedTime(), 234_532_000);
    assertEquals(preOrders.get(0).getEstimatedRouteLength(), 35_213);
    assertEquals(preOrders.get(0).getTotalCost(), 10_352);
    assertEquals(preOrders.get(0).getTimeout(), 25);
    assertEquals(preOrders.get(0).getEtaToStartPoint(), 1234567890);
    assertEquals(preOrders.get(0).getConfirmationTime(), 9876543210L);
    assertEquals(preOrders.get(0).getStartTime(), 9876598760L);
    assertEquals(preOrders.get(0).getScheduledStartTime(), 128937981273L);
    assertEquals(preOrders.get(0).getServiceName(), "");
    assertEquals(preOrders.get(0).getDistance(), 546);
    assertEquals(preOrders.get(0).getRoutePath(), Arrays.asList(rPoint, rPoint2, rPoint2));
    assertEquals(preOrders.get(0).getOptions(),
        Arrays.asList(optionNumeric, optionBoolean, optionBoolean, optionNumeric)
    );
  }

  /**
   * Должен успешно преобразовать JSON без цены услуги в список предзаказов.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingJsonStringWithoutServicePriceToPreOrdersListSuccess() throws Exception {
    // Дано:
    when(stompMessage.getPayload()).thenReturn("[" + rule.getOrderWithoutServicePrice() + "]");

    // Действие:
    List<Order> preOrders = new ArrayList<>(mapper.map(stompMessage));

    // Результат:
    assertEquals(preOrders.size(), 1);
    assertEquals(preOrders.get(0).getId(), 7);
    assertEquals(preOrders.get(0).getComment(), "some comment");
    assertEquals(preOrders.get(0).getEstimatedPriceText(), "over 9999 BTC");
    assertEquals(preOrders.get(0).getEstimatedPrice(), 9999);
    assertEquals(preOrders.get(0).getEstimatedTime(), 234_532_000);
    assertEquals(preOrders.get(0).getEstimatedRouteLength(), 35_213);
    assertEquals(preOrders.get(0).getTotalCost(), 10_352);
    assertEquals(preOrders.get(0).getTimeout(), 25);
    assertEquals(preOrders.get(0).getEtaToStartPoint(), 1234567890);
    assertEquals(preOrders.get(0).getConfirmationTime(), 9876543210L);
    assertEquals(preOrders.get(0).getStartTime(), 9876598760L);
    assertEquals(preOrders.get(0).getScheduledStartTime(), 128937981273L);
    assertEquals(preOrders.get(0).getServiceName(), "service");
    assertEquals(preOrders.get(0).getDistance(), 546);
    assertEquals(preOrders.get(0).getRoutePath(), Arrays.asList(rPoint, rPoint2, rPoint2));
    assertEquals(preOrders.get(0).getOptions(),
        Arrays.asList(optionNumeric, optionBoolean, optionBoolean, optionNumeric)
    );
  }

  /**
   * Должен дать ошибку, если пришел JSON без услуги в список предзаказов.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingJsonStringWithoutServiceToPreOrdersListFail() throws Exception {
    // Дано:
    when(stompMessage.getPayload()).thenReturn("[" + rule.getOrderWithoutService() + "]");

    // Действие:
    List<Order> preOrders = new ArrayList<>(mapper.map(stompMessage));

    // Результат:
    assertEquals(preOrders.size(), 1);
    assertEquals(preOrders.get(0).getId(), 7);
    assertEquals(preOrders.get(0).getComment(), "some comment");
    assertEquals(preOrders.get(0).getEstimatedPriceText(), "over 9999 BTC");
    assertEquals(preOrders.get(0).getEstimatedPrice(), 9999);
    assertEquals(preOrders.get(0).getEstimatedTime(), 234_532_000);
    assertEquals(preOrders.get(0).getEstimatedRouteLength(), 35_213);
    assertEquals(preOrders.get(0).getTotalCost(), 10_352);
    assertEquals(preOrders.get(0).getTimeout(), 25);
    assertEquals(preOrders.get(0).getEtaToStartPoint(), 1234567890);
    assertEquals(preOrders.get(0).getConfirmationTime(), 9876543210L);
    assertEquals(preOrders.get(0).getStartTime(), 9876598760L);
    assertEquals(preOrders.get(0).getScheduledStartTime(), 128937981273L);
    assertEquals(preOrders.get(0).getServiceName(), "");
    assertEquals(preOrders.get(0).getDistance(), 546);
    assertEquals(preOrders.get(0).getRoutePath(), Arrays.asList(rPoint, rPoint2, rPoint2));
    assertEquals(preOrders.get(0).getOptions(),
        Arrays.asList(optionNumeric, optionBoolean, optionBoolean, optionNumeric)
    );
  }

  /**
   * Должен дать ошибку, если пришел JSON с пустым маршрутом.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingJsonStringWithEmptyRouteToPreOrdersListFail() throws Exception {
    // Дано:
    when(stompMessage.getPayload()).thenReturn("[" + rule.getOrderWithEmptyRoute() + "]");

    // Действие:
    List<Order> preOrders = new ArrayList<>(mapper.map(stompMessage));

    // Результат:
    assertEquals(preOrders.size(), 1);
    assertEquals(preOrders.get(0).getId(), 7);
    assertEquals(preOrders.get(0).getComment(), "some comment");
    assertEquals(preOrders.get(0).getEstimatedPriceText(), "over 9999 BTC");
    assertEquals(preOrders.get(0).getEstimatedPrice(), 9999);
    assertEquals(preOrders.get(0).getEstimatedTime(), 234_532_000);
    assertEquals(preOrders.get(0).getEstimatedRouteLength(), 35_213);
    assertEquals(preOrders.get(0).getTotalCost(), 10_352);
    assertEquals(preOrders.get(0).getTimeout(), 25);
    assertEquals(preOrders.get(0).getEtaToStartPoint(), 1234567890);
    assertEquals(preOrders.get(0).getConfirmationTime(), 9876543210L);
    assertEquals(preOrders.get(0).getStartTime(), 9876598760L);
    assertEquals(preOrders.get(0).getScheduledStartTime(), 128937981273L);
    assertEquals(preOrders.get(0).getServiceName(), "service");
    assertEquals(preOrders.get(0).getDistance(), 546);
    assertEquals(preOrders.get(0).getRoutePath(), new ArrayList<>());
    assertEquals(preOrders.get(0).getOptions(),
        Arrays.asList(optionNumeric, optionBoolean, optionBoolean, optionNumeric)
    );
  }

  /**
   * Должен дать ошибку, если пришел JSON без маршрута.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingJsonStringWithoutRouteToPreOrdersListFail() throws Exception {
    // Дано:
    when(stompMessage.getPayload()).thenReturn("[" + rule.getOrderWithoutRoute() + "]");

    // Действие:
    List<Order> preOrders = new ArrayList<>(mapper.map(stompMessage));

    // Результат:
    assertEquals(preOrders.size(), 1);
    assertEquals(preOrders.get(0).getId(), 7);
    assertEquals(preOrders.get(0).getComment(), "some comment");
    assertEquals(preOrders.get(0).getEstimatedPriceText(), "over 9999 BTC");
    assertEquals(preOrders.get(0).getEstimatedPrice(), 9999);
    assertEquals(preOrders.get(0).getEstimatedTime(), 234_532_000);
    assertEquals(preOrders.get(0).getEstimatedRouteLength(), 35_213);
    assertEquals(preOrders.get(0).getTotalCost(), 10_352);
    assertEquals(preOrders.get(0).getTimeout(), 25);
    assertEquals(preOrders.get(0).getEtaToStartPoint(), 1234567890);
    assertEquals(preOrders.get(0).getConfirmationTime(), 9876543210L);
    assertEquals(preOrders.get(0).getStartTime(), 9876598760L);
    assertEquals(preOrders.get(0).getScheduledStartTime(), 128937981273L);
    assertEquals(preOrders.get(0).getServiceName(), "service");
    assertEquals(preOrders.get(0).getDistance(), 546);
    assertEquals(preOrders.get(0).getRoutePath(), new ArrayList<>());
    assertEquals(preOrders.get(0).getOptions(),
        Arrays.asList(optionNumeric, optionBoolean, optionBoolean, optionNumeric)
    );
  }

  /**
   * Должен успешно преобразовать JSON с пустым списком опций в список предзаказов.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingJsonStringWithEmptyOptionsToPreOrdersListSuccess() throws Exception {
    // Дано:
    when(stompMessage.getPayload()).thenReturn("[" + rule.getOrderWithEmptyOptions() + "]");

    // Действие:
    List<Order> preOrders = new ArrayList<>(mapper.map(stompMessage));

    // Результат:
    assertEquals(preOrders.size(), 1);
    assertEquals(preOrders.get(0).getId(), 7);
    assertEquals(preOrders.get(0).getComment(), "some comment");
    assertEquals(preOrders.get(0).getEstimatedPriceText(), "over 9999 BTC");
    assertEquals(preOrders.get(0).getEstimatedPrice(), 9999);
    assertEquals(preOrders.get(0).getEstimatedTime(), 234_532_000);
    assertEquals(preOrders.get(0).getEstimatedRouteLength(), 35_213);
    assertEquals(preOrders.get(0).getTotalCost(), 10_352);
    assertEquals(preOrders.get(0).getTimeout(), 25);
    assertEquals(preOrders.get(0).getEtaToStartPoint(), 1234567890);
    assertEquals(preOrders.get(0).getConfirmationTime(), 9876543210L);
    assertEquals(preOrders.get(0).getStartTime(), 9876598760L);
    assertEquals(preOrders.get(0).getScheduledStartTime(), 128937981273L);
    assertEquals(preOrders.get(0).getServiceName(), "service");
    assertEquals(preOrders.get(0).getDistance(), 546);
    assertEquals(preOrders.get(0).getRoutePath(), Arrays.asList(rPoint, rPoint2, rPoint2));
    assertEquals(preOrders.get(0).getOptions(), new ArrayList<>());
  }

  /**
   * Должен успешно преобразовать JSON без опций в список предзаказов.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingJsonStringWithoutOptionsToPreOrdersListSuccess() throws Exception {
    // Дано:
    when(stompMessage.getPayload()).thenReturn("[" + rule.getOrderWithoutOptions() + "]");

    // Действие:
    List<Order> preOrders = new ArrayList<>(mapper.map(stompMessage));

    // Результат:
    assertEquals(preOrders.size(), 1);
    assertEquals(preOrders.get(0).getId(), 7);
    assertEquals(preOrders.get(0).getComment(), "some comment");
    assertEquals(preOrders.get(0).getEstimatedPriceText(), "over 9999 BTC");
    assertEquals(preOrders.get(0).getEstimatedPrice(), 9999);
    assertEquals(preOrders.get(0).getEstimatedTime(), 234_532_000);
    assertEquals(preOrders.get(0).getEstimatedRouteLength(), 35_213);
    assertEquals(preOrders.get(0).getTotalCost(), 10_352);
    assertEquals(preOrders.get(0).getTimeout(), 25);
    assertEquals(preOrders.get(0).getEtaToStartPoint(), 1234567890);
    assertEquals(preOrders.get(0).getConfirmationTime(), 9876543210L);
    assertEquals(preOrders.get(0).getStartTime(), 9876598760L);
    assertEquals(preOrders.get(0).getScheduledStartTime(), 128937981273L);
    assertEquals(preOrders.get(0).getServiceName(), "service");
    assertEquals(preOrders.get(0).getDistance(), 546);
    assertEquals(preOrders.get(0).getRoutePath(), Arrays.asList(rPoint, rPoint2, rPoint2));
    assertEquals(preOrders.get(0).getOptions(), new ArrayList<>());
  }

  /**
   * Должен дать ошибку, если была ошибка маппинга адреса.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingFailForRoutePointMappingError() throws Exception {
    // Дано:
    when(stompMessage.getPayload()).thenReturn("[" + rule.getFullOrder() + "]");
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
    when(stompMessage.getPayload()).thenReturn("[" + rule.getFullOrder() + "]");
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
    when(stompMessage.getPayload()).thenReturn("\n");

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
   * Должен дать ошибку, если пришел объект.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingArrayFail() throws Exception {
    // Дано:
    when(stompMessage.getPayload()).thenReturn(rule.getFullOrder());

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