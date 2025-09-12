package pl.belicki.detector.external

import com.google.common.cache.CacheBuilder
import com.google.inject.{Inject, Provides}
import com.google.inject.name.Named
import pl.belicki.models.Response
import play.api.Logging
import scalacache.guava.GuavaCache
import scalacache.{Cache, Entry}
import scalacache.memoization.memoizeF
import scalacache.modes.scalaFuture.mode

import scala.concurrent.{ExecutionContext, Future}

class MemoizingExternalService @Inject() (
    @Named("underlying") val underlying: ExternalService,
    val externalServiceConfig: ExternalServiceConfig
) extends ExternalService with Logging {
  private val underlyingGuavaCache = CacheBuilder
    .newBuilder()
    .maximumSize(Long.MaxValue)
    .build[String, Entry[Response]]
  private implicit val scalaCacheGuava: Cache[Response] = GuavaCache(
    underlyingGuavaCache
  )

  override def checkUrl(url: String)(implicit
      ec: ExecutionContext
  ): Future[Response] = {
    logger.info(s"Checking url: $url")
    memoizeF[Future, Response](Some(externalServiceConfig.cacheTtl))(
      underlying.checkUrl(url)
    )
  }
}
