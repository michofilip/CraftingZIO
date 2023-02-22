package craftingzio.controller

import craftingzio.service.RecipeService
import zio.*
import zio.http.*
import zio.http.model.{Method, Status}
import zio.json.*

case class RecipeController(private val recipeService: RecipeService)
    extends Controller {

    def routes = Http.collectZIO[Request] {
        case Method.GET -> !! / "recipes" => recipeService.findAll.toJsonResponse

        case Method.GET -> !! / "recipes" / id => recipeService.findById(id.toInt).toJsonResponse
    }

}

object RecipeController {
    lazy val layer = ZLayer.derive[RecipeController]
}