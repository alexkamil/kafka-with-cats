package dosht.kafka.cats

import cats.effect.{ConcurrentEffect, ContextShift, IO, Resource, Timer}
import cats.implicits._
import cats.{FlatMap, Monad}
import org.scalactic.source.Position
import org.scalatest.Assertion
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AsyncWordSpec

import scala.concurrent.duration.FiniteDuration
import scala.concurrent.{ExecutionContext, Future}

class IOAsyncWordSpec extends AsyncWordSpec with Matchers {

  override implicit def executionContext: ExecutionContext  = ExecutionContext.global

  implicit val timer: Timer[IO] = IO.timer(executionContext)
  implicit val cs: ContextShift[IO] = IO.contextShift(executionContext)
  implicit val concurrentEffect: ConcurrentEffect[IO] = IO.ioConcurrentEffect(IO.contextShift(ExecutionContext.global))

  def testEffectOnRunAsync[A](source: IO[A], expected: A)(implicit pos: Position): Future[Assertion] =
    source.attempt.map(_ shouldEqual Right[Throwable, A](expected)).unsafeToFuture()

  def io(io: IO[Assertion]): Future[Assertion] = io.unsafeToFuture()

  def resource(resource: Resource[IO, IO[Assertion]]): Future[Assertion] = resource.use(identity).unsafeToFuture()

  def retry[A, F[_] : FlatMap : Monad : Timer](attempts: Int, wait: FiniteDuration)(f: F[Option[A]]): F[Option[A]] = f.flatMap {
    case a@Some(_) => Monad[F].pure(a)
    case None =>
      if (attempts == 0) Monad[F].pure(None)
      else Timer[F].sleep(wait) *> retry(attempts - 1, wait)(f)
  }
}
