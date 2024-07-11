FROM alpine/git
WORKDIR /app
RUN git clone https://github.com/IvanMTD/beartracker-web.git

FROM maven
WORKDIR /app
COPY --from=0 /app/beartracker-web /app
RUN mvn clean package -DskipTests

FROM bellsoft/liberica-openjdk-alpine
EXPOSE 8040 8080
WORKDIR /app
COPY --from=1 /app/target/beartracker-web-0.0.1-SNAPSHOT.jar /app
#CMD ["java","-Xms64m","-Xmx900m","-jar","infosec-1.0.0.jar"]
CMD ["java","-jar","beartracker-web-0.0.1-SNAPSHOT.jar"]
