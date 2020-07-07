#!/bin/bash

TOPIC_NAME=$1

cat <<EOF | kubectl apply -f -
apiVersion: v1
kind: Pod
metadata:
  name: "my-kafka-create-topic"
  annotations:
    "helm.sh/hook": test-success
spec:
  containers:
  - name: my-kafka-create-topic
    image: confluentinc/cp-kafka:5.0.1
    command:
    - sh
    - -c
    - |
      # Create the topic
      kafka-topics --zookeeper my-kafka-zookeeper:2181 --topic ${TOPIC_NAME} --create --partitions 1 --replication-factor 1 --if-not-exists && \
  restartPolicy: Never
EOF

