FROM openjdk:21

# Make a directory for the instance
RUN mkdir /instance
WORKDIR /instance

# Copy the template instance to the instance directory
COPY /template-instance /instance

# Run the jar file
CMD ["java", "-Xms1g", "-Xmx6g", "-jar", "server.jar", "nogui"]