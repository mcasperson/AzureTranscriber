FROM openjdk:11 AS build
# https://docs.microsoft.com/en-us/azure/cognitive-services/speech-service/how-to-use-codec-compressed-audio-input-streams?tabs=linux%2Cdebian&pivots=programming-language-java#installing-gstreamer
RUN apt update && \
    apt install -y libgstreamer1.0-0 \
    gstreamer1.0-plugins-base \
    gstreamer1.0-plugins-good \
    gstreamer1.0-plugins-bad \
    gstreamer1.0-plugins-ugly && \
    rm -rf /var/lib/apt/lists/*
ENV APP_HOME=/app
WORKDIR $APP_HOME
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline
COPY . .
RUN ./mvnw package
FROM openjdk:11-jre
COPY --from=build /app/target/azuretranscriber-0.0.1-SNAPSHOT.jar /app/azuretranscriber-0.0.1-SNAPSHOT.jar
CMD ["java", "/app/azuretranscriber-0.0.1-SNAPSHOT.jar"]