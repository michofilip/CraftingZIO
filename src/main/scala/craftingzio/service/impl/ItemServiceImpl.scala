package craftingzio.service.impl

import craftingzio.db.model.ItemEntity
import craftingzio.db.repository.ItemRepository
import craftingzio.dto.Item
import craftingzio.exceptions.NotFoundException
import craftingzio.service.ItemService
import craftingzio.utils.Log
import zio.{Task, ZIO, ZLayer}

case class ItemServiceImpl(itemRepository: ItemRepository) extends ItemService {
    override def findAll: Task[Seq[Item]] = {
        for
            items <- itemRepository.findAll
        yield items.map(Item.from)
    } @@ Log.timed("ItemServiceImpl::findAll")

    override def findById(id: Int): Task[Item] = {
        getById(id).map(Item.from)
    } @@ Log.timed("ItemServiceImpl::findById")

    private def getById(id: Int): Task[ItemEntity] = itemRepository.findById(id).flatMap {
        case Some(itemEntity) => ZIO.succeed(itemEntity)
        case None => ZIO.fail(NotFoundException(s"Item id: $id not found"))
    }
}

object ItemServiceImpl {
    lazy val layer = ZLayer.derive[ItemServiceImpl]
}