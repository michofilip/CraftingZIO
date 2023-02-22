package craftingzio.form

import zio.json.{DeriveJsonCodec, JsonCodec}

case class RecipeMakeForm(recipeId: Int, inventoryId: Int)

object RecipeMakeForm {
    given JsonCodec[RecipeMakeForm] = DeriveJsonCodec.gen
}
