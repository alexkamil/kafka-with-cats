# kafka-with-cats
This repo provides a functional approach for using Kafka java driver with Scala Cats library and wrapping it in Cats IO application.

The code is explained here: https://medium.com/@mou.abdelhamid/how-functional-programming-makes-working-with-kafka-easier-606d1156ea40?sk=20666df0a63d35748a154e98a9ca19e2

## Running everything:

```bash
sbt
kafkaStart
project/producer run
project/consumer run
```

## Testing

```bash
sbt test it:test
```
