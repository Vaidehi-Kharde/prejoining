FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

COPY Sort.java .

RUN javac Sort.java

CMD ["java", "Sort"]
