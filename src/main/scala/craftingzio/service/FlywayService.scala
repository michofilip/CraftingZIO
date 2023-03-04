package craftingzio.service

import zio.{Task, ZIO, ZLayer}

trait FlywayService {
    protected def run: Task[Unit]
}

object FlywayService {
    def run: ZIO[FlywayService, Throwable, Unit] = ZIO.serviceWithZIO[FlywayService](_.run)
}