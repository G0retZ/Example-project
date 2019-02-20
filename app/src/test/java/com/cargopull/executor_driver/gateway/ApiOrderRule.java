package com.cargopull.executor_driver.gateway;

import androidx.annotation.NonNull;
import com.cargopull.executor_driver.CommonTestRule;

/**
 * Тестовое правило, которое создает JSON заказа для тестов мапперов.
 */

class ApiOrderRule extends CommonTestRule {

  private final static String FIELD_DIVIDER = ",";
  private final static String OBJECT_START = "{";
  private final static String OBJECT_END = "}";
  private final static String ORDER_ID = "\"id\": %d";
  private final static String PAYMENT_TYPE = "\"paymentType\": \"%s\"";
  private final static String ORDER_COMMENT = "\"comment\": \"%s\"";
  private final static String ORDER_ESTIMATED_AMOUNT_TEXT = "\"estimatedAmountText\": \"%s\"";
  private final static String ORDER_ESTIMATED_AMOUNT = "\"estimatedAmount\": \"%d\"";
  private final static String ORDER_ESTIMATED_TIME = "\"overPackageStartCalculationTime\": \"%d\"";
  private final static String ORDER_ESTIMATED_ROUTE_DISTANCE = "\"estimatedRouteDistance\": \"%d\"";
  private final static String ORDER_TOTAL_COST = "\"totalAmount\": %d";
  private final static String ORDER_TIMEOUT = "\"timeOut\": %d";
  private final static String ORDER_ETA = "\"etaToStartPoint\": %d";
  private final static String ORDER_CONFIRM_TIME = "\"confirmationTime\": %d";
  private final static String ORDER_START_TIME = "\"startDate\": %d";
  private final static String ORDER_SCHEDULED_START_TIME = "\"preliminaryDate\": %d";
  private final static String ORDER_DISTANCE_START = "\"executorDistance\": {";
  private final static String ORDER_DISTANCE_EXECUTOR_ID = "\"executorId\": %d";
  private final static String ORDER_DISTANCE_VALUE = "\"distance\": %d";
  private final static String ORDER_DISTANCE_END = "}";
  private final static String ORDER_SERVICE_START = "\"carSearchRequest\": {";
  private final static String ORDER_SERVICE_ID = "\"id\": %d";
  private final static String ORDER_SERVICE_NAME = "\"name\": %s";
  private final static String ORDER_SERVICE_PRICE = "\"price\": %d";
  private final static String ORDER_SERVICE_END = "}";
  private final static String ORDER_ROUTE_START = "\"route\": [";
  private final static String ROUTE_ID = "\"id\":%d";
  private final static String ROUTE_LATITUDE = "\"latitude\":%s";
  private final static String ROUTE_LONGITUDE = "\"longitude\":%s";
  private final static String ROUTE_COMMENT = "\"comment\":\"%s\"";
  private final static String ROUTE_ADDRESS = "\"address\":\"%s\"";
  private final static String ROUTE_CHECKED = "\"checked\":true";
  private final static String ROUTE_UNCHECKED = "\"checked\":false";
  private final static String ORDER_ROUTE_END = "]";
  private final static String ORDER_OPTIONS_START = "\"optionsMobile\": [";
  private final static String OPTION_ID = "\"id\":%d";
  private final static String OPTION_NAME = "\"name\": \"%s\"";
  private final static String OPTION_FALSE_VALUE = "\"value\": \"false\"";
  private final static String OPTION_TRUE_VALUE = "\"value\": \"true\"";
  private final static String OPTION_NUMERIC_VALUE = "\"value\": %d";
  private final static String OPTION_BOOLEAN = "\"numeric\": false";
  private final static String OPTION_NUMERIC = "\"numeric\": true";
  private final static String OPTION_STATIC = "\"dynamic\": false";
  private final static String OPTION_DYNAMIC = "\"dynamic\": true";
  private final static String OPTION_MIN = "\"min\":%d";
  private final static String OPTION_MAX = "\"max\":%d";
  private final static String OPTION_DESCRIPTION = "\"description\": \"%s\"";
  private final static String ORDER_OPTIONS_END = "]";

  @NonNull
  String getFullOrder() {
    return OBJECT_START
        + String.format(ORDER_ID, 7) + FIELD_DIVIDER
        + String.format(PAYMENT_TYPE, "CASH") + FIELD_DIVIDER
        + String.format(ORDER_COMMENT, "some comment") + FIELD_DIVIDER
        + String.format(ORDER_ESTIMATED_AMOUNT_TEXT, "over 9999 BTC") + FIELD_DIVIDER
        + String.format(ORDER_ESTIMATED_AMOUNT, 9999) + FIELD_DIVIDER
        + String.format(ORDER_ESTIMATED_TIME, 234_532_000) + FIELD_DIVIDER
        + String.format(ORDER_ESTIMATED_ROUTE_DISTANCE, 35_213) + FIELD_DIVIDER
        + String.format(ORDER_TOTAL_COST, 10_352) + FIELD_DIVIDER
        + String.format(ORDER_TIMEOUT, 25) + FIELD_DIVIDER
        + String.format(ORDER_ETA, 1234567890) + FIELD_DIVIDER
        + String.format(ORDER_CONFIRM_TIME, 9876543210L) + FIELD_DIVIDER
        + String.format(ORDER_START_TIME, 9876598760L) + FIELD_DIVIDER
        + String.format(ORDER_SCHEDULED_START_TIME, 128937981273L) + FIELD_DIVIDER
        + ORDER_DISTANCE_START
        + String.format(ORDER_DISTANCE_EXECUTOR_ID, 5) + FIELD_DIVIDER
        + String.format(ORDER_DISTANCE_VALUE, 546)
        + ORDER_DISTANCE_END + FIELD_DIVIDER
        + ORDER_SERVICE_START
        + String.format(ORDER_SERVICE_ID, 4) + FIELD_DIVIDER
        + String.format(ORDER_SERVICE_NAME, "service") + FIELD_DIVIDER
        + String.format(ORDER_SERVICE_PRICE, 678)
        + ORDER_SERVICE_END + FIELD_DIVIDER
        + ORDER_ROUTE_START
        + OBJECT_START
        + String.format(ROUTE_ID, 7) + FIELD_DIVIDER
        + String.format(ROUTE_LATITUDE, "12.34") + FIELD_DIVIDER
        + String.format(ROUTE_LONGITUDE, "34.12") + FIELD_DIVIDER
        + String.format(ROUTE_COMMENT, "comment 1") + FIELD_DIVIDER
        + String.format(ROUTE_ADDRESS, "address 1") + FIELD_DIVIDER
        + ROUTE_UNCHECKED
        + OBJECT_END + FIELD_DIVIDER
        + OBJECT_START
        + String.format(ROUTE_ID, 8) + FIELD_DIVIDER
        + String.format(ROUTE_LATITUDE, "56.78") + FIELD_DIVIDER
        + String.format(ROUTE_LONGITUDE, "78.56") + FIELD_DIVIDER
        + String.format(ROUTE_COMMENT, "comment 2") + FIELD_DIVIDER
        + String.format(ROUTE_ADDRESS, "address 2") + FIELD_DIVIDER
        + ROUTE_CHECKED
        + OBJECT_END + FIELD_DIVIDER
        + OBJECT_START
        + String.format(ROUTE_ID, 9) + FIELD_DIVIDER
        + String.format(ROUTE_LATITUDE, "90.12") + FIELD_DIVIDER
        + String.format(ROUTE_LONGITUDE, "12.90") + FIELD_DIVIDER
        + String.format(ROUTE_COMMENT, "comment 3") + FIELD_DIVIDER
        + String.format(ROUTE_ADDRESS, "address 3") + FIELD_DIVIDER
        + ROUTE_UNCHECKED
        + OBJECT_END
        + ORDER_ROUTE_END + FIELD_DIVIDER
        + ORDER_OPTIONS_START
        + OBJECT_START
        + String.format(OPTION_ID, 56) + FIELD_DIVIDER
        + String.format(OPTION_NAME, "Грузчики") + FIELD_DIVIDER
        + String.format(OPTION_NUMERIC_VALUE, 2) + FIELD_DIVIDER
        + OPTION_NUMERIC + FIELD_DIVIDER
        + OPTION_DYNAMIC + FIELD_DIVIDER
        + String.format(OPTION_MIN, 0) + FIELD_DIVIDER
        + String.format(OPTION_MAX, 2)
        + OBJECT_END + FIELD_DIVIDER
        + OBJECT_START
        + String.format(OPTION_ID, 55) + FIELD_DIVIDER
        + String.format(OPTION_NAME, "Ремни крепления") + FIELD_DIVIDER
        + OPTION_TRUE_VALUE + FIELD_DIVIDER
        + OPTION_BOOLEAN + FIELD_DIVIDER
        + OPTION_DYNAMIC + FIELD_DIVIDER
        + String.format(OPTION_DESCRIPTION, "Имеются стяжные ремни для для фиксации груза.")
        + OBJECT_END + FIELD_DIVIDER
        + OBJECT_START
        + String.format(OPTION_ID, 6) + FIELD_DIVIDER
        + String.format(OPTION_NAME, "Безналичная оплата") + FIELD_DIVIDER
        + OPTION_FALSE_VALUE + FIELD_DIVIDER
        + OPTION_BOOLEAN + FIELD_DIVIDER
        + OPTION_STATIC
        + OBJECT_END + FIELD_DIVIDER
        + OBJECT_START
        + String.format(OPTION_ID, 57) + FIELD_DIVIDER
        + String.format(OPTION_NAME, "Гидроборт") + FIELD_DIVIDER
        + String.format(OPTION_NUMERIC_VALUE, 1500) + FIELD_DIVIDER
        + OPTION_NUMERIC + FIELD_DIVIDER
        + OPTION_STATIC + FIELD_DIVIDER
        + String.format(OPTION_DESCRIPTION, "Поднимающая штуковина")
        + OBJECT_END
        + ORDER_OPTIONS_END
        + OBJECT_END;
  }

  @NonNull
  String getOrderWithoutId() {
    return OBJECT_START
        + String.format(PAYMENT_TYPE, "CASH") + FIELD_DIVIDER
        + String.format(ORDER_COMMENT, "some comment") + FIELD_DIVIDER
        + String.format(ORDER_ESTIMATED_AMOUNT_TEXT, "over 9999 BTC") + FIELD_DIVIDER
        + String.format(ORDER_ESTIMATED_AMOUNT, 9999) + FIELD_DIVIDER
        + String.format(ORDER_ESTIMATED_TIME, 234_532_000) + FIELD_DIVIDER
        + String.format(ORDER_ESTIMATED_ROUTE_DISTANCE, 35_213) + FIELD_DIVIDER
        + String.format(ORDER_TOTAL_COST, 10_352) + FIELD_DIVIDER
        + String.format(ORDER_TIMEOUT, 25) + FIELD_DIVIDER
        + String.format(ORDER_ETA, 1234567890) + FIELD_DIVIDER
        + String.format(ORDER_CONFIRM_TIME, 9876543210L) + FIELD_DIVIDER
        + String.format(ORDER_START_TIME, 9876598760L) + FIELD_DIVIDER
        + String.format(ORDER_SCHEDULED_START_TIME, 128937981273L) + FIELD_DIVIDER
        + ORDER_DISTANCE_START
        + String.format(ORDER_DISTANCE_EXECUTOR_ID, 5) + FIELD_DIVIDER
        + String.format(ORDER_DISTANCE_VALUE, 546)
        + ORDER_DISTANCE_END + FIELD_DIVIDER
        + ORDER_SERVICE_START
        + String.format(ORDER_SERVICE_ID, 4) + FIELD_DIVIDER
        + String.format(ORDER_SERVICE_NAME, "service") + FIELD_DIVIDER
        + String.format(ORDER_SERVICE_PRICE, 678)
        + ORDER_SERVICE_END + FIELD_DIVIDER
        + ORDER_ROUTE_START
        + OBJECT_START
        + String.format(ROUTE_ID, 7) + FIELD_DIVIDER
        + String.format(ROUTE_LATITUDE, "12.34") + FIELD_DIVIDER
        + String.format(ROUTE_LONGITUDE, "34.12") + FIELD_DIVIDER
        + String.format(ROUTE_COMMENT, "comment 1") + FIELD_DIVIDER
        + String.format(ROUTE_ADDRESS, "address 1") + FIELD_DIVIDER
        + ROUTE_UNCHECKED
        + OBJECT_END + FIELD_DIVIDER
        + OBJECT_START
        + String.format(ROUTE_ID, 8) + FIELD_DIVIDER
        + String.format(ROUTE_LATITUDE, "56.78") + FIELD_DIVIDER
        + String.format(ROUTE_LONGITUDE, "78.56") + FIELD_DIVIDER
        + String.format(ROUTE_COMMENT, "comment 2") + FIELD_DIVIDER
        + String.format(ROUTE_ADDRESS, "address 2") + FIELD_DIVIDER
        + ROUTE_CHECKED
        + OBJECT_END + FIELD_DIVIDER
        + OBJECT_START
        + String.format(ROUTE_ID, 9) + FIELD_DIVIDER
        + String.format(ROUTE_LATITUDE, "90.12") + FIELD_DIVIDER
        + String.format(ROUTE_LONGITUDE, "12.90") + FIELD_DIVIDER
        + String.format(ROUTE_COMMENT, "comment 3") + FIELD_DIVIDER
        + String.format(ROUTE_ADDRESS, "address 3") + FIELD_DIVIDER
        + ROUTE_UNCHECKED
        + OBJECT_END
        + ORDER_ROUTE_END + FIELD_DIVIDER
        + ORDER_OPTIONS_START
        + OBJECT_START
        + String.format(OPTION_ID, 56) + FIELD_DIVIDER
        + String.format(OPTION_NAME, "Грузчики") + FIELD_DIVIDER
        + String.format(OPTION_NUMERIC_VALUE, 2) + FIELD_DIVIDER
        + OPTION_NUMERIC + FIELD_DIVIDER
        + OPTION_DYNAMIC + FIELD_DIVIDER
        + String.format(OPTION_MIN, 0) + FIELD_DIVIDER
        + String.format(OPTION_MAX, 2)
        + OBJECT_END + FIELD_DIVIDER
        + OBJECT_START
        + String.format(OPTION_ID, 55) + FIELD_DIVIDER
        + String.format(OPTION_NAME, "Ремни крепления") + FIELD_DIVIDER
        + OPTION_TRUE_VALUE + FIELD_DIVIDER
        + OPTION_BOOLEAN + FIELD_DIVIDER
        + OPTION_DYNAMIC + FIELD_DIVIDER
        + String.format(OPTION_DESCRIPTION, "Имеются стяжные ремни для для фиксации груза.")
        + OBJECT_END + FIELD_DIVIDER
        + OBJECT_START
        + String.format(OPTION_ID, 6) + FIELD_DIVIDER
        + String.format(OPTION_NAME, "Безналичная оплата") + FIELD_DIVIDER
        + OPTION_FALSE_VALUE + FIELD_DIVIDER
        + OPTION_BOOLEAN + FIELD_DIVIDER
        + OPTION_STATIC
        + OBJECT_END + FIELD_DIVIDER
        + OBJECT_START
        + String.format(OPTION_ID, 57) + FIELD_DIVIDER
        + String.format(OPTION_NAME, "Гидроборт") + FIELD_DIVIDER
        + String.format(OPTION_NUMERIC_VALUE, 1500) + FIELD_DIVIDER
        + OPTION_NUMERIC + FIELD_DIVIDER
        + OPTION_STATIC + FIELD_DIVIDER
        + String.format(OPTION_DESCRIPTION, "Поднимающая штуковина")
        + OBJECT_END
        + ORDER_OPTIONS_END
        + OBJECT_END;
  }

  @NonNull
  String getOrderWithoutPaymentType() {
    return OBJECT_START
        + String.format(ORDER_ID, 7) + FIELD_DIVIDER
        + String.format(ORDER_COMMENT, "some comment") + FIELD_DIVIDER
        + String.format(ORDER_ESTIMATED_AMOUNT_TEXT, "over 9999 BTC") + FIELD_DIVIDER
        + String.format(ORDER_ESTIMATED_AMOUNT, 9999) + FIELD_DIVIDER
        + String.format(ORDER_ESTIMATED_TIME, 234_532_000) + FIELD_DIVIDER
        + String.format(ORDER_ESTIMATED_ROUTE_DISTANCE, 35_213) + FIELD_DIVIDER
        + String.format(ORDER_TOTAL_COST, 10_352) + FIELD_DIVIDER
        + String.format(ORDER_TIMEOUT, 25) + FIELD_DIVIDER
        + String.format(ORDER_ETA, 1234567890) + FIELD_DIVIDER
        + String.format(ORDER_CONFIRM_TIME, 9876543210L) + FIELD_DIVIDER
        + String.format(ORDER_START_TIME, 9876598760L) + FIELD_DIVIDER
        + String.format(ORDER_SCHEDULED_START_TIME, 128937981273L) + FIELD_DIVIDER
        + ORDER_DISTANCE_START
        + String.format(ORDER_DISTANCE_EXECUTOR_ID, 5) + FIELD_DIVIDER
        + String.format(ORDER_DISTANCE_VALUE, 546)
        + ORDER_DISTANCE_END + FIELD_DIVIDER
        + ORDER_SERVICE_START
        + String.format(ORDER_SERVICE_ID, 4) + FIELD_DIVIDER
        + String.format(ORDER_SERVICE_NAME, "service") + FIELD_DIVIDER
        + String.format(ORDER_SERVICE_PRICE, 678)
        + ORDER_SERVICE_END + FIELD_DIVIDER
        + ORDER_ROUTE_START
        + OBJECT_START
        + String.format(ROUTE_ID, 7) + FIELD_DIVIDER
        + String.format(ROUTE_LATITUDE, "12.34") + FIELD_DIVIDER
        + String.format(ROUTE_LONGITUDE, "34.12") + FIELD_DIVIDER
        + String.format(ROUTE_COMMENT, "comment 1") + FIELD_DIVIDER
        + String.format(ROUTE_ADDRESS, "address 1") + FIELD_DIVIDER
        + ROUTE_UNCHECKED
        + OBJECT_END + FIELD_DIVIDER
        + OBJECT_START
        + String.format(ROUTE_ID, 8) + FIELD_DIVIDER
        + String.format(ROUTE_LATITUDE, "56.78") + FIELD_DIVIDER
        + String.format(ROUTE_LONGITUDE, "78.56") + FIELD_DIVIDER
        + String.format(ROUTE_COMMENT, "comment 2") + FIELD_DIVIDER
        + String.format(ROUTE_ADDRESS, "address 2") + FIELD_DIVIDER
        + ROUTE_CHECKED
        + OBJECT_END + FIELD_DIVIDER
        + OBJECT_START
        + String.format(ROUTE_ID, 9) + FIELD_DIVIDER
        + String.format(ROUTE_LATITUDE, "90.12") + FIELD_DIVIDER
        + String.format(ROUTE_LONGITUDE, "12.90") + FIELD_DIVIDER
        + String.format(ROUTE_COMMENT, "comment 3") + FIELD_DIVIDER
        + String.format(ROUTE_ADDRESS, "address 3") + FIELD_DIVIDER
        + ROUTE_UNCHECKED
        + OBJECT_END
        + ORDER_ROUTE_END + FIELD_DIVIDER
        + ORDER_OPTIONS_START
        + OBJECT_START
        + String.format(OPTION_ID, 56) + FIELD_DIVIDER
        + String.format(OPTION_NAME, "Грузчики") + FIELD_DIVIDER
        + String.format(OPTION_NUMERIC_VALUE, 2) + FIELD_DIVIDER
        + OPTION_NUMERIC + FIELD_DIVIDER
        + OPTION_DYNAMIC + FIELD_DIVIDER
        + String.format(OPTION_MIN, 0) + FIELD_DIVIDER
        + String.format(OPTION_MAX, 2)
        + OBJECT_END + FIELD_DIVIDER
        + OBJECT_START
        + String.format(OPTION_ID, 55) + FIELD_DIVIDER
        + String.format(OPTION_NAME, "Ремни крепления") + FIELD_DIVIDER
        + OPTION_TRUE_VALUE + FIELD_DIVIDER
        + OPTION_BOOLEAN + FIELD_DIVIDER
        + OPTION_DYNAMIC + FIELD_DIVIDER
        + String.format(OPTION_DESCRIPTION, "Имеются стяжные ремни для для фиксации груза.")
        + OBJECT_END + FIELD_DIVIDER
        + OBJECT_START
        + String.format(OPTION_ID, 6) + FIELD_DIVIDER
        + String.format(OPTION_NAME, "Безналичная оплата") + FIELD_DIVIDER
        + OPTION_FALSE_VALUE + FIELD_DIVIDER
        + OPTION_BOOLEAN + FIELD_DIVIDER
        + OPTION_STATIC
        + OBJECT_END + FIELD_DIVIDER
        + OBJECT_START
        + String.format(OPTION_ID, 57) + FIELD_DIVIDER
        + String.format(OPTION_NAME, "Гидроборт") + FIELD_DIVIDER
        + String.format(OPTION_NUMERIC_VALUE, 1500) + FIELD_DIVIDER
        + OPTION_NUMERIC + FIELD_DIVIDER
        + OPTION_STATIC + FIELD_DIVIDER
        + String.format(OPTION_DESCRIPTION, "Поднимающая штуковина")
        + OBJECT_END
        + ORDER_OPTIONS_END
        + OBJECT_END;
  }

