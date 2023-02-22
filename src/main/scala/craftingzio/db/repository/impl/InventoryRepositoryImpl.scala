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

    override def findAll: Task[Seq[(InventoryEntity, Seq[(InventoryStackEntity, ItemEntity)])]] = {
        val i = run(query[InventoryEntity])
        val isi = run(query[InventoryStackEntity].join(query[ItemEntity]).on((invs, i) => invs.itemId == i.id))

        (i <&> isi).map(groupFetched)
    }

    override def findById(id: Int): Task[Option[(InventoryEntity, Seq[(InventoryStackEntity, ItemEntity)])]] = {
        val i = run(query[InventoryEntity].filter(i => i.id == lift(id)))
        val isi = run(query[InventoryStackEntity].filter(invs => invs.inventoryId == lift(id)).join(query[ItemEntity]).on((invs, i) => invs.itemId == i.id))

        (i <&> isi).map(groupFetched).map(_.headOption)
    }

    override def save(inventoryEntity: InventoryEntity, inventoryStackEntities: Seq[InventoryStackEntity]): Task[Int] =
        if inventoryEntity.id == 0 then
            insertWithStacks(inventoryEntity, inventoryStackEntities)
        else
            updateWithStacks(inventoryEntity, inventoryStackEntities)

    override def delete(id: Int): Task[Unit] = transaction {
        for
            _ <- run(deleteStacks(lift(id)))
            _ <- run(deleteInventory(lift(id)))
        yield ()
    }

    private def insertWithStacks(inventoryEntity: InventoryEntity, inventoryStackEntities: Seq[InventoryStackEntity]) = transaction {
        for
            inventoryId <- run(insertInventory(lift(inventoryEntity)))
            inventoryStackEntitiesUpdated = inventoryStackEntities.map(_.copy(inventoryId = inventoryId))
            _ <- run(insertStacks(inventoryStackEntitiesUpdated))
        yield inventoryId
    }

    private def updateWithStacks(inventoryEntity: InventoryEntity, inventoryStackEntities: Seq[InventoryStackEntity]) = transaction {
        for
            inventoryId <- run(updateInventory(lift(inventoryEntity)))
            _ <- run(deleteStacks(lift(inventoryId)))
            _ <- run(insertStacks(inventoryStackEntities))
        yield inventoryId
    }

    private inline def insertInventory = quote { (inventoryEntity: InventoryEntity) =>
        query[InventoryEntity].insertValue(inventoryEntity).returning(i => i.id)
    }

    private inline def updateInventory = quote { (inventoryEntity: InventoryEntity) =>
        query[InventoryEntity].filter(i => i.id == inventoryEntity.id).updateValue(inventoryEntity).returning(i => i.id)
    }

    private inline def deleteInventory = quote { (id: Int) =>
        query[InventoryEntity].filter(i => i.id == id).delete
    }

    private inline def insertStacks(inventoryStackEntities: Seq[InventoryStackEntity]) = quote {
        liftQuery(inventoryStackEntities).foreach(invs => query[InventoryStackEntity].insertValue(invs))
    }

    private inline def deleteStacks = quote { (inventoryId: Int) =>
        query[InventoryStackEntity].filter(invs => invs.inventoryId == inventoryId).delete
    }

    private def groupFetched(data: (Seq[InventoryEntity], Seq[(InventoryStackEntity, ItemEntity)])) = {
        val (inventories, stacks) = data
        val stacksByInventoryId = stacks.groupBy { case (stack, _) => stack.inventoryId }

        inventories.map { inventory =>
            (inventory, stacksByInventoryId.getOrElse(inventory.id, Seq.empty))
        }
    }

}

object InventoryRepositoryImpl {
    lazy val layer = ZLayer.derive[InventoryRepositoryImpl]
}