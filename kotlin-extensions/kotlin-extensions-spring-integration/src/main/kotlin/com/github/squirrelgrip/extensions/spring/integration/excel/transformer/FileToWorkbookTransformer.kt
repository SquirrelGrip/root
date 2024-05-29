package com.github.squirrelgrip.extensions.spring.integration.excel.transformer

import com.github.squirrelgrip.extension.excel.toWorkbook
import org.apache.poi.ss.usermodel.Workbook
import org.springframework.integration.file.transformer.AbstractFilePayloadTransformer
import java.io.File

class FileToWorkbookTransformer: AbstractFilePayloadTransformer<Workbook>() {
    companion object {
        /**
         * Create a {@link FileToWorkbookTransformer} instance with no delete files afterwards.
         * @return the {@link FileToWorkbookTransformer}.
         */
        @JvmStatic
        fun toWorkbookTransformer(): FileToWorkbookTransformer =
            toWorkbookTransformer(false)

        /**
         * Create a [FileToWorkbookTransformer] instance with delete files flag.
         * @param deleteFiles true to delete the file.
         * @return the [FileToWorkbookTransformer].
         */
        @JvmStatic
        fun toWorkbookTransformer(deleteFiles: Boolean): FileToWorkbookTransformer =
            FileToWorkbookTransformer().apply {
                setDeleteFiles(deleteFiles)
            }
    }

    override fun transformFile(file: File): Workbook =
        file.toWorkbook()
}
