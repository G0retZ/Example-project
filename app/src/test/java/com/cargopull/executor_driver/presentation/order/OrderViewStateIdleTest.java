package com.cargopull.executor_driver.presentation.order;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.cargopull.executor_driver.R;
import com.cargopull.executor_driver.entity.OptionBoolean;
import com.cargopull.executor_driver.entity.OptionNumeric;
import com.cargopull.executor_driver.entity.Order;
import com.cargopull.executor_driver.entity.RoutePoint;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class OrderViewStateIdleTest {

  private OrderViewStateIdle viewState;

  @Mock
  private OrderViewActions viewActions;

  @Mock
  private Order order;
  @Mock
  private Order order2;
  @Mock
  private RoutePoint routePoint;
  @Mock
  private RoutePoint routePoint1;
  @Mock
  private RoutePoint routePoint2;

  @Before
  public void setUp() {
    viewState = new OrderViewStateIdle(order);
  }

  @Test
  public void testActionsWithComment() {
    // Дано:
    when(viewActions.isShowCents()).thenReturn(false);
    when(viewActions.getCurrencyFormat()).thenReturn("##,###,### ¤");
    when(routePoint.getAddress()).thenReturn("address");
    when(routePoint.getLatitude()).thenReturn(5.421);
    when(routePoint.getLongitude()).thenReturn(10.2341);
    when(routePoint.getComment()).thenReturn("a comment");
    when(routePoint2.getAddress()).thenReturn("address 2");
    when(order.getNextActiveRoutePoint()).thenReturn(routePoint);
    when(order.getDistance()).thenReturn(12239);
    when(order.getEtaToStartPoint()).thenReturn(3264132L);
    when(order.getEstimatedRouteLength()).thenReturn(31278L);
    when(order.getStartTime()).thenReturn(1238403200L);
    when(order.getRoutePath()).thenReturn(Arrays.asList(routePoint, routePoint1, routePoint2));
    when(order.getEstimatedTime()).thenReturn(7929000L);
    when(order.getEstimatedPrice()).thenReturn(681250L);
    when(order.getServiceName()).thenReturn("service");
    when(order.getComment()).thenReturn("comm");
    when(order.getOptions()).thenReturn(new ArrayList<>());

    // Действие:
    viewState.apply(viewActions);

    // Результат:
    verify(viewActions).setImage(R.id.mapImage, "https://maps.googleapis.com/maps/api/staticmap?"
        + "center=5.421,10.2341"
        + "&zoom=16"
        + "&size=360x200"
        + "&maptype=roadmap"
        + "&key=AIzaSyBwlubLyqI6z_ivfAWcTCfyTXkoRHTagMk");
    verify(viewActions).setFormattedText(R.id.distanceText, R.string.km, 12.239d);
    verify(viewActions).setFormattedText(R.id.etaText, R.string.eta, 54);
    verify(viewActions).setFormattedText(R.id.routeTitleText, R.string.route_distance, 31.278d);
    verify(viewActions).setText(R.id.nextAddressText, "address");
    verify(viewActions)
        .setFormattedText(R.id.openNavigator, R.string.client_location, 5.421, 10.2341);
    verify(viewActions).setVisible(R.id.nextAddressCommentTitleText, true);
    verify(viewActions).setVisible(R.id.nextAddressCommentText, true);
    verify(viewActions).setText(R.id.nextAddressCommentText, "a comment");
    verify(viewActions).setText(R.id.routePointsCount, "3");
    verify(viewActions).setText(R.id.lastAddressText, "address 2");
    verify(viewActions).isShowCents();
    verify(viewActions).getCurrencyFormat();
    DecimalFormat decimalFormat = new DecimalFormat("##,###,### ¤");
    decimalFormat.setMaximumFractionDigits(0);
    decimalFormat.setMinimumFractionDigits(0);
    verify(viewActions).setFormattedText(R.id.estimationText, R.string.km_h_m_p, 31.278d, 2, 12,
        decimalFormat.format(6813)
    );
    verify(viewActions).setFormattedText(R.id.estimatedPriceText, R.string.price,
        decimalFormat.format(6813)
    );
    verify(viewActions).setText(R.id.startDateText,
        DateTimeFormat.forPattern("d MMMM, EEEE").print(DateTime.now().withMillis(1238403200L)));
    verify(viewActions).setText(R.id.startTimeText,
        DateTimeFormat.forPattern("HH:mm").print(DateTime.now().withMillis(1238403200L)));
    DateTime dateTime = DateTime.now().withMillis(1238403200L + 7929000L);
    verify(viewActions).setFormattedText(R.id.occupationTimeText, R.string.h_m_d,
        dateTime.getHourOfDay(), dateTime.getMinuteOfHour(),
        DateTimeFormat.forPattern("HH:mm").print(dateTime));
    verify(viewActions).setVisible(R.id.cargoDescTitleText, true);
    verify(viewActions).setVisible(R.id.cargoDescText, true);
    verify(viewActions).setText(R.id.cargoDescText, "comm");
    verify(viewActions).setVisible(R.id.optionsTitleText, false);
    verify(viewActions).setVisible(R.id.optionsText, false);
    verify(viewActions).setText(R.id.optionsText, "");
    verify(viewActions).setText(R.id.serviceText, "service");
    verify(viewActions).unblockWithPending("OrderViewStateIdle");
    verify(viewActions).dismissDialog();
    verifyNoMoreInteractions(viewActions);
  }

  @Test
  public void testActionsWithoutComment() {
    // Дано:
    when(viewActions.isShowCents()).thenReturn(true);
    when(viewActions.getCurrencyFormat()).thenReturn("##,###,### ¤");
    when(routePoint.getAddress()).thenReturn("address");
    when(routePoint.getLatitude()).thenReturn(5.421);
    when(routePoint.getLongitude()).thenReturn(10.2341);
    when(routePoint.getComment()).thenReturn("");
    when(routePoint2.getAddress()).thenReturn("address 2");
    when(order.getNextActiveRoutePoint()).thenReturn(routePoint);
    when(order.getDistance()).thenReturn(12239);
    when(order.getEtaToStartPoint()).thenReturn(3264132L);
    when(order.getEstimatedRouteLength()).thenReturn(31278L);
    when(order.getStartTime()).thenReturn(1238403200L);
    when(order.getRoutePath()).thenReturn(Arrays.asList(routePoint, routePoint1, routePoint2));
    when(order.getEstimatedTime()).thenReturn(7929000L);
    when(order.getEstimatedPrice()).thenReturn(681250L);
    when(order.getServiceName()).thenReturn("service");
    when(order.getComment()).thenReturn("");
    when(order.getOptions()).thenReturn(new ArrayList<>(Arrays.asList(
        new OptionBoolean(0, "bool1", "bd", false, false),
        new OptionBoolean(1, "bool2", "bd", false, true),
        new OptionBoolean(2, "bool3", "bd", false, false),
        new OptionBoolean(3, "bool4", "bd", false, true),
        new OptionNumeric(4, "num1", "nd", false, 3, 0, 5),
        new OptionNumeric(5, "num2", "nd", false, 7, 0, 5)
    )));

    // Действие:
    viewState.apply(viewActions);

    // Результат:
    verify(viewActions).setImage(R.id.mapImage, "https://maps.googleapis.com/maps/api/staticmap?"
        + "center=5.421,10.2341"
        + "&zoom=16"
        + "&size=360x200"
        + "&maptype=roadmap"
        + "&key=AIzaSyBwlubLyqI6z_ivfAWcTCfyTXkoRHTagMk");
    verify(viewActions).setFormattedText(R.id.distanceText, R.string.km, 12.239d);
    verify(viewActions).setFormattedText(R.id.etaText, R.string.eta, 54);
    verify(viewActions).setFormattedText(R.id.routeTitleText, R.string.route_distance, 31.278d);
    verify(viewActions).setText(R.id.nextAddressText, "address");
    verify(viewActions)
        .setFormattedText(R.id.openNavigator, R.string.client_location, 5.421, 10.2341);
    verify(viewActions).setVisible(R.id.nextAddressCommentTitleText, false);
    verify(viewActions).setVisible(R.id.nextAddressCommentText, false);
    verify(viewActions).setText(R.id.nextAddressCommentText, "");
    verify(viewActions).setText(R.id.routePointsCount, "3");
    verify(viewActions).setText(R.id.lastAddressText, "address 2");
    verify(viewActions).isShowCents();
    verify(viewActions).getCurrencyFormat();
    DecimalFormat decimalFormat = new DecimalFormat("##,###,### ¤");
    decimalFormat.setMaximumFractionDigits(2);
    decimalFormat.setMinimumFractionDigits(2);
    verify(viewActions).setFormattedText(R.id.estimationText, R.string.km_h_m_p, 31.278d, 2, 12,
        decimalFormat.format(6812.50)
    );
    verify(viewActions).setFormattedText(R.id.estimatedPriceText, R.string.price,
        decimalFormat.format(6812.50)
    );
    verify(viewActions).setText(R.id.startDateText,
        DateTimeFormat.forPattern("d MMMM, EEEE").print(DateTime.now().withMillis(1238403200L)));
    verify(viewActions).setText(R.id.startTimeText,
        DateTimeFormat.forPattern("HH:mm").print(DateTime.now().withMillis(1238403200L)));
    DateTime dateTime = DateTime.now().withMillis(1238403200L + 7929000L);
    verify(viewActions).setFormattedText(R.id.occupationTimeText, R.string.h_m_d,
        dateTime.getHourOfDay(), dateTime.getMinuteOfHour(),
        DateTimeFormat.forPattern("HH:mm").print(dateTime));
    verify(viewActions).setVisible(R.id.cargoDescTitleText, false);
    verify(viewActions).setVisible(R.id.cargoDescText, false);
    verify(viewActions).setText(R.id.cargoDescText, "");
    verify(viewActions).setVisible(R.id.optionsTitleText, true);
    verify(viewActions).setVisible(R.id.optionsText, true);
    verify(viewActions).setText(R.id.optionsText, "bool2\nbool4\nnum1: 3\nnum2: 7");
    verify(viewActions).setText(R.id.serviceText, "service");
    verify(viewActions).unblockWithPending("OrderViewStateIdle");
    verify(viewActions).dismissDialog();
    verifyNoMoreInteractions(viewActions);
  }

  @Test
  public void testActionsWithCommentFreeRide() {
    // Дано:
    when(viewActions.isShowCents()).thenReturn(false);
    when(viewActions.getCurrencyFormat()).thenReturn("##,###,### ¤");
    when(routePoint.getAddress()).thenReturn("address");
    when(routePoint.getLatitude()).thenReturn(5.421);
    when(routePoint.getLongitude()).thenReturn(10.2341);
    when(routePoint.getComment()).thenReturn("a comment");
    when(order.getNextActiveRoutePoint()).thenReturn(routePoint);
    when(order.getDistance()).thenReturn(12239);
    when(order.getEtaToStartPoint()).thenReturn(3264132L);
    when(order.getEstimatedRouteLength()).thenReturn(31278L);
    when(order.getStartTime()).thenReturn(1238403200L);
    when(order.getRoutePath()).thenReturn(Collections.singletonList(routePoint));
    when(order.getEstimatedTime()).thenReturn(7929000L);
    when(order.getEstimatedPrice()).thenReturn(681254L);
    when(order.getServiceName()).thenReturn("service");
    when(order.getComment()).thenReturn("comm");
    when(order.getOptions()).thenReturn(new ArrayList<>(Arrays.asList(
        new OptionBoolean(0, "bool1", "bd", false, false),
        new OptionBoolean(1, "bool2", "bd", false, true),
        new OptionBoolean(2, "bool3", "bd", true, false),
        new OptionBoolean(3, "bool4", "bd", true, true),
        new OptionNumeric(4, "num1", "nd", false, 3, 0, 5),
        new OptionNumeric(5, "num2", "nd", true, 7, 0, 5)
    )));

    // Действие:
    viewState.apply(viewActions);

    // Результат:
    verify(viewActions).setImage(R.id.mapImage, "https://maps.googleapis.com/maps/api/staticmap?"
        + "center=5.421,10.2341"
        + "&zoom=16"
        + "&size=360x200"
        + "&maptype=roadmap"
        + "&key=AIzaSyBwlubLyqI6z_ivfAWcTCfyTXkoRHTagMk");
    verify(viewActions).setFormattedText(R.id.distanceText, R.string.km, 12.239d);
    verify(viewActions).setFormattedText(R.id.etaText, R.string.eta, 54);
    verify(viewActions).setFormattedText(R.id.routeTitleText, R.string.route_distance, 31.278d);
    verify(viewActions).setText(R.id.nextAddressText, "address");
    verify(viewActions)
        .setFormattedText(R.id.openNavigator, R.string.client_location, 5.421, 10.2341);
    verify(viewActions).setVisible(R.id.nextAddressCommentTitleText, true);
    verify(viewActions).setVisible(R.id.nextAddressCommentText, true);
    verify(viewActions).setText(R.id.nextAddressCommentText, "a comment");
    verify(viewActions).setText(R.id.routePointsCount, "2");
    verify(viewActions).setText(R.id.lastAddressText, R.string.free_ride);
    verify(viewActions).isShowCents();
    verify(viewActions).getCurrencyFormat();
    DecimalFormat decimalFormat = new DecimalFormat("##,###,### ¤");
    decimalFormat.setMaximumFractionDigits(0);
    decimalFormat.setMinimumFractionDigits(0);
    verify(viewActions).setFormattedText(R.id.estimationText, R.string.km_h_m_p, 31.278d, 2, 12,
        decimalFormat.format(6813)
    );
    verify(viewActions).setFormattedText(R.id.estimatedPriceText, R.string.price,
        decimalFormat.format(6813)
    );
    verify(viewActions).setText(R.id.startDateText,
        DateTimeFormat.forPattern("d MMMM, EEEE").print(DateTime.now().withMillis(1238403200L)));
    verify(viewActions).setText(R.id.startTimeText,
        DateTimeFormat.forPattern("HH:mm").print(DateTime.now().withMillis(1238403200L)));
    DateTime dateTime = DateTime.now().withMillis(1238403200L + 7929000L);
    verify(viewActions).setFormattedText(R.id.occupationTimeText, R.string.h_m_d,
        dateTime.getHourOfDay(), dateTime.getMinuteOfHour(),
        DateTimeFormat.forPattern("HH:mm").print(dateTime));
    verify(viewActions).setVisible(R.id.cargoDescTitleText, true);
    verify(viewActions).setVisible(R.id.cargoDescText, true);
    verify(viewActions).setText(R.id.cargoDescText, "comm");
    verify(viewActions).setVisible(R.id.optionsTitleText, true);
    verify(viewActions).setVisible(R.id.optionsText, true);
    verify(viewActions).setText(R.id.optionsText, "bool2\nbool4\nnum1: 3\nnum2: 7");
    verify(viewActions).setText(R.id.serviceText, "service");
    verify(viewActions).unblockWithPending("OrderViewStateIdle");
    verify(viewActions).dismissDialog();
    verifyNoMoreInteractions(viewActions);
  }

  @Test
  public void testActionsWithoutCommentFreeRide() {
    // Дано:
    when(viewActions.isShowCents()).thenReturn(true);
    when(viewActions.getCurrencyFormat()).thenReturn("##,###,### ¤");
    when(routePoint.getAddress()).thenReturn("address");
    when(routePoint.getLatitude()).thenReturn(5.421);
    when(routePoint.getLongitude()).thenReturn(10.2341);
    when(routePoint.getComment()).thenReturn("");
    when(order.getRoutePath()).thenReturn(Collections.singletonList(routePoint));
    when(order.getNextActiveRoutePoint()).thenReturn(routePoint);
    when(order.getDistance()).thenReturn(12239);
    when(order.getEtaToStartPoint()).thenReturn(3264132L);
    when(order.getEstimatedRouteLength()).thenReturn(31278L);
    when(order.getStartTime()).thenReturn(1238403200L);
    when(order.getEstimatedTime()).thenReturn(7929000L);
    when(order.getEstimatedPrice()).thenReturn(681254L);
    when(order.getServiceName()).thenReturn("service");
    when(order.getComment()).thenReturn("");
    when(order.getOptions()).thenReturn(new ArrayList<>(Arrays.asList(
        new OptionBoolean(0, "bool1", "bd", false, false),
        new OptionBoolean(1, "bool2", "bd", false, true),
        new OptionBoolean(2, "bool3", "bd", true, false),
        new OptionBoolean(3, "bool4", "bd", true, true),
        new OptionNumeric(4, "num1", "nd", false, 3, 0, 5),
        new OptionNumeric(5, "num2", "nd", true, 7, 0, 5)
    )));

    // Действие:
    viewState.apply(viewActions);

    // Результат:
    verify(viewActions).setImage(R.id.mapImage, "https://maps.googleapis.com/maps/api/staticmap?"
        + "center=5.421,10.2341"
        + "&zoom=16"
        + "&size=360x200"
        + "&maptype=roadmap"
        + "&key=AIzaSyBwlubLyqI6z_ivfAWcTCfyTXkoRHTagMk");
    verify(viewActions).setFormattedText(R.id.distanceText, R.string.km, 12.239d);
    verify(viewActions).setFormattedText(R.id.etaText, R.string.eta, 54);
    verify(viewActions).setFormattedText(R.id.routeTitleText, R.string.route_distance, 31.278d);
    verify(viewActions).setText(R.id.nextAddressText, "address");
    verify(viewActions)
        .setFormattedText(R.id.openNavigator, R.string.client_location, 5.421, 10.2341);
    verify(viewActions).setVisible(R.id.nextAddressCommentTitleText, false);
    verify(viewActions).setVisible(R.id.nextAddressCommentText, false);
    verify(viewActions).setText(R.id.nextAddressCommentText, "");
    verify(viewActions).setText(R.id.routePointsCount, "2");
    verify(viewActions).setText(R.id.lastAddressText, R.string.free_ride);
    verify(viewActions).isShowCents();
    verify(viewActions).getCurrencyFormat();
    DecimalFormat decimalFormat = new DecimalFormat("##,###,### ¤");
    decimalFormat.setMaximumFractionDigits(2);
    decimalFormat.setMinimumFractionDigits(2);
    verify(viewActions).setFormattedText(R.id.estimationText, R.string.km_h_m_p, 31.278d, 2, 12,
        decimalFormat.format(6812.54)
    );
    verify(viewActions).setFormattedText(R.id.estimatedPriceText, R.string.price,
        decimalFormat.format(6812.54)
    );
    verify(viewActions).setText(R.id.startDateText,
        DateTimeFormat.forPattern("d MMMM, EEEE").print(DateTime.now().withMillis(1238403200L)));
    verify(viewActions).setText(R.id.startTimeText,
        DateTimeFormat.forPattern("HH:mm").print(DateTime.now().withMillis(1238403200L)));
    DateTime dateTime = DateTime.now().withMillis(1238403200L + 7929000L);
    verify(viewActions).setFormattedText(R.id.occupationTimeText, R.string.h_m_d,
        dateTime.getHourOfDay(), dateTime.getMinuteOfHour(),
        DateTimeFormat.forPattern("HH:mm").print(dateTime));
    verify(viewActions).setVisible(R.id.cargoDescTitleText, false);
    verify(viewActions).setVisible(R.id.cargoDescText, false);
    verify(viewActions).setText(R.id.cargoDescText, "");
    verify(viewActions).setVisible(R.id.optionsTitleText, true);
    verify(viewActions).setVisible(R.id.optionsText, true);
    verify(viewActions).setText(R.id.optionsText, "bool2\nbool4\nnum1: 3\nnum2: 7");
    verify(viewActions).setText(R.id.serviceText, "service");
    verify(viewActions).unblockWithPending("OrderViewStateIdle");
    verify(viewActions).dismissDialog();
    verifyNoMoreInteractions(viewActions);
  }

  @Test
  public void testEquals() {
    assertEquals(viewState, new OrderViewStateIdle(order));
    assertNotEquals(viewState, new OrderViewStateIdle(order2));
  }
}