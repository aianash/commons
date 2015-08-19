package commons.core.util

object UnsafeUtil {

  val UNSAFE: sun.misc.Unsafe = {
    var tmpUnsafe: sun.misc.Unsafe = null

    try {
      val field: java.lang.reflect.Field = classOf[sun.misc.Unsafe].getDeclaredField("theUnsafe")
      field.setAccessible(true)
      tmpUnsafe = field.get(null).asInstanceOf[sun.misc.Unsafe]
    } catch {
      case ex: Exception =>
        throw new RuntimeException("unable to get UNSAFE instance", ex)
    }

    tmpUnsafe
  }

  val BYTE_ARRAY_BASE_OFFSET: Long   = UNSAFE.arrayBaseOffset(classOf[Array[Byte]])
  val FLOAT_ARRAY_BASE_OFFSET: Long  = UNSAFE.arrayBaseOffset(classOf[Array[Float]])
  val DOUBLE_ARRAY_BASE_OFFSET: Long = UNSAFE.arrayBaseOffset(classOf[Array[Double]])
  val INT_ARRAY_BASE_OFFSET: Long    = UNSAFE.arrayBaseOffset(classOf[Array[Int]])
  val LONG_ARRAY_BASE_OFFSET: Long   = UNSAFE.arrayBaseOffset(classOf[Array[Long]])
  val SHORT_ARRAY_BASE_OFFSET: Long  = UNSAFE.arrayBaseOffset(classOf[Array[Short]])
  val CHAR_ARRAY_BASE_OFFSET: Long   = UNSAFE.arrayBaseOffset(classOf[Array[Char]])

  val REFERENCE_SIZE_BYTES = UNSAFE.arrayIndexScale(classOf[Array[Object]])
  val BOOLEAN_SIZE_BYTES   = UNSAFE.arrayIndexScale(classOf[Array[Boolean]])
  val BYTE_SIZE_BYTES      = UNSAFE.arrayIndexScale(classOf[Array[Byte]])
  val SHORT_SIZE_BYTES     = UNSAFE.arrayIndexScale(classOf[Array[Short]])
  val CHAR_SIZE_BYTES      = UNSAFE.arrayIndexScale(classOf[Array[Char]])
  val INT_SIZE_BYTES       = UNSAFE.arrayIndexScale(classOf[Array[Int]])
  val LONG_SIZE_BYTES      = UNSAFE.arrayIndexScale(classOf[Array[Long]])
  val FLOAT_SIZE_BYTES     = UNSAFE.arrayIndexScale(classOf[Array[Float]])
  val DOUBLE_SIZE_BYTES    = UNSAFE.arrayIndexScale(classOf[Array[Double]])

  val REFERENCE_SIZE_EXP = Integer.numberOfTrailingZeros(REFERENCE_SIZE_BYTES)
  val BOOLEAN_SIZE_EXP   = Integer.numberOfTrailingZeros(BOOLEAN_SIZE_BYTES)
  val BYTE_SIZE_EXP      = Integer.numberOfTrailingZeros(BYTE_SIZE_BYTES)
  val SHORT_SIZE_EXP     = Integer.numberOfTrailingZeros(SHORT_SIZE_BYTES)
  val CHAR_SIZE_EXP      = Integer.numberOfTrailingZeros(CHAR_SIZE_BYTES)
  val INT_SIZE_EXP       = Integer.numberOfTrailingZeros(INT_SIZE_BYTES)
  val LONG_SIZE_EXP      = Integer.numberOfTrailingZeros(LONG_SIZE_BYTES)
  val FLOAT_SIZE_EXP     = Integer.numberOfTrailingZeros(FLOAT_SIZE_BYTES)
  val DOUBLE_SIZE_EXP    = Integer.numberOfTrailingZeros(DOUBLE_SIZE_BYTES)

}