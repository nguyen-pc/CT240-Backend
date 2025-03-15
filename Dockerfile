# Sử dụng OpenJDK làm base image
FROM openjdk:17-jdk-slim

# Định nghĩa biến môi trường
ENV SPRING_PROFILES_ACTIVE=prod
ENV DB_URL=jdbc:mysql://db:3306/formhub
ENV DB_USER=root
ENV DB_PASSWORD=123456

# Tạo thư mục app trong container
WORKDIR /app

# Sao chép file JAR từ thư mục build/libs
COPY build/libs/*.jar app.jar

# Mở cổng ứng dụng
EXPOSE 8080

# Lệnh chạy ứng dụng
ENTRYPOINT ["java", "-jar", "app.jar"]
