FROM gradle:7.5.0-jdk17-alpine AS builder

# add pom.xml and source code
COPY ./build.gradle build.gradle
COPY ./src src/

# package jar
RUN mvn clean package -DskipTests=true

# Second stage: minimal runtime environment
From openjdk:11-jre-slim

# copy jar from the first stage
COPY --from=builder target/meetup-0.0.1-SNAPSHOT.jar meetup-0.0.1-SNAPSHOT.jar

EXPOSE 8080

ENV SPRING_PROFILES_ACTIVE=
ENV DB_HOST=
ENV DB_NAME=
ENV DB_USERNAME=
ENV DB_PASSWORD=

CMD ["java", "-jar", "-Dspring.profiles.active=${SPRING_PROFILES_ACTIVE}", "meetup-0.0.1-SNAPSHOT.jar"]