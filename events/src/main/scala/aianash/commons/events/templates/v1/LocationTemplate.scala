package aianash.commons.events.templates.v1

import org.msgpack.packer.Packer
import org.msgpack.unpacker.Unpacker
import org.msgpack.template.{Templates, AbstractTemplate}
import org.msgpack.ScalaMessagePack

import aianash.commons.events._

object LocationTemplate {

  import Location.Codes._

  implicit val webPageTemplate = new WebPageTemplate
  implicit val appTemplate = new AppTemplate

  def write(packer: Packer, location: Location) = {
    location match {
      case wp : WebPage =>
        packer.write(Location.codeFor(wp))
        packer.write(ScalaMessagePack.writeT(wp))
      case app: App =>
        packer.write(Location.codeFor(app))
        packer.write(ScalaMessagePack.writeT(app))
    }
  }

  def read(unpacker: Unpacker, locationType: Char) = {
    import ScalaMessagePack.messagePack
    locationType match {
      case WEB_PAGE =>
        val serialized = unpacker.read(Templates.TByteArray)
        messagePack.read(serialized, webPageTemplate)
      case APP =>
        val serialized = unpacker.read(Templates.TByteArray)
        messagePack.read(serialized, appTemplate)
    }
  }

}
