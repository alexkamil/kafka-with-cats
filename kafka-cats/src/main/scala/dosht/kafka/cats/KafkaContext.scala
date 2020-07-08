package dosht.kafka.consumer

import java.util.concurrent.{ExecutorService, Executors, ThreadFactory}

import cats.effect.{ContextShift, IO, Resource}
import com.google.common.util.concurrent.ThreadFactoryBuilder

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor}

/**
 * Creates a custom thread pool with a single thread for the KafkaConsumer.
 * Any function executed in execute method will run in the kafka thread
 * and shifts back the context to the original thread pool that was used before
 */
class KafkaContext(cs: ContextShift[IO]) {
  private val threadFActor = new ThreadFactoryBuilder().setNameFormat("kafka-thread").build()
  private val pool: ExecutorService = Executors.newFixedThreadPool(1,  threadFActor)
  protected val synchronousExecutionContext: ExecutionContextExecutor = ExecutionContext.fromExecutor(pool)

  def execute[A](f: => A): IO[A] = cs.evalOn(synchronousExecutionContext)(IO(f))

  // Alias for execute
  def ~>[A](f: => A): IO[A] = execute(f)

  def close(): IO[Unit] = IO(pool.shutdown())
}

object
KafkaContext {
  def resource(cs: ContextShift[IO]): Resource[IO, KafkaContext] = Resource.make(IO(new KafkaContext(cs)))(_.close())

}
