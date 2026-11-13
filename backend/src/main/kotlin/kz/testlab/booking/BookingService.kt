package kz.testlab.booking

import kz.testlab.common.ApiException
import kz.testlab.court.Court
import kz.testlab.court.CourtRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Clock
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

/**
 * Вся бизнес-логика бронирования. Здесь живут правила —
 * и именно сюда на шаге 3 мы будем добавлять намеренные баги.
 */
@Service
class BookingService(
    private val bookingRepository: BookingRepository,
    private val courtRepository: CourtRepository,
    private val clock: Clock,
) {

    @Transactional
    fun create(request: CreateBookingRequest): BookingResponse {
        // После @Valid в контроллере эти поля гарантированно не null
        val court = findCourt(request.courtId!!)
        val startAt = request.startAt!!
        val duration = request.durationHours!!
        val endAt = startAt.plusHours(duration.toLong())
        val now = LocalDateTime.now(clock)

        // Правило 1: бронь начинается ровно в начале часа (10:00, а не 10:30)
        if (startAt.minute != 0 || startAt.second != 0 || startAt.nano != 0) {
            throw ApiException(HttpStatus.BAD_REQUEST, "INVALID_START_TIME", "Booking must start at the beginning of an hour, e.g. 10:00")
        }

        // Правило 2: нельзя бронировать прошедшее время
        if (!startAt.isAfter(now)) {
            throw ApiException(HttpStatus.BAD_REQUEST, "BOOKING_IN_PAST", "Cannot book a time in the past")
        }

        // Правило 3: бронь целиком внутри часов работы корта и в пределах одного дня
        val startsTooEarly = startAt.toLocalTime().isBefore(court.openTime)
        val endsNextDay = endAt.toLocalDate() != startAt.toLocalDate()
        val endsTooLate = endAt.toLocalTime().isAfter(court.closeTime)
        if (startsTooEarly || endsNextDay || endsTooLate) {
            throw ApiException(
                HttpStatus.BAD_REQUEST,
                "OUTSIDE_WORKING_HOURS",
                "Court '${court.name}' works from ${court.openTime} to ${court.closeTime}",
            )
        }

        // Правило 4: время не пересекается с другими активными бронями этого корта
        val overlaps = activeBookingsOn(court.id!!, startAt.toLocalDate())
            .any { it.startAt < endAt && it.endAt() > startAt }
        if (overlaps) {
            throw ApiException(HttpStatus.CONFLICT, "SLOT_TAKEN", "Court is already booked for this time")
        }

        val booking = Booking(
            court = court,
            customerName = request.customerName!!.trim(),
            phone = request.phone!!,
            startAt = startAt,
            durationHours = duration,
            price = court.pricePerHour * duration,
            createdAt = now,
        )
        return bookingRepository.save(booking).toResponse()
    }

    @Transactional(readOnly = true)
    fun getById(id: Long): BookingResponse = findBooking(id).toResponse()

    @Transactional(readOnly = true)
    fun findByPhone(phone: String): List<BookingResponse> =
        bookingRepository.findByPhoneOrderByStartAtDesc(phone).map { it.toResponse() }

    @Transactional
    fun cancel(id: Long): BookingResponse {
        val booking = findBooking(id)

        // Правило 5: отменить можно только активную бронь
        if (booking.status == BookingStatus.CANCELLED) {
            throw ApiException(HttpStatus.CONFLICT, "ALREADY_CANCELLED", "Booking $id is already cancelled")
        }

        // Правило 6: нельзя отменить бронь, которая уже началась
        if (!booking.startAt.isAfter(LocalDateTime.now(clock))) {
            throw ApiException(HttpStatus.CONFLICT, "BOOKING_ALREADY_STARTED", "Cannot cancel a booking that has already started")
        }

        booking.status = BookingStatus.CANCELLED
        return bookingRepository.save(booking).toResponse()
    }

    /** Часовые слоты корта на дату: свободен слот или нет */
    @Transactional(readOnly = true)
    fun slots(courtId: Long, date: LocalDate): List<SlotResponse> {
        val court = findCourt(courtId)
        val bookings = activeBookingsOn(courtId, date)
        val now = LocalDateTime.now(clock)

        // Считаем количество часов, а не прибавляем час к LocalTime в цикле:
        // 23:00 + 1 час = 00:00, и такой цикл мог бы стать бесконечным
        val hours = Duration.between(court.openTime, court.closeTime).toHours()

        return (0L until hours).map { i ->
            val slotStart = date.atTime(court.openTime).plusHours(i)
            val slotEnd = slotStart.plusHours(1)
            val taken = bookings.any { it.startAt < slotEnd && it.endAt() > slotStart }
            SlotResponse(
                start = slotStart.toLocalTime(),
                end = slotEnd.toLocalTime(),
                available = !taken && slotStart.isAfter(now),
            )
        }
    }

    private fun activeBookingsOn(courtId: Long, date: LocalDate): List<Booking> =
        bookingRepository.findByCourtIdAndStatusAndStartAtBetween(
            courtId,
            BookingStatus.ACTIVE,
            date.atStartOfDay(),
            date.atTime(LocalTime.MAX),
        )

    private fun findCourt(id: Long): Court =
        courtRepository.findById(id).orElseThrow {
            ApiException(HttpStatus.NOT_FOUND, "COURT_NOT_FOUND", "Court $id not found")
        }

    private fun findBooking(id: Long): Booking =
        bookingRepository.findById(id).orElseThrow {
            ApiException(HttpStatus.NOT_FOUND, "BOOKING_NOT_FOUND", "Booking $id not found")
        }
}
