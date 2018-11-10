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

    private static final long WAIT_TIME = 200;// 操作间隔时间

    private static final String GROUP_NAME = "测试";// 要加入的群名称

    private static final int MAX_COUNT = 2;// 单次最大邀请人数

    private Handler handler = new Handler();

    @Override
    public void onInterrupt() {
        Log.d(TAG, "onInterrupt");
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        int eventType = event.getEventType();
        String className = event.getClassName().toString();
        Log.d(TAG, event.toString());
        switch (eventType) {
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
//                if ("com.tencent.mm.ui.widget.a.c".equals(className)) {// Dialog
//                    dialogClick();
//                }
//                if ("com.tencent.mm.ui.base.p".equals(className)) {// Dialog
//                    dialogClick();
//                }
                dialogClick();

                switch (className) {
                    case "com.tencent.mm.ui.LauncherUI":// 微信首页
                        openGroup();
                        break;
                    case "com.tencent.mm.ui.contact.ChatroomContactUI":// 群聊列表页面
                        searchGroup();
                        break;
                    case "com.tencent.mm.ui.chatting.ChattingUI":// 聊天页面
                        openGroupSetting();
                        break;
                    case "com.tencent.mm.chatroom.ui.ChatroomInfoUI":// 群信息页面
                        openSelectContact();
                        break;
                    case "com.tencent.mm.ui.contact.SelectContactUI":// 选择联系人页面
                        addMembers();
                        break;
                }
                break;
        }
    }

    /**
     * 点击返回
     */
    private void performBackClick() {
        performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
    }

    /**
     * 对话框自动点击
     */
    private void dialogClick() {
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo == null) {
            return;
        }
        List<AccessibilityNodeInfo> nodeInfoList = nodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/cvo");
        if (nodeInfoList == null || nodeInfoList.size() <= 0) {
            return;
        }
        String description = nodeInfoList.get(0).getText().toString();
        boolean isFrequently = description.contains("频繁") && description.contains("稍后再试");

        nodeInfoList = nodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/au_");
        if (nodeInfoList == null || nodeInfoList.size() <= 0) {
            return;
        }
        nodeInfoList.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);

        if (isFrequently) {
            // 主要是为了处理点击对话框后卡在群信息页面没反应的问题
            try {
                Thread.sleep(WAIT_TIME);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            performBackClick();
        }

        try {
            Thread.sleep(WAIT_TIME);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 1.打开群聊
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
                        AccessibilityNodeInfo parent = info.getParent();
                        if (parent == null) {
                            return;
                        }
                        parent = parent.getParent();
                        if (parent == null) {
                            return;
                        }
                        parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        break;
                    }
                }
            }, WAIT_TIME);
            break;
        }
    }

    /**
     * 2.搜索群聊
     */
    private void searchGroup() {
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo == null) {
            return;
        }
        List<AccessibilityNodeInfo> nodeInfoList = nodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/m6");
        for (AccessibilityNodeInfo info : nodeInfoList) {
            if (!GROUP_NAME.equals(info.getText().toString())) {
                continue;
            }
            info.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
            break;
        }
    }

    /**
     * 3.打开群设置
     */
    private void openGroupSetting() {
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo == null) {
            return;
        }
        List<AccessibilityNodeInfo> nodeInfoList = nodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/j1");
        if (nodeInfoList == null || nodeInfoList.size() <= 0) {
            return;
        }
        nodeInfoList.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
    }

    /**
     * 4.点击添加按钮(找不到则滚动)，打开添加成员页面
     */
    private void openSelectContact() {
        while (true) {
            AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
            if (nodeInfo == null) {
                performBackClick();
                return;
            }

            List<AccessibilityNodeInfo> nodeInfoList = nodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/dnm");
            if (nodeInfoList == null || nodeInfoList.size() <= 0) {
                return;
            }
            for (AccessibilityNodeInfo info : nodeInfoList) {
                if (!"添加成员".equals(info.getContentDescription().toString())) {
                    continue;
                }
                info.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                return;
            }

            nodeInfoList = nodeInfo.findAccessibilityNodeInfosByViewId("android:id/list");
            if (nodeInfoList == null || nodeInfoList.size() <= 0) {
                return;
            }
            nodeInfoList.get(0).performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);

            try {
                Thread.sleep(WAIT_TIME);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 5.添加成员
     */
    private void addMembers() {
        selectedMember();

        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo == null) {
            return;
        }

        List<AccessibilityNodeInfo> nodeInfoList = nodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/j0");
        if (nodeInfoList == null || nodeInfoList.size() <= 0) {
            return;
        }
        nodeInfoList.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
    }

    /**
     * 选择成员
     */
    private void selectedMember() {
        String firstNameNow = null;// 当前 倒数第一人名字
        String secondNameNow = null;// 当前 倒数第二人名字
        String firstNameLast;// 上次 倒数第一人名字
        String secondNameLast;// 上次 倒数第二人名字

        int numNow = 0;// 当前数量
        do {

            firstNameLast = firstNameNow;
            secondNameLast = secondNameNow;

            AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
            if (nodeInfo == null) {
                return;
            }

            List<AccessibilityNodeInfo> checkBoxList = nodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/x8");
            if (checkBoxList == null || checkBoxList.size() <= 0) {
                return;
            }
            for (AccessibilityNodeInfo info : checkBoxList) {
                if (numNow >= MAX_COUNT) {
                    return;
                }
                if (!info.isChecked()) {
                    info.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    numNow++;
                }
            }
            if (numNow >= MAX_COUNT) {
                return;
            }

            List<AccessibilityNodeInfo> nameNodeInfoList = nodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/om");
            int size = nameNodeInfoList == null ? 0 : nameNodeInfoList.size();
            if (size >= 2) {
                firstNameNow = nameNodeInfoList.get(size - 1).getText().toString();
                secondNameNow = nameNodeInfoList.get(size - 2).getText().toString();
            }

            List<AccessibilityNodeInfo> listNodeInfoList = nodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/hc");
            if (listNodeInfoList == null || listNodeInfoList.size() <= 0) {
                return;
            }
            listNodeInfoList.get(0).performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);

            try {
                Thread.sleep(WAIT_TIME);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (equalsObject(firstNameNow, firstNameLast, true) && equalsObject(secondNameNow, secondNameLast, true)) {
                return;
            }
        }
        while (true);
    }

    /**
     * 比较两个Object是否相同
     *
     * @param obj_1           obj_1
     * @param obj_2           obj_2
     * @param isEqualsAllNull 全部为Null是否相同
     * @return 是否相同
     */
    public boolean equalsObject(Object obj_1, Object obj_2, boolean isEqualsAllNull) {
        if (obj_1 == null && obj_2 == null) {
            if (isEqualsAllNull) {
                return true;
            } else {
                return false;
            }
        }
        if (obj_1 != null) {
            return obj_1.equals(obj_2);
        } else {
            return false;
        }
    }

}
