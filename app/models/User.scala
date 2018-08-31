package models

import skinny.orm._, feature._
import scalikejdbc._
import org.joda.time._

case class User(
    id: Int,
    userId: String,
    role: String,
    hasher: String,
    salt: Option[String] = None,
    password: String,
    firstName: Option[String] = None,
    lastName: Option[String] = None,
    email: String,
    avatarUrl: Option[String] = None,
    activated: Option[Boolean] = None
)

object User extends SkinnyCRUDMapperWithId[Int, User] {
  override lazy val tableName = "users"
  override lazy val defaultAlias = createAlias("u")
  override def idToRawValue(id: Int): Any = id
  override def rawValueToId(value: Any): Int = value.asInstanceOf[Int]
  override def useExternalIdGenerator = true
  // override def generateId = 0

  override def extract(rs: WrappedResultSet, rn: ResultName[User]): User = {
    autoConstruct(rs, rn)
  }
}
