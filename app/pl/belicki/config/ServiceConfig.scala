package pl.belicki.config

import pl.belicki.models.Message
import pl.belicki.models.command.{AnalyzeMessage, Command, Start, Stop}

case class ServiceConfig(
    servicePhoneNumber: String
) {
  def toCommand(message: Message): Command = {
    def analyzeMessage = AnalyzeMessage(message.sender, message.message)

    if (message.recipient != servicePhoneNumber) return analyzeMessage
    if (message.message == Start.COMMAND) return Start(message.sender)
    if (message.message == Stop.COMMAND) return Stop(message.sender)

    analyzeMessage
  }
}
