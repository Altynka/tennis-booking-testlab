# Tennis Booking Testlab

![CI](https://github.com/Altynka/tennis-booking-testlab/actions/workflows/ci.yml/badge.svg)

Тестовый полигон: сервис бронирования теннисных кортов с намеренными багами
и автотестами, которые эти баги находят.

## Структура

```
tennis-booking-testlab/
├── backend/        # сервер: Kotlin + Spring Boot + H2
├── docs/           # тест-кейсы, позже тест-план и баг-репорты
├── android-app/    # (позже) Android-приложение
└── autotests/      # (позже) API, UI и нагрузочные тесты
```

## Запуск сервера

Нужен JDK 21.

**Через IntelliJ IDEA:** открыть `backend/src/main/kotlin/kz/testlab/TestlabApplication.kt`
и нажать зелёный ▶ рядом с `fun main`.

**Через терминал:**

```bash
cd backend
./gradlew bootRun
```

Сервер стартует на http://localhost:8080

## API

| Метод | Путь | Описание |
|---|---|---|
| GET | `/courts` | список кортов |
| GET | `/courts/{id}` | один корт |
| GET | `/courts/{id}/slots?date=YYYY-MM-DD` | часовые слоты корта на дату |
| POST | `/bookings` | создать бронь |
| GET | `/bookings?phone=...` | брони по телефону |
| GET | `/bookings/{id}` | одна бронь |
| POST | `/bookings/{id}/cancel` | отменить бронь |

### Правила бронирования

1. Бронь начинается ровно в начале часа
2. Нельзя бронировать прошедшее время
3. Бронь целиком внутри часов работы корта
4. Нельзя пересекаться с другой активной бронью этого корта
5. Отменить можно только активную бронь
6. Нельзя отменить бронь, которая уже началась
7. Длительность 1–3 часа, телефон в формате `+7XXXXXXXXXX`

### Формат ошибок

Любая ошибка возвращается в одном формате:

```json
{
  "timestamp": "2026-11-13T15:40:00+05:00",
  "status": 409,
  "error": "Conflict",
  "code": "SLOT_TAKEN",
  "message": "Court is already booked for this time",
  "path": "/bookings"
}
```

Тест-кейсы для ручной проверки: [docs/test-cases/booking.md](docs/test-cases/booking.md)

## База данных

Веб-консоль: http://localhost:8080/h2-console

- JDBC URL: `jdbc:h2:mem:testlab`
- User: `sa`, пароль пустой

```sql
SELECT * FROM court;
SELECT * FROM booking;
```

## Статус

- [x] Шаг 1 — корты, `GET /courts`, CI
- [x] Шаг 2 — бронирование, единый формат ошибок
- [ ] Шаг 3 — переключатель багов (профили `buggy` / `fixed`)
- [ ] Шаг 4 — API-тесты
