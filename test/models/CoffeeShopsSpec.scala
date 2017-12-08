package models

import scalikejdbc.specs2.mutable.AutoRollback
import org.specs2.mutable._
import scalikejdbc._


class CoffeeShopsSpec extends Specification {

  "CoffeeShops" should {

    val cs = CoffeeShops.syntax("cs")

    "find by primary keys" in new AutoRollback {
      val maybeFound = CoffeeShops.find(123)
      maybeFound.isDefined should beTrue
    }
    "find by where clauses" in new AutoRollback {
      val maybeFound = CoffeeShops.findBy(sqls.eq(cs.id, 123))
      maybeFound.isDefined should beTrue
    }
    "find all records" in new AutoRollback {
      val allResults = CoffeeShops.findAll()
      allResults.size should be_>(0)
    }
    "count all records" in new AutoRollback {
      val count = CoffeeShops.countAll()
      count should be_>(0L)
    }
    "find all by where clauses" in new AutoRollback {
      val results = CoffeeShops.findAllBy(sqls.eq(cs.id, 123))
      results.size should be_>(0)
    }
    "count by where clauses" in new AutoRollback {
      val count = CoffeeShops.countBy(sqls.eq(cs.id, 123))
      count should be_>(0L)
    }
    "create new record" in new AutoRollback {
      val created = CoffeeShops.create()
      created should not beNull
    }
    "save a record" in new AutoRollback {
      val entity = CoffeeShops.findAll().head
      // TODO modify something
      val modified = entity
      val updated = CoffeeShops.save(modified)
      updated should not equalTo(entity)
    }
    "destroy a record" in new AutoRollback {
      val entity = CoffeeShops.findAll().head
      val deleted = CoffeeShops.destroy(entity) == 1
      deleted should beTrue
      val shouldBeNone = CoffeeShops.find(123)
      shouldBeNone.isDefined should beFalse
    }
    "perform batch insert" in new AutoRollback {
      val entities = CoffeeShops.findAll()
      entities.foreach(e => CoffeeShops.destroy(e))
      val batchInserted = CoffeeShops.batchInsert(entities)
      batchInserted.size should be_>(0)
    }
  }

}
