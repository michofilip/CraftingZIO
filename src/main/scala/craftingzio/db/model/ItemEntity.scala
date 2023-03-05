package craftingzio.db.model

import io.getquill.*

case class ItemEntity(
    name: String,
    id: Int = 0
)

object ItemEntity {
    inline given SchemaMeta[ItemEntity] = schemaMeta(
        "item",
        _.id -> "id",
        _.name -> "name"
    )

    inline given InsertMeta[ItemEntity] = insertMeta[ItemEntity](_.id)

    inline given UpdateMeta[ItemEntity] = updateMeta[ItemEntity](_.id)
}