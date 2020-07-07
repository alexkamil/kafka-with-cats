package dosht.kafka.consumer

import cats.effect.IO
import dosht.kafka.api.GyroMessage

class MessageHandler {
  def handleMessage(messages: List[GyroMessage]): IO[Int] = IO {
    messages foreach println
    messages.size
  }
}
