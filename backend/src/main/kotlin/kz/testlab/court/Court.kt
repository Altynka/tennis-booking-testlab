package kz.testlab.court

import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import java.time.LocalTime

enum class Surface { HARD, CLAY, GRASS }

/** Теннисный корт. Превращается в таблицу COURT в базе данных. */
@Entity
class Court(
    var name: String,

    @Enumerated(EnumType.STRING)
    var surface: Surface,

    /** Цена за час в тенге */
    var pricePerHour: Int,

    var openTime: LocalTime,
    var closeTime: LocalTime,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
)
