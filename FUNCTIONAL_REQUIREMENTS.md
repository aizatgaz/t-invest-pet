# Функциональные требования: «Управление учебным портфелем T-Invest Sandbox»

## 1. Назначение приложения

Приложение позволяет работать с учебным брокерским счётом T-Invest Sandbox:

- создать песочничный счёт;
- пополнить его виртуальными рублями;
- создать и отменить учебную заявку;
- посмотреть счета, заявки и общую стоимость портфеля;
- сохранить краткие данные в PostgreSQL.

Приложение работает только с песочницей. Операции с реальными деньгами и реальными биржевыми заявками не входят в проект.

## 2. Технологические ограничения

- Java 17 или новее.
- Spring Framework 6 без Spring Boot.
- Java-конфигурация Spring без XML.
- Spring Web MVC для собственного REST API.
- Spring `RestClient` для обращения к T-Invest API.
- Maven для сборки.
- PostgreSQL 15 или новее.
- Spring JDBC или обычный JDBC для доступа к БД.
- Flyway для миграций БД.
- Docker и Docker Compose.
- WireMock для интеграционных тестов внешнего API.
- Формат собственного и внешнего REST API — JSON.

Spring Boot и его автоконфигурация использоваться не должны.

## 3. Ограничение интеграции

Приложение должно использовать методы только одного сервиса T-Invest OpenAPI:

1. `SandboxService` — создание счёта, пополнение, работа с учебными заявками и получение портфеля.

Вызовы `InstrumentsService`, `UsersService`, `OperationsService`, `OrdersService`, `MarketDataService` и других сервисов запрещены в рамках MVP.

Всего используется 7 методов внешнего API.

## 4. Настройка внешнего API

- Все внешние методы вызываются HTTP-методом `POST`.
- Базовый URL задаётся переменной `T_INVEST_BASE_URL`.
- Для песочницы рекомендуемое значение: `https://sandbox-invest-public-api.tbank.ru/rest`.
- Токен задаётся переменной `INVEST_TOKEN`.
- Заголовок авторизации: `Authorization: Bearer <token>`.
- Токен запрещено хранить в исходном коде, PostgreSQL, Docker-образе и логах.
- Денежные значения `MoneyValue` и `Quotation` преобразуются в `BigDecimal` по формуле `units + nano / 1_000_000_000`.
- Все значения времени сохраняются как `TIMESTAMPTZ` в UTC.

## 5. Используемые методы T-Invest OpenAPI

| № | Сервис | Метод | Путь | Использование |
|---|---|---|---|---|
| 1 | `SandboxService` | `OpenSandboxAccount` | `/tinkoff.public.invest.api.contract.v1.SandboxService/OpenSandboxAccount` | Создать новый учебный счёт. |
| 2 | `SandboxService` | `GetSandboxAccounts` | `/tinkoff.public.invest.api.contract.v1.SandboxService/GetSandboxAccounts` | Получить и обновить список учебных счетов. |
| 3 | `SandboxService` | `SandboxPayIn` | `/tinkoff.public.invest.api.contract.v1.SandboxService/SandboxPayIn` | Пополнить счёт виртуальными рублями. |
| 4 | `SandboxService` | `PostSandboxOrder` | `/tinkoff.public.invest.api.contract.v1.SandboxService/PostSandboxOrder` | Создать учебную рыночную или лимитную заявку. |
| 5 | `SandboxService` | `GetSandboxOrders` | `/tinkoff.public.invest.api.contract.v1.SandboxService/GetSandboxOrders` | Получить активные заявки счёта. |
| 6 | `SandboxService` | `CancelSandboxOrder` | `/tinkoff.public.invest.api.contract.v1.SandboxService/CancelSandboxOrder` | Отменить активную заявку. |
| 7 | `SandboxService` | `GetSandboxPortfolio` | `/tinkoff.public.invest.api.contract.v1.SandboxService/GetSandboxPortfolio` | Получить общую стоимость портфеля в рублях. |

Три метода создают данные во внешней системе:

- `OpenSandboxAccount` создаёт песочничный счёт;
- `SandboxPayIn` создаёт операцию пополнения;
- `PostSandboxOrder` создаёт торговую заявку.

`CancelSandboxOrder` изменяет ранее созданную заявку во внешней системе.

## 6. Функциональные требования

### FR-01. Создание песочничного счёта

