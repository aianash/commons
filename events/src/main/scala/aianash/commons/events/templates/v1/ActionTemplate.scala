package aianash.commons.events.templates.v1

import scala.collection.JavaConversions._

import org.joda.time.DateTime

import org.msgpack.packer.Packer
import org.msgpack.unpacker.Unpacker
import org.msgpack.template.{Templates, AbstractTemplate}

import aianash.commons.events._

class ActionTemplate extends AbstractTemplate[Action] {

  def write(packer: Packer, from: Action, required: Boolean) = {
    if(from == null) {
      if(required) throw new NullPointerException
      packer.writeNil
    } else {
      packer.writeArrayBegin(4)
      packer.write(from.pageId.pguuid)
      packer.write(from.timeStamp.getMillis)
      packer.write(from.name)
      packer.write(from.props)
      packer.writeArrayEnd
    }
  }

  def read(unpacker: Unpacker, q: Action, required: Boolean) = {
    if(!required && unpacker.trySkipNil) {
      null.asInstanceOf[Action]
    } else {
      val size = unpacker.readArrayBegin
      val pageId = unpacker.read(Templates.TLong)
      val timeStamp = unpacker.read(Templates.TLong)
      val name = unpacker.read(Templates.TString)
      val props = unpacker.read(Templates.tMap(Templates.TString, Templates.TString))
      unpacker.readArrayEnd

      Action(PageId(pageId), new DateTime(timeStamp), name, props.toMap)
    }
  }
}
