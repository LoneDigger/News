package me.news

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.assist.FailReason
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener
import me.news.model.News
import java.text.DateFormat

class ContextActivity : AppCompatActivity() {
    private lateinit var news: News

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_context)

        val bundle = intent.getBundleExtra("obj")
        news = bundle.getSerializable("news") as News

        //標題
        val titleTextView: TextView = findViewById(R.id.titleTextView)
        titleTextView.text = news.title

        //內容
        val textView: TextView = findViewById(R.id.textView)
        textView.text = news.description

        val dateTextView: TextView = findViewById(R.id.dateTextView)
        dateTextView.text = DateFormat.getDateInstance(DateFormat.DEFAULT).format(news.date)

        //來自
        val fromTextView: TextView = findViewById(R.id.fromTextView)
        fromTextView.text = news.from

        //Web
        val floatingButton: FloatingActionButton = findViewById(R.id.floatingButton)
        floatingButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(news.url))
                startActivity(intent)
            }
        })

        val url = if (news.imageUrl.startsWith("http"))
            news.imageUrl
        else
            "http:" + news.imageUrl

        ImageLoader.getInstance().loadImage(url, object : ImageLoadingListener {
            override fun onLoadingStarted(imageUri: String?, view: View?) {
            }

            override fun onLoadingCancelled(imageUri: String?, view: View?) {
            }

            override fun onLoadingFailed(imageUri: String?, view: View?, failReason: FailReason?) {
            }

            override fun onLoadingComplete(imageUri: String?, view: View?, loadedImage: Bitmap?) {
                val imageView: ImageView = findViewById(R.id.imageView)
                imageView.setImageBitmap(loadedImage)
            }
        })
    }

    fun back(view: View) {
        finish()
    }
}
