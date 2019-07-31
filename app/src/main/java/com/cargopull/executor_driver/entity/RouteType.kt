package com.cargopull.executor_driver.entity

/**
 * Взимоисключающие состояния маршрута.
 */
enum class RouteType {

    /**
     * Весь маршрут пролегает в городе.
     */
    POLYGON,

    /**
     * Часть маршрута пролегает за городом.
     */
    ORDER_ZONE,

    /**
     * Маршрут междугородний.
     */
    INTER_CITY
}
