package craftingzio.dto

import zio.json.{DeriveJsonCodec, JsonCodec}

case class InventoryStack(item: Item, amount: Int)

object InventoryStack {
    given JsonCodec[InventoryStack] = DeriveJsonCodec.gen
}