package kz.testlab.common

import org.springframework.http.HttpStatus

/**
 * Ошибка бизнес-логики, которую API возвращает клиенту.
 *
 * @param status HTTP-статус ответа (400, 404, 409...)
 * @param code   короткий машинный код ошибки, например SLOT_TAKEN — по нему удобно делать проверки в автотестах
 * @param message понятное описание для человека
 */
class ApiException(
    val status: HttpStatus,
    val code: String,
    override val message: String,
) : RuntimeException(message)
