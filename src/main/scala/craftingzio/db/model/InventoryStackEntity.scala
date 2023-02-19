package craftingzio.db.model

import io.getquill.*

case class InventoryStackEntity(inventoryId: Int, itemId: Int, amount: Int)

object InventoryStackEntity {
    inline given SchemaMeta[InventoryStackEntity] = schemaMeta(
        "inventory_stack",
        _.inventoryId -> "inventory_id",
        _.itemId -> "item_id",
        _.amount -> "amount"
    )
}