1. Пользователь передаёт название счёта длиной от 1 до 100 символов.
2. Приложение вызывает `OpenSandboxAccount` с переданным названием.
3. После успешного создания приложение получает `accountId` из ответа, вызывает `GetSandboxAccounts`, находит счёт по этому `accountId` и сохраняет его в `sandbox_account_view`.
4. Собственный API возвращает созданный `SandboxAccountViewDto`.
5. Если внешний API вернул ошибку, локальная запись не создаётся.

### FR-02. Получение и синхронизация счетов

1. Приложение вызывает `GetSandboxAccounts` со статусом `ACCOUNT_STATUS_ALL`.
2. Каждый полученный счёт добавляется или обновляется по `accountId`.
3. Дубликаты счетов не создаются.
4. Для каждого открытого счёта приложение может вызвать `GetSandboxPortfolio` с валютой `RUB` и обновить `totalAmountRub`.
5. Ошибка обновления стоимости одного портфеля не должна удалять ранее сохранённое значение.

### FR-03. Пополнение счёта

1. Пользователь передаёт положительную сумму в рублях.
2. Минимальная сумма — `1.00 RUB`, максимальная сумма одного запроса — `1 000 000.00 RUB`.
3. Приложение проверяет наличие и открытый статус счёта в локальной витрине.
4. Приложение вызывает `SandboxPayIn` с `accountId` и `MoneyValue(currency = "rub")`.
5. После успешного пополнения приложение вызывает `GetSandboxPortfolio` и обновляет `totalAmountRub` счёта.
6. Самостоятельная витрина пополнений не создаётся.

### FR-04. Создание учебной заявки

1. Пользователь передаёт:
   - `instrumentUid`;
   - `direction`: `BUY` или `SELL`;
   - `quantity`: целое число лотов от 1 до 1000;
   - `orderType`: `MARKET` или `LIMIT`;
   - `price` только для лимитной заявки.
2. `price` обязателен и должен быть больше нуля для `LIMIT`; для `MARKET` он не передаётся.
3. `instrumentUid` пользователь получает самостоятельно; приложение не обращается к справочнику инструментов другого сервиса.
4. Корректность и доступность `instrumentUid` проверяются ответом `PostSandboxOrder`. При отклонении заявки пользователю возвращается понятная ошибка.
5. `accountId` должен соответствовать открытому счёту из локальной витрины.
6. Приложение формирует UUID как идентификатор идемпотентности `orderId` до обращения во внешний API.
7. Приложение вызывает `PostSandboxOrder`:
   - `ORDER_DIRECTION_BUY` или `ORDER_DIRECTION_SELL`;
   - `ORDER_TYPE_MARKET` или `ORDER_TYPE_LIMIT`;
   - `TIME_IN_FORCE_DAY`;
   - `PRICE_TYPE_CURRENCY` для лимитной заявки;
   - `confirmMarginTrade = false`.
8. После успешного ответа заявка сохраняется или обновляется в `sandbox_order_view`.
9. Повторная обработка того же `orderId` не должна создавать вторую внешнюю или локальную заявку.
10. Если внешний API отклонил заявку, локально сохраняется статус `REJECTED` и причина без технического stack trace.

### FR-05. Получение заявок

1. Приложение вызывает `GetSandboxOrders` для выбранного счёта.
2. Полученные активные заявки добавляются или обновляются в `sandbox_order_view`.
3. Ранее сохранённые заявки, отсутствующие в ответе, не удаляются: метод внешнего API возвращает активные заявки, а локальная витрина хранит также завершённые и отменённые.
4. Собственный API возвращает локальную витрину заявок после синхронизации.
5. По умолчанию заявки сортируются по `updatedAt` от новых к старым.

### FR-06. Отмена заявки

1. Пользователь передаёт `accountId` и `orderId`.
2. Заявка должна существовать в `sandbox_order_view` и принадлежать указанному счёту.
3. Отменять можно только заявку в активном статусе.
4. Приложение вызывает `CancelSandboxOrder` с `ORDER_ID_TYPE_REQUEST`, так как используется ранее сформированный идентификатор идемпотентности.
5. После успешной отмены локальный статус изменяется на `CANCELLED`.
6. Повторная отмена уже отменённой заявки возвращает успешный текущий результат без повторного вызова внешнего API.

### FR-07. Получение портфеля

1. Приложение вызывает `GetSandboxPortfolio` с `accountId` и `currency = RUB`.
2. Из ответа используется только общая стоимость портфеля `totalAmountPortfolio`.
3. Значение сохраняется в поле `totalAmountRub` витрины счёта.
4. Подробные позиции портфеля и свечи в MVP не сохраняются.

## 7. Собственный REST API приложения

