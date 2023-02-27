package craftingzio.utils

import zio.config.{PropertyTreePath, ReadError}
import zio.{Tag, Trace, ZIO, ZIOAspect, ZLayer}

object Log {

    def timed(label: String): ZIOAspect[Nothing, Any, Nothing, Any, Nothing, Any] =
        new ZIOAspect[Nothing, Any, Nothing, Any, Nothing, Any] {
            override def apply[R, E, A](zio: ZIO[R, E, A])(using Trace): ZIO[R, E, A] = {
                for
                    zioTimed <- zio.timed
                    (duration, a) = zioTimed
                    _ <- ZIO.logInfo(s"$label succeeded after ${duration.toMillis} milliseconds")
                yield a
            }.tapSomeError {
                case e: Throwable =>
                    ZIO.logError(s"$label failed with message: ${e.getMessage}")
            }
        }

}
