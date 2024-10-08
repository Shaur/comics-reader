package com.home.reader.archive

import com.github.junrar.Archive
import com.github.junrar.Junrar
import com.home.reader.component.dto.ArchiveMeta
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.nio.file.Files

class CbrTool(fileName: String) : ArchiveTool(fileName) {

    override fun getMeta(input: InputStream): ArchiveMeta {
        val xmlFile =
            Files.createTempFile("ComicsInfo" + System.currentTimeMillis(), "xml").toFile()
        val archive = Archive(input)
        archive.fileHeaders
            .find { it.fileName.contains("ComicInfo.xml") }
            ?.let {
                archive.extractFile(it, FileOutputStream(xmlFile))
            }

        if (xmlFile.length() > 0) {
            val meta = extractMetaFromXml(xmlFile)
            xmlFile.delete()

            return meta
        }

        val seriesName = extractSeriesNameFromFileName(fileName)

        val number = listOf(
            extractNumber(fileName, seriesName),
            extractNumberFromFileName(fileName)
        ).firstOrNull { it.isNotBlank() } ?: ""


        return ArchiveMeta(
            seriesName = seriesName,
            number = number
        )
    }

    override fun extract(input: InputStream, destination: File) {
        if (!destination.exists()) {
            destination.mkdirs()
        }

        var files = Junrar.extract(input, destination)
        files.filter { it.extension == "xml" }
            .forEach {
                files.remove(it)
                it.delete()
            }

        val min = files.minBy { it.nameWithoutExtension.length }
        if (hasTrashPages(files.map { it.nameWithoutExtension })) {
            files = files - min
            min.delete()
        }

        val parentFile = files.first().parentFile
        if (parentFile != destination) {
            files.forEach { it.renameTo(destination.resolve(it.name)) }
            parentFile?.deleteRecursively()
        }
    }

}
