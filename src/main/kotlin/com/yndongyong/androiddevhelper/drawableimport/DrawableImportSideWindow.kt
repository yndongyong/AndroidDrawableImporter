package com.yndongyong.androiddevhelper.drawableimport

import com.intellij.icons.AllIcons
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.psi.PsiManager
import com.intellij.ui.JBColor
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBPanel
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.components.fields.ExpandableTextField
import com.intellij.ui.components.fields.ExtendableTextComponent
import com.intellij.ui.components.fields.ExtendableTextField
import com.intellij.ui.content.ContentFactory
import com.intellij.ui.dsl.builder.*
import com.intellij.ui.table.JBTable
import com.intellij.util.ui.JBUI
import com.yndongyong.androiddevhelper.utils.UIUtils
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Component
import java.awt.Dimension
import java.awt.dnd.DropTarget
import java.awt.dnd.DropTargetDragEvent
import java.awt.dnd.DropTargetDropEvent
import java.awt.dnd.DropTargetEvent
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import javax.imageio.ImageIO
import javax.swing.*
import javax.swing.table.DefaultTableModel

/**
 * Created by Dong on 2021/5/23.
 * @description drawable import 侧边栏
 */
class DrawableImportSideWindow : ToolWindowFactory {

    companion object {
        const val PIC_PATTERN = "(drawable|mipmap)-(mdpi|hdpi|xhdpi|xxhdpi|xxxhdpi)/(.+)"
    }

    val supportedFileFormats = arrayOf("png", "jpg", "jpeg", "webp")

    lateinit var state: DrawableImportState

    private var lastZipFile: File? = null

    private var importButton: Cell<JButton>? = null
    private var fileNameRow: Row? = null
    private var fileNewName: Cell<ExpandableTextField>? = null

    private var onlyOneFile = false

    private val tableModel = DefaultTableModel(arrayOf(
        DrawableImporterBundle.message("drawable.importer.preview"),
        DrawableImporterBundle.message("drawable.importer.filename"),
        DrawableImporterBundle.message("drawable.importer.folder"),
        DrawableImporterBundle.message("drawable.importer.size")
    ), 0)

    private val table = JBTable(tableModel).apply {
        rowHeight = 40
        autoResizeMode = JBTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS
        columnModel.getColumn(0).cellRenderer = ImageRenderer()
        columnModel.getColumn(0).preferredWidth = 150
        columnModel.getColumn(1).preferredWidth = 200
        columnModel.getColumn(2).preferredWidth = 100
    }


    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        state = DrawableImportState.getInstance(project)

        val dragPanel = JBPanel<JBPanel<*>>(BorderLayout()).apply {
            preferredSize = Dimension(300, 150)
            border = BorderFactory.createTitledBorder(DrawableImporterBundle.message("drawable.importer.drag.title"))
            background = JBColor.PanelBackground
            add(JBLabel(DrawableImporterBundle.message("drawable.importer.drag.title")).apply {
                horizontalAlignment = JBLabel.CENTER
            }, BorderLayout.CENTER)

            dropTarget = object : DropTarget() {
                override fun drop(dtde: DropTargetDropEvent) {
                    dtde.acceptDrop(dtde.dropAction)
                    val data = dtde.transferable.getTransferData(
                        java.awt.datatransfer.DataFlavor.javaFileListFlavor
                    ) as? List<*>
                    val files = data?.filterIsInstance<File>()?.filter { it.extension.equals("zip", true) }
                    if (!files.isNullOrEmpty()) {
                        border = BorderFactory.createTitledBorder(DrawableImporterBundle.message("drawable.importer.drag.detected", files.size))
                        background = JBColor(Color(200, 255, 200), Color(40, 70, 40)) // 绿色
                        val zipFile = files.first()
                        parseZip(zipFile)
                        dtde.dropComplete(true)
                    } else {
                        dtde.dropComplete(false)
                        background = Color(0xFFD0D0)
                        border = BorderFactory.createTitledBorder(DrawableImporterBundle.message("drawable.importer.drag.title"))
                        tableModel.rowCount = 0
                        fileNewName?.component?.text = ""
                    }
                }

                //                override fun dragOver(dtde: DropTargetDragEvent?) {
//                    super.dragOver(dtde)
//                    dragPanel.background = JBColor(Color(200, 255, 200), Color(40, 70, 40))
//                }
                override fun dragEnter(dtde: DropTargetDragEvent?) {
                    super.dragEnter(dtde)
                    background = JBColor(Color(200, 255, 200), Color(40, 70, 40)) // 绿色
                }

                override fun dragExit(dte: DropTargetEvent?) {
                    super.dragExit(dte)
                    background = JBColor.PanelBackground
                }
            }
        }

