package craftingzio.db.repository.impl

import craftingzio.db.model.ItemEntity
import craftingzio.db.model.ItemEntity.given
import craftingzio.db.repository.{DataSourceAutoProvider, ItemRepository}
import io.getquill.*
import zio.{Task, ZLayer}

import javax.sql.DataSource

case class ItemRepositoryImpl(override protected val dataSource: DataSource)
    extends PostgresZioJdbcContext(SnakeCase)
        with ItemRepository
        with DataSourceAutoProvider {

    override def findAll: Task[Seq[ItemEntity]] = run {
        query[ItemEntity]
    }

    override def findById(id: Int): Task[Option[ItemEntity]] = run {
        query[ItemEntity].filter(i => i.id == lift(id))
    }.map(_.headOption)

    override def findAllByIdIn(ids: Seq[Int]): Task[Seq[ItemEntity]] = run {
        query[ItemEntity].filter(i => lift(ids).contains(i.id))
    }
}

object ItemRepositoryImpl {
    lazy val layer = ZLayer.derive[ItemRepositoryImpl]
}