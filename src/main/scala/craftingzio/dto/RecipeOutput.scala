package craftingzio.dto

import zio.json.{DeriveJsonCodec, JsonCodec}

case class RecipeOutput(
    item: Item,
    amount: Int
)

object RecipeOutput {
    given JsonCodec[RecipeOutput] = DeriveJsonCodec.gen
}