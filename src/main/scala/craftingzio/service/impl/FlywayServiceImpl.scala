package craftingzio.service.impl

import craftingzio.config.FlywayConfig
import craftingzio.service.FlywayService
import org.flywaydb.core.Flyway
import zio.{Task, Unsafe, ZIO, ZLayer}

import javax.sql.DataSource

case class FlywayServiceImpl(
    private val flywayConfig: FlywayConfig,
    private val dataSource: DataSource
) extends FlywayService {
    override protected def run: Task[Unit] = ZIO.attemptUnsafe { _ =>
        val flyway = Flyway.configure()
            .dataSource(dataSource)
            .locations(flywayConfig.locations)
            .load()

        flyway.migrate()
    }
}

object FlywayServiceImpl {
    lazy val layer = ZLayer.derive[FlywayServiceImpl]
}
