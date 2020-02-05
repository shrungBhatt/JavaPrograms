package annotationUtils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.LinkedList;
import java.util.Objects;

import co.blitzz.showme.Activity_VideoCall;
import co.blitzz.showme.R;
import models.ReqRes_LivePointerPoints;
import utility.Constants;
import utility.LoggerConfig;

/**
 * Created by sc-147 on 04-Jul-18.
 */
public class DrawerViewForLiveCursorAnnotation extends View {

    private final String TAG = getClass().getSimpleName();
    private onAnnotation onAnnotationListener;
    private Float viewWidth, viewHeight;
    private Float xnew = 0f, ynew = 0f;
    private Float drawx = 0f, drawy = 0f;
    private float scale = getResources().getDisplayMetrics().density;
    private String identity = "", color = "";
    private Context mContext;

    public DrawerViewForLiveCursorAnnotation(Context context) {
        super(context);
        mContext = context;
    }

    public DrawerViewForLiveCursorAnnotation(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public DrawerViewForLiveCursorAnnotation(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    /**
     * Get bitmap off sets
     *
     * @param v             1
     * @param includeLayout 1
     * @return 1
     */
    public static int[] getBitmapOffset(View v, Boolean includeLayout) {
        int[] offset = new int[2];
        float[] values = new float[9];

        Matrix m = v.getMatrix();
        m.getValues(values);

        offset[0] = (int) values[5];
        offset[1] = (int) values[2];

        if (includeLayout) {
            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            int paddingTop = v.getPaddingTop();
            int paddingLeft = v.getPaddingLeft();

            offset[0] += paddingTop + lp.topMargin;
            offset[1] += paddingLeft + lp.leftMargin;
        }
        return offset;
    }

    /***
     * parse color and handle default color
     * @param strColor
     * @return
     */
    private int parseColor(String strColor) {
        if (!TextUtils.isEmpty(strColor)) {
            return Color.parseColor(strColor);
        }
        return Color.parseColor(Activity_VideoCall.getColorArrayList()[0]);
    }

    /**
     * This method creates the instance of Paint.
     * In addition, this method sets styles for Paint.
     *
     * @return paint
     */
    private Paint createCirclePaint(int color, boolean showShadow) {
        Paint paint = new Paint();
        float strokeWidth = 4f;
        paint.setStrokeWidth(strokeWidth * scale);
        paint.setAntiAlias(true);
        paint.setColor(color);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.MITER);
        if (showShadow) {
            paint.setShadowLayer(10, 0, 0, Color.parseColor("#000000"));
            setLayerType(LAYER_TYPE_SOFTWARE, paint);
        }
        return paint;
    }

    /***
     * draw opponent points on canvas
     * @param jsonObject
     */
    public void movePointer(JSONObject jsonObject) {

        float xpoint = 0f, ypoint = 0f, height = 0f, width = 0f;
        String identity = "";

        if (!TextUtils.isEmpty(jsonObject.optString("IDENTITY", getIdentity())))
            identity = jsonObject.optString("IDENTITY", getIdentity());
        if (!TextUtils.isEmpty(jsonObject.optString("XPOINTS", "0")))
            xpoint = Float.parseFloat(jsonObject.optString("XPOINTS", "0"));
        if (!TextUtils.isEmpty(jsonObject.optString("YPOINTS", "0")))
            ypoint = Float.parseFloat(jsonObject.optString("YPOINTS", "0"));
        if (!TextUtils.isEmpty(jsonObject.optString("HEIGHT", "0")))
            height = Float.parseFloat(jsonObject.optString("HEIGHT", "0"));
        if (!TextUtils.isEmpty(jsonObject.optString("WIDTH", "0")))
            width = Float.parseFloat(jsonObject.optString("WIDTH", "0"));

        try {
            if (!TextUtils.isEmpty(jsonObject.optString("COLOR", "")))
                color = jsonObject.optString("COLOR", "");
        } catch (Exception e) {
           LoggerConfig.eLog(TAG,"movePointer :: " + e.getMessage());
        }

        float canvasHeight = (float) this.getHeight();
        float canvasWidth = (float) this.getWidth();
        Float mX = getXpoint(xpoint, width, canvasWidth);
        Float mY = getYpoint(ypoint, height, canvasHeight);
        Path myPath = new Path();

        if (!Objects.equals(identity, getIdentity())) {
            drawx = mX;
            drawy = mY;
            invalidate();
        }

    }


    /**
     * calculate x y points
     * @param x 1
     * @param y 1
     */
    private void calcPoints(float x, float y) {
        int[] offset = getBitmapOffset(this, true);
        float xx = x - offset[1];
        float yy = y - offset[0];
        // for Drawer
        xnew = pxTosp(xx);
        ynew = pxTosp(yy);

        drawx = xx;
        drawy = yy;
        calcViewHieghtWidth(offset);
    }

    /***
     * calculate view height and width
     * @param offset
     */
    private void calcViewHieghtWidth(int[] offset) {
        float width = this.getWidth() - offset[1] * 2f;
        float height = this.getHeight() - offset[0] * 2f;
        viewHeight = pxTosp(height);
        viewWidth = pxTosp(width);
    }


    /**
     * send drawing points
     */
    public void sendPointerpoint(Float viewHeight, Float viewWidth) {
        ReqRes_LivePointerPoints reqRes_livePointerPoints = new ReqRes_LivePointerPoints();
        color = Activity_VideoCall.getColorStringHashMap().get(getIdentity());
        ReqRes_LivePointerPoints.ANNOTATIONPOINT annotationpoint = reqRes_livePointerPoints.new ANNOTATIONPOINT();
        annotationpoint.setIDENTITY(getIdentity());
        annotationpoint.setCOLOR(color);
        annotationpoint.setXPOINTS(xnew);
        annotationpoint.setYPOINTS(ynew);
        annotationpoint.setHEIGHT(viewHeight);
        annotationpoint.setWIDTH(viewWidth);
        annotationpoint.setISENABLED(true);
        String data = new Gson().toJson(annotationpoint);

        reqRes_livePointerPoints.setANNOTATION_POINT(data);
        reqRes_livePointerPoints.setNotificationType(Constants.NOTIFICATION_TYPE_VIDEO_LIVE_POINTER);
        reqRes_livePointerPoints.setSessionid("");
        reqRes_livePointerPoints.setDeviceType(Constants.DEVICE_TYPE_ANDROID);

        onAnnotationListener.OnAnnotation(reqRes_livePointerPoints);
    }


    /***
     * on touch event
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                calcPoints(event.getX(), event.getY());
                onActionMove(event);
                invalidate();
                break;
            default:
                break;
        }
        return true;
    }

    /***
     * on touch move action
     * @param motionEvent
     */
    private void onActionMove(MotionEvent motionEvent) {
        sendPointerpoint(viewHeight, viewWidth);
    }

    /***
     * get path to draw on canvas
     * @param pathPoints
     * @return
     */
    private Path getPathForDrawing(LinkedList<ContinuousFadingPath> pathPoints) {
        Path path = new Path();

        final int nCurves = pathPoints.size() - 1;
        for (int i = 0; i < nCurves; i++) {
            ContinuousFadingPath curPt = pathPoints.get(i);
            ContinuousFadingPath prevPt, nextPt, endPt;

            if (i == 0) {
                path.moveTo(curPt.x, curPt.y);
            }

            int nexti = (i + 1) % pathPoints.size();
            int previ = (i - 1 < 0 ? pathPoints.size() - 1 : i - 1);

            prevPt = pathPoints.get(previ);
            nextPt = pathPoints.get(nexti);
            endPt = nextPt;

            float dx;
            float dy;
            if (i > 0) {
                dx = (nextPt.x - curPt.x) * 0.5f;
                dx += (curPt.x - prevPt.x) * 0.5f;
                dy = (nextPt.y - curPt.y) * 0.5f;
                dy += (curPt.y - prevPt.y) * 0.5f;
            } else {
                dx = (nextPt.x - curPt.x) * 0.5f;
                dy = (nextPt.y - curPt.y) * 0.5f;
            }

            ContinuousFadingPath ctrlPt1 = new ContinuousFadingPath(0, 0);
            ctrlPt1.x = curPt.x + dx / 3.0f;
            ctrlPt1.y = curPt.y + dy / 3.0f;

            curPt = pathPoints.get(nexti);

            nexti = (nexti + 1) % pathPoints.size();
            previ = i;

            prevPt = pathPoints.get(previ);
            nextPt = pathPoints.get(nexti);

            if (i < nCurves - 1) {
                dx = (nextPt.x - curPt.x) * 0.5f;
                dx += (curPt.x - prevPt.x) * 0.5f;
                dy = (nextPt.y - curPt.y) * 0.5f;
                dy += (curPt.y - prevPt.y) * 0.5f;
            } else {
                dx = (nextPt.x - curPt.x) * 0.1f;
                dy = (nextPt.y - curPt.y) * 0.1f;
            }

            ContinuousFadingPath ctrlPt2 = new ContinuousFadingPath(0, 0);
            ctrlPt2.x = curPt.x - dx / 3.0f;
            ctrlPt2.y = curPt.y - dy / 3.0f;

            path.cubicTo(ctrlPt1.x, ctrlPt1.y, ctrlPt2.x, ctrlPt2.y, endPt.x, endPt.y);

        }

        return path;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (drawx != 0 && drawy != 0) {
            canvas.drawCircle(drawx, drawy, mContext.getResources().getDimension(R.dimen.outer_circle_radius), createCirclePaint(parseColor("#FFFFFF"), false));
            canvas.drawCircle(drawx, drawy, mContext.getResources().getDimension(R.dimen.inner_circle_radius), createCirclePaint(parseColor(color), false));
        }

    }

    /**
     * @param onAnnotationListener 1
     */
    public void setOnAnnotationListener(onAnnotation onAnnotationListener) {
        this.onAnnotationListener = onAnnotationListener;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    /**
     * @return
     */
    public String getIdentity() {
        return identity;
    }


    public float pxTosp(float px) {
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        return (px / (displayMetrics.scaledDensity));
    }

    private float getXpoint(float xpoints, float senderWidth, float receiverWidth) {
        float xpoint = calculatePercenatagewithXPoints(xpoints, senderWidth, receiverWidth);
        return xpoints - xpoint;
    }

    private float getYpoint(float ypoints, float senderHeight, float receiverWidthHeight) {
        float ypoint = calculatePercenatageheightYPoints(ypoints, senderHeight, receiverWidthHeight);
        return ypoints - ypoint;
    }

    private float calculatePercenatagewithXPoints(float xPoint, float senderwidth, float receiverwidth) {
        float percentage = calculateWidthDifferencePercentage(receiverwidth, senderwidth);
        return (percentage * xPoint) / 100;
    }

    private float calculatePercenatageheightYPoints(float yPoint, float senderwidth, float receiverwidth) {
        float percentage = calculateHeightDifferencePercentage(receiverwidth, senderwidth);
        return (percentage * yPoint) / 100;
    }

    private float calculateWidthDifferencePercentage(float receiverWidth, float senderWidth) {
        float widthdifference = widthDifference(senderWidth, receiverWidth);
        return (100 * widthdifference) / senderWidth;
    }

    private float calculateHeightDifferencePercentage(float receiverHeight, float senderHeight) {
        float heightdifference = heightDifference(senderHeight, receiverHeight);
        return (100 * heightdifference) / senderHeight;
    }

    private float widthDifference(float senderWidth, float receiverWidth) {
        return senderWidth - receiverWidth;
    }

    private float heightDifference(float senderHeight, float receiverHeight) {
        return senderHeight - receiverHeight;
    }

    /**
     * Interface for annotation
     */
    public interface onAnnotation {
        void OnAnnotation(ReqRes_LivePointerPoints reqRes_livePointerPoints);
    }
}