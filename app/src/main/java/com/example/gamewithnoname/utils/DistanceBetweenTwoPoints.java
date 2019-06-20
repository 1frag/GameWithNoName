package com.example.gamewithnoname.utils;

import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.example.gamewithnoname.maps.MapMainMenu;
import com.yandex.mapkit.RequestPoint;
import com.yandex.mapkit.RequestPointType;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.transport.TransportFactory;
import com.yandex.mapkit.transport.masstransit.Jams;
import com.yandex.mapkit.transport.masstransit.PedestrianRouter;
import com.yandex.mapkit.transport.masstransit.Route;
import com.yandex.mapkit.transport.masstransit.RouteMetadata;
import com.yandex.mapkit.transport.masstransit.Session;
import com.yandex.mapkit.transport.masstransit.TimeOptions;
import com.yandex.mapkit.transport.masstransit.TravelEstimation;
import com.yandex.runtime.Error;
import com.yandex.runtime.network.NetworkError;
import com.yandex.runtime.network.RemoteError;

import java.util.ArrayList;
import java.util.List;

public class DistanceBetweenTwoPoints implements Session.RouteListener {

    private final String TAG = String.format("%s/%s",
            "HITS", "DistanceBetweenTwoPoints");
    private double mResult;
    private boolean mIsValid;
    private PedestrianRouter pdRouter;

    public DistanceBetweenTwoPoints(Point start, Point finish) {
        pdRouter = TransportFactory.getInstance().createPedestrianRouter();
        pdRouter.requestRoutes(initPath(start, finish), initOptions(), this);
        Log.i(TAG, String.format("%s %s", mResult, mIsValid));
    }

    private TimeOptions initOptions() {
        return new TimeOptions();
    }

    public double getResult() {
        if (mIsValid) {
            return mResult;
        } else {
            return -1;
        }
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
        if (list.size() == 0) {
            Log.i(TAG, "path not found");
            mIsValid = false;
            return;
        }
        RouteMetadata metadata = list.get(0).getMetadata();

        mIsValid = true;
        mResult = metadata.getWeight().getTime().getValue();

    }

    @Override
    public void onMasstransitRoutesError(@NonNull Error error) {
        Log.i(TAG, error.toString());
    }
}
