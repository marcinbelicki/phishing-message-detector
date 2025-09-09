package pl.belicki.models.command

case class Start(phoneNumber: String) extends Command

object Start {
  val COMMAND = "START"
}
