package craftingzio.controller

import craftingzio.exceptions.*
import zio.*
import zio.http.model.Status
import zio.http.{Request, Response}
import zio.json.*

private[controller] trait Controller {
    extension[B] (request: Request) {
        inline def fromJson[A: JsonDecoder](mapping: A => Task[B]): Task[B] = request.body.asString.map(_.fromJson[A]).flatMap {
            case Left(_) => ZIO.fail(BadRequestException("Bad request"))
            case Right(userForm) => ZIO.succeed(userForm)
        }.flatMap(mapping)
    }

    extension[A] (zio: Task[A]) {
        inline def toResponse(resultMapping: A => Response): URIO[Any, Response] = zio.fold(ExceptionHandler.errorResponse, resultMapping)
        inline def toResponse(status: Status): URIO[Any, Response] = toResponse(_ => Response(status))
    }

    extension[A: JsonEncoder] (zio: Task[A]) {
        inline def toJsonResponse: URIO[Any, Response] = zio.map(_.toJson).toResponse(Response.json)
    }
}
