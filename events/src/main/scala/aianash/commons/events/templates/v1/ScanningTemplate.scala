package aianash.commons.events.templates.v1

import org.joda.time.{Duration, DateTime}

import org.msgpack.packer.Packer
import org.msgpack.unpacker.Unpacker
import org.msgpack.template.{Templates, AbstractTemplate}

import aianash.commons.events._

class ScanningTemplate extends AbstractTemplate[Scanning] {

  def write(packer: Packer, from: Scanning, required: Boolean) = {
    if(from == null) {
      if(required) throw new NullPointerException
      packer.writeNil
    } else {
      packer.writeArrayBegin(7)
      packer.write(from.pageId.pguuid)
      packer.write(from.fromPos.x)
      packer.write(from.fromPos.y)
      packer.write(from.toPos.x)
      packer.write(from.toPos.y)
      packer.write(from.startTime.getMillis)
      packer.write(from.duration.getMillis)
      packer.writeArrayEnd
    }
  }

  def read(unpacker: Unpacker, q: Scanning, required: Boolean) = {
    if(!required && unpacker.trySkipNil) {
      null.asInstanceOf[Scanning]
    } else {
      unpacker.readArrayBegin
      val pageId = unpacker.read(Templates.TLong)
      val fromPosX = unpacker.read(Templates.TInteger)
      val fromPosY = unpacker.read(Templates.TInteger)
      val toPosX = unpacker.read(Templates.TInteger)
      val toPosY = unpacker.read(Templates.TInteger)
      val startTime = unpacker.read(Templates.TLong)
      val duration = unpacker.read(Templates.TLong)
      unpacker.readArrayEnd

      val fromPos = Position(fromPosX, fromPosY)
      val toPos = Position(toPosX, toPosY)

      Scanning(PageId(pageId), fromPos, toPos, new DateTime(startTime), new Duration(duration))
    }
  }
}
