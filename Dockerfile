# Use an official OpenJDK image as the base image
FROM openjdk:11-jdk

# Set the working directory
WORKDIR /target

ARG JAR_FILE=target/*.jar

# Copy the JAR file from the build output to the image
COPY target/school-managment-b181-0.0.1-SNAPSHOT.jar app.jar

# Start the application
CMD ["java", "-jar", "app.jar"]


# build image
# docker build -t school-management-demo .

# tag imege
# docker tag school-management-demo sametyaprak/school-management-demo:latest

# push image
# docker push sametyaprak/school-management-demo:latest

# run image
# docker run -p 8080:8080 school-management-demo

# to log-in to docker
# docker login


