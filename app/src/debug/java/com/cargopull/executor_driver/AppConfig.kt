package com.cargopull.executor_driver

const val QUIET_CHANNEL_ID = "state_channel"
const val ANNOUNCEMENT_CHANNEL_ID = "announcement_channel"
const val SERVER_NAME = "tst.capsrv.xyz"
const val BASE_URL = "http://$SERVER_NAME:9080/executor/"
const val SOCKET_URL = "ws://$SERVER_NAME:9080/executor/ws"
const val GEOLOCATION_DESTINATION = "/mobile/online"
fun STATUS_DESTINATION(value: String) = "/queue/$value"
const val POLLING_DESTINATION = "/mobile/retrieveOverPackage"