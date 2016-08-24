package aianonymous.commons.events.templates.v1

import org.msgpack.packer.Packer
import org.msgpack.unpacker.Unpacker
import org.msgpack.template.{Templates, AbstractTemplate}

import aianonymous.commons.events._

class PageFragmentViewTemplate extends AbstractTemplate[PageFragmentView] {

  def write(packer: Packer, from: PageFragmentView, required: Boolean) = {
    if(from == null) {
      if(required) throw new NullPointerException
      packer.writeNil
    } else {
      packer.writeArrayBegin(6)
      packer.write(from.scrollPos.x)
      packer.write(from.scrollPos.y)
      packer.write(from.windowHeight)
      packer.write(from.windowWidth)
      packer.write(from.startTime)
      packer.write(from.duration)
      packer.writeArrayEnd
    }
  }

  def read(unpacker: Unpacker, q: PageFragmentView, required: Boolean) = {
    if(!required && unpacker.trySkipNil) {
      null.asInstanceOf[PageFragmentView]
    } else {
      unpacker.readArrayBegin
      val scrollPosX = unpacker.read(Templates.TInteger)
      val scrollPosY = unpacker.read(Templates.TInteger)
      val windowHeight = unpacker.read(Templates.TInteger)
      val windowWidth = unpacker.read(Templates.TInteger)
      val startTime = unpacker.read(Templates.TLong)
      val duration = unpacker.read(Templates.TInteger)
      unpacker.readArrayEnd

      val scrollPos = Position(scrollPosX, scrollPosY)

      PageFragmentView(scrollPos, windowHeight, windowWidth,
        startTime, duration)
    }
  }
}
