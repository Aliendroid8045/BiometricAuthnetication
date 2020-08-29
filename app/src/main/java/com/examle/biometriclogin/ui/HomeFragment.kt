package com.examle.biometriclogin.ui

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.examle.biometriclogin.communication.OnButtonSelection
import com.example.biometriclogin.R


class HomeFragment : Fragment() {
    private lateinit var listener: OnButtonSelection

    companion object {

        fun newInstance(): HomeFragment {
            return HomeFragment()

        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnButtonSelection) {
            listener = context
        } else {
            throw ClassCastException(
                "$context must implement OnButtonSelection."
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_home, container, false)
        view.findViewById<Button>(R.id.logout)
            .setOnClickListener { listener.onClick("logout") }
        return view
    }
}