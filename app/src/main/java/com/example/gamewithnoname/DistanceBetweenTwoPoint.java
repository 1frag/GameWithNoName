package com.example.gamewithnoname;

import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.yandex.mapkit.RequestPoint;
import com.yandex.mapkit.RequestPointType;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.transport.TransportFactory;
import com.yandex.mapkit.transport.masstransit.PedestrianRouter;
import com.yandex.mapkit.transport.masstransit.Route;
import com.yandex.mapkit.transport.masstransit.Session;
import com.yandex.mapkit.transport.masstransit.TimeOptions;
import com.yandex.runtime.Error;
import com.yandex.runtime.network.NetworkError;
import com.yandex.runtime.network.RemoteError;

import java.util.ArrayList;
import java.util.List;

public class DistanceBetweenTwoPoint implements Session.RouteListener {

    private final String TAG = String.format("%s/%s",
            "HITS", "DistanceBetweenTwoPoint");
    private Session mResult;
    private boolean mIsValid;

    public DistanceBetweenTwoPoint(Point start, Point finish){
        // todo: think about..via points..
        PedestrianRouter pdRouter;
        pdRouter = TransportFactory.getInstance().createPedestrianRouter();
        mResult = pdRouter.requestRoutes(initPath(start, finish), initOptions(), this);
    }

    private TimeOptions initOptions() {
        return new TimeOptions();
    }

    private List<RequestPoint> initPath(Point start, Point finish) {
        ArrayList<RequestPoint> requestPoints = new ArrayList<>();
        requestPoints.add(new RequestPoint(
                start,
                RequestPointType.WAYPOINT,
                null));
        requestPoints.add(new RequestPoint(
                finish,
                RequestPointType.WAYPOINT,
                null));
        return requestPoints;
    }

    @Override
    public void onMasstransitRoutes(@NonNull List<Route> list) {
        Log.i(TAG, "Hi!");
    }

    @Override
    public void onMasstransitRoutesError(@NonNull Error error) {
        String errorMessage = "unknown_error_message";
        if (error instanceof RemoteError) {
            errorMessage = "remote_error_message";
        } else if (error instanceof NetworkError) {
            errorMessage = "network_error_message";
        }

        Log.i(TAG, errorMessage);
    }
}
