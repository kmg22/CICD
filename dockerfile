FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
EXPOSE 8080

COPY build/libs/ /app/libs/

RUN set -eux; \
    JAR="$(ls /app/libs/*.jar | grep -v plain | head -n 1)"; \
    cp "$JAR" /app/app.jar; \
    rm -rf /app/libs

ENTRYPOINT ["java","-jar","/app/app.jar"]