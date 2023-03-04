//package craftingzio.config
//
//import zio.ZLayer
//
//case class FlywayConfig(locations: String)
//
//object FlywayConfig {
//    lazy val layer = ZLayer.fromZIO(Config.fromPrefix[FlywayConfig]("FlywayConfig")).orDie
//}
