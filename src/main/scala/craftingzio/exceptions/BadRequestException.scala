package craftingzio.exceptions

case class BadRequestException(private val message: String) extends RuntimeException {
    override def getMessage: String = message
}
