package commons.core.util

object FastStringUtils {

  import UnsafeUtil._

  val FAST_STRING_IMPLEMENTATION = getApplicableStringImplementation()

  private val ENABLED = UNSAFE != null
  private var WRITE_TO_FINAL_FIELDS = true

  def getFieldOffset(fieldName: String): Long = {
    if(ENABLED) {
      try {
        return UNSAFE.objectFieldOffset(classOf[String].getDeclaredField(fieldName))
      } catch {
        case ex: NoSuchFieldException =>
      }
    }
    return -1L
  }

  private val STRING_VALUE_FIELD_OFFSET = getFieldOffset("value")
  private val STRING_OFFSET_FIELD_OFFSET = getFieldOffset("offset")
  private val STRING_COUNT_FIELD_OFFSET = getFieldOffset("count")

  trait StringImplementation {

    def toCharArray(string: String): Array[Char]

    def noCopyStringFromChars(chars: Array[Char]): String

    def numBytesRequiredToEncode(string: String) =
      string.length * CHAR_SIZE_BYTES + INT_SIZE_BYTES

    def putString(string: String, into: Array[Byte], from: Int): Int = {
      val chars = toCharArray(string)
      val strLen = string.length
      val totalBytes = strLen * CHAR_SIZE_BYTES + INT_SIZE_BYTES
      if(totalBytes > into.size - from) return -1

      var pos = from + BYTE_ARRAY_BASE_OFFSET

      // write length of string
      UNSAFE.putInt(into, pos, strLen)
      pos += INT_SIZE_BYTES
      // write all chars
      for(char <- chars) {
        UNSAFE.putChar(into, pos, char)
        pos += CHAR_SIZE_BYTES
      }

      totalBytes
    }

    def getString(from: Array[Byte], offset: Int): String = {
      var pos = BYTE_ARRAY_BASE_OFFSET + offset
      val strLen = UNSAFE.getInt(from, pos)
      pos += INT_SIZE_BYTES
      val chars = Array.ofDim[Char](strLen)
      for(i <- 0 until strLen) {
        chars(i) = UNSAFE.getChar(from, pos)
        pos += CHAR_SIZE_BYTES
      }
      new String(chars)
    }

  }

  /**
   * JDK 7 drops offset and count so there is special handling for later version of JDK 7.
   */
  object DIRECT_CHARS extends StringImplementation {

    def toCharArray(string: String) =
      UNSAFE.getObject(string, STRING_VALUE_FIELD_OFFSET).asInstanceOf[Array[Char]]

    def noCopyStringFromChars(chars: Array[Char]) = {
      val string = new String
      UNSAFE.putObject(string, STRING_VALUE_FIELD_OFFSET, chars)
      string
    }

  }

  object OFFSET extends StringImplementation {

    def toCharArray(string: String) = {
      val value = UNSAFE.getObject(string, STRING_VALUE_FIELD_OFFSET).asInstanceOf[Array[Char]]
      val offset = UNSAFE.getInt(string, STRING_OFFSET_FIELD_OFFSET)
      val count = UNSAFE.getInt(string, STRING_COUNT_FIELD_OFFSET)
      if(offset == 0 && count == value.length) value
      else string.toCharArray
    }

    def noCopyStringFromChars(chars: Array[Char]) = {
      val string = new String()
      UNSAFE.putObject(string, STRING_VALUE_FIELD_OFFSET, chars)
      UNSAFE.putInt(string, STRING_COUNT_FIELD_OFFSET, chars.length)
      string
    }

  }

  /**
   * Implement for other JDKs
   */

  object UNKNOWN extends StringImplementation {
    def toCharArray(string: String) = string.toCharArray
    def noCopyStringFromChars(chars: Array[Char]) = new String(chars)
  }

  private def getApplicableStringImplementation() =
    if(STRING_VALUE_FIELD_OFFSET != -1L) {
      if(STRING_OFFSET_FIELD_OFFSET != -1L && STRING_COUNT_FIELD_OFFSET != -1L) OFFSET
      else if(STRING_OFFSET_FIELD_OFFSET == -1L && STRING_COUNT_FIELD_OFFSET == -1L) DIRECT_CHARS
      else UNKNOWN
    } else UNKNOWN

}