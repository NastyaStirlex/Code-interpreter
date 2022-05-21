package com.example.pollernas

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.ernestoyaquello.dragdropswiperecyclerview.DragDropSwipeRecyclerView
import com.ernestoyaquello.dragdropswiperecyclerview.listener.OnItemSwipeListener
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.output_text_layout.view.*

class MainActivity : AppCompatActivity() {

    private val rotateOpen: Animation by lazy {
        AnimationUtils.loadAnimation(
            this,
            R.anim.rotate_open_anim
        )
    }
    private val rotateClose: Animation by lazy {
        AnimationUtils.loadAnimation(
            this,
            R.anim.rotate_close_anim
        )
    }
    private val fromBottom: Animation by lazy {
        AnimationUtils.loadAnimation(
            this,
            R.anim.from_bottom_anim
        )
    }
    private val toBottom: Animation by lazy {
        AnimationUtils.loadAnimation(
            this,
            R.anim.to_bottom_anim
        )
    }

    private var clicked = false

    private lateinit var dragDropAdapter: DragDropAdapter
    private var itemsList = mutableListOf<BlockModule>()
    var variablesMap = mutableMapOf<String, Int>() // словарь переменных и их значений
    var variablesAddAssingment =
        mutableListOf<String>() // переменные, добавленные присваиванием без объявления
    var arraysMap = mutableMapOf<String, List<String>>() // хранение массивов

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        burger.setOnClickListener {
            onAddButtonClicked()
        }

        setUpRecycler()

