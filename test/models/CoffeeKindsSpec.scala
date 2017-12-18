package models

import scalikejdbc.specs2.mutable.AutoRollback
import org.specs2.mutable._
import scalikejdbc._
import scalikejdbc.config.DBs

class CoffeeKindsSpec extends Specification {
  DBs.setupAll()

  trait AutoRollbackWithFixture extends AutoRollback {
    override def fixture(implicit session: DBSession) {
      SQL("insert into coffee_kinds values (?, ?, ?)")
        .bind(
          123, "fugafuga", "great"
        ).update.apply()
      SQL("insert into coffee_kinds values (?, ?, ?)")
        .bind(
          234, "hogehoge", "cool"
        ).update.apply()
    }
  }

  "CoffeeKinds" should {

    val ck = CoffeeKinds.syntax("ck")

    "find by primary keys" in new AutoRollbackWithFixture {
      val maybeFound = CoffeeKinds.find(123)
      maybeFound.isDefined should beTrue
    }
    "find by where clauses" in new AutoRollbackWithFixture {
      val maybeFound = CoffeeKinds.findBy(sqls.eq(ck.id, 123))
      maybeFound.isDefined should beTrue
    }
    "find all records" in new AutoRollbackWithFixture {
      val allResults = CoffeeKinds.findAll()
      allResults.size should be_>(0)
    }
    "count all records" in new AutoRollbackWithFixture {
      val count = CoffeeKinds.countAll()
      count should be_>(0L)
    }
    "find all by where clauses" in new AutoRollbackWithFixture {
      val results = CoffeeKinds.findAllBy(sqls.eq(ck.id, 123))
      results.size should be_>(0)
    }
    "count by where clauses" in new AutoRollbackWithFixture {
      val count = CoffeeKinds.countBy(sqls.eq(ck.id, 123))
      count should be_>(0L)
    }
    "create new record" in new AutoRollbackWithFixture {
      val created = CoffeeKinds.create()
      created should not beNull
    }
    "save a record" in new AutoRollbackWithFixture {
      val entity = CoffeeKinds.findAll().head
      val modified = entity.copy(name = Some("gafugafu"))
      val updated = CoffeeKinds.save(modified)
      updated should not equalTo (entity)
    }
    "destroy a record" in new AutoRollbackWithFixture {
      val entity = CoffeeKinds.findAll().head
      val deleted = CoffeeKinds.destroy(entity) == 1
      deleted should beTrue
      val shouldBeNone = CoffeeKinds.find(123)
      shouldBeNone.isDefined should beFalse
    }
    "perform batch insert" in new AutoRollbackWithFixture {
      val entities = CoffeeKinds.findAll()
      entities.foreach(e => CoffeeKinds.destroy(e))
      val batchInserted = CoffeeKinds.batchInsert(entities)
      batchInserted.size should be_>(0)
    }
  }

}
