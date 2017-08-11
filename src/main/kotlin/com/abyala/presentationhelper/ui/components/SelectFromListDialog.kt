package com.abyala.presentationhelper.ui.components

import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.beans.PropertyChangeEvent
import java.beans.PropertyChangeListener
import javax.swing.*

fun showSelectFromListDialog(frame: JFrame,
                             dialogTitle: String,
                             question: String,
                             options: List<String>
): String? {
    val dialog = SelectFromListDialog(frame, dialogTitle, question, options)
    return dialog.userResult
}

private class SelectFromListDialog(frame: JFrame,
                                   dialogTitle: String,
                                   question: String,
                                   options: List<String>
) : JDialog(frame, dialogTitle, true), ActionListener, PropertyChangeListener {

    private val questionLabel = JLabel(question)
    private val listBox = JList(options.toTypedArray())
    private val optionPane: JOptionPane
    var userResult: String? = null
        private set

    init {
        listBox.selectionMode = ListSelectionModel.SINGLE_SELECTION
        optionPane = JOptionPane(arrayOf(questionLabel, listBox), JOptionPane.QUESTION_MESSAGE, JOptionPane.OK_CANCEL_OPTION)
        contentPane = optionPane
        optionPane.addPropertyChangeListener(this)
        listBox.addPropertyChangeListener(this)
        pack()
        isVisible = true
    }

    override fun actionPerformed(e: ActionEvent?) {
        optionPane.value = JOptionPane.OK_OPTION
    }

    override fun propertyChange(event: PropertyChangeEvent?) {
        if (event != null && event.source == optionPane) {
            val propName = event.propertyName
            if (propName == JOptionPane.VALUE_PROPERTY || propName == JOptionPane.INPUT_VALUE_PROPERTY) {
                val value = optionPane.value
                if (value != JOptionPane.UNINITIALIZED_VALUE) {
                    // Must reset the JOptionPane's value
                    optionPane.value = JOptionPane.UNINITIALIZED_VALUE
                    when (value) {
                        JOptionPane.OK_OPTION -> processOk(listBox.selectedValue)
                        JOptionPane.CANCEL_OPTION, JOptionPane.CLOSED_OPTION -> processCancel()
                    }
                }
            }
        }
    }

    private fun processCancel() {
        userResult = null
        isVisible = false
    }

    private fun processOk(selectedValue: String?) {
        if (selectedValue != null) {
            userResult = selectedValue
            isVisible = false
        } else {
            JOptionPane.showMessageDialog(this, "You must select an option to remove", "Invalid input", JOptionPane.ERROR_MESSAGE)
            listBox.requestFocusInWindow()
        }
    }
}
