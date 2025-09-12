package pl.belicki.json.mapper

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.module.scala.{
  ClassTagExtensions,
  DefaultScalaModule
}
import com.google.inject.AbstractModule

class ObjectMapperModule extends AbstractModule {
  private val builder =
    JsonMapper
      .builder()
      .addModule(DefaultScalaModule)
      .enable(JsonParser.Feature.INCLUDE_SOURCE_IN_LOCATION)
      .enable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)

  private lazy val jsonMapper = builder.build() :: ClassTagExtensions

  override def configure(): Unit =
    bind(classOf[JsonMapper]).toInstance(jsonMapper)

}
