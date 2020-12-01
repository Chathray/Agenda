package com.chath.agenda;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class SwipeToDeleteCallback extends ItemTouchHelper.SimpleCallback {

    private ContentAdapter adapter;
    private ColorDrawable background;
    private Activity main;

    public SwipeToDeleteCallback(ContentAdapter adapter, Activity main) {
        super(0, ItemTouchHelper.LEFT);

        this.adapter = adapter;
        this.main = main;
        background = new ColorDrawable();
        background.setColor(Color.parseColor("#F85F47"));
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

        View itemView = viewHolder.itemView;
        int backgroundCornerOffset = 20;

        // Swiping to the left
        if (dX < 0) {
            background.setBounds(itemView.getRight() + ((int) dX) - backgroundCornerOffset,
                    itemView.getTop(), itemView.getRight(), itemView.getBottom());
            background.draw(c);

            int font_height = AppUtilities.dpToPx(17f, main);
            Paint paint = new Paint();
            paint.setColor(Color.parseColor("#FFFFFF"));
            paint.setTextSize(font_height);

            int x = (int) (itemView.getX() + itemView.getWidth()) + 32;
            int y = (int) ((int) itemView.getY() + (itemView.getHeight() >>1) + (font_height / 2.7f));
            c.drawText(main.getString(R.string.remove_content_item), x, y, paint);
        }
        // view is unSwiped
        else {
            background.setBounds(0, 0, 0, 0);
            background.draw(c);
        }
    }

    @Override
    public float getSwipeThreshold(@NonNull RecyclerView.ViewHolder viewHolder) {
        return 0.9F;
    }

    @Override
    public float getSwipeEscapeVelocity(float defaultValue) {
        return defaultValue * 8;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
    }
}