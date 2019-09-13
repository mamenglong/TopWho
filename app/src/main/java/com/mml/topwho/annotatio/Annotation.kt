package com.mml.topwho.annotatio


@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class FieldOrderAnnotation(val order: Int)