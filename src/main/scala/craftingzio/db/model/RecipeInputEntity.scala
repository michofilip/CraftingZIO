package craftingzio.db.model

import io.getquill.*

case class RecipeInputEntity(recipeId: Int, itemId: Int, amount: Int)

object RecipeInputEntity{
    inline given SchemaMeta[RecipeInputEntity] = schemaMeta(
        "inventory_stack",
        _.recipeId -> "recipe_id",
        _.itemId -> "item_id",
        _.amount -> "amount"
    )
}