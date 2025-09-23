package com.home.reader.ui.common.component

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.platform.Clipboard
import androidx.compose.ui.platform.TextToolbar
import androidx.compose.ui.platform.TextToolbarStatus

class SelectedTextToolbar(private val context: Context, private val clipboard: Clipboard) : TextToolbar {
    private var actionMode: ActionMode? = null

    override fun showMenu(
        rect: Rect,
        onCopyRequested: (() -> Unit)?,
        onPasteRequested: (() -> Unit)?,
        onCutRequested: (() -> Unit)?,
        onSelectAllRequested: (() -> Unit)?
    ) {
        actionMode = (context as? Activity)?.startActionMode(
            object : ActionMode.Callback {
                override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
                    menu?.apply {
                        // Стандартные пункты
                        onCopyRequested?.let {
                            add("Копировать").setOnMenuItemClickListener {
                                onCopyRequested()
                                true
                            }
                        }

                        // Добавляем "Перевести"
                        add("Перевести").setOnMenuItemClickListener {
                            onCopyRequested?.invoke()
                            val text = clipboard.nativeClipboard.primaryClip?.getItemAt(0)?.text
                            if (text != null) {
                                openTranslation(context, text.toString())
                            }
                            true
                        }
                    }
                    return true
                }

                override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?) = false
                override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?) = false
                override fun onDestroyActionMode(mode: ActionMode?) { actionMode = null }
            },
            ActionMode.TYPE_FLOATING
        )
        status = TextToolbarStatus.Shown
    }

    override var status: TextToolbarStatus = TextToolbarStatus.Hidden

    override fun hide() {
        status = TextToolbarStatus.Hidden
        actionMode?.finish()
    }

    private fun openTranslation(context: Context, text: String) {
        Intent(Intent.ACTION_PROCESS_TEXT).apply {
            putExtra(Intent.EXTRA_PROCESS_TEXT, text)
            type = "text/plain"
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }.takeIf { it.resolveActivity(context.packageManager) != null }?.let {
            context.startActivity(it)
        } ?: Toast.makeText(context, "Установите переводчик", Toast.LENGTH_SHORT).show()
    }
}