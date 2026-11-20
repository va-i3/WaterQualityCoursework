# Інформаційна система моніторингу якості води

Клієнт-серверний застосунок для ведення проб води, вимірювань, розрахунку інтегрального показника якості, контролю життєвого циклу проб та фонового імпорту вимірювань із CSV-файлів.

## Використані технології
* Серверна частина (server): Java 21, Maven, Servlet API / HTTP / JSON, JDBC, MySQL (water_monitoring_db), JUnit 5, java.util.logging.Logger.
* Клієнтська частина (client): Java 21, JavaFX (Task, ProgressBar, TableView), Jackson (ObjectMapper), java.net.http.HttpClient, java.util.logging.Logger.

## Структура проєкту
* server/ — серверний Maven-проєкт (моделі, DAO/JDBC, сервіси, сервлети, модульні тести JUnit 5, SQL-скрипти бази даних).
* client/ — клієнтський JavaFX-застосунок (WaterClientApp, CsvMeasurementImporter, WaterHttpClient, sample-data/measurements.csv).

## Підготовка бази даних MySQL
1. Переконайтеся, що сервер MySQL запущено.
2. Створіть або ініціалізуйте базу даних water_monitoring_db за допомогою SQL-скрипту схеми та початкових даних.
3. Перевірте параметри підключення до БД у конфігурації серверної частини (не зберігайте паролі та інші локальні секрети у відкритому вигляді в репозиторії).

## Збірка та запуск сервера (server)
Перейдіть у каталог server/:

mvn clean test
mvn clean package

* Після mvn clean package у каталозі server/target/ формується актуальний WAR-файл (water-monitoring-server.war).
* Розгорніть згенерований WAR-файл у веб-контейнері за адресою http://localhost:8080/water-monitoring-server.

## Запуск тестів JUnit 5
У каталозі server/ виконайте:

mvn test

(або mvn clean test для чистого запуску модульних тестів без необхідності запуску JavaFX, веб-контейнера та бази MySQL).

## Запуск JavaFX-клієнта (client)
1. Переконайтеся, що сервер запущений і доступний за налаштованою базовою URL-адресою.
2. Запустіть клас com.water.client.WaterClientApp.

## Формат і використання CSV-файлу
* Файл за замовчуванням: client/sample-data/measurements.csv (кодування UTF-8, роздільник полів — ;).
* Формат заголовка та рядків даних:

sampleId;param1;param2;param3;wqi
3;80.0;12.5;4.2;78.5

* Імпорт запускається кнопкою «Імпорт CSV» у вікні клієнта у фоновому потоці (JavaFX Task) з відображенням прогресу (ProgressBar), текстового статусу та оновленням таблиці після завершення.