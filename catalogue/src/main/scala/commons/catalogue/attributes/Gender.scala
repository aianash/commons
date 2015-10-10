package commons
package catalogue
package attributes

import scala.reflect.NameTransformer._
import scala.util.matching.Regex

import commons.core.util.UnsafeUtil
import commons.catalogue.memory.builder.MemoryBuilder
import commons.catalogue.memory.PreparedMemory


class Gender(id: Short) extends {
  val sizeInBytes = Gender.HEAD_SIZE_BYTES
} with FixedSizeAttribute {

  override private[catalogue] def writeAt(builder: MemoryBuilder, position: Int): Unit =
    builder.putShortAt(position, id)

  override def toString: String =
    ((getClass.getName stripSuffix MODULE_SUFFIX_STRING split '.').last split
      Regex.quote(NAME_JOIN_STRING)).last

}

case object Female extends Gender(0)
case object Male extends Gender(1)
case object Boy extends Gender(2)
case object Girl extends Gender(3)

object Gender extends FixedSizeAttributeConstants {

  def apply(id: Int) = id match {
    case 0 => Female
    case 1 => Male
    case 2 => Boy
    case 3 => Girl
  }

  def apply(name: String) = name.toLowerCase match {
    case "male"   => Male
    case "female" => Female
    case "boy"    => Boy
    case "girl"   => Girl
  }

  override private[catalogue] val HEAD_SIZE_BYTES = UnsafeUtil.SHORT_SIZE_BYTES

  def read(prepared: PreparedMemory) = Gender(prepared.getShort())

}