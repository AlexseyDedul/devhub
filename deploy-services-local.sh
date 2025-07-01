#!/bin/bash

KEYCLOAK_NAME="keycloak"
SERVICE_NAME="auth-service"
USER_SERVICE_NAME="user-service"
GATEWAY_SERVICE_NAME="gateway-service"

## auth service
cd $SERVICE_NAME

echo "🔨 Building (${SERVICE_NAME}) JAR..."
mvn clean package -DskipTests || { echo "❌ Build error JAR"; exit 1; }

echo "🐳 Build Docker image..."
docker buildx build --load -t "${SERVICE_NAME}:latest" . || { echo "❌ Building error"; exit 1; }
cd ..

## user service
cd $USER_SERVICE_NAME

echo "🔨 Building (${USER_SERVICE_NAME}) JAR..."
mvn clean package -DskipTests || { echo "❌ Build error JAR"; exit 1; }

echo "🐳 Build Docker image..."
docker buildx build --load -t "${USER_SERVICE_NAME}:latest" . || { echo "❌ Building error"; exit 1; }
cd ..

## gateway service
#cd $GATEWAY_SERVICE_NAME
#
#echo "🔨 Building (${GATEWAY_SERVICE_NAME}) JAR..."
#mvn clean package -DskipTests || { echo "❌ Build error JAR"; exit 1; }
#
#echo "🐳 Build Docker image..."
#docker buildx build --load -t "${GATEWAY_SERVICE_NAME}:latest" . || { echo "❌ Building error"; exit 1; }
#cd ..

echo "🚀 Apply manifests of Kubernetes..."
kubectl apply -f k8s/$KEYCLOAK_NAME/ || { echo "❌ Applying error"; exit 1; }
kubectl apply -f k8s/$SERVICE_NAME/ || { echo "❌ Applying error"; exit 1; }
kubectl apply -f k8s/$USER_SERVICE_NAME/ || { echo "❌ Applying error"; exit 1; }
#kubectl apply -f k8s/$GATEWAY_SERVICE_NAME/ || { echo "❌ Applying error"; exit 1; }

echo "✅ Done. Pods statuses:"
kubectl get pods -l app=$SERVICE_NAME
kubectl get pods -l app=$KEYCLOAK_NAME
kubectl get pods -l app=$USER_SERVICE_NAME
#kubectl get pods -l app=$GATEWAY_SERVICE_NAME
