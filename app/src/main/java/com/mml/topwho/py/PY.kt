package com.mml.topwho.py

interface PY {
    fun zh(): String

    /**
     * 对应首字首拼音字母
     */
    var firstChar: Char

    /**
     * 所有字符中的拼音首字母
     */
    var firstChars: String

    /**
     * 对应的所有字母拼音
     */
    var pinyins: String

    /**
     * 拼音总长度
     */
    var pinyinsTotalLength: Int
}
