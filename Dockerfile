# =================================================================
# ETAPA 1: Construcción (Build Stage) con JDK 21
# =================================================================
FROM eclipse-temurin:21-jdk-jammy AS build

# Establecemos el directorio de trabajo dentro del contenedor
WORKDIR /app

# Copiamos el wrapper de Maven y el pom.xml para descargar dependencias
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

# Descargamos todas las dependencias del proyecto
RUN chmod +x ./mvnw
RUN ./mvnw dependency:go-offline

# Copiamos el resto del código fuente del proyecto
COPY src ./src

# Compilamos el proyecto y generamos el .jar, omitiendo los tests
RUN  ./mvnw clean install -DskipTests


# =================================================================
# ETAPA 2: Ejecución (Run Stage) con JRE 21
# =================================================================
FROM eclipse-temurin:21-jre-jammy

# Establecemos el directorio de trabajo
WORKDIR /app

# Copiamos solo el archivo .jar generado en la etapa anterior
COPY --from=build /app/target/*.jar app.jar

# Exponemos el puerto en el que corre la aplicación (por defecto 8080)
EXPOSE 8080

# Comando para ejecutar la aplicación cuando se inicie el contenedor
ENTRYPOINT ["java", "-jar", "app.jar"]