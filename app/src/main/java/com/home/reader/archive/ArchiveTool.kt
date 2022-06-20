package com.home.reader.archive

import com.home.reader.component.dto.ArchiveMeta
import java.io.File
import java.io.InputStream
import kotlin.math.min

abstract class ArchiveTool(protected val fileName: String) {

    companion object {
        private val REGEX = Regex("^[A-Za-z\\d)(.\\-\" ']+ \\d{1,3}")
    }

    abstract fun getMeta(input: InputStream): ArchiveMeta

    abstract fun extract(input: InputStream, destination: File)

    protected fun extractSeriesNameFromFileName(fileName: String): String {
        val normalized = fileName.replace("_", " ")
        val nameWithNumber = REGEX.find(normalized)?.value ?: fileName
        val lastIndexOfSpace = nameWithNumber.lastIndexOf(" ")
        if (lastIndexOfSpace == -1) {
            return fileName
        }

        return nameWithNumber.substring(0, lastIndexOfSpace).trim()
    }

    protected fun extractNumberFromFileName(fileName: String): String {
        val normalized = fileName.replace("_", " ")
        val nameWithNumber = REGEX.find(normalized)?.value ?: fileName
        val lastIndexOfSpace = nameWithNumber.lastIndexOf(" ")
        if (lastIndexOfSpace == -1) {
            return fileName
        }

        val number = nameWithNumber.substring(lastIndexOfSpace).trim()
        return (number.toIntOrNull() ?: number.toDoubleOrNull() ?: number).toString()
    }

    protected fun extractNumber(fileName: String, seriesName: String): String {
        val substring = fileName.substring(seriesName.length + 1)
        val indexOfSpace = substring.indexOf(" ")
        if (indexOfSpace == -1) {
            return ""
        }

        val number = substring.substring(0, indexOfSpace).trim()
        return (number.toIntOrNull() ?: number.toDoubleOrNull() ?: number).toString()
    }

    protected fun crossNames(name1: String, name2: String): String {
        val min = min(name1.length, name2.length)

        val name1LowerCase = name1.lowercase()
        val name2LowerCase = name2.lowercase()

        val builder: StringBuilder = StringBuilder()
        for (i in 0 until min) {
            if (name1LowerCase[i] == ' ' || name2LowerCase[i] == ' ') {
                builder.append(' ')
                continue
            }

            if (name1LowerCase[i] != name2LowerCase[i]) {
                break
            }

            builder.append(name1[i])
        }

        return builder.toString()
    }

}