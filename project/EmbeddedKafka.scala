import net.manub.embeddedkafka.{EmbeddedK, EmbeddedKafka => UnderlyingEmbeddedKafka, EmbeddedKafkaConfig}
import sbt.{Def, Task, taskKey}

import scala.util.Try

object EmbeddedKafka {
  var kafka: EmbeddedK = _

  val DefaultPort: Int = 9092

  lazy val kafkaStart = taskKey[Unit]("Start Embedded Kafka.")
  lazy val kafkaStop = taskKey[Unit]("Stop Embedded Kafka.")

  def isPortInUse(port: Int): Boolean = Try(new java.net.Socket("127.0.0.1", port)).map(_.close()).isSuccess

  def kafkaStartImpl: Def.Initialize[Task[Unit]] = Def.task {
    val port: Int = Try(sys.props.get("kafka.embedded.port").map(_.toInt)).toOption.flatten.getOrElse(DefaultPort)
    if (isPortInUse(port)) {
      println(s"Port $port is not available!")
    } else {
      println("Starting EmbeddedKafka")
      kafka = UnderlyingEmbeddedKafka.start()(EmbeddedKafkaConfig.apply(kafkaPort = port))
    }
  }

  def kafkaStopImpl: Def.Initialize[Task[Unit]] = Def.task {
    val port: Int = Try(sys.props.get("kafka.embedded.port").map(_.toInt)).toOption.flatten.getOrElse(DefaultPort)
    println("Stopping EmbeddedKafka")
    Option(kafka).foreach(_.stop(true))
    kafka = null
  }
}
