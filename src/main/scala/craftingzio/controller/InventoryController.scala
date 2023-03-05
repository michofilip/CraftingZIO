package craftingzio.controller

import craftingzio.form.InventoryForm
import craftingzio.service.{InventoryService, ItemService}
import zio.*
import zio.http.*
import zio.http.model.{Method, Status}
import zio.json.*

case class InventoryController(
    private val inventoryService: InventoryService
) extends Controller {

    def routes = Http.collectZIO[Request] {
        case Method.GET -> !! / "inventories" => inventoryService.findAll.toJsonResponse

        case Method.GET -> !! / "inventories" / id => inventoryService.findById(id.toInt).toJsonResponse

        case request@Method.POST -> !! / "inventories" => request.fromJson[InventoryForm](inventoryService.create).toJsonResponse

        case request@Method.PUT -> !! / "inventories" / id => request.fromJson[InventoryForm](inventoryService.update(id.toInt, _)).toJsonResponse

        case Method.DELETE -> !! / "inventories" / id => inventoryService.delete(id.toInt).toResponse(Status.NoContent)
    }
}

object InventoryController {
    lazy val layer = ZLayer.derive[InventoryController]
}
