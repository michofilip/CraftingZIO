package craftingzio.config

import zio.*
import zio.config.magnolia.{Descriptor, descriptor}
import zio.config.typesafe.TypesafeConfigSource
import zio.config.{PropertyTreePath, ReadError, read}

object Config {
    def fromPrefix[T: Tag : Descriptor](prefix: String): ZIO[Any, ReadError[String], T] = {
        val configSource = TypesafeConfigSource.fromResourcePath.at(PropertyTreePath.$(prefix))
        val configDescriptor = descriptor[T].from(configSource)
        read(configDescriptor)
    }
}
