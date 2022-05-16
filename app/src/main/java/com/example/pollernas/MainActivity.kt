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
    private var itemsList = mutableListOf<BlockModule>()
    var variablesMap = mutableMapOf<String, Int>() // словарь переменных и их значений

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //  createFakeItems()
        setUpRecycler()
    }

    // private fun createFakeItems() {
    //      var newBlock1 = BlockModule("Ввод переменных",null)
    //     var newBlock2 = BlockModule("Присваивание",null)
    //     itemsList.add(newBlock1)
    //      itemsList.add(newBlock2)
    // }


    private fun outPut(str: String) {
        val builder1 = AlertDialog.Builder(this)
        val inflater1 = layoutInflater
        val dialogLayout1 = inflater1.inflate(R.layout.output_text_layout, null)

        with(builder1) {
            setTitle("Тут будет прикольная фраза")
            if (true){ // тут проверка всё ли корректно
                //Тут код для вывода
                dialogLayout1.textView.text = str
            }
            else{
                dialogLayout1.textView.text = "Ошибка! Что-то пошло не так..."
            }
            setView(dialogLayout1)
            show()
        }
    }

    private fun startCode() { // выполнение алгоритма
        buttonStart.setOnClickListener {
            itemsList = dragDropAdapter.getArray()

            for (i in 0..itemsList.size - 1) {
                val nameBlock = itemsList[i].name
                val etValue = itemsList[i].editTextValue
                Log.i("List", "$nameBlock : $etValue")
                when(nameBlock) {
                    //"Объявление переменных" -> {}
                    "Присваивание" -> {
                        val result = BlocksFuns(variablesMap).assingment(etValue)
                        Log.i("Result", "$result")
                        if (result.first != -1) {
                            Log.i("Assingment","Переменной ${result.second} присвоено значение ${result.first}")
                            //Toast.makeText(applicationContext, "Переменной ${result.second} присвоено значение $result.first", Toast.LENGTH_SHORT).show()
                        }
                    }

                    "Условие Начало" -> {} // старт условия
                    "Условие Конец" -> {} // конец условия
                    "Вывод переменных" -> {outPut(BlocksFuns(variablesMap).output(etValue))} // вывод, передаю строку,  которую надо вывести
                }
            }
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

        val onItemSwipeListener = object : OnItemSwipeListener<BlockModule> {
            override fun onItemSwiped(
                position: Int,
                direction: OnItemSwipeListener.SwipeDirection,
                item: BlockModule
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
                    Log.i("Button", "Pressed OK")
                    val editText =
                        dialogLayout.findViewById<EditText>(R.id.et_editText).text.toString() // строка - код блока
                    val nameBlock = spinner.selectedItem.toString() // тип блока
                    val editTextView =
                        dialogLayout.findViewById<EditText>(R.id.et_editText) // сам блок как View


                    if (nameBlock == "Объявление переменных") {
                        var newBlock = BlockModule(nameBlock, editText)

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
                                newVariables.forEach{
                                    variablesMap[it] = 0
                                }

                                Toast.makeText(applicationContext,
                                    "Блок успешно добавлен! Объявлены переменные: ${newVariables.joinToString(", ")}",
                                    Toast.LENGTH_LONG
                                ).show()
                            } else if(!itsOkay) {
                                Toast.makeText(
                                    applicationContext,
                                    "Блок не может быть добавлен:(",
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
                                        "${"Некорректные имена переменных: " + incorrectVariables.joinToString(", ")}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        } else {
                            Toast.makeText(applicationContext, "Некорректная строка", Toast.LENGTH_SHORT).show()
                        }
                    } else if(nameBlock == "Присваивание") {
                        var newBlock = BlockModule(nameBlock, editText)
                        // корректно ли выражение
                        if (BlockModule(nameBlock, editText).isCorrectAssingment(editText)) {
                            dragDropAdapter.updateItem(newBlock)
                            if (editText.substringBefore('=') !in variablesMap.keys) {
                                val result = BlocksFuns(variablesMap).assingment(editText)
                                Log.i("Result", "$result")
                                if (result.first != -1) {
                                    Log.i("Assingment","Переменной ${result.second} присвоено значение ${result.first}")
                                    variablesMap[result.second] = result.first
                                }
                            }


                            Toast.makeText(applicationContext, "Блок успешно добавлен!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(applicationContext, "Некорректное выражение!", Toast.LENGTH_SHORT).show()
                        }
                    } else if(nameBlock == "Условие Начало") {
                        if(true) { // дописать

                        } else {
                            Toast.makeText(applicationContext, "Некорректное выражение!", Toast.LENGTH_SHORT).show()
                        }
                    } else if(nameBlock == "Вывод переменных") {
                        var newBlock = BlockModule(nameBlock, editText)

                        if (newBlock.isCorrect(editText)) { // одна переменная
                            if (editText !in variablesMap.keys) {
                                Toast.makeText(
                                    applicationContext,
                                    "Такой переменной нет!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                dragDropAdapter.updateItem(newBlock)
                                Toast.makeText(applicationContext, "Блок успешно добавлен!", Toast.LENGTH_SHORT).show()
                            }
                        } else if(newBlock.isSequence(editText)) { // несколько переменных ч/з запятую)
                            val listOfVar = editText.replace("\\s".toRegex(), "").split(",")
                            var notDeclaratedVar: MutableList<String> = mutableListOf()
                            var incorrectVar: MutableList<String> = mutableListOf()
                            var itsOkay = true

                            for (name in listOfVar) {
                                if (newBlock.isCorrect(name)) {
                                    if (name !in variablesMap.keys) {
                                        notDeclaratedVar.add(name)
                                        itsOkay = false
                                    }
                                } else {
                                    incorrectVar.add(name)
                                    itsOkay = false
                                }
                            }
                            if(itsOkay) {
                                dragDropAdapter.updateItem(newBlock)
                                Toast.makeText(applicationContext, "Блок успешно добавлен!", Toast.LENGTH_SHORT).show()
                            } else if(!itsOkay) {
                                Toast.makeText(applicationContext,"Блок не может быть добавлен.", Toast.LENGTH_SHORT).show()
                                if(notDeclaratedVar.isNotEmpty()) {
                                    Toast.makeText(
                                        applicationContext,
                                        "Не объявлены такие переменные: " + notDeclaratedVar.joinToString(", "),
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                                if(incorrectVar.isNotEmpty()) {
                                    Toast.makeText(
                                        applicationContext,
                                        "Некорректные имена переменных: " + incorrectVar.joinToString(", "),
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        } else {
                            Toast.makeText(applicationContext, "Некорректная строка", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        var newBlock = BlockModule(nameBlock, editText)
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