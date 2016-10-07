package aianash.commons.events.templates.v1

import scala.collection.JavaConversions._

import org.joda.time.DateTime

import play.api.libs.json._

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
      packer.writeArrayBegin(5)
      LocationTemplate.write(packer, from.location)
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
      val size = unpacker.readArrayBegin
      val locationType = unpacker.read(Templates.TCharacter)
      val location = LocationTemplate.read(unpacker, locationType)
      val timeStamp = unpacker.read(Templates.TLong)
      val name = unpacker.read(Templates.TString)
      val props = unpacker.read(Templates.TString)
      unpacker.readArrayEnd

      Action(location, new DateTime(timeStamp), name, Json.parse(props).as[JsObject])
    }
  }
}
