package com.github.squirrelgrip.dependency

import org.apache.maven.doxia.sink.Sink

fun Sink.foot() {
    section1_()
    body_()
}

fun Sink.header(heading: String) {
    head()
    title()
    text(heading)
    title_()
    head_()
    body()
    section1()
    sectionTitle1()
    text(heading)
    sectionTitle1_()
    footer()
    footer_()
}

fun Sink.tableHeaderRow(
    vararg header: String
) {
    tableRow()
    header.forEach {
        tableHeaderCell()
        text(it)
        tableHeaderCell_()
    }
    tableRow_()
}

fun Sink.tableRow(
    vararg cell: String
) {
    tableRow()
    cell.forEach {
        tableCell()
        text(it)
        tableCell_()
    }
    tableRow_()
}
