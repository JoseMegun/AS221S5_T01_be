# Usa una imagen base de OpenJDK 17
FROM openjdk:17

# Directorio de trabajo dentro del contenedor
WORKDIR /app

# Copia el archivo JAR de tu aplicación al contenedor
COPY target/ModerationText-0.0.1-SNAPSHOT.jar app.jar

# Exponer el puerto 8086 en el contenedor
EXPOSE 8086

# Comando para ejecutar la aplicación al iniciar el contenedor
CMD ["java", "-jar", "app.jar"]
