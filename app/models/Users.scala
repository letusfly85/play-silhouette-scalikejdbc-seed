package models

import scalikejdbc._

case class Users(
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
    activated: Option[Boolean] = None) {

  def save()(implicit session: DBSession = Users.autoSession): Users = Users.save(this)(session)

  def destroy()(implicit session: DBSession = Users.autoSession): Int = Users.destroy(this)(session)

}

object Users extends SQLSyntaxSupport[Users] {

  override val schemaName = Some("example")

  override val tableName = "users"

  override val columns = Seq("id", "user_id", "role", "hasher", "salt", "password", "first_name", "last_name", "email", "avatar_url", "activated")

  def apply(u: SyntaxProvider[Users])(rs: WrappedResultSet): Users = apply(u.resultName)(rs)
  def apply(u: ResultName[Users])(rs: WrappedResultSet): Users = new Users(
    id = rs.get(u.id),
    userId = rs.get(u.userId),
    role = rs.get(u.role),
    hasher = rs.get(u.hasher),
    salt = rs.get(u.salt),
    password = rs.get(u.password),
    firstName = rs.get(u.firstName),
    lastName = rs.get(u.lastName),
    email = rs.get(u.email),
    avatarUrl = rs.get(u.avatarUrl),
    activated = rs.get(u.activated)
  )

  val u = Users.syntax("u")

  override val autoSession = AutoSession

  def find(id: Int)(implicit session: DBSession = autoSession): Option[Users] = {
    withSQL {
      select.from(Users as u).where.eq(u.id, id)
    }.map(Users(u.resultName)).single.apply()
  }

  def findAll()(implicit session: DBSession = autoSession): List[Users] = {
    withSQL(select.from(Users as u)).map(Users(u.resultName)).list.apply()
  }

  def countAll()(implicit session: DBSession = autoSession): Long = {
    withSQL(select(sqls.count).from(Users as u)).map(rs => rs.long(1)).single.apply().get
  }

  def findBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Option[Users] = {
    withSQL {
      select.from(Users as u).where.append(where)
    }.map(Users(u.resultName)).single.apply()
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession = autoSession): List[Users] = {
    withSQL {
      select.from(Users as u).where.append(where)
    }.map(Users(u.resultName)).list.apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Long = {
    withSQL {
      select(sqls.count).from(Users as u).where.append(where)
    }.map(_.long(1)).single.apply().get
  }

  def create(
    userId: String,
    role: String,
    hasher: String,
    salt: Option[String] = None,
    password: String,
    firstName: Option[String] = None,
    lastName: Option[String] = None,
    email: String,
    avatarUrl: Option[String] = None,
    activated: Option[Boolean] = None)(implicit session: DBSession = autoSession): Users = {
    val generatedKey = withSQL {
      insert.into(Users).namedValues(
        column.userId -> userId,
        column.role -> role,
        column.hasher -> hasher,
        column.salt -> salt,
        column.password -> password,
        column.firstName -> firstName,
        column.lastName -> lastName,
        column.email -> email,
        column.avatarUrl -> avatarUrl,
        column.activated -> activated
      )
    }.updateAndReturnGeneratedKey.apply()

    Users(
      id = generatedKey.toInt,
      userId = userId,
      role = role,
      hasher = hasher,
      salt = salt,
      password = password,
      firstName = firstName,
      lastName = lastName,
      email = email,
      avatarUrl = avatarUrl,
      activated = activated)
  }

  def batchInsert(entities: Seq[Users])(implicit session: DBSession = autoSession): List[Int] = {
    val params: Seq[Seq[(Symbol, Any)]] = entities.map(entity =>
      Seq(
        'userId -> entity.userId,
        'role -> entity.role,
        'hasher -> entity.hasher,
        'salt -> entity.salt,
        'password -> entity.password,
        'firstName -> entity.firstName,
        'lastName -> entity.lastName,
        'email -> entity.email,
        'avatarUrl -> entity.avatarUrl,
        'activated -> entity.activated))
    SQL("""insert into users(
      user_id,
      role,
      hasher,
      salt,
      password,
      first_name,
      last_name,
      email,
      avatar_url,
      activated
    ) values (
      {userId},
      {role},
      {hasher},
      {salt},
      {password},
      {firstName},
      {lastName},
      {email},
      {avatarUrl},
      {activated}
    )""").batchByName(params: _*).apply[List]()
  }

  def save(entity: Users)(implicit session: DBSession = autoSession): Users = {
    withSQL {
      update(Users).set(
        column.id -> entity.id,
        column.userId -> entity.userId,
        column.role -> entity.role,
        column.hasher -> entity.hasher,
        column.salt -> entity.salt,
        column.password -> entity.password,
        column.firstName -> entity.firstName,
        column.lastName -> entity.lastName,
        column.email -> entity.email,
        column.avatarUrl -> entity.avatarUrl,
        column.activated -> entity.activated
      ).where.eq(column.id, entity.id)
    }.update.apply()
    entity
  }

  def destroy(entity: Users)(implicit session: DBSession = autoSession): Int = {
    withSQL { delete.from(Users).where.eq(column.id, entity.id) }.update.apply()
  }

}
