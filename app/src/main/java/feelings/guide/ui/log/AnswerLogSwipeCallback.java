package feelings.guide.ui.log;

import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import feelings.guide.R;

public class AnswerLogSwipeCallback extends ItemTouchHelper.SimpleCallback {
    private AnswerLogAdapter adapter;
    private Drawable iconDelete;
    private Drawable iconEdit;
    private final ColorDrawable backgroundDelete;
    private final ColorDrawable backgroundEdit;

    public AnswerLogSwipeCallback(AnswerLogAdapter adapter) {
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.adapter = adapter;
        iconDelete = ContextCompat.getDrawable(adapter.getActivity(),
                R.drawable.ic_delete_sweep_white_24dp);
        iconEdit = ContextCompat.getDrawable(adapter.getActivity(),
                R.drawable.ic_edit_white_24dp);
        backgroundDelete = new ColorDrawable(ResourcesCompat.getColor(adapter.getActivity().getResources(),
                R.color.lightRed, null));
        backgroundEdit = new ColorDrawable(ResourcesCompat.getColor(adapter.getActivity().getResources(),
                R.color.lightGreen, null));
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView,
                          @NonNull RecyclerView.ViewHolder viewHolder,
                          @NonNull RecyclerView.ViewHolder target) {
        // used for up and down movements
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();
        if (direction == ItemTouchHelper.RIGHT) {
            adapter.deleteAnswer(position);
        } else if (direction == ItemTouchHelper.LEFT) {
            adapter.editAnswer(position);
        }
    }

    @Override
    public void onChildDraw(@NonNull Canvas c,
                            @NonNull RecyclerView recyclerView,
                            @NonNull RecyclerView.ViewHolder viewHolder,
                            float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        View itemView = viewHolder.itemView;

        int iconHeight = 0;
        if (dX > 0) {
            iconHeight = iconDelete.getIntrinsicHeight();
        } else if (dX < 0) {
            iconHeight = iconEdit.getIntrinsicHeight();
        }
        int iconMargin = (itemView.getHeight() - iconHeight) / 2;
        int iconTop = itemView.getTop() + (itemView.getHeight() - iconHeight) / 2;
        int iconBottom = iconTop + iconHeight;

        if (dX > 0) { // Swiping to the right
            backgroundDelete.setBounds(
                    itemView.getLeft(),
                    itemView.getTop(),
                    itemView.getLeft() + ((int) dX),
                    itemView.getBottom());
            backgroundDelete.draw(c);

            int iconLeft = itemView.getLeft() + iconMargin;
            int iconRight = itemView.getLeft() + iconMargin + iconDelete.getIntrinsicWidth();
            iconDelete.setBounds(iconLeft, iconTop, iconRight, iconBottom);
            iconDelete.draw(c);
        } else if (dX < 0) { // Swiping to the left
            backgroundEdit.setBounds(
                    itemView.getRight() + ((int) dX),
                    itemView.getTop(),
                    itemView.getRight(),
                    itemView.getBottom());
            backgroundEdit.draw(c);

            int iconLeft = itemView.getRight() - iconMargin - iconEdit.getIntrinsicWidth();
            int iconRight = itemView.getRight() - iconMargin;
            iconEdit.setBounds(iconLeft, iconTop, iconRight, iconBottom);
            iconEdit.draw(c);
        } else { // view is unSwiped
            backgroundDelete.setBounds(0, 0, 0, 0);
            backgroundEdit.setBounds(0, 0, 0, 0);
        }
    }
}
