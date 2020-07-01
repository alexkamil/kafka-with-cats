package dosht.kafka.producer

import cats.effect.IO
import pureconfig.ConfigSource
import pureconfig.generic.auto._

object ProducerConfig {
  def loadIO(resourceBasename: String): IO[ProducerConfig] =
    IO(
      ConfigSource
        .resources(resourceBasename)
        .at("kafka-producer")
        .loadOrThrow[ProducerConfig])
}
case class ProducerConfig(bootstrapServers: String, topic: String)
