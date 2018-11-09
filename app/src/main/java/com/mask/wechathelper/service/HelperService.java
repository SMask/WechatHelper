package com.mask.wechathelper.service;

import android.accessibilityservice.AccessibilityService;
import android.os.Handler;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

/**
 * 无障碍服务类
 * Created by lishilin on 2018/11/9.
 */
public class HelperService extends AccessibilityService {

    public static final String TAG = HelperService.class.toString();

    private Handler handler = new Handler();

    @Override
    public void onInterrupt() {

    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        int eventType = event.getEventType();
        CharSequence className = event.getClassName();
        Log.d(TAG, event.toString());
        switch (eventType) {
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                if ("com.tencent.mm.ui.LauncherUI".contentEquals(className)) {
                    openGroup();
                }
                break;
        }
    }

    /**
     * 打开群聊
     */
    private void openGroup() {
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo == null) {
            return;
        }
        List<AccessibilityNodeInfo> nodeInfoList = nodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/cw2");
        for (AccessibilityNodeInfo info : nodeInfoList) {
            if (!"通讯录".equals(info.getText().toString())) {
                continue;
            }
            info.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
                    if (nodeInfo == null) {
                        return;
                    }
                    List<AccessibilityNodeInfo> nodeInfoList = nodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/lv");
                    for (AccessibilityNodeInfo info : nodeInfoList) {
                        if (!"群聊".equals(info.getText().toString())) {
                            continue;
                        }
                        info.getParent().getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        break;
                    }
                }
            }, 500);
            break;
        }
    }

}
