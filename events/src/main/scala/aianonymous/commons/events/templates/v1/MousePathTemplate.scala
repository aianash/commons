package aianonymous.commons.events.templates.v1

import scala.collection.mutable.ArrayBuffer

import org.msgpack.packer.Packer
import org.msgpack.unpacker.Unpacker
import org.msgpack.template.{Templates, AbstractTemplate}

import aianonymous.commons.events._

class MousePathTemplate extends AbstractTemplate[MousePath] {

  def write(packer: Packer, from: MousePath, required: Boolean) = {
    if(from == null) {
      if(required) throw new NullPointerException
      packer.writeNil
    } else {
      packer.writeArrayBegin(from.sections.size * 3 + 2)
      for(section <- from.sections) {
        packer.write(section._1)
        packer.write(section._2.x)
        packer.write(section._2.y)
      }
      packer.write(from.startTime)
      packer.write(from.duration)
      packer.writeArrayEnd
    }
  }

  def read(unpacker: Unpacker, q: MousePath, required: Boolean) = {
    if(!required && unpacker.trySkipNil) {
      null.asInstanceOf[MousePath]
    } else {
      val size = unpacker.readArrayBegin
      val sectionsLen: Int = (size - 2) / 3
      val sections = ArrayBuffer.empty[(Int, Position)]
      for(_ <- 0 until sectionsLen) {
        val sectionNum: Int = unpacker.read(Templates.TInteger)
        val posx = unpacker.read(Templates.TInteger)
        val posy = unpacker.read(Templates.TInteger)
        val pos = Position(posx, posy)
        sections += sectionNum -> pos
      }
      val startTime = unpacker.read(Templates.TLong)
      val duration = unpacker.read(Templates.TInteger)
      unpacker.readArrayEnd

      MousePath(sections, startTime, duration)
    }
  }
}
