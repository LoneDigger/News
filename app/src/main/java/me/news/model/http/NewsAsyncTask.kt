package me.news.model.http

import android.annotation.SuppressLint
import android.os.AsyncTask
import me.news.model.News
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class NewsAsyncTask(private val callback: OnFinishedCallback, private val arrayList: ArrayList<News>) : AsyncTask<String, Void, Boolean>() {

    companion object {
        @SuppressLint("SimpleDateFormat")
        private val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
    }

    override fun doInBackground(vararg params: String?): Boolean {
        val client = OkHttpClient()
        val request: Request = Request.Builder().url(params[0]!!).method("GET", null).build()

        val response: Response = client.newCall(request).execute()

        if (response.isSuccessful) {
            val jsonObject = JSONObject(response.body!!.string())
            val jsonArray = jsonObject.getJSONArray("articles")

            for (i in 0 until jsonArray.length()) {
                val obj: JSONObject = jsonArray[i] as JSONObject

                val title: String = obj.getString("title")
                val description: String = obj.getString("description")
                val from: String = if (obj.isNull("author")) "" else obj.getString("author")
                val url: String = obj.getString("url")
                val imageUrl: String = obj.getString("urlToImage")
                val date = format.parse(obj.getString("publishedAt"))
                val news = News(title, description, from, url, imageUrl, date)

                arrayList.add(news)
            }
        }
        return response.isSuccessful
    }

    override fun onPostExecute(result: Boolean) {
        super.onPostExecute(result)
        callback.onResult(result)
    }
}
