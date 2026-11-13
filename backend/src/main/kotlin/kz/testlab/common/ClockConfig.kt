package kz.testlab.common

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Clock
import java.time.ZoneId

/**
 * Единые "часы" приложения в часовом поясе Алматы.
 * Весь код берёт текущее время отсюда, а не через LocalDateTime.now(),
 * поэтому в тестах время можно подменить и проверять, например, бронь "в прошлом".
 */
@Configuration
class ClockConfig {

    @Bean
    fun clock(): Clock = Clock.system(ZoneId.of("Asia/Almaty"))
}