        val outputDirField = ExtendableTextField().apply {
            columns = 50
            emptyText.text = DrawableImporterBundle.message("drawable.importer.res.directory.placeholder")
            border = JBUI.Borders.empty(2)
            horizontalAlignment = JTextField.RIGHT

            addExtension(
                ExtendableTextComponent.Extension.create(
                    AllIcons.Nodes.Folder,
                    DrawableImporterBundle.message("drawable.importer.res.directory"),
                ) {
                    val projectBase = ProjectManager.getInstance().openProjects.firstOrNull()?.basePath ?: return@create
                    val descriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor()
                    val toSelect = VfsUtil.findFile(File(projectBase).toPath(), true)
                    FileChooser.chooseFile(
                        descriptor,
                        ProjectManager.getInstance().openProjects.firstOrNull(),
                        null as Component?,
                        toSelect
                    ) { file: VirtualFile ->
                        text = file.path
                        state.resFilePath = text
                    }
                }
            )
        }
        if (state.resFilePath.isEmpty()) {
            outputDirField.text = ProjectManager.getInstance()
                .openProjects.firstOrNull()?.basePath.orEmpty()
        } else {
            outputDirField.text = state.resFilePath
        }
        // 构建 panel
        val panel = panel {
            indent {
//                row("res目录") {
//                    textFieldWithBrowseButton(
//                        "选择res目录",
//                        ProjectManager.getInstance().defaultProject,
//                        FileChooserDescriptorFactory.createSingleFolderDescriptor(),
//
//                        ).align(Align.FILL)
//                        .applyToComponent {
//                            if (state.resFilePath.isNotEmpty()) {
//                                text = state.resFilePath
//                            }
//                            textField.horizontalAlignment = JTextField.RIGHT
//                            textField.document.addDocumentListener(object : DocumentListener {
//                                override fun insertUpdate(e: DocumentEvent) = save()
//                                override fun removeUpdate(e: DocumentEvent) = save()
//                                override fun changedUpdate(e: DocumentEvent) = save()
//                                private fun save() {
//                                    state.resFilePath = text
//                                }
//                            })
//                        }
//
//                }.topGap(TopGap.SMALL)
                row(DrawableImporterBundle.message("drawable.importer.res.directory")) {
                    cell(outputDirField).resizableColumn().align(Align.FILL)
                }.topGap(TopGap.SMALL)
                row { cell(dragPanel).resizableColumn().align(Align.FILL) }.topGap(TopGap.MEDIUM)
                row { text(DrawableImporterBundle.message("drawable.importer.image.list")).resizableColumn() }.topGap(TopGap.SMALL)
                row {
                    cell(JBScrollPane(table).apply { preferredSize = Dimension(300, 200) }).resizableColumn()
                        .align(Align.FILL)
                }
                row(DrawableImporterBundle.message("drawable.importer.resource.type")) {
                    comboBox(DrawableType.entries.map { it.type }).resizableColumn().applyToComponent {
                        selectedItem = state.drawableType
                        addActionListener {
                            state.drawableType = selectedItem as String
                        }
                    }
                }.topGap(TopGap.SMALL)

                fileNameRow = row(DrawableImporterBundle.message("drawable.importer.icon.name")) {
                    fileNewName = expandableTextField().apply { text(DrawableImporterBundle.message("drawable.importer.icon.name.placeholder")) }.align(Align.FILL)
                }.rowComment(DrawableImporterBundle.message("drawable.importer.icon.name.comment")).visible(onlyOneFile).topGap(TopGap.SMALL)

                row {
                    importButton = button(DrawableImporterBundle.message("drawable.importer.import.button")) {
                        val resDir = state.resFilePath
                        println("resDir:$resDir")
                        if (resDir.isNullOrBlank()) {
                            Messages.showErrorDialog(DrawableImporterBundle.message("drawable.importer.error.select.res"), "Error")
                            return@button
                        }
                        val resDirFile = File(resDir)
                        if (!resDirFile.exists() || !resDirFile.isDirectory) {
                            Messages.showErrorDialog(DrawableImporterBundle.message("drawable.importer.error.invalid.res"), "Error")
                            return@button
                        }
                        if (lastZipFile == null) {
                            Messages.showErrorDialog(DrawableImporterBundle.message("drawable.importer.error.drag.zip"), "Error")
                            return@button
                        }

                        importImagesWithConfirm(project, resDirFile, lastZipFile!!)

                    }.enabled(tableModel.rowCount > 0)
                }.topGap(TopGap.SMALL).rowComment(DrawableImporterBundle.message("drawable.importer.import.comment"))
            }
        }


        // 监听 tableModel 变化
        tableModel.addTableModelListener { importButton?.enabled(tableModel.rowCount > 0) }

