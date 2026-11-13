package kz.testlab.common

import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import java.time.Clock
import java.time.OffsetDateTime

/**
 * Перехватывает ошибки из всех контроллеров и превращает их в ApiError.
 * Благодаря этому клиент всегда получает один и тот же формат: status, code, message, path.
 */
@RestControllerAdvice
class GlobalExceptionHandler(private val clock: Clock) {

    /** Наши бизнес-ошибки: корт не найден, слот занят и т.д. */
    @ExceptionHandler(ApiException::class)
    fun handleApi(ex: ApiException, request: HttpServletRequest): ResponseEntity<ApiError> =
        build(ex.status, ex.code, ex.message, request)

    /** Не прошла валидация тела запроса (@NotBlank, @Pattern...) */
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(ex: MethodArgumentNotValidException, request: HttpServletRequest): ResponseEntity<ApiError> {
        val message = ex.bindingResult.fieldErrors
            .sortedBy { it.field }
            .joinToString("; ") { "${it.field}: ${it.defaultMessage}" }
        return build(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", message, request)
    }

    /** Параметр неверного типа, например /courts/abc вместо числа */
    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleTypeMismatch(ex: MethodArgumentTypeMismatchException, request: HttpServletRequest): ResponseEntity<ApiError> =
        build(HttpStatus.BAD_REQUEST, "INVALID_PARAMETER", "Invalid value '${ex.value}' for parameter '${ex.name}'", request)

    /** Нет обязательного query-параметра, например ?date= */
    @ExceptionHandler(MissingServletRequestParameterException::class)
    fun handleMissingParam(ex: MissingServletRequestParameterException, request: HttpServletRequest): ResponseEntity<ApiError> =
        build(HttpStatus.BAD_REQUEST, "MISSING_PARAMETER", "Required parameter '${ex.parameterName}' is missing", request)

    /** Тело запроса пустое, не JSON или поле неверного формата (например, дата "завтра") */
    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleUnreadable(ex: HttpMessageNotReadableException, request: HttpServletRequest): ResponseEntity<ApiError> =
        build(HttpStatus.BAD_REQUEST, "MALFORMED_REQUEST", "Request body is missing or has invalid format", request)

    private fun build(status: HttpStatus, code: String, message: String, request: HttpServletRequest): ResponseEntity<ApiError> =
        ResponseEntity.status(status).body(
            ApiError(
                timestamp = OffsetDateTime.now(clock),
                status = status.value(),
                error = status.reasonPhrase,
                code = code,
                message = message,
                path = request.requestURI,
            )
        )
}
