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
import com.cargopull.executor_driver.entity.PaymentType;
import com.cargopull.executor_driver.entity.RoutePoint;
import com.cargopull.executor_driver.entity.RouteType;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
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
  public void testActionsWithCommentForCashInCity() {
    // Дано:
    when(viewActions.isShowCents()).thenReturn(false);
    when(viewActions.getCurrencyFormat()).thenReturn("##,###,### ₽");
    when(routePoint.getAddress()).thenReturn("address");
    when(routePoint.getLatitude()).thenReturn(5.421);
    when(routePoint.getLongitude()).thenReturn(10.2341);
    when(routePoint.getComment()).thenReturn("a comment");
    when(routePoint2.getAddress()).thenReturn("address 2");
    when(order.getPaymentType()).thenReturn(PaymentType.CASH);
    when(order.getRouteType()).thenReturn(RouteType.POLYGON);
    when(order.getNextActiveRoutePoint()).thenReturn(routePoint);
    when(order.getDistance()).thenReturn(12239);
    when(order.getEstimatedRouteLength()).thenReturn(31278L);
    when(order.getScheduledStartTime()).thenReturn(1238403200L);
    when(order.getRoutePath()).thenReturn(Arrays.asList(routePoint, routePoint1, routePoint2));
    when(order.getEstimatedTime()).thenReturn(7929000L);
    when(order.getEstimatedPrice()).thenReturn(681250L);
    when(order.getComment()).thenReturn("comm");
    when(order.getOptions()).thenReturn(new ArrayList<>());

    // Действие:
    viewState.apply(viewActions);

    // Результат:
    verify(viewActions).setVisible(R.id.paymentTypeSign, false);
    verify(viewActions).setText(R.id.routeType, R.string.city);
    verify(viewActions).setFormattedText(R.id.distanceText, R.string.km, 12.239d);
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
    DecimalFormat decimalFormat = new DecimalFormat("##,###,### ₽");
    decimalFormat.setMaximumFractionDigits(0);
    decimalFormat.setMinimumFractionDigits(0);
    verify(viewActions).setFormattedText(R.id.estimationText, R.string.h_m_km, 2, 12, 31.278d);
    verify(viewActions).setFormattedText(R.id.estimatedPriceText, R.string.price,
        decimalFormat.format(6813)
    );
    verify(viewActions).setText(R.id.startDateAndTimeText,
        DateTimeFormat.forPattern("d MMM, HH:mm").print(
            DateTime.now().withMillis(1238403200L).withZone(DateTimeZone.forOffsetHours(3))
        ));
    verify(viewActions).setText(R.id.startTimeText,
        DateTimeFormat.forPattern("HH:mm").print(
            DateTime.now().withMillis(1238403200L).withZone(DateTimeZone.forOffsetHours(3))
        ));
    verify(viewActions).setVisible(R.id.cargoDescTitleText, true);
    verify(viewActions).setVisible(R.id.cargoDescText, true);
    verify(viewActions).setText(R.id.cargoDescText, "comm");
    verify(viewActions).setVisible(R.id.optionsTitleText, false);
    verify(viewActions).setVisible(R.id.optionsText, false);
    verify(viewActions).setText(R.id.optionsText, "");
    verify(viewActions).unblockWithPending("OrderViewState");
    verify(viewActions).dismissDialog();
    verifyNoMoreInteractions(viewActions);
  }

  @Test
  public void testActionsWithoutCommentForCashInCity() {
    // Дано:
    when(viewActions.isShowCents()).thenReturn(true);
    when(viewActions.getCurrencyFormat()).thenReturn("##,###,###.## ₽");
    when(routePoint.getAddress()).thenReturn("address");
    when(routePoint.getLatitude()).thenReturn(5.421);
    when(routePoint.getLongitude()).thenReturn(10.2341);
    when(routePoint.getComment()).thenReturn("");
    when(routePoint2.getAddress()).thenReturn("address 2");
    when(order.getPaymentType()).thenReturn(PaymentType.CASH);
    when(order.getRouteType()).thenReturn(RouteType.POLYGON);
    when(order.getNextActiveRoutePoint()).thenReturn(routePoint);
    when(order.getDistance()).thenReturn(12239);
    when(order.getEstimatedRouteLength()).thenReturn(31278L);
    when(order.getScheduledStartTime()).thenReturn(1238403200L);
    when(order.getRoutePath()).thenReturn(Arrays.asList(routePoint, routePoint1, routePoint2));
    when(order.getEstimatedTime()).thenReturn(7929000L);
    when(order.getEstimatedPrice()).thenReturn(681250L);
    when(order.getComment()).thenReturn("");
    when(order.getOptions()).thenReturn(new ArrayList<>(Arrays.asList(
        new OptionBoolean(0, "bool1", "bd", false),
        new OptionBoolean(1, "bool2", "bd", true),
        new OptionBoolean(2, "bool3", "bd", false),
        new OptionBoolean(3, "bool4", "bd", true),
        new OptionNumeric(4, "num1", "nd", 3, 0, 5),
        new OptionNumeric(5, "num2", "nd", 7, 0, 5)
    )));

    // Действие:
    viewState.apply(viewActions);

    // Результат:
    verify(viewActions).setVisible(R.id.paymentTypeSign, false);
    verify(viewActions).setText(R.id.routeType, R.string.city);
    verify(viewActions).setFormattedText(R.id.distanceText, R.string.km, 12.239d);
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
    DecimalFormat decimalFormat = new DecimalFormat("##,###,###.## ₽");
    decimalFormat.setMaximumFractionDigits(2);
    decimalFormat.setMinimumFractionDigits(2);
    verify(viewActions).setFormattedText(R.id.estimationText, R.string.h_m_km, 2, 12, 31.278d);
    verify(viewActions).setFormattedText(R.id.estimatedPriceText, R.string.price,
        decimalFormat.format(6812.50)
    );
    verify(viewActions).setText(R.id.startDateAndTimeText,
        DateTimeFormat.forPattern("d MMM, HH:mm").print(
            DateTime.now().withMillis(1238403200L).withZone(DateTimeZone.forOffsetHours(3))
        ));
    verify(viewActions).setText(R.id.startTimeText,
        DateTimeFormat.forPattern("HH:mm").print(
            DateTime.now().withMillis(1238403200L).withZone(DateTimeZone.forOffsetHours(3))
        ));
    verify(viewActions).setVisible(R.id.cargoDescTitleText, false);
    verify(viewActions).setVisible(R.id.cargoDescText, false);
    verify(viewActions).setText(R.id.cargoDescText, "");
    verify(viewActions).setVisible(R.id.optionsTitleText, true);
    verify(viewActions).setVisible(R.id.optionsText, true);
    verify(viewActions).setText(R.id.optionsText, "bool2\nbool4\nnum1: 3\nnum2: 7");
    verify(viewActions).unblockWithPending("OrderViewState");
    verify(viewActions).dismissDialog();
    verifyNoMoreInteractions(viewActions);
  }

  @Test
  public void testActionsWithCommentFreeRideForCashInCity() {
    // Дано:
    when(viewActions.isShowCents()).thenReturn(false);
    when(viewActions.getCurrencyFormat()).thenReturn("##,###,### ₽");
    when(routePoint.getAddress()).thenReturn("address");
    when(routePoint.getLatitude()).thenReturn(5.421);
    when(routePoint.getLongitude()).thenReturn(10.2341);
    when(routePoint.getComment()).thenReturn("a comment");
    when(order.getPaymentType()).thenReturn(PaymentType.CASH);
    when(order.getRouteType()).thenReturn(RouteType.POLYGON);
    when(order.getNextActiveRoutePoint()).thenReturn(routePoint);
    when(order.getDistance()).thenReturn(12239);
    when(order.getEstimatedRouteLength()).thenReturn(31278L);
    when(order.getScheduledStartTime()).thenReturn(1238403200L);
    when(order.getRoutePath()).thenReturn(Collections.singletonList(routePoint));
    when(order.getEstimatedTime()).thenReturn(7929000L);
    when(order.getEstimatedPrice()).thenReturn(681254L);
    when(order.getComment()).thenReturn("comm");
    when(order.getOptions()).thenReturn(new ArrayList<>(Arrays.asList(
        new OptionBoolean(0, "bool1", "bd", false),
        new OptionBoolean(1, "bool2", "bd", true),
        new OptionBoolean(2, "bool3", "bd", false),
        new OptionBoolean(3, "bool4", "bd", true),
        new OptionNumeric(4, "num1", "nd", 3, 0, 5),
        new OptionNumeric(5, "num2", "nd", 7, 0, 5)
    )));

    // Действие:
    viewState.apply(viewActions);

    // Результат:
    verify(viewActions).setVisible(R.id.paymentTypeSign, false);
    verify(viewActions).setText(R.id.routeType, R.string.city);
    verify(viewActions).setFormattedText(R.id.distanceText, R.string.km, 12.239d);
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
    DecimalFormat decimalFormat = new DecimalFormat("##,###,### ₽");
    decimalFormat.setMaximumFractionDigits(0);
    decimalFormat.setMinimumFractionDigits(0);
    verify(viewActions).setFormattedText(R.id.estimationText, R.string.h_m_km, 2, 12, 31.278d);
    verify(viewActions).setFormattedText(R.id.estimatedPriceText, R.string.price,
        decimalFormat.format(6813)
    );
    verify(viewActions).setText(R.id.startDateAndTimeText,
        DateTimeFormat.forPattern("d MMM, HH:mm").print(
            DateTime.now().withMillis(1238403200L).withZone(DateTimeZone.forOffsetHours(3))
        ));
    verify(viewActions).setText(R.id.startTimeText,
        DateTimeFormat.forPattern("HH:mm").print(
            DateTime.now().withMillis(1238403200L).withZone(DateTimeZone.forOffsetHours(3))
        ));
    verify(viewActions).setVisible(R.id.cargoDescTitleText, true);
    verify(viewActions).setVisible(R.id.cargoDescText, true);
    verify(viewActions).setText(R.id.cargoDescText, "comm");
    verify(viewActions).setVisible(R.id.optionsTitleText, true);
    verify(viewActions).setVisible(R.id.optionsText, true);
    verify(viewActions).setText(R.id.optionsText, "bool2\nbool4\nnum1: 3\nnum2: 7");
    verify(viewActions).unblockWithPending("OrderViewState");
    verify(viewActions).dismissDialog();
    verifyNoMoreInteractions(viewActions);
  }

  @Test
  public void testActionsWithoutCommentFreeRideForCashInCity() {
    // Дано:
    when(viewActions.isShowCents()).thenReturn(true);
    when(viewActions.getCurrencyFormat()).thenReturn("##,###,###.## ₽");
    when(routePoint.getAddress()).thenReturn("address");
    when(routePoint.getLatitude()).thenReturn(5.421);
    when(routePoint.getLongitude()).thenReturn(10.2341);
    when(routePoint.getComment()).thenReturn("");
    when(order.getPaymentType()).thenReturn(PaymentType.CASH);
    when(order.getRouteType()).thenReturn(RouteType.POLYGON);
    when(order.getRoutePath()).thenReturn(Collections.singletonList(routePoint));
    when(order.getNextActiveRoutePoint()).thenReturn(routePoint);
    when(order.getDistance()).thenReturn(12239);
    when(order.getEstimatedRouteLength()).thenReturn(31278L);
    when(order.getScheduledStartTime()).thenReturn(1238403200L);
    when(order.getEstimatedTime()).thenReturn(7929000L);
    when(order.getEstimatedPrice()).thenReturn(681254L);
    when(order.getComment()).thenReturn("");
    when(order.getOptions()).thenReturn(new ArrayList<>(Arrays.asList(
        new OptionBoolean(0, "bool1", "bd", false),
        new OptionBoolean(1, "bool2", "bd", true),
        new OptionBoolean(2, "bool3", "bd", false),
        new OptionBoolean(3, "bool4", "bd", true),
        new OptionNumeric(4, "num1", "nd", 3, 0, 5),
        new OptionNumeric(5, "num2", "nd", 7, 0, 5)
    )));

    // Действие:
    viewState.apply(viewActions);

    // Результат:
    verify(viewActions).setVisible(R.id.paymentTypeSign, false);
    verify(viewActions).setText(R.id.routeType, R.string.city);
    verify(viewActions).setFormattedText(R.id.distanceText, R.string.km, 12.239d);
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
    DecimalFormat decimalFormat = new DecimalFormat("##,###,###.## ₽");
    decimalFormat.setMaximumFractionDigits(2);
    decimalFormat.setMinimumFractionDigits(2);
    verify(viewActions).setFormattedText(R.id.estimationText, R.string.h_m_km, 2, 12, 31.278d);
    verify(viewActions).setFormattedText(R.id.estimatedPriceText, R.string.price,
        decimalFormat.format(6812.54)
    );
    verify(viewActions).setText(R.id.startDateAndTimeText,
        DateTimeFormat.forPattern("d MMM, HH:mm").print(
            DateTime.now().withMillis(1238403200L).withZone(DateTimeZone.forOffsetHours(3))
        ));
    verify(viewActions).setText(R.id.startTimeText,
        DateTimeFormat.forPattern("HH:mm").print(
            DateTime.now().withMillis(1238403200L).withZone(DateTimeZone.forOffsetHours(3))
        ));
    verify(viewActions).setVisible(R.id.cargoDescTitleText, false);
    verify(viewActions).setVisible(R.id.cargoDescText, false);
    verify(viewActions).setText(R.id.cargoDescText, "");
    verify(viewActions).setVisible(R.id.optionsTitleText, true);
    verify(viewActions).setVisible(R.id.optionsText, true);
    verify(viewActions).setText(R.id.optionsText, "bool2\nbool4\nnum1: 3\nnum2: 7");
    verify(viewActions).unblockWithPending("OrderViewState");
    verify(viewActions).dismissDialog();
    verifyNoMoreInteractions(viewActions);
  }

  @Test
  public void testActionsWithCommentForCashlessInCity() {
    // Дано:
    when(viewActions.isShowCents()).thenReturn(false);
    when(viewActions.getCurrencyFormat()).thenReturn("##,###,### ₽");
    when(routePoint.getAddress()).thenReturn("address");
    when(routePoint.getLatitude()).thenReturn(5.421);
    when(routePoint.getLongitude()).thenReturn(10.2341);
    when(routePoint.getComment()).thenReturn("a comment");
    when(routePoint2.getAddress()).thenReturn("address 2");
    when(order.getPaymentType()).thenReturn(PaymentType.CONTRACT);
    when(order.getRouteType()).thenReturn(RouteType.POLYGON);
    when(order.getNextActiveRoutePoint()).thenReturn(routePoint);
    when(order.getDistance()).thenReturn(12239);
    when(order.getEstimatedRouteLength()).thenReturn(31278L);
    when(order.getScheduledStartTime()).thenReturn(1238403200L);
    when(order.getRoutePath()).thenReturn(Arrays.asList(routePoint, routePoint1, routePoint2));
    when(order.getEstimatedTime()).thenReturn(7929000L);
    when(order.getEstimatedPrice()).thenReturn(681250L);
    when(order.getComment()).thenReturn("comm");
    when(order.getOptions()).thenReturn(new ArrayList<>());

    // Действие:
    viewState.apply(viewActions);

    // Результат:
    verify(viewActions).setVisible(R.id.paymentTypeSign, true);
    verify(viewActions).setText(R.id.routeType, R.string.city);
    verify(viewActions).setFormattedText(R.id.distanceText, R.string.km, 12.239d);
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
    DecimalFormat decimalFormat = new DecimalFormat("##,###,### ₽");
    decimalFormat.setMaximumFractionDigits(0);
    decimalFormat.setMinimumFractionDigits(0);
    verify(viewActions).setFormattedText(R.id.estimationText, R.string.h_m_km, 2, 12, 31.278d);
    verify(viewActions).setFormattedText(R.id.estimatedPriceText, R.string.price,
        decimalFormat.format(6813)
    );
    verify(viewActions).setText(R.id.startDateAndTimeText,
        DateTimeFormat.forPattern("d MMM, HH:mm").print(
            DateTime.now().withMillis(1238403200L).withZone(DateTimeZone.forOffsetHours(3))
        ));
    verify(viewActions).setText(R.id.startTimeText,
        DateTimeFormat.forPattern("HH:mm").print(
            DateTime.now().withMillis(1238403200L).withZone(DateTimeZone.forOffsetHours(3))
        ));
    verify(viewActions).setVisible(R.id.cargoDescTitleText, true);
    verify(viewActions).setVisible(R.id.cargoDescText, true);
    verify(viewActions).setText(R.id.cargoDescText, "comm");
    verify(viewActions).setVisible(R.id.optionsTitleText, false);
    verify(viewActions).setVisible(R.id.optionsText, false);
    verify(viewActions).setText(R.id.optionsText, "");
    verify(viewActions).unblockWithPending("OrderViewState");
    verify(viewActions).dismissDialog();
    verifyNoMoreInteractions(viewActions);
  }

  @Test
  public void testActionsWithoutCommentForCashlessInCity() {
    // Дано:
    when(viewActions.isShowCents()).thenReturn(true);
    when(viewActions.getCurrencyFormat()).thenReturn("##,###,###.## ₽");
    when(routePoint.getAddress()).thenReturn("address");
    when(routePoint.getLatitude()).thenReturn(5.421);
    when(routePoint.getLongitude()).thenReturn(10.2341);
    when(routePoint.getComment()).thenReturn("");
    when(routePoint2.getAddress()).thenReturn("address 2");
    when(order.getPaymentType()).thenReturn(PaymentType.CONTRACT);
    when(order.getRouteType()).thenReturn(RouteType.POLYGON);
    when(order.getNextActiveRoutePoint()).thenReturn(routePoint);
    when(order.getDistance()).thenReturn(12239);
    when(order.getEstimatedRouteLength()).thenReturn(31278L);
    when(order.getScheduledStartTime()).thenReturn(1238403200L);
    when(order.getRoutePath()).thenReturn(Arrays.asList(routePoint, routePoint1, routePoint2));
    when(order.getEstimatedTime()).thenReturn(7929000L);
    when(order.getEstimatedPrice()).thenReturn(681250L);
    when(order.getComment()).thenReturn("");
    when(order.getOptions()).thenReturn(new ArrayList<>(Arrays.asList(
        new OptionBoolean(0, "bool1", "bd", false),
        new OptionBoolean(1, "bool2", "bd", true),
        new OptionBoolean(2, "bool3", "bd", false),
        new OptionBoolean(3, "bool4", "bd", true),
        new OptionNumeric(4, "num1", "nd", 3, 0, 5),
        new OptionNumeric(5, "num2", "nd", 7, 0, 5)
    )));

    // Действие:
    viewState.apply(viewActions);

    // Результат:
    verify(viewActions).setVisible(R.id.paymentTypeSign, true);
    verify(viewActions).setText(R.id.routeType, R.string.city);
    verify(viewActions).setFormattedText(R.id.distanceText, R.string.km, 12.239d);
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
    DecimalFormat decimalFormat = new DecimalFormat("##,###,###.## ₽");
    decimalFormat.setMaximumFractionDigits(2);
    decimalFormat.setMinimumFractionDigits(2);
    verify(viewActions).setFormattedText(R.id.estimationText, R.string.h_m_km, 2, 12, 31.278d);
    verify(viewActions).setFormattedText(R.id.estimatedPriceText, R.string.price,
        decimalFormat.format(6812.50)
    );
    verify(viewActions).setText(R.id.startDateAndTimeText,
        DateTimeFormat.forPattern("d MMM, HH:mm").print(
            DateTime.now().withMillis(1238403200L).withZone(DateTimeZone.forOffsetHours(3))
        ));
    verify(viewActions).setText(R.id.startTimeText,
        DateTimeFormat.forPattern("HH:mm").print(
            DateTime.now().withMillis(1238403200L).withZone(DateTimeZone.forOffsetHours(3))
        ));
    verify(viewActions).setVisible(R.id.cargoDescTitleText, false);
    verify(viewActions).setVisible(R.id.cargoDescText, false);
    verify(viewActions).setText(R.id.cargoDescText, "");
    verify(viewActions).setVisible(R.id.optionsTitleText, true);
    verify(viewActions).setVisible(R.id.optionsText, true);
    verify(viewActions).setText(R.id.optionsText, "bool2\nbool4\nnum1: 3\nnum2: 7");
    verify(viewActions).unblockWithPending("OrderViewState");
    verify(viewActions).dismissDialog();
    verifyNoMoreInteractions(viewActions);
  }

  @Test
  public void testActionsWithCommentFreeRideForCashlessInCity() {
    // Дано:
    when(viewActions.isShowCents()).thenReturn(false);
    when(viewActions.getCurrencyFormat()).thenReturn("##,###,### ₽");
    when(routePoint.getAddress()).thenReturn("address");
    when(routePoint.getLatitude()).thenReturn(5.421);
    when(routePoint.getLongitude()).thenReturn(10.2341);
    when(routePoint.getComment()).thenReturn("a comment");
    when(order.getPaymentType()).thenReturn(PaymentType.CONTRACT);
    when(order.getRouteType()).thenReturn(RouteType.POLYGON);
    when(order.getNextActiveRoutePoint()).thenReturn(routePoint);
    when(order.getDistance()).thenReturn(12239);
    when(order.getEstimatedRouteLength()).thenReturn(31278L);
    when(order.getScheduledStartTime()).thenReturn(1238403200L);
    when(order.getRoutePath()).thenReturn(Collections.singletonList(routePoint));
    when(order.getEstimatedTime()).thenReturn(7929000L);
    when(order.getEstimatedPrice()).thenReturn(681254L);
    when(order.getComment()).thenReturn("comm");
    when(order.getOptions()).thenReturn(new ArrayList<>(Arrays.asList(
        new OptionBoolean(0, "bool1", "bd", false),
        new OptionBoolean(1, "bool2", "bd", true),
        new OptionBoolean(2, "bool3", "bd", false),
        new OptionBoolean(3, "bool4", "bd", true),
        new OptionNumeric(4, "num1", "nd", 3, 0, 5),
        new OptionNumeric(5, "num2", "nd", 7, 0, 5)
    )));

    // Действие:
    viewState.apply(viewActions);

    // Результат:
    verify(viewActions).setVisible(R.id.paymentTypeSign, true);
    verify(viewActions).setText(R.id.routeType, R.string.city);
    verify(viewActions).setFormattedText(R.id.distanceText, R.string.km, 12.239d);
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
    DecimalFormat decimalFormat = new DecimalFormat("##,###,### ₽");
    decimalFormat.setMaximumFractionDigits(0);
    decimalFormat.setMinimumFractionDigits(0);
    verify(viewActions).setFormattedText(R.id.estimationText, R.string.h_m_km, 2, 12, 31.278d);
    verify(viewActions).setFormattedText(R.id.estimatedPriceText, R.string.price,
        decimalFormat.format(6813)
    );
    verify(viewActions).setText(R.id.startDateAndTimeText,
        DateTimeFormat.forPattern("d MMM, HH:mm").print(
            DateTime.now().withMillis(1238403200L).withZone(DateTimeZone.forOffsetHours(3))
        ));
    verify(viewActions).setText(R.id.startTimeText,
        DateTimeFormat.forPattern("HH:mm").print(
            DateTime.now().withMillis(1238403200L).withZone(DateTimeZone.forOffsetHours(3))
        ));
    verify(viewActions).setVisible(R.id.cargoDescTitleText, true);
    verify(viewActions).setVisible(R.id.cargoDescText, true);
    verify(viewActions).setText(R.id.cargoDescText, "comm");
    verify(viewActions).setVisible(R.id.optionsTitleText, true);
    verify(viewActions).setVisible(R.id.optionsText, true);
    verify(viewActions).setText(R.id.optionsText, "bool2\nbool4\nnum1: 3\nnum2: 7");
    verify(viewActions).unblockWithPending("OrderViewState");
    verify(viewActions).dismissDialog();
    verifyNoMoreInteractions(viewActions);
  }

  @Test
  public void testActionsWithoutCommentFreeRideForCashlessInCity() {
    // Дано:
    when(viewActions.isShowCents()).thenReturn(true);
    when(viewActions.getCurrencyFormat()).thenReturn("##,###,###.## ₽");
    when(routePoint.getAddress()).thenReturn("address");
    when(routePoint.getLatitude()).thenReturn(5.421);
    when(routePoint.getLongitude()).thenReturn(10.2341);
    when(routePoint.getComment()).thenReturn("");
    when(order.getPaymentType()).thenReturn(PaymentType.CONTRACT);
    when(order.getRouteType()).thenReturn(RouteType.POLYGON);
    when(order.getRoutePath()).thenReturn(Collections.singletonList(routePoint));
    when(order.getNextActiveRoutePoint()).thenReturn(routePoint);
    when(order.getDistance()).thenReturn(12239);
    when(order.getEstimatedRouteLength()).thenReturn(31278L);
    when(order.getScheduledStartTime()).thenReturn(1238403200L);
    when(order.getEstimatedTime()).thenReturn(7929000L);
    when(order.getEstimatedPrice()).thenReturn(681254L);
    when(order.getComment()).thenReturn("");
    when(order.getOptions()).thenReturn(new ArrayList<>(Arrays.asList(
        new OptionBoolean(0, "bool1", "bd", false),
        new OptionBoolean(1, "bool2", "bd", true),
        new OptionBoolean(2, "bool3", "bd", false),
        new OptionBoolean(3, "bool4", "bd", true),
        new OptionNumeric(4, "num1", "nd", 3, 0, 5),
        new OptionNumeric(5, "num2", "nd", 7, 0, 5)
    )));

    // Действие:
    viewState.apply(viewActions);

    // Результат:
    verify(viewActions).setVisible(R.id.paymentTypeSign, true);
    verify(viewActions).setText(R.id.routeType, R.string.city);
    verify(viewActions).setFormattedText(R.id.distanceText, R.string.km, 12.239d);
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
    DecimalFormat decimalFormat = new DecimalFormat("##,###,###.## ₽");
    decimalFormat.setMaximumFractionDigits(2);
    decimalFormat.setMinimumFractionDigits(2);
    verify(viewActions).setFormattedText(R.id.estimationText, R.string.h_m_km, 2, 12, 31.278d);
    verify(viewActions).setFormattedText(R.id.estimatedPriceText, R.string.price,
        decimalFormat.format(6812.54)
    );
    verify(viewActions).setText(R.id.startDateAndTimeText,
        DateTimeFormat.forPattern("d MMM, HH:mm").print(
            DateTime.now().withMillis(1238403200L).withZone(DateTimeZone.forOffsetHours(3))
        ));
    verify(viewActions).setText(R.id.startTimeText,
        DateTimeFormat.forPattern("HH:mm").print(
            DateTime.now().withMillis(1238403200L).withZone(DateTimeZone.forOffsetHours(3))
        ));
    verify(viewActions).setVisible(R.id.cargoDescTitleText, false);
    verify(viewActions).setVisible(R.id.cargoDescText, false);
    verify(viewActions).setText(R.id.cargoDescText, "");
    verify(viewActions).setVisible(R.id.optionsTitleText, true);
    verify(viewActions).setVisible(R.id.optionsText, true);
    verify(viewActions).setText(R.id.optionsText, "bool2\nbool4\nnum1: 3\nnum2: 7");
    verify(viewActions).unblockWithPending("OrderViewState");
    verify(viewActions).dismissDialog();
    verifyNoMoreInteractions(viewActions);
  }

  @Test
  public void testActionsWithCommentForCashInCountry() {
    // Дано:
    when(viewActions.isShowCents()).thenReturn(false);
    when(viewActions.getCurrencyFormat()).thenReturn("##,###,### ₽");
    when(routePoint.getAddress()).thenReturn("address");
    when(routePoint.getLatitude()).thenReturn(5.421);
    when(routePoint.getLongitude()).thenReturn(10.2341);
    when(routePoint.getComment()).thenReturn("a comment");
    when(routePoint2.getAddress()).thenReturn("address 2");
    when(order.getPaymentType()).thenReturn(PaymentType.CASH);
    when(order.getRouteType()).thenReturn(RouteType.ORDER_ZONE);
    when(order.getNextActiveRoutePoint()).thenReturn(routePoint);
    when(order.getDistance()).thenReturn(12239);
    when(order.getEstimatedRouteLength()).thenReturn(31278L);
    when(order.getScheduledStartTime()).thenReturn(1238403200L);
    when(order.getRoutePath()).thenReturn(Arrays.asList(routePoint, routePoint1, routePoint2));
    when(order.getEstimatedTime()).thenReturn(7929000L);
    when(order.getEstimatedPrice()).thenReturn(681250L);
    when(order.getComment()).thenReturn("comm");
    when(order.getOptions()).thenReturn(new ArrayList<>());

    // Действие:
    viewState.apply(viewActions);

    // Результат:
    verify(viewActions).setVisible(R.id.paymentTypeSign, false);
    verify(viewActions).setText(R.id.routeType, R.string.country);
    verify(viewActions).setFormattedText(R.id.distanceText, R.string.km, 12.239d);
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
    DecimalFormat decimalFormat = new DecimalFormat("##,###,### ₽");
    decimalFormat.setMaximumFractionDigits(0);
    decimalFormat.setMinimumFractionDigits(0);
    verify(viewActions).setFormattedText(R.id.estimationText, R.string.h_m_km, 2, 12, 31.278d);
    verify(viewActions).setFormattedText(R.id.estimatedPriceText, R.string.price,
        decimalFormat.format(6813)
    );
    verify(viewActions).setText(R.id.startDateAndTimeText,
        DateTimeFormat.forPattern("d MMM, HH:mm").print(
            DateTime.now().withMillis(1238403200L).withZone(DateTimeZone.forOffsetHours(3))
        ));
    verify(viewActions).setText(R.id.startTimeText,
        DateTimeFormat.forPattern("HH:mm").print(
            DateTime.now().withMillis(1238403200L).withZone(DateTimeZone.forOffsetHours(3))
        ));
    verify(viewActions).setVisible(R.id.cargoDescTitleText, true);
    verify(viewActions).setVisible(R.id.cargoDescText, true);
    verify(viewActions).setText(R.id.cargoDescText, "comm");
    verify(viewActions).setVisible(R.id.optionsTitleText, false);
    verify(viewActions).setVisible(R.id.optionsText, false);
    verify(viewActions).setText(R.id.optionsText, "");
    verify(viewActions).unblockWithPending("OrderViewState");
    verify(viewActions).dismissDialog();
    verifyNoMoreInteractions(viewActions);
  }

  @Test
  public void testActionsWithoutCommentForCashInCountry() {
    // Дано:
    when(viewActions.isShowCents()).thenReturn(true);
    when(viewActions.getCurrencyFormat()).thenReturn("##,###,###.## ₽");
    when(routePoint.getAddress()).thenReturn("address");
    when(routePoint.getLatitude()).thenReturn(5.421);
    when(routePoint.getLongitude()).thenReturn(10.2341);
    when(routePoint.getComment()).thenReturn("");
    when(routePoint2.getAddress()).thenReturn("address 2");
    when(order.getPaymentType()).thenReturn(PaymentType.CASH);
    when(order.getRouteType()).thenReturn(RouteType.ORDER_ZONE);
    when(order.getNextActiveRoutePoint()).thenReturn(routePoint);
    when(order.getDistance()).thenReturn(12239);
    when(order.getEstimatedRouteLength()).thenReturn(31278L);
    when(order.getScheduledStartTime()).thenReturn(1238403200L);
    when(order.getRoutePath()).thenReturn(Arrays.asList(routePoint, routePoint1, routePoint2));
    when(order.getEstimatedTime()).thenReturn(7929000L);
    when(order.getEstimatedPrice()).thenReturn(681250L);
    when(order.getComment()).thenReturn("");
    when(order.getOptions()).thenReturn(new ArrayList<>(Arrays.asList(
        new OptionBoolean(0, "bool1", "bd", false),
        new OptionBoolean(1, "bool2", "bd", true),
        new OptionBoolean(2, "bool3", "bd", false),
        new OptionBoolean(3, "bool4", "bd", true),
        new OptionNumeric(4, "num1", "nd", 3, 0, 5),
        new OptionNumeric(5, "num2", "nd", 7, 0, 5)
    )));

    // Действие:
    viewState.apply(viewActions);

    // Результат:
    verify(viewActions).setVisible(R.id.paymentTypeSign, false);
    verify(viewActions).setText(R.id.routeType, R.string.country);
    verify(viewActions).setFormattedText(R.id.distanceText, R.string.km, 12.239d);
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
    DecimalFormat decimalFormat = new DecimalFormat("##,###,###.## ₽");
    decimalFormat.setMaximumFractionDigits(2);
    decimalFormat.setMinimumFractionDigits(2);
    verify(viewActions).setFormattedText(R.id.estimationText, R.string.h_m_km, 2, 12, 31.278d);
    verify(viewActions).setFormattedText(R.id.estimatedPriceText, R.string.price,
        decimalFormat.format(6812.50)
    );
    verify(viewActions).setText(R.id.startDateAndTimeText,
        DateTimeFormat.forPattern("d MMM, HH:mm").print(
            DateTime.now().withMillis(1238403200L).withZone(DateTimeZone.forOffsetHours(3))
        ));
    verify(viewActions).setText(R.id.startTimeText,
        DateTimeFormat.forPattern("HH:mm").print(
            DateTime.now().withMillis(1238403200L).withZone(DateTimeZone.forOffsetHours(3))
        ));
    verify(viewActions).setVisible(R.id.cargoDescTitleText, false);
    verify(viewActions).setVisible(R.id.cargoDescText, false);
    verify(viewActions).setText(R.id.cargoDescText, "");
    verify(viewActions).setVisible(R.id.optionsTitleText, true);
    verify(viewActions).setVisible(R.id.optionsText, true);
    verify(viewActions).setText(R.id.optionsText, "bool2\nbool4\nnum1: 3\nnum2: 7");
    verify(viewActions).unblockWithPending("OrderViewState");
    verify(viewActions).dismissDialog();
    verifyNoMoreInteractions(viewActions);
  }

  @Test
  public void testActionsWithCommentFreeRideForCashInCountry() {
    // Дано:
    when(viewActions.isShowCents()).thenReturn(false);
    when(viewActions.getCurrencyFormat()).thenReturn("##,###,### ₽");
    when(routePoint.getAddress()).thenReturn("address");
    when(routePoint.getLatitude()).thenReturn(5.421);
    when(routePoint.getLongitude()).thenReturn(10.2341);
    when(routePoint.getComment()).thenReturn("a comment");
    when(order.getPaymentType()).thenReturn(PaymentType.CASH);
    when(order.getRouteType()).thenReturn(RouteType.ORDER_ZONE);
    when(order.getNextActiveRoutePoint()).thenReturn(routePoint);
    when(order.getDistance()).thenReturn(12239);
    when(order.getEstimatedRouteLength()).thenReturn(31278L);
    when(order.getScheduledStartTime()).thenReturn(1238403200L);
    when(order.getRoutePath()).thenReturn(Collections.singletonList(routePoint));
    when(order.getEstimatedTime()).thenReturn(7929000L);
    when(order.getEstimatedPrice()).thenReturn(681254L);
    when(order.getComment()).thenReturn("comm");
    when(order.getOptions()).thenReturn(new ArrayList<>(Arrays.asList(
        new OptionBoolean(0, "bool1", "bd", false),
        new OptionBoolean(1, "bool2", "bd", true),
        new OptionBoolean(2, "bool3", "bd", false),
        new OptionBoolean(3, "bool4", "bd", true),
        new OptionNumeric(4, "num1", "nd", 3, 0, 5),
        new OptionNumeric(5, "num2", "nd", 7, 0, 5)
    )));

    // Действие:
    viewState.apply(viewActions);

    // Результат:
    verify(viewActions).setVisible(R.id.paymentTypeSign, false);
    verify(viewActions).setText(R.id.routeType, R.string.country);
    verify(viewActions).setFormattedText(R.id.distanceText, R.string.km, 12.239d);
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
    DecimalFormat decimalFormat = new DecimalFormat("##,###,### ₽");
    decimalFormat.setMaximumFractionDigits(0);
    decimalFormat.setMinimumFractionDigits(0);
    verify(viewActions).setFormattedText(R.id.estimationText, R.string.h_m_km, 2, 12, 31.278d);
    verify(viewActions).setFormattedText(R.id.estimatedPriceText, R.string.price,
        decimalFormat.format(6813)
    );
    verify(viewActions).setText(R.id.startDateAndTimeText,
        DateTimeFormat.forPattern("d MMM, HH:mm").print(
            DateTime.now().withMillis(1238403200L).withZone(DateTimeZone.forOffsetHours(3))
        ));
    verify(viewActions).setText(R.id.startTimeText,
        DateTimeFormat.forPattern("HH:mm").print(
            DateTime.now().withMillis(1238403200L).withZone(DateTimeZone.forOffsetHours(3))
        ));
    verify(viewActions).setVisible(R.id.cargoDescTitleText, true);
    verify(viewActions).setVisible(R.id.cargoDescText, true);
    verify(viewActions).setText(R.id.cargoDescText, "comm");
    verify(viewActions).setVisible(R.id.optionsTitleText, true);
    verify(viewActions).setVisible(R.id.optionsText, true);
    verify(viewActions).setText(R.id.optionsText, "bool2\nbool4\nnum1: 3\nnum2: 7");
    verify(viewActions).unblockWithPending("OrderViewState");
    verify(viewActions).dismissDialog();
    verifyNoMoreInteractions(viewActions);
  }

  @Test
  public void testActionsWithoutCommentFreeRideForCashInCountry() {
    // Дано:
    when(viewActions.isShowCents()).thenReturn(true);
    when(viewActions.getCurrencyFormat()).thenReturn("##,###,###.## ₽");
    when(routePoint.getAddress()).thenReturn("address");
    when(routePoint.getLatitude()).thenReturn(5.421);
    when(routePoint.getLongitude()).thenReturn(10.2341);
    when(routePoint.getComment()).thenReturn("");
    when(order.getPaymentType()).thenReturn(PaymentType.CASH);
    when(order.getRouteType()).thenReturn(RouteType.ORDER_ZONE);
    when(order.getRoutePath()).thenReturn(Collections.singletonList(routePoint));
    when(order.getNextActiveRoutePoint()).thenReturn(routePoint);
    when(order.getDistance()).thenReturn(12239);
    when(order.getEstimatedRouteLength()).thenReturn(31278L);
    when(order.getScheduledStartTime()).thenReturn(1238403200L);
    when(order.getEstimatedTime()).thenReturn(7929000L);
    when(order.getEstimatedPrice()).thenReturn(681254L);
    when(order.getComment()).thenReturn("");
    when(order.getOptions()).thenReturn(new ArrayList<>(Arrays.asList(
        new OptionBoolean(0, "bool1", "bd", false),
        new OptionBoolean(1, "bool2", "bd", true),
        new OptionBoolean(2, "bool3", "bd", false),
        new OptionBoolean(3, "bool4", "bd", true),
        new OptionNumeric(4, "num1", "nd", 3, 0, 5),
        new OptionNumeric(5, "num2", "nd", 7, 0, 5)
    )));

    // Действие:
    viewState.apply(viewActions);

    // Результат:
    verify(viewActions).setVisible(R.id.paymentTypeSign, false);
    verify(viewActions).setText(R.id.routeType, R.string.country);
    verify(viewActions).setFormattedText(R.id.distanceText, R.string.km, 12.239d);
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
    DecimalFormat decimalFormat = new DecimalFormat("##,###,###.## ₽");
    decimalFormat.setMaximumFractionDigits(2);
    decimalFormat.setMinimumFractionDigits(2);
    verify(viewActions).setFormattedText(R.id.estimationText, R.string.h_m_km, 2, 12, 31.278d);
    verify(viewActions).setFormattedText(R.id.estimatedPriceText, R.string.price,
        decimalFormat.format(6812.54)
    );
    verify(viewActions).setText(R.id.startDateAndTimeText,
        DateTimeFormat.forPattern("d MMM, HH:mm").print(
            DateTime.now().withMillis(1238403200L).withZone(DateTimeZone.forOffsetHours(3))
        ));
    verify(viewActions).setText(R.id.startTimeText,
        DateTimeFormat.forPattern("HH:mm").print(
            DateTime.now().withMillis(1238403200L).withZone(DateTimeZone.forOffsetHours(3))
        ));
    verify(viewActions).setVisible(R.id.cargoDescTitleText, false);
    verify(viewActions).setVisible(R.id.cargoDescText, false);
    verify(viewActions).setText(R.id.cargoDescText, "");
    verify(viewActions).setVisible(R.id.optionsTitleText, true);
    verify(viewActions).setVisible(R.id.optionsText, true);
    verify(viewActions).setText(R.id.optionsText, "bool2\nbool4\nnum1: 3\nnum2: 7");
    verify(viewActions).unblockWithPending("OrderViewState");
    verify(viewActions).dismissDialog();
    verifyNoMoreInteractions(viewActions);
  }

  @Test
  public void testActionsWithCommentForCashlessInCountry() {
    // Дано:
    when(viewActions.isShowCents()).thenReturn(false);
    when(viewActions.getCurrencyFormat()).thenReturn("##,###,### ₽");
    when(routePoint.getAddress()).thenReturn("address");
    when(routePoint.getLatitude()).thenReturn(5.421);
    when(routePoint.getLongitude()).thenReturn(10.2341);
    when(routePoint.getComment()).thenReturn("a comment");
    when(routePoint2.getAddress()).thenReturn("address 2");
    when(order.getPaymentType()).thenReturn(PaymentType.CONTRACT);
    when(order.getRouteType()).thenReturn(RouteType.ORDER_ZONE);
    when(order.getNextActiveRoutePoint()).thenReturn(routePoint);
    when(order.getDistance()).thenReturn(12239);
    when(order.getEstimatedRouteLength()).thenReturn(31278L);
    when(order.getScheduledStartTime()).thenReturn(1238403200L);
    when(order.getRoutePath()).thenReturn(Arrays.asList(routePoint, routePoint1, routePoint2));
    when(order.getEstimatedTime()).thenReturn(7929000L);
    when(order.getEstimatedPrice()).thenReturn(681250L);
    when(order.getComment()).thenReturn("comm");
    when(order.getOptions()).thenReturn(new ArrayList<>());

    // Действие:
    viewState.apply(viewActions);

    // Результат:
    verify(viewActions).setVisible(R.id.paymentTypeSign, true);
    verify(viewActions).setText(R.id.routeType, R.string.country);
    verify(viewActions).setFormattedText(R.id.distanceText, R.string.km, 12.239d);
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
    DecimalFormat decimalFormat = new DecimalFormat("##,###,### ₽");
    decimalFormat.setMaximumFractionDigits(0);
    decimalFormat.setMinimumFractionDigits(0);
    verify(viewActions).setFormattedText(R.id.estimationText, R.string.h_m_km, 2, 12, 31.278d);
    verify(viewActions).setFormattedText(R.id.estimatedPriceText, R.string.price,
        decimalFormat.format(6813)
    );
    verify(viewActions).setText(R.id.startDateAndTimeText,
        DateTimeFormat.forPattern("d MMM, HH:mm").print(
            DateTime.now().withMillis(1238403200L).withZone(DateTimeZone.forOffsetHours(3))
        ));
    verify(viewActions).setText(R.id.startTimeText,
        DateTimeFormat.forPattern("HH:mm").print(
            DateTime.now().withMillis(1238403200L).withZone(DateTimeZone.forOffsetHours(3))
        ));
    verify(viewActions).setVisible(R.id.cargoDescTitleText, true);
    verify(viewActions).setVisible(R.id.cargoDescText, true);
    verify(viewActions).setText(R.id.cargoDescText, "comm");
    verify(viewActions).setVisible(R.id.optionsTitleText, false);
    verify(viewActions).setVisible(R.id.optionsText, false);
    verify(viewActions).setText(R.id.optionsText, "");
    verify(viewActions).unblockWithPending("OrderViewState");
    verify(viewActions).dismissDialog();
    verifyNoMoreInteractions(viewActions);
  }

  @Test
  public void testActionsWithoutCommentForCashlessInCountry() {
    // Дано:
    when(viewActions.isShowCents()).thenReturn(true);
    when(viewActions.getCurrencyFormat()).thenReturn("##,###,###.## ₽");
    when(routePoint.getAddress()).thenReturn("address");
    when(routePoint.getLatitude()).thenReturn(5.421);
    when(routePoint.getLongitude()).thenReturn(10.2341);
    when(routePoint.getComment()).thenReturn("");
    when(routePoint2.getAddress()).thenReturn("address 2");
    when(order.getPaymentType()).thenReturn(PaymentType.CONTRACT);
    when(order.getRouteType()).thenReturn(RouteType.ORDER_ZONE);
    when(order.getNextActiveRoutePoint()).thenReturn(routePoint);
    when(order.getDistance()).thenReturn(12239);
    when(order.getEstimatedRouteLength()).thenReturn(31278L);
    when(order.getScheduledStartTime()).thenReturn(1238403200L);
    when(order.getRoutePath()).thenReturn(Arrays.asList(routePoint, routePoint1, routePoint2));
    when(order.getEstimatedTime()).thenReturn(7929000L);
    when(order.getEstimatedPrice()).thenReturn(681250L);
    when(order.getComment()).thenReturn("");
    when(order.getOptions()).thenReturn(new ArrayList<>(Arrays.asList(
        new OptionBoolean(0, "bool1", "bd", false),
        new OptionBoolean(1, "bool2", "bd", true),
        new OptionBoolean(2, "bool3", "bd", false),
        new OptionBoolean(3, "bool4", "bd", true),
        new OptionNumeric(4, "num1", "nd", 3, 0, 5),
        new OptionNumeric(5, "num2", "nd", 7, 0, 5)
    )));

    // Действие:
    viewState.apply(viewActions);

    // Результат:
    verify(viewActions).setVisible(R.id.paymentTypeSign, true);
    verify(viewActions).setText(R.id.routeType, R.string.country);
    verify(viewActions).setFormattedText(R.id.distanceText, R.string.km, 12.239d);
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
    DecimalFormat decimalFormat = new DecimalFormat("##,###,###.## ₽");
    decimalFormat.setMaximumFractionDigits(2);
    decimalFormat.setMinimumFractionDigits(2);
    verify(viewActions).setFormattedText(R.id.estimationText, R.string.h_m_km, 2, 12, 31.278d);
    verify(viewActions).setFormattedText(R.id.estimatedPriceText, R.string.price,
        decimalFormat.format(6812.50)
    );
    verify(viewActions).setText(R.id.startDateAndTimeText,
        DateTimeFormat.forPattern("d MMM, HH:mm").print(
            DateTime.now().withMillis(1238403200L).withZone(DateTimeZone.forOffsetHours(3))
        ));
    verify(viewActions).setText(R.id.startTimeText,
        DateTimeFormat.forPattern("HH:mm").print(
            DateTime.now().withMillis(1238403200L).withZone(DateTimeZone.forOffsetHours(3))
        ));
    verify(viewActions).setVisible(R.id.cargoDescTitleText, false);
    verify(viewActions).setVisible(R.id.cargoDescText, false);
    verify(viewActions).setText(R.id.cargoDescText, "");
    verify(viewActions).setVisible(R.id.optionsTitleText, true);
    verify(viewActions).setVisible(R.id.optionsText, true);
    verify(viewActions).setText(R.id.optionsText, "bool2\nbool4\nnum1: 3\nnum2: 7");
    verify(viewActions).unblockWithPending("OrderViewState");
    verify(viewActions).dismissDialog();
    verifyNoMoreInteractions(viewActions);
  }

  @Test
  public void testActionsWithCommentFreeRideForCashlessInCountry() {
    // Дано:
    when(viewActions.isShowCents()).thenReturn(false);
    when(viewActions.getCurrencyFormat()).thenReturn("##,###,### ₽");
    when(routePoint.getAddress()).thenReturn("address");
    when(routePoint.getLatitude()).thenReturn(5.421);
    when(routePoint.getLongitude()).thenReturn(10.2341);
    when(routePoint.getComment()).thenReturn("a comment");
    when(order.getPaymentType()).thenReturn(PaymentType.CONTRACT);
    when(order.getRouteType()).thenReturn(RouteType.ORDER_ZONE);
    when(order.getNextActiveRoutePoint()).thenReturn(routePoint);
    when(order.getDistance()).thenReturn(12239);
    when(order.getEstimatedRouteLength()).thenReturn(31278L);
    when(order.getScheduledStartTime()).thenReturn(1238403200L);
    when(order.getRoutePath()).thenReturn(Collections.singletonList(routePoint));
    when(order.getEstimatedTime()).thenReturn(7929000L);
    when(order.getEstimatedPrice()).thenReturn(681254L);
    when(order.getComment()).thenReturn("comm");
    when(order.getOptions()).thenReturn(new ArrayList<>(Arrays.asList(
        new OptionBoolean(0, "bool1", "bd", false),
        new OptionBoolean(1, "bool2", "bd", true),
        new OptionBoolean(2, "bool3", "bd", false),
        new OptionBoolean(3, "bool4", "bd", true),
        new OptionNumeric(4, "num1", "nd", 3, 0, 5),
        new OptionNumeric(5, "num2", "nd", 7, 0, 5)
    )));

    // Действие:
    viewState.apply(viewActions);

    // Результат:
    verify(viewActions).setVisible(R.id.paymentTypeSign, true);
    verify(viewActions).setText(R.id.routeType, R.string.country);
    verify(viewActions).setFormattedText(R.id.distanceText, R.string.km, 12.239d);
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
    DecimalFormat decimalFormat = new DecimalFormat("##,###,### ₽");
    decimalFormat.setMaximumFractionDigits(0);
    decimalFormat.setMinimumFractionDigits(0);
    verify(viewActions).setFormattedText(R.id.estimationText, R.string.h_m_km, 2, 12, 31.278d);
    verify(viewActions).setFormattedText(R.id.estimatedPriceText, R.string.price,
        decimalFormat.format(6813)
    );
    verify(viewActions).setText(R.id.startDateAndTimeText,
        DateTimeFormat.forPattern("d MMM, HH:mm").print(
            DateTime.now().withMillis(1238403200L).withZone(DateTimeZone.forOffsetHours(3))
        ));
    verify(viewActions).setText(R.id.startTimeText,
        DateTimeFormat.forPattern("HH:mm").print(
            DateTime.now().withMillis(1238403200L).withZone(DateTimeZone.forOffsetHours(3))
        ));
    verify(viewActions).setVisible(R.id.cargoDescTitleText, true);
    verify(viewActions).setVisible(R.id.cargoDescText, true);
    verify(viewActions).setText(R.id.cargoDescText, "comm");
    verify(viewActions).setVisible(R.id.optionsTitleText, true);
    verify(viewActions).setVisible(R.id.optionsText, true);
    verify(viewActions).setText(R.id.optionsText, "bool2\nbool4\nnum1: 3\nnum2: 7");
    verify(viewActions).unblockWithPending("OrderViewState");
    verify(viewActions).dismissDialog();
    verifyNoMoreInteractions(viewActions);
  }

  @Test
  public void testActionsWithoutCommentFreeRideForCashlessInCountry() {
    // Дано:
    when(viewActions.isShowCents()).thenReturn(true);
    when(viewActions.getCurrencyFormat()).thenReturn("##,###,###.## ₽");
    when(routePoint.getAddress()).thenReturn("address");
    when(routePoint.getLatitude()).thenReturn(5.421);
    when(routePoint.getLongitude()).thenReturn(10.2341);
    when(routePoint.getComment()).thenReturn("");
    when(order.getPaymentType()).thenReturn(PaymentType.CONTRACT);
    when(order.getRouteType()).thenReturn(RouteType.ORDER_ZONE);
    when(order.getRoutePath()).thenReturn(Collections.singletonList(routePoint));
    when(order.getNextActiveRoutePoint()).thenReturn(routePoint);
    when(order.getDistance()).thenReturn(12239);
    when(order.getEstimatedRouteLength()).thenReturn(31278L);
    when(order.getScheduledStartTime()).thenReturn(1238403200L);
    when(order.getEstimatedTime()).thenReturn(7929000L);
    when(order.getEstimatedPrice()).thenReturn(681254L);
    when(order.getComment()).thenReturn("");
    when(order.getOptions()).thenReturn(new ArrayList<>(Arrays.asList(
        new OptionBoolean(0, "bool1", "bd", false),
        new OptionBoolean(1, "bool2", "bd", true),
        new OptionBoolean(2, "bool3", "bd", false),
        new OptionBoolean(3, "bool4", "bd", true),
        new OptionNumeric(4, "num1", "nd", 3, 0, 5),
        new OptionNumeric(5, "num2", "nd", 7, 0, 5)
    )));

    // Действие:
    viewState.apply(viewActions);

    // Результат:
    verify(viewActions).setVisible(R.id.paymentTypeSign, true);
    verify(viewActions).setText(R.id.routeType, R.string.country);
    verify(viewActions).setFormattedText(R.id.distanceText, R.string.km, 12.239d);
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
    DecimalFormat decimalFormat = new DecimalFormat("##,###,###.## ₽");
    decimalFormat.setMaximumFractionDigits(2);
    decimalFormat.setMinimumFractionDigits(2);
    verify(viewActions).setFormattedText(R.id.estimationText, R.string.h_m_km, 2, 12, 31.278d);
    verify(viewActions).setFormattedText(R.id.estimatedPriceText, R.string.price,
        decimalFormat.format(6812.54)
    );
    verify(viewActions).setText(R.id.startDateAndTimeText,
        DateTimeFormat.forPattern("d MMM, HH:mm").print(
            DateTime.now().withMillis(1238403200L).withZone(DateTimeZone.forOffsetHours(3))
        ));
    verify(viewActions).setText(R.id.startTimeText,
        DateTimeFormat.forPattern("HH:mm").print(
            DateTime.now().withMillis(1238403200L).withZone(DateTimeZone.forOffsetHours(3))
        ));
    verify(viewActions).setVisible(R.id.cargoDescTitleText, false);
    verify(viewActions).setVisible(R.id.cargoDescText, false);
    verify(viewActions).setText(R.id.cargoDescText, "");
    verify(viewActions).setVisible(R.id.optionsTitleText, true);
    verify(viewActions).setVisible(R.id.optionsText, true);
    verify(viewActions).setText(R.id.optionsText, "bool2\nbool4\nnum1: 3\nnum2: 7");
    verify(viewActions).unblockWithPending("OrderViewState");
    verify(viewActions).dismissDialog();
    verifyNoMoreInteractions(viewActions);
  }

  @Test
  public void testActionsWithCommentForCashIntercity() {
    // Дано:
    when(viewActions.isShowCents()).thenReturn(false);
    when(viewActions.getCurrencyFormat()).thenReturn("##,###,### ₽");
    when(routePoint.getAddress()).thenReturn("address");
    when(routePoint.getLatitude()).thenReturn(5.421);
    when(routePoint.getLongitude()).thenReturn(10.2341);
    when(routePoint.getComment()).thenReturn("a comment");
    when(routePoint2.getAddress()).thenReturn("address 2");
    when(order.getPaymentType()).thenReturn(PaymentType.CASH);
    when(order.getRouteType()).thenReturn(RouteType.INTER_CITY);
    when(order.getNextActiveRoutePoint()).thenReturn(routePoint);
    when(order.getDistance()).thenReturn(12239);
    when(order.getEstimatedRouteLength()).thenReturn(31278L);
    when(order.getScheduledStartTime()).thenReturn(1238403200L);
    when(order.getRoutePath()).thenReturn(Arrays.asList(routePoint, routePoint1, routePoint2));
    when(order.getEstimatedTime()).thenReturn(7929000L);
    when(order.getEstimatedPrice()).thenReturn(681250L);
    when(order.getComment()).thenReturn("comm");
    when(order.getOptions()).thenReturn(new ArrayList<>());

    // Действие:
    viewState.apply(viewActions);

    // Результат:
    verify(viewActions).setVisible(R.id.paymentTypeSign, false);
    verify(viewActions).setText(R.id.routeType, R.string.intercity);
    verify(viewActions).setFormattedText(R.id.distanceText, R.string.km, 12.239d);
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
    DecimalFormat decimalFormat = new DecimalFormat("##,###,### ₽");
    decimalFormat.setMaximumFractionDigits(0);
    decimalFormat.setMinimumFractionDigits(0);
    verify(viewActions).setFormattedText(R.id.estimationText, R.string.h_m_km, 2, 12, 31.278d);
    verify(viewActions).setFormattedText(R.id.estimatedPriceText, R.string.price,
        decimalFormat.format(6813)
    );
    verify(viewActions).setText(R.id.startDateAndTimeText,
        DateTimeFormat.forPattern("d MMM, HH:mm").print(
            DateTime.now().withMillis(1238403200L).withZone(DateTimeZone.forOffsetHours(3))
        ));
    verify(viewActions).setText(R.id.startTimeText,
        DateTimeFormat.forPattern("HH:mm").print(
            DateTime.now().withMillis(1238403200L).withZone(DateTimeZone.forOffsetHours(3))
        ));
    verify(viewActions).setVisible(R.id.cargoDescTitleText, true);
    verify(viewActions).setVisible(R.id.cargoDescText, true);
    verify(viewActions).setText(R.id.cargoDescText, "comm");
    verify(viewActions).setVisible(R.id.optionsTitleText, false);
    verify(viewActions).setVisible(R.id.optionsText, false);
    verify(viewActions).setText(R.id.optionsText, "");
    verify(viewActions).unblockWithPending("OrderViewState");
    verify(viewActions).dismissDialog();
    verifyNoMoreInteractions(viewActions);
  }

  @Test
  public void testActionsWithoutCommentForCashIntercity() {
    // Дано:
    when(viewActions.isShowCents()).thenReturn(true);
    when(viewActions.getCurrencyFormat()).thenReturn("##,###,###.## ₽");
    when(routePoint.getAddress()).thenReturn("address");
    when(routePoint.getLatitude()).thenReturn(5.421);
    when(routePoint.getLongitude()).thenReturn(10.2341);
    when(routePoint.getComment()).thenReturn("");
    when(routePoint2.getAddress()).thenReturn("address 2");
    when(order.getPaymentType()).thenReturn(PaymentType.CASH);
    when(order.getRouteType()).thenReturn(RouteType.INTER_CITY);
    when(order.getNextActiveRoutePoint()).thenReturn(routePoint);
    when(order.getDistance()).thenReturn(12239);
    when(order.getEstimatedRouteLength()).thenReturn(31278L);
    when(order.getScheduledStartTime()).thenReturn(1238403200L);
    when(order.getRoutePath()).thenReturn(Arrays.asList(routePoint, routePoint1, routePoint2));
    when(order.getEstimatedTime()).thenReturn(7929000L);
    when(order.getEstimatedPrice()).thenReturn(681250L);
    when(order.getComment()).thenReturn("");
    when(order.getOptions()).thenReturn(new ArrayList<>(Arrays.asList(
        new OptionBoolean(0, "bool1", "bd", false),
        new OptionBoolean(1, "bool2", "bd", true),
        new OptionBoolean(2, "bool3", "bd", false),
        new OptionBoolean(3, "bool4", "bd", true),
        new OptionNumeric(4, "num1", "nd", 3, 0, 5),
        new OptionNumeric(5, "num2", "nd", 7, 0, 5)
    )));

    // Действие:
    viewState.apply(viewActions);

    // Результат:
    verify(viewActions).setVisible(R.id.paymentTypeSign, false);
    verify(viewActions).setText(R.id.routeType, R.string.intercity);
    verify(viewActions).setFormattedText(R.id.distanceText, R.string.km, 12.239d);
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
    DecimalFormat decimalFormat = new DecimalFormat("##,###,###.## ₽");
    decimalFormat.setMaximumFractionDigits(2);
    decimalFormat.setMinimumFractionDigits(2);
    verify(viewActions).setFormattedText(R.id.estimationText, R.string.h_m_km, 2, 12, 31.278d);
    verify(viewActions).setFormattedText(R.id.estimatedPriceText, R.string.price,
        decimalFormat.format(6812.50)
    );
    verify(viewActions).setText(R.id.startDateAndTimeText,
        DateTimeFormat.forPattern("d MMM, HH:mm").print(
            DateTime.now().withMillis(1238403200L).withZone(DateTimeZone.forOffsetHours(3))
        ));
    verify(viewActions).setText(R.id.startTimeText,
        DateTimeFormat.forPattern("HH:mm").print(
            DateTime.now().withMillis(1238403200L).withZone(DateTimeZone.forOffsetHours(3))
        ));
    verify(viewActions).setVisible(R.id.cargoDescTitleText, false);
    verify(viewActions).setVisible(R.id.cargoDescText, false);
    verify(viewActions).setText(R.id.cargoDescText, "");
    verify(viewActions).setVisible(R.id.optionsTitleText, true);
    verify(viewActions).setVisible(R.id.optionsText, true);
    verify(viewActions).setText(R.id.optionsText, "bool2\nbool4\nnum1: 3\nnum2: 7");
    verify(viewActions).unblockWithPending("OrderViewState");
    verify(viewActions).dismissDialog();
    verifyNoMoreInteractions(viewActions);
  }

  @Test
  public void testActionsWithCommentFreeRideForCashIntercity() {
    // Дано:
    when(viewActions.isShowCents()).thenReturn(false);
    when(viewActions.getCurrencyFormat()).thenReturn("##,###,### ₽");
    when(routePoint.getAddress()).thenReturn("address");
    when(routePoint.getLatitude()).thenReturn(5.421);
    when(routePoint.getLongitude()).thenReturn(10.2341);
    when(routePoint.getComment()).thenReturn("a comment");
    when(order.getPaymentType()).thenReturn(PaymentType.CASH);
    when(order.getRouteType()).thenReturn(RouteType.INTER_CITY);
    when(order.getNextActiveRoutePoint()).thenReturn(routePoint);
    when(order.getDistance()).thenReturn(12239);
    when(order.getEstimatedRouteLength()).thenReturn(31278L);
    when(order.getScheduledStartTime()).thenReturn(1238403200L);
    when(order.getRoutePath()).thenReturn(Collections.singletonList(routePoint));
    when(order.getEstimatedTime()).thenReturn(7929000L);
    when(order.getEstimatedPrice()).thenReturn(681254L);
    when(order.getComment()).thenReturn("comm");
    when(order.getOptions()).thenReturn(new ArrayList<>(Arrays.asList(
        new OptionBoolean(0, "bool1", "bd", false),
        new OptionBoolean(1, "bool2", "bd", true),
        new OptionBoolean(2, "bool3", "bd", false),
        new OptionBoolean(3, "bool4", "bd", true),
        new OptionNumeric(4, "num1", "nd", 3, 0, 5),
        new OptionNumeric(5, "num2", "nd", 7, 0, 5)
    )));

    // Действие:
    viewState.apply(viewActions);

    // Результат:
    verify(viewActions).setVisible(R.id.paymentTypeSign, false);
    verify(viewActions).setText(R.id.routeType, R.string.intercity);
    verify(viewActions).setFormattedText(R.id.distanceText, R.string.km, 12.239d);
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
    DecimalFormat decimalFormat = new DecimalFormat("##,###,### ₽");
    decimalFormat.setMaximumFractionDigits(0);
    decimalFormat.setMinimumFractionDigits(0);
    verify(viewActions).setFormattedText(R.id.estimationText, R.string.h_m_km, 2, 12, 31.278d);
    verify(viewActions).setFormattedText(R.id.estimatedPriceText, R.string.price,
        decimalFormat.format(6813)
    );
    verify(viewActions).setText(R.id.startDateAndTimeText,
        DateTimeFormat.forPattern("d MMM, HH:mm").print(
            DateTime.now().withMillis(1238403200L).withZone(DateTimeZone.forOffsetHours(3))
        ));
    verify(viewActions).setText(R.id.startTimeText,
        DateTimeFormat.forPattern("HH:mm").print(
            DateTime.now().withMillis(1238403200L).withZone(DateTimeZone.forOffsetHours(3))
        ));
    verify(viewActions).setVisible(R.id.cargoDescTitleText, true);
    verify(viewActions).setVisible(R.id.cargoDescText, true);
    verify(viewActions).setText(R.id.cargoDescText, "comm");
    verify(viewActions).setVisible(R.id.optionsTitleText, true);
    verify(viewActions).setVisible(R.id.optionsText, true);
    verify(viewActions).setText(R.id.optionsText, "bool2\nbool4\nnum1: 3\nnum2: 7");
    verify(viewActions).unblockWithPending("OrderViewState");
    verify(viewActions).dismissDialog();
    verifyNoMoreInteractions(viewActions);
  }

  @Test
  public void testActionsWithoutCommentFreeRideForCashIntercity() {
    // Дано:
    when(viewActions.isShowCents()).thenReturn(true);
    when(viewActions.getCurrencyFormat()).thenReturn("##,###,###.## ₽");
    when(routePoint.getAddress()).thenReturn("address");
    when(routePoint.getLatitude()).thenReturn(5.421);
    when(routePoint.getLongitude()).thenReturn(10.2341);
    when(routePoint.getComment()).thenReturn("");
    when(order.getPaymentType()).thenReturn(PaymentType.CASH);
    when(order.getRouteType()).thenReturn(RouteType.INTER_CITY);
    when(order.getRoutePath()).thenReturn(Collections.singletonList(routePoint));
    when(order.getNextActiveRoutePoint()).thenReturn(routePoint);
    when(order.getDistance()).thenReturn(12239);
    when(order.getEstimatedRouteLength()).thenReturn(31278L);
    when(order.getScheduledStartTime()).thenReturn(1238403200L);
    when(order.getEstimatedTime()).thenReturn(7929000L);
    when(order.getEstimatedPrice()).thenReturn(681254L);
    when(order.getComment()).thenReturn("");
    when(order.getOptions()).thenReturn(new ArrayList<>(Arrays.asList(
        new OptionBoolean(0, "bool1", "bd", false),
        new OptionBoolean(1, "bool2", "bd", true),
        new OptionBoolean(2, "bool3", "bd", false),
        new OptionBoolean(3, "bool4", "bd", true),
        new OptionNumeric(4, "num1", "nd", 3, 0, 5),
        new OptionNumeric(5, "num2", "nd", 7, 0, 5)
    )));

    // Действие:
    viewState.apply(viewActions);

    // Результат:
    verify(viewActions).setVisible(R.id.paymentTypeSign, false);
    verify(viewActions).setText(R.id.routeType, R.string.intercity);
    verify(viewActions).setFormattedText(R.id.distanceText, R.string.km, 12.239d);
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
    DecimalFormat decimalFormat = new DecimalFormat("##,###,###.## ₽");
    decimalFormat.setMaximumFractionDigits(2);
    decimalFormat.setMinimumFractionDigits(2);
    verify(viewActions).setFormattedText(R.id.estimationText, R.string.h_m_km, 2, 12, 31.278d);
    verify(viewActions).setFormattedText(R.id.estimatedPriceText, R.string.price,
        decimalFormat.format(6812.54)
    );
    verify(viewActions).setText(R.id.startDateAndTimeText,
        DateTimeFormat.forPattern("d MMM, HH:mm").print(
            DateTime.now().withMillis(1238403200L).withZone(DateTimeZone.forOffsetHours(3))
        ));
    verify(viewActions).setText(R.id.startTimeText,
        DateTimeFormat.forPattern("HH:mm").print(
            DateTime.now().withMillis(1238403200L).withZone(DateTimeZone.forOffsetHours(3))
        ));
    verify(viewActions).setVisible(R.id.cargoDescTitleText, false);
    verify(viewActions).setVisible(R.id.cargoDescText, false);
    verify(viewActions).setText(R.id.cargoDescText, "");
    verify(viewActions).setVisible(R.id.optionsTitleText, true);
    verify(viewActions).setVisible(R.id.optionsText, true);
    verify(viewActions).setText(R.id.optionsText, "bool2\nbool4\nnum1: 3\nnum2: 7");
    verify(viewActions).unblockWithPending("OrderViewState");
    verify(viewActions).dismissDialog();
    verifyNoMoreInteractions(viewActions);
  }

  @Test
  public void testActionsWithCommentForCashlessIntercity() {
    // Дано:
    when(viewActions.isShowCents()).thenReturn(false);
    when(viewActions.getCurrencyFormat()).thenReturn("##,###,### ₽");
    when(routePoint.getAddress()).thenReturn("address");
    when(routePoint.getLatitude()).thenReturn(5.421);
    when(routePoint.getLongitude()).thenReturn(10.2341);
    when(routePoint.getComment()).thenReturn("a comment");
    when(routePoint2.getAddress()).thenReturn("address 2");
    when(order.getPaymentType()).thenReturn(PaymentType.CONTRACT);
    when(order.getRouteType()).thenReturn(RouteType.INTER_CITY);
    when(order.getNextActiveRoutePoint()).thenReturn(routePoint);
    when(order.getDistance()).thenReturn(12239);
    when(order.getEstimatedRouteLength()).thenReturn(31278L);
    when(order.getScheduledStartTime()).thenReturn(1238403200L);
    when(order.getRoutePath()).thenReturn(Arrays.asList(routePoint, routePoint1, routePoint2));
    when(order.getEstimatedTime()).thenReturn(7929000L);
    when(order.getEstimatedPrice()).thenReturn(681250L);
    when(order.getComment()).thenReturn("comm");
    when(order.getOptions()).thenReturn(new ArrayList<>());

    // Действие:
    viewState.apply(viewActions);

    // Результат:
    verify(viewActions).setVisible(R.id.paymentTypeSign, true);
    verify(viewActions).setText(R.id.routeType, R.string.intercity);
    verify(viewActions).setFormattedText(R.id.distanceText, R.string.km, 12.239d);
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
    DecimalFormat decimalFormat = new DecimalFormat("##,###,### ₽");
    decimalFormat.setMaximumFractionDigits(0);
    decimalFormat.setMinimumFractionDigits(0);
    verify(viewActions).setFormattedText(R.id.estimationText, R.string.h_m_km, 2, 12, 31.278d);
    verify(viewActions).setFormattedText(R.id.estimatedPriceText, R.string.price,
        decimalFormat.format(6813)
    );
    verify(viewActions).setText(R.id.startDateAndTimeText,
        DateTimeFormat.forPattern("d MMM, HH:mm").print(
            DateTime.now().withMillis(1238403200L).withZone(DateTimeZone.forOffsetHours(3))
        ));
    verify(viewActions).setText(R.id.startTimeText,
        DateTimeFormat.forPattern("HH:mm").print(
            DateTime.now().withMillis(1238403200L).withZone(DateTimeZone.forOffsetHours(3))
        ));
    verify(viewActions).setVisible(R.id.cargoDescTitleText, true);
    verify(viewActions).setVisible(R.id.cargoDescText, true);
    verify(viewActions).setText(R.id.cargoDescText, "comm");
    verify(viewActions).setVisible(R.id.optionsTitleText, false);
    verify(viewActions).setVisible(R.id.optionsText, false);
    verify(viewActions).setText(R.id.optionsText, "");
    verify(viewActions).unblockWithPending("OrderViewState");
    verify(viewActions).dismissDialog();
    verifyNoMoreInteractions(viewActions);
  }

  @Test
  public void testActionsWithoutCommentForCashlessIntercity() {
    // Дано:
    when(viewActions.isShowCents()).thenReturn(true);
    when(viewActions.getCurrencyFormat()).thenReturn("##,###,###.## ₽");
    when(routePoint.getAddress()).thenReturn("address");
    when(routePoint.getLatitude()).thenReturn(5.421);
    when(routePoint.getLongitude()).thenReturn(10.2341);
    when(routePoint.getComment()).thenReturn("");
    when(routePoint2.getAddress()).thenReturn("address 2");
    when(order.getPaymentType()).thenReturn(PaymentType.CONTRACT);
    when(order.getRouteType()).thenReturn(RouteType.INTER_CITY);
    when(order.getNextActiveRoutePoint()).thenReturn(routePoint);
    when(order.getDistance()).thenReturn(12239);
    when(order.getEstimatedRouteLength()).thenReturn(31278L);
    when(order.getScheduledStartTime()).thenReturn(1238403200L);
    when(order.getRoutePath()).thenReturn(Arrays.asList(routePoint, routePoint1, routePoint2));
    when(order.getEstimatedTime()).thenReturn(7929000L);
    when(order.getEstimatedPrice()).thenReturn(681250L);
    when(order.getComment()).thenReturn("");
    when(order.getOptions()).thenReturn(new ArrayList<>(Arrays.asList(
        new OptionBoolean(0, "bool1", "bd", false),
        new OptionBoolean(1, "bool2", "bd", true),
        new OptionBoolean(2, "bool3", "bd", false),
        new OptionBoolean(3, "bool4", "bd", true),
        new OptionNumeric(4, "num1", "nd", 3, 0, 5),
        new OptionNumeric(5, "num2", "nd", 7, 0, 5)
    )));

    // Действие:
    viewState.apply(viewActions);

    // Результат:
    verify(viewActions).setVisible(R.id.paymentTypeSign, true);
    verify(viewActions).setText(R.id.routeType, R.string.intercity);
    verify(viewActions).setFormattedText(R.id.distanceText, R.string.km, 12.239d);
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
    DecimalFormat decimalFormat = new DecimalFormat("##,###,###.## ₽");
    decimalFormat.setMaximumFractionDigits(2);
    decimalFormat.setMinimumFractionDigits(2);
    verify(viewActions).setFormattedText(R.id.estimationText, R.string.h_m_km, 2, 12, 31.278d);
    verify(viewActions).setFormattedText(R.id.estimatedPriceText, R.string.price,
        decimalFormat.format(6812.50)
    );
    verify(viewActions).setText(R.id.startDateAndTimeText,
        DateTimeFormat.forPattern("d MMM, HH:mm").print(
            DateTime.now().withMillis(1238403200L).withZone(DateTimeZone.forOffsetHours(3))
        ));
    verify(viewActions).setText(R.id.startTimeText,
        DateTimeFormat.forPattern("HH:mm").print(
            DateTime.now().withMillis(1238403200L).withZone(DateTimeZone.forOffsetHours(3))
        ));
    verify(viewActions).setVisible(R.id.cargoDescTitleText, false);
    verify(viewActions).setVisible(R.id.cargoDescText, false);
    verify(viewActions).setText(R.id.cargoDescText, "");
    verify(viewActions).setVisible(R.id.optionsTitleText, true);
    verify(viewActions).setVisible(R.id.optionsText, true);
    verify(viewActions).setText(R.id.optionsText, "bool2\nbool4\nnum1: 3\nnum2: 7");
    verify(viewActions).unblockWithPending("OrderViewState");
    verify(viewActions).dismissDialog();
    verifyNoMoreInteractions(viewActions);
  }

  @Test
  public void testActionsWithCommentFreeRideForCashlessIntercity() {
    // Дано:
    when(viewActions.isShowCents()).thenReturn(false);
    when(viewActions.getCurrencyFormat()).thenReturn("##,###,### ₽");
    when(routePoint.getAddress()).thenReturn("address");
    when(routePoint.getLatitude()).thenReturn(5.421);
    when(routePoint.getLongitude()).thenReturn(10.2341);
    when(routePoint.getComment()).thenReturn("a comment");
    when(order.getPaymentType()).thenReturn(PaymentType.CONTRACT);
    when(order.getRouteType()).thenReturn(RouteType.INTER_CITY);
    when(order.getNextActiveRoutePoint()).thenReturn(routePoint);
    when(order.getDistance()).thenReturn(12239);
    when(order.getEstimatedRouteLength()).thenReturn(31278L);
    when(order.getScheduledStartTime()).thenReturn(1238403200L);
    when(order.getRoutePath()).thenReturn(Collections.singletonList(routePoint));
    when(order.getEstimatedTime()).thenReturn(7929000L);
    when(order.getEstimatedPrice()).thenReturn(681254L);
    when(order.getComment()).thenReturn("comm");
    when(order.getOptions()).thenReturn(new ArrayList<>(Arrays.asList(
        new OptionBoolean(0, "bool1", "bd", false),
        new OptionBoolean(1, "bool2", "bd", true),
        new OptionBoolean(2, "bool3", "bd", false),
        new OptionBoolean(3, "bool4", "bd", true),
        new OptionNumeric(4, "num1", "nd", 3, 0, 5),
        new OptionNumeric(5, "num2", "nd", 7, 0, 5)
    )));

    // Действие:
    viewState.apply(viewActions);

    // Результат:
    verify(viewActions).setVisible(R.id.paymentTypeSign, true);
    verify(viewActions).setText(R.id.routeType, R.string.intercity);
    verify(viewActions).setFormattedText(R.id.distanceText, R.string.km, 12.239d);
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
    DecimalFormat decimalFormat = new DecimalFormat("##,###,### ₽");
    decimalFormat.setMaximumFractionDigits(0);
    decimalFormat.setMinimumFractionDigits(0);
    verify(viewActions).setFormattedText(R.id.estimationText, R.string.h_m_km, 2, 12, 31.278d);
    verify(viewActions).setFormattedText(R.id.estimatedPriceText, R.string.price,
        decimalFormat.format(6813)
    );
    verify(viewActions).setText(R.id.startDateAndTimeText,
        DateTimeFormat.forPattern("d MMM, HH:mm").print(
            DateTime.now().withMillis(1238403200L).withZone(DateTimeZone.forOffsetHours(3))
        ));
    verify(viewActions).setText(R.id.startTimeText,
        DateTimeFormat.forPattern("HH:mm").print(
            DateTime.now().withMillis(1238403200L).withZone(DateTimeZone.forOffsetHours(3))
        ));
    verify(viewActions).setVisible(R.id.cargoDescTitleText, true);
    verify(viewActions).setVisible(R.id.cargoDescText, true);
    verify(viewActions).setText(R.id.cargoDescText, "comm");
    verify(viewActions).setVisible(R.id.optionsTitleText, true);
    verify(viewActions).setVisible(R.id.optionsText, true);
    verify(viewActions).setText(R.id.optionsText, "bool2\nbool4\nnum1: 3\nnum2: 7");
    verify(viewActions).unblockWithPending("OrderViewState");
    verify(viewActions).dismissDialog();
    verifyNoMoreInteractions(viewActions);
  }

  @Test
  public void testActionsWithoutCommentFreeRideForCashlessIntercity() {
    // Дано:
    when(viewActions.isShowCents()).thenReturn(true);
    when(viewActions.getCurrencyFormat()).thenReturn("##,###,###.## ₽");
    when(routePoint.getAddress()).thenReturn("address");
    when(routePoint.getLatitude()).thenReturn(5.421);
    when(routePoint.getLongitude()).thenReturn(10.2341);
    when(routePoint.getComment()).thenReturn("");
    when(order.getPaymentType()).thenReturn(PaymentType.CONTRACT);
    when(order.getRouteType()).thenReturn(RouteType.INTER_CITY);
    when(order.getRoutePath()).thenReturn(Collections.singletonList(routePoint));
    when(order.getNextActiveRoutePoint()).thenReturn(routePoint);
    when(order.getDistance()).thenReturn(12239);
    when(order.getEstimatedRouteLength()).thenReturn(31278L);
    when(order.getScheduledStartTime()).thenReturn(1238403200L);
    when(order.getEstimatedTime()).thenReturn(7929000L);
    when(order.getEstimatedPrice()).thenReturn(681254L);
    when(order.getComment()).thenReturn("");
    when(order.getOptions()).thenReturn(new ArrayList<>(Arrays.asList(
        new OptionBoolean(0, "bool1", "bd", false),
        new OptionBoolean(1, "bool2", "bd", true),
        new OptionBoolean(2, "bool3", "bd", false),
        new OptionBoolean(3, "bool4", "bd", true),
        new OptionNumeric(4, "num1", "nd", 3, 0, 5),
        new OptionNumeric(5, "num2", "nd", 7, 0, 5)
    )));

    // Действие:
    viewState.apply(viewActions);

    // Результат:
    verify(viewActions).setVisible(R.id.paymentTypeSign, true);
    verify(viewActions).setText(R.id.routeType, R.string.intercity);
    verify(viewActions).setFormattedText(R.id.distanceText, R.string.km, 12.239d);
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
    DecimalFormat decimalFormat = new DecimalFormat("##,###,###.## ₽");
    decimalFormat.setMaximumFractionDigits(2);
    decimalFormat.setMinimumFractionDigits(2);
    verify(viewActions).setFormattedText(R.id.estimationText, R.string.h_m_km, 2, 12, 31.278d);
    verify(viewActions).setFormattedText(R.id.estimatedPriceText, R.string.price,
        decimalFormat.format(6812.54)
    );
    verify(viewActions).setText(R.id.startDateAndTimeText,
        DateTimeFormat.forPattern("d MMM, HH:mm").print(
            DateTime.now().withMillis(1238403200L).withZone(DateTimeZone.forOffsetHours(3))
        ));
    verify(viewActions).setText(R.id.startTimeText,
        DateTimeFormat.forPattern("HH:mm").print(
            DateTime.now().withMillis(1238403200L).withZone(DateTimeZone.forOffsetHours(3))
        ));
    verify(viewActions).setVisible(R.id.cargoDescTitleText, false);
    verify(viewActions).setVisible(R.id.cargoDescText, false);
    verify(viewActions).setText(R.id.cargoDescText, "");
    verify(viewActions).setVisible(R.id.optionsTitleText, true);
    verify(viewActions).setVisible(R.id.optionsText, true);
    verify(viewActions).setText(R.id.optionsText, "bool2\nbool4\nnum1: 3\nnum2: 7");
    verify(viewActions).unblockWithPending("OrderViewState");
    verify(viewActions).dismissDialog();
    verifyNoMoreInteractions(viewActions);
  }

  @Test
  public void testEquals() {
    assertEquals(viewState, new OrderViewStateIdle(order));
    assertNotEquals(viewState, new OrderViewStateIdle(order2));
  }
}