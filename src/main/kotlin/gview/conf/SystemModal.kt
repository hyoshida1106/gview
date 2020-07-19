package gview.conf

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleObjectProperty
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration

/*
    システムモーダル情報
 */
class SystemModal {

    companion object {
        //保存ファイルパス
        private const val modalInfoFilePath = "./ModalInfo.json"
    }

    //シリアル変換に使用するJSONインスタンス
    private val json = Json(JsonConfiguration.Stable.copy(unquotedPrint = false))

    //最大化フラグ(Boolean)
    val maximumProperty: SimpleBooleanProperty
    val maximum: Boolean get() { return maximumProperty.value }

    //メインウィンドウ高さ
    val mainHeightProperty: SimpleDoubleProperty
    val mainHeight: Double get() { return mainHeightProperty.value }

    //メインウィンドウ幅
    val mainWidthProperty: SimpleDoubleProperty
    val mainWidth: Double get() { return mainWidthProperty.value }

    //メインウィンドウ分割位置
    val mainSplitPosProperty: SimpleObjectProperty<DoubleArray>
    val mainSplitPos: DoubleArray get() { return mainSplitPosProperty.value }

    //保存データ
    private val data: StorageData
    var lastHashCode: Int                   //保存データハッシュコード

    //初期化
    init {
        //ファイルから保存データを取得、または初期値設定
        data = StorageData.deserialize(json)
        lastHashCode = data.hashCode()      //取得データのハッシュコード

        //取得した値で各プロパティを初期化
        maximumProperty = SimpleBooleanProperty(data.maximum)
        mainHeightProperty = SimpleDoubleProperty(data.mainHeight)
        mainWidthProperty = SimpleDoubleProperty(data.mainWidth )
        mainSplitPosProperty = SimpleObjectProperty(data.mainSplitPos)

        //プロパティ更新時に保存データを更新するためのBind
        maximumProperty.addListener { _, _, newValue -> data.maximum = newValue }
        mainHeightProperty.addListener { _, _, newValue -> data.mainHeight = newValue.toDouble() }
        mainWidthProperty.addListener { _, _, newValue -> data.mainWidth = newValue.toDouble() }
    }

    //ファイルへ保存
    fun serialize( ) {
        //ハッシュコードが変化している場合のみ更新する
        if(lastHashCode != data.hashCode()) {
            data.serialize(json)
            lastHashCode = data.hashCode()
        }
    }

    /*
        実際にファイルI/Oを行うプロパティを持ったデータクラス
     */
    @Serializable
    data class StorageData(var maximum: Boolean,
                           var mainHeight: Double,
                           var mainWidth: Double,
                           var mainSplitPos: DoubleArray){

        //ハッシュ値の算出
        override fun hashCode(): Int {
            var result = maximum.hashCode()
            result = 31 * result + mainHeight.hashCode()
            result = 31 * result + mainWidth.hashCode()
            result = 31 * result + mainSplitPos.contentHashCode()
            return result
        }

        //等価比較
        override fun equals(other: Any?): Boolean {
            //使用していないので暫定実装
            return super.equals(other)
        }

        //シリアライズ
        fun serialize(json: Json) {
            //JSON変換してファイル書き込み
            val serialData = json.stringify(StorageData.serializer(), this)
            writeToFile(modalInfoFilePath, serialData)
        }

        //デシリアライズするクラスメソッド
        companion object Factory {
            fun deserialize(json: Json): StorageData {
                //ファイルから読み取ったデータをJSONとして解析
                val str = readFromFile(modalInfoFilePath)
                return if(str != null) {
                    json.parse(StorageData.serializer(), str).copy()
                } else {
                    StorageData(false, 700.0, 1300.0, doubleArrayOf(0.2,0.5))
                }
            }
        }
    }
}