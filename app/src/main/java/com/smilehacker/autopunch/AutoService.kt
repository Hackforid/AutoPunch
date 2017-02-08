package com.smilehacker.autopunch

import android.accessibilityservice.AccessibilityService
import android.app.Notification
import android.os.CountDownTimer
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo


/**
 * Created by kleist on 2017/2/8.
 */
class AutoService : AccessibilityService() {
    val TAG = AutoService::class.java.simpleName
    override fun onInterrupt() {
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (!Prefs.enable) {
            return
        }
        event ?: return
        //Log.i(TAG, "event = $event pkg=${event.packageName}")
        when(event.eventType) {
            //AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED -> handleNotification(event)
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED, AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED  -> handleWindow(event)
        }
    }

    private fun handleNotification(event: AccessibilityEvent) {
        val texts = event.text
        texts.forEach {
            val content = it.toString()
            if (content.contains("[红包]")) {
                if (event.parcelableData != null && event.parcelableData is Notification) {
                    val notification = event.parcelableData as Notification
                    val pendingIntent = notification.contentIntent
                    try {
                        pendingIntent.send()
                    } catch (e : Exception) {
                        Log.e(TAG, "open notification error", e)
                    }
                }
            }
        }
    }

    private fun handleWindow(event: AccessibilityEvent) {
        val className = event.className.toString()
//        if (className == "com.tencent.wework.msg.controller.MessageListActivity") {
//            findPacket()
//        } else if (className == "com.tencent.wework.enterprise.redenvelopes.controller.RedEnvelopeCollectorActivity") {
//            openPacket()
//        } else if (className == "com.tencent.wework.enterprise.redenvelopes.controller.RedEnvelopeDetailActivity") {
//            closePacket()
//        }
        when(className) {
//            "com.tencent.wework.launch.WwMainActivity" -> jumpToWorkStation()
//            "com.tencent.wework.setting.controller.EnterpriseAppActivity" -> jumpToAttendance()
            "com.tencent.wework.enterprise.attendance.controller.AttendanceActivity2" -> checkPunch()
        }

    }

    private fun jumpToWorkStation() {
        Log.i(TAG, "jumpToWorkStation")
        val root = rootInActiveWindow
        val workStationNode = findNodeByText(root, "工作台")

        if (workStationNode == null) {
            Log.i(TAG, "open mine tab")
            val mineTabNode = findNodeByText(root, "我") ?: return
            clickNode(mineTabNode)
            mineTabNode.recycle()
            jumpToWorkStation()
        } else {
            Log.i(TAG, "click to goto workstation")
            clickNode(workStationNode)
        }

        workStationNode?.recycle()
        root?.recycle()
    }

    private fun jumpToAttendance() {
        val node = findNodeByText(rootInActiveWindow, "考勤") ?: return
        clickNode(node)
        node.recycle()
    }

    private fun checkPunch() {
        Log.i(TAG, "checkPunck")
        val countDownTimer = object : CountDownTimer(10000, 1000) {
            override fun onFinish() {
            }

            override fun onTick(millisUntilFinished: Long) {
                val node = findNodeByText(rootInActiveWindow, "你已在打卡范围内")
                if (node != null) {
                    Log.i(TAG, "checkPunck done")
                    this.cancel()
                    doPunch()
                    node.recycle()
                }
            }
        }
        countDownTimer.start()
    }

    private fun doPunch() {
        Log.i(TAG, "doPunch")
        val node = findNodeByText(rootInActiveWindow, "下班打卡") ?: return
        Log.i(TAG, "click Punch")
        clickNode(node)
        node.recycle()
    }

    private fun findNodeByText(node: AccessibilityNodeInfo, text: String) : AccessibilityNodeInfo? {
        if (node.childCount == 0) {
            if (node.text?.toString() == text) {
                return node
            }
        } else {
            for (i in 0..node.childCount-1) {
                val child = node.getChild(i) ?: continue
                val _node = findNodeByText(child, text)
                if (_node != null) {
                    return _node
                }
            }
        }

        return null
    }

    private fun clickNode(node: AccessibilityNodeInfo) {
        var parent = node
        while(!parent.isClickable) {
            parent = parent.parent ?: break
        }
        parent?.performAction(AccessibilityNodeInfo.ACTION_CLICK)
    }


    private fun findPacket() {
        val node = findPacketNode(rootInActiveWindow) ?: return

        var parent = node
        while(!parent.isClickable) {
            parent = parent.parent ?: break
        }
        Log.i(TAG, "${parent.toString()} ${parent.hashCode()}")
        parent?.performAction(AccessibilityNodeInfo.ACTION_CLICK)
    }

    private fun findPacketNode(node: AccessibilityNodeInfo) : AccessibilityNodeInfo? {
        if (node.childCount == 0) {
            if (node.text?.toString() == "领取红包") {
                return node
            }
        } else {
            for (i in 0..node.childCount-1) {
                val child = node.getChild(i) ?: continue
                val _node = findPacketNode(child)
                if (_node != null) {
                    return _node
                }
            }
        }

        return null
    }


    private fun openPacket() {
        Log.i(TAG, "openPacket")
        val list = rootInActiveWindow?.findAccessibilityNodeInfosByViewId("com.tencent.wework:id/aoi")
        list?.forEach {
            Log.i(TAG, "node ${it.className} clickable=${it.isClickable}")
            it?.performAction(AccessibilityNodeInfo.ACTION_CLICK) }
    }

    private fun closePacket() {
        Log.i(TAG, "closePacket")
        performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK)
    }
}
