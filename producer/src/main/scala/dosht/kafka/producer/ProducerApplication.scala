package dosht.kafka.producer

import cats.effect.{ConcurrentEffect, ExitCode, IO, IOApp}
import dosht.kafka.api.GyroMessage

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor}

object ProducerApplication extends IOApp {

  implicit private val ec: ExecutionContextExecutor = ExecutionContext.global
  private val cs = IO.contextShift(ec)
  implicit private val concurrentEffect: ConcurrentEffect[IO] = IO.ioConcurrentEffect(cs)

  override def run(args: List[String]): IO[ExitCode] = for {
    config <- ProducerConfig.loadIO("application.conf")
    exitCode <- startProducer(config: ProducerConfig).as(ExitCode.Success)
  } yield exitCode

  def startProducer(config: ProducerConfig): IO[Unit] =
    Producer
      .resource(config.bootstrapServers, config.topic)
      .use { producer =>
        IO(println("Producer started!")) *>
        producer.send(GyroMessage(2L, 1L, 1L ,1L, 1L)) *>
        producer.send(GyroMessage(3L, 1L, 1L ,1L, 1L)) *>
        producer.send(GyroMessage(4L, 1L, 1L ,1L, 1L)) *>
        producer.send(GyroMessage(5L, 1L, 1L ,1L, 1L)) *>
        IO(println("Messages sent ------------>")) *>
        producer.close
      }
}
