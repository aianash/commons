package commons

import scala.util.control.NonFatal
import scala.util._

import catalogue.memory._


package object catalogue {

  /** Description of function
    *
    * @param Parameter1 - blah blah
    * @return Return value - blah blah
    */
  def ifBrand[T](binary: Array[Byte])(f: Memory => T): T =
    CatalogueItem.ownerTypeOf(binary) match {
      case BRAND => f(PrimaryMemory(binary)) // catch { case NonFatal(ex) => Failure(ex) }
      case _ => throw new IllegalArgumentException("Owner type is not Brand")
    }

    // tryIfBrand(binary)({ x: T => Try(f(x))}).get

  /** Description of function
    *
    * @param Parameter1 - blah blah
    * @return Return value - blah blah
    */
  def tryIfBrand[T](binary: Array[Byte])(f: Memory => Try[T]): Try[T] =
    try ifBrand(binary)(f) catch { case NonFatal(ex) => Failure(ex) }

    // CatalogueItem.ownerTypeOf(binary) match {
    //   case BRAND =>
    //     try f(PrimaryMemory(binary)) catch { case NonFatal(ex) => Failure(ex) }
    //   case _ => Failure(new IllegalArgumentException("Owner type is not Brand"))
    // }

  /** Description of function
    *
    * @param Parameter1 - blah blah
    * @return Return value - blah blah
    */
  def tryIfStore[T](binary: Array[Byte], thenWithBrand: CatalogueItem)(f: Memory => Try[T]) =
    try ifStore(binary, thenWithBrand)(f) catch { case NonFatal(ex) => Failure(ex) }

    // CatalogueItem.ownerTypeOf(binary) match {
    //   case STORE =>
    //     try f(SecondaryMemory(binary, thenWithBrand.memory)) catch { case NonFatal(ex) => Failure(ex) }
    //   case _ => Failure(new IllegalArgumentException("Owner type is not Store"))
    // }

  /** Description of function
    *
    * @param Parameter1 - blah blah
    * @return Return value - blah blah
    */
  def ifStore[T](binary: Array[Byte], thenWithBrand: CatalogueItem)(f: Memory => T) =
    CatalogueItem.ownerTypeOf(binary) match {
      case STORE => f(SecondaryMemory(binary, thenWithBrand.memory)) // catch { case NonFatal(ex) => Failure(ex) }
      case _ => throw new IllegalArgumentException("Owner type is not Store")
    }

    // tryIfStore(binary, thenWithBrand)(f).get

}