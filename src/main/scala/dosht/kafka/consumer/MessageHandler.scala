package dosht.kafka.consumer

import cats.effect.IO

class MessageHandler {
  def handleMessage(messages: List[String]): IO[Int] = IO {
    messages foreach println
    messages.size
  }
}
