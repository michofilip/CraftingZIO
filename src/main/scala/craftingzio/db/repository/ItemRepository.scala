package craftingzio.db.repository

import craftingzio.db.model.ItemEntity
import craftingzio.exceptions.NotFoundException
import zio.*

trait ItemRepository {
    def findAll: Task[Seq[ItemEntity]]

    def findById(id: Int): Task[ItemEntity]

    def findAllByIdIn(ids: Seq[Int]): Task[Seq[ItemEntity]]
}
