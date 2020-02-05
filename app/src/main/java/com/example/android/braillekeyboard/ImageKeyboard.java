package com.example.android.braillekeyboard;

import android.inputmethodservice.InputMethodService;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.Button;
import android.widget.RelativeLayout;
import java.io.File;
import java.util.HashMap;


public class ImageKeyboard extends InputMethodService {

    private Button[] keys = new Button[8];
    HashMap<Integer, String> charMap = new HashMap<>();
    private RelativeLayout layout;
    private int currentChar;
    private CountDownTimer timer;
    private InputConnection inputConnection;
    private int numberOfKeys = 0;

    @Override
    public void onCreate() {
        super.onCreate();

        // TODO: Avoid file I/O in the main thread.
        final File imagesDir = new File(getFilesDir(), "images");
        imagesDir.mkdirs();
        charMap.put(16,"a");
        charMap.put(48,"b");
        charMap.put(24,"c");
        charMap.put(28,"d");
        charMap.put(20,"e");
        charMap.put(56,"f");
        charMap.put(60,"g");
        charMap.put(52,"h");
        charMap.put(40,"i");
        charMap.put(44,"j");
        charMap.put(80,"k");
        charMap.put(112,"l");
        charMap.put(88,"m");
        charMap.put(92,"n");
        charMap.put(84,"o");
        charMap.put(120,"p");
        charMap.put(124,"q");
        charMap.put(116,"r");
        charMap.put(104,"s");
        charMap.put(108,"t");
        charMap.put(82,"u");
        charMap.put(114,"v");
        charMap.put(46,"w");
        charMap.put(90,"x");
        charMap.put(94,"y");
        charMap.put(86,"z");
    }

    @Override
    public View onCreateInputView() {
        LayoutInflater li = LayoutInflater.from(this);
        layout = (RelativeLayout) li.inflate(R.layout.input_view, null);
        return layout;
    }

    @Override
    public boolean onEvaluateFullscreenMode() {
        // In full-screen mode the inserted content is likely to be hidden by the IME. Hence in this
        // sample we simply disable full-screen mode.
        return false;
    }

    @Override
    public void onStartInputView(EditorInfo info, boolean restarting) {

        inputConnection = getCurrentInputConnection();
        //layout.removeAllViews();
        for (Button btn : keys) {
            layout.removeView(btn);
        }
        numberOfKeys = 0;
        layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (numberOfKeys>=8) {
                    return false;
                }
                // TODO Auto-generated method stub
                float x = event.getX();
                float y = event.getY();

                final Button btn = new Button(getApplicationContext());
                btn.setText(String.valueOf(numberOfKeys+1));
                btn.setTag(String.valueOf(numberOfKeys+1));
                btn.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (event.getAction() == MotionEvent.ACTION_DOWN ) {
                            updateCurrent(1<<(8-Integer.parseInt(btn.getTag().toString())));
                        } else if (event.getAction() == MotionEvent.ACTION_UP ) {
                            updateCurrent(0-(1<<(8-Integer.parseInt(btn.getTag().toString()))));
                        }
                        return false;
                    }
                });
                keys[numberOfKeys] = btn;

                //btn.setText((x-v.getLeft())+" "+(y-v.getTop()));
                RelativeLayout.LayoutParams bp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                bp.leftMargin = (int) x-v.getLeft()-10;
                bp.topMargin = (int) y-v.getTop()-5;
                keys[numberOfKeys].setLayoutParams(bp);
                layout.addView(keys[numberOfKeys]);
                numberOfKeys++;
                return false;
            }
        });
    }

    private void updateCurrent(int delta) {
        if (timer != null) {
            timer.cancel();
        }
        //inputConnection.commitText(String.valueOf(delta),String.valueOf(delta).length());
        currentChar += delta;
        if (delta < 0) {
            return;
        }
        timer = new CountDownTimer(150, 200) {
            @Override
            public void onTick(long millisUntilFinished) {
                // do nothing
            }

            @Override
            public void onFinish() {
                if (charMap.containsKey(currentChar)) {
                    inputConnection.commitText(charMap.get(currentChar),1);
                }
            }
        };
        timer.start();
    }
}