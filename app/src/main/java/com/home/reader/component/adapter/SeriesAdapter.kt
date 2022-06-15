package com.home.reader.component.adapter

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.LruCache
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.home.reader.R
import com.home.reader.component.activitiy.IssuesActivity
import com.home.reader.component.activitiy.MainActivity
import com.home.reader.persistence.entity.SeriesWithIssues
import com.home.reader.utils.coversPath
import java.io.File
import java.util.concurrent.Executors


class SeriesAdapter(
    private var series: MutableList<SeriesWithIssues>,
    private val parent: Activity,
) : RecyclerView.Adapter<SeriesAdapter.SeriesViewHolder>() {

    private val readIssuesCount = series.associate {
        it.series.id to it.issues.count { issue -> issue.isRead() }
    }.toMutableMap()

    private var basePath: String = "${parent.filesDir}/${parent.packageName}"
    private val imageLoaderExecutor = Executors.newFixedThreadPool(3)
    private val cache: LruCache<Long, Bitmap>

    init {
        val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()
        val cacheSize = maxMemory / 8
        cache = object : LruCache<Long, Bitmap>(cacheSize) {

            override fun sizeOf(key: Long, bitmap: Bitmap): Int {
                return bitmap.byteCount / 1024
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeriesViewHolder {
        val v = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.series_row, parent, false)

        return SeriesViewHolder(v)
    }

    override fun onBindViewHolder(holder: SeriesViewHolder, position: Int) {
        val curSeries = series[position].series
        val issues = series[position].issues

        holder.text.text = curSeries.name
        holder.counter.text = "Read ${readIssuesCount[curSeries.id]}/${issues.size}"
        holder.tableRow.setOnClickListener(onSeriesClick(curSeries.id))

        if (issues.isEmpty()) {
            return
        }

        val firstIssue = issues.minByOrNull { it.issue }!!
        val cachedCover = cache[firstIssue.id]

        if (cachedCover != null) {
            holder.seriesCover.setImageBitmap(cachedCover)
        } else {
            imageLoaderExecutor.submit {
                val cover = getCover(
                    firstIssue.id,
                    holder.seriesCover.layoutParams.width,
                    holder.seriesCover.layoutParams.height
                )

                cache.put(firstIssue.id, cover)
                holder.seriesCover.setImageBitmap(cover)
            }
        }
    }

    fun addItem(item: SeriesWithIssues) {
        val existsSeriesIndex = series.indexOfFirst { it.series.id == item.series.id }
        if(existsSeriesIndex != -1) {
            series[existsSeriesIndex] = item
            notifyItemChanged(existsSeriesIndex)
        } else {
            series.add(item)
            notifyItemInserted(series.size - 1)
        }

        readIssuesCount[item.series.id] = item.issues.count { issue -> issue.isRead() }
    }

    private fun getCover(id: Long?, width: Int, height: Int): Bitmap? {
        val dir = File("$basePath/${id}")
        if (!dir.exists()) {
            return null
        }

        val coverFile = parent.coversPath().resolve("$id.jpg").toFile()

        val options = BitmapFactory.Options()
        options.inPreferredConfig = Bitmap.Config.ARGB_8888

        val bitmap = BitmapFactory.decodeFile(coverFile?.path, options)
        return Bitmap.createScaledBitmap(bitmap, width, height, false)
    }

    override fun getItemCount() = series.size

    private fun onSeriesClick(seriesId: Long?): View.OnClickListener {
        return View.OnClickListener {
            val intent = Intent(it.context, IssuesActivity::class.java).apply {
                putExtra("SERIES_ID", seriesId)
            }

            (parent as MainActivity).openSeries(intent)
        }
    }

    class SeriesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var text: TextView = itemView.findViewById(R.id.seriesName)
        var counter: TextView = itemView.findViewById(R.id.issuesCount)
        var tableRow: ConstraintLayout = itemView.findViewById(R.id.tableRow)
        var seriesCover: ImageView = itemView.findViewById(R.id.seriesCover)
    }

}
