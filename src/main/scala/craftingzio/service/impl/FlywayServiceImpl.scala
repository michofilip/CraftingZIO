package craftingzio.service.impl

import craftingzio.service.FlywayService
import org.flywaydb.core.Flyway
import zio.{Task, ZIO, ZLayer}

import javax.sql.DataSource

case class FlywayServiceImpl(
    //    private val flywayConfig: FlywayConfig
    //    private val dataSource: DataSource
) extends FlywayService {
    override protected def run: Task[Unit] = ZIO.attemptUnsafe { _ =>
        val flyway = Flyway.configure()
            //          .dataSource(dataSource)
            //            .locations(flywayConfig.locations)
            .dataSource(
                "jdbc:postgresql://localhost:5432/crafting_db?verifyServerCertificate=false&useSSL=false",
                "postgres",
                "postgres"
            )
            .locations("filesystem:src/main/resources/database/migrations")
            .load()

        flyway.migrate()
    }
}

object FlywayServiceImpl {
    //    lazy val layer = ZLayer.derive[FlywayService]
    lazy val layer = ZLayer.fromFunction(FlywayServiceImpl.apply _)
}
