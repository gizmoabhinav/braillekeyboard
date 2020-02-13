package com.example.android.braillekeyboard;

import android.content.Context;
import android.inputmethodservice.InputMethodService;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.util.HashMap;


public class ImageKeyboard extends InputMethodService {

    private View[] keys = new View[8];
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
        charMap.put(128, " ");
        charMap.put(16, "a");
        charMap.put(48, "b");
        charMap.put(24, "c");
        charMap.put(28, "d");
        charMap.put(20, "e");
        charMap.put(56, "f");
        charMap.put(60, "g");
        charMap.put(52, "h");
        charMap.put(40, "i");
        charMap.put(44, "j");
        charMap.put(80, "k");
        charMap.put(112, "l");
        charMap.put(88, "m");
        charMap.put(92, "n");
        charMap.put(84, "o");
        charMap.put(120, "p");
        charMap.put(124, "q");
        charMap.put(116, "r");
        charMap.put(104, "s");
        charMap.put(108, "t");
        charMap.put(82, "u");
        charMap.put(114, "v");
        charMap.put(46, "w");
        charMap.put(90, "x");
        charMap.put(94, "y");
        charMap.put(86, "z");
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
//        layout.removeAllViews();
        for (View btn : keys) {
            layout.removeView(btn);
        }
        numberOfKeys = 0;
        layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (numberOfKeys >= 8) {
                    return false;
                }

                // TODO Auto-generated method stub
                float x = event.getX();
                float y = event.getY();

                LayoutInflater vi = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View btn = vi.inflate(R.layout.button_view, null);

                //btn.setText(String.valueOf(numberOfKeys+1));
                btn.setTag(String.valueOf(numberOfKeys + 1));
                btn.findViewById(R.id.circleButton).setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        Vibrator vb = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                        vb.vibrate(50);
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                            updateCurrent(1 << (8 - Integer.parseInt(btn.getTag().toString())));
                            v.setPressed(true);
                        } else if (event.getAction() == MotionEvent.ACTION_UP) {
                            updateCurrent(0 - (1 << (8 - Integer.parseInt(btn.getTag().toString()))));
                            v.setPressed(false);
                        }

                        return true;
                    }
                });
                keys[numberOfKeys] = btn;

                ((TextView) btn.findViewById(R.id.circleButtonText)).setText("" + (numberOfKeys + 1));
                btn.setX(x - 150);
                btn.setY(y - 150);
                layout.addView(btn);
                numberOfKeys++;
                Vibrator vb = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                vb.vibrate(100);
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
                    inputConnection.commitText(charMap.get(currentChar), 1);
                }
                if(currentChar == 1) {
                    inputConnection.deleteSurroundingText(1, 0);
                }
            }
        };
        timer.start();
    }
}
