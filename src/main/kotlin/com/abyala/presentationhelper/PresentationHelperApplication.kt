package com.abyala.presentationhelper

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import javax.swing.UIManager

@SpringBootApplication
class PresentationHelperApplication

fun main(args: Array<String>) {
    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
    SpringApplicationBuilder(PresentationHelperApplication::class.java).headless(false).run(*args)
}
