package craftingzio.form

import craftingzio.exceptions.ValidationException
import zio.json.{DeriveJsonCodec, JsonCodec}
import zio.prelude.{Validation, ZValidation}
import zio.{IO, ZIO}

import scala.annotation.tailrec
import scala.collection.immutable.{AbstractSeq, LinearSeq}
import scala.meta.internal.io.ListFiles

case class InventoryForm(
    name: String,
    stacks: Seq[InventoryStackForm]
)

object InventoryForm {
    given JsonCodec[InventoryForm] = DeriveJsonCodec.gen

    def validateZIO(inventoryForm: InventoryForm): IO[ValidationException, InventoryForm] =
        validate(inventoryForm).mapError(ValidationException.apply).toZIO

    private[form] def validate(inventoryForm: InventoryForm): Validation[String, InventoryForm] =
        ZValidation.validateWith(
            validateName(inventoryForm.name),
            validateStacks(inventoryForm.stacks)
        )(InventoryForm.apply)

    private def validateName(name: String): Validation[String, String] =
        if name.isBlank then
            ZValidation.fail("Name cannot be blank")
        else
            ZValidation.succeed(name)

    private def validateStacks(stacks: Seq[InventoryStackForm]): Validation[String, Seq[InventoryStackForm]] =
        ZValidation.validateAll(stacks.map(InventoryStackForm.validate))

}
