package craftingzio.service.impl

import craftingzio.db.model.{InventoryEntity, InventoryStackEntity, ItemEntity}
import craftingzio.db.repository.{InventoryRepository, ItemRepository}
import craftingzio.dto.{Inventory, InventoryStack, Item}
import craftingzio.exceptions.NotFoundException
import craftingzio.form.InventoryForm
import craftingzio.service.InventoryService
import craftingzio.utils.Log
import zio.{Task, ZIO, ZLayer}

case class InventoryServiceImpl(private val itemRepository: ItemRepository,
                                private val inventoryRepository: InventoryRepository)
    extends InventoryService {

    override def findAll: Task[Seq[Inventory]] = {
        inventoryRepository.findAll.map(inventoriesFrom)
    } @@ Log.timed("InventoryServiceImpl::findAll")

    override def findById(id: Int): Task[Inventory] = {
        getById(id).map(inventoryFrom)
    } @@ Log.timed("InventoryServiceImpl::findById")

    override def create(inventoryForm: InventoryForm): Task[Inventory] = {
        for
            inventoryEntity <- ZIO.succeed(InventoryEntity(name = inventoryForm.name))
            _ <- validateAllItems(inventoryForm.stacks.map(_.itemId))
            inventoryId <- saveInventoryWithItems(inventoryEntity, inventoryForm)
            inventory <- findById(inventoryId)
        yield inventory
    } @@ Log.timed("InventoryServiceImpl::create")

    override def update(id: Int, inventoryForm: InventoryForm): Task[Inventory] = {
        for
            inventoryEntity <- getById(id).map(_._1)
            _ <- validateAllItems(inventoryForm.stacks.map(_.itemId))
            _ <- saveInventoryWithItems(inventoryEntity, inventoryForm)
            inventory <- findById(id)
        yield inventory
    } @@ Log.timed("InventoryServiceImpl::update")


    override def delete(id: Int): Task[Unit] = {
        for
            _ <- getById(id).map(_._1)
            _ <- inventoryRepository.delete(id)
        yield ()
    } @@ Log.timed("InventoryServiceImpl::delete")

    private def getById(id: Int) =
        inventoryRepository.findById(id).flatMap {
            case Some(inventory) => ZIO.succeed(inventory)
            case None => ZIO.fail(NotFoundException(s"Inventory id: $id not found"))
        }

    private def inventoryFrom(inventory: (InventoryEntity, Seq[(InventoryStackEntity, ItemEntity)])): Inventory = {
        val (inventoryEntity, items) = inventory
        val inventoryStacks = items.map { case (inventoryStackEntity, itemEntity) =>
            val item = Item.from(itemEntity)
            InventoryStack(item = item, amount = inventoryStackEntity.amount)
        }
        Inventory.from(inventoryEntity, inventoryStacks)
    }

    private def inventoriesFrom(inventories: Seq[(InventoryEntity, Seq[(InventoryStackEntity, ItemEntity)])]): Seq[Inventory] =
        inventories.map(inventoryFrom)

    private def validateAllItems(itemIds: Seq[Int]): Task[Unit] = {
        itemRepository.findAllByIdIn(itemIds).flatMap { itemEntities =>
            val missingIds = itemIds.toSet -- itemEntities.map(_.id).toSet
            ZIO.fail(NotFoundException(s"Items id: ${missingIds.mkString(", ")} not found"))
                .unless(missingIds.isEmpty)
                .unit
        }
    }

    private def saveInventoryWithItems(inventoryEntity: InventoryEntity, inventoryForm: InventoryForm): Task[Int] = {
        val inventoryEntityUpdated = inventoryEntity.copy(name = inventoryForm.name)
        val inventoryStackEntitiesUpdated = inventoryForm.stacks.map { inventoryStack =>
            InventoryStackEntity(
                inventoryId = inventoryEntity.id,
                itemId = inventoryStack.itemId,
                amount = inventoryStack.amount
            )
        }

        inventoryRepository.save(inventoryEntityUpdated, inventoryStackEntitiesUpdated)
    }
}

object InventoryServiceImpl {
    lazy val layer = ZLayer.derive[InventoryServiceImpl]
}