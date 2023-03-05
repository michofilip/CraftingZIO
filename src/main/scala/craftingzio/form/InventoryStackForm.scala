package craftingzio.form

import zio.json.{DeriveJsonCodec, JsonCodec}

case class InventoryStackForm(
    itemId: Int,
    amount: Int
)

object InventoryStackForm {
    given JsonCodec[InventoryStackForm] = DeriveJsonCodec.gen
}
