package com.home.reader.component.adapter

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.home.reader.R
import com.home.reader.api.dto.Series
import com.home.reader.component.ImageCache
import java.net.URL

abstract class AbstractSeriesAdapter(
    protected val series: MutableList<Series>,
    protected val parent: Activity,
) : RecyclerView.Adapter<AbstractSeriesAdapter.SeriesViewHolder>() {

    abstract fun onSeriesClick(seriesId: Long?): View.OnClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeriesViewHolder {
        val v = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.series_row, parent, false)

        return SeriesViewHolder(v)
    }

    override fun onBindViewHolder(holder: SeriesViewHolder, position: Int) {
        val curSeries = series[position]
//        val issues = series[position].issues

        holder.text.text = curSeries.name
        holder.counter.text = "Read ${curSeries.completedIssues}/${curSeries.issuesCount}"
        holder.tableRow.setOnClickListener(onSeriesClick(curSeries.id))

//        if (issues.isEmpty()) {
//            return
//        }

//        val firstIssue = issues.minByOrNull { it.issue }!!
        val cachedCover = ImageCache.cache[curSeries.cover]

        if (cachedCover != null) {
            holder.seriesCover.setImageBitmap(cachedCover)
        } else {
            ImageCache.imageLoaderExecutor.submit {
                val cover = getCover(
                    curSeries.cover,
                    holder.seriesCover.layoutParams.width,
                    holder.seriesCover.layoutParams.height
                )

                ImageCache.cache.put(curSeries.cover, cover)
                holder.seriesCover.setImageBitmap(cover)
            }
        }
    }

//    fun addItem(item: SeriesWithIssues) {
//        val existsSeriesIndex = series.indexOfFirst { it.series.id == item.series.id }
//        readIssuesCount[item.series.id] = item.issues.count { issue -> issue.isRead() }
//        if (existsSeriesIndex != -1) {
//            series[existsSeriesIndex] = item
//            notifyItemChanged(existsSeriesIndex)
//        } else {
//            series.add(item)
//            notifyItemInserted(series.size - 1)
//        }
//    }

    private fun getCover(id: Long?, width: Int, height: Int): Bitmap? {
        val url = URL("http://192.168.0.103:8080/file/$id/0?size=medium")

        val options = BitmapFactory.Options()
        options.inPreferredConfig = Bitmap.Config.ARGB_8888

        val bitmap = BitmapFactory.decodeStream(url.openStream())
        return Bitmap.createScaledBitmap(bitmap, width, height, false)
    }

    override fun getItemCount() = series.size

    class SeriesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var text: TextView = itemView.findViewById(R.id.seriesName)
        var counter: TextView = itemView.findViewById(R.id.issuesCount)
        var tableRow: ConstraintLayout = itemView.findViewById(R.id.tableRow)
        var seriesCover: ImageView = itemView.findViewById(R.id.seriesCover)
    }
}