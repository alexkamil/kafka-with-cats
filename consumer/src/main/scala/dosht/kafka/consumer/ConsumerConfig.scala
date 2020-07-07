package dosht.kafka.consumer

import cats.effect.IO
import pureconfig.ConfigSource
import pureconfig.generic.auto._

object ConsumerConfig {
  def loadIO(resourceBasename: String): IO[ConsumerConfig] =
    IO(
      ConfigSource
        .resources(resourceBasename)
        .at("kafka-consumer")
        .loadOrThrow[ConsumerConfig])
}

case class ConsumerConfig(servers: String, topic: String, consumerGroup: String)
