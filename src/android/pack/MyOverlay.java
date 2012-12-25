/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package android.pack;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;
import java.util.List;

public class MyOverlay extends Overlay {

    List<Double> ov_lats;
    List<Double> ov_longs;

    public List<Double> getOv_lats() {
        return ov_lats;
    }

    public void setOv_lats(List<Double> ov_lats) {
        this.ov_lats = ov_lats;
    }

    public List<Double> getOv_longs() {
        return ov_longs;
    }

    public void setOv_longs(List<Double> ov_longs) {
        this.ov_longs = ov_longs;
    }

    @Override
    public void draw(Canvas can, MapView view, boolean shadow) {

        Projection proj = view.getProjection();

        if (shadow == false) {

            for (int i = 0; i < ov_lats.size()-1; i++) {
                
                int lat = (int) ((ov_lats.get(i) * 1E6));
                int lng = (int) ((ov_longs.get(i) * 1E6));
                
                int lat2 = (int) ((ov_lats.get(i+1) * 1E6));
                int lng2 = (int) ((ov_longs.get(i+1) * 1E6));
                
                
                GeoPoint p = new GeoPoint(lat, lng);
                GeoPoint p2 = new GeoPoint(lat2, lng2);

                Point pt = new Point();
                Point pt2 = new Point();

                proj.toPixels(p, pt);
                proj.toPixels(p2, pt2);

                Paint paint = new Paint();
                paint.setARGB(250, 255, 0, 0);
                paint.setAntiAlias(true);
                paint.setFakeBoldText(true);

                can.drawLine(pt.x, pt.y, pt2.x, pt2.y, paint);
            }

        }
    }
}
