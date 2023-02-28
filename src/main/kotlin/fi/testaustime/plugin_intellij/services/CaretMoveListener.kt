package fi.testaustime.plugin_intellij.services

import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.editor.event.CaretEvent
import com.intellij.openapi.editor.event.EditorFactoryEvent
import com.intellij.openapi.editor.event.EditorFactoryListener
import java.time.Duration
import java.time.Duration.ofSeconds
import java.time.Instant
import java.time.Instant.now

class CaretMoveListener(service: TestaustimeProjectService) : com.intellij.openapi.editor.event.CaretListener {
    companion object {
        val TIMEOUT: Duration = ofSeconds(30)
    }

    init {
        EditorFactory.getInstance().addEditorFactoryListener(object : EditorFactoryListener {
            override fun editorCreated(event: EditorFactoryEvent) {
                event.editor.caretModel.addCaretListener(this@CaretMoveListener)
            }
        }, service)
    }

    private var lastActive: Instant = now()

    override fun caretPositionChanged(event: CaretEvent) {
        lastActive = now()
    }

    fun isActive(): Boolean {
        return Duration.between(lastActive, now()) <= TIMEOUT
    }
}