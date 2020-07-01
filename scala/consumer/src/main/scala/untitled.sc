import java.util.concurrent.{ExecutorService, Executors}

import cats.effect.{ContextShift, IO}
import com.google.common.util.concurrent.ThreadFactoryBuilder
import cats._
import scala.concurrent.{ExecutionContext, ExecutionContextExecutor}

val ec: ExecutionContextExecutor = ExecutionContext.global
val cs: ContextShift[IO] = IO.contextShift(ec)
val pool: ExecutorService = Executors.newFixedThreadPool(1, new ThreadFactoryBuilder().setNameFormat("kafka-thread").build())

val anotherExecutionContext: ExecutionContextExecutor = ExecutionContext.fromExecutor(pool)

val f1 = IO(s"f1 current thread is: ${Thread.currentThread().getName}")
val f2 = IO(s"f2 current thread is: ${Thread.currentThread().getName}")
val f3 = IO(s"f3 current thread is: ${Thread.currentThread().getName}")

val ob = for {
  _ <- cs.shift
  a <- f1
  b <- cs.evalOn(anotherExecutionContext)(f2)
  c <- f3
} yield (List(a, b, c))

ob.unsafeRunSync()