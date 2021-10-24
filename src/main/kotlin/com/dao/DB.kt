package com.dao

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

object Comments: Table("comments") {
    val id = integer("id").autoIncrement()
    val parentId = integer("parent_id").nullable()
    val childId = integer("child_id").nullable()
    val userId = integer("user_id")
    val text = text("text")
//    val reactions = varchar("reactions", 255).nullable()
    val acknowledged = bool("acknowledged").default(false)
    // todo: add something around video/image attachments

    override val primaryKey = PrimaryKey(id)
}

class DB {
    fun initialize() {
        Database.connect("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", driver = "org.h2.Driver")
        transaction {
            addLogger(StdOutSqlLogger)
            SchemaUtils.create(Comments)
        }
    }
}