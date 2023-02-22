package craftingzio.dto

import zio.json.{DeriveJsonCodec, JsonCodec}

case class RecipeInput(item: Item, amount: Int)

object RecipeInput {
    given JsonCodec[RecipeInput] = DeriveJsonCodec.gen
}