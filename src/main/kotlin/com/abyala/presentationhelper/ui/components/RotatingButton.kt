package com.abyala.presentationhelper.ui.components

import com.abyala.presentationhelper.ui.toImageIcon
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.*

class RotatingButton(val owner: JFrame, initialLabels: List<String> = emptyList()) : JButton() {
    private val labels = mutableListOf<String>()
    private var index = 0

    fun getLabels(): List<String> = labels.toList()

    init {
        isFocusable = false
//        isFocusPainted = false
        labels.addAll(initialLabels)
        recalculateLabel(false)
        Thread.sleep(1000)
        addActionListener({ recalculateLabel() })
        addMouseListener(RotatingButtonMouseAdapter(owner, this))
    }

    fun hasLabels() = labels.isNotEmpty()

    @Synchronized fun recalculateLabel(rotateFirst: Boolean = true) {
        data class TextAndImage(val text: String?, val image: ImageIcon?)

        val textAndImage = if (labels.isEmpty()) {
            index = 0
            TextAndImage("", toImageIcon("images/eyeball4.jpg"))
        } else {
            val newIndex = if (rotateFirst) index + 1 else index
            index = if (newIndex >= labels.size) 0 else newIndex
            TextAndImage(labels[index], null)
        }

/*

        val newText = if(labels.isEmpty()) {
            index = 0
            null
        } else {
            val newIndex = if (rotateFirst) index + 1 else index
            index = if (newIndex >= labels.size) 0 else newIndex
            labels[index]
        }
*/

        SwingUtilities.invokeLater {
//            setText(newText)
            text = textAndImage.text
            icon = textAndImage.image
            owner.pack()
        }
    }

    @Synchronized fun addOption(option: String) {
        if (!labels.contains(option)) {
            labels.add(option)
            if (index == 0 && text != labels[0]) {
                recalculateLabel()
            }
        }
    }

    @Synchronized fun removeOption(option: String) {
        if (labels.remove(option)) {
            if (option == text) {
                recalculateLabel(false)
            }
        }
    }
}

class RotatingButtonMouseAdapter(val frame: JFrame, val owner: RotatingButton) : MouseAdapter() {
    override fun mousePressed(e: MouseEvent?) {
        onMouseEvent(e)
    }

    override fun mouseReleased(e: MouseEvent?) {
        onMouseEvent(e)
    }

    private fun onMouseEvent(e: MouseEvent?) {
        e?.let {
            if (it.isMetaDown) onRightMouseClick(it)
        }
    }

    private fun onRightMouseClick(event: MouseEvent) {
        val popup = JPopupMenu()
        val addOption = JMenuItem("Add option")
        addOption.addActionListener({
            val response = showFormattedInputDialog(frame, "", "Enter new option", { !it.isNullOrBlank() })
            response?.let {
                owner.addOption(it)
            }
        })
        popup.add(addOption)

        if (owner.hasLabels()) {
            val removeOption = JMenuItem("Remove option")
            removeOption.addActionListener({
                val response = showSelectFromListDialog(frame, "", "Select option to remove", owner.getLabels())
                response?.let { owner.removeOption(response) }
            })
            popup.add(removeOption)
        }

        popup.show(owner, owner.x, owner.y)
    }
}
