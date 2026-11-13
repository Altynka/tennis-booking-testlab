# Tennis Booking Testlab

![CI](https://github.com/Altynka/tennis-booking-testlab/actions/workflows/ci.yml/badge.svg)

Тестовый полигон: сервис бронирования теннисных кортов с намеренными багами
и автотестами, которые эти баги находят.

## Структура

```
tennis-booking-testlab/
├── backend/        # сервер: Kotlin + Spring Boot + H2
├── android-app/    # (позже) Android-приложение
├── autotests/      # (позже) API, UI и нагрузочные тесты
└── docs/           # (позже) тест-план, тест-кейсы, баг-репорты
```

## Запуск сервера

Нужен JDK 21.

**Через IntelliJ IDEA:** File → Open → папка `backend` → «Open as Project».
IDEA сама скачает Gradle и зависимости. Затем открыть `TestlabApplication.kt`
и нажать зелёный ▶ рядом с `fun main`.

**Через терминал** (один раз создать wrapper):

```bash
brew install gradle
cd backend
gradle wrapper
./gradlew bootRun
```

Сервер стартует на http://localhost:8080

## Проверка в Postman

| Запрос | Ожидаемый результат |
|---|---|
| `GET http://localhost:8080/courts` | 200, список из 4 кортов |
| `GET http://localhost:8080/courts/1` | 200, один корт |
| `GET http://localhost:8080/courts/999` | 404 |
| `GET http://localhost:8080/courts/abc` | 400 (неверный формат id) |

## База данных

Веб-консоль: http://localhost:8080/h2-console

- JDBC URL: `jdbc:h2:mem:testlab`
- User: `sa`, пароль пустой

```sql
SELECT * FROM court;
```

## Статус

- [x] Шаг 1 — корты, `GET /courts`
- [ ] Шаг 2 — бронирование
- [ ] Шаг 3 — переключатель багов (профили `buggy` / `fixed`)
- [ ] Шаг 4 — API-тесты
