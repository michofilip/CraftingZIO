package craftingzio.db.repository

import craftingzio.db.model.{InventoryEntity, InventoryStackEntity, ItemEntity}
import zio.Task

trait InventoryRepository {
    def findAll: Task[Seq[(InventoryEntity, Seq[(InventoryStackEntity, ItemEntity)])]]

    def findById(id: Int): Task[(InventoryEntity, Seq[(InventoryStackEntity, ItemEntity)])]

    def save(inventoryEntity: InventoryEntity, inventoryStackEntities: Seq[InventoryStackEntity]): Task[Int]

    def delete(id: Int): Task[Unit]
}
