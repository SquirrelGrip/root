package com.github.squirrelgrip.extensions.spring.integration.excel.splitter

import org.apache.poi.ss.usermodel.Sheet
import org.springframework.integration.splitter.AbstractMessageSplitter
import org.springframework.messaging.Message

class SheetToRowSplitter : AbstractMessageSplitter() {
    override fun splitMessage(message: Message<*>?): Any =
        ((message?.payload as? Sheet)?.let { sheet ->
            sheet.rowIterator().asSequence().map { row ->
                row
            }
        }) ?: throw IllegalArgumentException("message.payload is not a Sheet")
}
