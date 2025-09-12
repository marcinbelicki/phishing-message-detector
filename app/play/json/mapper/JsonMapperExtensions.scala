package play.json.mapper

import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.module.scala.{ClassTagExtensions, JavaTypeable}
import org.apache.pekko.stream.Materializer
import org.apache.pekko.util.ByteString
import play.api.http.Status.UNSUPPORTED_MEDIA_TYPE
import play.api.http.{
  HttpErrorHandler,
  MimeTypes,
  ParserConfiguration,
  Writeable
}
import play.api.libs.Files
import play.api.libs.ws.{BodyReadable, BodyWritable, InMemoryBody}
import play.api.mvc._

trait JsonMapperExtensions {
  def jsonMapper: JsonMapper with ClassTagExtensions

  implicit class PlayBodyParsersExt(playBodyParsers: PlayBodyParsers)
      extends PlayBodyParsers {
    override private[play] implicit def materializer: Materializer =
      playBodyParsers.materializer

    override private[play] def config: ParserConfiguration =
      playBodyParsers.config

    override private[play] def errorHandler: HttpErrorHandler =
      playBodyParsers.errorHandler

    override private[play] def temporaryFileCreator
        : Files.TemporaryFileCreator = playBodyParsers.temporaryFileCreator

    def jacksonJson[A: JavaTypeable](maxLength: Long): BodyParser[A] = when(
      _.contentType.exists(m =>
        m.equalsIgnoreCase("text/json") || m.equalsIgnoreCase(
          MimeTypes.JSON
        )
      ),
      tolerantBodyParser[A]("json", maxLength, "Invalid Json")((_, bytes) =>
        jsonMapper.readValue[A](bytes.iterator.asInputStream)
      ),
      createBadResult(
        "Expecting text/json or application/json body",
        UNSUPPORTED_MEDIA_TYPE
      )
    )

    def jacksonJson[A: JavaTypeable]: BodyParser[A] = jacksonJson(
      DefaultMaxTextLength
    )
  }

  implicit def writeable[A: JavaTypeable]: Writeable[A] = Writeable[A](
    o => ByteString.fromArrayUnsafe(jsonMapper.writeValueAsBytes(o)),
    Some(MimeTypes.JSON)
  )

  implicit def bodyWritable[A: JavaTypeable]: BodyWritable[A] =
    BodyWritable(
      body =>
        InMemoryBody(
          ByteString.fromString(jsonMapper.writeValueAsString(body))
        ),
      MimeTypes.JSON
    )

  implicit def bodyReadable[A: JavaTypeable]: BodyReadable[A] =
    new BodyReadable({ response =>
      val body = response.body
      try jsonMapper.readValue[A](body)
      catch {
        case _: JsonParseException =>
          throw new JsonParseException(body.take(400))
      }
    })

}
