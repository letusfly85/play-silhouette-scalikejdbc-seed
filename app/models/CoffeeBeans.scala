package models

import scalikejdbc._

case class CoffeeBeans(
    id: Int,
    name: Option[String] = None,
    kind: Option[String] = None,
    coffeeShopId: Option[Int] = None) {

  def save()(implicit session: DBSession = CoffeeBeans.autoSession): CoffeeBeans = CoffeeBeans.save(this)(session)

  def destroy()(implicit session: DBSession = CoffeeBeans.autoSession): Int = CoffeeBeans.destroy(this)(session)

}

object CoffeeBeans extends SQLSyntaxSupport[CoffeeBeans] {

  override val schemaName = Some("example")

  override val tableName = "coffee_beans"

  override val columns = Seq("id", "name", "kind", "coffee_shop_id")

  def apply(cb: SyntaxProvider[CoffeeBeans])(rs: WrappedResultSet): CoffeeBeans = apply(cb.resultName)(rs)
  def apply(cb: ResultName[CoffeeBeans])(rs: WrappedResultSet): CoffeeBeans = new CoffeeBeans(
    id = rs.get(cb.id),
    name = rs.get(cb.name),
    kind = rs.get(cb.kind),
    coffeeShopId = rs.get(cb.coffeeShopId)
  )

  val cb = CoffeeBeans.syntax("cb")

  override val autoSession = AutoSession

  def find(id: Int)(implicit session: DBSession = autoSession): Option[CoffeeBeans] = {
    withSQL {
      select.from(CoffeeBeans as cb).where.eq(cb.id, id)
    }.map(CoffeeBeans(cb.resultName)).single.apply()
  }

  def findAll()(implicit session: DBSession = autoSession): List[CoffeeBeans] = {
    withSQL(select.from(CoffeeBeans as cb)).map(CoffeeBeans(cb.resultName)).list.apply()
  }

  def countAll()(implicit session: DBSession = autoSession): Long = {
    withSQL(select(sqls.count).from(CoffeeBeans as cb)).map(rs => rs.long(1)).single.apply().get
  }

  def findBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Option[CoffeeBeans] = {
    withSQL {
      select.from(CoffeeBeans as cb).where.append(where)
    }.map(CoffeeBeans(cb.resultName)).single.apply()
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession = autoSession): List[CoffeeBeans] = {
    withSQL {
      select.from(CoffeeBeans as cb).where.append(where)
    }.map(CoffeeBeans(cb.resultName)).list.apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Long = {
    withSQL {
      select(sqls.count).from(CoffeeBeans as cb).where.append(where)
    }.map(_.long(1)).single.apply().get
  }

  def create(
    name: Option[String] = None,
    kind: Option[String] = None,
    coffeeShopId: Option[Int] = None)(implicit session: DBSession = autoSession): CoffeeBeans = {
    val generatedKey = withSQL {
      insert.into(CoffeeBeans).namedValues(
        column.name -> name,
        column.kind -> kind,
        column.coffeeShopId -> coffeeShopId
      )
    }.updateAndReturnGeneratedKey.apply()

    CoffeeBeans(
      id = generatedKey.toInt,
      name = name,
      kind = kind,
      coffeeShopId = coffeeShopId)
  }

  def batchInsert(entities: Seq[CoffeeBeans])(implicit session: DBSession = autoSession): List[Int] = {
    val params: Seq[Seq[(Symbol, Any)]] = entities.map(entity =>
      Seq(
        'name -> entity.name,
        'kind -> entity.kind,
        'coffeeShopId -> entity.coffeeShopId))
    SQL("""insert into coffee_beans(
      name,
      kind,
      coffee_shop_id
    ) values (
      {name},
      {kind},
      {coffeeShopId}
    )""").batchByName(params: _*).apply[List]()
  }

  def save(entity: CoffeeBeans)(implicit session: DBSession = autoSession): CoffeeBeans = {
    withSQL {
      update(CoffeeBeans).set(
        column.id -> entity.id,
        column.name -> entity.name,
        column.kind -> entity.kind,
        column.coffeeShopId -> entity.coffeeShopId
      ).where.eq(column.id, entity.id)
    }.update.apply()
    entity
  }

  def destroy(entity: CoffeeBeans)(implicit session: DBSession = autoSession): Int = {
    withSQL { delete.from(CoffeeBeans).where.eq(column.id, entity.id) }.update.apply()
  }

}
