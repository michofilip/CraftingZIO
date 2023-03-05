package craftingzio.controller

import craftingzio.service.ItemService
import zio.*
import zio.http.*
import zio.http.model.{Method, Status}
import zio.json.*

case class ItemController(
    private val itemService: ItemService
) extends Controller {

    def routes = Http.collectZIO[Request] {
        case Method.GET -> !! / "items" => itemService.findAll.toJsonResponse

        case Method.GET -> !! / "items" / id => itemService.findById(id.toInt).toJsonResponse
    }
}

object ItemController {
    lazy val layer = ZLayer.derive[ItemController]
}
