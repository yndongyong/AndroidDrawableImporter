package com.yndongyong.androiddevhelper.drawableimport

import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.dsl.builder.Align
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.table.JBTable
import java.awt.Dimension
import java.io.File
import java.util.zip.ZipFile
import javax.imageio.ImageIO
import javax.swing.ImageIcon
import javax.swing.JComponent
import javax.swing.table.DefaultTableModel

/**
 * 文件冲突对比
 */
class FileConflictDialog(
    private val resFile: File,
    private val lastZipFile: File,
    private val selectedDrawableType: String
) : DialogWrapper(true) {

    private val _rowHeight = 40

    private val tableModel = DefaultTableModel(
        arrayOf(
            DrawableImporterBundle.message("drawable.importer.conflict.directory"),
            DrawableImporterBundle.message("drawable.importer.conflict.new.image"),
            DrawableImporterBundle.message("drawable.importer.conflict.existing"),
        ), 0
    )
    private val table = JBTable(tableModel).apply {
        this.rowHeight = _rowHeight
        autoResizeMode = JBTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS
        columnModel.getColumn(0).preferredWidth = 100
        columnModel.getColumn(1).preferredWidth = 150
        columnModel.getColumn(1).cellRenderer = ImageRenderer()
        columnModel.getColumn(2).preferredWidth = 150
        columnModel.getColumn(2).cellRenderer = ImageRenderer()
    }

    init {
        title = DrawableImporterBundle.message("drawable.importer.conflict.title")
        init()
        setCancelButtonText(DrawableImporterBundle.message("drawable.importer.conflict.cancel"))
        setOKButtonText(DrawableImporterBundle.message("drawable.importer.conflict.overwrite"))
        loadConflictFiles()
    }

    private fun loadConflictFiles() {
        tableModel.rowCount = 0
        ZipFile(lastZipFile).use { zip ->
            zip.entries().asSequence().forEach { entry ->
                val match = Regex(DrawableImportSideWindow.PIC_PATTERN).find(entry.name)
                if (match != null) {
                    val resType = match.groupValues[1] // drawable 或 mipmap
                    val dpiFolder = match.groupValues[2] // hdpi/xhdpi/xxhdpi
                    var fileName = match.groupValues[3]

                    val bufferedImage = ImageIO.read(zip.getInputStream(entry))
                    val imageIcon = if (bufferedImage != null) {
                        ImageIcon(bufferedImage)
                    } else {
                        null
                    }

                    val folder = "$selectedDrawableType-$dpiFolder"
                    val newName = "$folder/$fileName"
                    val oldImageIcon = if (File(resFile, newName).exists()) {
                        ImageIcon(ImageIO.read(File(resFile, newName)))
                    } else {
                        null
                    }

                    tableModel.addRow(arrayOf(folder, imageIcon, oldImageIcon))
                }
            }
        }
    }

    override fun createCenterPanel(): JComponent = panel {
        row { text(DrawableImporterBundle.message("drawable.importer.conflict.message")) }
        row {
            cell(JBScrollPane(table).apply {
                preferredSize = Dimension(450, 300) // 控制表格显示区域高度
            }).resizableColumn().align(Align.FILL)
        }

    }
//
//    override fun createActions(): Array<Action> {
//        super.createActions()
//        return arrayOf(cancelAction, okAction)
//    }

}