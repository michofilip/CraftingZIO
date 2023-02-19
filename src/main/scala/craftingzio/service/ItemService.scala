package craftingzio.service

import craftingzio.dto.Item
import zio.Task

trait ItemService {
    def findAll: Task[Seq[Item]]

    def findById(id: Int): Task[Item]
}