  @NonNull
  String getOrderWithEmptyPaymentType() {
    return OBJECT_START
        + String.format(ORDER_ID, 7) + FIELD_DIVIDER
        + String.format(PAYMENT_TYPE, "") + FIELD_DIVIDER
        + String.format(ORDER_COMMENT, "some comment") + FIELD_DIVIDER
        + String.format(ORDER_ESTIMATED_AMOUNT_TEXT, "over 9999 BTC") + FIELD_DIVIDER
        + String.format(ORDER_ESTIMATED_AMOUNT, 9999) + FIELD_DIVIDER
        + String.format(ORDER_ESTIMATED_TIME, 234_532_000) + FIELD_DIVIDER
        + String.format(ORDER_ESTIMATED_ROUTE_DISTANCE, 35_213) + FIELD_DIVIDER
        + String.format(ORDER_TOTAL_COST, 10_352) + FIELD_DIVIDER
        + String.format(ORDER_TIMEOUT, 25) + FIELD_DIVIDER
        + String.format(ORDER_ETA, 1234567890) + FIELD_DIVIDER
        + String.format(ORDER_CONFIRM_TIME, 9876543210L) + FIELD_DIVIDER
        + String.format(ORDER_START_TIME, 9876598760L) + FIELD_DIVIDER
        + String.format(ORDER_SCHEDULED_START_TIME, 128937981273L) + FIELD_DIVIDER
        + ORDER_DISTANCE_START
        + String.format(ORDER_DISTANCE_EXECUTOR_ID, 5) + FIELD_DIVIDER
        + String.format(ORDER_DISTANCE_VALUE, 546)
        + ORDER_DISTANCE_END + FIELD_DIVIDER
        + ORDER_SERVICE_START
        + String.format(ORDER_SERVICE_ID, 4) + FIELD_DIVIDER
        + String.format(ORDER_SERVICE_NAME, "service") + FIELD_DIVIDER
        + String.format(ORDER_SERVICE_PRICE, 678)
        + ORDER_SERVICE_END + FIELD_DIVIDER
        + ORDER_ROUTE_START
        + OBJECT_START
        + String.format(ROUTE_ID, 7) + FIELD_DIVIDER
        + String.format(ROUTE_LATITUDE, "12.34") + FIELD_DIVIDER
        + String.format(ROUTE_LONGITUDE, "34.12") + FIELD_DIVIDER
        + String.format(ROUTE_COMMENT, "comment 1") + FIELD_DIVIDER
        + String.format(ROUTE_ADDRESS, "address 1") + FIELD_DIVIDER
        + ROUTE_UNCHECKED
        + OBJECT_END + FIELD_DIVIDER
        + OBJECT_START
        + String.format(ROUTE_ID, 8) + FIELD_DIVIDER
        + String.format(ROUTE_LATITUDE, "56.78") + FIELD_DIVIDER
        + String.format(ROUTE_LONGITUDE, "78.56") + FIELD_DIVIDER
        + String.format(ROUTE_COMMENT, "comment 2") + FIELD_DIVIDER
        + String.format(ROUTE_ADDRESS, "address 2") + FIELD_DIVIDER
        + ROUTE_CHECKED
        + OBJECT_END + FIELD_DIVIDER
        + OBJECT_START
        + String.format(ROUTE_ID, 9) + FIELD_DIVIDER
        + String.format(ROUTE_LATITUDE, "90.12") + FIELD_DIVIDER
        + String.format(ROUTE_LONGITUDE, "12.90") + FIELD_DIVIDER
        + String.format(ROUTE_COMMENT, "comment 3") + FIELD_DIVIDER
        + String.format(ROUTE_ADDRESS, "address 3") + FIELD_DIVIDER
        + ROUTE_UNCHECKED
        + OBJECT_END
        + ORDER_ROUTE_END + FIELD_DIVIDER
        + ORDER_OPTIONS_START
        + OBJECT_START
        + String.format(OPTION_ID, 56) + FIELD_DIVIDER
        + String.format(OPTION_NAME, "Грузчики") + FIELD_DIVIDER
        + String.format(OPTION_NUMERIC_VALUE, 2) + FIELD_DIVIDER
        + OPTION_NUMERIC + FIELD_DIVIDER
        + OPTION_DYNAMIC + FIELD_DIVIDER
        + String.format(OPTION_MIN, 0) + FIELD_DIVIDER
        + String.format(OPTION_MAX, 2)
        + OBJECT_END + FIELD_DIVIDER
        + OBJECT_START
        + String.format(OPTION_ID, 55) + FIELD_DIVIDER
        + String.format(OPTION_NAME, "Ремни крепления") + FIELD_DIVIDER
        + OPTION_TRUE_VALUE + FIELD_DIVIDER
        + OPTION_BOOLEAN + FIELD_DIVIDER
        + OPTION_DYNAMIC + FIELD_DIVIDER
        + String.format(OPTION_DESCRIPTION, "Имеются стяжные ремни для для фиксации груза.")
        + OBJECT_END + FIELD_DIVIDER
        + OBJECT_START
        + String.format(OPTION_ID, 6) + FIELD_DIVIDER
        + String.format(OPTION_NAME, "Безналичная оплата") + FIELD_DIVIDER
        + OPTION_FALSE_VALUE + FIELD_DIVIDER
        + OPTION_BOOLEAN + FIELD_DIVIDER
        + OPTION_STATIC
        + OBJECT_END + FIELD_DIVIDER
        + OBJECT_START
        + String.format(OPTION_ID, 57) + FIELD_DIVIDER
        + String.format(OPTION_NAME, "Гидроборт") + FIELD_DIVIDER
        + String.format(OPTION_NUMERIC_VALUE, 1500) + FIELD_DIVIDER
        + OPTION_NUMERIC + FIELD_DIVIDER
        + OPTION_STATIC + FIELD_DIVIDER
        + String.format(OPTION_DESCRIPTION, "Поднимающая штуковина")
        + OBJECT_END
        + ORDER_OPTIONS_END
        + OBJECT_END;
  }

  @NonNull
  String getOrderWithWrongPaymentType() {
    return OBJECT_START
        + String.format(ORDER_ID, 7) + FIELD_DIVIDER
        + String.format(PAYMENT_TYPE, "!@$#!@") + FIELD_DIVIDER
        + String.format(ORDER_COMMENT, "some comment") + FIELD_DIVIDER
        + String.format(ORDER_ESTIMATED_AMOUNT_TEXT, "over 9999 BTC") + FIELD_DIVIDER
        + String.format(ORDER_ESTIMATED_AMOUNT, 9999) + FIELD_DIVIDER
        + String.format(ORDER_ESTIMATED_TIME, 234_532_000) + FIELD_DIVIDER
        + String.format(ORDER_ESTIMATED_ROUTE_DISTANCE, 35_213) + FIELD_DIVIDER
        + String.format(ORDER_TOTAL_COST, 10_352) + FIELD_DIVIDER
        + String.format(ORDER_TIMEOUT, 25) + FIELD_DIVIDER
        + String.format(ORDER_ETA, 1234567890) + FIELD_DIVIDER
        + String.format(ORDER_CONFIRM_TIME, 9876543210L) + FIELD_DIVIDER
        + String.format(ORDER_START_TIME, 9876598760L) + FIELD_DIVIDER
        + String.format(ORDER_SCHEDULED_START_TIME, 128937981273L) + FIELD_DIVIDER
        + ORDER_DISTANCE_START
        + String.format(ORDER_DISTANCE_EXECUTOR_ID, 5) + FIELD_DIVIDER
        + String.format(ORDER_DISTANCE_VALUE, 546)
        + ORDER_DISTANCE_END + FIELD_DIVIDER
        + ORDER_SERVICE_START
        + String.format(ORDER_SERVICE_ID, 4) + FIELD_DIVIDER
        + String.format(ORDER_SERVICE_NAME, "service") + FIELD_DIVIDER
        + String.format(ORDER_SERVICE_PRICE, 678)
        + ORDER_SERVICE_END + FIELD_DIVIDER
        + ORDER_ROUTE_START
        + OBJECT_START
        + String.format(ROUTE_ID, 7) + FIELD_DIVIDER
        + String.format(ROUTE_LATITUDE, "12.34") + FIELD_DIVIDER
        + String.format(ROUTE_LONGITUDE, "34.12") + FIELD_DIVIDER
        + String.format(ROUTE_COMMENT, "comment 1") + FIELD_DIVIDER
        + String.format(ROUTE_ADDRESS, "address 1") + FIELD_DIVIDER
        + ROUTE_UNCHECKED
        + OBJECT_END + FIELD_DIVIDER
        + OBJECT_START
        + String.format(ROUTE_ID, 8) + FIELD_DIVIDER
        + String.format(ROUTE_LATITUDE, "56.78") + FIELD_DIVIDER
        + String.format(ROUTE_LONGITUDE, "78.56") + FIELD_DIVIDER
        + String.format(ROUTE_COMMENT, "comment 2") + FIELD_DIVIDER
        + String.format(ROUTE_ADDRESS, "address 2") + FIELD_DIVIDER
        + ROUTE_CHECKED
        + OBJECT_END + FIELD_DIVIDER
        + OBJECT_START
        + String.format(ROUTE_ID, 9) + FIELD_DIVIDER
        + String.format(ROUTE_LATITUDE, "90.12") + FIELD_DIVIDER
        + String.format(ROUTE_LONGITUDE, "12.90") + FIELD_DIVIDER
        + String.format(ROUTE_COMMENT, "comment 3") + FIELD_DIVIDER
        + String.format(ROUTE_ADDRESS, "address 3") + FIELD_DIVIDER
        + ROUTE_UNCHECKED
        + OBJECT_END
        + ORDER_ROUTE_END + FIELD_DIVIDER
        + ORDER_OPTIONS_START
        + OBJECT_START
        + String.format(OPTION_ID, 56) + FIELD_DIVIDER
        + String.format(OPTION_NAME, "Грузчики") + FIELD_DIVIDER
        + String.format(OPTION_NUMERIC_VALUE, 2) + FIELD_DIVIDER
        + OPTION_NUMERIC + FIELD_DIVIDER
        + OPTION_DYNAMIC + FIELD_DIVIDER
        + String.format(OPTION_MIN, 0) + FIELD_DIVIDER
        + String.format(OPTION_MAX, 2)
        + OBJECT_END + FIELD_DIVIDER
        + OBJECT_START
        + String.format(OPTION_ID, 55) + FIELD_DIVIDER
        + String.format(OPTION_NAME, "Ремни крепления") + FIELD_DIVIDER
        + OPTION_TRUE_VALUE + FIELD_DIVIDER
        + OPTION_BOOLEAN + FIELD_DIVIDER
        + OPTION_DYNAMIC + FIELD_DIVIDER
        + String.format(OPTION_DESCRIPTION, "Имеются стяжные ремни для для фиксации груза.")
        + OBJECT_END + FIELD_DIVIDER
        + OBJECT_START
        + String.format(OPTION_ID, 6) + FIELD_DIVIDER
        + String.format(OPTION_NAME, "Безналичная оплата") + FIELD_DIVIDER
        + OPTION_FALSE_VALUE + FIELD_DIVIDER
        + OPTION_BOOLEAN + FIELD_DIVIDER
        + OPTION_STATIC
        + OBJECT_END + FIELD_DIVIDER
        + OBJECT_START
        + String.format(OPTION_ID, 57) + FIELD_DIVIDER
        + String.format(OPTION_NAME, "Гидроборт") + FIELD_DIVIDER
        + String.format(OPTION_NUMERIC_VALUE, 1500) + FIELD_DIVIDER
        + OPTION_NUMERIC + FIELD_DIVIDER
        + OPTION_STATIC + FIELD_DIVIDER
        + String.format(OPTION_DESCRIPTION, "Поднимающая штуковина")
        + OBJECT_END
        + ORDER_OPTIONS_END
        + OBJECT_END;
  }

  @NonNull
  String getOrderWithoutComment() {
    return OBJECT_START
        + String.format(ORDER_ID, 7) + FIELD_DIVIDER
        + String.format(PAYMENT_TYPE, "CASH") + FIELD_DIVIDER
        + String.format(ORDER_ESTIMATED_AMOUNT_TEXT, "over 9999 BTC") + FIELD_DIVIDER
        + String.format(ORDER_ESTIMATED_AMOUNT, 9999) + FIELD_DIVIDER
        + String.format(ORDER_ESTIMATED_TIME, 234_532_000) + FIELD_DIVIDER
        + String.format(ORDER_ESTIMATED_ROUTE_DISTANCE, 35_213) + FIELD_DIVIDER
        + String.format(ORDER_TOTAL_COST, 10_352) + FIELD_DIVIDER
        + String.format(ORDER_TIMEOUT, 25) + FIELD_DIVIDER
        + String.format(ORDER_ETA, 1234567890) + FIELD_DIVIDER
        + String.format(ORDER_CONFIRM_TIME, 9876543210L) + FIELD_DIVIDER
        + String.format(ORDER_START_TIME, 9876598760L) + FIELD_DIVIDER
        + String.format(ORDER_SCHEDULED_START_TIME, 128937981273L) + FIELD_DIVIDER
        + ORDER_DISTANCE_START
        + String.format(ORDER_DISTANCE_EXECUTOR_ID, 5) + FIELD_DIVIDER
        + String.format(ORDER_DISTANCE_VALUE, 546)
        + ORDER_DISTANCE_END + FIELD_DIVIDER
        + ORDER_SERVICE_START
        + String.format(ORDER_SERVICE_ID, 4) + FIELD_DIVIDER
        + String.format(ORDER_SERVICE_NAME, "service") + FIELD_DIVIDER
        + String.format(ORDER_SERVICE_PRICE, 678)
        + ORDER_SERVICE_END + FIELD_DIVIDER
        + ORDER_ROUTE_START
        + OBJECT_START
        + String.format(ROUTE_ID, 7) + FIELD_DIVIDER
        + String.format(ROUTE_LATITUDE, "12.34") + FIELD_DIVIDER
        + String.format(ROUTE_LONGITUDE, "34.12") + FIELD_DIVIDER
        + String.format(ROUTE_COMMENT, "comment 1") + FIELD_DIVIDER
        + String.format(ROUTE_ADDRESS, "address 1") + FIELD_DIVIDER
        + ROUTE_UNCHECKED
        + OBJECT_END + FIELD_DIVIDER
        + OBJECT_START
        + String.format(ROUTE_ID, 8) + FIELD_DIVIDER
        + String.format(ROUTE_LATITUDE, "56.78") + FIELD_DIVIDER
        + String.format(ROUTE_LONGITUDE, "78.56") + FIELD_DIVIDER
        + String.format(ROUTE_COMMENT, "comment 2") + FIELD_DIVIDER
        + String.format(ROUTE_ADDRESS, "address 2") + FIELD_DIVIDER
        + ROUTE_CHECKED
        + OBJECT_END + FIELD_DIVIDER
        + OBJECT_START
        + String.format(ROUTE_ID, 9) + FIELD_DIVIDER
        + String.format(ROUTE_LATITUDE, "90.12") + FIELD_DIVIDER
        + String.format(ROUTE_LONGITUDE, "12.90") + FIELD_DIVIDER
        + String.format(ROUTE_COMMENT, "comment 3") + FIELD_DIVIDER
        + String.format(ROUTE_ADDRESS, "address 3") + FIELD_DIVIDER
        + ROUTE_UNCHECKED
        + OBJECT_END
        + ORDER_ROUTE_END + FIELD_DIVIDER
        + ORDER_OPTIONS_START
        + OBJECT_START
        + String.format(OPTION_ID, 56) + FIELD_DIVIDER
        + String.format(OPTION_NAME, "Грузчики") + FIELD_DIVIDER
        + String.format(OPTION_NUMERIC_VALUE, 2) + FIELD_DIVIDER
        + OPTION_NUMERIC + FIELD_DIVIDER
        + OPTION_DYNAMIC + FIELD_DIVIDER
        + String.format(OPTION_MIN, 0) + FIELD_DIVIDER
        + String.format(OPTION_MAX, 2)
        + OBJECT_END + FIELD_DIVIDER
        + OBJECT_START
        + String.format(OPTION_ID, 55) + FIELD_DIVIDER
        + String.format(OPTION_NAME, "Ремни крепления") + FIELD_DIVIDER
        + OPTION_TRUE_VALUE + FIELD_DIVIDER
        + OPTION_BOOLEAN + FIELD_DIVIDER
        + OPTION_DYNAMIC + FIELD_DIVIDER
        + String.format(OPTION_DESCRIPTION, "Имеются стяжные ремни для для фиксации груза.")
        + OBJECT_END + FIELD_DIVIDER
        + OBJECT_START
        + String.format(OPTION_ID, 6) + FIELD_DIVIDER
        + String.format(OPTION_NAME, "Безналичная оплата") + FIELD_DIVIDER
        + OPTION_FALSE_VALUE + FIELD_DIVIDER
        + OPTION_BOOLEAN + FIELD_DIVIDER
        + OPTION_STATIC
        + OBJECT_END + FIELD_DIVIDER
        + OBJECT_START
        + String.format(OPTION_ID, 57) + FIELD_DIVIDER
        + String.format(OPTION_NAME, "Гидроборт") + FIELD_DIVIDER
        + String.format(OPTION_NUMERIC_VALUE, 1500) + FIELD_DIVIDER
        + OPTION_NUMERIC + FIELD_DIVIDER
        + OPTION_STATIC + FIELD_DIVIDER
        + String.format(OPTION_DESCRIPTION, "Поднимающая штуковина")
        + OBJECT_END
        + ORDER_OPTIONS_END
        + OBJECT_END;
  }

