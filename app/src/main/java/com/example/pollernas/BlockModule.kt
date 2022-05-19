package com.example.pollernas

import android.widget.Toast
import com.example.pollernas.MainActivity

class BlockModule(var name: String, var editTextValue: String){

    fun isCorrect(str: String) : Boolean { // одна переменная
        val result = Regex("[А-Яа-яA-Za-z_][А-Яа-яA-Za-z_0-9]*").matchEntire(str)
        return result != null
    }

    fun isSequence(str: String) : Boolean { // последовательность ч/з запятую
        val result = Regex("[А-Яа-яA-Za-z_0-9][А-Яа-яA-Za-z_0-9]*\\s*,{1}\\s*[А-Яа-яA-Za-z_0-9][А-Яа-яA-Za-z_0-9]*\\s*(?:,{1}\\s*[А-Яа-яA-Za-z_0-9][А-Яа-яA-Za-z_0-9]*\\s*)*").matchEntire(str)
        return result != null
    }

    fun isCorrectAssingment(str: String) : Boolean {
        val result = Regex("[А-Яа-яA-Za-z_][А-Яа-яA-Za-z_0-9]*\\s*=\\s*([А-Яа-яA-Za-z_][А-Яа-яA-Za-z_0-9]*|[0-9]+)\\s*(?:\\s*[+\\-*\\/%]\\s*([А-Яа-яA-Za-z_][А-Яа-яA-Za-z_0-9]*|\\d+)\\s*)*\\s*").matchEntire(str)
        return result != null
    }

    fun isCorrectIf(str: String) : Boolean {
        val result = Regex("[А-Яа-яA-Za-z_][А-Яа-яA-Za-z_0-9]*(?:\\s*[+\\-*\\/%]\\s*([А-Яа-яA-Za-z_][А-Яа-яA-Za-z_0-9]*|\\d+)\\s*)*\\s*(>|<|<=|>=|!=|==)\\s*([А-Яа-яA-Za-z_][А-Яа-яA-Za-z_0-9]*|[1-9]\\d*|0)\\s*").matchEntire(str)
        return result != null
    }
}