        varr.setOnClickListener {
            Log.d("Main", "Button pressed")
            val builder = AlertDialog.Builder(this)
            val inflater = layoutInflater
            val dialogLayout = inflater.inflate(R.layout.edit_text_layout, null)
            val BlockName = dialogLayout.findViewById<TextView>(R.id.BlockName)
            with(builder) {
                setTitle("Привет! Я блок Объявления")
                BlockName.text = "Объявление переменных"

                setPositiveButton("OK") { dialog, which ->
                    Log.i("Button", "Pressed OK")
                    val editText =
                        dialogLayout.findViewById<EditText>(R.id.et_editText).text.toString() // строка - код блока
                    val nameBlock = BlockName.text.toString() // тип блока
                    var newBlock = BlockModule(nameBlock, editText)



                    if (newBlock.isCorrect(editText)) { // одна переменная
                        if (editText in variablesMap.keys || editText in arraysMap.keys) {
                            Toast.makeText(
                                applicationContext,
                                "Переменная $editText уже объявлена",
                                Toast.LENGTH_SHORT
                            )
                        } else {
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
                                if (name in variablesMap.keys || name in arraysMap.keys) {
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
                            newVariables.forEach {
                                variablesMap[it] = 0
                            }

                            Toast.makeText(
                                applicationContext,
                                "Блок успешно добавлен! Объявлены переменные: ${
                                    newVariables.joinToString(
                                        ", "
                                    )
                                }",
                                Toast.LENGTH_LONG
                            ).show()
                        } else if (!itsOkay) {
                            Toast.makeText(
                                applicationContext,
                                "Блок не может быть добавлен:(",
                                Toast.LENGTH_SHORT
                            ).show()
                            if (earlyDeclaratedVar.isNotEmpty()) {
                                Toast.makeText(
                                    applicationContext,
                                    "${
                                        "Уже объявлены такие переменные: " + earlyDeclaratedVar.joinToString(
                                            ","
                                        )
                                    }",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                            if (incorrectVariables.isNotEmpty()) {
                                Toast.makeText(
                                    applicationContext,
                                    "${
                                        "Некорректные имена переменных: " + incorrectVariables.joinToString(
                                            ", "
                                        )
                                    }",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    } else {
                        Toast.makeText(
                            applicationContext,
                            "Некорректная строка",
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

        assign.setOnClickListener {
            Log.d("Main", "Button pressed")
            val builder = AlertDialog.Builder(this)
            val inflater = layoutInflater
            val dialogLayout = inflater.inflate(R.layout.edit_text_layout, null)
            val BlockName = dialogLayout.findViewById<TextView>(R.id.BlockName)
            with(builder) {
                setTitle("Привет! Я блок Присваивания")
                BlockName.text = "Присваивание"

                setPositiveButton("OK") { dialog, which ->
                    Log.i("Button", "Pressed OK")
                    val editText =
                        dialogLayout.findViewById<EditText>(R.id.et_editText).text.toString() // строка - код блока
                    val nameBlock = BlockName.text.toString() // тип блока
                    var newBlock = BlockModule(nameBlock, editText)

                    if (BlockModule(nameBlock, editText).isCorrectAssingment(editText)) {
                        val result = BlocksFuns(variablesMap).assingment(editText)
                        if (result.second !in variablesMap.keys) {
                            variablesAddAssingment.add(result.second)
                        }
                        dragDropAdapter.updateItem(newBlock)

                        if (result.first != -1) {
                            Log.i(
                                "Assingment",
                                "Переменной ${result.second} присвоено значение ${result.first}"
                            )
                            variablesMap[result.second] = result.first
                        }
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
                }
                setNegativeButton("Отмена") { dialog, which ->
                    Log.d("Main", "Negative button clicked")
                }

                setView(dialogLayout)
                show()
            }
        }

        whilee.setOnClickListener {
            Log.d("Main", "Button pressed")
            val builder = AlertDialog.Builder(this)
            val inflater = layoutInflater
            val dialogLayout = inflater.inflate(R.layout.edit_text_layout, null)
            val BlockName = dialogLayout.findViewById<TextView>(R.id.BlockName)
            with(builder) {
                setTitle("Я блок Цикла")
                BlockName.text = "Пока Начало"

                setPositiveButton("OK") { dialog, which ->
                    Log.i("Button", "Pressed OK")
                    val editText =
                        dialogLayout.findViewById<EditText>(R.id.et_editText).text.toString() // строка - код блока
                    val nameBlock = BlockName.text.toString() // тип блока
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
                }
                setNegativeButton("Отмена") { dialog, which ->
                    Log.d("Main", "Negative button clicked")
                }

                setView(dialogLayout)
                show()
            }
        }

        iff.setOnClickListener {
            Log.d("Main", "Button pressed")
            val builder = AlertDialog.Builder(this)
            val inflater = layoutInflater
            val dialogLayout = inflater.inflate(R.layout.edit_text_layout, null)
            val BlockName = dialogLayout.findViewById<TextView>(R.id.BlockName)
            with(builder) {
                setTitle("Я блок Условия")
                BlockName.text = "Условие Начало"

                setPositiveButton("OK") { dialog, which ->
                    Log.i("Button", "Pressed OK")
                    val editText =
                        dialogLayout.findViewById<EditText>(R.id.et_editText).text.toString() // строка - код блока
                    val nameBlock = BlockName.text.toString() // тип блока
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
                }
                setNegativeButton("Отмена") { dialog, which ->
                    Log.d("Main", "Negative button clicked")
                }

                setView(dialogLayout)
                show()
            }
        }

        outputt.setOnClickListener {
            Log.d("Main", "Button pressed")
            val builder = AlertDialog.Builder(this)
            val inflater = layoutInflater
            val dialogLayout = inflater.inflate(R.layout.edit_text_layout, null)
            val BlockName = dialogLayout.findViewById<TextView>(R.id.BlockName)
            with(builder) {
                setTitle("Я блок Вывода")
                BlockName.text = "Вывод переменных"

                setPositiveButton("OK") { dialog, which ->
                    Log.i("Button", "Pressed OK")
                    val editText =
                        dialogLayout.findViewById<EditText>(R.id.et_editText).text.toString() // строка - код блока
                    val nameBlock = BlockName.text.toString() // тип блока
                    var newBlock = BlockModule(nameBlock, editText)

                    if (newBlock.isCorrect(editText)) { // одна переменная
                        if (editText !in variablesMap.keys && editText !in arraysMap.keys) {
                            Toast.makeText(
                                applicationContext,
                                "Такой переменной нет!",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            dragDropAdapter.updateItem(newBlock)
                            Toast.makeText(
                                applicationContext,
                                "Блок успешно добавлен!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else if (newBlock.isSequence(editText)) { // несколько переменных ч/з запятую)
                        val listOfVar = editText.replace("\\s".toRegex(), "").split(",")
                        var notDeclaratedVar: MutableList<String> = mutableListOf()
                        var incorrectVar: MutableList<String> = mutableListOf()
                        var itsOkay = true

                        for (name in listOfVar) {
                            if (newBlock.isCorrect(name)) {
                                if (name !in variablesMap.keys && name !in arraysMap.keys) {
                                    notDeclaratedVar.add(name)
                                    itsOkay = false
                                }
                            } else {
                                incorrectVar.add(name)
                                itsOkay = false
                            }
                        }
                        if (itsOkay) {
                            dragDropAdapter.updateItem(newBlock)
                            Toast.makeText(
                                applicationContext,
                                "Блок успешно добавлен!",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else if (!itsOkay) {
                            Toast.makeText(
                                applicationContext,
                                "Блок не может быть добавлен.",
                                Toast.LENGTH_SHORT
                            ).show()
                            if (notDeclaratedVar.isNotEmpty()) {
                                Toast.makeText(
                                    applicationContext,
                                    "Не объявлены такие переменные: " + notDeclaratedVar.joinToString(
                                        ", "
                                    ),
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                            if (incorrectVar.isNotEmpty()) {
                                Toast.makeText(
                                    applicationContext,
                                    "Некорректные имена переменных: " + incorrectVar.joinToString(", "),
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    } else {
                        Toast.makeText(
                            applicationContext,
                            "Некорректная строка",
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

        arr.setOnClickListener {
            Log.d("Main", "Button pressed")
            val builder = AlertDialog.Builder(this)
            val inflater = layoutInflater
            val dialogLayout = inflater.inflate(R.layout.edit_text_layout, null)
            val BlockName = dialogLayout.findViewById<TextView>(R.id.BlockName)
            with(builder) {
                setTitle("Я блок Массива")
                BlockName.text = "Имя и элементы массива"

                setPositiveButton("OK") { dialog, which ->
                    Log.i("Button", "Pressed OK")
                    val editText =
                        dialogLayout.findViewById<EditText>(R.id.et_editText).text.toString() // строка - код блока
                    val nameBlock = BlockName.text.toString() // тип блока

                    var newBlock = BlockModule(nameBlock, editText)
                    val listOfInputItems = editText.replace("\\s".toRegex(), "").split("=")
                    var itsOkay = true

                    if (listOfInputItems.size == 2) {
                        val listOfVar = listOfInputItems[1].replace("\\s".toRegex(), "").split(",")
                        if (newBlock.isCorrect(listOfInputItems[0]) && newBlock.isArraySequence(listOfInputItems[1])) {
                            if (listOfInputItems[0] in arraysMap.keys && listOfVar.size != arraysMap.getValue(listOfInputItems[0]).size) { //не совпал размер
                                Toast.makeText(
                                    applicationContext,
                                    "Размер массива изменить нельзя",
                                    Toast.LENGTH_LONG
                                ).show()
                                itsOkay = false
                            } else if (listOfInputItems[0] in variablesMap.keys) { //имя уже занято
                                Toast.makeText(
                                    applicationContext,
                                    "Имя уже занято",
                                    Toast.LENGTH_LONG
                                ).show()
                                itsOkay = false
                            }
                        } else { //некорретный ввод
                            Toast.makeText(
                                applicationContext,
                                "Некорректный ввод",
                                Toast.LENGTH_SHORT
                            ).show()
                            itsOkay = false
                        }
                        if (itsOkay) {
                            newBlock.arrayElement = listOfVar
                            dragDropAdapter.updateItem(newBlock)
                            arraysMap.put(listOfInputItems[0], listOfVar)

                            Toast.makeText(
                                applicationContext,
                                "Блок успешно добавлен!",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            applicationContext,
                            "Один из аргументов не указан",
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

        elsee.setOnClickListener {
            Log.d("Main", "Button pressed")
            val builder = AlertDialog.Builder(this)
            val inflater = layoutInflater
            val dialogLayout = inflater.inflate(R.layout.edit_text_layout, null)
            val BlockName = dialogLayout.findViewById<TextView>(R.id.BlockName)
            with(builder) {
                setTitle("Я блок Иначе")
                BlockName.text = "Иначе Начало"

                setPositiveButton("OK") { dialog, which ->
                    Log.i("Button", "Pressed OK")
                    val editText =
                        dialogLayout.findViewById<EditText>(R.id.et_editText).text.toString() // строка - код блока
                    val nameBlock = BlockName.text.toString() // тип блока
                    var newBlock = BlockModule(nameBlock, editText)

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

        ifEnd.setOnClickListener {
            Log.d("Main", "Button pressed")
            val builder = AlertDialog.Builder(this)
            val inflater = layoutInflater
            val dialogLayout = inflater.inflate(R.layout.edit_text_layout, null)
            val BlockName = dialogLayout.findViewById<TextView>(R.id.BlockName)
            with(builder) {
                setTitle("Я второй блок Условия")
                BlockName.text = "Условие Конец"

                setPositiveButton("OK") { dialog, which ->
                    Log.i("Button", "Pressed OK")
                    val editText =
                        dialogLayout.findViewById<EditText>(R.id.et_editText)
                    val nameBlock = BlockName.text.toString() // тип блока
                    var newBlock = BlockModule(nameBlock, editText.text.toString())
                    dragDropAdapter.updateItem(newBlock)

                    Toast.makeText(
                        applicationContext,
                        "Блок успешно добавлен!",
                        Toast.LENGTH_LONG
                    ).show()
                }
                setNegativeButton("Отмена") { dialog, which ->
                    Log.d("Main", "Negative button clicked")
                }
                setView(dialogLayout)
                show()
            }
        }

        elseEnd.setOnClickListener {
            Log.d("Main", "Button pressed")
            val builder = AlertDialog.Builder(this)
            val inflater = layoutInflater
            val dialogLayout = inflater.inflate(R.layout.edit_text_layout, null)
            val BlockName = dialogLayout.findViewById<TextView>(R.id.BlockName)
            with(builder) {
                setTitle("Я второй блок Иначе")
                BlockName.text = "Иначе Конец"

                setPositiveButton("OK") { dialog, which ->
                    Log.i("Button", "Pressed OK")
                    val editText =
                        dialogLayout.findViewById<EditText>(R.id.et_editText)
                    val nameBlock = BlockName.text.toString() // тип блока
                    var newBlock = BlockModule(nameBlock, editText.text.toString())
                    dragDropAdapter.updateItem(newBlock)

                    Toast.makeText(
                        applicationContext,
                        "Блок успешно добавлен!",
                        Toast.LENGTH_LONG
                    ).show()
                }
                setNegativeButton("Отмена") { dialog, which ->
                    Log.d("Main", "Negative button clicked")
                }
                setView(dialogLayout)
                show()
            }
        }

        whileEnd.setOnClickListener {
            Log.d("Main", "Button pressed")
            val builder = AlertDialog.Builder(this)
            val inflater = layoutInflater
            val dialogLayout = inflater.inflate(R.layout.edit_text_layout, null)
            val BlockName = dialogLayout.findViewById<TextView>(R.id.BlockName)
            with(builder) {
                setTitle("Я второй блок Цикла")
                BlockName.text = "Пока Конец"

                setPositiveButton("OK") { dialog, which ->
                    Log.i("Button", "Pressed OK")
                    val editText =
                        dialogLayout.findViewById<EditText>(R.id.et_editText)
                    val nameBlock = BlockName.text.toString() // тип блока
                    var newBlock = BlockModule(nameBlock, editText.text.toString())
                    dragDropAdapter.updateItem(newBlock)

                    Toast.makeText(
                        applicationContext,
                        "Блок успешно добавлен!",
                        Toast.LENGTH_LONG
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


    private fun onAddButtonClicked() {
        setVisibility(clicked)
        setAnimation(clicked)
        setClickable(clicked)
        clicked = !clicked
    }

    private fun setVisibility(clicked: Boolean) = if (!clicked) {
        varr.visibility = View.VISIBLE
        assign.visibility = View.VISIBLE
        outputt.visibility = View.VISIBLE
        whilee.visibility = View.VISIBLE
        arr.visibility = View.VISIBLE
        burger.visibility = View.VISIBLE
        iff.visibility = View.VISIBLE
        elsee.visibility = View.VISIBLE
        play.visibility = View.VISIBLE
        whileEnd.visibility = View.VISIBLE
        elseEnd.visibility = View.VISIBLE
        ifEnd.visibility = View.VISIBLE
        Letstart.visibility = View.INVISIBLE


    } else {
        varr.visibility = View.INVISIBLE
        assign.visibility = View.INVISIBLE
        outputt.visibility = View.INVISIBLE
        whilee.visibility = View.INVISIBLE
        arr.visibility = View.INVISIBLE
        Letstart.visibility = View.VISIBLE
        iff.visibility = View.INVISIBLE
        elsee.visibility = View.INVISIBLE
        whileEnd.visibility = View.INVISIBLE
        elseEnd.visibility = View.INVISIBLE
        ifEnd.visibility = View.INVISIBLE
        play.visibility = View.INVISIBLE


    }

    private fun setAnimation(clicked: Boolean) {
        if (!clicked) {
            varr.startAnimation(fromBottom)
            assign.startAnimation(fromBottom)
            outputt.startAnimation(fromBottom)
            whilee.startAnimation(fromBottom)
            arr.startAnimation(fromBottom)
            burger.startAnimation(fromBottom)
            iff.startAnimation(fromBottom)
            elsee.startAnimation(fromBottom)
            play.startAnimation(fromBottom)
            whileEnd.startAnimation(fromBottom)
            elseEnd.startAnimation(fromBottom)
            ifEnd.startAnimation(fromBottom)


        } else {
            varr.startAnimation(toBottom)
            assign.startAnimation(toBottom)
            outputt.startAnimation(toBottom)
            whilee.startAnimation(toBottom)
            arr.startAnimation(toBottom)
            burger.startAnimation(toBottom)
            iff.startAnimation(toBottom)
            elsee.startAnimation(toBottom)
            play.startAnimation(toBottom)
            whileEnd.startAnimation(toBottom)
            elseEnd.startAnimation(toBottom)
            ifEnd.startAnimation(toBottom)
            burger.startAnimation(rotateClose)
        }
    }

    private fun setClickable(clicked: Boolean) {
        if (!clicked) {
            varr.isClickable = true
            assign.isClickable = true
            outputt.isClickable = true
            whilee.isClickable = true
            arr.isClickable = true
            iff.isClickable = true
            elsee.isClickable = true
            play.isClickable = true
            whileEnd.isClickable = true
            elseEnd.isClickable = true
            ifEnd.isClickable = true
        } else {
            varr.isClickable = false
            assign.isClickable = false
            outputt.isClickable = false
            whilee.isClickable = false
            arr.isClickable = false
            iff.isClickable = false
            elsee.isClickable = false
            play.isClickable = false
            whileEnd.isClickable = false
            elseEnd.isClickable = false
            ifEnd.isClickable = false
        }
    }


    private fun outPut(str: String) {
        val builder1 = AlertDialog.Builder(this)
        val inflater1 = layoutInflater
        val dialogLayout1 = inflater1.inflate(R.layout.output_text_layout, null)

        with(builder1) {
            setTitle("Вывод переменных")
            if (true) { // тут проверка всё ли корректно
                //Тут код для вывода
                dialogLayout1.textView.text = str
            } else {
                dialogLayout1.textView.text = "Ошибка! Что-то пошло не так..."
            }
            setView(dialogLayout1)
            show()
        }
    }

    private fun startCode() { // выполнение алгоритма
        play.setOnClickListener {
            itemsList = dragDropAdapter.getArray()
            var i = 0
            var flagElse = false

            while (i < itemsList.size) {
                val nameBlock = itemsList[i].name
                var etValue = itemsList[i].editTextValue
                Log.i("List", "$nameBlock : $etValue")


                when (nameBlock) {
                    "Объявление переменных" -> {
                        i += 1
                    }

                    "Присваивание" -> {
                        val result = BlocksFuns(variablesMap).assingment(etValue)
                        Log.i("Result", "$result")
                        if (result.first != -1) {
                            variablesMap[result.second] = result.first
                            Log.i(
                                "Assingment",
                                "Переменной ${result.second} присвоено значение ${result.first}"
                            )
                        }
                        i += 1
                    }

                    "Условие Начало" -> {
                        etValue = etValue.replace("\\s".toRegex(), "")
                        val newStr = etValue.replace("\\s".toRegex(), "")
                            .split(">", "<", "<=", ">=", "==", "!=")
                        val leftPart = newStr[0]
                        val rightPart = newStr[1]
                        var delimiter =
                            etValue.slice(leftPart.length..etValue.indexOf(rightPart[0]) - 1)


                        var leftExp = RPN().rpnToAnswer(
                            RPN().expressionToRPN(
                                RPN().preparingExpression(leftPart)
                            ), variablesMap
                        )
                        val rightExp = RPN().rpnToAnswer(
                            RPN().expressionToRPN(
                                RPN().preparingExpression(rightPart)
                            ), variablesMap
                        )

                        var j = 0
                        when (delimiter) {
                            ">" -> {
                                if (leftExp > rightExp) {
                                    j = i + 1
                                    while (itemsList[j].name != "Условие Конец" && j < itemsList.size) {
                                        val nameBlock = itemsList[j].name
                                        var etValue = itemsList[j].editTextValue
                                        when (nameBlock) {
                                            "Объявление переменных" -> {
                                                j += 1
                                            }
                                            "Присваивание" -> {
                                                val result =
                                                    BlocksFuns(variablesMap).assingment(etValue)
                                                if (result.first != -1) {
                                                    variablesMap[result.second] = result.first
                                                    Log.i(
                                                        "Assingment",
                                                        "Переменной ${result.second} присвоено значение ${result.first}"
                                                    )
                                                } else {
                                                    Toast.makeText(
                                                        applicationContext,
                                                        "Некорректное выражение!",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                                j += 1
                                            }
                                            "Вывод переменных" -> {
                                                outPut(BlocksFuns(variablesMap).output(etValue))
                                                j += 1
                                            }
                                        }
                                    }
                                } else while (itemsList[j].name != "Условие Конец") {
                                    j += 1
                                    flagElse = true
                                }
                            }

                            "<" -> {
                                if (leftExp < rightExp) {
                                    j = i + 1
                                    while (itemsList[j].name != "Условие Конец" && j < itemsList.size) {
                                        val nameBlock = itemsList[j].name
                                        var etValue = itemsList[j].editTextValue
                                        when (nameBlock) {
                                            "Объявление переменных" -> {
                                                j += 1
                                            }
                                            "Присваивание" -> {
                                                val result =
                                                    BlocksFuns(variablesMap).assingment(etValue)
                                                if (result.first != -1) {
                                                    variablesMap[result.second] = result.first
                                                    Log.i(
                                                        "Assingment",
                                                        "Переменной ${result.second} присвоено значение ${result.first}"
                                                    )
                                                } else {
                                                    Toast.makeText(
                                                        applicationContext,
                                                        "Некорректное выражение!",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                                j += 1
                                            }
                                            "Вывод переменных" -> {
                                                outPut(BlocksFuns(variablesMap).output(etValue))
                                                j += 1
                                            }
                                            "Имя и элементы массива" -> {
                                                j += 1
                                            }
                                        }
                                    }
                                } else while (itemsList[j].name != "Условие Конец") {
                                    j += 1
                                    flagElse = true
                                }
                            }
                            ">=" -> {
                                if (leftExp >= rightExp) {
                                    j = i + 1
                                    while (itemsList[j].name != "Условие Конец" && j < itemsList.size) {
                                        val nameBlock = itemsList[j].name
                                        var etValue = itemsList[j].editTextValue
                                        when (nameBlock) {
                                            "Объявление переменных" -> {
                                                j += 1
                                            }
                                            "Присваивание" -> {
                                                val result =
                                                    BlocksFuns(variablesMap).assingment(etValue)
                                                if (result.first != -1) {
                                                    variablesMap[result.second] = result.first
                                                    Log.i(
                                                        "Assingment",
                                                        "Переменной ${result.second} присвоено значение ${result.first}"
                                                    )
                                                } else {
                                                    Toast.makeText(
                                                        applicationContext,
                                                        "Некорректное выражение!",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                                j += 1
                                            }
                                            "Вывод переменных" -> {
                                                outPut(BlocksFuns(variablesMap).output(etValue))
                                                j += 1
                                            }
                                            "Имя и элементы массива" -> {
                                                j += 1
                                            }
                                        }
                                    }
                                } else while (itemsList[j].name != "Условие Конец") {
                                    j += 1
                                    flagElse = true
                                }
                            }
                            "<=" -> {
                                if (leftExp <= rightExp) {
                                    j = i + 1
                                    while (itemsList[j].name != "Условие Конец" && j < itemsList.size) {
                                        val nameBlock = itemsList[j].name
                                        var etValue = itemsList[j].editTextValue
                                        when (nameBlock) {
                                            "Объявление переменных" -> {
                                                j += 1
                                            }
                                            "Присваивание" -> {
                                                val result =
                                                    BlocksFuns(variablesMap).assingment(etValue)
                                                if (result.first != -1) {
                                                    variablesMap[result.second] = result.first
                                                    Log.i(
                                                        "Assingment",
                                                        "Переменной ${result.second} присвоено значение ${result.first}"
                                                    )
                                                } else {
                                                    Toast.makeText(
                                                        applicationContext,
                                                        "Некорректное выражение!",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                                j += 1
                                            }
                                            "Вывод переменных" -> {
                                                outPut(BlocksFuns(variablesMap).output(etValue))
                                                j += 1
                                            }
                                            "Имя и элементы массива" -> {
                                                j += 1
                                            }
                                        }
                                    }
                                } else while (itemsList[j].name != "Условие Конец") {
                                    j += 1
                                    flagElse = true
                                }
                            }
                            "!=" -> {
                                if (leftExp != rightExp) {
                                    j = i + 1
                                    while (itemsList[j].name != "Условие Конец" && j < itemsList.size) {
                                        val nameBlock = itemsList[j].name
                                        var etValue = itemsList[j].editTextValue
                                        when (nameBlock) {
                                            "Объявление переменных" -> {
                                                j += 1
                                            }
                                            "Присваивание" -> {
                                                val result =
                                                    BlocksFuns(variablesMap).assingment(etValue)
                                                if (result.first != -1) {
                                                    variablesMap[result.second] = result.first
                                                    Log.i(
                                                        "Assingment",
                                                        "Переменной ${result.second} присвоено значение ${result.first}"
                                                    )
                                                } else {
                                                    Toast.makeText(
                                                        applicationContext,
                                                        "Некорректное выражение!",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                                j += 1
                                            }
                                            "Вывод переменных" -> {
                                                outPut(BlocksFuns(variablesMap).output(etValue))
                                                j += 1
                                            }
                                            "Имя и элементы массива" -> {
                                                j += 1
                                            }
                                        }
                                    }
                                } else while (itemsList[j].name != "Условие Конец") {
                                    j += 1
                                    flagElse = true
                                }
                            }
                            "==" -> {
                                if (leftExp == rightExp) {
                                    j = i + 1
                                    while (itemsList[j].name != "Условие Конец" && j < itemsList.size) {
                                        val nameBlock = itemsList[j].name
                                        var etValue = itemsList[j].editTextValue
                                        when (nameBlock) {
                                            "Объявление переменных" -> {
                                                j += 1
                                            }
                                            "Присваивание" -> {
                                                val result =
                                                    BlocksFuns(variablesMap).assingment(etValue)
                                                if (result.first != -1) {
                                                    variablesMap[result.second] = result.first
                                                    Log.i(
                                                        "Assingment",
                                                        "Переменной ${result.second} присвоено значение ${result.first}"
                                                    )
                                                } else {
                                                    Toast.makeText(
                                                        applicationContext,
                                                        "Некорректное выражение!",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                                j += 1
                                            }
                                            "Вывод переменных" -> {
                                                outPut(BlocksFuns(variablesMap).output(etValue))
                                                j += 1
                                            }
                                            "Имя и элементы массива" -> {
                                                j += 1
                                            }
                                        }
                                    }
                                } else while (itemsList[j].name != "Условие Конец") {
                                    j += 1
                                    flagElse = true
                                }
                            }
                        }
                        i = j
                    }

                    "Условие Конец" -> {
                        i += 1
                    }

                    "Иначе Начало" -> {
                        var j = 0
                        if (flagElse) {
                            j = i + 1
                            while (itemsList[j].name != "Иначе Конец" && j < itemsList.size) {
                                val nameBlock = itemsList[j].name
                                var etValue = itemsList[j].editTextValue
                                when (nameBlock) {
                                    "Объявление переменных" -> {
                                        j += 1
                                    }
                                    "Присваивание" -> {
                                        val result = BlocksFuns(variablesMap).assingment(etValue)
                                        if (result.first != -1) {
                                            variablesMap[result.second] = result.first
                                            Log.i(
                                                "Assingment",
                                                "Переменной ${result.second} присвоено значение ${result.first}"
                                            )
                                        } else {
                                            Toast.makeText(
                                                applicationContext,
                                                "Некорректное выражение!",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                        j += 1
                                    }
                                    "Вывод переменных" -> {
                                        outPut(BlocksFuns(variablesMap).output(etValue))
                                        j += 1
                                    }
                                    "Имя и элементы массива" -> {
                                        j += 1
                                    }
                                }
                            }
                        }
                        i = j
                    }

                    "Иначе Конец" -> {
                        i += 1
                    }

                    "Пока Начало" -> {
                        etValue = etValue.replace("\\s".toRegex(), "")
                        val newStr = etValue.replace("\\s".toRegex(), "")
                            .split(">", "<", "<=", ">=", "==", "!=")
                        val leftPart = newStr[0]
                        val rightPart = newStr[1]

                        var delimiter =
                            etValue.slice(leftPart.length..etValue.indexOf(rightPart[0]) - 1)

                        var leftExp = RPN().rpnToAnswer(
                            RPN().expressionToRPN(
                                RPN().preparingExpression(leftPart)
                            ), variablesMap
                        )
                        var rightExp = RPN().rpnToAnswer(
                            RPN().expressionToRPN(
                                RPN().preparingExpression(rightPart)
                            ), variablesMap
                        )
                        var j = 0
                        when (delimiter) {
                            ">" -> {
                                while (leftExp > rightExp) {
                                    j = i + 1

                                    while (itemsList[j].name != "Пока Конец" && j < itemsList.size) {
                                        val nameBlock = itemsList[j].name
                                        var etValue = itemsList[j].editTextValue
                                        when (nameBlock) {
                                            "Объявление переменных" -> {
                                                j += 1
                                            }
                                            "Присваивание" -> {
                                                val result =
                                                    BlocksFuns(variablesMap).assingment(etValue)
                                                if (result.first != -1) {
                                                    variablesMap[result.second] = result.first
                                                    Log.i(
                                                        "Assingment",
                                                        "Переменной ${result.second} присвоено значение ${result.first}"
                                                    )
                                                } else {
                                                    Toast.makeText(
                                                        applicationContext,
                                                        "Некорректное выражение!",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                                j += 1
                                            }
                                            "Вывод переменных" -> {
                                                outPut(BlocksFuns(variablesMap).output(etValue))
                                                j += 1
                                            }
                                            "Имя и элементы массива" -> {
                                                j += 1
                                            }
                                        }

                                    }
                                    leftExp = RPN().rpnToAnswer(
                                        RPN().expressionToRPN(
                                            RPN().preparingExpression(leftPart)
                                        ), variablesMap
                                    )
                                    rightExp = RPN().rpnToAnswer(
                                        RPN().expressionToRPN(
                                            RPN().preparingExpression(rightPart)
                                        ), variablesMap
                                    )
                                }
                            }
                            "<" -> {
                                while (leftExp < rightExp) {
                                    j = i + 1

                                    while (itemsList[j].name != "Пока Конец" && j < itemsList.size) {
                                        val nameBlock = itemsList[j].name
                                        var etValue = itemsList[j].editTextValue
                                        when (nameBlock) {
                                            "Объявление переменных" -> {
                                                j += 1
                                            }
                                            "Присваивание" -> {
                                                val result =
                                                    BlocksFuns(variablesMap).assingment(etValue)
                                                if (result.first != -1) {
                                                    variablesMap[result.second] = result.first
                                                    Log.i(
                                                        "Assingment",
                                                        "Переменной ${result.second} присвоено значение ${result.first}"
                                                    )
                                                } else {
                                                    Toast.makeText(
                                                        applicationContext,
                                                        "Некорректное выражение!",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                                j += 1
                                            }
                                            "Вывод переменных" -> {
                                                outPut(BlocksFuns(variablesMap).output(etValue))
                                                j += 1
                                            }
                                            "Имя и элементы массива" -> {
                                                j += 1
                                            }
                                        }

                                    }
                                    leftExp = RPN().rpnToAnswer(
                                        RPN().expressionToRPN(
                                            RPN().preparingExpression(leftPart)
                                        ), variablesMap
                                    )
                                    rightExp = RPN().rpnToAnswer(
                                        RPN().expressionToRPN(
                                            RPN().preparingExpression(rightPart)
                                        ), variablesMap
                                    )
                                }
                            }
                            ">=" -> {
                                while (leftExp >= rightExp) {
                                    j = i + 1

                                    while (itemsList[j].name != "Пока Конец" && j < itemsList.size) {
                                        val nameBlock = itemsList[j].name
                                        var etValue = itemsList[j].editTextValue
                                        when (nameBlock) {
                                            "Объявление переменных" -> {
                                                j += 1
                                            }
                                            "Присваивание" -> {
                                                val result =
                                                    BlocksFuns(variablesMap).assingment(etValue)
                                                if (result.first != -1) {
                                                    variablesMap[result.second] = result.first
                                                    Log.i(
                                                        "Assingment",
                                                        "Переменной ${result.second} присвоено значение ${result.first}"
                                                    )
                                                } else {
                                                    Toast.makeText(
                                                        applicationContext,
                                                        "Некорректное выражение!",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                                j += 1
                                            }
                                            "Вывод переменных" -> {
                                                outPut(BlocksFuns(variablesMap).output(etValue))
                                                j += 1
                                            }
                                            "Имя и элементы массива" -> {
                                                j += 1
                                            }
                                        }

                                    }
                                    leftExp = RPN().rpnToAnswer(
                                        RPN().expressionToRPN(
                                            RPN().preparingExpression(leftPart)
                                        ), variablesMap
                                    )
                                    rightExp = RPN().rpnToAnswer(
                                        RPN().expressionToRPN(
                                            RPN().preparingExpression(rightPart)
                                        ), variablesMap
                                    )
                                }
                            }
                            "<=" -> {
                                while (leftExp <= rightExp) {
                                    j = i + 1

                                    while (itemsList[j].name != "Пока Конец" && j < itemsList.size) {
                                        val nameBlock = itemsList[j].name
                                        var etValue = itemsList[j].editTextValue
                                        when (nameBlock) {
                                            "Объявление переменных" -> {
                                                j += 1
                                            }
                                            "Присваивание" -> {
                                                val result =
                                                    BlocksFuns(variablesMap).assingment(etValue)
                                                if (result.first != -1) {
                                                    variablesMap[result.second] = result.first
                                                    Log.i(
                                                        "Assingment",
                                                        "Переменной ${result.second} присвоено значение ${result.first}"
                                                    )
                                                } else {
                                                    Toast.makeText(
                                                        applicationContext,
                                                        "Некорректное выражение!",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                                j += 1
                                            }
                                            "Вывод переменных" -> {
                                                outPut(BlocksFuns(variablesMap).output(etValue))
                                                j += 1
                                            }
                                            "Имя и элементы массива" -> {
                                                j += 1
                                            }
                                        }

                                    }
                                    leftExp = RPN().rpnToAnswer(
                                        RPN().expressionToRPN(
                                            RPN().preparingExpression(leftPart)
                                        ), variablesMap
                                    )
                                    rightExp = RPN().rpnToAnswer(
                                        RPN().expressionToRPN(
                                            RPN().preparingExpression(rightPart)
                                        ), variablesMap
                                    )
                                }
                            }
                            "==" -> {
                                while (leftExp == rightExp) {
                                    j = i + 1

                                    while (itemsList[j].name != "Пока Конец" && j < itemsList.size) {
                                        val nameBlock = itemsList[j].name
                                        var etValue = itemsList[j].editTextValue
                                        when (nameBlock) {
                                            "Объявление переменных" -> {
                                                j += 1
                                            }
                                            "Присваивание" -> {
                                                val result =
                                                    BlocksFuns(variablesMap).assingment(etValue)
                                                if (result.first != -1) {
                                                    variablesMap[result.second] = result.first
                                                    Log.i(
                                                        "Assingment",
                                                        "Переменной ${result.second} присвоено значение ${result.first}"
                                                    )
                                                } else {
                                                    Toast.makeText(
                                                        applicationContext,
                                                        "Некорректное выражение!",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                                j += 1
                                            }
                                            "Вывод переменных" -> {
                                                outPut(BlocksFuns(variablesMap).output(etValue))
                                                j += 1
                                            }
                                            "Имя и элементы массива" -> {
                                                j += 1
                                            }
                                        }

                                    }
                                    leftExp = RPN().rpnToAnswer(
                                        RPN().expressionToRPN(
                                            RPN().preparingExpression(leftPart)
                                        ), variablesMap
                                    )
                                    rightExp = RPN().rpnToAnswer(
                                        RPN().expressionToRPN(
                                            RPN().preparingExpression(rightPart)
                                        ), variablesMap
                                    )
                                }
                            }
                            "!=" -> {
                                while (leftExp != rightExp) {
                                    j = i + 1

                                    while (itemsList[j].name != "Пока Конец" && j < itemsList.size) {
                                        val nameBlock = itemsList[j].name
                                        var etValue = itemsList[j].editTextValue
                                        when (nameBlock) {
                                            "Объявление переменных" -> {
                                                j += 1
                                            }
                                            "Присваивание" -> {
                                                val result =
                                                    BlocksFuns(variablesMap).assingment(etValue)
                                                if (result.first != -1) {
                                                    variablesMap[result.second] = result.first
                                                    Log.i(
                                                        "Assingment",
                                                        "Переменной ${result.second} присвоено значение ${result.first}"
                                                    )
                                                } else {
                                                    Toast.makeText(
                                                        applicationContext,
                                                        "Некорректное выражение!",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                                j += 1
                                            }
                                            "Вывод переменных" -> {
                                                outPut(BlocksFuns(variablesMap).output(etValue))
                                                j += 1
                                            }
                                            "Имя и элементы массива" -> {
                                                j += 1
                                            }
                                        }

                                    }
                                    leftExp = RPN().rpnToAnswer(
                                        RPN().expressionToRPN(
                                            RPN().preparingExpression(leftPart)
                                        ), variablesMap
                                    )
                                    rightExp = RPN().rpnToAnswer(
                                        RPN().expressionToRPN(
                                            RPN().preparingExpression(rightPart)
                                        ), variablesMap
                                    )
                                }
                            }
                        }
                        i = j
                    }

                    "Пока Конец" -> {
                        i += 1
                    }

                    "Имя и элементы массива" -> {
                        i += 1
                    }

                    "Вывод переменных" -> {
                        outPut(BlocksFuns(variablesMap).output(etValue)); i += 1
                    }
                }
            }
        }
    }


    private fun setUpRecycler() {
        dragDropAdapter = DragDropAdapter(itemsList)
        val mList: DragDropSwipeRecyclerView = findViewById(R.id.list)
        mList.layoutManager = LinearLayoutManager(this)
        mList.adapter = dragDropAdapter

        mList.orientation =
            DragDropSwipeRecyclerView.ListOrientation.VERTICAL_LIST_WITH_VERTICAL_DRAGGING
        mList.reduceItemAlphaOnSwiping = true

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
                            if (BlockModule(
                                    nameBlock,
                                    editText
                                ).isCorrect(editText)
                            ) { // одна переменная
                                variablesMap.remove(editText, 0)
                                Toast.makeText(
                                    applicationContext,
                                    "Если эта переменная используется далее и все сломается, мы ни при чем:)",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else { // несколько переменных
                                val newStr = editText.replace("\\s".toRegex(), "").split(",")
                                for (name in newStr) {
                                    variablesMap.remove(name, 0)
                                }
                                Toast.makeText(
                                    applicationContext,
                                    "Если эти переменные используются далее и все сломается, мы ни при чем:)",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else if (nameBlock == "Присваивание") {
                            Log.i("AssDec", "${variablesAddAssingment.toString()}")
                            val nameVar = editText.substringBefore('=')
                            if (nameVar in variablesAddAssingment) { // если переменную объявили и присвоили в блоке Присваивание
                                variablesMap.remove(nameVar)
                                variablesAddAssingment.remove(nameVar)

                                Toast.makeText(
                                    applicationContext,
                                    "За дальнейшие ошибки ответственности не несем:)",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else { // удаляем присваивание, переменная просто объявлена
                                variablesMap[nameVar] = 0
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
                        val nameBlock = item.name
                        val editText = item.editTextValue
                        if (nameBlock == "Объявление переменных") {
                            if (BlockModule(
                                    nameBlock,
                                    editText
                                ).isCorrect(editText)
                            ) { // одна переменная
                                variablesMap.remove(editText, 0)
                                Toast.makeText(
                                    applicationContext,
                                    "Если эта переменная используется далее и все сломается, мы ни при чем:)",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else { // несколько переменных
                                val newStr = editText.replace("\\s".toRegex(), "").split(",")
                                for (name in newStr) {
                                    variablesMap.remove(name, 0)
                                }
                                Toast.makeText(
                                    applicationContext,
                                    "Если эти переменные используются далее и все сломается, мы ни при чем:)",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else if (nameBlock == "Присваивание") {
                            Log.i("AssDec", "${variablesAddAssingment.toString()}")
                            val nameVar = editText.substringBefore('=')
                            if (nameVar in variablesAddAssingment) { // если переменную объявили и присвоили в блоке Присваивание
                                variablesMap.remove(nameVar)
                                variablesAddAssingment.remove(nameVar)

                                Toast.makeText(
                                    applicationContext,
                                    "За дальнейшие ошибки ответственности не несем:)",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else { // удаляем присваивание, переменная просто объявлена
                                variablesMap[nameVar] = 0
                            }
                        }
                    }
                    else -> return false
                }
                return false
            }
        }
        mList.swipeListener = onItemSwipeListener

        // button
        startCode()
    }
}