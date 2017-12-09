package models

import scalikejdbc._

case class CoffeeShops(
    id: Int,
    name: Option[String] = None,
    email: Option[String] = None,
    ownerName: Option[String] = None,
    address: Option[String] = None) {

  def save()(implicit session: DBSession = CoffeeShops.autoSession): CoffeeShops = CoffeeShops.save(this)(session)

  def destroy()(implicit session: DBSession = CoffeeShops.autoSession): Int = CoffeeShops.destroy(this)(session)

}

object CoffeeShops extends SQLSyntaxSupport[CoffeeShops] {

  override val schemaName = Some("example")

  override val tableName = "coffee_shops"

  override val columns = Seq("id", "name", "email", "owner_name", "address")

  def apply(cs: SyntaxProvider[CoffeeShops])(rs: WrappedResultSet): CoffeeShops = apply(cs.resultName)(rs)
  def apply(cs: ResultName[CoffeeShops])(rs: WrappedResultSet): CoffeeShops = new CoffeeShops(
    id = rs.get(cs.id),
    name = rs.get(cs.name),
    email = rs.get(cs.email),
    ownerName = rs.get(cs.ownerName),
    address = rs.get(cs.address)
  )

  val cs = CoffeeShops.syntax("cs")

  override val autoSession = AutoSession

  def find(id: Int)(implicit session: DBSession = autoSession): Option[CoffeeShops] = {
    withSQL {
      select.from(CoffeeShops as cs).where.eq(cs.id, id)
    }.map(CoffeeShops(cs.resultName)).single.apply()
  }

  def findAll()(implicit session: DBSession = autoSession): List[CoffeeShops] = {
    withSQL(select.from(CoffeeShops as cs)).map(CoffeeShops(cs.resultName)).list.apply()
  }

  def countAll()(implicit session: DBSession = autoSession): Long = {
    withSQL(select(sqls.count).from(CoffeeShops as cs)).map(rs => rs.long(1)).single.apply().get
  }

  def findBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Option[CoffeeShops] = {
    withSQL {
      select.from(CoffeeShops as cs).where.append(where)
    }.map(CoffeeShops(cs.resultName)).single.apply()
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession = autoSession): List[CoffeeShops] = {
    withSQL {
      select.from(CoffeeShops as cs).where.append(where)
    }.map(CoffeeShops(cs.resultName)).list.apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Long = {
    withSQL {
      select(sqls.count).from(CoffeeShops as cs).where.append(where)
    }.map(_.long(1)).single.apply().get
  }

  def create(
    name: Option[String] = None,
    email: Option[String] = None,
    ownerName: Option[String] = None,
    address: Option[String] = None)(implicit session: DBSession = autoSession): CoffeeShops = {
    val generatedKey = withSQL {
      insert.into(CoffeeShops).namedValues(
        column.name -> name,
        column.email -> email,
        column.ownerName -> ownerName,
        column.address -> address
      )
    }.updateAndReturnGeneratedKey.apply()

    CoffeeShops(
      id = generatedKey.toInt,
      name = name,
      email = email,
      ownerName = ownerName,
      address = address)
  }

  def batchInsert(entities: Seq[CoffeeShops])(implicit session: DBSession = autoSession): List[Int] = {
    val params: Seq[Seq[(Symbol, Any)]] = entities.map(entity =>
      Seq(
        'name -> entity.name,
        'email -> entity.email,
        'ownerName -> entity.ownerName,
        'address -> entity.address))
    SQL("""insert into coffee_shops(
      name,
      email,
      owner_name,
      address
    ) values (
      {name},
      {email},
      {ownerName},
      {address}
    )""").batchByName(params: _*).apply[List]()
  }

  def save(entity: CoffeeShops)(implicit session: DBSession = autoSession): CoffeeShops = {
    withSQL {
      update(CoffeeShops).set(
        column.id -> entity.id,
        column.name -> entity.name,
        column.email -> entity.email,
        column.ownerName -> entity.ownerName,
        column.address -> entity.address
      ).where.eq(column.id, entity.id)
    }.update.apply()
    entity
  }

  def destroy(entity: CoffeeShops)(implicit session: DBSession = autoSession): Int = {
    withSQL { delete.from(CoffeeShops).where.eq(column.id, entity.id) }.update.apply()
  }

}
