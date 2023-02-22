package craftingzio.controller

import craftingzio.form.RecipeMakeForm
import craftingzio.service.{RecipeMakerService, RecipeService}
import zio.*
import zio.http.*
import zio.http.model.{Method, Status}
import zio.json.*

case class RecipeController(private val recipeService: RecipeService,
                            private val recipeMakerService: RecipeMakerService)
    extends Controller {

    def routes = Http.collectZIO[Request] {
        case Method.GET -> !! / "recipes" => recipeService.findAll.toJsonResponse

        case Method.GET -> !! / "recipes" / id => recipeService.findById(id.toInt).toJsonResponse

        case request@Method.POST -> !! / "recipes" / "make" => request.fromJson[RecipeMakeForm](recipeMakerService.makeRecipe).toJsonResponse
    }

}

object RecipeController {
    lazy val layer = ZLayer.derive[RecipeController]
}