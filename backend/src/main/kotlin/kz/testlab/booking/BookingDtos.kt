package kz.testlab.booking

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import java.time.LocalDateTime
import java.time.LocalTime

/**
 * Тело запроса POST /bookings.
 * Поля nullable, чтобы отсутствующее поле давало понятную ошибку валидации,
 * а не падение при чтении JSON.
 */
data class CreateBookingRequest(
    @field:NotNull(message = "is required")
    val courtId: Long?,

    @field:NotBlank(message = "must not be blank")
    @field:Size(max = 100, message = "must be at most 100 characters")
    val customerName: String?,

    @field:NotBlank(message = "must not be blank")
    @field:Pattern(regexp = "^\\+7\\d{10}$", message = "must match format +7XXXXXXXXXX")
    val phone: String?,

    @field:NotNull(message = "is required")
    val startAt: LocalDateTime?,

    @field:NotNull(message = "is required")
    @field:Min(value = 1, message = "must be between 1 and 3")
    @field:Max(value = 3, message = "must be between 1 and 3")
    val durationHours: Int?,
)

/** Что API возвращает о брони. Отдельно от сущности, чтобы не отдавать наружу лишнее. */
data class BookingResponse(
    val id: Long,
    val courtId: Long,
    val courtName: String,
    val customerName: String,
    val phone: String,
    val startAt: LocalDateTime,
    val endAt: LocalDateTime,
    val durationHours: Int,
    val price: Int,
    val status: BookingStatus,
    val createdAt: LocalDateTime,
)

/** Один часовой слот на корте в выбранную дату */
data class SlotResponse(
    val start: LocalTime,
    val end: LocalTime,
    val available: Boolean,
)

fun Booking.toResponse() = BookingResponse(
    id = id!!,
    courtId = court.id!!,
    courtName = court.name,
    customerName = customerName,
    phone = phone,
    startAt = startAt,
    endAt = endAt(),
    durationHours = durationHours,
    price = price,
    status = status,
    createdAt = createdAt,
)
