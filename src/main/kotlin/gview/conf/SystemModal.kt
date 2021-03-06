package gview.conf

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleObjectProperty
import kotlinx.serialization.Serializable

/*
    システムモーダル情報
 */
object SystemModal: SerializableData("ModalInfo.json") {

    //最大化フラグ(Boolean)
    val maximumProperty: SimpleBooleanProperty

    //メインウィンドウ高さ
    val mainHeightProperty: SimpleDoubleProperty

    //メインウィンドウ幅
    val mainWidthProperty: SimpleDoubleProperty

    //メインウィンドウ分割位置
    val mainSplitPosProperty: SimpleObjectProperty<DoubleArray>

    //保存データ
    private val data: StorageData                   //保存データインスタンス
    private var lastHashCode: Int                   //保存データハッシュコード

    //初期化
    init {
        //ファイルから読み取ったデータをJSONとして解析
        data = readFromFile()
        lastHashCode = data.hashCode()

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

    //ファイルから取得
    private fun readFromFile(): StorageData {
        //ファイルから取得できなければ既定値を設定
        return deserialize(StorageData.serializer()) ?: StorageData()
    }

    //ファイルへ保存
    fun saveToFile() {
        //ハッシュコードが変化している場合のみ更新する
        if(lastHashCode != data.hashCode()) {
            lastHashCode = data.hashCode()
            serialize(StorageData.serializer(), data)
        }
    }

    /*
        実際にファイルI/Oを行うプロパティを持ったデータクラス
     */
    @Serializable
    data class StorageData(
            var maximum: Boolean,
            var mainHeight: Double,
            var mainWidth: Double,
            var mainSplitPos: DoubleArray){

        //デフォルト値
        constructor( ): this(false, 800.0, 1200.0, doubleArrayOf(0.15, 0.4))

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
    }
}