| Метод и путь | Назначение | Успешный ответ |
|---|---|---|
| `POST /api/v1/sandbox/accounts` | Создать учебный счёт | `201 Created`, `SandboxAccountViewDto` |
| `POST /api/v1/sandbox/accounts/sync` | Синхронизировать счета | `200 OK`, массив `SandboxAccountViewDto` |
| `GET /api/v1/sandbox/accounts` | Получить счета из PostgreSQL | `200 OK`, массив `SandboxAccountViewDto` |
| `POST /api/v1/sandbox/accounts/{accountId}/pay-ins` | Пополнить счёт | `200 OK`, обновлённый `SandboxAccountViewDto` |
| `POST /api/v1/sandbox/accounts/{accountId}/portfolio/refresh` | Обновить стоимость портфеля | `200 OK`, обновлённый `SandboxAccountViewDto` |
| `POST /api/v1/sandbox/accounts/{accountId}/orders` | Создать заявку | `201 Created`, `SandboxOrderViewDto` |
| `POST /api/v1/sandbox/accounts/{accountId}/orders/sync` | Синхронизировать активные заявки | `200 OK`, массив `SandboxOrderViewDto` |
| `GET /api/v1/sandbox/accounts/{accountId}/orders` | Получить локальную витрину заявок | `200 OK`, массив `SandboxOrderViewDto` |
| `DELETE /api/v1/sandbox/accounts/{accountId}/orders/{orderId}` | Отменить заявку | `200 OK`, обновлённый `SandboxOrderViewDto` |

Минимальные тела запросов собственного API:

```json
POST /api/v1/sandbox/accounts
{
  "name": "Учебный счет"
}
```

```json
POST /api/v1/sandbox/accounts/{accountId}/pay-ins
{
  "amount": 100000.00
}
```

```json
POST /api/v1/sandbox/accounts/{accountId}/orders
{
  "instrumentUid": "uuid",
  "direction": "BUY",
  "quantity": 1,
  "orderType": "MARKET"
}
```

## 8. Упрощённые DTO и витрины PostgreSQL

В MVP есть только две постоянные витрины и два соответствующих DTO. Внешние transport-модели клиента T-Invest и модели тел запросов собственного API не считаются витринами.

### 8.1. `SandboxAccountViewDto` / `sandbox_account_view`

Одна строка соответствует одному песочничному счёту.

| Java-поле | Тип Java | Столбец PostgreSQL | Назначение |
|---|---|---|---|
| `accountId` | `String` | `account_id VARCHAR(64) PRIMARY KEY` | Идентификатор счёта T-Invest |
| `name` | `String` | `name VARCHAR(100) NOT NULL` | Название счёта |
| `status` | `String` | `status VARCHAR(32) NOT NULL` | `OPEN` или `CLOSED` |
| `totalAmountRub` | `BigDecimal` | `total_amount_rub NUMERIC(19,2)` | Общая стоимость портфеля в рублях |
| `updatedAt` | `Instant` | `updated_at TIMESTAMPTZ NOT NULL` | Время последнего обновления |

### 8.2. `SandboxOrderViewDto` / `sandbox_order_view`

Одна строка соответствует одной учебной заявке.

| Java-поле | Тип Java | Столбец PostgreSQL | Назначение |
|---|---|---|---|
| `orderId` | `UUID` | `order_id UUID PRIMARY KEY` | Идентификатор идемпотентности заявки |
| `accountId` | `String` | `account_id VARCHAR(64) NOT NULL` | Ссылка на `sandbox_account_view` |
| `instrumentUid` | `UUID` | `instrument_uid UUID NOT NULL` | Идентификатор инструмента |
| `direction` | `String` | `direction VARCHAR(8) NOT NULL` | `BUY` или `SELL` |
| `quantity` | `long` | `quantity BIGINT NOT NULL` | Количество лотов |
| `orderType` | `String` | `order_type VARCHAR(16) NOT NULL` | `MARKET` или `LIMIT` |
| `price` | `BigDecimal` | `price NUMERIC(19,9)` | Цена лимитной заявки; `null` для рыночной |
| `status` | `String` | `status VARCHAR(32) NOT NULL` | Текущий статус заявки |
| `updatedAt` | `Instant` | `updated_at TIMESTAMPTZ NOT NULL` | Время последнего изменения |

Для `sandbox_order_view.account_id` создаётся внешний ключ на `sandbox_account_view.account_id` и индекс для чтения заявок по счёту.

## 9. Проверка входных данных и обработка ошибок