  @NonNull
  String getOrderWithoutEstimatedAmountText() {
    return OBJECT_START
        + String.format(ORDER_ID, 7) + FIELD_DIVIDER
        + String.format(PAYMENT_TYPE, "CASH") + FIELD_DIVIDER
        + String.format(ORDER_COMMENT, "some comment") + FIELD_DIVIDER
        + String.format(ORDER_ESTIMATED_AMOUNT, 9999) + FIELD_DIVIDER
        + String.format(ORDER_ESTIMATED_TIME, 234_532_000) + FIELD_DIVIDER
        + String.format(ORDER_ESTIMATED_ROUTE_DISTANCE, 35_213) + FIELD_DIVIDER
        + String.format(ORDER_TOTAL_COST, 10_352) + FIELD_DIVIDER
        + String.format(ORDER_TIMEOUT, 25) + FIELD_DIVIDER
        + String.format(ORDER_ETA, 1234567890) + FIELD_DIVIDER
        + String.format(ORDER_CONFIRM_TIME, 9876543210L) + FIELD_DIVIDER
        + String.format(ORDER_START_TIME, 9876598760L) + FIELD_DIVIDER
        + String.format(ORDER_SCHEDULED_START_TIME, 128937981273L) + FIELD_DIVIDER
        + ORDER_DISTANCE_START
        + String.format(ORDER_DISTANCE_EXECUTOR_ID, 5) + FIELD_DIVIDER
        + String.format(ORDER_DISTANCE_VALUE, 546)
        + ORDER_DISTANCE_END + FIELD_DIVIDER
        + ORDER_SERVICE_START
        + String.format(ORDER_SERVICE_ID, 4) + FIELD_DIVIDER
        + String.format(ORDER_SERVICE_NAME, "service") + FIELD_DIVIDER
        + String.format(ORDER_SERVICE_PRICE, 678)
        + ORDER_SERVICE_END + FIELD_DIVIDER
        + ORDER_ROUTE_START
        + OBJECT_START
        + String.format(ROUTE_ID, 7) + FIELD_DIVIDER
        + String.format(ROUTE_LATITUDE, "12.34") + FIELD_DIVIDER
        + String.format(ROUTE_LONGITUDE, "34.12") + FIELD_DIVIDER
        + String.format(ROUTE_COMMENT, "comment 1") + FIELD_DIVIDER
        + String.format(ROUTE_ADDRESS, "address 1") + FIELD_DIVIDER
        + ROUTE_UNCHECKED
        + OBJECT_END + FIELD_DIVIDER
        + OBJECT_START
        + String.format(ROUTE_ID, 8) + FIELD_DIVIDER
        + String.format(ROUTE_LATITUDE, "56.78") + FIELD_DIVIDER
        + String.format(ROUTE_LONGITUDE, "78.56") + FIELD_DIVIDER
        + String.format(ROUTE_COMMENT, "comment 2") + FIELD_DIVIDER
        + String.format(ROUTE_ADDRESS, "address 2") + FIELD_DIVIDER
        + ROUTE_CHECKED
        + OBJECT_END + FIELD_DIVIDER
        + OBJECT_START
        + String.format(ROUTE_ID, 9) + FIELD_DIVIDER
        + String.format(ROUTE_LATITUDE, "90.12") + FIELD_DIVIDER
        + String.format(ROUTE_LONGITUDE, "12.90") + FIELD_DIVIDER
        + String.format(ROUTE_COMMENT, "comment 3") + FIELD_DIVIDER
        + String.format(ROUTE_ADDRESS, "address 3") + FIELD_DIVIDER
        + ROUTE_UNCHECKED
        + OBJECT_END
        + ORDER_ROUTE_END + FIELD_DIVIDER
        + ORDER_OPTIONS_START
        + OBJECT_START
        + String.format(OPTION_ID, 56) + FIELD_DIVIDER
        + String.format(OPTION_NAME, "Грузчики") + FIELD_DIVIDER
        + String.format(OPTION_NUMERIC_VALUE, 2) + FIELD_DIVIDER
        + OPTION_NUMERIC + FIELD_DIVIDER
        + OPTION_DYNAMIC + FIELD_DIVIDER
        + String.format(OPTION_MIN, 0) + FIELD_DIVIDER
        + String.format(OPTION_MAX, 2)
        + OBJECT_END + FIELD_DIVIDER
        + OBJECT_START
        + String.format(OPTION_ID, 55) + FIELD_DIVIDER
        + String.format(OPTION_NAME, "Ремни крепления") + FIELD_DIVIDER
        + OPTION_TRUE_VALUE + FIELD_DIVIDER
        + OPTION_BOOLEAN + FIELD_DIVIDER
        + OPTION_DYNAMIC + FIELD_DIVIDER
        + String.format(OPTION_DESCRIPTION, "Имеются стяжные ремни для для фиксации груза.")
        + OBJECT_END + FIELD_DIVIDER
        + OBJECT_START
        + String.format(OPTION_ID, 6) + FIELD_DIVIDER
        + String.format(OPTION_NAME, "Безналичная оплата") + FIELD_DIVIDER
        + OPTION_FALSE_VALUE + FIELD_DIVIDER
        + OPTION_BOOLEAN + FIELD_DIVIDER
        + OPTION_STATIC
        + OBJECT_END + FIELD_DIVIDER
        + OBJECT_START
        + String.format(OPTION_ID, 57) + FIELD_DIVIDER
        + String.format(OPTION_NAME, "Гидроборт") + FIELD_DIVIDER
        + String.format(OPTION_NUMERIC_VALUE, 1500) + FIELD_DIVIDER
        + OPTION_NUMERIC + FIELD_DIVIDER
        + OPTION_STATIC + FIELD_DIVIDER
        + String.format(OPTION_DESCRIPTION, "Поднимающая штуковина")
        + OBJECT_END
        + ORDER_OPTIONS_END
        + OBJECT_END;
  }

  @NonNull
  String getOrderWithoutEstimatedAmount() {
    return OBJECT_START
        + String.format(ORDER_ID, 7) + FIELD_DIVIDER
        + String.format(PAYMENT_TYPE, "CASH") + FIELD_DIVIDER
        + String.format(ORDER_COMMENT, "some comment") + FIELD_DIVIDER
        + String.format(ORDER_ESTIMATED_AMOUNT_TEXT, "over 9999 BTC") + FIELD_DIVIDER
        + String.format(ORDER_ESTIMATED_TIME, 234_532_000) + FIELD_DIVIDER
        + String.format(ORDER_ESTIMATED_ROUTE_DISTANCE, 35_213) + FIELD_DIVIDER
        + String.format(ORDER_TOTAL_COST, 10_352) + FIELD_DIVIDER
        + String.format(ORDER_TIMEOUT, 25) + FIELD_DIVIDER
        + String.format(ORDER_ETA, 1234567890) + FIELD_DIVIDER
        + String.format(ORDER_CONFIRM_TIME, 9876543210L) + FIELD_DIVIDER
        + String.format(ORDER_START_TIME, 9876598760L) + FIELD_DIVIDER
        + String.format(ORDER_SCHEDULED_START_TIME, 128937981273L) + FIELD_DIVIDER
        + ORDER_DISTANCE_START
        + String.format(ORDER_DISTANCE_EXECUTOR_ID, 5) + FIELD_DIVIDER
        + String.format(ORDER_DISTANCE_VALUE, 546)
        + ORDER_DISTANCE_END + FIELD_DIVIDER
        + ORDER_SERVICE_START
        + String.format(ORDER_SERVICE_ID, 4) + FIELD_DIVIDER
        + String.format(ORDER_SERVICE_NAME, "service") + FIELD_DIVIDER
        + String.format(ORDER_SERVICE_PRICE, 678)
        + ORDER_SERVICE_END + FIELD_DIVIDER
        + ORDER_ROUTE_START
        + OBJECT_START
        + String.format(ROUTE_ID, 7) + FIELD_DIVIDER
        + String.format(ROUTE_LATITUDE, "12.34") + FIELD_DIVIDER
        + String.format(ROUTE_LONGITUDE, "34.12") + FIELD_DIVIDER
        + String.format(ROUTE_COMMENT, "comment 1") + FIELD_DIVIDER
        + String.format(ROUTE_ADDRESS, "address 1") + FIELD_DIVIDER
        + ROUTE_UNCHECKED
        + OBJECT_END + FIELD_DIVIDER
        + OBJECT_START
        + String.format(ROUTE_ID, 8) + FIELD_DIVIDER
        + String.format(ROUTE_LATITUDE, "56.78") + FIELD_DIVIDER
        + String.format(ROUTE_LONGITUDE, "78.56") + FIELD_DIVIDER
        + String.format(ROUTE_COMMENT, "comment 2") + FIELD_DIVIDER
        + String.format(ROUTE_ADDRESS, "address 2") + FIELD_DIVIDER
        + ROUTE_CHECKED
        + OBJECT_END + FIELD_DIVIDER
        + OBJECT_START
        + String.format(ROUTE_ID, 9) + FIELD_DIVIDER
        + String.format(ROUTE_LATITUDE, "90.12") + FIELD_DIVIDER
        + String.format(ROUTE_LONGITUDE, "12.90") + FIELD_DIVIDER
        + String.format(ROUTE_COMMENT, "comment 3") + FIELD_DIVIDER
        + String.format(ROUTE_ADDRESS, "address 3") + FIELD_DIVIDER
        + ROUTE_UNCHECKED
        + OBJECT_END
        + ORDER_ROUTE_END + FIELD_DIVIDER
        + ORDER_OPTIONS_START
        + OBJECT_START
        + String.format(OPTION_ID, 56) + FIELD_DIVIDER
        + String.format(OPTION_NAME, "Грузчики") + FIELD_DIVIDER
        + String.format(OPTION_NUMERIC_VALUE, 2) + FIELD_DIVIDER
        + OPTION_NUMERIC + FIELD_DIVIDER
        + OPTION_DYNAMIC + FIELD_DIVIDER
        + String.format(OPTION_MIN, 0) + FIELD_DIVIDER
        + String.format(OPTION_MAX, 2)
        + OBJECT_END + FIELD_DIVIDER
        + OBJECT_START
        + String.format(OPTION_ID, 55) + FIELD_DIVIDER
        + String.format(OPTION_NAME, "Ремни крепления") + FIELD_DIVIDER
        + OPTION_TRUE_VALUE + FIELD_DIVIDER
        + OPTION_BOOLEAN + FIELD_DIVIDER
        + OPTION_DYNAMIC + FIELD_DIVIDER
        + String.format(OPTION_DESCRIPTION, "Имеются стяжные ремни для для фиксации груза.")
        + OBJECT_END + FIELD_DIVIDER
        + OBJECT_START
        + String.format(OPTION_ID, 6) + FIELD_DIVIDER
        + String.format(OPTION_NAME, "Безналичная оплата") + FIELD_DIVIDER
        + OPTION_FALSE_VALUE + FIELD_DIVIDER
        + OPTION_BOOLEAN + FIELD_DIVIDER
        + OPTION_STATIC
        + OBJECT_END + FIELD_DIVIDER
        + OBJECT_START
        + String.format(OPTION_ID, 57) + FIELD_DIVIDER
        + String.format(OPTION_NAME, "Гидроборт") + FIELD_DIVIDER
        + String.format(OPTION_NUMERIC_VALUE, 1500) + FIELD_DIVIDER
        + OPTION_NUMERIC + FIELD_DIVIDER
        + OPTION_STATIC + FIELD_DIVIDER
        + String.format(OPTION_DESCRIPTION, "Поднимающая штуковина")
        + OBJECT_END
        + ORDER_OPTIONS_END
        + OBJECT_END;
  }

  @NonNull
  String getOrderWithoutEstimatedTime() {
    return OBJECT_START
        + String.format(ORDER_ID, 7) + FIELD_DIVIDER
        + String.format(PAYMENT_TYPE, "CASH") + FIELD_DIVIDER
        + String.format(ORDER_COMMENT, "some comment") + FIELD_DIVIDER
        + String.format(ORDER_ESTIMATED_AMOUNT_TEXT, "over 9999 BTC") + FIELD_DIVIDER
        + String.format(ORDER_ESTIMATED_AMOUNT, 9999) + FIELD_DIVIDER
        + String.format(ORDER_ESTIMATED_ROUTE_DISTANCE, 35_213) + FIELD_DIVIDER
        + String.format(ORDER_TOTAL_COST, 10_352) + FIELD_DIVIDER
        + String.format(ORDER_TIMEOUT, 25) + FIELD_DIVIDER
        + String.format(ORDER_ETA, 1234567890) + FIELD_DIVIDER
        + String.format(ORDER_CONFIRM_TIME, 9876543210L) + FIELD_DIVIDER
        + String.format(ORDER_START_TIME, 9876598760L) + FIELD_DIVIDER
        + String.format(ORDER_SCHEDULED_START_TIME, 128937981273L) + FIELD_DIVIDER
        + ORDER_DISTANCE_START
        + String.format(ORDER_DISTANCE_EXECUTOR_ID, 5) + FIELD_DIVIDER
        + String.format(ORDER_DISTANCE_VALUE, 546)
        + ORDER_DISTANCE_END + FIELD_DIVIDER
        + ORDER_SERVICE_START
        + String.format(ORDER_SERVICE_ID, 4) + FIELD_DIVIDER
        + String.format(ORDER_SERVICE_NAME, "service") + FIELD_DIVIDER
        + String.format(ORDER_SERVICE_PRICE, 678)
        + ORDER_SERVICE_END + FIELD_DIVIDER
        + ORDER_ROUTE_START
        + OBJECT_START
        + String.format(ROUTE_ID, 7) + FIELD_DIVIDER
        + String.format(ROUTE_LATITUDE, "12.34") + FIELD_DIVIDER
        + String.format(ROUTE_LONGITUDE, "34.12") + FIELD_DIVIDER
        + String.format(ROUTE_COMMENT, "comment 1") + FIELD_DIVIDER
        + String.format(ROUTE_ADDRESS, "address 1") + FIELD_DIVIDER
        + ROUTE_UNCHECKED
        + OBJECT_END + FIELD_DIVIDER
        + OBJECT_START
        + String.format(ROUTE_ID, 8) + FIELD_DIVIDER
        + String.format(ROUTE_LATITUDE, "56.78") + FIELD_DIVIDER
        + String.format(ROUTE_LONGITUDE, "78.56") + FIELD_DIVIDER
        + String.format(ROUTE_COMMENT, "comment 2") + FIELD_DIVIDER
        + String.format(ROUTE_ADDRESS, "address 2") + FIELD_DIVIDER
        + ROUTE_CHECKED
        + OBJECT_END + FIELD_DIVIDER
        + OBJECT_START
        + String.format(ROUTE_ID, 9) + FIELD_DIVIDER
        + String.format(ROUTE_LATITUDE, "90.12") + FIELD_DIVIDER
        + String.format(ROUTE_LONGITUDE, "12.90") + FIELD_DIVIDER
        + String.format(ROUTE_COMMENT, "comment 3") + FIELD_DIVIDER
        + String.format(ROUTE_ADDRESS, "address 3") + FIELD_DIVIDER
        + ROUTE_UNCHECKED
        + OBJECT_END
        + ORDER_ROUTE_END + FIELD_DIVIDER
        + ORDER_OPTIONS_START
        + OBJECT_START
        + String.format(OPTION_ID, 56) + FIELD_DIVIDER
        + String.format(OPTION_NAME, "Грузчики") + FIELD_DIVIDER
        + String.format(OPTION_NUMERIC_VALUE, 2) + FIELD_DIVIDER
        + OPTION_NUMERIC + FIELD_DIVIDER
        + OPTION_DYNAMIC + FIELD_DIVIDER
        + String.format(OPTION_MIN, 0) + FIELD_DIVIDER
        + String.format(OPTION_MAX, 2)
        + OBJECT_END + FIELD_DIVIDER
        + OBJECT_START
        + String.format(OPTION_ID, 55) + FIELD_DIVIDER
        + String.format(OPTION_NAME, "Ремни крепления") + FIELD_DIVIDER
        + OPTION_TRUE_VALUE + FIELD_DIVIDER
        + OPTION_BOOLEAN + FIELD_DIVIDER
        + OPTION_DYNAMIC + FIELD_DIVIDER
        + String.format(OPTION_DESCRIPTION, "Имеются стяжные ремни для для фиксации груза.")
        + OBJECT_END + FIELD_DIVIDER
        + OBJECT_START
        + String.format(OPTION_ID, 6) + FIELD_DIVIDER
        + String.format(OPTION_NAME, "Безналичная оплата") + FIELD_DIVIDER
        + OPTION_FALSE_VALUE + FIELD_DIVIDER
        + OPTION_BOOLEAN + FIELD_DIVIDER
        + OPTION_STATIC
        + OBJECT_END + FIELD_DIVIDER
        + OBJECT_START
        + String.format(OPTION_ID, 57) + FIELD_DIVIDER
        + String.format(OPTION_NAME, "Гидроборт") + FIELD_DIVIDER
        + String.format(OPTION_NUMERIC_VALUE, 1500) + FIELD_DIVIDER
        + OPTION_NUMERIC + FIELD_DIVIDER
        + OPTION_STATIC + FIELD_DIVIDER
        + String.format(OPTION_DESCRIPTION, "Поднимающая штуковина")
        + OBJECT_END
        + ORDER_OPTIONS_END
        + OBJECT_END;
  }

