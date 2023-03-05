package craftingzio.db.repository.impl

import craftingzio.db.model.ItemEntity
import craftingzio.db.model.ItemEntity.given
import craftingzio.db.repository.{DataSourceAutoProvider, ItemRepository}
import craftingzio.exceptions.NotFoundException
import io.getquill.*
import zio.*

import javax.sql.DataSource

case class ItemRepositoryImpl(
    override protected val dataSource: DataSource
) extends PostgresZioJdbcContext(SnakeCase)
    with ItemRepository
    with DataSourceAutoProvider {

    override def findAll: Task[Seq[ItemEntity]] = run {
        query[ItemEntity]
    }

    override def findById(id: Int): Task[ItemEntity] = run {
        query[ItemEntity].filter(i => i.id == lift(id))
    }.map(_.headOption).flatMap {
        case Some(itemEntity) => ZIO.succeed(itemEntity)
        case None => ZIO.fail(NotFoundException(s"Item id: $id not found"))
    }

    override def findAllByIdIn(ids: Seq[Int]): Task[Seq[ItemEntity]] = run {
        query[ItemEntity].filter(i => lift(ids).contains(i.id))
    }
}

object ItemRepositoryImpl {
    lazy val layer = ZLayer.derive[ItemRepositoryImpl]
}