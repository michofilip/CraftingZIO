package craftingzio.db.repository.impl

import craftingzio.db.model.InventoryEntity.given
import craftingzio.db.model.{InventoryEntity, InventoryStackEntity, ItemEntity}
import craftingzio.db.repository.{DataSourceAutoProvider, InventoryRepository}
import io.getquill.*
import zio.{Task, ZIO, ZLayer}

import java.sql.SQLException
import javax.sql.DataSource

case class InventoryRepositoryImpl(override protected val dataSource: DataSource)
    extends PostgresZioJdbcContext(SnakeCase)
        with InventoryRepository
        with DataSourceAutoProvider {

    override def findAll: Task[Seq[(InventoryEntity, Seq[(InventoryStackEntity, ItemEntity)])]] =
        run(query[InventoryEntity].fetchItems)
            .map(groupFetched)

    override def findById(id: Int): Task[Option[(InventoryEntity, Seq[(InventoryStackEntity, ItemEntity)])]] =
        run(query[InventoryEntity].filter(i => i.id == lift(id)).fetchItems)
            .map(groupFetched)
            .map(_.headOption)

    override def save(inventoryEntity: InventoryEntity, inventoryStackEntities: Seq[InventoryStackEntity]): Task[Int] =
        if inventoryEntity.id == 0 then
            transaction {
                for
                    inventoryId <- run(insert(lift(inventoryEntity)))
                    inventoryStackEntities <- ZIO.succeed(inventoryStackEntities).map(_.map(_.copy(inventoryId = inventoryId)))
                    _ <- run(insertAllStacks(inventoryStackEntities))
                yield inventoryId
            }
        else
            transaction {
                for
                    inventoryId <- run(update(lift(inventoryEntity)))
                    _ <- run(deleteAllStacks(lift(inventoryId)))
                    _ <- run(insertAllStacks(inventoryStackEntities))
                yield inventoryId
            }

    override def delete(id: Int): Task[Unit] = transaction {
        for
            _ <- run(deleteAllStacks(lift(id)))
            _ <- run(query[InventoryEntity].filter(i => i.id == lift(id)).delete)
        yield ()
    }

    private inline def insert = quote { (inventoryEntity: InventoryEntity) =>
        query[InventoryEntity].insertValue(inventoryEntity).returning(i => i.id)
    }

    private inline def update = quote { (inventoryEntity: InventoryEntity) =>
        query[InventoryEntity].filter(i => i.id == inventoryEntity.id).updateValue(inventoryEntity).returning(i => i.id)
    }

    private inline def insertAllStacks(inventoryStackEntities: Seq[InventoryStackEntity]) = quote {
        liftQuery(inventoryStackEntities).foreach(invs => query[InventoryStackEntity].insertValue(invs))
    }

    private inline def deleteAllStacks = quote { (inventoryId: Int) =>
        query[InventoryStackEntity].filter(invs => invs.inventoryId == inventoryId).delete
    }

    extension (inventoryQuery: EntityQuery[InventoryEntity]) {
        private inline def fetchItems: Query[(InventoryEntity, Option[(InventoryStackEntity, ItemEntity)])] = quote {
            inventoryQuery.leftJoin(
                query[InventoryStackEntity]
                    .join(query[ItemEntity])
                    .on((invs, i) => invs.itemId == i.id)
            ).on({ case (inv, (invs, _)) => inv.id == invs.inventoryId })
        }
    }

    private def groupFetched(data: Seq[(InventoryEntity, Option[(InventoryStackEntity, ItemEntity)])]) =
        data.groupMap(_._1)(_._2).view.mapValues(_.flatten).toSeq

}

object InventoryRepositoryImpl {
    lazy val layer = ZLayer.derive[InventoryRepositoryImpl]
}