package aianash.commons.events.templates.v1

import org.msgpack.packer.Packer
import org.msgpack.unpacker.Unpacker
import org.msgpack.template.{Templates, AbstractTemplate}

import aianash.commons.events._

class AppTemplate extends AbstractTemplate[App] {

  def write(packer: Packer, from: App, required: Boolean) = {
    if(from == null) {
      if(required) throw new NullPointerException
      packer.writeNil
    } else {
      packer.writeArrayBegin(2)
      packer.write(from.appId.appuuid)
      packer.write(from.activityId.actuuid)
      packer.writeArrayEnd
    }
  }

  def read(unpacker: Unpacker, q: App, required: Boolean) = {
    if(!required && unpacker.trySkipNil) {
      null.asInstanceOf[App]
    } else {
      unpacker.readArrayBegin
      val appId = unpacker.read(Templates.TLong)
      val activityId = unpacker.read(Templates.TLong)
      unpacker.readArrayEnd

      App(AppId(appId), ActivityId(activityId))
    }
  }
}
