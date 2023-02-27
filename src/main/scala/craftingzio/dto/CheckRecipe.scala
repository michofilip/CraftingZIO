package craftingzio.dto

import zio.json.{DeriveJsonCodec, JsonCodec}

case class CheckRecipe(canCraft: Boolean, message: Option[String]) {
    def withMessage(message: String): CheckRecipe =
        copy(message = Some(message))
}

object CheckRecipe {
    given JsonCodec[CheckRecipe] = DeriveJsonCodec.gen

    def yes: CheckRecipe = CheckRecipe(true, None)

    def no: CheckRecipe = CheckRecipe(false, None)
}