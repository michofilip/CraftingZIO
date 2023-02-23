package craftingzio.db.repository

import craftingzio.db.model.{ItemEntity, RecipeEntity, RecipeInputEntity, RecipeOutputEntity}
import zio.Task

trait RecipeRepository {
    def findAll: Task[Seq[(RecipeEntity, Seq[(RecipeInputEntity, ItemEntity)], Seq[(RecipeOutputEntity, ItemEntity)])]]

    def findById(id: Int): Task[(RecipeEntity, Seq[(RecipeInputEntity, ItemEntity)], Seq[(RecipeOutputEntity, ItemEntity)])]
}
