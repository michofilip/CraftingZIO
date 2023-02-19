package craftingzio.db.model

import io.getquill.*

case class RecipeEntity(name: String, id: Int = 0)

object RecipeEntity {
    inline given SchemaMeta[RecipeEntity] = schemaMeta(
        "recipe",
        _.id -> "id",
        _.name -> "name"
    )

    inline given InsertMeta[RecipeEntity] = insertMeta[RecipeEntity](_.id)

    inline given UpdateMeta[RecipeEntity] = updateMeta[RecipeEntity](_.id)
}