package craftingzio.form

import zio.json.{DeriveJsonCodec, JsonCodec}

case class CraftingForm(recipeId: Int, inventoryId: Int, amount: Int = 1)

object CraftingForm {
    given JsonCodec[CraftingForm] = DeriveJsonCodec.gen
}
