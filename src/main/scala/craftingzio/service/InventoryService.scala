package craftingzio.service

import craftingzio.dto.Inventory
import craftingzio.form.InventoryForm
import zio.Task

trait InventoryService {
    def findAll: Task[Seq[Inventory]]

    def findById(id: Int): Task[Inventory]

    def create(inventoryForm: InventoryForm): Task[Inventory]

    def update(id: Int, inventoryForm: InventoryForm): Task[Inventory]

    def delete(id: Int): Task[Unit]
}
