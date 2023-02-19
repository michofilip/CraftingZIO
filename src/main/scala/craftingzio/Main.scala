package craftingzio

import craftingzio.Main.Environment
import craftingzio.config.{ApplicationConfig, DbConfig, HttpServerConfig, SLF4JConfig}
import craftingzio.controller.{InventoryController, ItemController}
import craftingzio.db.repository.impl.{InventoryRepositoryImpl, ItemRepositoryImpl}
import craftingzio.service.impl.{InventoryServiceImpl, ItemServiceImpl}
import zio.*
import zio.Console.printLine
import zio.http.Server

object Main extends ZIOAppDefault {

    override val bootstrap: ZLayer[ZIOAppArgs, Any, Any] = SLF4JConfig.layer

    private val app = for
        _ <- ZIO.logInfo("Welcome to CraftingZIO")
        itemController <- ZIO.service[ItemController]
        inventoryController <- ZIO.service[InventoryController]
        routes = itemController.routes ++ inventoryController.routes
        port <- Server.install(routes)
        _ <- ZIO.logInfo(s"Server started at port: $port")
        _ <- ZIO.never
    yield ()

    override def run: ZIO[Environment & ZIOAppArgs & Scope, Any, Any] =
        app.provide(
            ItemController.layer,
            InventoryController.layer,
            ItemServiceImpl.layer,
            ItemRepositoryImpl.layer,
            InventoryServiceImpl.layer,
            InventoryRepositoryImpl.layer,
            DbConfig.layer,
            ApplicationConfig.layer,
            HttpServerConfig.layer,
            ZLayer.Debug.mermaid
        ).exitCode
}