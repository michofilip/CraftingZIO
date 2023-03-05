package craftingzio.controller

import craftingzio.form.CraftingForm
import craftingzio.service.CraftingService
import zio.ZLayer
import zio.http.*
import zio.http.model.{Method, Status}
import zio.json.*

case class CraftingController(
    private val craftingService: CraftingService
) extends Controller {

    def routes = Http.collectZIO[Request] {
        case request@Method.POST -> !! / "crafting" => request.fromJson[CraftingForm](craftingService.craftRecipe).toJsonResponse

        case request@Method.POST -> !! / "crafting" / "check" => request.fromJson[CraftingForm](craftingService.checkRecipe).toJsonResponse
    }
}

object CraftingController {
    lazy val layer = ZLayer.derive[CraftingController]
}