  @NonNull
  String getOrderWithoutEstimatedRouteDistance() {
    return OBJECT_START
        + String.format(ORDER_ID, 7) + FIELD_DIVIDER
        + String.format(PAYMENT_TYPE, "CASH") + FIELD_DIVIDER
        + String.format(ORDER_COMMENT, "some comment") + FIELD_DIVIDER
        + String.format(ORDER_ESTIMATED_AMOUNT_TEXT, "over 9999 BTC") + FIELD_DIVIDER
        + String.format(ORDER_ESTIMATED_AMOUNT, 9999) + FIELD_DIVIDER
        + String.format(ORDER_ESTIMATED_TIME, 234_532_000) + FIELD_DIVIDER
        + String.format(ORDER_TOTAL_COST, 10_352) + FIELD_DIVIDER
        + String.format(ORDER_TIMEOUT, 25) + FIELD_DIVIDER
        + String.format(ORDER_ETA, 1234567890) + FIELD_DIVIDER
        + String.format(ORDER_CONFIRM_TIME, 9876543210L) + FIELD_DIVIDER
        + String.format(ORDER_START_TIME, 9876598760L) + FIELD_DIVIDER
        + String.format(ORDER_SCHEDULED_START_TIME, 128937981273L) + FIELD_DIVIDER
        + ORDER_DISTANCE_START
        + String.format(ORDER_DISTANCE_EXECUTOR_ID, 5) + FIELD_DIVIDER
        + String.format(ORDER_DISTANCE_VALUE, 546)
        + ORDER_DISTANCE_END + FIELD_DIVIDER
        + ORDER_SERVICE_START
        + String.format(ORDER_SERVICE_ID, 4) + FIELD_DIVIDER
        + String.format(ORDER_SERVICE_NAME, "service") + FIELD_DIVIDER
        + String.format(ORDER_SERVICE_PRICE, 678)
        + ORDER_SERVICE_END + FIELD_DIVIDER
        + ORDER_ROUTE_START
        + OBJECT_START
        + String.format(ROUTE_ID, 7) + FIELD_DIVIDER
        + String.format(ROUTE_LATITUDE, "12.34") + FIELD_DIVIDER
        + String.format(ROUTE_LONGITUDE, "34.12") + FIELD_DIVIDER
        + String.format(ROUTE_COMMENT, "comment 1") + FIELD_DIVIDER
        + String.format(ROUTE_ADDRESS, "address 1") + FIELD_DIVIDER
        + ROUTE_UNCHECKED
        + OBJECT_END + FIELD_DIVIDER
        + OBJECT_START
        + String.format(ROUTE_ID, 8) + FIELD_DIVIDER
        + String.format(ROUTE_LATITUDE, "56.78") + FIELD_DIVIDER
        + String.format(ROUTE_LONGITUDE, "78.56") + FIELD_DIVIDER
        + String.format(ROUTE_COMMENT, "comment 2") + FIELD_DIVIDER
        + String.format(ROUTE_ADDRESS, "address 2") + FIELD_DIVIDER
        + ROUTE_CHECKED
        + OBJECT_END + FIELD_DIVIDER
        + OBJECT_START
        + String.format(ROUTE_ID, 9) + FIELD_DIVIDER
        + String.format(ROUTE_LATITUDE, "90.12") + FIELD_DIVIDER
        + String.format(ROUTE_LONGITUDE, "12.90") + FIELD_DIVIDER
        + String.format(ROUTE_COMMENT, "comment 3") + FIELD_DIVIDER
        + String.format(ROUTE_ADDRESS, "address 3") + FIELD_DIVIDER
        + ROUTE_UNCHECKED
        + OBJECT_END
        + ORDER_ROUTE_END + FIELD_DIVIDER
        + ORDER_OPTIONS_START
        + OBJECT_START
        + String.format(OPTION_ID, 56) + FIELD_DIVIDER
        + String.format(OPTION_NAME, "Грузчики") + FIELD_DIVIDER
        + String.format(OPTION_NUMERIC_VALUE, 2) + FIELD_DIVIDER
        + OPTION_NUMERIC + FIELD_DIVIDER
        + OPTION_DYNAMIC + FIELD_DIVIDER
        + String.format(OPTION_MIN, 0) + FIELD_DIVIDER
        + String.format(OPTION_MAX, 2)
        + OBJECT_END + FIELD_DIVIDER
        + OBJECT_START
        + String.format(OPTION_ID, 55) + FIELD_DIVIDER
        + String.format(OPTION_NAME, "Ремни крепления") + FIELD_DIVIDER
        + OPTION_TRUE_VALUE + FIELD_DIVIDER
        + OPTION_BOOLEAN + FIELD_DIVIDER
        + OPTION_DYNAMIC + FIELD_DIVIDER
        + String.format(OPTION_DESCRIPTION, "Имеются стяжные ремни для для фиксации груза.")
        + OBJECT_END + FIELD_DIVIDER
        + OBJECT_START
        + String.format(OPTION_ID, 6) + FIELD_DIVIDER
        + String.format(OPTION_NAME, "Безналичная оплата") + FIELD_DIVIDER
        + OPTION_FALSE_VALUE + FIELD_DIVIDER
        + OPTION_BOOLEAN + FIELD_DIVIDER
        + OPTION_STATIC
        + OBJECT_END + FIELD_DIVIDER
        + OBJECT_START
        + String.format(OPTION_ID, 57) + FIELD_DIVIDER
        + String.format(OPTION_NAME, "Гидроборт") + FIELD_DIVIDER
        + String.format(OPTION_NUMERIC_VALUE, 1500) + FIELD_DIVIDER
        + OPTION_NUMERIC + FIELD_DIVIDER
        + OPTION_STATIC + FIELD_DIVIDER
        + String.format(OPTION_DESCRIPTION, "Поднимающая штуковина")
        + OBJECT_END
        + ORDER_OPTIONS_END
        + OBJECT_END;
  }

  @NonNull
  String getOrderWithoutCost() {
    return OBJECT_START
        + String.format(ORDER_ID, 7) + FIELD_DIVIDER
        + String.format(PAYMENT_TYPE, "CASH") + FIELD_DIVIDER
        + String.format(ORDER_COMMENT, "some comment") + FIELD_DIVIDER
        + String.format(ORDER_ESTIMATED_AMOUNT_TEXT, "over 9999 BTC") + FIELD_DIVIDER
        + String.format(ORDER_ESTIMATED_AMOUNT, 9999) + FIELD_DIVIDER
        + String.format(ORDER_ESTIMATED_TIME, 234_532_000) + FIELD_DIVIDER
        + String.format(ORDER_ESTIMATED_ROUTE_DISTANCE, 35_213) + FIELD_DIVIDER
        + String.format(ORDER_TIMEOUT, 25) + FIELD_DIVIDER
        + String.format(ORDER_ETA, 1234567890) + FIELD_DIVIDER
        + String.format(ORDER_CONFIRM_TIME, 9876543210L) + FIELD_DIVIDER
        + String.format(ORDER_START_TIME, 9876598760L) + FIELD_DIVIDER
        + String.format(ORDER_SCHEDULED_START_TIME, 128937981273L) + FIELD_DIVIDER
        + ORDER_DISTANCE_START
        + String.format(ORDER_DISTANCE_EXECUTOR_ID, 5) + FIELD_DIVIDER
        + String.format(ORDER_DISTANCE_VALUE, 546)
        + ORDER_DISTANCE_END + FIELD_DIVIDER
        + ORDER_SERVICE_START
        + String.format(ORDER_SERVICE_ID, 4) + FIELD_DIVIDER
        + String.format(ORDER_SERVICE_NAME, "service") + FIELD_DIVIDER
        + String.format(ORDER_SERVICE_PRICE, 678)
        + ORDER_SERVICE_END + FIELD_DIVIDER
        + ORDER_ROUTE_START
        + OBJECT_START
        + String.format(ROUTE_ID, 7) + FIELD_DIVIDER
        + String.format(ROUTE_LATITUDE, "12.34") + FIELD_DIVIDER
        + String.format(ROUTE_LONGITUDE, "34.12") + FIELD_DIVIDER
        + String.format(ROUTE_COMMENT, "comment 1") + FIELD_DIVIDER
        + String.format(ROUTE_ADDRESS, "address 1") + FIELD_DIVIDER
        + ROUTE_UNCHECKED
        + OBJECT_END + FIELD_DIVIDER
        + OBJECT_START
        + String.format(ROUTE_ID, 8) + FIELD_DIVIDER
        + String.format(ROUTE_LATITUDE, "56.78") + FIELD_DIVIDER
        + String.format(ROUTE_LONGITUDE, "78.56") + FIELD_DIVIDER
        + String.format(ROUTE_COMMENT, "comment 2") + FIELD_DIVIDER
        + String.format(ROUTE_ADDRESS, "address 2") + FIELD_DIVIDER
        + ROUTE_CHECKED
        + OBJECT_END + FIELD_DIVIDER
        + OBJECT_START
        + String.format(ROUTE_ID, 9) + FIELD_DIVIDER
        + String.format(ROUTE_LATITUDE, "90.12") + FIELD_DIVIDER
        + String.format(ROUTE_LONGITUDE, "12.90") + FIELD_DIVIDER
        + String.format(ROUTE_COMMENT, "comment 3") + FIELD_DIVIDER
        + String.format(ROUTE_ADDRESS, "address 3") + FIELD_DIVIDER
        + ROUTE_UNCHECKED
        + OBJECT_END
        + ORDER_ROUTE_END + FIELD_DIVIDER
        + ORDER_OPTIONS_START
        + OBJECT_START
        + String.format(OPTION_ID, 56) + FIELD_DIVIDER
        + String.format(OPTION_NAME, "Грузчики") + FIELD_DIVIDER
        + String.format(OPTION_NUMERIC_VALUE, 2) + FIELD_DIVIDER
        + OPTION_NUMERIC + FIELD_DIVIDER
        + OPTION_DYNAMIC + FIELD_DIVIDER
        + String.format(OPTION_MIN, 0) + FIELD_DIVIDER
        + String.format(OPTION_MAX, 2)
        + OBJECT_END + FIELD_DIVIDER
        + OBJECT_START
        + String.format(OPTION_ID, 55) + FIELD_DIVIDER
        + String.format(OPTION_NAME, "Ремни крепления") + FIELD_DIVIDER
        + OPTION_TRUE_VALUE + FIELD_DIVIDER
        + OPTION_BOOLEAN + FIELD_DIVIDER
        + OPTION_DYNAMIC + FIELD_DIVIDER
        + String.format(OPTION_DESCRIPTION, "Имеются стяжные ремни для для фиксации груза.")
        + OBJECT_END + FIELD_DIVIDER
        + OBJECT_START
        + String.format(OPTION_ID, 6) + FIELD_DIVIDER
        + String.format(OPTION_NAME, "Безналичная оплата") + FIELD_DIVIDER
        + OPTION_FALSE_VALUE + FIELD_DIVIDER
        + OPTION_BOOLEAN + FIELD_DIVIDER
        + OPTION_STATIC
        + OBJECT_END + FIELD_DIVIDER
        + OBJECT_START
        + String.format(OPTION_ID, 57) + FIELD_DIVIDER
        + String.format(OPTION_NAME, "Гидроборт") + FIELD_DIVIDER
        + String.format(OPTION_NUMERIC_VALUE, 1500) + FIELD_DIVIDER
        + OPTION_NUMERIC + FIELD_DIVIDER
        + OPTION_STATIC + FIELD_DIVIDER
        + String.format(OPTION_DESCRIPTION, "Поднимающая штуковина")
        + OBJECT_END
        + ORDER_OPTIONS_END
        + OBJECT_END;
  }

  @NonNull
  String getOrderWithoutTimeout() {
    return OBJECT_START
        + String.format(ORDER_ID, 7) + FIELD_DIVIDER
        + String.format(PAYMENT_TYPE, "CASH") + FIELD_DIVIDER
        + String.format(ORDER_COMMENT, "some comment") + FIELD_DIVIDER
        + String.format(ORDER_ESTIMATED_AMOUNT_TEXT, "over 9999 BTC") + FIELD_DIVIDER
        + String.format(ORDER_ESTIMATED_AMOUNT, 9999) + FIELD_DIVIDER
        + String.format(ORDER_ESTIMATED_TIME, 234_532_000) + FIELD_DIVIDER
        + String.format(ORDER_ESTIMATED_ROUTE_DISTANCE, 35_213) + FIELD_DIVIDER
        + String.format(ORDER_TOTAL_COST, 10_352) + FIELD_DIVIDER
        + String.format(ORDER_ETA, 1234567890) + FIELD_DIVIDER
        + String.format(ORDER_CONFIRM_TIME, 9876543210L) + FIELD_DIVIDER
        + String.format(ORDER_START_TIME, 9876598760L) + FIELD_DIVIDER
        + String.format(ORDER_SCHEDULED_START_TIME, 128937981273L) + FIELD_DIVIDER
        + ORDER_DISTANCE_START
        + String.format(ORDER_DISTANCE_EXECUTOR_ID, 5) + FIELD_DIVIDER
        + String.format(ORDER_DISTANCE_VALUE, 546)
        + ORDER_DISTANCE_END + FIELD_DIVIDER
        + ORDER_SERVICE_START
        + String.format(ORDER_SERVICE_ID, 4) + FIELD_DIVIDER
        + String.format(ORDER_SERVICE_NAME, "service") + FIELD_DIVIDER
        + String.format(ORDER_SERVICE_PRICE, 678)
        + ORDER_SERVICE_END + FIELD_DIVIDER
        + ORDER_ROUTE_START
        + OBJECT_START
        + String.format(ROUTE_ID, 7) + FIELD_DIVIDER
        + String.format(ROUTE_LATITUDE, "12.34") + FIELD_DIVIDER
        + String.format(ROUTE_LONGITUDE, "34.12") + FIELD_DIVIDER
        + String.format(ROUTE_COMMENT, "comment 1") + FIELD_DIVIDER
        + String.format(ROUTE_ADDRESS, "address 1") + FIELD_DIVIDER
        + ROUTE_UNCHECKED
        + OBJECT_END + FIELD_DIVIDER
        + OBJECT_START
        + String.format(ROUTE_ID, 8) + FIELD_DIVIDER
        + String.format(ROUTE_LATITUDE, "56.78") + FIELD_DIVIDER
        + String.format(ROUTE_LONGITUDE, "78.56") + FIELD_DIVIDER
        + String.format(ROUTE_COMMENT, "comment 2") + FIELD_DIVIDER
        + String.format(ROUTE_ADDRESS, "address 2") + FIELD_DIVIDER
        + ROUTE_CHECKED
        + OBJECT_END + FIELD_DIVIDER
        + OBJECT_START
        + String.format(ROUTE_ID, 9) + FIELD_DIVIDER
        + String.format(ROUTE_LATITUDE, "90.12") + FIELD_DIVIDER
        + String.format(ROUTE_LONGITUDE, "12.90") + FIELD_DIVIDER
        + String.format(ROUTE_COMMENT, "comment 3") + FIELD_DIVIDER
        + String.format(ROUTE_ADDRESS, "address 3") + FIELD_DIVIDER
        + ROUTE_UNCHECKED
        + OBJECT_END
        + ORDER_ROUTE_END + FIELD_DIVIDER
        + ORDER_OPTIONS_START
        + OBJECT_START
        + String.format(OPTION_ID, 56) + FIELD_DIVIDER
        + String.format(OPTION_NAME, "Грузчики") + FIELD_DIVIDER
        + String.format(OPTION_NUMERIC_VALUE, 2) + FIELD_DIVIDER
        + OPTION_NUMERIC + FIELD_DIVIDER
        + OPTION_DYNAMIC + FIELD_DIVIDER
        + String.format(OPTION_MIN, 0) + FIELD_DIVIDER
        + String.format(OPTION_MAX, 2)
        + OBJECT_END + FIELD_DIVIDER
        + OBJECT_START
        + String.format(OPTION_ID, 55) + FIELD_DIVIDER
        + String.format(OPTION_NAME, "Ремни крепления") + FIELD_DIVIDER
        + OPTION_TRUE_VALUE + FIELD_DIVIDER
        + OPTION_BOOLEAN + FIELD_DIVIDER
        + OPTION_DYNAMIC + FIELD_DIVIDER
        + String.format(OPTION_DESCRIPTION, "Имеются стяжные ремни для для фиксации груза.")
        + OBJECT_END + FIELD_DIVIDER
        + OBJECT_START
        + String.format(OPTION_ID, 6) + FIELD_DIVIDER
        + String.format(OPTION_NAME, "Безналичная оплата") + FIELD_DIVIDER
        + OPTION_FALSE_VALUE + FIELD_DIVIDER
        + OPTION_BOOLEAN + FIELD_DIVIDER
        + OPTION_STATIC
        + OBJECT_END + FIELD_DIVIDER
        + OBJECT_START
        + String.format(OPTION_ID, 57) + FIELD_DIVIDER
        + String.format(OPTION_NAME, "Гидроборт") + FIELD_DIVIDER
        + String.format(OPTION_NUMERIC_VALUE, 1500) + FIELD_DIVIDER
        + OPTION_NUMERIC + FIELD_DIVIDER
        + OPTION_STATIC + FIELD_DIVIDER
        + String.format(OPTION_DESCRIPTION, "Поднимающая штуковина")
        + OBJECT_END
        + ORDER_OPTIONS_END
        + OBJECT_END;
  }

