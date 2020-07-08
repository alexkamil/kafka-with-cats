package dosht.kafka.cats

import cats.effect.{IO, Resource}
import net.manub.embeddedkafka.{EmbeddedK, EmbeddedKafka, EmbeddedKafkaConfig}

object EmbeddedKafkaResource extends PortsUtils {

  val DefaultPort: Int = 9092

  def start(port: Int = nextAvailablePort): IO[EmbeddedK] =
    IO(EmbeddedKafka.start()(EmbeddedKafkaConfig(kafkaPort = port, zooKeeperPort = nextAvailablePort)))

  def stop(kafka: EmbeddedK): IO[Unit] = IO(kafka.stop(true))

  def resource(port: Int = nextAvailablePort): Resource[IO, EmbeddedK] = Resource.make(start(port))(stop)

  def defaultResource: Resource[IO, EmbeddedK] = Resource.make(start(DefaultPort))(stop)
}
