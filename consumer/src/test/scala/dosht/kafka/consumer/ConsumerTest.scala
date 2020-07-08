package dosht.kafka.consumer

import cats.effect.{IO, Timer}
import dosht.kafka.api.GyroMessage
import dosht.kafka.cats.{EmbeddedKafkaResource, IOAsyncWordSpec}
import dosht.kafka.producer.Producer
import dosht.kafka.cats.KafkaContext

import scala.concurrent.duration._

class ConsumerTest extends IOAsyncWordSpec {
  import ConsumerTest._

  implicit val t: Timer[IO] = IO.timer(executionContext)

  "Application" must {
    "Ingest messages from gyro topic" in resource {
      for {
        _ <- EmbeddedKafkaResource.defaultResource
        producer <- Producer.resource(BootstrapServers, Topic)
        messageHandler = new TestMessageHandler
        config = ConsumerConfig(Servers, Topic, ConsumerGroup)
        testKafkaContext <- KafkaContext.resource(cs)
        consumer <- Consumer.resource(messageHandler, config, testKafkaContext)
      } yield for {
        _ <- producer.send(GyroMessage.defaultInstance)
        _ <- consumer.subscribe()
        consumerCancelToken <- consumer.poll.runCancelable(_ => IO.unit).toIO
        consumedMessages <- retry(500, 20.millis)(IO(messageHandler.queue.headOption))
        _ <- consumerCancelToken
      } yield {
        consumedMessages shouldBe Some(GyroMessage.defaultInstance)
      }
    }
  }
}
 object ConsumerTest {
   val BootstrapServers = s"localhost:${EmbeddedKafkaResource.DefaultPort}"
   val Servers = s"localhost:${EmbeddedKafkaResource.DefaultPort}"
   val ConsumerGroup = "test-consumer-group"
   val Topic = "test-topic"
 }
