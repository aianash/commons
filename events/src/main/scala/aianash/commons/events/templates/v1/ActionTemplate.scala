package aianash.commons.events.templates.v1

import scala.collection.JavaConversions._

import org.joda.time.DateTime

import play.api.libs.json._

import org.msgpack.packer.Packer
import org.msgpack.unpacker.Unpacker
import org.msgpack.template.{Templates, AbstractTemplate}
import org.msgpack.ScalaMessagePack.messagePack

import aianash.commons.events._

class ActionTemplate extends AbstractTemplate[Action] {

  import Implicits._

  def write(packer: Packer, from: Action, required: Boolean) = {
    if(from == null) {
      if(required) throw new NullPointerException
      packer.writeNil
    } else {
      packer.writeArrayBegin(4)
      val binary = messagePack.write(from.location, locationTemplate)
      packer.write(binary)
      packer.write(from.timeStamp.getMillis)
      packer.write(from.name)
      packer.write(Json.stringify(from.props))
      packer.writeArrayEnd
    }
  }

  def read(unpacker: Unpacker, q: Action, required: Boolean) = {
    if(!required && unpacker.trySkipNil) {
      null.asInstanceOf[Action]
    } else {
      unpacker.readArrayBegin
      val binary = unpacker.read(Templates.TByteArray)
      val location = messagePack.read(binary, locationTemplate)
      val timeStamp = unpacker.read(Templates.TLong)
      val name = unpacker.read(Templates.TString)
      val props = unpacker.read(Templates.TString)
      unpacker.readArrayEnd

      Action(location, new DateTime(timeStamp), name, Json.parse(props).as[JsObject])
    }
  }
}
