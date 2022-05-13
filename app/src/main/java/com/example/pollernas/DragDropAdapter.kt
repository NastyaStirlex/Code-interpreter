package com.example.pollernas

import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.ernestoyaquello.dragdropswiperecyclerview.DragDropSwipeAdapter

class DragDropAdapter(dataSet: MutableList<blockModule>) :
    DragDropSwipeAdapter<blockModule, DragDropAdapter.ViewHolder>(dataSet)  {

    private var list: MutableList<blockModule> = this.dataSet as MutableList<blockModule>

    inner class ViewHolder(itemView: View) : DragDropSwipeAdapter.ViewHolder(itemView) {
        val itemName: TextView = itemView.findViewById(R.id.item_text)
        val itemText: TextView = itemView.findViewById(R.id.item_meaning)
        val dragIcon: ImageView = itemView.findViewById(R.id.drag_icon)

        init {
            itemView.setOnClickListener { v: View ->
                val position: Int = adapterPosition
                Log.d("DragDropAdapter", list[position].toString())

                Toast.makeText(itemView.context, dataSet[position].name, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun getViewHolder(itemView: View) = ViewHolder(itemView)

    override fun onBindViewHolder(item: blockModule, viewHolder: ViewHolder, position: Int) {
        // Here we update the contents of the view holder's views to reflect the item's data
        viewHolder.itemName.text = dataSet[position].name
        viewHolder.itemText.text = dataSet[position].editTextValue
    }

    override fun getViewToTouchToStartDraggingItem(
        item: blockModule,
        viewHolder: ViewHolder,
        position: Int
    ): View? {
        // We return the view holder's view on which the user has to touch to drag the item
        return viewHolder.dragIcon
    }

    override fun onDragFinished(item: blockModule, viewHolder: ViewHolder) {
        super.onDragFinished(item, viewHolder)
        Log.d("DragDropAdapter", "$dataSet")}

    fun updateItem(item: blockModule) {
        list.add(item)
        notifyDataSetChanged()

        Log.d("MyAdapter", "${list}")
    }
    fun getArray() : MutableList<blockModule> {return list}
}