        //添加 panel 到 ToolWindow
        toolWindow.contentManager.addContent(
            ContentFactory.getInstance().createContent(panel, "", false)
        )
    }


    private fun parseZip(zipFile: File) {
        lastZipFile = zipFile
        SwingUtilities.invokeLater {
            tableModel.rowCount = 0 // 清空旧数据
            val allFileName = hashSetOf<String>()
            ZipFile(zipFile).use { zip ->
                zip.entries().asSequence().forEach { entry ->
                    if (!entry.isDirectory && entry.name.matches(Regex("(drawable|mipmap)-(mdpi|hdpi|xhdpi|xxhdpi|xxxhdpi)/.+"))) {
                        val size = "${entry.size / 1024} KB"
                        val fileName = entry.name.substringAfterLast("/")
                        allFileName.add(fileName)
                        val folder = entry.name.substringBeforeLast("/")
                        val bufferedImage = ImageIO.read(zip.getInputStream(entry))
                        val imageIcon = if (bufferedImage != null) {
//                            ImageIcon(bufferedImage.getScaledInstance(32, 32, Image.SCALE_SMOOTH))
                            ImageIcon(bufferedImage)
                        } else {
                            null
                        }
                        tableModel.addRow(arrayOf(imageIcon, fileName, folder, size))
                    }
                }
            }
            if (allFileName.size == 1) {
                onlyOneFile = true
                fileNewName?.text(allFileName.first())
                fileNameRow?.visible(true)
            } else {
                onlyOneFile = false
                fileNewName?.text("")
                fileNameRow?.visible(false)
            }
        }

    }


    private fun importImagesWithConfirm(project: Project, resDir: File, zipFile: File) {
        val zip = ZipFile(zipFile)
        val conflictEntries = mutableListOf<Pair<ZipFile, ZipEntry>>()

        val selectedDrawableType = state.drawableType
        // 1. 先收集冲突文件
        zip.entries().asSequence().forEach { entry ->
            val match = Regex(PIC_PATTERN).find(entry.name)
            if (match != null) {
                val resType = match.groupValues[1] // drawable 或 mipmap
                val dpiFolder = match.groupValues[2] // hdpi/xhdpi/xxhdpi
                var fileName = match.groupValues[3]


                val targetDir = File(resDir, "$selectedDrawableType-$dpiFolder")

//                if (!targetDir.exists()) targetDir.mkdirs()

                if (onlyOneFile) {
                    // 支持rename
                    fileName = fileNewName?.component?.text ?: fileName
                }
                val targetFile = File(targetDir, fileName)
                if (targetFile.exists()) {
                    conflictEntries.add(zip to entry)
                }

            }
        }

        if (conflictEntries.isNotEmpty()) {
            val dialog = FileConflictDialog(resDir, zipFile, selectedDrawableType)
            if (dialog.showAndGet()) {
                WriteCommandAction.runWriteCommandAction(project) {
                    importImages(resDir, lastZipFile!!)
                }
                SwingUtilities.invokeLater {
                    UIUtils.showInfoNotification(ProjectManager.getInstance().defaultProject, DrawableImporterBundle.message("drawable.importer.import.complete"))
                }
            }
        } else {
//            没有冲突
            WriteCommandAction.runWriteCommandAction(project) {
                importImages(resDir, lastZipFile!!)
            }
            SwingUtilities.invokeLater {
                UIUtils.showInfoNotification(ProjectManager.getInstance().defaultProject, DrawableImporterBundle.message("drawable.importer.import.complete"))
            }
        }


    }


    /**
     * 导入图片到项目 res 目录
     */
    private fun importImages(resDir: File, zipFile: File) {
        ZipFile(zipFile).use { zip ->
            zip.entries().asSequence().forEach { entry ->
//                var folderName = entry.name.substringBeforeLast("/") // e.g. drawable-xhdpi
//                val fileName = entry.name.substringAfterLast("/")

                val match = Regex(PIC_PATTERN).find(entry.name)
                if (match != null) {
                    val resType = match.groupValues[1] // drawable 或 mipmap
                    val dpiFolder = match.groupValues[2] // hdpi/xhdpi/xxhdpi
                    var fileName = match.groupValues[3]

//                    val selectedResType = resTypeCombo.selectedItem as String
                    val selectedResType = state.drawableType
                    val targetDir = File(resDir, "$selectedResType-$dpiFolder")

                    if (!targetDir.exists()) targetDir.mkdirs()

                    if (onlyOneFile) {
                        // 支持rename
                        fileName = fileNewName?.component?.text ?: fileName
                    }
                    val targetFile = File(targetDir, fileName)

                    zip.getInputStream(entry).use { input ->
                        FileOutputStream(targetFile).use { output ->
                            input.copyTo(output)
                        }
                    }

                    // 刷新 VirtualFileSystem
                    val virtualFile = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(targetFile)
                    if (virtualFile != null) {
                        VfsUtil.markDirtyAndRefresh(true, false, false, virtualFile)
                        PsiManager.getInstance(ProjectManager.getInstance().defaultProject)
                            .findFile(virtualFile) // 建立 PSI
                    }
                }


            }
        }
    }

    internal enum class DrawableType(val type: String) {
        Drawable("drawable"),
        Mipmap("mipmap")
    }
}


