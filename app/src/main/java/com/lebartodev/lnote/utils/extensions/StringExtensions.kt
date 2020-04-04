package com.lebartodev.lnote.utils.extensions

private const val MAX_TITLE_CHARACTERS = 24

fun String.formattedHint(): String {
    val separateIndex = this.indexOf("\n")
    val firstLine: String
    if (separateIndex != -1) {
        firstLine = this.substring(0, separateIndex)
    } else
        firstLine = this
    return if (firstLine.length > MAX_TITLE_CHARACTERS) {
        firstLine.substring(0, MAX_TITLE_CHARACTERS)
    } else {
        firstLine
    }
}