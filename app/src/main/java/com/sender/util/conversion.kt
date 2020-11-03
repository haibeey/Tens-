package com.sender.util

import android.net.Uri
import com.google.gson.*
import java.lang.reflect.Type
import java.nio.ByteBuffer


class conversion {
    companion object{
        fun longToByteArray(long : Long):ByteArray{
            val byteBuffer = ByteBuffer.allocate(Long.SIZE_BYTES)
            byteBuffer.putLong(long)
            return  byteBuffer.array()
        }
        fun intToByteArray(int : Int):ByteArray{
            val byteBuffer = ByteBuffer.allocate(Int.SIZE_BYTES)
            byteBuffer.putInt(int)
            return  byteBuffer.array()
        }
        fun byteArrayToLong(b: ByteArray):Long{
            if (b.size<8) return 0

            return (b[7].toLong() shl 56
                    or (b[6].toLong() and 0xff shl 48
                    ) or (b[5].toLong() and 0xff shl 40
                    ) or (b[4].toLong() and 0xff shl 32
                    ) or (b[3].toLong() and 0xff shl 24
                    ) or (b[2].toLong() and 0xff shl 16
                    ) or (b[1].toLong() and 0xff shl 8
                    ) or (b[0].toLong() and 0xff))
        }

        fun byteArrayToInt(b: ByteArray):Int{
            if (b.size<4) return 0
            return (0xff and b[0].toInt() shl 56
                    or (0xff and b[1].toInt() shl 48)
                    or (0xff and b[ 2].toInt() shl 40)
                    or (0xff and b[3].toInt() shl 32))
        }

        private fun removeDecimal(value : Int):String{
            return value.toString().split(".")[0]
        }
        private fun removeDecimalLong(value : Long):String{
            return value.toString().split(".")[0]
        }
        fun byteToMb(bytes : Int):String{
            return if (bytes>1024*1024*1024){
                "${removeDecimal((bytes/(1024*1024*1024)))}gb"
            }else if (bytes>1024*1024){
                "${removeDecimal((bytes/(1024*1024)))}mb"
            }else if(bytes>1024){
                "${removeDecimal((bytes/(1024)))}kb"
            }else{
                "${bytes}b"
            }
        }

        fun byteToMbLong(bytes : Long):String{
            return if (bytes>1024*1024*1024){
                "${removeDecimalLong((bytes/(1024*1024*1024)))}gb"
            }else if (bytes>1024*1024){
                "${removeDecimalLong((bytes/(1024*1024)))}mb"
            }else if(bytes>1024){
                "${removeDecimalLong((bytes/(1024)))}kb"
            }else{
                "${bytes}b"
            }
        }

        fun getGSONSerializer():Gson{
            return GsonBuilder()
                .registerTypeAdapter(Uri::class.java, UriSerializer())
                .create()
        }

        fun getGSONDeSerializer():Gson{
            return GsonBuilder()
                .registerTypeAdapter(Uri::class.java, UriDeserializer())
                .create()
        }
    }
}

class UriSerializer : JsonSerializer<Uri?> {
    override fun serialize(
        src: Uri?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        return JsonPrimitive(src.toString())
    }
}

class UriDeserializer : JsonDeserializer<Uri?> {
    @Throws(JsonParseException::class)
    override fun deserialize(
        src: JsonElement, srcType: Type?,
        context: JsonDeserializationContext?
    ): Uri {
        return Uri.parse(src.asString)
    }
}
