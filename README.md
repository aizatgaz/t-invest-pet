# Запуск в Tomcat (Windows)

1) Установить TomCat 10-ой версии
2) Выполнить в директории проекта `mvn clean package`
3) Скопировать `tinvest.war` из target директории в `apache-tomcat-10/webapps`
4) Перейти в директорию `apache-tomcat-10/bin` и выполнить `.\catalina.bat run`

Приложение запуститься на хосте: http://localhost:8080

## Swagger UI

После сборки `mvn clean package` и развёртывания `target/tinvest.war` откройте:

- http://localhost:8080/tinvest/swagger-ui/index.html
- Короткий адрес: http://localhost:8080/tinvest/swagger-ui.html
- OpenAPI YAML: http://localhost:8080/tinvest/api-docs/openapi.yaml

Если Tomcat использует другой порт или context path, замените их в адресе.
Swagger UI и спецификация доступны без аутентификации; сами API сохраняют защиту.
Ресурсы UI включены в WAR через WebJar и не загружаются с CDN.

Чтобы вызвать API из Swagger:

1. Нажмите **Authorize**, в `supportBasicAuth` укажите логин и пароль SUPPORT
   из локального `security.yaml`.
2. Откройте `POST /api/support/auth`, нажмите **Try it out**, затем **Execute**.
   Тело запроса не требуется.
3. Скопируйте `access_token` из ответа. В **Authorize → managerBearerAuth**
   вставьте сам JWT, без префикса `Bearer`.
4. Выполняйте операции Sandbox API. Для GET счетов заполните параметр `status`,
   например `ACCOUNT_STATUS_ALL`.

Спецификация хранится в одном исходном файле `api/openapi.yaml` и копируется
в classpath при сборке Maven. Относительный `servers.url` использует тот же
сервер и context path, где открыта документация. После изменения YAML
пересоберите и повторно разверните WAR. Введённые credentials не сохраняются
Swagger UI между перезагрузками страницы.

## Аутентификация и роли

Настройки находятся в локальном `src/main/resources/security.yaml`, исключённом из Git.
Для новой копии проекта создайте его до сборки командой Java 21:

```powershell
java scripts/InitializeSecurity.java
```

Команда создаёт локальный файл с логином `support` и случайным паролем. Существующий
файл она не перезаписывает. Используемые приложением настройки:

```yaml
security:
  support:
    username: 'support'
    password: '<пароль>'
  jwt:
    issuer: 'urn:invest:demo'
    audience: 'tinvest-api'
```

В текущем режиме разработки `JwtConfig` генерирует RSA-ключи 3072 бит при каждом
старте. После перезапуска ранее выданные JWT перестают действовать; ключи из YAML
не используются. После изменения YAML нужны новая сборка WAR и повторное
развёртывание. Локальный YAML с паролем попадает в WAR: не публикуйте этот артефакт.

### Получение токена MANAGER

`POST /api/support/auth` требует HTTP Basic с логином и паролем SUPPORT. Тело запроса не нужно.
Для WAR с context path `/tinvest` адрес будет `/tinvest/api/support/auth`.

Пример PowerShell (curl запросит пароль, указанный в YAML):

```powershell
$auth = curl.exe --silent --show-error --fail-with-body `
  --user support --request POST `
  'http://localhost:8080/tinvest/api/support/auth' | ConvertFrom-Json
```

Ответ содержит `access_token`, `token_type: Bearer` и `expires_in: 1200`.
Токен подписан RS256, имеет роль MANAGER, идентификатор `jti` и имя SUPPORT в `sub`.
Право SUPPORT выпускать MANAGER-токены является явной политикой этой ручки.
Ответ запрещено кешировать. Сессия и refresh token не создаются.

### Вызов API

```powershell
curl.exe --fail-with-body `
  -H "Authorization: Bearer $($auth.access_token)" `
  'http://localhost:8080/tinvest/api/v1/sandbox/accounts?status=ACCOUNT_STATUS_ALL'
```

Все `/api/v1/sandbox/**` требуют подписанный JWT с ролью MANAGER. Проверяются RS256-подпись,
`iss`, `aud`, наличие `sub`, `iat`, `exp` и временные ограничения. Токен действует
20 минут от `iat`; дополнительный допуск после `exp` отключён. После истечения
получите новый токен через `POST /api/support/auth`. Прежний статический MANAGER-токен больше
не используется, а Basic credentials SUPPORT не открывают Sandbox API.

`/api/support/**` требуют Basic с ролью SUPPORT. MANAGER JWT не даёт
доступ к методам SUPPORT. Документация открыта для чтения, остальные адреса закрыты по умолчанию.
Отсутствующие/неверные credentials дают 401, недостаточная роль JWT — 403.

Эти endpoints рассчитаны на служебные API-клиенты, явно передающие Authorization;
CSRF отключён. Для браузерной панели с автоматически отправляемыми credentials
нужна отдельная конфигурация с CSRF-защитой. В реальном окружении используйте HTTPS.

Выданные JWT действуют до истечения срока или перезапуска приложения с новой
парой RSA-ключей. Проверка пароля SUPPORT выполняется при каждом Basic-запросе.

### Проверка

```powershell
mvn test
mvn package
```

Тесты безопасности используют отдельные credentials и RSA-ключи в памяти;
они не обращаются к БД или Т-Инвестициям.
