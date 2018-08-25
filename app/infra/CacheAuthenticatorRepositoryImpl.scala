package infra

import javax.inject.Inject
import com.mohiva.play.silhouette.api.repositories.AuthenticatorRepository
import com.mohiva.play.silhouette.api.util.CacheLayer
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator
import play.api.Logger

import scala.concurrent.{ ExecutionContext, Future }
import scala.concurrent.duration.Duration

/**
 * Implementation of the authenticator repository which uses the cache layer to persist the authenticator.
 *
 * @param cacheLayer The cache layer implementation.
 */
class CacheAuthenticatorRepositoryImpl @Inject() (cacheLayer: CacheLayer)(implicit ec: ExecutionContext)
  extends AuthenticatorRepository[CookieAuthenticator] {

  Logger.info("initialized")
  Logger.info(cacheLayer.getClass.getName)
  Logger.info("initialized")

  /**
   * Finds the authenticator for the given ID.
   *
   * @param id The authenticator ID.
   * @return The found authenticator or None if no authenticator could be found for the given ID.
   */
  override def find(id: String): Future[Option[CookieAuthenticator]] = {
    cacheLayer.find[CookieAuthenticator](id)
  }

  /**
   * Adds a new authenticator.
   *
   * @param authenticator The authenticator to add.
   * @return The added authenticator.
   */
  override def add(authenticator: CookieAuthenticator): Future[CookieAuthenticator] = cacheLayer.save[CookieAuthenticator](authenticator.id, authenticator, Duration.Inf)

  /**
   * Updates an already existing authenticator.
   *
   * @param authenticator The authenticator to update.
   * @return The updated authenticator.
   */
  override def update(authenticator: CookieAuthenticator): Future[CookieAuthenticator] = cacheLayer.save[CookieAuthenticator](authenticator.id, authenticator, Duration.Inf)

  /**
   * Removes the authenticator for the given ID.
   *
   * @param id The authenticator ID.
   * @return An empty future.
   */
  override def remove(id: String): Future[Unit] = cacheLayer.remove(id)
}
