package com.github.squirrelgrip.extensions.spring.integration.excel.splitter

import com.github.squirrelgrip.extensions.spring.integration.excel.transformer.FileToWorkbookTransformer
import org.apache.poi.ss.usermodel.Workbook
import org.springframework.integration.splitter.AbstractMessageSplitter
import org.springframework.integration.support.MessageBuilder
import org.springframework.messaging.Message

class WorkbookToSheetSplitter : AbstractMessageSplitter() {
    override fun splitMessage(message: Message<*>?): Any =
        ((message?.payload as? Workbook)?.let { workbook ->
            IntRange(0, workbook.numberOfSheets - 1).map {
                workbook.getSheetAt(it).let { sheet ->
                    MessageBuilder.withPayload(sheet)
                        .copyHeaders(message.headers)
                        .setHeader("sheet.index", it)
                        .setHeader("sheet.name", sheet.sheetName)
                }
            }
        }) ?: throw IllegalArgumentException("message.payload is not a Workbook")
}
