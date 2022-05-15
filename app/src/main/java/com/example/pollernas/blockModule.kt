package com.example.pollernas

import android.widget.Toast
import com.example.pollernas.MainActivity

class blockModule(var name: String, var editTextValue: String, var variablesMap: MutableMap<String, Int>){

    fun isCorrect(str: String): Boolean {
        val result = Regex("[А-Яа-яA-Za-z_][А-Яа-яA-Za-z_0-9]*").matchEntire(str)
        return result != null
    }

    fun isSequence(str: String): Boolean {
        val result = Regex("[А-Яа-яA-Za-z_0-9][А-Яа-яA-Za-z_0-9]*\\s*,{1}\\s*[А-Яа-яA-Za-z_0-9][А-Яа-яA-Za-z_0-9]*\\s*(?:,{1}\\s*[А-Яа-яA-Za-z_0-9][А-Яа-яA-Za-z_0-9]*\\s*)*").matchEntire(str)
        return result != null
    }

    private fun isCorrectAssingment(str: String): Boolean {
        val result = Regex("[А-Яа-яA-Za-z_][А-Яа-яA-Za-z_0-9]*\\s*=\\s*([А-Яа-яA-Za-z_][А-Яа-яA-Za-z_0-9]*|\\d+)\\s*(?:\\s*[+=*/]\\s*([А-Яа-яA-Za-z_][А-Яа-яA-Za-z_0-9]*|\\d+)\\s*)*\\s*").matchEntire(str)
        return result != null
    }

    fun assingment(str: String) {
        if(isCorrectAssingment(str)) {
            var newStr = str.replace("\\s".toRegex(), "").split("=")
            val nameVar = newStr[0]

            val valueVar = RPN().rpnToAnswer(RPN().expressionToRPN(RPN().preparingExpression(newStr[1])), variablesMap)
            if(valueVar == -1) {

            }

            variablesMap[nameVar] = valueVar
        }
    }

}