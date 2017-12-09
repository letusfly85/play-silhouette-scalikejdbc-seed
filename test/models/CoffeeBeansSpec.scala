package models

import scalikejdbc.specs2.mutable.AutoRollback
import org.specs2.mutable._
import scalikejdbc._
import scalikejdbc.config.DBs

class CoffeeBeansSpec extends Specification {
  DBs.setupAll()

  trait AutoRollbackWithFixture extends AutoRollback {
    override def fixture(implicit session: DBSession) {
      SQL("insert into coffee_beans values (?, ?, ?)").bind(123, "Guatemala", "Latin America").update.apply()
      SQL("insert into coffee_beans values (?, ?, ?)").bind(234, "Ethiopia", "Africa").update.apply()
    }
  }

  "CoffeeBeans" should {

    val cb = CoffeeBeans.syntax("cb")

    "find by primary keys" in new AutoRollbackWithFixture {
      val maybeFound = CoffeeBeans.find(123)
      maybeFound.isDefined should beTrue
    }
    "find by where clauses" in new AutoRollbackWithFixture {
      val maybeFound = CoffeeBeans.findBy(sqls.eq(cb.id, 123))
      maybeFound.isDefined should beTrue
    }
    "find all records" in new AutoRollbackWithFixture {
      val allResults = CoffeeBeans.findAll()
      allResults.size should be_>(0)
    }
    "count all records" in new AutoRollbackWithFixture {
      val count = CoffeeBeans.countAll()
      count should be_>(0L)
    }
    "find all by where clauses" in new AutoRollbackWithFixture {
      val results = CoffeeBeans.findAllBy(sqls.eq(cb.id, 123))
      results.size should be_>(0)
    }
    "count by where clauses" in new AutoRollbackWithFixture {
      val count = CoffeeBeans.countBy(sqls.eq(cb.id, 123))
      count should be_>(0L)
    }
    "create new record" in new AutoRollbackWithFixture {
      val created = CoffeeBeans.create()
      created should not beNull
    }
    "save a record" in new AutoRollbackWithFixture {
      val entity = CoffeeBeans.find(123).get
      val modified = entity.copy(name = Some("fugafuga"))
      val updated = CoffeeBeans.save(modified)
      updated should not equalTo (entity)
    }
    "destroy a record" in new AutoRollbackWithFixture {
      val entity = CoffeeBeans.findAll().head
      val deleted = CoffeeBeans.destroy(entity) == 1
      deleted should beTrue
      val shouldBeNone = CoffeeBeans.find(123)
      shouldBeNone.isDefined should beFalse
    }
    "perform batch insert" in new AutoRollbackWithFixture {
      val entities = CoffeeBeans.findAll()
      entities.foreach(e => CoffeeBeans.destroy(e))
      val batchInserted = CoffeeBeans.batchInsert(entities)
      batchInserted.size should be_>(0)
    }
  }

}
