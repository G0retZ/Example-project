package com.cargopull.executor_driver

const val QUIET_CHANNEL_ID = "state_channel"
const val ANNOUNCEMENT_CHANNEL_ID = "announcement_channel"
const val SERVER_NAME = "gate.cargopull.host"
const val BASE_URL = "https://$SERVER_NAME:8443/executor/"
const val SOCKET_URL = "wss://$SERVER_NAME:8443/executor/ws"
const val GEOLOCATION_DESTINATION = "/mobile/online"
fun STATUS_DESTINATION(value: String) = "/queue/$value"
const val POLLING_DESTINATION = "/mobile/retrieveOverPackage"