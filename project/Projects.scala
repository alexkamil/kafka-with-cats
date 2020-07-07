import sbt.LocalProject

object Projects {
    lazy val kafkaCats = LocalProject("kafka-cats")
    lazy val api = LocalProject("api")
    lazy val consumer = LocalProject("consumer")
    lazy val producer = LocalProject("producer")
}
