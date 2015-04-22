package goshoplane.commons.catalogue.templates

import org.msgpack.packer.Packer
import org.msgpack.unpacker.Unpacker
import org.msgpack.template.{Templates, AbstractTemplate}

import goshoplane.commons.catalogue.{ItemTypeGroups, ItemTypeGroup}

class ItemTypeGroupsTemplate extends AbstractTemplate[ItemTypeGroups] {

  def write(packer: Packer, from: ItemTypeGroups, required: Boolean) = {
    if(from == null) {
      if(required) throw new NullPointerException
      packer.writeNil
    } else {
      packer.writeArrayBegin(from.groups.size)
      for(group <- from.groups) packer.write(group.id)
      packer.writeArrayEnd
    }
  }



  def read(unpacker: Unpacker, q: ItemTypeGroups, required: Boolean) = {
    if(!required && unpacker.trySkipNil) {
      null.asInstanceOf[ItemTypeGroups]
    } else {
      val size = unpacker.readArrayBegin
      val groups = Array.ofDim[ItemTypeGroup.Value](size)
      for(i <- 0 until size) {
        groups(i) = ItemTypeGroup(unpacker.read(Templates.TInteger))
      }
      unpacker.readArrayEnd

      ItemTypeGroups(groups = groups)
    }
  }

}