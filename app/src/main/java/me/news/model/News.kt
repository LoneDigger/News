package me.news.model

import java.io.Serializable
import java.util.*

/**
 * 序列化
 * 用在傳給下個畫面用
 * */
class News(
    //標題
    val title: String,

    //內文
    val description: String,

    //來源
    val from: String,

    //來源網址
    val url: String,

    //圖片
    val imageUrl: String,

    //日期
    val date: Date

) : Serializable