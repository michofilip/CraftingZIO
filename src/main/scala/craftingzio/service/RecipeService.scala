package craftingzio.service

import craftingzio.dto.Recipe
import zio.Task

trait RecipeService {
    def findAll: Task[Seq[Recipe]]

    def findById(id: Int): Task[Recipe]
}
