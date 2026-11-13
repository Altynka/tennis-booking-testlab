package kz.testlab.booking

import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import kz.testlab.court.Court
import java.time.LocalDateTime

enum class BookingStatus { ACTIVE, CANCELLED }

/** Бронь корта. Превращается в таблицу BOOKING, связанную с COURT через court_id. */
@Entity
class Booking(
    @ManyToOne(optional = false)
    var court: Court,

    var customerName: String,

    /** Телефон в формате +7XXXXXXXXXX — по нему клиент находит "мои брони" */
    var phone: String,

    /** Начало брони (время Алматы), всегда ровно в начале часа */
    var startAt: LocalDateTime,

    var durationHours: Int,

    /** Итоговая цена в тенге = цена корта за час × длительность */
    var price: Int,

    @Enumerated(EnumType.STRING)
    var status: BookingStatus = BookingStatus.ACTIVE,

    var createdAt: LocalDateTime,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
) {
    /** Конец брони не хранится в БД, а вычисляется */
    fun endAt(): LocalDateTime = startAt.plusHours(durationHours.toLong())
}
