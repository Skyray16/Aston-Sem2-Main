Проект состоит из двух микросервисов:
1. **user-service** (порт 8080) — управление пользователями (CRUD), отправка событий в Kafka.
2. **notification-service** (порт 8081) — слушает Kafka и отправляет email-уведомления, а также имеет API для прямой отправки писем.

## Технологии
Java 21, Spring Boot 3, Spring Data JPA, PostgreSQL, Apache Kafka, Docker, Testcontainers, GreenMail.

## Как запустить
1. Запустите Kafka (из корневой папки проекта):
   docker-compose up -d
2. Запустите user-service (из корневой папки проекта):
   mvn spring-boot:run
3. В новом терминале запустите notification-service:
   cd notification-service
   mvn spring-boot:run

## API
- POST /api/users — создать пользователя (user-service, порт 8080).
- DELETE /api/users/{id} — удалить пользователя (user-service, порт 8080).
- POST /api/notifications/email (порт 8081) — отправить email вручную.