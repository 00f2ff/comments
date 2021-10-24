package com.service

import com.dao.Comments
import com.data.Comment
import io.ktor.http.*
import io.ktor.util.*
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import kotlin.text.Regex.Companion.escape

class CommentService {

    fun create(comment: Comment): Comment {
        val id = transaction {
            Comments.insert {
                it[parentId] = comment.parentId
                it[childId] = comment.child?.id
                it[userId] = comment.userId
                it[text] = escape(comment.text)
                it[acknowledged] = comment.acknowledged
//                it[reactions] = comment.reactions.toString() // fixme
            } get Comments.id
        }
        return comment.copy(id = id)
    }

    fun read(loadThread: Boolean = false): List<Comment> {
        return transaction {
            Comments.selectAll().toList().map { commentsResultToComment(it, loadThread) }
        }
    }

    fun read(id: Int, loadThread: Boolean = false): Comment {
        val result = transaction {
            Comments.select { Comments.id eq id }.map { commentsResultToComment(it, loadThread) }
        }
        if (result.isEmpty()) {
            throw Exception("Could not find comment with id $id")
        }
        return result.single()
    }

    fun update(comment: Comment) {
        transaction {
            Comments.update({Comments.id eq comment.id!!}) {
                it[text] = escape(comment.text)
                it[acknowledged] = comment.acknowledged
            }
        }
    }

    fun setChildIdForParent(childComment: Comment) {
        transaction {
            Comments.update({Comments.id eq childComment.parentId!!}) {
                it[childId] = childComment.id!!
            }
        }
    }

    // fixme: this doesn't work. I think it might have something to do with the Exposed library, or
    // maybe it requires a regex (either way, undocumented)
    fun findCommentsMatchingText(text: String): List<Comment> {
        return transaction {
            Comments.select { Comments.text like text }.map { commentsResultToComment(it, false) }
        }
    }

    /**
     * Helper that converts Exposed results to Comment classes.
     * Loads comment threads via recursive calls to the `read` function.
     * todo (out of scope): Load threads using recursive queries to reduce read transactions on DB
     */
    private fun commentsResultToComment(resultRow: ResultRow, loadThread: Boolean): Comment =
        Comment(
            id = resultRow[Comments.id],
            parentId = resultRow[Comments.parentId],
            child = if (loadThread) resultRow[Comments.childId]?.let { read(it, loadThread) } else null,
            userId = resultRow[Comments.userId],
            text = resultRow[Comments.text],
            acknowledged = resultRow[Comments.acknowledged]
        )
}