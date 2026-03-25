FROM eclipse-temurin:17
COPY target/restaurantesAuthServer-0.0.1-SNAPSHOT.jar rewards-auth.jar
CMD ["java", "-jar", "/rewards-auth.jar"]