  @NonNull
  String getOrderWithoutEta() {
    return OBJECT_START
        + String.format(ORDER_ID, 7) + FIELD_DIVIDER
        + String.format(PAYMENT_TYPE, "CASH") + FIELD_DIVIDER
        + String.format(ORDER_COMMENT, "some comment") + FIELD_DIVIDER
        + String.format(ORDER_ESTIMATED_AMOUNT_TEXT, "over 9999 BTC") + FIELD_DIVIDER
        + String.format(ORDER_ESTIMATED_AMOUNT, 9999) + FIELD_DIVIDER
        + String.format(ORDER_ESTIMATED_TIME, 234_532_000) + FIELD_DIVIDER
        + String.format(ORDER_ESTIMATED_ROUTE_DISTANCE, 35_213) + FIELD_DIVIDER
        + String.format(ORDER_TOTAL_COST, 10_352) + FIELD_DIVIDER
        + String.format(ORDER_TIMEOUT, 25) + FIELD_DIVIDER
        + String.format(ORDER_CONFIRM_TIME, 9876543210L) + FIELD_DIVIDER
        + String.format(ORDER_START_TIME, 9876598760L) + FIELD_DIVIDER
        + String.format(ORDER_SCHEDULED_START_TIME, 128937981273L) + FIELD_DIVIDER
        + ORDER_DISTANCE_START
        + String.format(ORDER_DISTANCE_EXECUTOR_ID, 5) + FIELD_DIVIDER
        + String.format(ORDER_DISTANCE_VALUE, 546)
        + ORDER_DISTANCE_END + FIELD_DIVIDER
        + ORDER_SERVICE_START
        + String.format(ORDER_SERVICE_ID, 4) + FIELD_DIVIDER
        + String.format(ORDER_SERVICE_NAME, "service") + FIELD_DIVIDER
        + String.format(ORDER_SERVICE_PRICE, 678)
        + ORDER_SERVICE_END + FIELD_DIVIDER
        + ORDER_ROUTE_START
        + OBJECT_START
        + String.format(ROUTE_ID, 7) + FIELD_DIVIDER
        + String.format(ROUTE_LATITUDE, "12.34") + FIELD_DIVIDER
        + String.format(ROUTE_LONGITUDE, "34.12") + FIELD_DIVIDER
        + String.format(ROUTE_COMMENT, "comment 1") + FIELD_DIVIDER
        + String.format(ROUTE_ADDRESS, "address 1") + FIELD_DIVIDER
        + ROUTE_UNCHECKED
        + OBJECT_END + FIELD_DIVIDER
        + OBJECT_START
        + String.format(ROUTE_ID, 8) + FIELD_DIVIDER
        + String.format(ROUTE_LATITUDE, "56.78") + FIELD_DIVIDER
        + String.format(ROUTE_LONGITUDE, "78.56") + FIELD_DIVIDER
        + String.format(ROUTE_COMMENT, "comment 2") + FIELD_DIVIDER
        + String.format(ROUTE_ADDRESS, "address 2") + FIELD_DIVIDER
        + ROUTE_CHECKED
        + OBJECT_END + FIELD_DIVIDER
        + OBJECT_START
        + String.format(ROUTE_ID, 9) + FIELD_DIVIDER
        + String.format(ROUTE_LATITUDE, "90.12") + FIELD_DIVIDER
        + String.format(ROUTE_LONGITUDE, "12.90") + FIELD_DIVIDER
        + String.format(ROUTE_COMMENT, "comment 3") + FIELD_DIVIDER
        + String.format(ROUTE_ADDRESS, "address 3") + FIELD_DIVIDER
        + ROUTE_UNCHECKED
        + OBJECT_END
        + ORDER_ROUTE_END + FIELD_DIVIDER
        + ORDER_OPTIONS_START
        + OBJECT_START
        + String.format(OPTION_ID, 56) + FIELD_DIVIDER
        + String.format(OPTION_NAME, "Грузчики") + FIELD_DIVIDER
        + String.format(OPTION_NUMERIC_VALUE, 2) + FIELD_DIVIDER
        + OPTION_NUMERIC + FIELD_DIVIDER
        + OPTION_DYNAMIC + FIELD_DIVIDER
        + String.format(OPTION_MIN, 0) + FIELD_DIVIDER
        + String.format(OPTION_MAX, 2)
        + OBJECT_END + FIELD_DIVIDER
        + OBJECT_START
        + String.format(OPTION_ID, 55) + FIELD_DIVIDER
        + String.format(OPTION_NAME, "Ремни крепления") + FIELD_DIVIDER
        + OPTION_TRUE_VALUE + FIELD_DIVIDER
        + OPTION_BOOLEAN + FIELD_DIVIDER
        + OPTION_DYNAMIC + FIELD_DIVIDER
        + String.format(OPTION_DESCRIPTION, "Имеются стяжные ремни для для фиксации груза.")
        + OBJECT_END + FIELD_DIVIDER
        + OBJECT_START
        + String.format(OPTION_ID, 6) + FIELD_DIVIDER
        + String.format(OPTION_NAME, "Безналичная оплата") + FIELD_DIVIDER
        + OPTION_FALSE_VALUE + FIELD_DIVIDER
        + OPTION_BOOLEAN + FIELD_DIVIDER
        + OPTION_STATIC
        + OBJECT_END + FIELD_DIVIDER
        + OBJECT_START
        + String.format(OPTION_ID, 57) + FIELD_DIVIDER
        + String.format(OPTION_NAME, "Гидроборт") + FIELD_DIVIDER
        + String.format(OPTION_NUMERIC_VALUE, 1500) + FIELD_DIVIDER
        + OPTION_NUMERIC + FIELD_DIVIDER
        + OPTION_STATIC + FIELD_DIVIDER
        + String.format(OPTION_DESCRIPTION, "Поднимающая штуковина")
        + OBJECT_END
        + ORDER_OPTIONS_END
        + OBJECT_END;
  }

  @NonNull
  String getOrderWithoutConfirmationTime() {
    return OBJECT_START
        + String.format(ORDER_ID, 7) + FIELD_DIVIDER
        + String.format(PAYMENT_TYPE, "CASH") + FIELD_DIVIDER
        + String.format(ORDER_COMMENT, "some comment") + FIELD_DIVIDER
        + String.format(ORDER_ESTIMATED_AMOUNT_TEXT, "over 9999 BTC") + FIELD_DIVIDER
        + String.format(ORDER_ESTIMATED_AMOUNT, 9999) + FIELD_DIVIDER
        + String.format(ORDER_ESTIMATED_TIME, 234_532_000) + FIELD_DIVIDER
        + String.format(ORDER_ESTIMATED_ROUTE_DISTANCE, 35_213) + FIELD_DIVIDER
        + String.format(ORDER_TOTAL_COST, 10_352) + FIELD_DIVIDER
        + String.format(ORDER_TIMEOUT, 25) + FIELD_DIVIDER
        + String.format(ORDER_ETA, 1234567890) + FIELD_DIVIDER
        + String.format(ORDER_START_TIME, 9876598760L) + FIELD_DIVIDER
        + String.format(ORDER_SCHEDULED_START_TIME, 128937981273L) + FIELD_DIVIDER
        + ORDER_DISTANCE_START
        + String.format(ORDER_DISTANCE_EXECUTOR_ID, 5) + FIELD_DIVIDER
        + String.format(ORDER_DISTANCE_VALUE, 546)
        + ORDER_DISTANCE_END + FIELD_DIVIDER
        + ORDER_SERVICE_START
        + String.format(ORDER_SERVICE_ID, 4) + FIELD_DIVIDER
        + String.format(ORDER_SERVICE_NAME, "service") + FIELD_DIVIDER
        + String.format(ORDER_SERVICE_PRICE, 678)
        + ORDER_SERVICE_END + FIELD_DIVIDER
        + ORDER_ROUTE_START
        + OBJECT_START
        + String.format(ROUTE_ID, 7) + FIELD_DIVIDER
        + String.format(ROUTE_LATITUDE, "12.34") + FIELD_DIVIDER
        + String.format(ROUTE_LONGITUDE, "34.12") + FIELD_DIVIDER
        + String.format(ROUTE_COMMENT, "comment 1") + FIELD_DIVIDER
        + String.format(ROUTE_ADDRESS, "address 1") + FIELD_DIVIDER
        + ROUTE_UNCHECKED
        + OBJECT_END + FIELD_DIVIDER
        + OBJECT_START
        + String.format(ROUTE_ID, 8) + FIELD_DIVIDER
        + String.format(ROUTE_LATITUDE, "56.78") + FIELD_DIVIDER
        + String.format(ROUTE_LONGITUDE, "78.56") + FIELD_DIVIDER
        + String.format(ROUTE_COMMENT, "comment 2") + FIELD_DIVIDER
        + String.format(ROUTE_ADDRESS, "address 2") + FIELD_DIVIDER
        + ROUTE_CHECKED
        + OBJECT_END + FIELD_DIVIDER
        + OBJECT_START
        + String.format(ROUTE_ID, 9) + FIELD_DIVIDER
        + String.format(ROUTE_LATITUDE, "90.12") + FIELD_DIVIDER
        + String.format(ROUTE_LONGITUDE, "12.90") + FIELD_DIVIDER
        + String.format(ROUTE_COMMENT, "comment 3") + FIELD_DIVIDER
        + String.format(ROUTE_ADDRESS, "address 3") + FIELD_DIVIDER
        + ROUTE_UNCHECKED
        + OBJECT_END
        + ORDER_ROUTE_END + FIELD_DIVIDER
        + ORDER_OPTIONS_START
        + OBJECT_START
        + String.format(OPTION_ID, 56) + FIELD_DIVIDER
        + String.format(OPTION_NAME, "Грузчики") + FIELD_DIVIDER
        + String.format(OPTION_NUMERIC_VALUE, 2) + FIELD_DIVIDER
        + OPTION_NUMERIC + FIELD_DIVIDER
        + OPTION_DYNAMIC + FIELD_DIVIDER
        + String.format(OPTION_MIN, 0) + FIELD_DIVIDER
        + String.format(OPTION_MAX, 2)
        + OBJECT_END + FIELD_DIVIDER
        + OBJECT_START
        + String.format(OPTION_ID, 55) + FIELD_DIVIDER
        + String.format(OPTION_NAME, "Ремни крепления") + FIELD_DIVIDER
        + OPTION_TRUE_VALUE + FIELD_DIVIDER
        + OPTION_BOOLEAN + FIELD_DIVIDER
        + OPTION_DYNAMIC + FIELD_DIVIDER
        + String.format(OPTION_DESCRIPTION, "Имеются стяжные ремни для для фиксации груза.")
        + OBJECT_END + FIELD_DIVIDER
        + OBJECT_START
        + String.format(OPTION_ID, 6) + FIELD_DIVIDER
        + String.format(OPTION_NAME, "Безналичная оплата") + FIELD_DIVIDER
        + OPTION_FALSE_VALUE + FIELD_DIVIDER
        + OPTION_BOOLEAN + FIELD_DIVIDER
        + OPTION_STATIC
        + OBJECT_END + FIELD_DIVIDER
        + OBJECT_START
        + String.format(OPTION_ID, 57) + FIELD_DIVIDER
        + String.format(OPTION_NAME, "Гидроборт") + FIELD_DIVIDER
        + String.format(OPTION_NUMERIC_VALUE, 1500) + FIELD_DIVIDER
        + OPTION_NUMERIC + FIELD_DIVIDER
        + OPTION_STATIC + FIELD_DIVIDER
        + String.format(OPTION_DESCRIPTION, "Поднимающая штуковина")
        + OBJECT_END
        + ORDER_OPTIONS_END
        + OBJECT_END;
  }

  @NonNull
  String getOrderWithoutStartTime() {
    return OBJECT_START
        + String.format(ORDER_ID, 7) + FIELD_DIVIDER
        + String.format(PAYMENT_TYPE, "CASH") + FIELD_DIVIDER
        + String.format(ORDER_COMMENT, "some comment") + FIELD_DIVIDER
        + String.format(ORDER_ESTIMATED_AMOUNT_TEXT, "over 9999 BTC") + FIELD_DIVIDER
        + String.format(ORDER_ESTIMATED_AMOUNT, 9999) + FIELD_DIVIDER
        + String.format(ORDER_ESTIMATED_TIME, 234_532_000) + FIELD_DIVIDER
        + String.format(ORDER_ESTIMATED_ROUTE_DISTANCE, 35_213) + FIELD_DIVIDER
        + String.format(ORDER_TOTAL_COST, 10_352) + FIELD_DIVIDER
        + String.format(ORDER_TIMEOUT, 25) + FIELD_DIVIDER
        + String.format(ORDER_ETA, 1234567890) + FIELD_DIVIDER
        + String.format(ORDER_SCHEDULED_START_TIME, 128937981273L) + FIELD_DIVIDER
        + String.format(ORDER_CONFIRM_TIME, 9876543210L) + FIELD_DIVIDER
        + ORDER_DISTANCE_START
        + String.format(ORDER_DISTANCE_EXECUTOR_ID, 5) + FIELD_DIVIDER
        + String.format(ORDER_DISTANCE_VALUE, 546)
        + ORDER_DISTANCE_END + FIELD_DIVIDER
        + ORDER_SERVICE_START
        + String.format(ORDER_SERVICE_ID, 4) + FIELD_DIVIDER
        + String.format(ORDER_SERVICE_NAME, "service") + FIELD_DIVIDER
        + String.format(ORDER_SERVICE_PRICE, 678)
        + ORDER_SERVICE_END + FIELD_DIVIDER
        + ORDER_ROUTE_START
        + OBJECT_START
        + String.format(ROUTE_ID, 7) + FIELD_DIVIDER
        + String.format(ROUTE_LATITUDE, "12.34") + FIELD_DIVIDER
        + String.format(ROUTE_LONGITUDE, "34.12") + FIELD_DIVIDER
        + String.format(ROUTE_COMMENT, "comment 1") + FIELD_DIVIDER
        + String.format(ROUTE_ADDRESS, "address 1") + FIELD_DIVIDER
        + ROUTE_UNCHECKED
        + OBJECT_END + FIELD_DIVIDER
        + OBJECT_START
        + String.format(ROUTE_ID, 8) + FIELD_DIVIDER
        + String.format(ROUTE_LATITUDE, "56.78") + FIELD_DIVIDER
        + String.format(ROUTE_LONGITUDE, "78.56") + FIELD_DIVIDER
        + String.format(ROUTE_COMMENT, "comment 2") + FIELD_DIVIDER
        + String.format(ROUTE_ADDRESS, "address 2") + FIELD_DIVIDER
        + ROUTE_CHECKED
        + OBJECT_END + FIELD_DIVIDER
        + OBJECT_START
        + String.format(ROUTE_ID, 9) + FIELD_DIVIDER
        + String.format(ROUTE_LATITUDE, "90.12") + FIELD_DIVIDER
        + String.format(ROUTE_LONGITUDE, "12.90") + FIELD_DIVIDER
        + String.format(ROUTE_COMMENT, "comment 3") + FIELD_DIVIDER
        + String.format(ROUTE_ADDRESS, "address 3") + FIELD_DIVIDER
        + ROUTE_UNCHECKED
        + OBJECT_END
        + ORDER_ROUTE_END + FIELD_DIVIDER
        + ORDER_OPTIONS_START
        + OBJECT_START
        + String.format(OPTION_ID, 56) + FIELD_DIVIDER
        + String.format(OPTION_NAME, "Грузчики") + FIELD_DIVIDER
        + String.format(OPTION_NUMERIC_VALUE, 2) + FIELD_DIVIDER
        + OPTION_NUMERIC + FIELD_DIVIDER
        + OPTION_DYNAMIC + FIELD_DIVIDER
        + String.format(OPTION_MIN, 0) + FIELD_DIVIDER
        + String.format(OPTION_MAX, 2)
        + OBJECT_END + FIELD_DIVIDER
        + OBJECT_START
        + String.format(OPTION_ID, 55) + FIELD_DIVIDER
        + String.format(OPTION_NAME, "Ремни крепления") + FIELD_DIVIDER
        + OPTION_TRUE_VALUE + FIELD_DIVIDER
        + OPTION_BOOLEAN + FIELD_DIVIDER
        + OPTION_DYNAMIC + FIELD_DIVIDER
        + String.format(OPTION_DESCRIPTION, "Имеются стяжные ремни для для фиксации груза.")
        + OBJECT_END + FIELD_DIVIDER
        + OBJECT_START
        + String.format(OPTION_ID, 6) + FIELD_DIVIDER
        + String.format(OPTION_NAME, "Безналичная оплата") + FIELD_DIVIDER
        + OPTION_FALSE_VALUE + FIELD_DIVIDER
        + OPTION_BOOLEAN + FIELD_DIVIDER
        + OPTION_STATIC
        + OBJECT_END + FIELD_DIVIDER
        + OBJECT_START
        + String.format(OPTION_ID, 57) + FIELD_DIVIDER
        + String.format(OPTION_NAME, "Гидроборт") + FIELD_DIVIDER
        + String.format(OPTION_NUMERIC_VALUE, 1500) + FIELD_DIVIDER
        + OPTION_NUMERIC + FIELD_DIVIDER
        + OPTION_STATIC + FIELD_DIVIDER
        + String.format(OPTION_DESCRIPTION, "Поднимающая штуковина")
        + OBJECT_END
        + ORDER_OPTIONS_END
        + OBJECT_END;
  }

  @NonNull
  String getOrderWithoutScheduledStartTime() {
    return OBJECT_START
        + String.format(ORDER_ID, 7) + FIELD_DIVIDER
        + String.format(PAYMENT_TYPE, "CASH") + FIELD_DIVIDER
        + String.format(ORDER_COMMENT, "some comment") + FIELD_DIVIDER
        + String.format(ORDER_ESTIMATED_AMOUNT_TEXT, "over 9999 BTC") + FIELD_DIVIDER
        + String.format(ORDER_ESTIMATED_AMOUNT, 9999) + FIELD_DIVIDER
        + String.format(ORDER_ESTIMATED_TIME, 234_532_000) + FIELD_DIVIDER
        + String.format(ORDER_ESTIMATED_ROUTE_DISTANCE, 35_213) + FIELD_DIVIDER
        + String.format(ORDER_TOTAL_COST, 10_352) + FIELD_DIVIDER
        + String.format(ORDER_TIMEOUT, 25) + FIELD_DIVIDER
        + String.format(ORDER_ETA, 1234567890) + FIELD_DIVIDER
        + String.format(ORDER_START_TIME, 9876598760L) + FIELD_DIVIDER
        + String.format(ORDER_CONFIRM_TIME, 9876543210L) + FIELD_DIVIDER
        + ORDER_DISTANCE_START
        + String.format(ORDER_DISTANCE_EXECUTOR_ID, 5) + FIELD_DIVIDER
        + String.format(ORDER_DISTANCE_VALUE, 546)
        + ORDER_DISTANCE_END + FIELD_DIVIDER
        + ORDER_SERVICE_START
        + String.format(ORDER_SERVICE_ID, 4) + FIELD_DIVIDER
        + String.format(ORDER_SERVICE_NAME, "service") + FIELD_DIVIDER
        + String.format(ORDER_SERVICE_PRICE, 678)
        + ORDER_SERVICE_END + FIELD_DIVIDER
        + ORDER_ROUTE_START
        + OBJECT_START
        + String.format(ROUTE_ID, 7) + FIELD_DIVIDER
        + String.format(ROUTE_LATITUDE, "12.34") + FIELD_DIVIDER
        + String.format(ROUTE_LONGITUDE, "34.12") + FIELD_DIVIDER
        + String.format(ROUTE_COMMENT, "comment 1") + FIELD_DIVIDER
        + String.format(ROUTE_ADDRESS, "address 1") + FIELD_DIVIDER
        + ROUTE_UNCHECKED
        + OBJECT_END + FIELD_DIVIDER
        + OBJECT_START
        + String.format(ROUTE_ID, 8) + FIELD_DIVIDER
        + String.format(ROUTE_LATITUDE, "56.78") + FIELD_DIVIDER
        + String.format(ROUTE_LONGITUDE, "78.56") + FIELD_DIVIDER
        + String.format(ROUTE_COMMENT, "comment 2") + FIELD_DIVIDER
        + String.format(ROUTE_ADDRESS, "address 2") + FIELD_DIVIDER
        + ROUTE_CHECKED
        + OBJECT_END + FIELD_DIVIDER
        + OBJECT_START
        + String.format(ROUTE_ID, 9) + FIELD_DIVIDER
        + String.format(ROUTE_LATITUDE, "90.12") + FIELD_DIVIDER
        + String.format(ROUTE_LONGITUDE, "12.90") + FIELD_DIVIDER
        + String.format(ROUTE_COMMENT, "comment 3") + FIELD_DIVIDER
        + String.format(ROUTE_ADDRESS, "address 3") + FIELD_DIVIDER
        + ROUTE_UNCHECKED
        + OBJECT_END
        + ORDER_ROUTE_END + FIELD_DIVIDER
        + ORDER_OPTIONS_START
        + OBJECT_START
        + String.format(OPTION_ID, 56) + FIELD_DIVIDER
        + String.format(OPTION_NAME, "Грузчики") + FIELD_DIVIDER
        + String.format(OPTION_NUMERIC_VALUE, 2) + FIELD_DIVIDER
        + OPTION_NUMERIC + FIELD_DIVIDER
        + OPTION_DYNAMIC + FIELD_DIVIDER
        + String.format(OPTION_MIN, 0) + FIELD_DIVIDER
        + String.format(OPTION_MAX, 2)
        + OBJECT_END + FIELD_DIVIDER
        + OBJECT_START
        + String.format(OPTION_ID, 55) + FIELD_DIVIDER
        + String.format(OPTION_NAME, "Ремни крепления") + FIELD_DIVIDER
        + OPTION_TRUE_VALUE + FIELD_DIVIDER
        + OPTION_BOOLEAN + FIELD_DIVIDER
        + OPTION_DYNAMIC + FIELD_DIVIDER
        + String.format(OPTION_DESCRIPTION, "Имеются стяжные ремни для для фиксации груза.")
        + OBJECT_END + FIELD_DIVIDER
        + OBJECT_START
        + String.format(OPTION_ID, 6) + FIELD_DIVIDER
        + String.format(OPTION_NAME, "Безналичная оплата") + FIELD_DIVIDER
        + OPTION_FALSE_VALUE + FIELD_DIVIDER
        + OPTION_BOOLEAN + FIELD_DIVIDER
        + OPTION_STATIC
        + OBJECT_END + FIELD_DIVIDER
        + OBJECT_START
        + String.format(OPTION_ID, 57) + FIELD_DIVIDER
        + String.format(OPTION_NAME, "Гидроборт") + FIELD_DIVIDER
        + String.format(OPTION_NUMERIC_VALUE, 1500) + FIELD_DIVIDER
        + OPTION_NUMERIC + FIELD_DIVIDER
        + OPTION_STATIC + FIELD_DIVIDER
        + String.format(OPTION_DESCRIPTION, "Поднимающая штуковина")
        + OBJECT_END
        + ORDER_OPTIONS_END
        + OBJECT_END;
  }

