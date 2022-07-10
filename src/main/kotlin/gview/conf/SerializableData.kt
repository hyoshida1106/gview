package gview.conf

import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import java.io.*

/**
 * シリアライゼーションを行う基本クラス
 *
 * @constructor         プライマリコンストラクタ
 * @param[fileName]     シリアライズ対象ファイル名
 */
open class SerializableData(fileName: String) {

    /**
     * データファイルパス名
     */
    private val filePath = "./$fileName"

    /**
     * シリアル変換に使用するJSONインスタンス
     */
    private val json = Json

    //ファイルから取得
    /**
     * ファイルから取得(デシリアライズ)
     *
     * @param[serializer]       シリアライザインスタンス
     * @return                  ファイルから取得したインスタンス
     */
    protected fun <T> deserialize(serializer: KSerializer<T>): T? {
        return try {
            val out = ByteArrayOutputStream()
            FileInputStream(filePath).use { stream -> stream.copyTo(out) }
            val str = out.toString("utf-8")
            if (str != null) { json.decodeFromString(serializer, str) } else { null }
        } catch(e: FileNotFoundException) {
            null
        }
     }

    /**
     * ファイルへ保存(シリアライズ)
     *
     * @param[serializer]       シリアライザインスタンス
     * @param[data]             ファイルに保存するインスタンス
     */
    protected fun <T> serialize(serializer: KSerializer<T>, data:T) {
        val serialData = json.encodeToJsonElement(serializer, data)
        val reader = StringReader(serialData.toString())
        FileWriter(filePath).use { stream -> reader.copyTo(stream) }
        reader.close()
    }

}