package craftingzio.db.model

import io.getquill.*

case class RecipeOutputEntity(recipeId: Int, itemId: Int, amount: Int)

object RecipeOutputEntity {
    inline given SchemaMeta[RecipeOutputEntity] = schemaMeta(
        "inventory_stack",
        _.recipeId -> "recipe_id",
        _.itemId -> "item_id",
        _.amount -> "amount"
    )
}