  @NonNull
  String getOrderWithoutDistanceId() {
    return OBJECT_START
        + String.format(ORDER_ID, 7) + FIELD_DIVIDER
        + String.format(PAYMENT_TYPE, "CASH") + FIELD_DIVIDER
        + String.format(ORDER_COMMENT, "some comment") + FIELD_DIVIDER
        + String.format(ORDER_ESTIMATED_AMOUNT_TEXT, "over 9999 BTC") + FIELD_DIVIDER
        + String.format(ORDER_ESTIMATED_AMOUNT, 9999) + FIELD_DIVIDER
        + String.format(ORDER_ESTIMATED_TIME, 234_532_000) + FIELD_DIVIDER
        + String.format(ORDER_ESTIMATED_ROUTE_DISTANCE, 35_213) + FIELD_DIVIDER
        + String.format(ORDER_TOTAL_COST, 10_352) + FIELD_DIVIDER
        + String.format(ORDER_TIMEOUT, 25) + FIELD_DIVIDER
        + String.format(ORDER_ETA, 1234567890) + FIELD_DIVIDER
        + String.format(ORDER_CONFIRM_TIME, 9876543210L) + FIELD_DIVIDER
        + String.format(ORDER_START_TIME, 9876598760L) + FIELD_DIVIDER
        + String.format(ORDER_SCHEDULED_START_TIME, 128937981273L) + FIELD_DIVIDER
        + ORDER_DISTANCE_START
        + String.format(ORDER_DISTANCE_VALUE, 546)
        + ORDER_DISTANCE_END + FIELD_DIVIDER
        + ORDER_SERVICE_START
        + String.format(ORDER_SERVICE_ID, 4) + FIELD_DIVIDER
        + String.format(ORDER_SERVICE_NAME, "service") + FIELD_DIVIDER
        + String.format(ORDER_SERVICE_PRICE, 678)
        + ORDER_SERVICE_END + FIELD_DIVIDER
        + ORDER_ROUTE_START
        + OBJECT_START
        + String.format(ROUTE_ID, 7) + FIELD_DIVIDER
        + String.format(ROUTE_LATITUDE, "12.34") + FIELD_DIVIDER
        + String.format(ROUTE_LONGITUDE, "34.12") + FIELD_DIVIDER
        + String.format(ROUTE_COMMENT, "comment 1") + FIELD_DIVIDER
        + String.format(ROUTE_ADDRESS, "address 1") + FIELD_DIVIDER
        + ROUTE_UNCHECKED
        + OBJECT_END + FIELD_DIVIDER
        + OBJECT_START
        + String.format(ROUTE_ID, 8) + FIELD_DIVIDER
        + String.format(ROUTE_LATITUDE, "56.78") + FIELD_DIVIDER
        + String.format(ROUTE_LONGITUDE, "78.56") + FIELD_DIVIDER
        + String.format(ROUTE_COMMENT, "comment 2") + FIELD_DIVIDER
        + String.format(ROUTE_ADDRESS, "address 2") + FIELD_DIVIDER
        + ROUTE_CHECKED
        + OBJECT_END + FIELD_DIVIDER
        + OBJECT_START
        + String.format(ROUTE_ID, 9) + FIELD_DIVIDER
        + String.format(ROUTE_LATITUDE, "90.12") + FIELD_DIVIDER
        + String.format(ROUTE_LONGITUDE, "12.90") + FIELD_DIVIDER
        + String.format(ROUTE_COMMENT, "comment 3") + FIELD_DIVIDER
        + String.format(ROUTE_ADDRESS, "address 3") + FIELD_DIVIDER
        + ROUTE_UNCHECKED
        + OBJECT_END
        + ORDER_ROUTE_END + FIELD_DIVIDER
        + ORDER_OPTIONS_START
        + OBJECT_START
        + String.format(OPTION_ID, 56) + FIELD_DIVIDER
        + String.format(OPTION_NAME, "Грузчики") + FIELD_DIVIDER
        + String.format(OPTION_NUMERIC_VALUE, 2) + FIELD_DIVIDER
        + OPTION_NUMERIC + FIELD_DIVIDER
        + OPTION_DYNAMIC + FIELD_DIVIDER
        + String.format(OPTION_MIN, 0) + FIELD_DIVIDER
        + String.format(OPTION_MAX, 2)
        + OBJECT_END + FIELD_DIVIDER
        + OBJECT_START
        + String.format(OPTION_ID, 55) + FIELD_DIVIDER
        + String.format(OPTION_NAME, "Ремни крепления") + FIELD_DIVIDER
        + OPTION_TRUE_VALUE + FIELD_DIVIDER
        + OPTION_BOOLEAN + FIELD_DIVIDER
        + OPTION_DYNAMIC + FIELD_DIVIDER
        + String.format(OPTION_DESCRIPTION, "Имеются стяжные ремни для для фиксации груза.")
        + OBJECT_END + FIELD_DIVIDER
        + OBJECT_START
        + String.format(OPTION_ID, 6) + FIELD_DIVIDER
        + String.format(OPTION_NAME, "Безналичная оплата") + FIELD_DIVIDER
        + OPTION_FALSE_VALUE + FIELD_DIVIDER
        + OPTION_BOOLEAN + FIELD_DIVIDER
        + OPTION_STATIC
        + OBJECT_END + FIELD_DIVIDER
        + OBJECT_START
        + String.format(OPTION_ID, 57) + FIELD_DIVIDER
        + String.format(OPTION_NAME, "Гидроборт") + FIELD_DIVIDER
        + String.format(OPTION_NUMERIC_VALUE, 1500) + FIELD_DIVIDER
        + OPTION_NUMERIC + FIELD_DIVIDER
        + OPTION_STATIC + FIELD_DIVIDER
        + String.format(OPTION_DESCRIPTION, "Поднимающая штуковина")
        + OBJECT_END
        + ORDER_OPTIONS_END
        + OBJECT_END;
  }

  @NonNull
  String getOrderWithoutDistanceValue() {
    return OBJECT_START
        + String.format(ORDER_ID, 7) + FIELD_DIVIDER
        + String.format(PAYMENT_TYPE, "CASH") + FIELD_DIVIDER
        + String.format(ORDER_COMMENT, "some comment") + FIELD_DIVIDER
        + String.format(ORDER_ESTIMATED_AMOUNT_TEXT, "over 9999 BTC") + FIELD_DIVIDER
        + String.format(ORDER_ESTIMATED_AMOUNT, 9999) + FIELD_DIVIDER
        + String.format(ORDER_ESTIMATED_TIME, 234_532_000) + FIELD_DIVIDER
        + String.format(ORDER_ESTIMATED_ROUTE_DISTANCE, 35_213) + FIELD_DIVIDER
        + String.format(ORDER_TOTAL_COST, 10_352) + FIELD_DIVIDER
        + String.format(ORDER_TIMEOUT, 25) + FIELD_DIVIDER
        + String.format(ORDER_ETA, 1234567890) + FIELD_DIVIDER
        + String.format(ORDER_CONFIRM_TIME, 9876543210L) + FIELD_DIVIDER
        + String.format(ORDER_START_TIME, 9876598760L) + FIELD_DIVIDER
        + String.format(ORDER_SCHEDULED_START_TIME, 128937981273L) + FIELD_DIVIDER
        + ORDER_DISTANCE_START
        + String.format(ORDER_DISTANCE_EXECUTOR_ID, 5) + FIELD_DIVIDER
        + ORDER_DISTANCE_END + FIELD_DIVIDER
        + ORDER_SERVICE_START
        + String.format(ORDER_SERVICE_ID, 4) + FIELD_DIVIDER
        + String.format(ORDER_SERVICE_NAME, "service") + FIELD_DIVIDER
        + String.format(ORDER_SERVICE_PRICE, 678)
        + ORDER_SERVICE_END + FIELD_DIVIDER
        + ORDER_ROUTE_START
        + OBJECT_START
        + String.format(ROUTE_ID, 7) + FIELD_DIVIDER
        + String.format(ROUTE_LATITUDE, "12.34") + FIELD_DIVIDER
        + String.format(ROUTE_LONGITUDE, "34.12") + FIELD_DIVIDER
        + String.format(ROUTE_COMMENT, "comment 1") + FIELD_DIVIDER
        + String.format(ROUTE_ADDRESS, "address 1") + FIELD_DIVIDER
        + ROUTE_UNCHECKED
        + OBJECT_END + FIELD_DIVIDER
        + OBJECT_START
        + String.format(ROUTE_ID, 8) + FIELD_DIVIDER
        + String.format(ROUTE_LATITUDE, "56.78") + FIELD_DIVIDER
        + String.format(ROUTE_LONGITUDE, "78.56") + FIELD_DIVIDER
        + String.format(ROUTE_COMMENT, "comment 2") + FIELD_DIVIDER
        + String.format(ROUTE_ADDRESS, "address 2") + FIELD_DIVIDER
        + ROUTE_CHECKED
        + OBJECT_END + FIELD_DIVIDER
        + OBJECT_START
        + String.format(ROUTE_ID, 9) + FIELD_DIVIDER
        + String.format(ROUTE_LATITUDE, "90.12") + FIELD_DIVIDER
        + String.format(ROUTE_LONGITUDE, "12.90") + FIELD_DIVIDER
        + String.format(ROUTE_COMMENT, "comment 3") + FIELD_DIVIDER
        + String.format(ROUTE_ADDRESS, "address 3") + FIELD_DIVIDER
        + ROUTE_UNCHECKED
        + OBJECT_END
        + ORDER_ROUTE_END + FIELD_DIVIDER
        + ORDER_OPTIONS_START
        + OBJECT_START
        + String.format(OPTION_ID, 56) + FIELD_DIVIDER
        + String.format(OPTION_NAME, "Грузчики") + FIELD_DIVIDER
        + String.format(OPTION_NUMERIC_VALUE, 2) + FIELD_DIVIDER
        + OPTION_NUMERIC + FIELD_DIVIDER
        + OPTION_DYNAMIC + FIELD_DIVIDER
        + String.format(OPTION_MIN, 0) + FIELD_DIVIDER
        + String.format(OPTION_MAX, 2)
        + OBJECT_END + FIELD_DIVIDER
        + OBJECT_START
        + String.format(OPTION_ID, 55) + FIELD_DIVIDER
        + String.format(OPTION_NAME, "Ремни крепления") + FIELD_DIVIDER
        + OPTION_TRUE_VALUE + FIELD_DIVIDER
        + OPTION_BOOLEAN + FIELD_DIVIDER
        + OPTION_DYNAMIC + FIELD_DIVIDER
        + String.format(OPTION_DESCRIPTION, "Имеются стяжные ремни для для фиксации груза.")
        + OBJECT_END + FIELD_DIVIDER
        + OBJECT_START
        + String.format(OPTION_ID, 6) + FIELD_DIVIDER
        + String.format(OPTION_NAME, "Безналичная оплата") + FIELD_DIVIDER
        + OPTION_FALSE_VALUE + FIELD_DIVIDER
        + OPTION_BOOLEAN + FIELD_DIVIDER
        + OPTION_STATIC
        + OBJECT_END + FIELD_DIVIDER
        + OBJECT_START
        + String.format(OPTION_ID, 57) + FIELD_DIVIDER
        + String.format(OPTION_NAME, "Гидроборт") + FIELD_DIVIDER
        + String.format(OPTION_NUMERIC_VALUE, 1500) + FIELD_DIVIDER
        + OPTION_NUMERIC + FIELD_DIVIDER
        + OPTION_STATIC + FIELD_DIVIDER
        + String.format(OPTION_DESCRIPTION, "Поднимающая штуковина")
        + OBJECT_END
        + ORDER_OPTIONS_END
        + OBJECT_END;
  }

  @NonNull
  String getOrderWithoutDistance() {
    return OBJECT_START
        + String.format(ORDER_ID, 7) + FIELD_DIVIDER
        + String.format(PAYMENT_TYPE, "CASH") + FIELD_DIVIDER
        + String.format(ORDER_COMMENT, "some comment") + FIELD_DIVIDER
        + String.format(ORDER_ESTIMATED_AMOUNT_TEXT, "over 9999 BTC") + FIELD_DIVIDER
        + String.format(ORDER_ESTIMATED_AMOUNT, 9999) + FIELD_DIVIDER
        + String.format(ORDER_ESTIMATED_TIME, 234_532_000) + FIELD_DIVIDER
        + String.format(ORDER_ESTIMATED_ROUTE_DISTANCE, 35_213) + FIELD_DIVIDER
        + String.format(ORDER_TOTAL_COST, 10_352) + FIELD_DIVIDER
        + String.format(ORDER_TIMEOUT, 25) + FIELD_DIVIDER
        + String.format(ORDER_ETA, 1234567890) + FIELD_DIVIDER
        + String.format(ORDER_CONFIRM_TIME, 9876543210L) + FIELD_DIVIDER
        + String.format(ORDER_START_TIME, 9876598760L) + FIELD_DIVIDER
        + String.format(ORDER_SCHEDULED_START_TIME, 128937981273L) + FIELD_DIVIDER
        + ORDER_SERVICE_START
        + String.format(ORDER_SERVICE_ID, 4) + FIELD_DIVIDER
        + String.format(ORDER_SERVICE_NAME, "service") + FIELD_DIVIDER
        + String.format(ORDER_SERVICE_PRICE, 678)
        + ORDER_SERVICE_END + FIELD_DIVIDER
        + ORDER_ROUTE_START
        + OBJECT_START
        + String.format(ROUTE_ID, 7) + FIELD_DIVIDER
        + String.format(ROUTE_LATITUDE, "12.34") + FIELD_DIVIDER
        + String.format(ROUTE_LONGITUDE, "34.12") + FIELD_DIVIDER
        + String.format(ROUTE_COMMENT, "comment 1") + FIELD_DIVIDER
        + String.format(ROUTE_ADDRESS, "address 1") + FIELD_DIVIDER
        + ROUTE_UNCHECKED
        + OBJECT_END + FIELD_DIVIDER
        + OBJECT_START
        + String.format(ROUTE_ID, 8) + FIELD_DIVIDER
        + String.format(ROUTE_LATITUDE, "56.78") + FIELD_DIVIDER
        + String.format(ROUTE_LONGITUDE, "78.56") + FIELD_DIVIDER
        + String.format(ROUTE_COMMENT, "comment 2") + FIELD_DIVIDER
        + String.format(ROUTE_ADDRESS, "address 2") + FIELD_DIVIDER
        + ROUTE_CHECKED
        + OBJECT_END + FIELD_DIVIDER
        + OBJECT_START
        + String.format(ROUTE_ID, 9) + FIELD_DIVIDER
        + String.format(ROUTE_LATITUDE, "90.12") + FIELD_DIVIDER
        + String.format(ROUTE_LONGITUDE, "12.90") + FIELD_DIVIDER
        + String.format(ROUTE_COMMENT, "comment 3") + FIELD_DIVIDER
        + String.format(ROUTE_ADDRESS, "address 3") + FIELD_DIVIDER
        + ROUTE_UNCHECKED
        + OBJECT_END
        + ORDER_ROUTE_END + FIELD_DIVIDER
        + ORDER_OPTIONS_START
        + OBJECT_START
        + String.format(OPTION_ID, 56) + FIELD_DIVIDER
        + String.format(OPTION_NAME, "Грузчики") + FIELD_DIVIDER
        + String.format(OPTION_NUMERIC_VALUE, 2) + FIELD_DIVIDER
        + OPTION_NUMERIC + FIELD_DIVIDER
        + OPTION_DYNAMIC + FIELD_DIVIDER
        + String.format(OPTION_MIN, 0) + FIELD_DIVIDER
        + String.format(OPTION_MAX, 2)
        + OBJECT_END + FIELD_DIVIDER
        + OBJECT_START
        + String.format(OPTION_ID, 55) + FIELD_DIVIDER
        + String.format(OPTION_NAME, "Ремни крепления") + FIELD_DIVIDER
        + OPTION_TRUE_VALUE + FIELD_DIVIDER
        + OPTION_BOOLEAN + FIELD_DIVIDER
        + OPTION_DYNAMIC + FIELD_DIVIDER
        + String.format(OPTION_DESCRIPTION, "Имеются стяжные ремни для для фиксации груза.")
        + OBJECT_END + FIELD_DIVIDER
        + OBJECT_START
        + String.format(OPTION_ID, 6) + FIELD_DIVIDER
        + String.format(OPTION_NAME, "Безналичная оплата") + FIELD_DIVIDER
        + OPTION_FALSE_VALUE + FIELD_DIVIDER
        + OPTION_BOOLEAN + FIELD_DIVIDER
        + OPTION_STATIC
        + OBJECT_END + FIELD_DIVIDER
        + OBJECT_START
        + String.format(OPTION_ID, 57) + FIELD_DIVIDER
        + String.format(OPTION_NAME, "Гидроборт") + FIELD_DIVIDER
        + String.format(OPTION_NUMERIC_VALUE, 1500) + FIELD_DIVIDER
        + OPTION_NUMERIC + FIELD_DIVIDER
        + OPTION_STATIC + FIELD_DIVIDER
        + String.format(OPTION_DESCRIPTION, "Поднимающая штуковина")
        + OBJECT_END
        + ORDER_OPTIONS_END
        + OBJECT_END;
  }

