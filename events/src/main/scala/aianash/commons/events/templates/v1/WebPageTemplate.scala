package aianash.commons.events.templates.v1

import org.msgpack.packer.Packer
import org.msgpack.unpacker.Unpacker
import org.msgpack.template.{Templates, AbstractTemplate}

import aianash.commons.events._

class WebPageTemplate extends AbstractTemplate[WebPage] {

  def write(packer: Packer, from: WebPage, required: Boolean) = {
    if(from == null) {
      if(required) throw new NullPointerException
      packer.writeNil
    } else {
      packer.writeArrayBegin(2)
      packer.write(from.websiteId.stuuid)
      packer.write(from.pageId.pguuid)
      packer.writeArrayEnd
    }
  }

  def read(unpacker: Unpacker, q: WebPage, required: Boolean) = {
    if(!required && unpacker.trySkipNil) {
      null.asInstanceOf[WebPage]
    } else {
      unpacker.readArrayBegin
      val websiteId = unpacker.read(Templates.TLong)
      val pageId = unpacker.read(Templates.TLong)
      unpacker.readArrayEnd

      WebPage(WebsiteId(websiteId), PageId(pageId))
    }
  }
}
