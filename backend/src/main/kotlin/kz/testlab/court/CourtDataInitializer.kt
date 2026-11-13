package kz.testlab.court

import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import java.time.LocalTime

/** При запуске сервера заполняет базу тестовыми кортами. */
@Component
class CourtDataInitializer(private val repository: CourtRepository) : CommandLineRunner {

    override fun run(vararg args: String) {
        if (repository.count() > 0) return

        repository.saveAll(
            listOf(
                Court("Корт 1 — Центральный", Surface.HARD, 8000, LocalTime.of(7, 0), LocalTime.of(22, 0)),
                Court("Корт 2 — Грунт", Surface.CLAY, 10000, LocalTime.of(8, 0), LocalTime.of(21, 0)),
                Court("Корт 3 — Трава", Surface.GRASS, 15000, LocalTime.of(9, 0), LocalTime.of(20, 0)),
                Court("Корт 4 — Хард, крытый", Surface.HARD, 12000, LocalTime.of(6, 0), LocalTime.of(23, 0)),
            )
        )
    }
}