  @NonNull
  String getOrderWithoutServiceId() {
    return OBJECT_START
        + String.format(ORDER_ID, 7) + FIELD_DIVIDER
        + String.format(PAYMENT_TYPE, "CASH") + FIELD_DIVIDER
        + String.format(ORDER_COMMENT, "some comment") + FIELD_DIVIDER
        + String.format(ORDER_ESTIMATED_AMOUNT_TEXT, "over 9999 BTC") + FIELD_DIVIDER
        + String.format(ORDER_ESTIMATED_AMOUNT, 9999) + FIELD_DIVIDER
        + String.format(ORDER_ESTIMATED_TIME, 234_532_000) + FIELD_DIVIDER
        + String.format(ORDER_ESTIMATED_ROUTE_DISTANCE, 35_213) + FIELD_DIVIDER
        + String.format(ORDER_TOTAL_COST, 10_352) + FIELD_DIVIDER
        + String.format(ORDER_TIMEOUT, 25) + FIELD_DIVIDER
        + String.format(ORDER_ETA, 1234567890) + FIELD_DIVIDER
        + String.format(ORDER_CONFIRM_TIME, 9876543210L) + FIELD_DIVIDER
        + String.format(ORDER_START_TIME, 9876598760L) + FIELD_DIVIDER
        + String.format(ORDER_SCHEDULED_START_TIME, 128937981273L) + FIELD_DIVIDER
        + ORDER_DISTANCE_START
        + String.format(ORDER_DISTANCE_EXECUTOR_ID, 5) + FIELD_DIVIDER
        + String.format(ORDER_DISTANCE_VALUE, 546)
        + ORDER_DISTANCE_END + FIELD_DIVIDER
        + ORDER_SERVICE_START
        + String.format(ORDER_SERVICE_NAME, "service") + FIELD_DIVIDER
        + String.format(ORDER_SERVICE_PRICE, 678)
        + ORDER_SERVICE_END + FIELD_DIVIDER
        + ORDER_ROUTE_START
        + OBJECT_START
        + String.format(ROUTE_ID, 7) + FIELD_DIVIDER
        + String.format(ROUTE_LATITUDE, "12.34") + FIELD_DIVIDER
        + String.format(ROUTE_LONGITUDE, "34.12") + FIELD_DIVIDER
        + String.format(ROUTE_COMMENT, "comment 1") + FIELD_DIVIDER
        + String.format(ROUTE_ADDRESS, "address 1") + FIELD_DIVIDER
        + ROUTE_UNCHECKED
        + OBJECT_END + FIELD_DIVIDER
        + OBJECT_START
        + String.format(ROUTE_ID, 8) + FIELD_DIVIDER
        + String.format(ROUTE_LATITUDE, "56.78") + FIELD_DIVIDER
        + String.format(ROUTE_LONGITUDE, "78.56") + FIELD_DIVIDER
        + String.format(ROUTE_COMMENT, "comment 2") + FIELD_DIVIDER
        + String.format(ROUTE_ADDRESS, "address 2") + FIELD_DIVIDER
        + ROUTE_CHECKED
        + OBJECT_END + FIELD_DIVIDER
        + OBJECT_START
        + String.format(ROUTE_ID, 9) + FIELD_DIVIDER
        + String.format(ROUTE_LATITUDE, "90.12") + FIELD_DIVIDER
        + String.format(ROUTE_LONGITUDE, "12.90") + FIELD_DIVIDER
        + String.format(ROUTE_COMMENT, "comment 3") + FIELD_DIVIDER
        + String.format(ROUTE_ADDRESS, "address 3") + FIELD_DIVIDER
        + ROUTE_UNCHECKED
        + OBJECT_END
        + ORDER_ROUTE_END + FIELD_DIVIDER
        + ORDER_OPTIONS_START
        + OBJECT_START
        + String.format(OPTION_ID, 56) + FIELD_DIVIDER
        + String.format(OPTION_NAME, "Грузчики") + FIELD_DIVIDER
        + String.format(OPTION_NUMERIC_VALUE, 2) + FIELD_DIVIDER
        + OPTION_NUMERIC + FIELD_DIVIDER
        + OPTION_DYNAMIC + FIELD_DIVIDER
        + String.format(OPTION_MIN, 0) + FIELD_DIVIDER
        + String.format(OPTION_MAX, 2)
        + OBJECT_END + FIELD_DIVIDER
        + OBJECT_START
        + String.format(OPTION_ID, 55) + FIELD_DIVIDER
        + String.format(OPTION_NAME, "Ремни крепления") + FIELD_DIVIDER
        + OPTION_TRUE_VALUE + FIELD_DIVIDER
        + OPTION_BOOLEAN + FIELD_DIVIDER
        + OPTION_DYNAMIC + FIELD_DIVIDER
        + String.format(OPTION_DESCRIPTION, "Имеются стяжные ремни для для фиксации груза.")
        + OBJECT_END + FIELD_DIVIDER
        + OBJECT_START
        + String.format(OPTION_ID, 6) + FIELD_DIVIDER
        + String.format(OPTION_NAME, "Безналичная оплата") + FIELD_DIVIDER
        + OPTION_FALSE_VALUE + FIELD_DIVIDER
        + OPTION_BOOLEAN + FIELD_DIVIDER
        + OPTION_STATIC
        + OBJECT_END + FIELD_DIVIDER
        + OBJECT_START
        + String.format(OPTION_ID, 57) + FIELD_DIVIDER
        + String.format(OPTION_NAME, "Гидроборт") + FIELD_DIVIDER
        + String.format(OPTION_NUMERIC_VALUE, 1500) + FIELD_DIVIDER
        + OPTION_NUMERIC + FIELD_DIVIDER
        + OPTION_STATIC + FIELD_DIVIDER
        + String.format(OPTION_DESCRIPTION, "Поднимающая штуковина")
        + OBJECT_END
        + ORDER_OPTIONS_END
        + OBJECT_END;
  }

  @NonNull
  String getOrderWithoutServiceName() {
    return OBJECT_START
        + String.format(ORDER_ID, 7) + FIELD_DIVIDER
        + String.format(PAYMENT_TYPE, "CASH") + FIELD_DIVIDER
        + String.format(ORDER_COMMENT, "some comment") + FIELD_DIVIDER
        + String.format(ORDER_ESTIMATED_AMOUNT_TEXT, "over 9999 BTC") + FIELD_DIVIDER
        + String.format(ORDER_ESTIMATED_AMOUNT, 9999) + FIELD_DIVIDER
        + String.format(ORDER_ESTIMATED_TIME, 234_532_000) + FIELD_DIVIDER
        + String.format(ORDER_ESTIMATED_ROUTE_DISTANCE, 35_213) + FIELD_DIVIDER
        + String.format(ORDER_TOTAL_COST, 10_352) + FIELD_DIVIDER
        + String.format(ORDER_TIMEOUT, 25) + FIELD_DIVIDER
        + String.format(ORDER_ETA, 1234567890) + FIELD_DIVIDER
        + String.format(ORDER_CONFIRM_TIME, 9876543210L) + FIELD_DIVIDER
        + String.format(ORDER_START_TIME, 9876598760L) + FIELD_DIVIDER
        + String.format(ORDER_SCHEDULED_START_TIME, 128937981273L) + FIELD_DIVIDER
        + ORDER_DISTANCE_START
        + String.format(ORDER_DISTANCE_EXECUTOR_ID, 5) + FIELD_DIVIDER
        + String.format(ORDER_DISTANCE_VALUE, 546)
        + ORDER_DISTANCE_END + FIELD_DIVIDER
        + ORDER_SERVICE_START
        + String.format(ORDER_SERVICE_ID, 4) + FIELD_DIVIDER
        + String.format(ORDER_SERVICE_PRICE, 678)
        + ORDER_SERVICE_END + FIELD_DIVIDER
        + ORDER_ROUTE_START
        + OBJECT_START
        + String.format(ROUTE_ID, 7) + FIELD_DIVIDER
        + String.format(ROUTE_LATITUDE, "12.34") + FIELD_DIVIDER
        + String.format(ROUTE_LONGITUDE, "34.12") + FIELD_DIVIDER
        + String.format(ROUTE_COMMENT, "comment 1") + FIELD_DIVIDER
        + String.format(ROUTE_ADDRESS, "address 1") + FIELD_DIVIDER
        + ROUTE_UNCHECKED
        + OBJECT_END + FIELD_DIVIDER
        + OBJECT_START
        + String.format(ROUTE_ID, 8) + FIELD_DIVIDER
        + String.format(ROUTE_LATITUDE, "56.78") + FIELD_DIVIDER
        + String.format(ROUTE_LONGITUDE, "78.56") + FIELD_DIVIDER
        + String.format(ROUTE_COMMENT, "comment 2") + FIELD_DIVIDER
        + String.format(ROUTE_ADDRESS, "address 2") + FIELD_DIVIDER
        + ROUTE_CHECKED
        + OBJECT_END + FIELD_DIVIDER
        + OBJECT_START
        + String.format(ROUTE_ID, 9) + FIELD_DIVIDER
        + String.format(ROUTE_LATITUDE, "90.12") + FIELD_DIVIDER
        + String.format(ROUTE_LONGITUDE, "12.90") + FIELD_DIVIDER
        + String.format(ROUTE_COMMENT, "comment 3") + FIELD_DIVIDER
        + String.format(ROUTE_ADDRESS, "address 3") + FIELD_DIVIDER
        + ROUTE_UNCHECKED
        + OBJECT_END
        + ORDER_ROUTE_END + FIELD_DIVIDER
        + ORDER_OPTIONS_START
        + OBJECT_START
        + String.format(OPTION_ID, 56) + FIELD_DIVIDER
        + String.format(OPTION_NAME, "Грузчики") + FIELD_DIVIDER
        + String.format(OPTION_NUMERIC_VALUE, 2) + FIELD_DIVIDER
        + OPTION_NUMERIC + FIELD_DIVIDER
        + OPTION_DYNAMIC + FIELD_DIVIDER
        + String.format(OPTION_MIN, 0) + FIELD_DIVIDER
        + String.format(OPTION_MAX, 2)
        + OBJECT_END + FIELD_DIVIDER
        + OBJECT_START
        + String.format(OPTION_ID, 55) + FIELD_DIVIDER
        + String.format(OPTION_NAME, "Ремни крепления") + FIELD_DIVIDER
        + OPTION_TRUE_VALUE + FIELD_DIVIDER
        + OPTION_BOOLEAN + FIELD_DIVIDER
        + OPTION_DYNAMIC + FIELD_DIVIDER
        + String.format(OPTION_DESCRIPTION, "Имеются стяжные ремни для для фиксации груза.")
        + OBJECT_END + FIELD_DIVIDER
        + OBJECT_START
        + String.format(OPTION_ID, 6) + FIELD_DIVIDER
        + String.format(OPTION_NAME, "Безналичная оплата") + FIELD_DIVIDER
        + OPTION_FALSE_VALUE + FIELD_DIVIDER
        + OPTION_BOOLEAN + FIELD_DIVIDER
        + OPTION_STATIC
        + OBJECT_END + FIELD_DIVIDER
        + OBJECT_START
        + String.format(OPTION_ID, 57) + FIELD_DIVIDER
        + String.format(OPTION_NAME, "Гидроборт") + FIELD_DIVIDER
        + String.format(OPTION_NUMERIC_VALUE, 1500) + FIELD_DIVIDER
        + OPTION_NUMERIC + FIELD_DIVIDER
        + OPTION_STATIC + FIELD_DIVIDER
        + String.format(OPTION_DESCRIPTION, "Поднимающая штуковина")
        + OBJECT_END
        + ORDER_OPTIONS_END
        + OBJECT_END;
  }

  @NonNull
  String getOrderWithoutServicePrice() {
    return OBJECT_START
        + String.format(ORDER_ID, 7) + FIELD_DIVIDER
        + String.format(PAYMENT_TYPE, "CASH") + FIELD_DIVIDER
        + String.format(ORDER_COMMENT, "some comment") + FIELD_DIVIDER
        + String.format(ORDER_ESTIMATED_AMOUNT_TEXT, "over 9999 BTC") + FIELD_DIVIDER
        + String.format(ORDER_ESTIMATED_AMOUNT, 9999) + FIELD_DIVIDER
        + String.format(ORDER_ESTIMATED_TIME, 234_532_000) + FIELD_DIVIDER
        + String.format(ORDER_ESTIMATED_ROUTE_DISTANCE, 35_213) + FIELD_DIVIDER
        + String.format(ORDER_TOTAL_COST, 10_352) + FIELD_DIVIDER
        + String.format(ORDER_TIMEOUT, 25) + FIELD_DIVIDER
        + String.format(ORDER_ETA, 1234567890) + FIELD_DIVIDER
        + String.format(ORDER_CONFIRM_TIME, 9876543210L) + FIELD_DIVIDER
        + String.format(ORDER_START_TIME, 9876598760L) + FIELD_DIVIDER
        + String.format(ORDER_SCHEDULED_START_TIME, 128937981273L) + FIELD_DIVIDER
        + ORDER_DISTANCE_START
        + String.format(ORDER_DISTANCE_EXECUTOR_ID, 5) + FIELD_DIVIDER
        + String.format(ORDER_DISTANCE_VALUE, 546)
        + ORDER_DISTANCE_END + FIELD_DIVIDER
        + ORDER_SERVICE_START
        + String.format(ORDER_SERVICE_ID, 4) + FIELD_DIVIDER
        + String.format(ORDER_SERVICE_NAME, "service")
        + ORDER_SERVICE_END + FIELD_DIVIDER
        + ORDER_ROUTE_START
        + OBJECT_START
        + String.format(ROUTE_ID, 7) + FIELD_DIVIDER
        + String.format(ROUTE_LATITUDE, "12.34") + FIELD_DIVIDER
        + String.format(ROUTE_LONGITUDE, "34.12") + FIELD_DIVIDER
        + String.format(ROUTE_COMMENT, "comment 1") + FIELD_DIVIDER
        + String.format(ROUTE_ADDRESS, "address 1") + FIELD_DIVIDER
        + ROUTE_UNCHECKED
        + OBJECT_END + FIELD_DIVIDER
        + OBJECT_START
        + String.format(ROUTE_ID, 8) + FIELD_DIVIDER
        + String.format(ROUTE_LATITUDE, "56.78") + FIELD_DIVIDER
        + String.format(ROUTE_LONGITUDE, "78.56") + FIELD_DIVIDER
        + String.format(ROUTE_COMMENT, "comment 2") + FIELD_DIVIDER
        + String.format(ROUTE_ADDRESS, "address 2") + FIELD_DIVIDER
        + ROUTE_CHECKED
        + OBJECT_END + FIELD_DIVIDER
        + OBJECT_START
        + String.format(ROUTE_ID, 9) + FIELD_DIVIDER
        + String.format(ROUTE_LATITUDE, "90.12") + FIELD_DIVIDER
        + String.format(ROUTE_LONGITUDE, "12.90") + FIELD_DIVIDER
        + String.format(ROUTE_COMMENT, "comment 3") + FIELD_DIVIDER
        + String.format(ROUTE_ADDRESS, "address 3") + FIELD_DIVIDER
        + ROUTE_UNCHECKED
        + OBJECT_END
        + ORDER_ROUTE_END + FIELD_DIVIDER
        + ORDER_OPTIONS_START
        + OBJECT_START
        + String.format(OPTION_ID, 56) + FIELD_DIVIDER
        + String.format(OPTION_NAME, "Грузчики") + FIELD_DIVIDER
        + String.format(OPTION_NUMERIC_VALUE, 2) + FIELD_DIVIDER
        + OPTION_NUMERIC + FIELD_DIVIDER
        + OPTION_DYNAMIC + FIELD_DIVIDER
        + String.format(OPTION_MIN, 0) + FIELD_DIVIDER
        + String.format(OPTION_MAX, 2)
        + OBJECT_END + FIELD_DIVIDER
        + OBJECT_START
        + String.format(OPTION_ID, 55) + FIELD_DIVIDER
        + String.format(OPTION_NAME, "Ремни крепления") + FIELD_DIVIDER
        + OPTION_TRUE_VALUE + FIELD_DIVIDER
        + OPTION_BOOLEAN + FIELD_DIVIDER
        + OPTION_DYNAMIC + FIELD_DIVIDER
        + String.format(OPTION_DESCRIPTION, "Имеются стяжные ремни для для фиксации груза.")
        + OBJECT_END + FIELD_DIVIDER
        + OBJECT_START
        + String.format(OPTION_ID, 6) + FIELD_DIVIDER
        + String.format(OPTION_NAME, "Безналичная оплата") + FIELD_DIVIDER
        + OPTION_FALSE_VALUE + FIELD_DIVIDER
        + OPTION_BOOLEAN + FIELD_DIVIDER
        + OPTION_STATIC
        + OBJECT_END + FIELD_DIVIDER
        + OBJECT_START
        + String.format(OPTION_ID, 57) + FIELD_DIVIDER
        + String.format(OPTION_NAME, "Гидроборт") + FIELD_DIVIDER
        + String.format(OPTION_NUMERIC_VALUE, 1500) + FIELD_DIVIDER
        + OPTION_NUMERIC + FIELD_DIVIDER
        + OPTION_STATIC + FIELD_DIVIDER
        + String.format(OPTION_DESCRIPTION, "Поднимающая штуковина")
        + OBJECT_END
        + ORDER_OPTIONS_END
        + OBJECT_END;
  }

