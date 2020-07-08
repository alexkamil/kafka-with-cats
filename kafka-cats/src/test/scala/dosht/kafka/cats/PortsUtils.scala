package dosht.kafka.cats

import scala.annotation.tailrec
import scala.util.{Random, Try}

trait PortsUtils {

  @tailrec
  final def nextAvailablePort: Int = {
    val lowestPortNumber = 49152 // https://en.wikipedia.org/wiki/List_of_TCP_and_UDP_port_numbers#Dynamic,_private_or_ephemeral_ports
    val highestPortNumber = 65535
    val portNumber = lowestPortNumber + Random.nextInt(highestPortNumber - lowestPortNumber)
    if (!isPortInUse(portNumber)) portNumber else nextAvailablePort
  }

  def isPortInUse(port: Int): Boolean = Try(new java.net.Socket("127.0.0.1", port)).map(_.close()).isSuccess

}
