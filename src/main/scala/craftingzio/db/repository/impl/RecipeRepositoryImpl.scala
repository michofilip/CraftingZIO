package craftingzio.db.repository.impl

import craftingzio.db.model.{ItemEntity, RecipeEntity, RecipeInputEntity, RecipeOutputEntity}
import craftingzio.db.repository.{DataSourceAutoProvider, RecipeRepository}
import craftingzio.exceptions.NotFoundException
import io.getquill.*
import zio.*

import javax.sql.DataSource

case class RecipeRepositoryImpl(
    override protected val dataSource: DataSource
) extends PostgresZioJdbcContext(SnakeCase)
    with RecipeRepository
    with DataSourceAutoProvider {
    override def findAll: Task[Seq[(RecipeEntity, Seq[(RecipeInputEntity, ItemEntity)], Seq[(RecipeOutputEntity, ItemEntity)])]] = {
        val r = run(query[RecipeEntity])
        val rii = run(query[RecipeInputEntity].join(query[ItemEntity]).on((r, i) => r.itemId == i.id))
        val roi = run(query[RecipeOutputEntity].join(query[ItemEntity]).on((r, i) => r.itemId == i.id))

        (r <&> rii <&> roi).map { case (recipes, inputs, outputs) =>
            groupFetched(recipes, inputs, outputs)
        }
    }

    override def findById(id: Index): Task[(RecipeEntity, Seq[(RecipeInputEntity, ItemEntity)], Seq[(RecipeOutputEntity, ItemEntity)])] = {
        val r = run(query[RecipeEntity].filter(r => r.id == lift(id)))
        val rii = run(query[RecipeInputEntity].filter(ri => ri.recipeId == lift(id)).join(query[ItemEntity]).on((r, i) => r.itemId == i.id))
        val roi = run(query[RecipeOutputEntity].filter(ro => ro.recipeId == lift(id)).join(query[ItemEntity]).on((r, i) => r.itemId == i.id))

        (r <&> rii <&> roi).map { case (recipes, inputs, outputs) =>
            groupFetched(recipes, inputs, outputs)
        }.map(_.headOption).flatMap {
            case Some(recipe) => ZIO.succeed(recipe)
            case None => ZIO.fail(NotFoundException(s"Recipe id: $id not found"))
        }
    }

    private def groupFetched(recipes: Seq[RecipeEntity], inputs: Seq[(RecipeInputEntity, ItemEntity)], outputs: Seq[(RecipeOutputEntity, ItemEntity)]) = {
        val inputsByRecipeId = inputs.groupBy { case (input, _) => input.recipeId }
        val outputsByRecipeId = outputs.groupBy { case (output, _) => output.recipeId }

        recipes.map { recipe =>
            (recipe, inputsByRecipeId.getOrElse(recipe.id, Seq.empty), outputsByRecipeId.getOrElse(recipe.id, Seq.empty))
        }
    }

}

object RecipeRepositoryImpl {
    lazy val layer = ZLayer.derive[RecipeRepositoryImpl]
}