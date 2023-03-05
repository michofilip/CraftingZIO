package craftingzio.dto

import craftingzio.db.model.ItemEntity
import zio.json.{DeriveJsonCodec, JsonCodec}

case class Item(
    id: Int,
    name: String
)

object Item {
    given JsonCodec[Item] = DeriveJsonCodec.gen

    def from(itemEntity: ItemEntity): Item =
        Item(
            id = itemEntity.id,
            name = itemEntity.name
        )
}