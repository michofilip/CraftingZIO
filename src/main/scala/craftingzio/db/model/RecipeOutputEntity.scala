package craftingzio.db.model

import io.getquill.*

case class RecipeOutputEntity(recipeId: Int, itemId: Int, amount: Int)

object RecipeOutputEntity {
    inline given SchemaMeta[RecipeOutputEntity] = schemaMeta(
        "recipe_output",
        _.recipeId -> "recipe_id",
        _.itemId -> "item_id",
        _.amount -> "amount"
    )
}