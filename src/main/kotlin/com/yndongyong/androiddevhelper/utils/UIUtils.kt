package com.yndongyong.androiddevhelper.utils

import com.intellij.codeInsight.hint.HintManager
import com.intellij.codeInsight.hint.HintUtil
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.MessageType
import com.intellij.openapi.ui.popup.Balloon
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.openapi.wm.WindowManager
import com.intellij.openapi.wm.ex.StatusBarEx
import com.intellij.ui.JBColor
import com.intellij.ui.awt.RelativePoint
import java.awt.Color
import javax.swing.Icon

object UIUtils {
    fun showErrorHintInEditor(editor: Editor, message: String) {
        /*  55 */
        if (editor == null) throw NullPointerException()
        if (message == null) throw NullPointerException()
        HintManager.getInstance().showErrorHint(editor, message)
    }

    fun showErrorInfoBelowCaret(editor: Editor, message: String, durationSeconds: Int) {
        /*  67 */
        if (editor == null) throw NullPointerException()
        if (message == null) throw NullPointerException()
        showInfoBelowCaret(editor, message, MessageType.ERROR, durationSeconds)
    }

    fun showErrorInfoBelowCaret(editor: Editor, message: String) {
        /*  78 */
        if (editor == null) throw NullPointerException()
        if (message == null) throw NullPointerException()
        val durationSeconds = 3
        /*  79 */showErrorInfoBelowCaret(editor, message, durationSeconds)
    }

    fun showInfoBelowCaret(editor: Editor, msg: String, icon: Icon?, fillColor: JBColor, durationSeconds: Int) {
        /*  97 */
        if (editor == null) throw NullPointerException()
        if (msg == null) throw NullPointerException()
        if (fillColor == null) throw NullPointerException()
        ApplicationManager.getApplication().invokeLater {
            if (editor == null) {
                throw NullPointerException()
            }
            if (msg == null) {
                throw NullPointerException()
            }
            if (fillColor == null) {
                throw NullPointerException()
            }
            val factory = JBPopupFactory.getInstance()
            val point = factory.guessBestPopupLocation(editor)
            factory.createHtmlTextBalloonBuilder(msg, icon, fillColor as Color, null)
                .setFadeoutTime((durationSeconds * 1000).toLong()).createBalloon().show(point, Balloon.Position.below)
        }
    }

    fun showInfoBelowCaret(editor: Editor, msg: String, msgType: MessageType?, durationSeconds: Int) {
        /* 121 */
        if (editor == null) throw NullPointerException()
        if (msg == null) throw NullPointerException()
        ApplicationManager.getApplication().invokeLater {
            if (editor == null) {
                throw NullPointerException()
            }
            if (msg == null) {
                throw NullPointerException()
            }
            val factory = JBPopupFactory.getInstance()
            val point = factory.guessBestPopupLocation(editor)
            factory.createHtmlTextBalloonBuilder(msg, msgType, null).setFadeoutTime((durationSeconds * 1000).toLong())
                .createBalloon().show(point, Balloon.Position.below)
        }
    }

    fun showNormalInfoAboveStatusBar(project: Project, msg: String) {
        /* 140 */
        if (project == null) throw NullPointerException()
        if (msg == null) throw NullPointerException()
        showInfoAboveStatusBar(project, msg, HintUtil.getInformationColor())
    }

    fun showErrorInfoAboveStatusBar(project: Project, msg: String) {
        /* 151 */
        if (project == null) throw NullPointerException()
        if (msg == null) throw NullPointerException()
        showInfoAboveStatusBar(project, msg, HintUtil.getErrorColor())
    }

    fun showInfoAboveStatusBar(project: Project, msg: String, icon: Icon?, fillColor: JBColor, durationSeconds: Int) {
        /* 169 */
        if (project == null) throw NullPointerException()
        if (msg == null) throw NullPointerException()
        if (fillColor == null) throw NullPointerException()
        ApplicationManager.getApplication().invokeLater {
            if (project == null) {
                throw NullPointerException()
            }
            if (msg == null) {
                throw NullPointerException()
            }
            if (fillColor == null) {
                throw NullPointerException()
            }
            val statusBar = WindowManager.getInstance().getStatusBar(project) as StatusBarEx ?: return@invokeLater
            val centerPoint = statusBar.component?.let { RelativePoint.getCenterOf(it) }
            val factory = JBPopupFactory.getInstance()
            factory.createHtmlTextBalloonBuilder(msg, icon, fillColor as Color, null)
                .setFadeoutTime((durationSeconds * 1000).toLong()).createBalloon()
                .show(centerPoint, Balloon.Position.atRight)
        }
    }

