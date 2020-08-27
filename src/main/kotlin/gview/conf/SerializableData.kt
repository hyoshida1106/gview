package gview.conf

import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import java.io.ByteArrayOutputStream
import java.io.FileInputStream
import java.io.FileWriter
import java.io.StringReader

open class SerializableData(private val filePath: String) {

    //シリアル変換に使用するJSONインスタンス
    private val json = Json { }

    //ファイルから取得
    protected fun <T> baseDeserialize(serializer: KSerializer<T>): T? {
        val out = ByteArrayOutputStream()
        FileInputStream(filePath).use { stream -> stream.copyTo(out) }
        val str = out.toString("utf-8")
        return if(str != null) { json.decodeFromString<T>(serializer, str) } else { null }
    }

    //ファイルへ保存
    protected fun <T> baseSerialize(serializer: KSerializer<T>, data:T) {
        val serialData = json.encodeToJsonElement(serializer, data)
        val reader = StringReader(serialData.toString())
        val writer = FileWriter(filePath)
        reader.copyTo(writer)
        reader.close()
        writer.close()
    }

}