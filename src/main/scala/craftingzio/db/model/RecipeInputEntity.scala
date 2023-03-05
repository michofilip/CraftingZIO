package craftingzio.db.model

import io.getquill.*

case class RecipeInputEntity(
    recipeId: Int,
    itemId: Int, amount: Int
)

object RecipeInputEntity {
    inline given SchemaMeta[RecipeInputEntity] = schemaMeta(
        "recipe_input",
        _.recipeId -> "recipe_id",
        _.itemId -> "item_id",
        _.amount -> "amount"
    )
}