FROM maven:3.9.9-eclipse-temurin-17 AS deps
WORKDIR /app
COPY pom.xml .

RUN --mount=type=cache,target=/root/.m2 mvn -B -DskipTests dependency:go-offline

FROM deps AS build
COPY src ./src
RUN --mount=type=cache,target=/root/.m2 mvn -B -DskipTests package


FROM mcr.microsoft.com/playwright/java:v1.57.0-jammy AS runtime
WORKDIR /app

ENV JAVA_OPTS="" \
    SPRING_PROFILES_ACTIVE=prod \
    PLAYWRIGHT_BROWSERS_PATH=/ms-playwright \
    PLAYWRIGHT_SKIP_BROWSER_DOWNLOAD=1

RUN addgroup --system spring && adduser --system --ingroup spring spring

COPY --from=build /app/target/*.jar /app/app.jar

USER spring:spring
EXPOSE 8080
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]
