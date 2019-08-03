package feelings.guide.ui

import android.content.Context
import android.database.Cursor
import android.database.DataSetObserver
import android.provider.BaseColumns._ID
import androidx.recyclerview.widget.RecyclerView

abstract class RecyclerViewCursorAdapter<VH : RecyclerView.ViewHolder>
protected constructor(protected val context: Context) :
    RecyclerView.Adapter<VH>() {
    protected var cursor: Cursor? = null
    private var isDataValid: Boolean = false
    private var rowIdColumn: Int = 0


    private val dataSetObserver = object : DataSetObserver() {
        override fun onChanged() {
            isDataValid = true
            notifyDataSetChanged()
        }

        override fun onInvalidated() {
            isDataValid = false
            notifyDataSetChanged()
        }
    }

    private val isEmpty: Boolean
        get() = cursor?.count ?: 0 == 0

    val isNotEmpty: Boolean
        get() = !isEmpty

    protected abstract fun onBindViewHolder(holder: VH, cursor: Cursor)

    override fun onBindViewHolder(holder: VH, position: Int) {
        if (!isDataValid) {
            throw IllegalStateException("this should only be called when the cursor is valid")
        }
        if (!cursor!!.moveToPosition(position)) {
            throw IllegalStateException("couldn't move cursor to position $position")
        }
        onBindViewHolder(holder, cursor!!)
    }

    override fun getItemId(position: Int): Long {
        return if (isDataValid && cursor!!.moveToPosition(position))
            cursor!!.getLong(rowIdColumn)
        else RecyclerView.NO_ID
    }

    override fun getItemCount(): Int {
        return if (isDataValid) cursor?.count ?: 0 else 0
    }

    protected fun swapCursor(newCursor: Cursor): Cursor? {
        if (newCursor === cursor) {
            return null
        }

        val oldCursor = cursor
        oldCursor?.unregisterDataSetObserver(dataSetObserver)

        cursor = newCursor
        newCursor.registerDataSetObserver(dataSetObserver)
        rowIdColumn = newCursor.getColumnIndexOrThrow(_ID)
        isDataValid = true

        notifyDataSetChanged()
        return oldCursor
    }

    fun close() {
        cursor?.unregisterDataSetObserver(dataSetObserver)
        cursor?.close()
        notifyDataSetChanged()
    }
}