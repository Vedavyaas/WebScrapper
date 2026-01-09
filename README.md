# WebScrapper

> CI/CD: A Jenkins declarative pipeline is configured and ready (see [Jenkinsfile](Jenkinsfile)) to build, test, package, create the Docker image, and deploy the containerized app.

Track product URLs with target prices, authenticate via OTP + JWT, and run scheduled background checks that update current prices and send email notifications when the price drops.

Highlights: scheduled + multithreaded price checks (`@Scheduled` + `@Async` with the `AsyncScrapper` thread pool), OTP email flow, JWT auth, and startup seeding for demo accounts + sample tracked products.

Spring Boot app that lets users track product URLs and target prices, then periodically checks for price changes and notifies users.

## Tech stack
- Java 17 (see `pom.xml`)
- Spring Boot (Web, Data JPA, Thymeleaf)
- Spring Security (JWT via OAuth2 Resource Server)
- H2 (in-memory)
- Spring Mail
- Microsoft Playwright (Java) for headless browser scraping (Chromium)

## Features
- Create account with OTP
- Authenticate with username/password and receive a JWT
- Add/list/update/delete tracked product URLs + target prices per user
- Scheduled price check (async) using `PriceRetrievalService` + Playwright-based `ScrapingService`
- Basic UI pages served by Thymeleaf (`/`, `/ui/dashboard`, `/ui/account`)

## Scraping (non-AI)
Price extraction is done by rendering the product page with Playwright (headless Chromium) and then locating the first currency-looking text (e.g., `$123.45`, `€99.99`, `₹1,299`).

Notes:
- First run may download Playwright browser binaries to `~/.cache/ms-playwright` (requires internet access).
- Some sites may block automation or render prices in ways that are not captured by the default locator.

## Local run
### Prerequisites
- JDK 17+
- Internet access on first run (Playwright browser download)

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

## Docker

The provided Dockerfile assumes the JAR is built first (no multi-stage build).

### Build image
```bash
mvn -B -DskipTests package
docker build -t web-scrapper-app:v1 .
```

### Run container
```bash
docker run -d --name web-scrapper-live -p 8081:9000 web-scrapper-app:v1
```

- App inside the container listens on port `9000` (see `server.port`), mapped to host `8081` above.
- Playwright browsers are preinstalled by the base image; downloads are skipped via env settings.

### Passing mail credentials (optional)
```bash
docker run -d --name web-scrapper-live \
  -e spring.mail.username="<gmail-username>" \
  -e spring.mail.password="<gmail-app-password>" \
  -p 8081:9000 web-scrapper-app:v1
```

You can also override any Spring property via env, e.g. `-e server.port=9000`.

## Jenkins CI/CD

This repo includes a declarative pipeline in `Jenkinsfile` with stages:

1. Fetch: clone `main` from the GitHub repo
2. Clean and test: `mvn clean test`
3. JAR package: `mvn package`
4. Image build: `docker build -t web-scrapper-app:v1 .`
5. Deploy image: runs the container mapping host `8081` to app port `9000`

### Jenkins prerequisites
- A global Maven installation named `maven` (Manage Jenkins → Global Tool Configuration)
- Docker available on the agent (daemon socket accessible)
- Agent user can run Docker commands

### Configure environment/ports
- To change the published port, edit the `docker run` command in `Jenkinsfile` (e.g., `-p 80:9000`).
- To set mail credentials or other Spring properties at deploy time, pass `-e` flags in the `docker run` step.

### Typical run outputs
- App URL (host): `http://<agent-host>:8081`
- H2 console: `http://<agent-host>:8081/h2-console`

## Ports summary
- Local dev (Maven): `http://localhost:9000`
- Docker/Jenkins default mapping: `http://localhost:8081` → container `9000`
