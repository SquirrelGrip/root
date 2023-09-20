package com.github.squirrelgrip.plugin

import com.fasterxml.jackson.databind.module.SimpleModule
import com.github.squirrelgrip.extension.xml.Xml
import com.github.squirrelgrip.plugin.model.Version
import com.github.squirrelgrip.plugin.serial.VersionDeserializer
import org.apache.maven.reporting.AbstractMavenReport
import org.apache.maven.reporting.MavenReportException
import java.util.Locale

abstract class AbstractDoxiaReport : AbstractMavenReport() {
    abstract val reportHeading: String

    abstract fun body(locale: Locale)

    abstract fun logParameters()

    override fun executeReport(locale: Locale) {
        logParameters()
        if (sink == null) {
            throw MavenReportException("Could not get the Doxia sink")
        }

        sink.head()
        sink.title()
        sink.text(reportHeading)
        sink.title_()
        sink.head_()
        sink.body()
        sink.section1()
        sink.sectionTitle1()
        sink.text(reportHeading)
        sink.sectionTitle1_()

        body(locale)

        sink.section1_()
        sink.body_()
        sink.footer()
        sink.footer_()
    }

    init {
        Xml.objectMapper.registerModule(
            SimpleModule().apply {
                addDeserializer(
                    Version::class.java,
                    VersionDeserializer()
                )
            }
        )
    }
}
