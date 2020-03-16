package com.killzone.testapp.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.killzone.testapp.databinding.FragmentInputBinding
import com.killzone.testapp.viewmodels.InputFragmentViewModel
import java.util.jar.Manifest


class InputFragment : Fragment() {

    private lateinit var binding: FragmentInputBinding
    private val viewModel by viewModels<InputFragmentViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentInputBinding.inflate(inflater, container, false)
        binding.setLifecycleOwner(this)


        binding.button.setOnClickListener {
            handleDigits()
        }

        viewModel.navigateValue.observe(viewLifecycleOwner, Observer {
           handleNavigation(it)
        })

        viewModel.errorValue.observe(viewLifecycleOwner, Observer {
           handleError(it)
        })


        return binding.root
    }


    private fun handleDigits() {
        val digits: Int = binding.editText.text.toString().toInt()

        if (digits > 0) {
            viewModel.acceptNumber(digits)
        } else {
            Toast.makeText(this.context, "Неправильные Данные", Toast.LENGTH_LONG).show()
        }
    }

    private fun handleNavigation(v: Boolean) {
        if (v) {
            val action = InputFragmentDirections
                .actionInputFragmentToResultFragment(viewModel.coordinatesValue.value!!)

            findNavController().navigate(action)
            viewModel.setNavigateValue()
        }
    }

    private fun handleError(v: Boolean) {
        if (v) {
            Toast.makeText(this.context, viewModel.errorMessage.value, Toast.LENGTH_LONG).show()
            viewModel.setErrorValue()
        }

    }

}
