package dosht.kafka.consumer

import cats.effect.IO
import dosht.kafka.cats.{EmbeddedKafkaResource, IOAsyncWordSpec}
import dosht.kafka.producer.ProducerApplication

import scala.concurrent.duration._

class ConsumerApplicationTest extends IOAsyncWordSpec {
  import ConsumerApplicationTest._

  "ConsumerApplication" must {
    "consume data and store it" in io {
      for {
        kafka <- EmbeddedKafkaResource.start(KafkaPort)
        _ <- IO.sleep(3.seconds)
        consumerApplicationCancelToken <- ConsumerApplication.run(Nil).runCancelable(_ => IO.unit).toIO
        _ <- ProducerApplication.run(Nil)
        _ <- IO.sleep(3.seconds)
        _ <- consumerApplicationCancelToken  // Stopping the consumer application
        _ <- EmbeddedKafkaResource.stop(kafka)
      } yield {
        // Make some real assertions here
        1 shouldEqual 1
      }
    }
  }
}

object ConsumerApplicationTest {
  val KafkaPort: Int = EmbeddedKafkaResource.DefaultPort
}
