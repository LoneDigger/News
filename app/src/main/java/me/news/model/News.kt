package me.news.model

import java.io.Serializable
import java.util.*

class News(
    val title: String,
    val description: String,
    val from: String,
    val url: String,
    val imageUrl: String,
    val date: Date
) : Serializable