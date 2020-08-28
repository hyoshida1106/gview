package gview.conf

import kotlinx.serialization.Serializable

object ConfigUserInfo: SerializableData("UserInfo.json") {

    var userName: String = ""
    var mailAddr: String = ""

    //初期化
    init {
        val data = deserialize(StorageData.serializer()) ?: StorageData()
        userName = data.userName
        mailAddr = data.mailAddress
    }

    //保存
    fun saveToFile() {
        serialize(StorageData.serializer(), StorageData(userName, mailAddr))
    }

    /*
        実際にファイルI/Oを行うプロパティを持ったデータクラス
    */
    @Serializable
    data class StorageData(var userName: String, var mailAddress: String) {
        constructor( ): this("", "")
    }
}