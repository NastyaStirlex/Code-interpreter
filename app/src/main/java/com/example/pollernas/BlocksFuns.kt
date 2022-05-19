package com.example.pollernas

import android.util.Log

class BlocksFuns(var variablesMap: MutableMap<String, Int>) {
    fun assingment(editTextValue: String) : Pair<Int, String> {
        var newStr = editTextValue.replace("\\s".toRegex(), "").split("=")

        val nameVar = newStr[0]
        val valueVar = newStr[1]
        val newValueVar = RPN().rpnToAnswer(RPN().expressionToRPN(RPN().preparingExpression(valueVar)), variablesMap)
        return if(newValueVar == -1) {
            Pair(-1, "")
        } else {
            Pair(newValueVar, nameVar)
        }
    }

    fun ifStartMain(editTextValue: String) {
        val newStr = editTextValue.replace("\\s".toRegex(), "").split(">", "<", "<=", ">=", "==", "!=")
        val leftPart = newStr[0]
        val rightPart = newStr[1]
        val delimiter = editTextValue.slice(leftPart.length..editTextValue.indexOf(rightPart[0]))
       // if(a < b) {

        //}
    }

    // блок вывода
    fun output(editText: String) : String {
        val listOfVar = editText.replace("\\s".toRegex(), "").split(",")
        var outputString = ""
        listOfVar.forEach {
            outputString += "$it = ${variablesMap[it]}\n"
        }
        return outputString
    }


    fun search(list: MutableList<BlockModule>, podstr: String) {

        list.forEach {

        }

    }
}