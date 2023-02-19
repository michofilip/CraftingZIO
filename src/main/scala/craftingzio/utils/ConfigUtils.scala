package craftingzio.utils

import zio.*
import zio.config.magnolia.{Descriptor, descriptor}
import zio.config.typesafe.TypesafeConfigSource
import zio.config.{PropertyTreePath, ReadError, read}

object ConfigUtils {
    def fromPrefix[T: Tag : Descriptor](prefix: String): ZLayer[Any, ReadError[String], T] = {
        val configSource = TypesafeConfigSource.fromResourcePath.at(PropertyTreePath.$(prefix))
        val configDescriptor = descriptor[T].from(configSource)
        ZLayer.fromZIO(read(configDescriptor)).orDie
    }
}
