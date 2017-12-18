package models

import scalikejdbc._

case class CoffeeKinds(
    id: Int,
    name: Option[String] = None,
    description: Option[String] = None) {

  def save()(implicit session: DBSession = CoffeeKinds.autoSession): CoffeeKinds = CoffeeKinds.save(this)(session)

  def destroy()(implicit session: DBSession = CoffeeKinds.autoSession): Int = CoffeeKinds.destroy(this)(session)

}

object CoffeeKinds extends SQLSyntaxSupport[CoffeeKinds] {

  override val schemaName = Some("example")

  override val tableName = "coffee_kinds"

  override val columns = Seq("id", "name", "description")

  def apply(ck: SyntaxProvider[CoffeeKinds])(rs: WrappedResultSet): CoffeeKinds = apply(ck.resultName)(rs)
  def apply(ck: ResultName[CoffeeKinds])(rs: WrappedResultSet): CoffeeKinds = new CoffeeKinds(
    id = rs.get(ck.id),
    name = rs.get(ck.name),
    description = rs.get(ck.description)
  )

  val ck = CoffeeKinds.syntax("ck")

  override val autoSession = AutoSession

  def find(id: Int)(implicit session: DBSession = autoSession): Option[CoffeeKinds] = {
    withSQL {
      select.from(CoffeeKinds as ck).where.eq(ck.id, id)
    }.map(CoffeeKinds(ck.resultName)).single.apply()
  }

  def findAll()(implicit session: DBSession = autoSession): List[CoffeeKinds] = {
    withSQL(select.from(CoffeeKinds as ck)).map(CoffeeKinds(ck.resultName)).list.apply()
  }

  def countAll()(implicit session: DBSession = autoSession): Long = {
    withSQL(select(sqls.count).from(CoffeeKinds as ck)).map(rs => rs.long(1)).single.apply().get
  }

  def findBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Option[CoffeeKinds] = {
    withSQL {
      select.from(CoffeeKinds as ck).where.append(where)
    }.map(CoffeeKinds(ck.resultName)).single.apply()
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession = autoSession): List[CoffeeKinds] = {
    withSQL {
      select.from(CoffeeKinds as ck).where.append(where)
    }.map(CoffeeKinds(ck.resultName)).list.apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Long = {
    withSQL {
      select(sqls.count).from(CoffeeKinds as ck).where.append(where)
    }.map(_.long(1)).single.apply().get
  }

  def create(
    name: Option[String] = None,
    description: Option[String] = None)(implicit session: DBSession = autoSession): CoffeeKinds = {
    val generatedKey = withSQL {
      insert.into(CoffeeKinds).namedValues(
        column.name -> name,
        column.description -> description
      )
    }.updateAndReturnGeneratedKey.apply()

    CoffeeKinds(
      id = generatedKey.toInt,
      name = name,
      description = description)
  }

  def batchInsert(entities: Seq[CoffeeKinds])(implicit session: DBSession = autoSession): List[Int] = {
    val params: Seq[Seq[(Symbol, Any)]] = entities.map(entity =>
      Seq(
        'name -> entity.name,
        'description -> entity.description))
    SQL("""insert into coffee_kinds(
      name,
      description
    ) values (
      {name},
      {description}
    )""").batchByName(params: _*).apply[List]()
  }

  def save(entity: CoffeeKinds)(implicit session: DBSession = autoSession): CoffeeKinds = {
    withSQL {
      update(CoffeeKinds).set(
        column.id -> entity.id,
        column.name -> entity.name,
        column.description -> entity.description
      ).where.eq(column.id, entity.id)
    }.update.apply()
    entity
  }

  def destroy(entity: CoffeeKinds)(implicit session: DBSession = autoSession): Int = {
    withSQL { delete.from(CoffeeKinds).where.eq(column.id, entity.id) }.update.apply()
  }

}
