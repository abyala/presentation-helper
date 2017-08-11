package com.abyala.presentationhelper.ui

import com.abyala.presentationhelper.ui.components.CountdownLabel
import com.abyala.presentationhelper.ui.components.RotatingButton
import org.springframework.stereotype.Component
import java.awt.Color
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.imageio.ImageIO
import javax.swing.*

fun toImageIcon(resourceName: String) : ImageIcon =ImageIcon(ImageIO.read(MainScreen::class.java.classLoader.getResourceAsStream(resourceName)))

@Component
class MainScreen {
    private val frame = JFrame("")

    init {
        frame.isAlwaysOnTop = true
        frame.isUndecorated = true
        frame.isLocationByPlatform = true  // are we sure?

        val box = Box.createHorizontalBox()
        box.border = BorderFactory.createLineBorder(Color.decode("0xF0F0F0"), 3)
        box.add(RotatingButton(frame)) //, listOf("Kotlin", "Spring Boot", "Spring Data")))
        box.add(CountdownLabel(frame))
        val closeIcon = JLabel(toImageIcon("images/closeButton3.jpg"))
        closeIcon.requestFocusInWindow()
        closeIcon.addMouseListener(CloseIconClickedListener(frame))
        box.add(closeIcon)

        frame.add(box)
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE

        // Prepare to show
        frame.invalidate()
        frame.pack()
        frame.setBounds(50, 0, frame.width, frame.height)
        frame.isVisible = true
    }
}

private class CloseIconClickedListener(val frame: JFrame) : MouseAdapter() {
    override fun mouseClicked(e: MouseEvent?) {
        if (JOptionPane.showConfirmDialog(frame, "Are you sure you want to shut down Presentation Helper?", "", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            SwingUtilities.invokeLater {
                frame.dispose()
            }
        }
    }
}
