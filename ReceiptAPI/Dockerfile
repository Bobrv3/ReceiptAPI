FROM gradle:7.5.0-jdk17-alpine AS builder

# add pom.xml and source code
COPY --chown=gradle:gradle . /home/gradle/src

WORKDIR /home/gradle/src

# package jar
RUN gradle build -x test --no-daemon

# Second stage: minimal runtime environment
From eclipse-temurin:17-jre-alpine

RUN mkdir /app

# copy jar from the first stage
COPY --from=builder /home/gradle/src/build/libs/*.jar /app/checkApp-0.0.1-SNAPSHOT.jar

EXPOSE 8080

ENV SPRING_PROFILES_ACTIVE=
ENV DB_HOST=
ENV DB_NAME=
ENV DB_USERNAME=
ENV DB_PASSWORD=

CMD ["java", "-jar", "-Dspring.profiles.active=${SPRING_PROFILES_ACTIVE}", "/app/checkApp-0.0.1-SNAPSHOT.jar"]