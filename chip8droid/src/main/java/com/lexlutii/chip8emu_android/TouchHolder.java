package com.lexlutii.chip8emu_android;

import android.graphics.PointF;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class TouchHolder implements View.OnTouchListener{

    private ArrayList<OnActiveTouchListener> listeners = new ArrayList<OnActiveTouchListener>();

    public interface OnActiveTouchListener{
        public void onActiveTouch(List<PointF> fingers);
    }

    public void addListener(OnActiveTouchListener listener) {
        listeners.add(listener);
    }

    public void removeListener(OnActiveTouchListener listener) {
        listeners.remove(listener);
    }

    private void fireListeners(List<PointF> fingers) {
        for(OnActiveTouchListener listener : listeners) {
            listener.onActiveTouch(fingers);
        }
    }
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        boolean inTouch = true;
        int upPI = -1;
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_UP: // прерывание последнего касания
                inTouch = false;
            case MotionEvent.ACTION_POINTER_UP: // прерывания касаний
                upPI = event.getActionIndex();
                break;
        }
        List<PointF> fingers = new ArrayList<PointF>();
        // число касаний
        int pointerCount = event.getPointerCount();
        boolean currentPressed = false;
        for (int j = 0; j < pointerCount; j++) {
            if (j != upPI)
                fingers.add(new PointF(event.getX(j), event.getY(j)));
        }
        fireListeners(fingers);
        return true;
    }
}
