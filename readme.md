# 1.Docker Images prepare
## 1.1 Config service
    ./gradlew config-api:bootBuildImage
## 1.2 Eureka service
    ./gradlew eureka-api:bootBuildImage
## 1.3 Gateway service
    ./gradlew gateway-api:bootBuildImage
## 1.4 User Service
    ./gradlew user-api:openApiGenerate
    ./gradlew user-api:bootBuildImage
## 1.5 Feed Service
    ./gradlew feed-api:openApiGenerate
    ./gradlew feed-api:bootBuildImage
## 1.6 Payment Service
    ./gradlew payment-api:openApiGenerate
    ./gradlew payment-api:bootBuildImage
## 1.7 Notification Service
    ./gradlew notification-api:openApiGenerate
    ./gradlew notification-api:bootBuildImage
# 2.Docker Compose
    docker-compose up -d
# 3.Third-Party Services Prepare
## 3.1 Keycloak
 Open http://localhost:7001/auth/admin/ in browser, login with username: admin, password: admin
 Create a new client: social. 
