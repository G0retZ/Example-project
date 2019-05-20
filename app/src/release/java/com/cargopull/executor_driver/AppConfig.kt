package com.cargopull.executor_driver

const val QUIET_CHANNEL_ID = "state_channel"
const val ANNOUNCEMENT_CHANNEL_ID = "announcement_channel"
const val BASE_URL = "https://gate.cargopull.host:8443/executor/"
const val SOCKET_URL = "wss://gate.cargopull.host:8443/executor/ws"
const val GEOLOCATION_DESTINATION = "/mobile/online"
fun STATUS_DESTINATION(value: String) = "/queue/$value"
const val POLLING_DESTINATION = "/mobile/retrieveOverPackage"
const val STATIC_MAP_KEY = "AIzaSyBwlubLyqI6z_ivfAWcTCfyTXkoRHTagMk"