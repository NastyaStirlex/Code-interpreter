package com.example.pollernas

import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.ernestoyaquello.dragdropswiperecyclerview.DragDropSwipeRecyclerView
import com.ernestoyaquello.dragdropswiperecyclerview.listener.OnItemSwipeListener
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private lateinit var dragDropAdapter: DragDropAdapter
    private var itemsList = mutableListOf<blockModule>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //  createFakeItems()
        setUpRecycler()
    }

    // private fun createFakeItems() {
    //      var newBlock1 = blockModule("Ввод переменных",null)
    //     var newBlock2 = blockModule("Присваивание",null)
    //     itemsList.add(newBlock1)
    //      itemsList.add(newBlock2)
    // }

    private fun setUpRecycler() {
        dragDropAdapter =
            DragDropAdapter(itemsList)
        val mList: DragDropSwipeRecyclerView = findViewById(R.id.list)
        mList.layoutManager = LinearLayoutManager(this)
        mList.adapter = dragDropAdapter

        mList.orientation =
            DragDropSwipeRecyclerView.ListOrientation.VERTICAL_LIST_WITH_VERTICAL_DRAGGING
        mList.reduceItemAlphaOnSwiping = true

        //mList.disableSwipeDirection(DragDropSwipeRecyclerView.ListOrientation.DirectionFlag.RIGHT)

        val onItemSwipeListener = object : OnItemSwipeListener<blockModule> {
            override fun onItemSwiped(
                position: Int,
                direction: OnItemSwipeListener.SwipeDirection,
                item: blockModule
            ): Boolean {
                Log.d("Main", "Position = $position, Direction = $direction, Item = $item")

                when (direction) {
                    //Delete Item
                    OnItemSwipeListener.SwipeDirection.RIGHT_TO_LEFT -> {
                        Toast.makeText(
                            applicationContext,
                            "Блок удален",
                            Toast.LENGTH_SHORT
                        ).show()
                        //todo: add deleted code
                    }
                    //Archive Item
                    OnItemSwipeListener.SwipeDirection.LEFT_TO_RIGHT -> {
                        Toast.makeText(
                            applicationContext,
                            "Блок архивирован",
                            Toast.LENGTH_SHORT
                        ).show()
                        //todo: add archived code here
                    }
                    else -> return false
                }
                return false
            }
        }
        mList.swipeListener = onItemSwipeListener

        // button
        fabAddItem()
        startCode()
    }

    private fun fabAddItem() {
        fab_add.setOnClickListener {
            Log.d("Main", "Button pressed")
            val builder = AlertDialog.Builder(this)
            val inflater = layoutInflater
            val dialogLayout = inflater.inflate(R.layout.edit_text_layout, null)
            val spinner = dialogLayout.findViewById<Spinner>(R.id.spinner)
            val editText = dialogLayout.findViewById<EditText>(R.id.et_editText).text

            with(builder) {
                setTitle("Выбери нужный блок")
                setPositiveButton("OK") { dialog, which ->
                    var newBlock = blockModule(spinner.selectedItem.toString(), editText.toString())
                    dragDropAdapter.updateItem(newBlock)

                    Toast.makeText(
                        applicationContext,
                        "Блок успешно добавлен!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                setNegativeButton("Отмена") { dialog, which ->
                    Log.d("Main", "Negative button clicked")
                }
                setView(dialogLayout)
                show()
            }
        }
    }

    private fun startCode() {
        buttonStart.setOnClickListener {
            itemsList = dragDropAdapter.getArray()
            for (i in 0..itemsList.size-1) {
                Log.i(itemsList[i].name,itemsList[i].editTextValue)
            }
        }
    }
}
