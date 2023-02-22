package craftingzio.service

import craftingzio.dto.Inventory
import craftingzio.form.RecipeMakeForm
import zio.Task

trait RecipeMakerService {
    def makeRecipe(recipeMakeForm: RecipeMakeForm): Task[Inventory]
}
