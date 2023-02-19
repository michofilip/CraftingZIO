package craftingzio.dto

import craftingzio.db.model.{InventoryEntity, ItemEntity}
import org.checkerframework.checker.units.qual.s
import zio.json.{DeriveJsonCodec, JsonCodec}

case class Inventory(id: Int, name: String, stacks: Seq[InventoryStack])

object Inventory {
    given JsonCodec[Inventory] = DeriveJsonCodec.gen

    def from(inventoryEntity: InventoryEntity, inventoryStacks: Seq[InventoryStack]): Inventory =
        Inventory(
            id = inventoryEntity.id,
            name = inventoryEntity.name,
            stacks = inventoryStacks
        )
}