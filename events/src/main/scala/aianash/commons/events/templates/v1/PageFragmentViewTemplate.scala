package aianash.commons.events.templates.v1

import org.joda.time.{Duration, DateTime}

import org.msgpack.packer.Packer
import org.msgpack.unpacker.Unpacker
import org.msgpack.template.{Templates, AbstractTemplate}

import aianash.commons.events._

class PageFragmentViewTemplate extends AbstractTemplate[PageFragmentView] {

  def write(packer: Packer, from: PageFragmentView, required: Boolean) = {
    if(from == null) {
      if(required) throw new NullPointerException
      packer.writeNil
    } else {
      packer.writeArrayBegin(7)
      packer.write(from.pageId.pguuid)
      packer.write(from.scrollPos.x)
      packer.write(from.scrollPos.y)
      packer.write(from.windowHeight)
      packer.write(from.windowWidth)
      packer.write(from.startTime.getMillis)
      packer.write(from.duration.getMillis)
      packer.writeArrayEnd
    }
  }

  def read(unpacker: Unpacker, q: PageFragmentView, required: Boolean) = {
    if(!required && unpacker.trySkipNil) {
      null.asInstanceOf[PageFragmentView]
    } else {
      unpacker.readArrayBegin
      val pageId = unpacker.read(Templates.TLong)
      val scrollPosX = unpacker.read(Templates.TInteger)
      val scrollPosY = unpacker.read(Templates.TInteger)
      val windowHeight = unpacker.read(Templates.TInteger)
      val windowWidth = unpacker.read(Templates.TInteger)
      val startTime = unpacker.read(Templates.TLong)
      val duration = unpacker.read(Templates.TLong)
      unpacker.readArrayEnd

      val scrollPos = Position(scrollPosX, scrollPosY)

      PageFragmentView(PageId(pageId), scrollPos, windowHeight, windowWidth,
        new DateTime(startTime), new Duration(duration))
    }
  }
}
