import sbt._

object Dependencies {
    object Core {
        val guava = "com.google.guava" % "guava" % "28.1-jre"
        val pureConfig = "com.github.pureconfig" %% "pureconfig" % "0.12.3"
        val log4jOverSlf4j = "org.slf4j" % "log4j-over-slf4j" % "1.7.26"

    }

    object Protobuf {
        lazy val protobuf = "com.google.protobuf" % "protobuf-java" % "3.8.0" // to avoid illegal reflective access warning with JDK >= 9
        lazy val scalapbJson = "com.thesamet.scalapb" %% "scalapb-json4s" % "0.7.0"
    }

    object Cats {
        private val catsCoreVersion = "2.1.1"
        private val catsEffectVersion = "2.1.3"

        val catsCore = "org.typelevel" %% "cats-core" % catsCoreVersion
        val catsEffect = "org.typelevel" %% "cats-effect" % catsEffectVersion
    }

    object Kafka {
        private val kafkaVersion = "2.5.0"
        val kafkaClient = "org.apache.kafka" % "kafka-clients" % kafkaVersion
        val embeddedKafka = "io.github.embeddedkafka" %% "embedded-kafka" % kafkaVersion
    }

      object Testing {
        val scalaTest = "org.scalatest" %% "scalatest" % "3.1.2"
    }
}
