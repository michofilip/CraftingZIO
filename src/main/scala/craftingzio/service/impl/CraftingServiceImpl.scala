package craftingzio.service.impl

import craftingzio.db.model.*
import craftingzio.db.repository.{InventoryRepository, RecipeRepository}
import craftingzio.dto.{CheckRecipe, Inventory, Recipe}
import craftingzio.exceptions.ValidationException
import craftingzio.form.CraftingForm
import craftingzio.service.{CraftingService, InventoryService}
import craftingzio.utils.Log
import zio.{Task, ZIO, ZLayer}

case class CraftingServiceImpl(
    private val recipeRepository: RecipeRepository,
    private val inventoryRepository: InventoryRepository,
    private val inventoryService: InventoryService
) extends CraftingService {

    override def craftRecipe(craftingForm: CraftingForm): Task[Inventory] = {
        for
            inventoryAndRecipe <- inventoryRepository.findById(craftingForm.inventoryId) <&> recipeRepository.findById(craftingForm.recipeId)

            inventoryEntity = inventoryAndRecipe._1
            inventoryStackEntities = inventoryAndRecipe._2.map(_._1)
            recipeInputEntities = inventoryAndRecipe._3._2.map(_._1)
            recipeOutputEntities = inventoryAndRecipe._3._3.map(_._1)

            _ <- validateRecipe(inventoryStackEntities, recipeInputEntities, craftingForm.amount)
            _ <- craft(inventoryEntity, inventoryStackEntities, recipeInputEntities, recipeOutputEntities, craftingForm.amount)

            inventory <- inventoryService.findById(craftingForm.inventoryId)
        yield inventory
    } @@ Log.timed("RecipeMakerServiceImpl::makeRecipe")


    override def checkRecipe(craftingForm: CraftingForm): Task[CheckRecipe] = {
        for
            inventoryAndRecipe <- inventoryRepository.findById(craftingForm.inventoryId) <&> recipeRepository.findById(craftingForm.recipeId)

            inventoryStackEntities = inventoryAndRecipe._2.map(_._1)
            recipeInputEntities = inventoryAndRecipe._3._2.map(_._1)

            checkRecipe <- validateRecipe(inventoryStackEntities, recipeInputEntities, craftingForm.amount).foldZIO({
                case e: ValidationException => ZIO.succeed(CheckRecipe.no.withMessage(e.getMessage))
                case e => ZIO.fail(e)
            }, _ => ZIO.succeed(CheckRecipe.yes))

        yield checkRecipe
    } @@ Log.timed("RecipeMakerServiceImpl::checkRecipe")

    private def validateRecipe(
        inventoryStackEntities: Seq[InventoryStackEntity],
        recipeInputEntities: Seq[RecipeInputEntity],
        amount: Int
    ): Task[Unit] = {
        val inventory = inventoryStackEntities.map(stack => stack.itemId -> stack.amount).toMap

        val requiredItemIds = recipeInputEntities.filter { input =>
            val stackedAmount = inventory.getOrElse(input.itemId, 0)
            val requiredAmount = input.amount * amount

            stackedAmount < requiredAmount
        }.map(_.itemId)

        ZIO.fail(ValidationException(s"Not enough items id: ${requiredItemIds.mkString(", ")}")).unless(requiredItemIds.isEmpty).unit
    }

    private def craft(
        inventoryEntity: InventoryEntity,
        inventoryStackEntities: Seq[InventoryStackEntity],
        recipeInputEntities: Seq[RecipeInputEntity],
        recipeOutputEntities: Seq[RecipeOutputEntity],
        amount: Int
    ): Task[Unit] = {
        val inventory = inventoryStackEntities.map(stack => stack.itemId -> stack.amount).toMap

        val inventoryStackEntitiesUpdated = Seq(
            recipeInputEntities.map(input => input.itemId -> -input.amount * amount),
            recipeOutputEntities.map(output => output.itemId -> output.amount * amount)
        ).flatten.groupMapReduce(_._1)(_._2)(_ + _).foldLeft(inventory) { case (inventory, (itemId, change)) =>
            val amount = inventory.getOrElse(itemId, 0)
            inventory.updated(itemId, amount + change)
        }.filter { case (_, amount) => amount > 0 }.map { case (itemId, amount) =>
            InventoryStackEntity(inventoryEntity.id, itemId, amount)
        }.toSeq

        inventoryRepository.save(inventoryEntity, inventoryStackEntitiesUpdated).unit
    }

}

object CraftingServiceImpl {
    lazy val layer = ZLayer.derive[CraftingServiceImpl]
}
