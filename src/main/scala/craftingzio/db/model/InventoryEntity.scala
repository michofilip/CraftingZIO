package craftingzio.db.model

import io.getquill.*

case class InventoryEntity(
    name: String,
    id: Int = 0
)

object InventoryEntity {
    inline given SchemaMeta[InventoryEntity] = schemaMeta(
        "inventory",
        _.id -> "id",
        _.name -> "name"
    )

    inline given InsertMeta[InventoryEntity] = insertMeta[InventoryEntity](_.id)

    inline given UpdateMeta[InventoryEntity] = updateMeta[InventoryEntity](_.id)
}