package dosht.kafka.consumer

import java.time.Duration
import java.util.{Collections, Properties}

import cats.effect.{IO, Resource, Timer}
import org.apache.kafka.clients.consumer.{ConsumerRecord, KafkaConsumer, OffsetResetStrategy, ConsumerConfig => KafkaConsumerConfig}
import org.apache.kafka.common.serialization.{ByteArrayDeserializer, StringDeserializer}

import scala.collection.JavaConverters._
import scala.concurrent.ExecutionContext

class Consumer(messageHandler: MessageHandler, config: ConsumerConfig, kafkaContext: KafkaContext) {
  implicit val timer: Timer[IO] = IO.timer(ExecutionContext.global)

  val props = new Properties()
  props.put(KafkaConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, config.servers)
  props.put(KafkaConsumerConfig.GROUP_ID_CONFIG, config.consumerGroup)
  props.put(KafkaConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false)
  props.put(KafkaConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, Int.MaxValue)
  props.put(KafkaConsumerConfig.AUTO_OFFSET_RESET_CONFIG, OffsetResetStrategy.EARLIEST.toString.toLowerCase)

  private lazy val kafkaConsumer: KafkaConsumer[String, String] = {
    new KafkaConsumer[String, String](props, new StringDeserializer, new StringDeserializer)
  }

  def close: IO[Unit] = kafkaContext ~> kafkaConsumer.close()

  def subscribe(): IO[Unit] = kafkaContext ~> kafkaConsumer.subscribe(Collections.singletonList(config.topic))

  def consumer: IO[Unit] = for {
    rawMessages <- kafkaContext ~> kafkaConsumer.poll(Duration.ofMillis(1000)).iterator().asScala.toList
    messages = rawMessages.map(_.value())
    _ <- messageHandler.handleMessage(messages)
    _ <- consumer
  } yield ()

}

object Consumer {
  def resource(messageHandler: MessageHandler, config: ConsumerConfig, context: KafkaContext): Resource[IO, Consumer] =
    Resource.make(IO(new Consumer(messageHandler, config, context)))(_.close)
}
