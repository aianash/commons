package goshoplane.commons.core.factories

import com.goshoplane.common._

import scalaz._, Scalaz._

object Common {

  def address(gpsLoc:   Option[GPSLocation],
              title:    Option[String],
              short:    Option[String],
              full:     Option[String],
              pincode:  Option[String],
              country:  Option[String],
              city:     Option[String]) = {

    val empty    = PostalAddress()
    var addressO = gpsLoc.map(v => PostalAddress(gpsLoc = v.some))
    addressO     = title    .map {_ => addressO.getOrElse(empty).copy(title   =  title) }
    addressO     = short    .map {_ => addressO.getOrElse(empty).copy(short   =  short) }
    addressO     = full     .map {_ => addressO.getOrElse(empty).copy(full    =  full) }
    addressO     = pincode  .map {_ => addressO.getOrElse(empty).copy(pincode =  pincode) }
    addressO     = country  .map {_ => addressO.getOrElse(empty).copy(country =  country) }
    addressO     = city     .map {_ => addressO.getOrElse(empty).copy(city    =  city) }

    addressO
  }


  def storeName(full: Option[String], handle: Option[String]) = {
    val empty      = StoreName()
    var storeNameO = Option.empty[StoreName]
    storeNameO     = full   .map {_ => storeNameO.getOrElse(empty).copy(full   = full) }
    storeNameO     = handle .map {_ => storeNameO.getOrElse(empty).copy(handle = handle) }

    storeNameO
  }


  def storeAvatar(small: Option[String], medium: Option[String], large: Option[String]) = {
    val empty        = StoreAvatar()
    var storeAvatarO = Option.empty[StoreAvatar]
    storeAvatarO     = small  .map {_ => storeAvatarO.getOrElse(empty).copy(small  = small) }
    storeAvatarO     = medium .map {_ => storeAvatarO.getOrElse(empty).copy(medium = medium) }
    storeAvatarO     = large  .map {_ => storeAvatarO.getOrElse(empty).copy(large  = large) }

    storeAvatarO
  }


  def phoneContact(numbers: Seq[String]) =
    numbers.some.filter(!_.isEmpty).map(PhoneContact(_))


  def storeInfo(name:       Option[StoreName],
                itemTypes:  Option[Seq[ItemType]],
                address:    Option[PostalAddress],
                avatar:     Option[StoreAvatar],
                email:      Option[String],
                phone:      Option[PhoneContact]) = {

    val empty      = StoreInfo()
    var storeInfoO = Option.empty[StoreInfo]
    storeInfoO     = name       .map {_ => storeInfoO.getOrElse(empty).copy(name      = name) }
    storeInfoO     = itemTypes  .map {_ => storeInfoO.getOrElse(empty).copy(itemTypes = itemTypes) }
    storeInfoO     = address    .map {_ => storeInfoO.getOrElse(empty).copy(address   = address) }
    storeInfoO     = avatar     .map {_ => storeInfoO.getOrElse(empty).copy(avatar    = avatar) }
    storeInfoO     = email      .map {_ => storeInfoO.getOrElse(empty).copy(email     = email) }
    storeInfoO     = phone      .map {_ => storeInfoO.getOrElse(empty).copy(phone     = phone) }

    storeInfoO
  }
}