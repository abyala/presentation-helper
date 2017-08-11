package com.abyala.presentationhelper.ui.components

import java.awt.Color
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.time.Duration
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.SwingUtilities
import javax.swing.border.EmptyBorder
import kotlin.concurrent.fixedRateTimer


class CountdownLabel(owner: JFrame) : JLabel() {
    companion object {
        private const val ZERO = "00:00"
        private val ORANGE_READABLE = Color.decode("0xFF9600")
        private val ZERO_TIME_REMAINING = TimeRemaining(ZERO, Color.BLACK)
        private val TIME_FORMAT_MINUTES_SECONDS = DateTimeFormatter.ofPattern("m:ss")
        private val TIME_FORMAT_HOURS_MINUTES_SECONDS = DateTimeFormatter.ofPattern("h:mm:ss")

        private data class TimeRemaining(val text: String, val color: Color)

        private fun getTimeRemaining(alarmTime: LocalTime?, lastUpdateTime: LocalTime?) : TimeRemaining {
            if (alarmTime == null) {
                return ZERO_TIME_REMAINING
            }

            val secondsTotal = Duration.between(lastUpdateTime, alarmTime).seconds + 1
            val timeRemaining = Duration.between(LocalTime.now(), alarmTime)
            val secondsRemaining = timeRemaining.seconds
            val percentRemaining = secondsRemaining.toDouble() / secondsTotal
            val color = when {
                percentRemaining <= 0.0 || percentRemaining > .2 -> Color.BLACK
                percentRemaining <= .1 -> Color.RED
                else -> ORANGE_READABLE
            }

            return when {
                secondsRemaining <= 0 -> ZERO_TIME_REMAINING
                secondsRemaining < 60 * 60 -> TimeRemaining(formatDurationAsTime(TIME_FORMAT_MINUTES_SECONDS, timeRemaining), color)
                else -> TimeRemaining(formatDurationAsTime(TIME_FORMAT_HOURS_MINUTES_SECONDS, timeRemaining), color)
            }
        }

        private fun formatDurationAsTime(format: DateTimeFormatter, duration: Duration) =
                format.format(LocalTime.MIDNIGHT.plus(duration))
    }

    private var alarmTime: LocalTime? = null            // TODO: Change to LocalDateTime
    private var lastRequestTime: LocalTime? = null
    private var timer: Timer? = null

    init {
        border = EmptyBorder(10, 10, 10, 10)
        updateTime()
        addMouseListener(MyMouseAdapter(this, owner))
    }

    private fun createTimer() = fixedRateTimer("CountdownLabel", true, 0L, 1000L, { updateTime() })

    @Synchronized private fun updateTime() {
//        val DELETE_ME = getTimeRemaining(alarmTime)
        val timeRemaining = getTimeRemaining(alarmTime, lastRequestTime)
        SwingUtilities.invokeLater {
            text = timeRemaining.text
            foreground = timeRemaining.color
        }

        if (timeRemaining == ZERO_TIME_REMAINING) {
            timer?.cancel()
            timer = null
        } else if (timer == null) {
            timer = createTimer()
        }
    }

    @Synchronized fun resetTime(newAlarmTime: LocalTime) {
        alarmTime = newAlarmTime
        lastRequestTime = LocalTime.now()
        updateTime()
    }
}

class MyMouseAdapter(val countdownLabel: CountdownLabel, val owner: JFrame) : MouseAdapter() {
    override fun mousePressed(e: MouseEvent?) {
        doPopup()
    }

    override fun mouseReleased(e: MouseEvent?) {
        doPopup()
    }

    private fun doPopup() {
        val minutes: Int? = toMinutes(showFormattedInputDialog(owner, "New countdown time", "Enter the number of minutes to count down", { isValidMinute(it) }, 10))
        minutes?.let {
            countdownLabel.resetTime(LocalTime.now().plusMinutes(it.toLong()))
        }
    }

    private fun isValidMinute(value: String?): Boolean {
        return toMinutes(value)?.let { it >= 0 } ?: false
    }

    private fun toMinutes(value: String?): Int? {
        return try {
            if (value == null) null else Integer.parseInt(value)
        } catch (e: NumberFormatException) {
            null
        }
    }
}
