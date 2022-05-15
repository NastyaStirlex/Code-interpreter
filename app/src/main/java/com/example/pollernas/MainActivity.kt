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
import kotlinx.android.synthetic.main.output_text_layout.view.*

class MainActivity : AppCompatActivity() {

    private lateinit var dragDropAdapter: DragDropAdapter
    private var itemsList = mutableListOf<blockModule>()
    var variablesMap = mutableMapOf<String, Int>() // словарь переменных и их значений

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



    private fun outPut() {
        val builder1 = AlertDialog.Builder(this)
        val inflater1 = layoutInflater
        val dialogLayout1 = inflater1.inflate(R.layout.output_text_layout, null)

        with(builder1) {
            setTitle("Тут будет прикольная фраза")
            if (true){ // тут проверка всё ли корректно
                //Тут код для вывода
                dialogLayout1.textView.text = "Готово!"
            }
            else{
                dialogLayout1.textView.text = "Ошибка! Что-то пошло не так..."
            }
            setView(dialogLayout1)
            show()
        }
    }

    fun startCode() {
        buttonStart.setOnClickListener {
            outPut()
//            itemsList = dragDropAdapter.getArray()
//            for (i in 0..itemsList.size-1) {
//                Log.i("List", "${itemsList[i].name} : ${itemsList[i].editTextValue}")
//                //when(itemsList[i].name) {
//                    //"Объявление переменных" -> {blockModule(itemsList[i].name, itemsList[i].editTextValue, variablesMap).declaration(itemsList[i].editTextValue)} // объявление
//                    //"Присваивание" -> {blockModule(itemsList[i].name, itemsList[i].editTextValue, variablesMap).assingment(itemsList[i].editTextValue)} // присваивание
////                    arrayTypes[2] -> {} // старт условия
////                    arrayTypes[3] -> {} // конец условия
//                    //arrayTypes[4] -> {outPut()} // вывод, передаю строку,  которую надо вывести
//                //}
//            }
        }
    }

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
                        //todo: add deleted code, удаляем блок присваивания для ранее объявленной переменной
                       // if(nameBlock == "Объявление переменных")
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


            with(builder) {
                setTitle("Выбери нужный блок")

                setPositiveButton("OK") { dialog, which ->
                    val editText =
                        dialogLayout.findViewById<EditText>(R.id.et_editText).text.toString() // строка - код блока
                    val nameBlock = spinner.selectedItem.toString() // тип блока
                    val editTextView =
                        dialogLayout.findViewById<EditText>(R.id.et_editText) // сам блок как View


                    if (nameBlock == "Объявление переменных") {
                        var newBlock = blockModule(nameBlock, editText, variablesMap)

                        if (newBlock.isCorrect(editText)) { // одна переменная
                            if (editText in variablesMap.keys) {
                                Toast.makeText(
                                    applicationContext,
                                    "Переменная $editText уже объявлена",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                builder.setCancelable(true);
                                variablesMap.put(editText, 0)
                                dragDropAdapter.updateItem(newBlock)
                                Toast.makeText(
                                    applicationContext,
                                    "Блок успешно добавлен!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                Toast.makeText(
                                    applicationContext,
                                    "Объявлена переменная: $editText",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                        } else if (newBlock.isSequence(editText)) { // несколько переменных ч/з запятую
                            val listOfVar = editText.replace("\\s".toRegex(), "").split(",")
                            var earlyDeclaratedVar: MutableList<String> = mutableListOf()
                            var newVariables: MutableList<String> = mutableListOf()
                            var incorrectVariables: MutableList<String> = mutableListOf()
                            var itsOkay = true
                            for (name in listOfVar) {
                                if (newBlock.isCorrect(name)) {
                                    if (name in variablesMap.keys) {
                                        earlyDeclaratedVar.add(name)
                                        itsOkay = false
                                    } else {
                                        newVariables.add(name)
                                    }
                                } else {
                                    incorrectVariables.add(name)
                                    itsOkay = false
                                }
                            }
                            if (itsOkay) {
                                dragDropAdapter.updateItem(newBlock)

                                Toast.makeText(
                                    applicationContext,
                                    "Блок успешно добавлен! Объявлены переменные: ${newVariables.joinToString(",")}",
                                    Toast.LENGTH_LONG
                                ).show()
                            } else if(!itsOkay) {
                                Toast.makeText(
                                    applicationContext,
                                    "Блок не может быть добавлен.",
                                    Toast.LENGTH_SHORT
                                ).show()
                                if(earlyDeclaratedVar.isNotEmpty()) {
                                    Toast.makeText(
                                        applicationContext,
                                        "${"Уже объявлены такие переменные: " + earlyDeclaratedVar.joinToString(",")}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                                if(incorrectVariables.isNotEmpty()) {
                                    Toast.makeText(
                                        applicationContext,
                                        "${"Некорректные имена переменных: " + incorrectVariables.joinToString(",")}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        }
                        else {
                            Toast.makeText(
                                applicationContext,
                                "Некорректная строка",
                                Toast.LENGTH_SHORT
                            ).show()

                        }
                    } else {
                        var newBlock = blockModule(nameBlock, editText, variablesMap)
                        dragDropAdapter.updateItem(newBlock)

                        Toast.makeText(
                            applicationContext,
                            "Блок успешно добавлен!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                setNegativeButton("Отмена") { dialog, which ->
                    Log.d("Main", "Negative button clicked")
                }

                setView(dialogLayout)
                show()
            }
        }
    }
}
