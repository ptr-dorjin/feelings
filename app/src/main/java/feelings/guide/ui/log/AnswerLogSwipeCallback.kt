package feelings.guide.ui.log

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.ColorDrawable
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import feelings.guide.R

private const val ICON_MARGIN = 64

internal class AnswerLogSwipeCallback(context: Context, private val swipeListener: AnswerLogSwipeListener) :
    ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
    private val iconDelete: VectorDrawableCompat =
        VectorDrawableCompat.create(context.resources, R.drawable.ic_delete_sweep_white_24dp, null)!!
    private val iconEdit: VectorDrawableCompat =
        VectorDrawableCompat.create(context.resources, R.drawable.ic_edit_white_24dp, null)!!
    private val backgroundDelete: ColorDrawable =
        ColorDrawable(ResourcesCompat.getColor(context.resources, R.color.lightRed, null))
    private val backgroundEdit: ColorDrawable =
        ColorDrawable(ResourcesCompat.getColor(context.resources, R.color.lightGreen, null))

    internal interface AnswerLogSwipeListener {
        fun onDeleteAnswer(position: Int)
        fun onEditAnswer(position: Int)
    }

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        // used for up and down movements
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.adapterPosition
        if (direction == ItemTouchHelper.RIGHT) {
            swipeListener.onDeleteAnswer(position)
        } else if (direction == ItemTouchHelper.LEFT) {
            swipeListener.onEditAnswer(position)
        }
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean
    ) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        val itemView = viewHolder.itemView

        var iconHeight = 0
        if (dX > 0) {
            iconHeight = iconDelete.intrinsicHeight
        } else if (dX < 0) {
            iconHeight = iconEdit.intrinsicHeight
        }
        val iconTop = itemView.top + (itemView.height - iconHeight) / 2
        val iconBottom = iconTop + iconHeight

        when {
            dX > 0 -> { // Swiping to the right
                backgroundDelete.setBounds(
                    itemView.left,
                    itemView.top,
                    itemView.left + dX.toInt(),
                    itemView.bottom
                )
                backgroundDelete.draw(c)

                val iconLeft = itemView.left + ICON_MARGIN
                val iconRight = itemView.left + ICON_MARGIN + iconDelete.intrinsicWidth
                iconDelete.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                iconDelete.draw(c)
            }
            dX < 0 -> { // Swiping to the left
                backgroundEdit.setBounds(
                    itemView.right + dX.toInt(),
                    itemView.top,
                    itemView.right,
                    itemView.bottom
                )
                backgroundEdit.draw(c)

                val iconLeft = itemView.right - ICON_MARGIN - iconEdit.intrinsicWidth
                val iconRight = itemView.right - ICON_MARGIN
                iconEdit.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                iconEdit.draw(c)
            }
            else -> { // view is unSwiped
                backgroundDelete.setBounds(0, 0, 0, 0)
                backgroundEdit.setBounds(0, 0, 0, 0)
            }
        }
    }
}
