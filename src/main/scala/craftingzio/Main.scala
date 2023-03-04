package craftingzio

import craftingzio.Main.Environment
import craftingzio.config.*
import craftingzio.controller.{CraftingController, InventoryController, ItemController, RecipeController}
import craftingzio.db.repository.impl.{InventoryRepositoryImpl, ItemRepositoryImpl, RecipeRepositoryImpl}
import craftingzio.service.FlywayService
import craftingzio.service.impl.*
import org.flywaydb.core.Flyway
import zio.*
import zio.http.Server

object Main extends ZIOAppDefault {

    override val bootstrap: ZLayer[ZIOAppArgs, Any, Any] = SLF4JConfig.layer

    private val app = for
        _ <- FlywayService.run
        _ <- ZIO.logInfo("Welcome to CraftingZIO")

        itemController <- ZIO.service[ItemController]
        inventoryController <- ZIO.service[InventoryController]
        recipeController <- ZIO.service[RecipeController]
        craftingController <- ZIO.service[CraftingController]

        routes = itemController.routes ++ inventoryController.routes ++ recipeController.routes ++ craftingController.routes

        port <- Server.install(routes)
        _ <- ZIO.logInfo(s"Server started at port: $port")

        _ <- ZIO.never
    yield ()

    override def run: ZIO[Environment & ZIOAppArgs & Scope, Any, Any] =
        app.provide(
            ItemController.layer,
            InventoryController.layer,
            RecipeController.layer,
            CraftingController.layer,

            ItemServiceImpl.layer,
            RecipeRepositoryImpl.layer,
            InventoryServiceImpl.layer,
            CraftingServiceImpl.layer,
            FlywayServiceImpl.layer,

            ItemRepositoryImpl.layer,
            InventoryRepositoryImpl.layer,
            RecipeServiceImpl.layer,

            DbConfig.layer,
            ApplicationConfig.layer,
            HttpServerConfig.layer,
            FlywayConfig.layer,

            ZLayer.Debug.mermaid
        ).exitCode
}