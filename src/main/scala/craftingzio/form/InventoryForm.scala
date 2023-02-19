package craftingzio.form

import zio.json.{DeriveJsonCodec, JsonCodec}

case class InventoryForm(name: String, stacks: Seq[InventoryStackForm])

object InventoryForm {
    given JsonCodec[InventoryForm] = DeriveJsonCodec.gen
}
