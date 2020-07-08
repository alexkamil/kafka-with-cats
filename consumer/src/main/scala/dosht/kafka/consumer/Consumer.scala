package dosht.kafka.consumer

import java.time.Duration
import java.util.{Collections, Properties}

import cats.effect.{IO, Resource, Timer}
import dosht.kafka.api.GyroMessage
import dosht.kafka.consumer.KafkaContext
import org.apache.kafka.clients.consumer.{ConsumerRecord, KafkaConsumer, OffsetResetStrategy, ConsumerConfig => KafkaConsumerConfig}
import org.apache.kafka.common.serialization.{ByteArrayDeserializer, StringDeserializer}
import scalapb.GeneratedMessageCompanion

import scala.collection.JavaConverters._
import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success, Try}

class Consumer(messageHandler: MessageHandler, config: ConsumerConfig, kafkaContext: KafkaContext) {
  implicit val timer: Timer[IO] = IO.timer(ExecutionContext.global)
  private val parser = implicitly[GeneratedMessageCompanion[GyroMessage]]

  val props = new Properties()
  props.put(KafkaConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, config.servers)
  props.put(KafkaConsumerConfig.GROUP_ID_CONFIG, config.consumerGroup)
  props.put(KafkaConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false)
  props.put(KafkaConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, Int.MaxValue)
  props.put(KafkaConsumerConfig.AUTO_OFFSET_RESET_CONFIG, OffsetResetStrategy.EARLIEST.toString.toLowerCase)

  private lazy val kafkaConsumer: KafkaConsumer[String, Array[Byte]] = {
    new KafkaConsumer[String, Array[Byte]](props, new StringDeserializer, new ByteArrayDeserializer)
  }

  def close: IO[Unit] = kafkaContext ~> kafkaConsumer.close()

  def subscribe(): IO[Unit] = kafkaContext ~> kafkaConsumer.subscribe(Collections.singletonList(config.topic))

  def poll: IO[Unit] = for {
    rawMessages <- kafkaContext ~> kafkaConsumer.poll(Duration.ofMillis(1000)).iterator().asScala.toList
    (errors, parsedMessages) = parseMessages(rawMessages)
    _ <- logErrors(errors)
    _ <- countErrors(errors)
    _ <- messageHandler.handleMessage(parsedMessages)
    _ <- poll
  } yield ()

  private def parseMessages(rawMessages: List[ConsumerRecord[String, Array[Byte]]]): (List[String], List[GyroMessage]) =
    rawMessages.map(parseMessage).foldLeft((List.empty[String], List.empty[GyroMessage])) {
      case ((errors, tus), Right(tu)) => (errors, tus :+ tu)
      case ((errors, tus), Left(e)) => (errors :+ e, tus)
    }

  private def parseMessage(consumerRecord: ConsumerRecord[String, Array[Byte]]): Either[String, GyroMessage] =
    Try(parser.parseFrom(consumerRecord.value)) match {
      case Success(value) => Right(value)
      case Failure(exception) => Left(exception.getMessage)
    }

  private def logErrors(errors: List[String]): IO[Unit] =
    if (errors.nonEmpty) IO(println(s"error --------> $errors")) else IO.unit

  private def countErrors(errors: List[String]): IO[Unit] = IO.unit

}

object Consumer {
  def resource(messageHandler: MessageHandler, config: ConsumerConfig, context: KafkaContext): Resource[IO, Consumer] =
    Resource.make(IO(new Consumer(messageHandler, config, context)))(_.close)
}
