# ÉTAPE 1 : La Construction (Build)
# On utilise une image avec Maven pour compiler le code
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
# On compile le projet en sautant les tests (car pas de base de données dispo pendant le build)
RUN mvn clean package -DskipTests

# ÉTAPE 2 : L'Exécution (Run)
# On prend une image très légère juste pour lancer Java
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
# On récupère uniquement le fichier .jar généré à l'étape 1
COPY --from=build /app/target/*.jar app.jar

# On expose le port 8080
EXPOSE 8080

# La commande de démarrage
ENTRYPOINT ["java", "-jar", "app.jar"]
