package com.cargopull.executor_driver.presentation.order;

import androidx.annotation.NonNull;
import com.cargopull.executor_driver.R;
import com.cargopull.executor_driver.entity.Option;
import com.cargopull.executor_driver.entity.OptionBoolean;
import com.cargopull.executor_driver.entity.OptionNumeric;
import com.cargopull.executor_driver.entity.Order;
import com.cargopull.executor_driver.entity.PaymentType;
import com.cargopull.executor_driver.entity.RoutePoint;
import com.cargopull.executor_driver.presentation.ViewState;
import java.text.DecimalFormat;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;

/**
 * Состояние бездействия вида заказа.
 */
final class OrderViewStateIdle implements ViewState<OrderViewActions> {

  @NonNull
  private final Order order;

  OrderViewStateIdle(@NonNull Order order) {
    this.order = order;
  }

  @Override
  public void apply(@NonNull OrderViewActions stateActions) {
    // Показать способ оплаты
    stateActions.setVisible(R.id.paymentTypeSign, order.getPaymentType() == PaymentType.CONTRACT);
    // Задать тип маршрута
    switch (order.getRouteType()) {
      case POLYGON:
        stateActions.setText(R.id.routeType, R.string.city);
        break;
      case ORDER_ZONE:
        stateActions.setText(R.id.routeType, R.string.country);
        break;
      case INTER_CITY:
        stateActions.setText(R.id.routeType, R.string.intercity);
        break;
    }
    // Расстояние в км
    stateActions.setFormattedText(R.id.distanceText, R.string.km, order.getDistance() / 1000d);
    // Следующий адрес
    RoutePoint routePoint = order.getNextActiveRoutePoint();
    stateActions.setText(R.id.nextAddressText, routePoint.getAddress().trim());
    stateActions.setFormattedText(R.id.openNavigator, R.string.client_location,
        routePoint.getLatitude(), routePoint.getLongitude());
    // Комментарий к следующему адресу
    String routePointComment = routePoint.getComment().trim();
    stateActions.setVisible(R.id.nextAddressCommentTitleText, !routePointComment.isEmpty());
    stateActions.setVisible(R.id.nextAddressCommentText, !routePointComment.isEmpty());
    stateActions.setText(R.id.nextAddressCommentText, routePointComment);
    // Последний адрес
    int size = order.getRoutePath().size();
    if (size < 2) {
      stateActions.setText(R.id.routePointsCount, String.valueOf(2));
      stateActions.setText(R.id.lastAddressText, R.string.free_ride);
    } else {
      stateActions.setText(R.id.routePointsCount, String.valueOf(size));
      stateActions.setText(R.id.lastAddressText,
          order.getRoutePath().get(size - 1).getAddress().trim());
    }
    // Условия оплаты
    long estimatedTime = order.getEstimatedTime();
    LocalTime localTime = LocalTime.fromMillisOfDay(estimatedTime);
    boolean showCents = stateActions.isShowCents();
    float cost = order.getEstimatedPrice() / 100f;
    cost = showCents ? cost : Math.round(cost);
    int fractionDigits = showCents ? 2 : 0;
    DecimalFormat decimalFormat = new DecimalFormat(stateActions.getCurrencyFormat());
    decimalFormat.setMaximumFractionDigits(fractionDigits);
    decimalFormat.setMinimumFractionDigits(fractionDigits);
    stateActions.setFormattedText(R.id.estimationText, R.string.h_m_km,
        localTime.getHourOfDay(),
        localTime.getMinuteOfHour(),
        Math.round(order.getEstimatedRouteLength() / 1000f)
    );
    stateActions.setFormattedText(R.id.estimatedPriceText, R.string.price,
        decimalFormat.format(cost)
    );
    // Комментарий к заказу
    String orderComment = order.getComment().trim();
    stateActions.setVisible(R.id.cargoDescTitleText, !orderComment.isEmpty());
    stateActions.setVisible(R.id.cargoDescText, !orderComment.isEmpty());
    stateActions.setText(R.id.cargoDescText, orderComment);
    // Опции заказа
    StringBuilder result = new StringBuilder();
    for (Option option : order.getOptions()) {
      if (option instanceof OptionNumeric) {
        result.append(option.getName()).append(": ").append(option.getValue());
      } else if (option instanceof OptionBoolean) {
        if (!(boolean) option.getValue()) {
          continue;
        }
        result.append(option.getName());
      }
      result.append("\n");
    }
    String options = result.toString().trim();
    stateActions.setVisible(R.id.optionsTitleText, !options.isEmpty());
    stateActions.setVisible(R.id.optionsText, !options.isEmpty());
    stateActions.setText(R.id.optionsText, options);
    // Дата начала предзаказа
    DateTime scheduledDate = DateTime.now().withMillis(order.getScheduledStartTime());
    stateActions.setText(R.id.startDateAndTimeText,
        DateTimeFormat.forPattern("d MMM, HH:mm")
            .withZone(DateTimeZone.forOffsetHours(3))
            .print(scheduledDate));
    // Время начала предзаказа
    stateActions.setText(R.id.startTimeText,
        DateTimeFormat.forPattern("HH:mm")
            .withZone(DateTimeZone.forOffsetHours(3))
            .print(scheduledDate));
    // Разблокируем экран
    stateActions.unblockWithPending("OrderViewState");
    // Убираем диалог
    stateActions.dismissDialog();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    OrderViewStateIdle that = (OrderViewStateIdle) o;

    return order.equals(that.order);
  }

  @Override
  public int hashCode() {
    return order.hashCode();
  }
}
