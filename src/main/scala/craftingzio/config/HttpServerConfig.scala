package craftingzio.config

import zio.http.{Server, ServerConfig}
import zio.{ZIO, ZLayer}

object HttpServerConfig {
    lazy val layer = ZLayer {
        for
            applicationConfig <- ZIO.service[ApplicationConfig]
        yield ServerConfig.default.port(applicationConfig.port)
    } >>> Server.live
}
