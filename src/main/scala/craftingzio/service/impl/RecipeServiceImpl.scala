package craftingzio.service.impl

import craftingzio.db.model.{ItemEntity, RecipeEntity, RecipeInputEntity, RecipeOutputEntity}
import craftingzio.db.repository.RecipeRepository
import craftingzio.dto.{Item, Recipe, RecipeInput, RecipeOutput}
import craftingzio.exceptions.NotFoundException
import craftingzio.service.RecipeService
import craftingzio.utils.Log
import zio.{Task, ZIO, ZLayer}

case class RecipeServiceImpl(private val recipeRepository: RecipeRepository)
    extends RecipeService {
    override def findAll: Task[Seq[Recipe]] = {
        recipeRepository.findAll.map(recipesFrom)
    } @@ Log.timed("RecipeServiceImpl::findAll")

    override def findById(id: Int): Task[Recipe] = {
        getById(id).map(recipeFrom)
    } @@ Log.timed("RecipeServiceImpl::findById")

    override private[service] def getById(id: Int): Task[(RecipeEntity, Seq[(RecipeInputEntity, ItemEntity)], Seq[(RecipeOutputEntity, ItemEntity)])] =
        recipeRepository.findById(id).flatMap {
            case Some(recipe) => ZIO.succeed(recipe)
            case None => ZIO.fail(NotFoundException(s"Recipe id: $id not found"))
        }

    private def recipeFrom(recipe: (RecipeEntity, Seq[(RecipeInputEntity, ItemEntity)], Seq[(RecipeOutputEntity, ItemEntity)])): Recipe = {
        val (recipeEntity, inputs, outputs) = recipe
        val recipeInputs = inputs.map { case (recipeInputEntity, itemEntity) =>
            val item = Item.from(itemEntity)
            RecipeInput(item = item, amount = recipeInputEntity.amount)
        }
        val recipeOutputs = outputs.map { case (recipeOutputEntity, itemEntity) =>
            val item = Item.from(itemEntity)
            RecipeOutput(item = item, amount = recipeOutputEntity.amount)
        }

        Recipe.from(recipeEntity, recipeInputs, recipeOutputs)
    }

    private def recipesFrom(recipes: Seq[(RecipeEntity, Seq[(RecipeInputEntity, ItemEntity)], Seq[(RecipeOutputEntity, ItemEntity)])]): Seq[Recipe] =
        recipes.map(recipeFrom)
}

object RecipeServiceImpl {
    lazy val layer = ZLayer.derive[RecipeServiceImpl]
}