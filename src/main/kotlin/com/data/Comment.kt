package com.data

import kotlinx.serialization.Serializable

/**
 * Container for Comment data. Comments are threads are implemented using doubly-linked lists, designed
 * for printing out a nested thread from a parent.
 */
@Serializable
data class Comment(
    val id: Int? = null,
    val parentId: Int? = null,
    val child: Comment? = null,
    val userId: Int,
    val text: String,
//    val reactions: List<String>?,
    val acknowledged: Boolean = false
) {
}