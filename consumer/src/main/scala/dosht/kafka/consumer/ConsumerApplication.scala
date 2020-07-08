package dosht.kafka.consumer

import cats.effect.{ConcurrentEffect, ExitCode, IO, IOApp, Resource}
import dosht.kafka.cats.KafkaContext

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor}

object ConsumerApplication extends IOApp {

  implicit private val ec: ExecutionContextExecutor = ExecutionContext.global
  private val cs = IO.contextShift(ec)
  implicit private val concurrentEffect: ConcurrentEffect[IO] = IO.ioConcurrentEffect(cs)

  override def run(args: List[String]): IO[ExitCode] = for {
    config <- ConsumerConfig.loadIO("application.conf")
    exitCode <- startConsumer(config).as(ExitCode.Success)
  } yield exitCode

  def startConsumer(config: ConsumerConfig): IO[Unit] =
    consumerResource(config).use(consumer =>
        consumer.checkKafkaAvailabilityOrAbort *>
        consumer.subscribe *>
        consumer.poll)

  def consumerResource(config: ConsumerConfig): Resource[IO, Consumer] = for {
    kafkaContext <- KafkaContext.resource(cs)
    consumer <- Consumer.resource(new MessageHandler, config, kafkaContext)
  } yield consumer
}
