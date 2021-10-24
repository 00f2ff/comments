package com.plugins

import com.data.Comment
import com.service.CommentService
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

fun Application.configureRouting(commentService: CommentService) {

    routing {
        route("/comment") {
            get {
                val loadThread = call.request.queryParameters["thread"] == true.toString()
                val seeAcked = call.request.queryParameters["acked"] == true.toString()
                // Filter out replies, since those will be visible if `thread` param is set
                var result = commentService.read(loadThread).filter { it.parentId == null }
                if (!seeAcked) {
                    result = result.filter { !it.acknowledged }
                }
                call.respond(Json.encodeToJsonElement(result))
            }

            get("{id}") {
                val id = call.parameters["id"]
                if (id.isNullOrBlank()) {
                    call.respond(HttpStatusCode.BadRequest, "Missing id")
                }
                val loadThread = call.request.queryParameters["thread"] == true.toString()
                val result = commentService.read(id!!.toInt(), loadThread)
                call.respond(HttpStatusCode.Found, Json.encodeToJsonElement(result))
            }

            get("/text") {
                val searchText = call.request.queryParameters["text"]
                if (searchText.isNullOrBlank()) {
                    call.respond(HttpStatusCode.BadRequest, "Text cannot be blank")
                }
                val result = commentService.findCommentsMatchingText(searchText!!)
                call.respond(HttpStatusCode.Found, Json.encodeToJsonElement(result))
            }

            post {
                val comment = call.receive<Comment>()
                val result = commentService.create(comment)
                call.respond(HttpStatusCode.Created, Json.encodeToJsonElement(result))
            }

            // Unhandled edge case: replies to the same parent comment overwrite each other
            post("/reply") {
                val comment = call.receive<Comment>()
                // Create the reply comment
                val result = commentService.create(comment)
                // Link the parent to the child
                commentService.setChildIdForParent(result)
                call.respond(HttpStatusCode.Created, Json.encodeToJsonElement(result))
            }

            put {
                val comment = call.receive<Comment>()
                commentService.update(comment)
                call.respond(HttpStatusCode.OK)
            }
        }
    }
}