    fun showInfoAboveStatusBar(project: Project, msg: String, msgType: MessageType, durationSeconds: Int) {
        /* 198 */
        if (project == null) throw NullPointerException()
        if (msg == null) throw NullPointerException()
        if (msgType == null) throw NullPointerException()
        ApplicationManager.getApplication().invokeLater {
            if (project == null) {
                throw NullPointerException()
            }
            if (msg == null) {
                throw NullPointerException()
            }
            if (msgType == null) {
                throw NullPointerException()
            }
            val statusBar = WindowManager.getInstance().getStatusBar(project) as StatusBarEx ?: return@invokeLater
            val centerPoint = statusBar.component?.let { RelativePoint.getCenterOf(it) }
            val factory = JBPopupFactory.getInstance()
            factory.createHtmlTextBalloonBuilder(msg, msgType, null).setFadeoutTime((durationSeconds * 1000).toLong())
                .createBalloon().show(centerPoint, Balloon.Position.atRight)
        }
    }

    fun showInfoAboveStatusBar(project: Project, msg: String, color: Color) {
        /* 225 */
        if (project == null) throw NullPointerException()
        if (msg == null) throw NullPointerException()
        if (color == null) throw NullPointerException()
        ApplicationManager.getApplication().invokeLater {
            if (project == null) {
                throw NullPointerException()
            }
            if (msg == null) {
                throw NullPointerException()
            }
            if (color == null) {
                throw NullPointerException()
            }
            val statusBar = WindowManager.getInstance().getStatusBar(project) as StatusBarEx ?: return@invokeLater
            val centerPoint = statusBar.component?.let { RelativePoint.getCenterOf(it) }
            val factory = JBPopupFactory.getInstance()
            factory.createHtmlTextBalloonBuilder(msg, null, color, null).setHideOnAction(true)
                .setHideOnClickOutside(true).setHideOnKeyOutside(true).createBalloon()
                .show(centerPoint, Balloon.Position.atRight)
        }
    }

    fun showInfoAboveStatusBar(project: Project, msg: String, msgType: MessageType) {
        /* 254 */
        if (project == null) throw NullPointerException()
        if (msg == null) throw NullPointerException()
        if (msgType == null) throw NullPointerException()
        ApplicationManager.getApplication().invokeLater {
            if (project == null) {
                throw NullPointerException()
            }
            if (msg == null) {
                throw NullPointerException()
            }
            if (msgType == null) {
                throw NullPointerException()
            }
            val statusBar = WindowManager.getInstance().getStatusBar(project) as StatusBarEx ?: return@invokeLater
            val centerPoint = statusBar.component?.let { RelativePoint.getCenterOf(it) }
            val factory = JBPopupFactory.getInstance()
            factory.createHtmlTextBalloonBuilder(msg, msgType, null).setHideOnAction(true).setHideOnClickOutside(true)
                .setHideOnKeyOutside(true).createBalloon().show(centerPoint, Balloon.Position.atRight)
        }
    }

    fun setStatusBarText(project: Project?, message: String?) {
        /* 274 */
        if (project != null) {
            /* 275 */
            val statusBar = WindowManager.getInstance().getStatusBar(project) as StatusBarEx
            /* 276 */if (statusBar != null) /* 277 */ statusBar.info = message
        }
    }

    /**
     * Show information with Notification
     * @param project Project
     * @param content The content will show
     */
    fun showErrorNotification(project: Project, content: String) {
        NotificationGroupManager.getInstance().getNotificationGroup("DongAndroidDevHelper Notification Group")
            .createNotification(content, NotificationType.ERROR)
            .notify(project)
    }
    fun showInfoNotification(project: Project, content: String) {
        NotificationGroupManager.getInstance().getNotificationGroup("DongAndroidDevHelper Notification Group")
            .createNotification(content, NotificationType.INFORMATION)
            .notify(project)
    }
}