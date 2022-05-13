package com.example.pollernas

import android.widget.Toast
import com.example.pollernas.MainActivity

class blockModule(var name: String, var editTextValue: String, var variablesMap: MutableMap<String, Int>){

    private fun isCorrect(str: String): Boolean {
        val result = Regex("[A-Za-z_]\\w*").matchEntire(str)
        return result != null
    }

    private fun isSequence(str: String): Boolean {
        val result = Regex("(([A-Za-z_]\\w*) *,* *)+").matchEntire(str)
        return result != null
    }

    fun declaration(variable: String) {
        if(isCorrect(variable)) {
            if (name in variablesMap.keys) {
                // Toast.makeText(applicationContext, "Переменная с таким именем ${name} уже объявлена", Toast.LENGTH_LONG).show()
            } else {
                variablesMap.put("$name", 0)
            }
        } else if(isSequence(variable)) {
            val listOfVar = variable.replace("\\s".toRegex(), "").split(",")
            for (name in listOfVar) {
                if (isCorrect(name)) {
                    if (name in variablesMap.keys) {
                        // Toast.makeText(applicationContext, "Переменная с таким именем ${name} уже объявлена", Toast.LENGTH_LONG).show()
                        break
                    } else {
                        variablesMap.put("$name", 0)
                    }
                } else {
                    // некорректно имя {name}
                    break
                }
            }
        } else {
            // предупреждение некорректная строка
        }
    }

    private fun isCorrectAssingment(str: String): Boolean {
        val result = Regex("[A-Za-z_]\\w*\\s*=\\s*([A-Za-z_]\\w*|\\d+)\\s*(?:\\s*[+=*/]\\s*([A-Za-z_]\\w*|\\d+)\\s*)*\\s*").matchEntire(str)
        return result != null
    }

    fun assingment(str: String) {
        if(isCorrectAssingment(str)) {
            var newStr = str.replace("\\s".toRegex(), "").split("=")
            val nameVar = newStr[0]
            val valueVar = RPN().rpnToAnswer(RPN().expressionToRPN(RPN().preparingExpression(newStr[1])), variablesMap)
            variablesMap[nameVar] = valueVar
        }
    }

}