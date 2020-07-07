import EmbeddedKafka._

inThisBuild(
    List(
        organization := "io.github.dosht",
        scalaVersion := "2.12.9",
    )
)

lazy val kafkaCats = project in file("kafka-cats") withId Projects.kafkaCats.project
lazy val api = project in file("api") withId Projects.api.project
lazy val consumer = project in file("consumer") withId Projects.consumer.project
lazy val producer = project in file("producer") withId Projects.producer.project

lazy val root = project in file(".") aggregate (kafkaCats, api, consumer, producer)

kafkaStart := kafkaStartImpl.value
kafkaStop := kafkaStopImpl.value
