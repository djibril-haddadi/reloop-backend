# ÉTAPE 1 : La Construction (Build)
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# 🧹 NETTOYAGE CHIRURGICAL 🧹
# On va dans le dossier target
# 1. On supprime le jar "plain" (le parasite)
# 2. On renomme le seul jar restant en "app.jar" pour être sûr du nom
RUN cd target && rm -f *-plain.jar && mv *.jar final.jar

# ÉTAPE 2 : L'Exécution (Run)
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
# Maintenant, on copie le fichier dont on connait le nom exact !
COPY --from=build /app/target/final.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
