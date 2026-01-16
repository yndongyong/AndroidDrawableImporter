package com.yndongyong.androiddevhelper.drawableimport

import java.awt.Component
import java.awt.Image
import javax.swing.ImageIcon
import javax.swing.JLabel
import javax.swing.JTable
import javax.swing.table.TableCellRenderer
import kotlin.math.max
import kotlin.math.min


/**
 * Created by Dong on 2018/5/23.
 * 图片渲染
 */
//class ImageRenderer : TableCellRenderer {
//    override fun getTableCellRendererComponent(
//        table: JTable,
//        value: Any?,
//        isSelected: Boolean,
//        hasFocus: Boolean,
//        row: Int,
//        column: Int
//    ): Component {
//        return if (value is Icon) JLabel(value) else JLabel(value?.toString() ?: "")
//    }
//}

class ImageRenderer() : TableCellRenderer {
    override fun getTableCellRendererComponent(
        table: JTable,
        value: Any?,
        isSelected: Boolean,
        hasFocus: Boolean,
        row: Int,
        column: Int
    ): Component {
        val label = JLabel()
        label.horizontalAlignment = JLabel.CENTER
        if (value is ImageIcon) {
            // 按行高缩放图片
//            val img = value.image
//            val aspect = img.getWidth(null).toDouble() / img.getHeight(null)
//            val width = (rowHeight * aspect).toInt()
//            val scaled = img.getScaledInstance(width, rowHeight, Image.SCALE_SMOOTH)
//            label.icon = ImageIcon(scaled)

            // 按行高缩放图片
            val img = value.image
            val cellWidth = table.columnModel.getColumn(column).width
            val cellHeight = table.getRowHeight(row)

            // 按单元格大小等比缩放
            val scaleX: Double = cellWidth.toDouble() / img.getWidth(null)
            val scaleY: Double = cellHeight.toDouble() / img.getHeight(null)
            val scale = min(scaleX, scaleY)

            val newW = (img.getWidth(null) * scale).toInt()
            val newH = (img.getHeight(null) * scale).toInt()

            val scaled: Image = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH)
            label.icon = ImageIcon(scaled)
        }
        return label
    }
}