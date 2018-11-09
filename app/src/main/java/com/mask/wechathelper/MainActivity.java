package com.mask.wechathelper;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.TextView;

import com.mask.wechathelper.service.HelperService;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView tv_state;
    private View btn_check;
    private View btn_open_setting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        setListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        doCheckState();
    }

    private void initView() {
        tv_state = findViewById(R.id.tv_state);
        btn_check = findViewById(R.id.btn_check);
        btn_open_setting = findViewById(R.id.btn_open_setting);
    }

    private void setListener() {
        btn_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doCheckState();
            }
        });
        btn_open_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                startActivity(intent);
            }
        });
    }

    /**
     * 检查服务状态
     */
    private void doCheckState() {
        String packageName = getPackageName();
        String serviceName = HelperService.class.getSimpleName();

        AccessibilityManager accessibilityManager = (AccessibilityManager) getSystemService(Context.ACCESSIBILITY_SERVICE);
        List<AccessibilityServiceInfo> serviceList = accessibilityManager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC);
        boolean isEnable = false;
        for (AccessibilityServiceInfo info : serviceList) {
            String id = info.getId();
            if (id.contains(packageName) && id.contains(serviceName)) {
                isEnable = true;
                break;
            }
        }
        tv_state.setText(isEnable ? R.string.state_success : R.string.state_fail);
    }

}
