package craftingzio.db.repository

import zio.stream.ZStream
import zio.{Task, ZIO, ZLayer}

import java.sql.SQLException
import javax.sql.DataSource
import scala.Conversion

private[repository] trait DataSourceAutoProvider {

    protected val dataSource: DataSource

    inline given[T]: Conversion[ZIO[DataSource, Throwable, T], Task[T]] = _.provideLayer(ZLayer.succeed(dataSource))

    inline given[T]: Conversion[ZStream[DataSource, Throwable, T], ZStream[Any, Throwable, T]] = _.provideLayer(ZLayer.succeed(dataSource))
}
