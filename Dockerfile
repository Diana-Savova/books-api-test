# =============================================================================

# Stage 1: dependency cache layer

#

# Resolving ~200MB of Maven dependencies on every CI run is slow. We do it

# once in a dedicated stage that only re-runs if pom.xml changes.

# =============================================================================

FROM maven:3.9.9-eclipse-temurin-17 AS deps

WORKDIR /build

COPY pom.xml .

# Pre-fetch test deps + the Allure CLI bundle used by allure-maven:report

RUN mvn -B -q dependency:go-offline && \

    mvn -B -q io.qameta.allure:allure-maven:resolve || true


# =============================================================================

# Stage 2: runtime image

# =============================================================================

FROM maven:3.9.9-eclipse-temurin-17

WORKDIR /app

# Reuse the resolved dependency cache from the previous stage

COPY --from=deps /root/.m2 /root/.m2

# Copy project files

COPY pom.xml .

COPY src ./src

COPY docker-entrypoint.sh /usr/local/bin/docker-entrypoint.sh

RUN chmod +x /usr/local/bin/docker-entrypoint.sh

# Sensible defaults - overridable at `docker run` via -e

ENV API_BASE_URL=https://fakerestapi.azurewebsites.net \

    API_BASE_PATH=/api/v1

# Reports land here; mount a host volume to read them out:

#   docker run -v $(pwd)/reports:/app/target ...

VOLUME ["/app/target"]

# Runs `mvn test` then `mvn allure:report`. See docker-entrypoint.sh for details.

ENTRYPOINT ["/usr/local/bin/docker-entrypoint.sh"]
