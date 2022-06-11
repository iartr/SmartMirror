package com.iartr.smartmirror.ui.currency.util

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration

class VerticalSpaceItemDecoration(private val verticalSpaceHeight: Int) : ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
    ) {
        if (parent.adapter == null) return
        if (parent.getChildAdapterPosition(view) != parent.adapter!!.itemCount.minus(1)) {
            outRect.bottom = verticalSpaceHeight;
        }
    }
}