package craftingzio.form

import craftingzio.exceptions.ValidationException
import zio.IO
import zio.json.{DeriveJsonCodec, JsonCodec}
import zio.prelude.{Validation, ZValidation}

case class CraftingForm(
    recipeId: Int,
    inventoryId: Int,
    amount: Int = 1
)

object CraftingForm {
    given JsonCodec[CraftingForm] = DeriveJsonCodec.gen

    def validateZIO(craftingForm: CraftingForm): IO[ValidationException, CraftingForm] =
        validate(craftingForm).mapError(ValidationException.apply).toZIO

    private[form] def validate(craftingForm: CraftingForm): Validation[String, CraftingForm] =
        ZValidation.validateWith(
            ZValidation.succeed(craftingForm.recipeId),
            ZValidation.succeed(craftingForm.inventoryId),
            validateAmount(craftingForm.amount)
        )(CraftingForm.apply)

    private def validateAmount(amount: Int): Validation[String, Int] =
        if amount <= 0 then
            ZValidation.fail("Amount must be positive")
        else
            ZValidation.succeed(amount)
}
