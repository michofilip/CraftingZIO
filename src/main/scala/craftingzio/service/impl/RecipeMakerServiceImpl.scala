package craftingzio.service.impl

import craftingzio.db.model.{InventoryEntity, InventoryStackEntity, ItemEntity, RecipeEntity, RecipeInputEntity, RecipeOutputEntity}
import craftingzio.db.repository.InventoryRepository
import craftingzio.dto.{Inventory, Recipe}
import craftingzio.exceptions.ValidationException
import craftingzio.form.RecipeMakeForm
import craftingzio.service.{InventoryService, RecipeMakerService, RecipeService}
import craftingzio.utils.Log
import zio.{Task, ZIO, ZLayer}

case class RecipeMakerServiceImpl(private val recipeService: RecipeService,
                                  private val inventoryService: InventoryService,
                                  private val inventoryRepository: InventoryRepository)
    extends RecipeMakerService {

    override def makeRecipe(recipeMakeForm: RecipeMakeForm): Task[Inventory] = {
        for
            inventoryAndRecipe <- inventoryService.getById(recipeMakeForm.inventoryId) <&> recipeService.getById(recipeMakeForm.recipeId)

            inventoryEntity = inventoryAndRecipe._1
            inventoryStackEntities = inventoryAndRecipe._2.map(_._1)
            recipeInputEntities = inventoryAndRecipe._3._2.map(_._1)
            recipeOutputEntities = inventoryAndRecipe._3._3.map(_._1)

            _ <- validateRecipe(inventoryStackEntities, recipeInputEntities)
            _ <- makeRecipe(inventoryEntity, inventoryStackEntities, recipeInputEntities, recipeOutputEntities)

            inventory <- inventoryService.findById(recipeMakeForm.inventoryId)
        yield inventory
    } @@ Log.timed("RecipeMakerServiceImpl::makeRecipe")

    private def validateRecipe(inventoryStackEntities: Seq[InventoryStackEntity],
                               recipeInputEntities: Seq[RecipeInputEntity]): Task[Unit] = {
        val inventory = inventoryStackEntities.map(stack => stack.itemId -> stack.amount).toMap

        val requiredItemIds = recipeInputEntities.filter { input =>
            val stackedAmount = inventory.getOrElse(input.itemId, 0)
            val requiredAmount = input.amount

            stackedAmount < requiredAmount
        }.map(_.itemId)

        ZIO.fail(ValidationException(s"Not enough items id: ${requiredItemIds.mkString(", ")}")).unless(requiredItemIds.isEmpty).unit
    }

    private def makeRecipe(inventoryEntity: InventoryEntity,
                           inventoryStackEntities: Seq[InventoryStackEntity],
                           recipeInputEntities: Seq[RecipeInputEntity],
                           recipeOutputEntities: Seq[RecipeOutputEntity]): Task[Unit] = {
        val inventory = inventoryStackEntities.map(stack => stack.itemId -> stack.amount).toMap

        val inventoryStackEntitiesUpdated = Seq(
            recipeInputEntities.map(input => input.itemId -> -input.amount),
            recipeOutputEntities.map(output => output.itemId -> output.amount)
        ).flatten.groupMapReduce(_._1)(_._2)(_ + _).foldLeft(inventory) { case (inventory, (itemId, change)) =>
            val amount = inventory.getOrElse(itemId, 0)
            inventory.updated(itemId, amount + change)
        }.filter { case (_, amount) => amount > 0 }.map { case (itemId, amount) =>
            InventoryStackEntity(inventoryEntity.id, itemId, amount)
        }.toSeq

        inventoryRepository.save(inventoryEntity, inventoryStackEntitiesUpdated).unit
    }

}

object RecipeMakerServiceImpl {
    lazy val layer = ZLayer.derive[RecipeMakerServiceImpl]
}
