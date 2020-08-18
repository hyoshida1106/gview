package gview.conf

import java.io.ByteArrayOutputStream
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileWriter
import java.io.StringReader

/*
    ファイルから文字列情報を取得する
 */
fun readFromFile(file: String): String? {
    return try {
        val out = ByteArrayOutputStream()
        FileInputStream(file).use { stream -> stream.copyTo(out) }
        out.toString("utf-8")
    } catch(e: FileNotFoundException) {
        null
    }
}

/*
    ファイルへ文字列情報を記入する
 */
fun writeToFile(file: String, data: String) {
    val reader = StringReader(data)
    val writer = FileWriter(file)
    reader.copyTo(writer)
    reader.close()
    writer.close()
}

/*
    コンフィギュレーション情報クラス
 */
object Configuration {

    //モーダル情報(画面サイズなど)
    val systemModal = SystemModal()

    //情報をファイルに保存する
    fun saveToFile() {
        systemModal.serialize()           //モーダル情報
    }
}