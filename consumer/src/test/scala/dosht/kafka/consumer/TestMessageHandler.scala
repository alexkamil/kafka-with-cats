package dosht.kafka.consumer

import cats.effect.IO
import dosht.kafka.api.GyroMessage

import scala.collection.mutable.{Queue => MutableQueue}

class TestMessageHandler extends MessageHandler {

  val queue: MutableQueue[GyroMessage] = MutableQueue.empty

  override def handleMessage(messages: List[GyroMessage]): IO[Int] = {
    queue ++= messages
    IO.pure(messages.size)
  }

}
