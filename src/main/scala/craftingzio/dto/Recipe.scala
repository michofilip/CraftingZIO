package craftingzio.dto

import craftingzio.db.model.RecipeEntity
import zio.json.{DeriveJsonCodec, JsonCodec}

case class Recipe(id: Int, name: String, input: Seq[RecipeInput], output: Seq[RecipeOutput])

object Recipe {
    given JsonCodec[Recipe] = DeriveJsonCodec.gen

    def from(recipeEntity: RecipeEntity, input: Seq[RecipeInput], output: Seq[RecipeOutput]): Recipe =
        Recipe(
            id = recipeEntity.id,
            name = recipeEntity.name,
            input = input,
            output = output
        )
}