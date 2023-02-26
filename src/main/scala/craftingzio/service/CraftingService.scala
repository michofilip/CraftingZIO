package craftingzio.service

import craftingzio.dto.{CheckRecipe, Inventory}
import craftingzio.form.CraftingForm
import zio.Task

trait CraftingService {
    def craftRecipe(recipeMakeForm: CraftingForm): Task[Inventory]

    def checkRecipe(recipeMakeForm: CraftingForm): Task[CheckRecipe]
}
