package com.example.pollernas

import java.util.*

class RPN {
    private fun isInMapVar(name: String, variablesMap: MutableMap<String, Int>): Boolean {
        return name in variablesMap.keys
    }

    fun preparingExpression(expression: String) : String {
        var preparedExpression = ""
        for(token in expression.indices) {
            if(expression[token] == '-') {
                if (token == 0)
                    preparedExpression += '0'
                else if(expression[token - 1] == '(')
                    preparedExpression += '0'
            }
            preparedExpression += expression[token]
        }
        return preparedExpression
    }

    fun getPriority(token: Char) : Int {
        return when (token) {
            '*', '/', '%' -> 3
            '+', '-' -> 2
            '(' -> 1
            ')' -> -1
            in 'a'..'z', in 'A' ..'Z', '_' -> -2 // переменные
            else -> 0 // цифры
        }
    }

    fun expressionToRPN(expression: String) : String {
        var current = String()
        val stack = Stack<Char>()
        var priority: Int
        for(token in expression.indices) {
            priority = getPriority(expression[token])
            if(priority == 0 || priority == -2)
                current += expression[token]

            if(priority == 1)
                stack.push(expression[token])

            if(priority > 1) {
                current += " "
                while(!stack.empty()) {
                    if(getPriority(stack.peek()) >= priority)
                        current += stack.pop()
                    else
                        break
                }
                stack.push(expression[token])
            }

            if(priority == -1) {
                current += " "
                while(getPriority(stack.peek()) != 1)
                    current += stack.pop()
                stack.pop()
            }
        }
        while(!stack.empty())
            current += stack.pop()
        return current
    }

    fun rpnToAnswer(rpn: String, variablesMap: MutableMap<String, Int>) : Int {
        var operand: String // для чисел, состоящих из более чем 1 символа / для переменных
        var stack = Stack<Int>()
        var token = 0
        while(token < rpn.length) {

            if(rpn[token] == ' ') {
                token++
                continue
            }

            if(getPriority(rpn[token]) == 0) { // если текущий символ - цифра
                operand = String()
                while(rpn[token] != ' ' && getPriority(rpn[token]) == 0) {
                    operand += rpn[token]
                    token++
                    if(token == rpn.length)
                        break
                }
                stack.push(operand.toInt())
                operand = String()
            }

            else if(getPriority(rpn[token]) == -2) { // если текущий символ - буква
                operand = String()
                while(rpn[token] != ' ' && getPriority(rpn[token]) == -2) {
                    operand += rpn[token]
                    token++
                    if(token == rpn.length)
                        break
                }
                // проверять, есть в словаре переменная или нет
                if(isInMapVar(operand, variablesMap)) {
                    val valueVar = variablesMap[operand]
                    stack.push(valueVar)
                } else {
                    return -1
                }
                operand = String()
            }

            else if(getPriority(rpn[token]) > 1) { // если текущий символ - оператор
                val a = stack.pop()
                val b = stack.pop()
                if(rpn[token] == '+')
                    stack.push(b + a)
                else if(rpn[token] == '-')
                    stack.push(b - a)
                else if(rpn[token] == '/')
                    stack.push(b / a)
                else if(rpn[token] == '*')
                    stack.push(b * a)
                else if(rpn[token] == '%')
                    stack.push(b % a)
                token++
            }
        }
        return stack.pop()
    }
}