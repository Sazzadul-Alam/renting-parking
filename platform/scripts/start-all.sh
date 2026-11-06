#!/bin/bash
set -e

echo "==================================================="
echo "Starting Microservices Platform"
echo "==================================================="

if [ -z "$MONGODB_URI" ]; then
    echo "[ERROR] MONGODB_URI environment variable is not set!"
    echo "Please export MONGODB_URI before running start-all.sh"
    exit 1
fi

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )/.." >/dev/null 2>&1 && pwd )"
cd "$DIR"

echo "[1/5] Starting Service Registry on port 8761..."
./mvnw -pl service-registry spring-boot:run &
REGISTRY_PID=$!

until curl -s -f http://localhost:8761/actuator/health > /dev/null 2>&1; do
    sleep 2
done
echo "[1/5] Service Registry is UP!"

echo "[2/5] Starting OAuth2 Service on port 9000..."
./mvnw -pl oauth2-service spring-boot:run &
OAUTH2_PID=$!

until curl -s -f http://localhost:9000/actuator/health > /dev/null 2>&1; do
    sleep 2
done
echo "[2/5] OAuth2 Service is UP!"

echo "[3/5] Starting Web Service on port 8081..."
./mvnw -pl web-service spring-boot:run &
WEB_PID=$!

until curl -s -f http://localhost:8081/actuator/health > /dev/null 2>&1; do
    sleep 2
done
echo "[3/5] Web Service is UP!"

echo "[4/5] Starting API Gateway on port 8080..."
./mvnw -pl api-gateway spring-boot:run &
GATEWAY_PID=$!

until curl -s -f http://localhost:8080/actuator/health > /dev/null 2>&1; do
    sleep 2
done
echo "[4/5] API Gateway is UP!"

echo "[5/5] Starting Angular Frontend on port 4200..."
cd frontend
npm start &
FRONTEND_PID=$!

echo "==================================================="
echo "All services started successfully!"
echo "Frontend:         http://localhost:4200"
echo "API Gateway:      http://localhost:8080"
echo "OAuth2 Service:   http://localhost:9000"
echo "Eureka Registry:  http://localhost:8761"
echo "==================================================="

