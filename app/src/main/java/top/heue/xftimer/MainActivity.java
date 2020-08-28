package top.heue.xftimer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View.OnTouchListener;
import android.view.MotionEvent;
import android.view.View.OnClickListener;

public class MainActivity extends Activity { 
    private static long startTime;
    private static long nextTime;

    private static Boolean isStart = false;

    private static WindowManager windowManager;
	private static WindowManager.LayoutParams layoutParams;
    private static TextView txt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!isStart) {
            if (Settings.canDrawOverlays(this)) {
                txt = new TextView(this);
                txt.setTextSize(25.0f);
                txt.setBackgroundColor(0xFFFFFFFF);
                //txt.setTextColor(0x000000);
                txt.setText("000");
                txt.setOnTouchListener(new OnTouchListener() {
                        private int x;
                        private int y;
                        @Override
                        public boolean onTouch(View view, MotionEvent event) {
                            switch (event.getAction()) {
                                case MotionEvent.ACTION_DOWN:
                                    x = (int) event.getRawX();
                                    y = (int) event.getRawY();
                                    break;
                                case MotionEvent.ACTION_MOVE:
                                    int nowX = (int) event.getRawX();
                                    int nowY = (int) event.getRawY();
                                    int movedX = nowX - x;
                                    int movedY = nowY - y;
                                    x = nowX;
                                    y = nowY;
                                    layoutParams.x = layoutParams.x + movedX;
                                    layoutParams.y = layoutParams.y + movedY;
                                    // 更新悬浮窗控件布局
                                    windowManager.updateViewLayout(view, layoutParams);
                                    break;
                                default:
                                    break;
                            }
                            return false;
                        }
                    });

                txt.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View p1) {
                            if (isStart) {
                                handler.removeMessages(1);
                                isStart = false;
                            } else {
                                //startTime = System.currentTimeMillis();
                                startTime = 0;
                                handler.sendEmptyMessage(1);
                                isStart = true;
                            }
                        }
                    });

                windowManager = (WindowManager)this.getSystemService(Context.WINDOW_SERVICE);
                int screenWidth = 0, screenHeight = 0;
                if (windowManager != null) {
                    //获取屏幕的宽和高
                    Point point = new Point();
                    windowManager.getDefaultDisplay().getSize(point);
                    screenWidth = point.x;
                    screenHeight = point.y;
                    layoutParams = new WindowManager.LayoutParams();
                    layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
                    layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
                    //layoutParams.width = 200;
                    //layoutParams.height = 200;
                    //设置type
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        //26及以上必须使用TYPE_APPLICATION_OVERLAY   @deprecated TYPE_PHONE
                        layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
                    } else {
                        layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
                    }
                    //设置flags
                    layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED;
                    layoutParams.gravity = Gravity.START | Gravity.TOP;
                    //背景设置成透明
                    layoutParams.format = PixelFormat.TRANSPARENT;
                    layoutParams.x = screenWidth / 2;
                    layoutParams.y = screenHeight / 2;
                    //将View添加到屏幕上
                    windowManager.addView(txt, layoutParams);
                    
                    //startTime = System.currentTimeMillis();
                    startTime = 0;
                    handler.sendEmptyMessage(1);
                    isStart = true;
                    
                    Intent home = new Intent(Intent.ACTION_MAIN);
                    home.addCategory(Intent.CATEGORY_HOME);
                    startActivity(home);
                    
                }	
            } else {
                Toast.makeText(this, "需要悬浮窗权限", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivity(intent);
                finish();
            }
        }

        
    }

    private static Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                handler.sendEmptyMessageDelayed(1, 1);
                txt.setText(String.valueOf(startTime));
                startTime++;
            }/*
            if (msg.what == 1) {
                handler.sendEmptyMessageDelayed(1, 1);
                nextTime = System.currentTimeMillis();
                nextTime = nextTime - startTime;
                txt.setText(String.valueOf(nextTime));
                windowManager.updateViewLayout(txt, layoutParams);
            }*/
        }
    };
} 
