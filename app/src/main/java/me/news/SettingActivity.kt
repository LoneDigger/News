package me.news

import android.content.DialogInterface
import android.content.res.Resources
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.TextView
import com.nostra13.universalimageloader.core.ImageLoader
import java.util.*

class SettingActivity : AppCompatActivity() {

    private lateinit var localResources: Resources
    private lateinit var alertDialog: AlertDialog.Builder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        localResources = MyApplication.getLocalizedResources(this, Locale.getDefault())

        val sizeTextView: TextView = findViewById(R.id.sizeTextView)
        sizeTextView.text = localResources.getString(R.string.page_size) + "  " + MyApplication.pageSize

        sizeTextView.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                alertDialog.show()
            }
        })

        val checkBox: CheckBox = findViewById(R.id.checkBox)
        checkBox.isChecked = MyApplication.showImage
        checkBox.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener {
            override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                MyApplication.showImage = isChecked

                val edit = PreferenceManager.getDefaultSharedPreferences(this@SettingActivity).edit()
                edit.putBoolean(MyApplication.SHOW, MyApplication.showImage)
                edit.apply()
            }
        })

        alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle(localResources.getString(R.string.page_size))
        alertDialog.setItems(MyApplication.pages, object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface?, which: Int) {
                MyApplication.pageSize = MyApplication.pages[which]
                sizeTextView.text = localResources.getString(R.string.page_size) + "  " + MyApplication.pageSize

                val edit = PreferenceManager.getDefaultSharedPreferences(this@SettingActivity).edit()
                edit.putString(MyApplication.PAGE_SIZE, MyApplication.pageSize)
                edit.apply()
            }
        })
        alertDialog.setNegativeButton(localResources.getString(R.string.cancel), null)
    }

    fun back(view: View) {
        finish()
    }

    /**
     * 清除快取
     * */
    fun cleanCache(view: View) {
        ImageLoader.getInstance().clearDiskCache()
        ImageLoader.getInstance().clearMemoryCache()
        view.visibility = View.INVISIBLE
    }
}
