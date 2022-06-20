package com.home.reader.archive

import com.home.reader.component.dto.ArchiveMeta
import java.io.File
import java.io.InputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

class CbzTool(fileName: String) : ArchiveTool(fileName) {

    override fun getMeta(input: InputStream): ArchiveMeta {
        val descriptors = ZipInputStream(input).use { zis ->
            zis.seq()
                .filter { !it.isDirectory && !it.name.endsWith("xml") }
                .map { it.name }
                .toList()
        }

        val firstPageName = descriptors.first()
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

        ZipInputStream(input).use { zis ->
            zis.seq()
                .filter { !it.isDirectory && !it.name.endsWith("xml") }
                .forEach {
                    val imageName = if(it.name.contains("/")) {
                        it.name.split("/").last()
                    } else {
                        it.name
                    }

                    val output = destination.resolve(imageName)
                    zis.copyTo(output.outputStream())
                }
        }
    }
}

private fun ZipInputStream.seq(): Sequence<ZipEntry> {
    return generateSequence { this.nextEntry.takeIf { it != null } }
}