package kz.testlab.common

import java.time.OffsetDateTime

/** Единый формат тела ответа при любой ошибке API. */
data class ApiError(
    val timestamp: OffsetDateTime,
    val status: Int,
    val error: String,
    val code: String,
    val message: String,
    val path: String,
)
