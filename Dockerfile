FROM mcr.microsoft.com/playwright/java:v1.40.0-jammy

WORKDIR /app

COPY target/*.jar app.jar

ENV PLAYWRIGHT_SKIP_BROWSER_DOWNLOAD=1
ENV PLAYWRIGHT_BROWSERS_PATH=/ms-playwright

RUN addgroup --system spring && adduser --system --ingroup spring spring
USER spring:spring

ENTRYPOINT ["java", "-jar", "app.jar"]