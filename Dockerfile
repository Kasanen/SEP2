# FROM maven:3.9.6-eclipse-temurin-21 AS build

# ENV DISPLAY=host.docker.internal:0.0

# # Install only required libraries (NO MAVEN HERE)
# RUN apt-get update && \
#     apt-get install -y wget unzip libgtk-3-0 libgbm1 libx11-6 && \
#     apt-get clean

# # Download JavaFX SDK
# RUN wget https://download2.gluonhq.com/openjfx/21/openjfx-21_linux-x64_bin-sdk.zip -O /tmp/openjfx.zip && \
#     unzip /tmp/openjfx.zip -d /opt && \
#     rm /tmp/openjfx.zip

# WORKDIR /app

# # Copy project
# COPY pom.xml .
# COPY src ./src

# # ✅ NOW Maven works correctly
# RUN mvn clean package -DskipTests

# # Debug
# RUN ls -l target/

# CMD ["java", "--module-path", "/opt/javafx-sdk-21/lib", "--add-modules", "javafx.controls,javafx.fxml", "-jar", "target/fuelConsumption.jar"]

#### VER 2 ####
# # ---------- Build stage ----------
# FROM maven:3.9-eclipse-temurin-21 AS build
# WORKDIR /app
# COPY pom.xml .
# COPY src ./src
# RUN mvn clean package -DskipTests

# # Download JavaFX SDK
# RUN wget https://download2.gluonhq.com/openjfx/21/openjfx-21_linux-x64_bin-sdk.zip -O /tmp/openjfx.zip && \
#     unzip /tmp/openjfx.zip -d /opt && \
#     rm /tmp/openjfx.zip

# # ---------- Runtime stage ----------
# FROM eclipse-temurin:21-jre
# WORKDIR /app
# COPY --from=build /app/target/fuel_consumption.jar app.jar
# EXPOSE 8081
# ENTRYPOINT ["java", "-jar", "app.jar"]


#### VER 3 ####
# ---------- Build stage ----------
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

RUN apt-get update && apt-get install -y wget unzip && rm -rf /var/lib/apt/lists/*

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

RUN wget https://download2.gluonhq.com/openjfx/21/openjfx-21_linux-x64_bin-sdk.zip -O /tmp/openjfx.zip && \
    unzip /tmp/openjfx.zip -d /opt && \
    rm /tmp/openjfx.zip

# ---------- Runtime stage ----------
FROM eclipse-temurin:21-jre
WORKDIR /app

RUN apt-get update && apt-get install -y \
    libgtk-3-0 \
    libgbm1 \
    libx11-6 \
    libxrender1 \
    libxtst6 \
    libasound2 \
    && rm -rf /var/lib/apt/lists/*

ENV DISPLAY=host.docker.internal:0.0

COPY --from=build /app/target/fuel_consumption.jar app.jar
COPY --from=build /opt/javafx-sdk-21 /opt/javafx-sdk-21

ENTRYPOINT ["java", "--module-path", "/opt/javafx-sdk-21/lib", "--add-modules", "javafx.controls,javafx.fxml", "-jar", "app.jar"]