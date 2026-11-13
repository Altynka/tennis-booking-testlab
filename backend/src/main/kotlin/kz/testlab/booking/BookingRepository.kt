package kz.testlab.booking

import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDateTime

/**
 * Spring сам строит SQL по имени метода:
 * findByCourtIdAndStatusAndStartAtBetween →
 *   SELECT ... WHERE court_id = ? AND status = ? AND start_at BETWEEN ? AND ?
 */
interface BookingRepository : JpaRepository<Booking, Long> {

    fun findByCourtIdAndStatusAndStartAtBetween(
        courtId: Long,
        status: BookingStatus,
        from: LocalDateTime,
        to: LocalDateTime,
    ): List<Booking>

    fun findByPhoneOrderByStartAtDesc(phone: String): List<Booking>
}
