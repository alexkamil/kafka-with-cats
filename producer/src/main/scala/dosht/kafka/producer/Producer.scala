package dosht.kafka.producer

import java.util.Properties

import cats.effect.{IO, Resource}
import dosht.kafka.api.GyroMessage
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord, RecordMetadata, ProducerConfig => KafkaProducerConfig}
import org.apache.kafka.common.serialization.StringSerializer

import scala.concurrent.duration.{FiniteDuration, _}
import scala.util.Random

class Producer(bootstrapServers: String, topic: String) {

  val ProducerPublishTimeout: FiniteDuration = 10.seconds

  val properties = new Properties()
  properties.put(KafkaProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers)

  val kafkaProducer = new KafkaProducer[String, GyroMessage](properties, new StringSerializer, new ProtobufSerializer[GyroMessage])

  def close: IO[Unit] = IO(kafkaProducer.close())

  def send(message: GyroMessage): IO[RecordMetadata] =
    IO(kafkaProducer
      .send(new ProducerRecord(topic, Random.nextString(5), message))
      .get(ProducerPublishTimeout.length, ProducerPublishTimeout.unit))

}

object Producer {
  def resource(bootstrapServers: String, topic: String): Resource[IO, Producer] =
    Resource.make(IO(new Producer(bootstrapServers, topic)))(_.close)

}
