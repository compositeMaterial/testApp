package com.killzone.testapp.ui

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.killzone.testapp.R
import com.killzone.testapp.databinding.FragmentResultBinding
import com.killzone.testapp.databinding.RecyclerViewItemBinding
import com.killzone.testapp.network.Coordinates
import com.killzone.testapp.network.Point
import com.killzone.testapp.recyclerviewadapter.CoordinatesAdapter
import com.killzone.testapp.viewmodels.ResultFragmentViewModel
import kotlinx.android.synthetic.main.fragment_result.view.*
import kotlinx.android.synthetic.main.recycler_view_item.view.*
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.OutputStream
import java.util.*
import kotlin.random.Random


class ResultFragment : Fragment() {

    private val STORAGE_PERMISSION_CODE = 23

    private lateinit var binding: FragmentResultBinding
    private val adapter by lazy { CoordinatesAdapter() }
    private val viewModel by viewModels<ResultFragmentViewModel>()
    private val args: ResultFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentResultBinding.inflate(inflater, container, false)
        binding.setLifecycleOwner(this)
        binding.recyclerView.adapter = adapter

        viewModel.setPoints(args.coordinates)

        viewModel.adapterPoints.observe(viewLifecycleOwner, Observer {
            it?.let { adapter.submitList(it)}
        })

        if (viewModel.points.value != null) {
            binding.pointsView.setPoints(viewModel.points.value)
        }

        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.result_fragment_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.title == "Screenshot") {
            makeScreenshot(binding.pointsView)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun makeScreenshot(v: View): Boolean {

        v.setDrawingCacheEnabled(true)
        val b = Bitmap.createBitmap(v.getDrawingCache())
        v.setDrawingCacheEnabled(false)

        val path = Environment.getExternalStorageDirectory().absolutePath + "/Download/" + Random.nextInt().toString() + ".jpg"
        val file = File(path)

        val out: FileOutputStream

        try {
            requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), STORAGE_PERMISSION_CODE)
            out = FileOutputStream(file)
            b.compress(Bitmap.CompressFormat.JPEG, 100, out)
            out.flush()
            out.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        Toast.makeText(this.context, "Screenshot is taken", Toast.LENGTH_SHORT).show()
        return true
    }


}





