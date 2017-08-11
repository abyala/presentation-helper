package com.abyala.presentationhelper.ui.components

import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.beans.PropertyChangeEvent
import java.beans.PropertyChangeListener
import javax.swing.*

fun showFormattedInputDialog(frame: JFrame,
                             dialogTitle: String,
                             question: String,
                             validator: ((String?) -> Boolean),
                             inputFieldLength : Int = 10
) : String? {
    val dialog = FormattedInputDialog(frame, dialogTitle, question, validator, inputFieldLength)
    return dialog.userResult
}

private class FormattedInputDialog(frame: JFrame,
                                   dialogTitle: String,
                                   question: String,
                                   private val validator: ((String) -> Boolean),
                                   inputFieldLength : Int = 10
) : JDialog(frame, dialogTitle, true), ActionListener, PropertyChangeListener {

    private val questionLabel = JLabel(question)
    private val inputField = JTextField(inputFieldLength)
    private val optionPane : JOptionPane
    var userResult : String? = null
        private set

    init {
        optionPane = JOptionPane(arrayOf(questionLabel, inputField), JOptionPane.QUESTION_MESSAGE, JOptionPane.OK_CANCEL_OPTION)
        contentPane = optionPane
        optionPane.addPropertyChangeListener(this)
        inputField.addPropertyChangeListener(this)
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
                        JOptionPane.OK_OPTION -> processOk()
                        JOptionPane.CANCEL_OPTION -> processCancel()
                        JOptionPane.CLOSED_OPTION -> processCancel()
                    }
                }
            }
        }
    }

    private fun processCancel() {
        userResult = null
        isVisible = false
    }

    private fun processOk() {
        val inputText = inputField.text

        if (validator.invoke(inputText)) {
            userResult = inputText
            isVisible = false
        } else {
            JOptionPane.showMessageDialog(this, "Invalid input.  Please try again", "Invalid input", JOptionPane.ERROR_MESSAGE)
            inputField.requestFocusInWindow()
        }
    }
}
