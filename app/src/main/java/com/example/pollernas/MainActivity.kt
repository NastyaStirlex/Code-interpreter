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
    var variablesAddAssingment = mutableListOf<String>() // переменные, добавленные присваиванием без объявления

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setUpRecycler()
        val newValueVar = RPN().rpnToAnswer(RPN().expressionToRPN(RPN().preparingExpression(valueVar)), variablesMap)
    }

    private fun outPut(str: String) {
        val builder1 = AlertDialog.Builder(this)
        val inflater1 = layoutInflater
        val dialogLayout1 = inflater1.inflate(R.layout.output_text_layout, null)

        with(builder1) {
            setTitle("Тут будет прикольная фраза")
            if (true) { // тут проверка всё ли корректно
                //Тут код для вывода
                dialogLayout1.textView.text = str
            }
            else {
                dialogLayout1.textView.text = "Ошибка! Что-то пошло не так..."
            }
            setView(dialogLayout1)
            show()
        }
    }

    private fun startCode() { // выполнение алгоритма
        buttonStart.setOnClickListener {
            itemsList = dragDropAdapter.getArray()
            var i = 0
            
            while(i != itemsList.size) {
                val nameBlock = itemsList[i].name
                var etValue = itemsList[i].editTextValue
                Log.i("List", "$nameBlock : $etValue")

                when(nameBlock) {
                    "Объявление переменных" -> {i += 1}

                    "Присваивание" -> {
                        val result = BlocksFuns(variablesMap).assingment(etValue)
                        Log.i("Result", "$result")
                        if (result.first != -1) {
                            variablesMap[result.second] = result.first
                            Log.i("Assingment","Переменной ${result.second} присвоено значение ${result.first}")
                        }
                        i += 1
                    }

                    "Условие Начало" -> {
                        etValue = etValue.replace("\\s".toRegex(), "")
                        val newStr = etValue.replace("\\s".toRegex(), "").split(">", "<", "<=", ">=", "==", "!=")
                        val leftPart = newStr[0]
                        val rightPart = newStr[1]
                        var delimiter = etValue.slice(leftPart.length..etValue.indexOf(rightPart[0]) - 1)

                        var leftExp = RPN().rpnToAnswer(RPN().expressionToRPN(RPN().preparingExpression(leftPart)), variablesMap)
                        val rightExp = RPN().rpnToAnswer(RPN().expressionToRPN(RPN().preparingExpression(rightPart)), variablesMap)

                        var j = 0
                        when (delimiter) {
                            ">" -> {
                                if (leftExp > rightExp) {
                                    j = i + 1
                                    while (itemsList[j].name != "Условие Конец" && j < itemsList.size) {
                                        val nameBlock = itemsList[j].name
                                        var etValue = itemsList[j].editTextValue
                                        when (nameBlock) {
                                            "Объявление переменных" -> {j += 1}
                                            "Присваивание" -> {
                                                val result = BlocksFuns(variablesMap).assingment(etValue)
                                                if (result.first != -1) {
                                                    variablesMap[result.second] = result.first
                                                    Log.i("Assingment","Переменной ${result.second} присвоено значение ${result.first}")
                                                } else {
                                                    Toast.makeText(applicationContext, "Некорректное выражение!", Toast.LENGTH_SHORT).show()
                                                }
                                                j += 1
                                            }
                                            "Вывод переменных" -> {
                                                outPut(BlocksFuns(variablesMap).output(etValue))
                                                j += 1
                                            }
                                        }
                                    }
                                }
                            }

                            "<" -> {
                                if (leftExp < rightExp) {
                                    j = i + 1
                                    while (itemsList[j].name != "Условие Конец" && j < itemsList.size) {
                                        val nameBlock = itemsList[j].name
                                        var etValue = itemsList[j].editTextValue
                                        when (nameBlock) {
                                            "Объявление переменных" -> {j += 1}
                                            "Присваивание" -> {
                                                val result = BlocksFuns(variablesMap).assingment(etValue)
                                                if (result.first != -1) {
                                                    variablesMap[result.second] = result.first
                                                    Log.i("Assingment","Переменной ${result.second} присвоено значение ${result.first}")
                                                } else {
                                                    Toast.makeText(applicationContext, "Некорректное выражение!", Toast.LENGTH_SHORT).show()
                                                }
                                                j += 1
                                            }
                                            "Вывод переменных" -> {
                                                outPut(BlocksFuns(variablesMap).output(etValue))
                                                j += 1
                                            }
                                        }
                                    }
                                }
                            }
                            ">=" -> {
                                if (leftExp >= rightExp) {
                                    j = i + 1
                                    while (itemsList[j].name != "Условие Конец" && j < itemsList.size) {
                                        val nameBlock = itemsList[j].name
                                        var etValue = itemsList[j].editTextValue
                                        when (nameBlock) {
                                            "Объявление переменных" -> {j += 1}
                                            "Присваивание" -> {
                                                val result = BlocksFuns(variablesMap).assingment(etValue)
                                                if (result.first != -1) {
                                                    variablesMap[result.second] = result.first
                                                    Log.i("Assingment","Переменной ${result.second} присвоено значение ${result.first}")
                                                } else {
                                                    Toast.makeText(applicationContext, "Некорректное выражение!", Toast.LENGTH_SHORT).show()
                                                }
                                                j += 1
                                            }
                                            "Вывод переменных" -> {
                                                outPut(BlocksFuns(variablesMap).output(etValue))
                                                j += 1
                                            }
                                        }
                                    }
                                }
                            }
                            "<=" -> {
                                if (leftExp <= rightExp) {
                                    j = i + 1
                                    while (itemsList[j].name != "Условие Конец" && j < itemsList.size) {
                                        val nameBlock = itemsList[j].name
                                        var etValue = itemsList[j].editTextValue
                                        when (nameBlock) {
                                            "Объявление переменных" -> {j += 1}
                                            "Присваивание" -> {
                                                val result = BlocksFuns(variablesMap).assingment(etValue)
                                                if (result.first != -1) {
                                                    variablesMap[result.second] = result.first
                                                    Log.i("Assingment","Переменной ${result.second} присвоено значение ${result.first}")
                                                } else {
                                                    Toast.makeText(applicationContext, "Некорректное выражение!", Toast.LENGTH_SHORT).show()
                                                }
                                                j += 1
                                            }
                                            "Вывод переменных" -> {
                                                outPut(BlocksFuns(variablesMap).output(etValue))
                                                j += 1
                                            }
                                        }
                                    }
                                }
                            }
                            "!=" -> {
                                if (leftExp != rightExp) {
                                    j = i + 1
                                    while (itemsList[j].name != "Условие Конец" && j < itemsList.size) {
                                        val nameBlock = itemsList[j].name
                                        var etValue = itemsList[j].editTextValue
                                        when (nameBlock) {
                                            "Объявление переменных" -> {j += 1}
                                            "Присваивание" -> {
                                                val result = BlocksFuns(variablesMap).assingment(etValue)
                                                if (result.first != -1) {
                                                    variablesMap[result.second] = result.first
                                                    Log.i("Assingment","Переменной ${result.second} присвоено значение ${result.first}")
                                                } else {
                                                    Toast.makeText(applicationContext, "Некорректное выражение!", Toast.LENGTH_SHORT).show()
                                                }
                                                j += 1
                                            }
                                            "Вывод переменных" -> {
                                                outPut(BlocksFuns(variablesMap).output(etValue))
                                                j += 1
                                            }
                                        }
                                    }
                                }
                            }
                            "==" -> {
                                if (leftExp == rightExp) {
                                    j = i + 1
                                    while (itemsList[j].name != "Условие Конец" && j < itemsList.size) {
                                        val nameBlock = itemsList[j].name
                                        var etValue = itemsList[j].editTextValue
                                        when (nameBlock) {
                                            "Объявление переменных" -> {j += 1}
                                            "Присваивание" -> {
                                                val result = BlocksFuns(variablesMap).assingment(etValue)
                                                if (result.first != -1) {
                                                    variablesMap[result.second] = result.first
                                                    Log.i("Assingment","Переменной ${result.second} присвоено значение ${result.first}")
                                                } else {
                                                    Toast.makeText(applicationContext, "Некорректное выражение!", Toast.LENGTH_SHORT).show()
                                                }
                                                j += 1
                                            }
                                            "Вывод переменных" -> {
                                                outPut(BlocksFuns(variablesMap).output(etValue))
                                                j += 1
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        i = j
                    }

                    "Условие Конец" -> {Log.i("i = ","$i"); i += 1} // конец условия

                    "Пока Начало" -> {
                        etValue = etValue.replace("\\s".toRegex(), "")
                        val newStr = etValue.replace("\\s".toRegex(), "").split(">", "<", "<=", ">=", "==", "!=")
                        val leftPart = newStr[0]
                        val rightPart = newStr[1]

                        var delimiter = etValue.slice(leftPart.length..etValue.indexOf(rightPart[0]) - 1)

                        var leftExp = RPN().rpnToAnswer(RPN().expressionToRPN(RPN().preparingExpression(leftPart)), variablesMap)
                        var rightExp = RPN().rpnToAnswer(RPN().expressionToRPN(RPN().preparingExpression(rightPart)), variablesMap)
                        Log.i("Left Right","$leftExp $rightExp")
                        var j = 0
                        when (delimiter) {
                            ">" -> {
                                while (leftExp > rightExp) {
                                    j = i + 1
                                    Log.i("i = ", "$i")
                                    Log.i("j = ", "$j")

                                    while (itemsList[j].name != "Пока Конец" && j < itemsList.size) {
                                        val nameBlock = itemsList[j].name
                                        var etValue = itemsList[j].editTextValue
                                        when (nameBlock) {
                                            "Объявление переменных" -> {j += 1}
                                            "Присваивание" -> {
                                                val result = BlocksFuns(variablesMap).assingment(etValue)
                                                if (result.first != -1) {
                                                    variablesMap[result.second] = result.first
                                                    Log.i("Assingment","Переменной ${result.second} присвоено значение ${result.first}")
                                                } else {
                                                    Toast.makeText(applicationContext, "Некорректное выражение!", Toast.LENGTH_SHORT).show()
                                                }
                                                j += 1
                                            }
                                            "Вывод переменных" -> {
                                                outPut(BlocksFuns(variablesMap).output(etValue))
                                                j += 1
                                            }
                                        }
                                        leftExp = RPN().rpnToAnswer(RPN().expressionToRPN(RPN().preparingExpression(leftPart)), variablesMap)
                                        rightExp = RPN().rpnToAnswer(RPN().expressionToRPN(RPN().preparingExpression(rightPart)), variablesMap)
                                    }

                                }
                            }
                            "<" -> {}
                            ">=" -> {}
                            "<=" -> {}
                            "==" -> {}
                            "!=" -> {}
                        }
                    }

                    "Вывод переменных" -> {Log.i("i = ","$i"); outPut(BlocksFuns(variablesMap).output(etValue)); i += 1}
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
                        Toast.makeText(applicationContext, "Блок удалён", Toast.LENGTH_SHORT).show()

                        //todo: add deleted code
                        val nameBlock = item.name
                        val editText = item.editTextValue
                        if (nameBlock == "Объявление переменных") {
                            if (BlockModule(nameBlock, editText).isCorrect(editText)) { // одна переменная
                                variablesMap.remove(editText, 0)
                                Toast.makeText(applicationContext, "Если эта переменная используется далее и все сломается, мы ни при чем:)", Toast.LENGTH_SHORT).show()
                            } else { // несколько переменных
                                val newStr = editText.replace("\\s".toRegex(), "").split(",")
                                for (name in newStr) {
                                    variablesMap.remove(name, 0)
                                }
                                Toast.makeText(applicationContext, "Если эти переменные используются далее и все сломается, мы ни при чем:)", Toast.LENGTH_SHORT).show()
                            }
                        } else if (nameBlock == "Присваивание") {
                            Log.i("AssDec","${variablesAddAssingment.toString()}")
                            val nameVar = editText.substringBefore('=')
                            if (nameVar in variablesAddAssingment) { // если переменную объявили и присвоили в блоке Присваивание
                                variablesMap.remove(nameVar)
                                variablesAddAssingment.remove(nameVar)

                                Toast.makeText(applicationContext, "За дальнейшие ошибки ответственности не несем:)", Toast.LENGTH_SHORT).show()
                            } else { // удаляем присваивание, переменная просто объявлена
                                variablesMap[nameVar] = 0
                                // Toast.makeText(applicationContext, "Значение $nameVar снова 0", Toast.LENGTH_SHORT).show()
                            }
                        }
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
                            } else if (!itsOkay) {
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

                        if (BlockModule(nameBlock, editText).isCorrectAssingment(editText)) {
                            val result = BlocksFuns(variablesMap).assingment(editText)
                            if (result.second !in variablesMap.keys) {
                                variablesAddAssingment.add(result.second)
                            }
                            dragDropAdapter.updateItem(newBlock)

                            if (result.first != -1) {
                                Log.i("Assingment","Переменной ${result.second} присвоено значение ${result.first}")
                                variablesMap[result.second] = result.first
                            }
                            Toast.makeText(applicationContext, "Блок успешно добавлен!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(applicationContext, "Некорректное выражение!", Toast.LENGTH_SHORT).show()
                        }
                    } else if(nameBlock == "Условие Начало") {
                        var newBlock = BlockModule(nameBlock, editText)
                        if (BlockModule(nameBlock, editText).isCorrectIf(editText)) {
                            dragDropAdapter.updateItem(newBlock)
                            Toast.makeText(
                                applicationContext,
                                "Блок успешно добавлен!",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                applicationContext,
                                "Некорректное выражение!",
                                Toast.LENGTH_SHORT
                            ).show()
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

                    } else if(nameBlock == "Пока Начало") {
                        var newBlock = BlockModule(nameBlock, editText)
                        if (BlockModule(nameBlock, editText).isCorrectIf(editText)) {
                            dragDropAdapter.updateItem(newBlock)
                            Toast.makeText(
                                applicationContext,
                                "Блок успешно добавлен!",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                applicationContext,
                                "Некорректное выражение!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else { // условие конец, пока конец
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