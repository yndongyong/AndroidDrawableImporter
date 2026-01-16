package com.yndongyong.androiddevhelper.drawableimport

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.project.Project
import com.intellij.util.xmlb.XmlSerializerUtil

@Service(Service.Level.PROJECT)
@State(name = "DrawableImportState", storages = [Storage("dy-drawable-import-state.xml")])
class DrawableImportState : PersistentStateComponent<DrawableImportState> {

    companion object {
        fun getInstance(project: Project): DrawableImportState =
            project.getService(DrawableImportState::class.java)
    }

    /*res 目录 地址*/
    var resFilePath = ""

    /*drawableType ：drawable or mipmap*/
    var drawableType = "drawable"

    override fun getState(): DrawableImportState = this

    override fun loadState(state: DrawableImportState) {
        XmlSerializerUtil.copyBean(state, this)
    }

}