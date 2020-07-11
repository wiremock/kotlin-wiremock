package com.marcinziolo.kotlin.wiremock

import java.net.ServerSocket

fun findRandomOpenPort(): Int {
    ServerSocket(0).use { socket -> return socket.localPort }
}