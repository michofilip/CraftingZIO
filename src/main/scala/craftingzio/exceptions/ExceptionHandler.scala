package craftingzio.exceptions

import zio.*
import zio.http.*
import zio.http.model.Status

object ExceptionHandler {
    def errorResponse: Throwable => Response = {
        case ex: NotFoundException => Response.text(ex.getMessage).setStatus(Status.NotFound)
        case ex: BadRequestException => Response.text(ex.getMessage).setStatus(Status.BadRequest)
        case ex: ValidationException => Response.text(ex.getMessage).setStatus(Status.BadRequest)
        case ex => Response.text(ex.getMessage).setStatus(Status.BadRequest)
    }
}
