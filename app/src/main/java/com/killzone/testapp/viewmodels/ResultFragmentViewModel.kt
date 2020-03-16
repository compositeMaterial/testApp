package com.killzone.testapp.viewmodels

import android.graphics.PointF
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.killzone.testapp.network.Coordinates
import com.killzone.testapp.network.Point

class ResultFragmentViewModel : ViewModel() {

    private var _points = MutableLiveData<Array<PointF>>()
    val points: LiveData<Array<PointF>> = _points

    private var _adapterPoints = MutableLiveData<List<Point>>()
    val adapterPoints: LiveData<List<Point>> = _adapterPoints


    fun setPoints(c: Coordinates) {

        val t = Array<PointF>(c.response.points.size) { PointF(0f, 0f) }
        val p = mutableListOf<Pair<Float, Float>>()
        val k = mutableListOf<Point>()

        for (i in c.response.points.indices) {
            p.add(i, Pair(c.response.points[i].x.toFloat(), c.response.points[i].y.toFloat()))
        }

        p.sortBy { it.first }

        for (i in p.indices) {
            t[i].x = p[i].first
            t[i].y = p[i].second
        }

        // Здесь просто добавляем в лист лишнее значение, чтобы потом это значение послужило
        // пустышкой при отправлке в RecyclerView Adapter. Там значение с нулевым индексом
        // преобразуется в название строк (x и y) (сделал вместо добавления хедера к RecyclerView)

        for (i in 0 until p.size+1) {
            if (i == 0) {
                k.add(i, Point(0.0, 0.0))
            } else {
                k.add(i, Point(p[i-1].first.toDouble(), p[i-1].second.toDouble()))
            }
        }

        _points.value = t
        _adapterPoints.value = k

    }
}