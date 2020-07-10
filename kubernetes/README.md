Running Kafka on Kubernetes
If you are familiar with Kubernetes and have Minikube installed already on your machine, then an easy way to get started with Kafka is to install it to your Minikube. I prepared a yaml file called kafka-with-zookeeper.k8s.yaml which I generated from a Helm chart, but I preferred to store it as plain yaml file to get rid an extra dependencies.

To install Kafka and Zookeeper on K8s, do the following:

```bash
kubectl create namespace kafka
kubectl apply -n kafka -f kafka-with-zookeeper.k8s.yaml
```
