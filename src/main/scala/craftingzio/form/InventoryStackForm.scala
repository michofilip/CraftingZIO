package craftingzio.form

import craftingzio.exceptions.ValidationException
import zio.IO
import zio.json.{DeriveJsonCodec, JsonCodec}
import zio.prelude.{Validation, ZValidation}

case class InventoryStackForm(
    itemId: Int,
    amount: Int
)

object InventoryStackForm {
    given JsonCodec[InventoryStackForm] = DeriveJsonCodec.gen

    private[form] def validate(inventoryStackForm: InventoryStackForm): Validation[String, InventoryStackForm] =
        ZValidation.validateWith(
            ZValidation.succeed(inventoryStackForm.itemId),
            validateAmount(inventoryStackForm.amount)
        )(InventoryStackForm.apply)

    private def validateAmount(amount: Int): Validation[String, Int] =
        if amount <= 0 then
            ZValidation.fail("Amount must be positive")
        else
            ZValidation.succeed(amount)


}
