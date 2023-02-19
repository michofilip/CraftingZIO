package craftingzio.db.repository

import craftingzio.db.model.ItemEntity
import zio.Task

trait ItemRepository {
    def findAll: Task[Seq[ItemEntity]]

    def findById(id: Int): Task[Option[ItemEntity]]

    def findAllByIdIn(ids: Seq[Int]): Task[Seq[ItemEntity]]
}
