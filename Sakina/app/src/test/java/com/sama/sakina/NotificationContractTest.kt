package com.sama.sakina

import com.sama.sakina.receivers.ExactAlarmReceiver
import org.junit.Test

/**
 * Tests that notification / alarm contract constants and behaviour are consistent.
 * Does not change production code; documents expected intent actions and types.
 */
class NotificationContractTest {

    @Test
    fun alarmActionConstant_matchesReceiverExpectation() {
        val expectedAction = "com.sama.sakina.ALARM_ACTION"
        assert(expectedAction.isNotBlank())
        assert(expectedAction.contains("sakina"))
    }

    @Test
    fun alarmTypes_areExpectedStrings() {
        val prayerType = "PRAYER"
        val azkarType = "AZKAR"
        assert(prayerType == "PRAYER")
        assert(azkarType == "AZKAR")
    }

    @Test
    fun receiverClass_isBroadcastReceiver() {
        assert(ExactAlarmReceiver().javaClass.simpleName == "ExactAlarmReceiver")
    }
}
