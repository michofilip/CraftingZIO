package craftingzio.service.impl

import craftingzio.db.model.ItemEntity
import craftingzio.db.repository.ItemRepository
import craftingzio.dto.Item
import craftingzio.exceptions.NotFoundException
import craftingzio.service.ItemService
import craftingzio.utils.Log
import zio.{Task, ZIO, ZLayer}

case class ItemServiceImpl(
    private val itemRepository: ItemRepository
) extends ItemService {
    override def findAll: Task[Seq[Item]] = {
        itemRepository.findAll.map(_.map(Item.from))
    } @@ Log.timed("ItemServiceImpl::findAll")

    override def findById(id: Int): Task[Item] = {
        itemRepository.findById(id).map(Item.from)
    } @@ Log.timed("ItemServiceImpl::findById")
}

object ItemServiceImpl {
    lazy val layer = ZLayer.derive[ItemServiceImpl]
}