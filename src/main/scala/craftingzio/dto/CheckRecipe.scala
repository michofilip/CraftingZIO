package craftingzio.dto

import zio.json.{DeriveJsonCodec, JsonCodec}

case class CheckRecipe(canCraft: Boolean, cannotCraftReason: Option[String])

object CheckRecipe {
    given JsonCodec[CheckRecipe] = DeriveJsonCodec.gen

    def yes: CheckRecipe = CheckRecipe(true, None)

    def no(reason: String): CheckRecipe = CheckRecipe(false, Some(reason))
}