# WebScrapper

Track product URLs with target prices, authenticate via OTP + JWT, and run scheduled background checks that update current prices and send email notifications when the price drops.

Highlights: scheduled + multithreaded price checks (`@Scheduled` + `@Async` with the `AsyncScrapper` thread pool), OTP email flow, JWT auth, and startup seeding for demo accounts + sample tracked products.

Spring Boot app that lets users track product URLs and target prices, then periodically checks for price changes and notifies users.

## Tech stack
- Java 17 (see `pom.xml`)
- Spring Boot (Web, Data JPA, Thymeleaf)
- Spring Security (JWT via OAuth2 Resource Server)
- H2 (in-memory)
- Spring Mail
- Spring AI (Google GenAI)

## Features
- Create account with OTP
- Authenticate with username/password and receive a JWT
- Add/list/update/delete tracked product URLs + target prices per user
- Scheduled price check (async) using `PriceRetrievalService`
- Basic UI pages served by Thymeleaf (`/`, `/ui/dashboard`, `/ui/account`)

## Local run
### Prerequisites
- JDK 17+

### Start
```bash
./mvnw spring-boot:run
```

App runs on: `http://localhost:9000`

### H2 console
- URL: `http://localhost:9000/h2-console`
- JDBC URL: `jdbc:h2:mem:scrap_db`
- Username: `user`
- Password: `sa`

## Default seeded accounts
Seeding is controlled by `app.seed.enabled` in `src/main/resources/application.properties`.

Default emails/passwords (dev only):
- User: `user@webscrapper.local` / `user123`
- Admin: `admin@webscrapper.local` / `admin123`

The project also seeds example `ProductScrapEntity` rows for these accounts at startup.

## API (quick reference)
### Auth
- `POST /authenticate`
  - Body: `{ "email": "user@webscrapper.local", "password": "user123" }`
  - Returns: `{ "token": "<jwt>" }`

Use the JWT for authenticated endpoints:
- Header: `Authorization: Bearer <jwt>`

### Product tracking
- `POST /post/url?url=<url>&targetPrice=<decimal>`
- `GET /get/url`
- `PUT /change/url?id=<id>&url=<url>`
- `PUT /change/price?id=<id>&newPrice=<decimal>`
- `DELETE /delete/scrap?id=<id>`

### OTP / account
- `GET /get/OTP?email=<email>`
- `POST /create/account?OTP=<otp>` (body: `{ "email": "...", "password": "..." }`)
- `PUT /forget/password?email=<email>&OTP=<otp>&newPassword=<password>`

## Scheduler / async
- `PriceRetrievalService.fetchPrice()` runs every 10 minutes.
- Async executor bean name: `AsyncScrapper` (see `AsyncConfiguration`).
