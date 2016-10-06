package aianash.commons.events.templates.v1

import org.joda.time.{Duration, DateTime}

import org.msgpack.packer.Packer
import org.msgpack.unpacker.Unpacker
import org.msgpack.template.{Templates, AbstractTemplate}

import aianash.commons.events._

class SectionViewTemplate extends AbstractTemplate[SectionView] {

  implicit val locationTemplate = new LocationTemplate

  def write(packer: Packer, from: SectionView, required: Boolean) = {
    if(from == null) {
      if(required) throw new NullPointerException
      packer.writeNil
    } else {
      packer.writeArrayBegin(6)
      packer.write(from.location)
      packer.write(from.sectionId)
      packer.write(from.pos.x)
      packer.write(from.pos.y)
      packer.write(from.startTime.getMillis)
      packer.write(from.duration.getMillis)
      packer.writeArrayEnd
    }
  }

  def read(unpacker: Unpacker, q: SectionView, required: Boolean) = {
    if(!required && unpacker.trySkipNil) {
      null.asInstanceOf[SectionView]
    } else {
      unpacker.readArrayBegin
      val location = unpacker.read(locationTemplate)
      val sectionId = unpacker.read(Templates.TInteger)
      val posX = unpacker.read(Templates.TInteger)
      val posY = unpacker.read(Templates.TInteger)
      val startTime = unpacker.read(Templates.TLong)
      val duration = unpacker.read(Templates.TLong)
      unpacker.readArrayEnd

      val pos = Position(posX, posY)

      SectionView(location, sectionId, pos, new DateTime(startTime), new Duration(duration))
    }
  }
}
