#!/usr/bin/env bash
set -e
cd "$(dirname "$0")"

echo "==> Building..."
mvn package -DskipTests -q

echo "==> Starting server..."
java -jar server/target/server-1.0-SNAPSHOT.jar &
SERVER_PID=$!

cleanup() {
    echo ""
    echo "==> Stopping server (PID $SERVER_PID)..."
    kill "$SERVER_PID" 2>/dev/null
}
trap cleanup EXIT

sleep 0.5  # give the server a moment to bind the port

echo "==> Starting client..."
java -jar client/target/client-1.0-SNAPSHOT.jar
