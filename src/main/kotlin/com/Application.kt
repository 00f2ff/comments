package com

import com.dao.DB
import io.ktor.server.netty.*
import com.plugins.*
import com.service.CommentService
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.serialization.*

fun Application.module() {
    install(ContentNegotiation) {
        json()
    }

    DB().initialize()
    val commentService = CommentService()
    configureRouting(commentService)
    configureMonitoring()
    configureHTTP()
}

fun main(args: Array<String>): Unit = EngineMain.main(args)
