package com.home.reader.utils

import com.github.junrar.Junrar
import com.home.reader.component.dto.CbrMeta
import java.io.File
import java.io.InputStream
import java.nio.file.Files
import kotlin.math.min

object CbrUtil {

    fun getMeta(input: InputStream, fileName: String): CbrMeta {
        val descriptors = Junrar.getContentsDescription(input)
            .filter { !it.path.endsWith("xml") }

        val firstPageName = descriptors.first().path
        val seriesName = crossNames(fileName, firstPageName).trim()

        return CbrMeta(
            seriesName = seriesName,
            number = extractNumber(fileName, seriesName),
            pagesCount = descriptors.count()
        )
    }

    private fun extractNumber(fileName: String, seriesName: String): String {
        val substring = fileName.substring(seriesName.length + 1)
        val indexOfSpace = substring.indexOf(" ")
        if (indexOfSpace == -1) {
            return ""
        }

        val number = substring.substring(0, indexOfSpace).trim()
        return (number.toIntOrNull() ?: number.toDoubleOrNull() ?: number).toString()
    }

    fun extract(input: InputStream, destination: File) {
        if (!destination.exists()) {
            destination.mkdirs()
        }

        val files = Junrar.extract(input, destination)
        files.filter { it.extension == "xml" }
            .forEach {
                files.remove(it)
                it.delete()
            }

        val parentFile = files.first().parentFile
        if (parentFile != destination) {
            files.forEach { it.renameTo(destination.resolve(it.name)) }
            parentFile?.deleteRecursively()
        }
    }

    private fun crossNames(name1: String, name2: String): String {
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