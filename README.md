Проект представляет собой микросервисную архитектуру:
1. **user-service** (порт 8080) — управление пользователями (CRUD), отправка событий в Kafka. Документирован Swagger и поддерживает HATEOAS.
2. **notification-service** (порт 8081) — слушает Kafka и отправляет email-уведомления, а также имеет API для прямой отправки писем.

## Технологии
Java 21, Spring Boot 3, Spring Data JPA, PostgreSQL, Apache Kafka, Swagger (Springdoc OpenAPI), Spring HATEOAS, Docker, Testcontainers, GreenMail.

## Как запустить
1. Запустите Kafka (из корневой папки проекта):
   docker-compose up -d
2. Запустите user-service (из корневой папки):
   mvn spring-boot:run
3. (Опционально) Запустите notification-service для получения писем:
   cd notification-service
   mvn spring-boot:run

## Документация API (Swagger UI)
После запуска `user-service` интерактивная документация доступна по адресу:
**http://localhost:8080/swagger-ui.html**

Через Swagger UI можно тестировать все эндпоинты (GET, POST, PUT, DELETE) прямо из браузера.

## HATEOAS
Ответы API содержат гипермедиа-ссылки (`_links`), позволяющие клиенту навигироваться по ресурсам.
Пример ответа `GET /api/users/1`:
{
  "id": 1,
  "name": "Иван",
  "email": "ivan@test.com",
  "_links": {
    "self": { "href": "http://localhost:8080/api/users/1" },
    "all-users": { "href": "http://localhost:8080/api/users" },
    "delete": { "href": "http://localhost:8080/api/users/1" }
  }
}