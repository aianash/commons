package aianash.commons.events.templates.v1

import org.joda.time.{Duration, DateTime}

import org.msgpack.packer.Packer
import org.msgpack.unpacker.Unpacker
import org.msgpack.template.{Templates, AbstractTemplate}
import org.msgpack.ScalaMessagePack.messagePack

import aianash.commons.events._

class ScanningTemplate extends AbstractTemplate[Scanning] {

  import Implicits._

  def write(packer: Packer, from: Scanning, required: Boolean) = {
    if(from == null) {
      if(required) throw new NullPointerException
      packer.writeNil
    } else {
      packer.writeArrayBegin(7)
      val binary = messagePack.write(from.location, locationTemplate)
      packer.write(binary)
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
      val binary = unpacker.read(Templates.TByteArray)
      val location = messagePack.read(binary, locationTemplate)
      val fromPosX = unpacker.read(Templates.TInteger)
      val fromPosY = unpacker.read(Templates.TInteger)
      val toPosX = unpacker.read(Templates.TInteger)
      val toPosY = unpacker.read(Templates.TInteger)
      val startTime = unpacker.read(Templates.TLong)
      val duration = unpacker.read(Templates.TLong)
      unpacker.readArrayEnd

      val fromPos = Position(fromPosX, fromPosY)
      val toPos = Position(toPosX, toPosY)

      Scanning(location, fromPos, toPos, new DateTime(startTime), new Duration(duration))
    }
  }
}
