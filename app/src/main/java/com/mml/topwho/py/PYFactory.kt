package com.mml.topwho.py

import com.github.promeg.pinyinhelper.Pinyin
import com.github.promeg.pinyinhelper.PinyinMapDict
import com.github.promeg.tinypinyin.lexicons.java.cncity.CnCityDict
import com.mml.topwho.TopWhoApplication

object PYFactory {
    const val DEF_CHAR = '#'

    init {
        // 添加中文城市词典
        Pinyin.init(Pinyin.newConfig().with(CnCityDict.getInstance()))

        Pinyin.init(
            Pinyin.newConfig()
                .with(object : PinyinMapDict() {
                    override fun mapping(): MutableMap<String, Array<String>> {
                        val map = HashMap<String, Array<String>>()
                        map["重庆"] = arrayOf("CHONG", "QING");
                        map["仇"] = arrayOf("QIU")
                        map["柏"] = arrayOf("BO")
                        map["牟"] = arrayOf("MU")
                        map["颉"] = arrayOf("XIE")
                        map["解"] = arrayOf("XIE")
                        map["尉"] = arrayOf("YU")
                        map["奇"] = arrayOf("JI")
                        map["单"] = arrayOf("SHAN")
                        map["谌"] = arrayOf("SHEN")
                        map["乐"] = arrayOf("YUE")
                        map["召"] = arrayOf("SHAO")
                        map["朴"] = arrayOf("PIAO")
                        map["区"] = arrayOf("OU")
                        map["查"] = arrayOf("ZHA")
                        map["曾"] = arrayOf("ZENG")
                        map["缪"] = arrayOf("MIAO")
                        return map
                    }
                })
        )
    }

    fun <T : PY> createPinyinList(list: List<T>) {
        list.forEach {
            createPinyin(it)
        }
    }

    fun <T : PY> createPinyin(data: T) {
        val chinese = data.zh()
        val charArray = chinese.toCharArray()
        var firstChar = '#'
        var firstCharArray = CharArray(charArray.size)
        val stringBuilder = StringBuilder()
        charArray.forEachIndexed { index, char ->
            val pinyin = charToPinyin(char)
            if (index == 0) {
                val result = getFirstChar(pinyin)
                if (result.toString().matches(Regex("[a-zA-Z]+"))) {
                    firstChar = result
                }
            }
            firstCharArray[index] = getFirstChar(pinyin)
            stringBuilder.append(pinyin)
        }
        data.firstChar = firstChar
        data.firstChars = firstCharArray.toString()
        data.pinyins = stringBuilder.toString()
        data.pinyinsTotalLength = data.pinyins.length
    }


    /**
     *
     * @param c
     * @return
     */
    private fun charToPinyin(c: Char): String {
        var pinyin = Pinyin.toPinyin(c)
        return pinyin
    }

    /**
     * 拼音首个字母
     * @param pinyins
     * @return
     */
    private fun getFirstChar(pinyins: String): Char {
        if (pinyins.isNotEmpty()) {
            val firstPinying = pinyins[0]
            return charToUpperCase(firstPinying)
        }
        return DEF_CHAR
    }

    /**
     * 字符转大写
     * @param c
     * @return
     */
    private fun charToUpperCase(c: Char): Char {
        var c = c
        if (c in 'a'..'z') {
            c -= 32;
        }
        return c
    }
}