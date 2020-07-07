package dosht.kafka.producer

import org.apache.kafka.common.serialization.{Deserializer, Serde, Serializer}
import scalapb.{GeneratedMessage, GeneratedMessageCompanion, Message}

class ProtobufSerializer[A <: GeneratedMessage with Message[A]](implicit parser: GeneratedMessageCompanion[A]) extends Serializer[A] {
  override def serialize(topic: String, data: A): Array[Byte] = parser.toByteArray(data)
}

class ProtobufDeserializer[A <: GeneratedMessage with Message[A]](implicit parser: GeneratedMessageCompanion[A]) extends Deserializer[A] {
  override def deserialize(topic: String, data: Array[Byte]): A = parser.parseFrom(data)
}

class ProtobufSerde[A <: GeneratedMessage with Message[A] : GeneratedMessageCompanion] extends Serde[A] {
  override val serializer = new ProtobufSerializer[A]
  override val deserializer = new ProtobufDeserializer[A]
}
