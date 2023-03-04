//package craftingzio.config
//
//import org.flywaydb.core.Flyway
//import zio.{Task, ZIO, ZLayer}
//
//import javax.sql.DataSource
//
//case class FlywayService(
//    //    private val flywayConfig: FlywayConfig
//    //    private val dataSource: DataSource
//) {
//    private def run: Task[Unit] = ZIO.attemptUnsafe { _ =>
//        val flyway = Flyway.configure()
//            //          .dataSource(dataSource)
//            .dataSource(
//                "jdbc:postgresql://localhost:5432/crafting_db?verifyServerCertificate=false&useSSL=false",
//                "postgres",
//                "postgres"
//            )
//            //            .locations(flywayConfig.locations)
//            .locations("filesystem:src/main/resources/database/migrations")
//            .load()
//
//        flyway.migrate()
//    }
//}
//
//object FlywayService {
//    //    lazy val layer = ZLayer.derive[FlywayService]
//    lazy val layer = ZLayer.succeed(FlywayService())
//
//    def run: ZIO[FlywayService, Throwable, Unit] = ZIO.serviceWithZIO[FlywayService](_.run)
//}
