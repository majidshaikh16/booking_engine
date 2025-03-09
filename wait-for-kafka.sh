#!/bin/sh

echo "Waiting for Kafka to be ready..."
while ! nc -z kafka 9092; do
  sleep 2
done
echo "Kafka is ready!"

echo "Creating Kafka topics..."
kafka-topics --create --topic payment-new --partitions 3 --replication-factor 1 --if-not-exists --bootstrap-server kafka:9092 || echo "Topic may already exist."
kafka-topics --create --topic payment-updates --partitions 3 --replication-factor 1 --if-not-exists --bootstrap-server kafka:9092 || echo "Topic may already exist."
echo "Kafka topic setup complete."
