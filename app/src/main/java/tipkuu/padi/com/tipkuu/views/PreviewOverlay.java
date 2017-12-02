package tipkuu.padi.com.tipkuu.views;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import tipkuu.padi.com.tipkuu.R;
import tipkuu.padi.com.tipkuu.ScanCode2Activity;

public class PreviewOverlay extends View {
    private static final String TAG = PreviewOverlay.class.getName();
    private static final long HIT_DELAY = 700L;
    private final Paint paint;
    private final int frameColor;
    private final float frameMargin;
    private final float lineThickness;
    private final float verticalGuideLength;
    private final float captureRectHeight;
    private final Paint rectPaint;
    private final Paint textPaint;
    private final Resources resources;
    private final String targetText;
    private final Paint targetTextColor;
    private final float barMargin;
    private final Rect textBoundsRect;
    private final Rect barTextBoundsRect;

    ArrayList<Pair<String,Rect>> codeBounds = new ArrayList<Pair<String,Rect>>();
    private int targetLeft;
    private int targetTop;
    private int targetBottom;
    private int targetRight;
    private Rect hotSpot;
    String candidate;
    long timestamp;

    public PreviewOverlay(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        resources = getResources();
        frameColor = resources.getColor(R.color.light_blue);
        frameMargin = getResources().getDimension(R.dimen.finder_frame_margin);
        lineThickness = getResources().getDimension(R.dimen.finder_frame_thickness);
        verticalGuideLength = getResources().getDimension(R.dimen.finder_vertical_guide_length);
        captureRectHeight = getResources().getDimension(R.dimen.finder_capture_rect_height);

        targetText = resources.getString(R.string.place_barcode_inside_box);
        targetTextColor = new Paint();
        targetTextColor.setAntiAlias(true);
        targetTextColor.setTextSize(getResources().getDimension(R.dimen.bar_font_size));
        targetTextColor.setColor(Color.GREEN);
        textBoundsRect = new Rect();
        targetTextColor.getTextBounds(targetText, 0, targetText.length(), textBoundsRect);



        rectPaint = new Paint();
        rectPaint.setAntiAlias(true);
        rectPaint.setStrokeWidth(getResources().getDimension(R.dimen.target_line_width));
        rectPaint.setStyle(Paint.Style.STROKE);
        rectPaint.setColor(Color.RED);

        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(getResources().getDimension(R.dimen.bar_font_size));
        textPaint.setStyle(Paint.Style.STROKE);
        textPaint.setColor(Color.RED);
        String measureStr = "0123456789";

        barTextBoundsRect = new Rect();
        textPaint.getTextBounds(measureStr, 0, measureStr.length(), barTextBoundsRect);


        barMargin = getResources().getDimension(R.dimen.standard_margin) + getResources().getDimension(R.dimen.target_line_width);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        targetLeft = (int) frameMargin;
        targetTop = (int) h  /2 - (int)captureRectHeight / 2;
        targetBottom = (int) targetTop + (int)captureRectHeight;
        targetRight = w - (int) frameMargin;

        hotSpot = new Rect(targetLeft, targetTop, targetRight, targetBottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        paint.setColor(frameColor);

        canvas.drawRect(targetLeft, targetTop, targetRight, targetTop + lineThickness, paint);
        canvas.drawRect(targetLeft, targetTop + lineThickness, targetLeft + lineThickness, targetTop + verticalGuideLength, paint);
        canvas.drawRect(targetLeft, targetBottom - verticalGuideLength, targetLeft + lineThickness, targetBottom, paint);
        canvas.drawRect(targetRight - lineThickness, targetTop + lineThickness, targetRight, targetTop + verticalGuideLength, paint);
        canvas.drawRect(targetRight - lineThickness, targetBottom - verticalGuideLength, targetRight, targetBottom, paint);

        canvas.drawRect(targetLeft, targetBottom - lineThickness, targetRight, targetBottom, paint);
        canvas.drawText(targetText, targetLeft, targetBottom + lineThickness + barMargin + textBoundsRect.bottom, targetTextColor);
        for (Pair<String,Rect> p : codeBounds) {
            canvas.drawRect(p.second.left, p.second.top, p.second.right, p.second.bottom, rectPaint);
            canvas.drawText(p.first,p.second.left, p.second.bottom + barMargin + barTextBoundsRect.bottom, textPaint);
        }
    }

    public synchronized String clearAndAddBounds(List<Pair<String, ScanCode2Activity.CodeInfo>> boundsList) {
        codeBounds.clear();
        String candidateBarcode = null;
        for(Pair<String, ScanCode2Activity.CodeInfo> e : boundsList) {
            if (addBounds(e.first, e.second.getBounds(), e.second.getLabel())) {
                candidateBarcode = e.first;
            }
        }
        return candidateBarcode;
    }

    public synchronized List<Pair<String,Rect>> getCodeBounds() {
        ArrayList<Pair<String,Rect>> codeBoundsCopy = new ArrayList<>();
        for(Pair<String,Rect> item : codeBounds) {
            codeBoundsCopy.add(item);
        }
        return codeBoundsCopy;
    }

    public boolean addBounds(String code, int[] bounds, String label) {
        Rect rect = new Rect();
        rect.left = bounds[0];
        rect.top = bounds[1];
        rect.right = bounds[0] + bounds[2];
        rect.bottom = bounds[1] + bounds[3];

        if (label!=null) {
            code = label;
        }

        codeBounds.add(new Pair<String, Rect>(code, rect));

        if (hotSpot!=null) {
//            Log.d(TAG, "bound left " + rect.left + " " + rect.top + " " + rect.right + " " + rect.bottom);
            if (hotSpot.contains(rect)) {
                if (candidate == null || !candidate.equals(code)) {
                    this.candidate = code;
                    this.timestamp = SystemClock.elapsedRealtime();
                } else
                if (candidate.equals(code)) {
                    long elapsed = SystemClock.elapsedRealtime() - this.timestamp;
                    if (elapsed > HIT_DELAY) {
                        candidate = null;
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public synchronized void clearBounds() {
        codeBounds.clear();
    }
}