  @NonNull
  String getOrderWithoutService() {
    return OBJECT_START
        + String.format(ORDER_ID, 7) + FIELD_DIVIDER
        + String.format(PAYMENT_TYPE, "CASH") + FIELD_DIVIDER
        + String.format(ORDER_COMMENT, "some comment") + FIELD_DIVIDER
        + String.format(ORDER_ESTIMATED_AMOUNT_TEXT, "over 9999 BTC") + FIELD_DIVIDER
        + String.format(ORDER_ESTIMATED_AMOUNT, 9999) + FIELD_DIVIDER
        + String.format(ORDER_ESTIMATED_TIME, 234_532_000) + FIELD_DIVIDER
        + String.format(ORDER_ESTIMATED_ROUTE_DISTANCE, 35_213) + FIELD_DIVIDER
        + String.format(ORDER_TOTAL_COST, 10_352) + FIELD_DIVIDER
        + String.format(ORDER_TIMEOUT, 25) + FIELD_DIVIDER
        + String.format(ORDER_ETA, 1234567890) + FIELD_DIVIDER
        + String.format(ORDER_CONFIRM_TIME, 9876543210L) + FIELD_DIVIDER
        + String.format(ORDER_START_TIME, 9876598760L) + FIELD_DIVIDER
        + String.format(ORDER_SCHEDULED_START_TIME, 128937981273L) + FIELD_DIVIDER
        + ORDER_DISTANCE_START
        + String.format(ORDER_DISTANCE_EXECUTOR_ID, 5) + FIELD_DIVIDER
        + String.format(ORDER_DISTANCE_VALUE, 546)
        + ORDER_DISTANCE_END + FIELD_DIVIDER
        + ORDER_ROUTE_START
        + OBJECT_START
        + String.format(ROUTE_ID, 7) + FIELD_DIVIDER
        + String.format(ROUTE_LATITUDE, "12.34") + FIELD_DIVIDER
        + String.format(ROUTE_LONGITUDE, "34.12") + FIELD_DIVIDER
        + String.format(ROUTE_COMMENT, "comment 1") + FIELD_DIVIDER
        + String.format(ROUTE_ADDRESS, "address 1") + FIELD_DIVIDER
        + ROUTE_UNCHECKED
        + OBJECT_END + FIELD_DIVIDER
        + OBJECT_START
        + String.format(ROUTE_ID, 8) + FIELD_DIVIDER
        + String.format(ROUTE_LATITUDE, "56.78") + FIELD_DIVIDER
        + String.format(ROUTE_LONGITUDE, "78.56") + FIELD_DIVIDER
        + String.format(ROUTE_COMMENT, "comment 2") + FIELD_DIVIDER
        + String.format(ROUTE_ADDRESS, "address 2") + FIELD_DIVIDER
        + ROUTE_CHECKED
        + OBJECT_END + FIELD_DIVIDER
        + OBJECT_START
        + String.format(ROUTE_ID, 9) + FIELD_DIVIDER
        + String.format(ROUTE_LATITUDE, "90.12") + FIELD_DIVIDER
        + String.format(ROUTE_LONGITUDE, "12.90") + FIELD_DIVIDER
        + String.format(ROUTE_COMMENT, "comment 3") + FIELD_DIVIDER
        + String.format(ROUTE_ADDRESS, "address 3") + FIELD_DIVIDER
        + ROUTE_UNCHECKED
        + OBJECT_END
        + ORDER_ROUTE_END + FIELD_DIVIDER
        + ORDER_OPTIONS_START
        + OBJECT_START
        + String.format(OPTION_ID, 56) + FIELD_DIVIDER
        + String.format(OPTION_NAME, "Грузчики") + FIELD_DIVIDER
        + String.format(OPTION_NUMERIC_VALUE, 2) + FIELD_DIVIDER
        + OPTION_NUMERIC + FIELD_DIVIDER
        + OPTION_DYNAMIC + FIELD_DIVIDER
        + String.format(OPTION_MIN, 0) + FIELD_DIVIDER
        + String.format(OPTION_MAX, 2)
        + OBJECT_END + FIELD_DIVIDER
        + OBJECT_START
        + String.format(OPTION_ID, 55) + FIELD_DIVIDER
        + String.format(OPTION_NAME, "Ремни крепления") + FIELD_DIVIDER
        + OPTION_TRUE_VALUE + FIELD_DIVIDER
        + OPTION_BOOLEAN + FIELD_DIVIDER
        + OPTION_DYNAMIC + FIELD_DIVIDER
        + String.format(OPTION_DESCRIPTION, "Имеются стяжные ремни для для фиксации груза.")
        + OBJECT_END + FIELD_DIVIDER
        + OBJECT_START
        + String.format(OPTION_ID, 6) + FIELD_DIVIDER
        + String.format(OPTION_NAME, "Безналичная оплата") + FIELD_DIVIDER
        + OPTION_FALSE_VALUE + FIELD_DIVIDER
        + OPTION_BOOLEAN + FIELD_DIVIDER
        + OPTION_STATIC
        + OBJECT_END + FIELD_DIVIDER
        + OBJECT_START
        + String.format(OPTION_ID, 57) + FIELD_DIVIDER
        + String.format(OPTION_NAME, "Гидроборт") + FIELD_DIVIDER
        + String.format(OPTION_NUMERIC_VALUE, 1500) + FIELD_DIVIDER
        + OPTION_NUMERIC + FIELD_DIVIDER
        + OPTION_STATIC + FIELD_DIVIDER
        + String.format(OPTION_DESCRIPTION, "Поднимающая штуковина")
        + OBJECT_END
        + ORDER_OPTIONS_END
        + OBJECT_END;
  }

  @NonNull
  String getOrderWithEmptyRoute() {
    return OBJECT_START
        + String.format(ORDER_ID, 7) + FIELD_DIVIDER
        + String.format(PAYMENT_TYPE, "CASH") + FIELD_DIVIDER
        + String.format(ORDER_COMMENT, "some comment") + FIELD_DIVIDER
        + String.format(ORDER_ESTIMATED_AMOUNT_TEXT, "over 9999 BTC") + FIELD_DIVIDER
        + String.format(ORDER_ESTIMATED_AMOUNT, 9999) + FIELD_DIVIDER
        + String.format(ORDER_ESTIMATED_TIME, 234_532_000) + FIELD_DIVIDER
        + String.format(ORDER_ESTIMATED_ROUTE_DISTANCE, 35_213) + FIELD_DIVIDER
        + String.format(ORDER_TOTAL_COST, 10_352) + FIELD_DIVIDER
        + String.format(ORDER_TIMEOUT, 25) + FIELD_DIVIDER
        + String.format(ORDER_ETA, 1234567890) + FIELD_DIVIDER
        + String.format(ORDER_CONFIRM_TIME, 9876543210L) + FIELD_DIVIDER
        + String.format(ORDER_START_TIME, 9876598760L) + FIELD_DIVIDER
        + String.format(ORDER_SCHEDULED_START_TIME, 128937981273L) + FIELD_DIVIDER
        + ORDER_DISTANCE_START
        + String.format(ORDER_DISTANCE_EXECUTOR_ID, 5) + FIELD_DIVIDER
        + String.format(ORDER_DISTANCE_VALUE, 546)
        + ORDER_DISTANCE_END + FIELD_DIVIDER
        + ORDER_SERVICE_START
        + String.format(ORDER_SERVICE_ID, 4) + FIELD_DIVIDER
        + String.format(ORDER_SERVICE_NAME, "service") + FIELD_DIVIDER
        + String.format(ORDER_SERVICE_PRICE, 678)
        + ORDER_SERVICE_END + FIELD_DIVIDER
        + ORDER_ROUTE_START
        + ORDER_ROUTE_END + FIELD_DIVIDER
        + ORDER_OPTIONS_START
        + OBJECT_START
        + String.format(OPTION_ID, 56) + FIELD_DIVIDER
        + String.format(OPTION_NAME, "Грузчики") + FIELD_DIVIDER
        + String.format(OPTION_NUMERIC_VALUE, 2) + FIELD_DIVIDER
        + OPTION_NUMERIC + FIELD_DIVIDER
        + OPTION_DYNAMIC + FIELD_DIVIDER
        + String.format(OPTION_MIN, 0) + FIELD_DIVIDER
        + String.format(OPTION_MAX, 2)
        + OBJECT_END + FIELD_DIVIDER
        + OBJECT_START
        + String.format(OPTION_ID, 55) + FIELD_DIVIDER
        + String.format(OPTION_NAME, "Ремни крепления") + FIELD_DIVIDER
        + OPTION_TRUE_VALUE + FIELD_DIVIDER
        + OPTION_BOOLEAN + FIELD_DIVIDER
        + OPTION_DYNAMIC + FIELD_DIVIDER
        + String.format(OPTION_DESCRIPTION, "Имеются стяжные ремни для для фиксации груза.")
        + OBJECT_END + FIELD_DIVIDER
        + OBJECT_START
        + String.format(OPTION_ID, 6) + FIELD_DIVIDER
        + String.format(OPTION_NAME, "Безналичная оплата") + FIELD_DIVIDER
        + OPTION_FALSE_VALUE + FIELD_DIVIDER
        + OPTION_BOOLEAN + FIELD_DIVIDER
        + OPTION_STATIC
        + OBJECT_END + FIELD_DIVIDER
        + OBJECT_START
        + String.format(OPTION_ID, 57) + FIELD_DIVIDER
        + String.format(OPTION_NAME, "Гидроборт") + FIELD_DIVIDER
        + String.format(OPTION_NUMERIC_VALUE, 1500) + FIELD_DIVIDER
        + OPTION_NUMERIC + FIELD_DIVIDER
        + OPTION_STATIC + FIELD_DIVIDER
        + String.format(OPTION_DESCRIPTION, "Поднимающая штуковина")
        + OBJECT_END
        + ORDER_OPTIONS_END
        + OBJECT_END;
  }

  @NonNull
  String getOrderWithoutRoute() {
    return OBJECT_START
        + String.format(ORDER_ID, 7) + FIELD_DIVIDER
        + String.format(PAYMENT_TYPE, "CASH") + FIELD_DIVIDER
        + String.format(ORDER_COMMENT, "some comment") + FIELD_DIVIDER
        + String.format(ORDER_ESTIMATED_AMOUNT_TEXT, "over 9999 BTC") + FIELD_DIVIDER
        + String.format(ORDER_ESTIMATED_AMOUNT, 9999) + FIELD_DIVIDER
        + String.format(ORDER_ESTIMATED_TIME, 234_532_000) + FIELD_DIVIDER
        + String.format(ORDER_ESTIMATED_ROUTE_DISTANCE, 35_213) + FIELD_DIVIDER
        + String.format(ORDER_TOTAL_COST, 10_352) + FIELD_DIVIDER
        + String.format(ORDER_TIMEOUT, 25) + FIELD_DIVIDER
        + String.format(ORDER_ETA, 1234567890) + FIELD_DIVIDER
        + String.format(ORDER_CONFIRM_TIME, 9876543210L) + FIELD_DIVIDER
        + String.format(ORDER_START_TIME, 9876598760L) + FIELD_DIVIDER
        + String.format(ORDER_SCHEDULED_START_TIME, 128937981273L) + FIELD_DIVIDER
        + ORDER_DISTANCE_START
        + String.format(ORDER_DISTANCE_EXECUTOR_ID, 5) + FIELD_DIVIDER
        + String.format(ORDER_DISTANCE_VALUE, 546)
        + ORDER_DISTANCE_END + FIELD_DIVIDER
        + ORDER_SERVICE_START
        + String.format(ORDER_SERVICE_ID, 4) + FIELD_DIVIDER
        + String.format(ORDER_SERVICE_NAME, "service") + FIELD_DIVIDER
        + String.format(ORDER_SERVICE_PRICE, 678)
        + ORDER_SERVICE_END + FIELD_DIVIDER
        + ORDER_OPTIONS_START
        + OBJECT_START
        + String.format(OPTION_ID, 56) + FIELD_DIVIDER
        + String.format(OPTION_NAME, "Грузчики") + FIELD_DIVIDER
        + String.format(OPTION_NUMERIC_VALUE, 2) + FIELD_DIVIDER
        + OPTION_NUMERIC + FIELD_DIVIDER
        + OPTION_DYNAMIC + FIELD_DIVIDER
        + String.format(OPTION_MIN, 0) + FIELD_DIVIDER
        + String.format(OPTION_MAX, 2)
        + OBJECT_END + FIELD_DIVIDER
        + OBJECT_START
        + String.format(OPTION_ID, 55) + FIELD_DIVIDER
        + String.format(OPTION_NAME, "Ремни крепления") + FIELD_DIVIDER
        + OPTION_TRUE_VALUE + FIELD_DIVIDER
        + OPTION_BOOLEAN + FIELD_DIVIDER
        + OPTION_DYNAMIC + FIELD_DIVIDER
        + String.format(OPTION_DESCRIPTION, "Имеются стяжные ремни для для фиксации груза.")
        + OBJECT_END + FIELD_DIVIDER
        + OBJECT_START
        + String.format(OPTION_ID, 6) + FIELD_DIVIDER
        + String.format(OPTION_NAME, "Безналичная оплата") + FIELD_DIVIDER
        + OPTION_FALSE_VALUE + FIELD_DIVIDER
        + OPTION_BOOLEAN + FIELD_DIVIDER
        + OPTION_STATIC
        + OBJECT_END + FIELD_DIVIDER
        + OBJECT_START
        + String.format(OPTION_ID, 57) + FIELD_DIVIDER
        + String.format(OPTION_NAME, "Гидроборт") + FIELD_DIVIDER
        + String.format(OPTION_NUMERIC_VALUE, 1500) + FIELD_DIVIDER
        + OPTION_NUMERIC + FIELD_DIVIDER
        + OPTION_STATIC + FIELD_DIVIDER
        + String.format(OPTION_DESCRIPTION, "Поднимающая штуковина")
        + OBJECT_END
        + ORDER_OPTIONS_END
        + OBJECT_END;
  }

  @NonNull
  String getOrderWithEmptyOptions() {
    return OBJECT_START
        + String.format(ORDER_ID, 7) + FIELD_DIVIDER
        + String.format(PAYMENT_TYPE, "CASH") + FIELD_DIVIDER
        + String.format(ORDER_COMMENT, "some comment") + FIELD_DIVIDER
        + String.format(ORDER_ESTIMATED_AMOUNT_TEXT, "over 9999 BTC") + FIELD_DIVIDER
        + String.format(ORDER_ESTIMATED_AMOUNT, 9999) + FIELD_DIVIDER
        + String.format(ORDER_ESTIMATED_TIME, 234_532_000) + FIELD_DIVIDER
        + String.format(ORDER_ESTIMATED_ROUTE_DISTANCE, 35_213) + FIELD_DIVIDER
        + String.format(ORDER_TOTAL_COST, 10_352) + FIELD_DIVIDER
        + String.format(ORDER_TIMEOUT, 25) + FIELD_DIVIDER
        + String.format(ORDER_ETA, 1234567890) + FIELD_DIVIDER
        + String.format(ORDER_CONFIRM_TIME, 9876543210L) + FIELD_DIVIDER
        + String.format(ORDER_START_TIME, 9876598760L) + FIELD_DIVIDER
        + String.format(ORDER_SCHEDULED_START_TIME, 128937981273L) + FIELD_DIVIDER
        + ORDER_DISTANCE_START
        + String.format(ORDER_DISTANCE_EXECUTOR_ID, 5) + FIELD_DIVIDER
        + String.format(ORDER_DISTANCE_VALUE, 546)
        + ORDER_DISTANCE_END + FIELD_DIVIDER
        + ORDER_SERVICE_START
        + String.format(ORDER_SERVICE_ID, 4) + FIELD_DIVIDER
        + String.format(ORDER_SERVICE_NAME, "service") + FIELD_DIVIDER
        + String.format(ORDER_SERVICE_PRICE, 678)
        + ORDER_SERVICE_END + FIELD_DIVIDER
        + ORDER_ROUTE_START
        + OBJECT_START
        + String.format(ROUTE_ID, 7) + FIELD_DIVIDER
        + String.format(ROUTE_LATITUDE, "12.34") + FIELD_DIVIDER
        + String.format(ROUTE_LONGITUDE, "34.12") + FIELD_DIVIDER
        + String.format(ROUTE_COMMENT, "comment 1") + FIELD_DIVIDER
        + String.format(ROUTE_ADDRESS, "address 1") + FIELD_DIVIDER
        + ROUTE_UNCHECKED
        + OBJECT_END + FIELD_DIVIDER
        + OBJECT_START
        + String.format(ROUTE_ID, 8) + FIELD_DIVIDER
        + String.format(ROUTE_LATITUDE, "56.78") + FIELD_DIVIDER
        + String.format(ROUTE_LONGITUDE, "78.56") + FIELD_DIVIDER
        + String.format(ROUTE_COMMENT, "comment 2") + FIELD_DIVIDER
        + String.format(ROUTE_ADDRESS, "address 2") + FIELD_DIVIDER
        + ROUTE_CHECKED
        + OBJECT_END + FIELD_DIVIDER
        + OBJECT_START
        + String.format(ROUTE_ID, 9) + FIELD_DIVIDER
        + String.format(ROUTE_LATITUDE, "90.12") + FIELD_DIVIDER
        + String.format(ROUTE_LONGITUDE, "12.90") + FIELD_DIVIDER
        + String.format(ROUTE_COMMENT, "comment 3") + FIELD_DIVIDER
        + String.format(ROUTE_ADDRESS, "address 3") + FIELD_DIVIDER
        + ROUTE_UNCHECKED
        + OBJECT_END
        + ORDER_ROUTE_END + FIELD_DIVIDER
        + ORDER_OPTIONS_START
        + ORDER_OPTIONS_END
        + OBJECT_END;
  }

  @NonNull
  String getOrderWithoutOptions() {
    return OBJECT_START
        + String.format(ORDER_ID, 7) + FIELD_DIVIDER
        + String.format(PAYMENT_TYPE, "CASH") + FIELD_DIVIDER
        + String.format(ORDER_COMMENT, "some comment") + FIELD_DIVIDER
        + String.format(ORDER_ESTIMATED_AMOUNT_TEXT, "over 9999 BTC") + FIELD_DIVIDER
        + String.format(ORDER_ESTIMATED_AMOUNT, 9999) + FIELD_DIVIDER
        + String.format(ORDER_ESTIMATED_TIME, 234_532_000) + FIELD_DIVIDER
        + String.format(ORDER_ESTIMATED_ROUTE_DISTANCE, 35_213) + FIELD_DIVIDER
        + String.format(ORDER_TOTAL_COST, 10_352) + FIELD_DIVIDER
        + String.format(ORDER_TIMEOUT, 25) + FIELD_DIVIDER
        + String.format(ORDER_ETA, 1234567890) + FIELD_DIVIDER
        + String.format(ORDER_CONFIRM_TIME, 9876543210L) + FIELD_DIVIDER
        + String.format(ORDER_START_TIME, 9876598760L) + FIELD_DIVIDER
        + String.format(ORDER_SCHEDULED_START_TIME, 128937981273L) + FIELD_DIVIDER
        + ORDER_DISTANCE_START
        + String.format(ORDER_DISTANCE_EXECUTOR_ID, 5) + FIELD_DIVIDER
        + String.format(ORDER_DISTANCE_VALUE, 546)
        + ORDER_DISTANCE_END + FIELD_DIVIDER
        + ORDER_SERVICE_START
        + String.format(ORDER_SERVICE_ID, 4) + FIELD_DIVIDER
        + String.format(ORDER_SERVICE_NAME, "service") + FIELD_DIVIDER
        + String.format(ORDER_SERVICE_PRICE, 678)
        + ORDER_SERVICE_END + FIELD_DIVIDER
        + ORDER_ROUTE_START
        + OBJECT_START
        + String.format(ROUTE_ID, 7) + FIELD_DIVIDER
        + String.format(ROUTE_LATITUDE, "12.34") + FIELD_DIVIDER
        + String.format(ROUTE_LONGITUDE, "34.12") + FIELD_DIVIDER
        + String.format(ROUTE_COMMENT, "comment 1") + FIELD_DIVIDER
        + String.format(ROUTE_ADDRESS, "address 1") + FIELD_DIVIDER
        + ROUTE_UNCHECKED
        + OBJECT_END + FIELD_DIVIDER
        + OBJECT_START
        + String.format(ROUTE_ID, 8) + FIELD_DIVIDER
        + String.format(ROUTE_LATITUDE, "56.78") + FIELD_DIVIDER
        + String.format(ROUTE_LONGITUDE, "78.56") + FIELD_DIVIDER
        + String.format(ROUTE_COMMENT, "comment 2") + FIELD_DIVIDER
        + String.format(ROUTE_ADDRESS, "address 2") + FIELD_DIVIDER
        + ROUTE_CHECKED
        + OBJECT_END + FIELD_DIVIDER
        + OBJECT_START
        + String.format(ROUTE_ID, 9) + FIELD_DIVIDER
        + String.format(ROUTE_LATITUDE, "90.12") + FIELD_DIVIDER
        + String.format(ROUTE_LONGITUDE, "12.90") + FIELD_DIVIDER
        + String.format(ROUTE_COMMENT, "comment 3") + FIELD_DIVIDER
        + String.format(ROUTE_ADDRESS, "address 3") + FIELD_DIVIDER
        + ROUTE_UNCHECKED
        + OBJECT_END
        + ORDER_ROUTE_END
        + OBJECT_END;
  }
}
