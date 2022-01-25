FROM openjdk:11
# https://docs.microsoft.com/en-us/azure/cognitive-services/speech-service/how-to-use-codec-compressed-audio-input-streams?tabs=linux%2Cdebian&pivots=programming-language-java#installing-gstreamer
RUN sudo apt install libgstreamer1.0-0 \
    gstreamer1.0-plugins-base \
    gstreamer1.0-plugins-good \
    gstreamer1.0-plugins-bad \
    gstreamer1.0-plugins-ugly
ENV APP_HOME=/app
WORKDIR $APP_HOME
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline
COPY . .
RUN ./mvnw package
CMD ["./mvnw", "spring-boot:run"]