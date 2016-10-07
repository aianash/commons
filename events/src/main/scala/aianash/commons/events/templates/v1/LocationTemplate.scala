package aianash.commons.events.templates.v1

import org.msgpack.packer.Packer
import org.msgpack.unpacker.Unpacker
import org.msgpack.template.{Templates, AbstractTemplate}
import org.msgpack.ScalaMessagePack.messagePack

import aianash.commons.events._

class LocationTemplate extends AbstractTemplate[Location] {

  import Location.Codes._
  import Implicits._

  def write(packer: Packer, from: Location, required: Boolean) = {
    if(from == null) {
      if(required) throw new NullPointerException
      packer.writeNil
    } else {
      packer.writeArrayBegin(2)
      from match {
        case wp : WebPage =>
          packer.write(Location.codeFor(wp))
          packer.write(messagePack.write(wp, webPageTemplate))
        case app: App =>
          packer.write(Location.codeFor(app))
          packer.write(messagePack.write(app, appTemplate))
      }
      packer.writeArrayEnd
    }
  }

  def read(unpacker: Unpacker, q: Location, required: Boolean) = {
    if(!required && unpacker.trySkipNil) {
      null.asInstanceOf[Location]
    } else {
      unpacker.readArrayBegin
      val locationType: Char = unpacker.read(Templates.TCharacter)
      val location = locationType match {
        case WEB_PAGE =>
          val serialized = unpacker.read(Templates.TByteArray)
          messagePack.read(serialized, webPageTemplate)
        case APP =>
          val serialized = unpacker.read(Templates.TByteArray)
          messagePack.read(serialized, appTemplate)
      }
      unpacker.readArrayEnd
      location
    }
  }

}
