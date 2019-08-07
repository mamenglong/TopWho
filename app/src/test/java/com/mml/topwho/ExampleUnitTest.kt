package com.mml.topwho

import org.junit.Test

import org.junit.Assert.*
import kotlin.math.ceil

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }
    @Test
    fun math(){
        val page=0
        val size=10.0
        val ss=101/size
        val s= ceil(ss)
    }
}