- Некорректные имя, сумма, количество, цена или UUID — `400 Bad Request`.
- Неизвестный локальный `accountId` или `orderId` — `404 Not Found`.
- Попытка отменить чужую заявку — `404 Not Found` без раскрытия существования заявки.
- Ошибка авторизации внешнего API (`401` или `403`) — `502 Bad Gateway`.
- Ограничение внешнего API (`429`) — одна повторная попытка; затем `503 Service Unavailable`.
- Тайм-аут или ответ `5xx` внешнего API — `503 Service Unavailable`.
- При ошибке внешнего API ранее сохранённые витрины не очищаются.
- Ошибка возвращается в формате `timestamp`, `status`, `code`, `message`, `path`.
- В логах фиксируются имя внешнего метода, HTTP-статус и длительность. Токен и полные тела запросов не логируются.

## 10. Docker

`docker-compose.yml` должен содержать:

1. `app` — приложение;
2. `postgres` — PostgreSQL с постоянным volume и healthcheck;
3. `wiremock` — локальный эмулятор `SandboxService`.

Настройки приложения:

- `T_INVEST_BASE_URL`;
- `INVEST_TOKEN`;
- `DB_URL`;
- `DB_USER`;
- `DB_PASSWORD`.

Для WireMock `T_INVEST_BASE_URL=http://wiremock:8080/rest`.

## 11. Тестирование с WireMock

Для каждого из 7 внешних методов должен существовать успешный WireMock stub.

Обязательные интеграционные тесты:

1. Создание счёта вызывает `OpenSandboxAccount`, затем `GetSandboxAccounts` и создаёт одну локальную запись.
2. Повторная синхронизация счетов не создаёт дубликаты.
3. Пополнение передаёт корректный `MoneyValue` в `SandboxPayIn` и обновляет стоимость через `GetSandboxPortfolio`.
4. Создание рыночной заявки передаёт UUID идемпотентности и создаёт одну локальную запись.
5. Создание лимитной заявки передаёт `price`, а рыночной — не передаёт.
6. Некорректный `instrumentUid` приводит к ожидаемой ошибке и не создаёт локальную активную заявку.
7. Синхронизация заявок обновляет статусы без удаления истории.
8. Отмена вызывает `CancelSandboxOrder` с `ORDER_ID_TYPE_REQUEST` и сохраняет `CANCELLED`.
9. Ошибка внешнего API не оставляет частично записанные локальные данные.
10. Ответы `401`, `429` и `500` преобразуются в ожидаемые ошибки собственного API.

Тесты должны проверять путь, JSON-тело и наличие заголовка `Authorization`, не выводя значение токена.

## 12. Критерии приёмки

MVP считается готовым, если:

- проект собирается командой `mvn clean verify`;
- приложение запускается через `docker compose up --build`;
- Flyway создаёт ровно две таблицы-витрины;
- используется только `SandboxService`;
- реализованы все 7 методов из раздела 5;
- счёт, пополнение и заявка действительно создаются через T-Invest Sandbox API;
- повторные синхронизации не создают дубликаты;
- повторная обработка `orderId` не создаёт дублирующую заявку;
- тесты выполняются без реального доступа к T-Invest благодаря WireMock;
- токен отсутствует в репозитории, БД и логах.

## 13. Вне рамок MVP

- реальные брокерские счета и реальные деньги;
- сервисы T-Invest API, кроме `SandboxService`;
- стоп-заявки и изменение существующей заявки;
- подробная витрина позиций портфеля;
- свечи, стакан и исторические рыночные данные;
- комиссии, налоги и аналитика доходности;
- собственная авторизация и несколько пользователей;
- пользовательский веб-интерфейс.

## 14. Источники

- [SandboxService](https://developer.tbank.ru/invest/api/sandbox-service)
- [OpenSandboxAccount](https://developer.tbank.ru/invest/api/sandbox-service-open-sandbox-account)
- [GetSandboxAccounts](https://developer.tbank.ru/invest/api/sandbox-service-get-sandbox-accounts)
- [SandboxPayIn](https://developer.tbank.ru/invest/api/sandbox-service-sandbox-pay-in)
- [PostSandboxOrder](https://developer.tbank.ru/invest/api/sandbox-service-post-sandbox-order)
- [GetSandboxOrders](https://developer.tbank.ru/invest/api/sandbox-service-get-sandbox-orders)
- [CancelSandboxOrder](https://developer.tbank.ru/invest/api/sandbox-service-cancel-sandbox-order)
- [GetSandboxPortfolio](https://developer.tbank.ru/invest/api/sandbox-service-get-sandbox-portfolio)

Документ актуализирован по публичной документации T-Invest API, доступной 20 сентября 2026 года.
