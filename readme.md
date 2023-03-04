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

# 2. Configuration

## 2.1 Env File

create .env File in root directory and set next variables:

    GITHUB_USER=your github username
    GITHUB_TOKEN=your github token
    KEYCLOAK_SECRET=your keycloak client secret

## 2.2 Keycloak

run keycloak and postgres:

    docker-compose up -d keycloak postgres

Open http://localhost:7001/admin/master/console/ in browser, login with username: admin, password: admin
Create a new client: social.
enable Client authentication and Authorization
Set Valid redirect URIs * and Web Origins *.

open social client and copy client secret from Credentials.

open Client scopes and click social-dedicated. Click Add mapper and choose By configuration.
then choose User Attribute. set userId in name, user Attribute and Token Claim Name
Enable add to ID Token and Add to Access Token, then save.

paste client secret to .env file.
