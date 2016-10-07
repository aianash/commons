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
      packer.write(from.appId.appuuid)
      packer.write(from.activityId.actuuid)
    }
  }

  def read(unpacker: Unpacker, q: App, required: Boolean) = {
    if(!required && unpacker.trySkipNil) {
      null.asInstanceOf[App]
    } else {
      val appId = unpacker.read(Templates.TLong)
      val activityId = unpacker.read(Templates.TLong)
      App(AppId(appId), ActivityId(activityId))
    }
  }
}
