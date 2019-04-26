package feelings.guide.ui;

import android.database.Cursor;
import android.database.DataSetObserver;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import static android.provider.BaseColumns._ID;

public abstract class RecyclerViewCursorAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    protected final BaseActivity activity;
    private Cursor cursor;
    private boolean isDataValid;
    private int rowIdColumn;

    public RecyclerViewCursorAdapter(BaseActivity activity, Cursor cursor) {
        this.activity = activity;
        setHasStableIds(true);
        swapCursor(cursor);
    }

    public BaseActivity getActivity() {
        return activity;
    }

    protected abstract void onBindViewHolder(VH holder, Cursor cursor);

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        if (!isDataValid) {
            throw new IllegalStateException("this should only be called when the cursor is valid");
        }
        if (!cursor.moveToPosition(position)) {
            throw new IllegalStateException("couldn't move cursor to position " + position);
        }
        onBindViewHolder(holder, cursor);
    }

    @Override
    public long getItemId(int position) {
        if (isDataValid && cursor != null && cursor.moveToPosition(position)) {
            return cursor.getLong(rowIdColumn);
        }
        return RecyclerView.NO_ID;
    }

    @Override
    public int getItemCount() {
        if (isDataValid && cursor != null) {
            return cursor.getCount();
        } else {
            return 0;
        }
    }

    protected Cursor getCursor() {
        return cursor;
    }

    public void changeCursor(Cursor cursor) {
        Cursor old = swapCursor(cursor);
        if (old != null) {
            old.close();
        }
    }

    public Cursor swapCursor(Cursor newCursor) {
        if (newCursor == cursor) {
            return null;
        }
        Cursor oldCursor = cursor;
        if (oldCursor != null) {
            if (mDataSetObserver != null) {
                oldCursor.unregisterDataSetObserver(mDataSetObserver);
            }
        }
        cursor = newCursor;
        if (newCursor != null) {
            if (mDataSetObserver != null) {
                newCursor.registerDataSetObserver(mDataSetObserver);
            }
            rowIdColumn = newCursor.getColumnIndexOrThrow(_ID);
            isDataValid = true;
            notifyDataSetChanged();
        } else {
            rowIdColumn = -1;
            isDataValid = false;
            notifyDataSetChanged();
        }
        return oldCursor;
    }


    private DataSetObserver mDataSetObserver = new DataSetObserver() {
        @Override
        public void onChanged() {
            isDataValid = true;
            notifyDataSetChanged();
        }

        @Override
        public void onInvalidated() {
            isDataValid = false;
            notifyDataSetChanged();
        }
    };
}