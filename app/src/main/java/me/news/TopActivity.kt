package me.news

import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.assist.FailReason
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener
import kotlinx.android.synthetic.main.layout_recyclerview.*
import me.news.model.News
import me.news.model.http.NewsAsyncTask
import me.news.model.http.OnFinishedCallback
import me.news.widget.Holder
import net.simonvt.menudrawer.MenuDrawer
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import java.util.*

class TopActivity : AppCompatActivity(), OnFinishedCallback {

    private lateinit var dialog: AlertDialog

    private lateinit var menuDrawer: MenuDrawer
    private lateinit var titleTextView: TextView

    private lateinit var floatingButton: FloatingActionButton

    private lateinit var localResources: Resources

    //代號
    private lateinit var menu: Array<String>
    //語系
    private lateinit var nameMenu: Array<String>

    private val arrayList: ArrayList<News> = ArrayList()

    //閘道
    private var flag = false
    //頁
    private var page = 0
    //項目
    private lateinit var category: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        localResources = MyApplication.getLocalizedResources(this, Locale.getDefault())

        nameMenu = localResources.getStringArray(R.array.cate)
        menu = localResources.getStringArray(R.array.arrays)

        menuDrawer = MenuDrawer.attach(this)
        menuDrawer.touchMode = MenuDrawer.TOUCH_MODE_BEZEL
        menuDrawer.setContentView(R.layout.activity_main)
        menuDrawer.setMenuView(R.layout.layout_listview)

        titleTextView = findViewById(R.id.titleTextView)
        titleTextView.text = nameMenu[0]
        category = menu[0]

        dialog = AlertDialog.Builder(this).setView(R.layout.dialog_process).create()
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        dialog.window.setBackgroundDrawableResource(android.R.color.transparent)

        val listView: ListView = menuDrawer.menuView.findViewById(R.id.listView)
        setMenu(listView)

        recyclerView!!.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = object : RecyclerView.Adapter<Holder>() {
            override fun onCreateViewHolder(p0: ViewGroup, p1: Int): Holder {
                val view: View = layoutInflater.inflate(R.layout.item_news, null)
                view.layoutParams = RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                return Holder(view)
            }

            override fun getItemCount(): Int {
                return arrayList.count()
            }

            override fun onBindViewHolder(p0: Holder, p1: Int) {
                val n = arrayList[p1]

                val titleTextView: TextView = p0.itemView.findViewById(R.id.titleTextView)
                titleTextView.text = n.title

                val descTextView: TextView = p0.itemView.findViewById(R.id.descTextView)
                descTextView.text = n.description

                val imageView: ImageView = p0.itemView.findViewById(R.id.imageView)
                if (MyApplication.showImage) {
                    imageView.visibility = View.VISIBLE
                    ImageLoader.getInstance().loadImage(n.imageUrl, object : ImageLoadingListener {
                        override fun onLoadingStarted(imageUri: String?, view: View?) {
                            imageView.setImageResource(R.drawable.load)
                        }

                        override fun onLoadingCancelled(imageUri: String?, view: View?) {
                        }

                        override fun onLoadingFailed(imageUri: String?, view: View?, failReason: FailReason?) {
                        }

                        override fun onLoadingComplete(imageUri: String?, view: View?, loadedImage: Bitmap?) {
                            imageView.setImageBitmap(loadedImage)
                        }
                    })
                } else {
                    imageView.visibility = View.GONE
                }

                p0.itemView.setOnClickListener {
                    val bundle = Bundle()
                    bundle.putSerializable("news", arrayList[p1])

                    val intent = Intent(this@TopActivity, ContextActivity::class.java)
                    intent.putExtra("obj", bundle)

                    startActivity(intent)
                }
            }
        }

        //Refresh Scroll to bottom
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager
                if (linearLayoutManager.findLastCompletelyVisibleItemPosition() == arrayList.size - 1)
                    load()
            }
        })

        //下拉式更新
        val swipeRefreshLayout: SwipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)
        swipeRefreshLayout.setOnRefreshListener {
            page = 0
            flag = false
            arrayList.clear()
            load()
        }

        floatingButton = findViewById(R.id.floatingButton)
        floatingButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                recyclerView.smoothScrollToPosition(0)
            }
        })

        load()
    }

    fun exit(view: View) {
        finish()
        System.exit(0)
    }

    private fun load() {
        if (flag)
            return
        else
            flag = true

        dialog.show()
        page++

        val builder = "https://newsapi.org/v2/top-headlines?apiKey=95730f81b3044364b0bd17986bbff23d".toHttpUrlOrNull()!!.newBuilder()
        builder.addQueryParameter("category", category)
        builder.addQueryParameter("page", page.toString())
        builder.addQueryParameter("country", MyApplication.countryCode)
        builder.addQueryParameter("pageSize", MyApplication.pageSize)

        val url = builder.build().toString()
        NewsAsyncTask(this, arrayList).execute(url)
    }

    /**
     * 網路結果
     * */
    override fun onResult(boolean: Boolean) {
        dialog.hide()

        if (!boolean) {
            flag = true
            return
        }
        flag = false


        if (page == 1)
            recyclerView.smoothScrollToPosition(0)

        recyclerView.adapter!!.notifyDataSetChanged()
        swipeRefreshLayout.isRefreshing = false
    }

    fun setting(view: View) {
        val intent = Intent(this, SettingActivity::class.java)
        startActivity(intent)
    }

    private fun setMenu(listView: ListView) {
        listView.adapter = object : BaseAdapter() {
            override fun getItem(position: Int): Any {
                return nameMenu[position]
            }

            override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
                val view = convertView ?: layoutInflater.inflate(R.layout.item_menu, parent, false)
                val textView: TextView = view.findViewById(R.id.textView)
                textView.text = nameMenu[position]
                return view
            }

            override fun getItemId(position: Int): Long {
                return 0
            }

            override fun getCount(): Int {
                return nameMenu.size
            }
        }

        listView.onItemClickListener = object : AdapterView.OnItemClickListener {
            override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (category != menu[position]) {
                    titleTextView.text = nameMenu[position]
                    category = menu[position]
                    page = 0
                    arrayList.clear()
                    flag = false
                    load()
                }
                menuDrawer.closeMenu()
            }
        }
    }

    fun show(view: View) {
        menuDrawer.openMenu()
    }
}
