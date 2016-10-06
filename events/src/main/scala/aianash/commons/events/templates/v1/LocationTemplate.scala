package aianash.commons.events.templates.v1

import org.msgpack.packer.Packer
import org.msgpack.unpacker.Unpacker
import org.msgpack.template.{Templates, AbstractTemplate}

import aianash.commons.events._

class LocationTemplate extends AbstractTemplate[Location] {

  implicit val webPageTemplate = new WebPageTemplate
  implicit val appTemplate = new AppTemplate

  def write(packer: Packer, from: Location, required: Boolean) = {
    if(from == null) {
      if(required) throw new NullPointerException
      packer.writeNil
    } else {
      from match {
        case wp : WebPage =>
          packer.write(wp)
        case app: App =>
          packer.write(app)
      }
    }
  }

  def read(unpacker: Unpacker, q: Location, required: Boolean) = {
    if(!required && unpacker.trySkipNil) {
      null.asInstanceOf[Location]
    } else {
      q match {
        case wp : WebPage =>
          unpacker.read(webPageTemplate)
        case app: App =>
          unpacker.read(appTemplate)
      }
    }
  }

}
