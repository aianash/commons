package aianonymous.commons.events.templates.v1

import org.msgpack.packer.Packer
import org.msgpack.unpacker.Unpacker
import org.msgpack.template.{Templates, AbstractTemplate}

import aianonymous.commons.events._

class ScanningTemplate extends AbstractTemplate[Scanning] {

  def write(packer: Packer, from: Scanning, required: Boolean) = {
    if(from == null) {
      if(required) throw new NullPointerException
      packer.writeNil
    } else {
      packer.writeArrayBegin(6)
      packer.write(from.fromPos.x)
      packer.write(from.fromPos.y)
      packer.write(from.toPos.x)
      packer.write(from.toPos.y)
      packer.write(from.startTime)
      packer.write(from.duration)
      packer.writeArrayEnd
    }
  }

  def read(unpacker: Unpacker, q: Scanning, required: Boolean) = {
    if(!required && unpacker.trySkipNil) {
      null.asInstanceOf[Scanning]
    } else {
      unpacker.readArrayBegin
      val fromPosX = unpacker.read(Templates.TInteger)
      val fromPosY = unpacker.read(Templates.TInteger)
      val toPosX = unpacker.read(Templates.TInteger)
      val toPosY = unpacker.read(Templates.TInteger)
      val startTime = unpacker.read(Templates.TLong)
      val duration = unpacker.read(Templates.TInteger)
      unpacker.readArrayEnd

      val fromPos = Position(fromPosX, fromPosY)
      val toPos = Position(toPosX, toPosY)

      Scanning(fromPos, toPos, startTime, duration)
    }
  }
}
