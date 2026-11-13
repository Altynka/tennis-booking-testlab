package kz.testlab.booking

import jakarta.validation.Valid
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

/** HTTP-эндпоинты бронирования. Контроллер только принимает запросы, вся логика — в BookingService. */
@RestController
class BookingController(private val bookingService: BookingService) {

    /** POST /bookings — создать бронь → 201 Created */
    @PostMapping("/bookings")
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@Valid @RequestBody request: CreateBookingRequest): BookingResponse =
        bookingService.create(request)

    /** GET /bookings?phone=+77011234567 — "мои брони" по телефону */
    @GetMapping("/bookings")
    fun findByPhone(@RequestParam phone: String): List<BookingResponse> =
        bookingService.findByPhone(phone)

    /** GET /bookings/{id} — одна бронь */
    @GetMapping("/bookings/{id}")
    fun getById(@PathVariable id: Long): BookingResponse =
        bookingService.getById(id)

    /** POST /bookings/{id}/cancel — отменить бронь */
    @PostMapping("/bookings/{id}/cancel")
    fun cancel(@PathVariable id: Long): BookingResponse =
        bookingService.cancel(id)

    /** GET /courts/{courtId}/slots?date=2026-11-20 — свободные и занятые часы корта */
    @GetMapping("/courts/{courtId}/slots")
    fun slots(
        @PathVariable courtId: Long,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) date: LocalDate,
    ): List<SlotResponse> =
        bookingService.slots(courtId, date)
}
