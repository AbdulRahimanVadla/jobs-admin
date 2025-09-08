# Step 1: Build the WAR using Maven
FROM maven:3.9.9-eclipse-temurin-21 AS builder
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Step 2: Run it with Tomcat
FROM tomcat:10.1-jdk21
WORKDIR /usr/local/tomcat

# Remove default ROOT app
RUN rm -rf webapps/ROOT

# Copy the WAR built in step 1
COPY --from=builder /app/target/jobs-admin.war webapps/ROOT.war

# Expose Railway's port
EXPOSE 8080

# Start Tomcat
CMD ["catalina.sh", "run"]
