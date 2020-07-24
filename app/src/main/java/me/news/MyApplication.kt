package me.news

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.preference.PreferenceManager
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration
import java.util.*


class MyApplication : Application() {

    companion object {
        val pages = arrayOf("20", "30", "50", "60", "80", "100")
        var pageSize = pages[0]

        val PAGE_SIZE = "page_size"
        val SHOW = "show"

        lateinit var countryCode: String
        var showImage = true
        //lateinit var language: String

        fun getLocalizedResources(context: Context, desiredLocale: Locale): Resources {
            var conf = context.resources.configuration
            conf = Configuration(conf)
            conf.setLocale(desiredLocale)
            val localizedContext = context.createConfigurationContext(conf)
            return localizedContext.resources
        }
    }

    override fun onCreate() {
        super.onCreate()

        val locale = Locale.getDefault()

        countryCode = locale.country

        val sp = PreferenceManager.getDefaultSharedPreferences(this)
        pageSize = sp.getString(PAGE_SIZE, pages[0])
        showImage = sp.getBoolean(SHOW, true)

        val config = ImageLoaderConfiguration.Builder(applicationContext)
        config.threadPriority(Thread.NORM_PRIORITY)
        config.threadPoolSize(3)
        config.denyCacheImageMultipleSizesInMemory()

        config.diskCacheFileCount(64)
        config.diskCacheFileNameGenerator(Md5FileNameGenerator())
        config.diskCacheSize(50 * 1024 * 1024) // 50 MiB

        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config.build())
    }
}