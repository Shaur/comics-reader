package com.home.reader.component.adapter

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.recyclerview.widget.RecyclerView
import com.home.reader.R
import com.home.reader.component.ImageCache.cache
import com.home.reader.component.ImageCache.imageLoaderExecutor
import com.home.reader.component.activitiy.ReaderActivity
import com.home.reader.persistence.AppDatabase
import com.home.reader.persistence.entity.Issue
import com.home.reader.utils.Constants.SeriesExtra.ISSUE_DIR
import com.home.reader.utils.coversPath
import kotlinx.coroutines.launch
import java.io.File


class IssuesAdapter(
    private var name: String?,
    private var issues: MutableList<Issue>,
    private var lifecycleScope: LifecycleCoroutineScope,
    private val parent: Activity
) : RecyclerView.Adapter<IssuesAdapter.IssueViewHolder>() {

    private var basePath: String = "${parent.filesDir}/${parent.packageName}"
    private var db = AppDatabase.invoke(parent)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IssueViewHolder {
        val v = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.issue_card, parent, false)

        return IssueViewHolder(v)
    }

    override fun onBindViewHolder(holder: IssueViewHolder, position: Int) {
        val issue = issues[position]

        val issueNumber = issue.issue
        holder.progress.max = issue.pagesCount - 1
        holder.progress.progress = issue.currentPage
        holder.name.text = if (issueNumber.isEmpty()) {
            name
        } else {
            "$name #$issueNumber"
        }

        val cachedCover = cache[issue.id]
        if(cachedCover != null) {
            holder.preview.setImageBitmap(cachedCover)
        } else {
            imageLoaderExecutor.submit {
                val cover = getCover(
                    issue.id,
                    holder.preview.layoutParams.width,
                    holder.preview.layoutParams.height
                )

                cache.put(issue.id, cover)
                holder.preview.setImageBitmap(cover)
            }
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(it.context, ReaderActivity::class.java).apply {
                putExtra(ISSUE_DIR, issue.id)
            }

            parent.startActivityForResult(intent, 50)
        }

        holder.itemView.setOnLongClickListener {
            showMenu(it, issue)
            true
        }
    }

    private fun getCover(id: Long?, width: Int, height: Int): Bitmap {
        val cover = parent.coversPath().resolve("$id.jpg").toFile()

        val options = BitmapFactory.Options()
        options.inPreferredConfig = Bitmap.Config.ARGB_8888

        val bitmap = BitmapFactory.decodeFile(cover?.path, options)

        return Bitmap.createScaledBitmap(bitmap, width, height, false)
    }

    override fun getItemCount() = issues.size


    class IssueViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var name: TextView = itemView.findViewById(R.id.issueName)
        var preview: ImageView = itemView.findViewById(R.id.issuePreview)
        var progress: ProgressBar = itemView.findViewById(R.id.readProgress)
    }

    private fun showMenu(anchor: View, issue: Issue) {
        val popup = PopupMenu(anchor.context, anchor)
        popup.menuInflater.inflate(R.menu.issue_menu, popup.menu)

        popup.setOnMenuItemClickListener {
            lifecycleScope.launch {
                db.issueDao().delete(issue)

                val index = issues.indexOf(issue)
                issues.removeAt(index)
                notifyItemChanged(index)
            }
            File("$basePath/${issue.id}").deleteRecursively()
        }

        popup.show()
    }
}