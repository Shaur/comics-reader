package com.home.reader.archive

import com.github.junrar.Junrar
import com.home.reader.component.dto.ArchiveMeta
import java.io.File
import java.io.InputStream

class CbrTool(fileName: String) : ArchiveTool(fileName) {

    override fun getMeta(input: InputStream): ArchiveMeta {
        val descriptors = Junrar.getContentsDescription(input)
            .filter { !it.path.endsWith("xml") }

        val firstPageName = descriptors.first().path
        val seriesName = listOf(
            crossNames(fileName, firstPageName).trim(),
            extractSeriesNameFromFileName(fileName)
        )
            .filter { it != fileName && it.isNotEmpty() }
            .minByOrNull { it.length } ?: fileName

        val number = listOf(
            extractNumber(fileName, seriesName),
            extractNumberFromFileName(fileName)
        ).firstOrNull { it.isNotBlank() } ?: ""


        return ArchiveMeta(
            seriesName = seriesName,
            number = number,
            pagesCount = descriptors.count()
        )
    }

    override fun extract(input: InputStream, destination: File) {
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

}
