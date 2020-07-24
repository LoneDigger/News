package me.news.model.http

import android.os.AsyncTask
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject


class IPAsyncTask : AsyncTask<Void, Void, String>() {

    companion object {
        private const val URL: String = "http://api.ipstack.com/check?access_key=314e2d996277b587bad3bc08602bd6d1"
        private const val CODE: String = "country_code"
        const val DEFAULT = "us"
    }

    override fun doInBackground(vararg params: Void?): String {
        val client = OkHttpClient()
        val request: Request = Request.Builder().url(URL).method("GET", null).build()
        val response: Response = client.newCall(request).execute()

        if (response.isSuccessful) {
            val jsonObject = JSONObject(response.body!!.string())

            return if (jsonObject.isNull(CODE))
                DEFAULT
            else
                jsonObject.getString(CODE)
        }

        return DEFAULT
    }
}