package pl.belicki.models.command

case class Stop(phoneNumber: String) extends Command

object Stop {
  val COMMAND = "STOP"
}