#!/bin/bash

KEYCLOAK_NAME="keycloak"
SERVICE_NAME="auth-service"
USER_SERVICE_NAME="user-service"
GATEWAY_SERVICE_NAME="gateway-service"

echo "🚀 Deleting manifests of Kubernetes..."
kubectl delete -f k8s/$KEYCLOAK_NAME || { echo "❌ Deleting error"; exit 1; }
kubectl delete -f k8s/$SERVICE_NAME || { echo "❌ Deleting error"; exit 1; }
kubectl delete -f k8s/$USER_SERVICE_NAME || { echo "❌ Deleting error"; exit 1; }
#kubectl delete -f k8s/$GATEWAY_SERVICE_NAME || { echo "❌ Deleting error"; exit 1; }
