package models

import scalikejdbc.specs2.mutable.AutoRollback
import org.specs2.mutable._
import scalikejdbc._
import scalikejdbc.config.DBs

class UsersSpec extends Specification {
  DBs.setupAll()

  trait AutoRollbackWithFixture extends AutoRollback {
    override def fixture(implicit session: DBSession) {
      SQL("insert into users (id, user_id, email, role, hasher, password) values (123, 123, 'normal@example.com', 'normal', 'bycrop', 'hoge')")
        .update.apply()
    }
  }

  "Users" should {
    val u = Users.syntax("u")

    "find by primary keys" in new AutoRollbackWithFixture {
      val maybeFound = Users.find(123)
      maybeFound.isDefined should beTrue
    }
    "find by where clauses" in new AutoRollbackWithFixture {
      val maybeFound = Users.findBy(sqls.eq(u.id, 123))
      maybeFound.isDefined should beTrue
    }
    "find all records" in new AutoRollbackWithFixture {
      val allResults = Users.findAll()
      allResults.size should be_>(0)
    }
    "count all records" in new AutoRollbackWithFixture {
      val count = Users.countAll()
      count should be_>(0L)
    }
    "find all by where clauses" in new AutoRollbackWithFixture {
      val results = Users.findAllBy(sqls.eq(u.id, 123))
      results.size should be_>(0)
    }
    "count by where clauses" in new AutoRollbackWithFixture {
      val count = Users.countBy(sqls.eq(u.id, 123))
      count should be_>(0L)
    }
    "create new record" in new AutoRollbackWithFixture {
      val created = Users.create(userId = "MyString", role = "MyString", hasher = "MyString", password = "MyString", email = "MyString")
      created should not beNull
    }
    "save a record" in new AutoRollbackWithFixture {
      val entity = Users.findAll().head
      val modified = entity.copy(email = "abnormal@example.com")
      val updated = Users.save(modified)
      updated should not equalTo (entity)
    }
    "destroy a record" in new AutoRollbackWithFixture {
      val entity = Users.find(123).get
      val deleted = Users.destroy(entity) == 1
      deleted should beTrue
      val shouldBeNone = Users.find(123)
      shouldBeNone.isDefined should beFalse
    }
    "perform batch insert" in new AutoRollbackWithFixture {
      val entities = Users.findAll()
      entities.foreach(e => Users.destroy(e))
      val batchInserted = Users.batchInsert(entities)
      batchInserted.size should be_>(0)
    }
  }

}
