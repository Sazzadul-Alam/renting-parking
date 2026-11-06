@echo off
setlocal enabledelayedexpansion

echo ===================================================
echo Starting Microservices Platform
echo ===================================================

REM Check MONGODB_URI
if "%MONGODB_URI%"=="" (
    echo [ERROR] MONGODB_URI environment variable is not set!
    echo Please set MONGODB_URI before running start-all.bat.
    echo Example: set MONGODB_URI=mongodb+srv://user:pass@cluster.mongodb.net/?retryWrites=true^&w=majority
    exit /b 1
)

cd /d "%~dp0\.."

echo [1/5] Starting Service Registry on port 8761...
start "service-registry" cmd /k "mvnw.cmd -pl service-registry spring-boot:run"

:wait_registry
curl -s -f http://localhost:8761/actuator/health >nul 2>&1
if errorlevel 1 (
    timeout /t 2 >nul
    goto wait_registry
)
echo [1/5] Service Registry is UP!

echo [2/5] Starting OAuth2 Service on port 9000...
start "oauth2-service" cmd /k "mvnw.cmd -pl oauth2-service spring-boot:run"

:wait_oauth2
curl -s -f http://localhost:9000/actuator/health >nul 2>&1
if errorlevel 1 (
    timeout /t 2 >nul
    goto wait_oauth2
)
echo [2/5] OAuth2 Service is UP!

echo [3/5] Starting Web Service on port 8081...
start "web-service" cmd /k "mvnw.cmd -pl web-service spring-boot:run"

:wait_web
curl -s -f http://localhost:8081/actuator/health >nul 2>&1
if errorlevel 1 (
    timeout /t 2 >nul
    goto wait_web
)
echo [3/5] Web Service is UP!

echo [4/5] Starting API Gateway on port 8080...
start "api-gateway" cmd /k "mvnw.cmd -pl api-gateway spring-boot:run"

:wait_gateway
curl -s -f http://localhost:8080/actuator/health >nul 2>&1
if errorlevel 1 (
    timeout /t 2 >nul
    goto wait_gateway
)
echo [4/5] API Gateway is UP!

echo [5/5] Starting Angular Frontend on port 4200...
cd frontend
start "frontend" cmd /k "npm start"

echo ===================================================
echo All services started successfully!
echo Frontend:         http://localhost:4200
echo API Gateway:      http://localhost:8080
echo OAuth2 Service:   http://localhost:9000
echo Eureka Registry:  http://localhost:8761
echo ===================================================

