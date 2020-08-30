package com.examle.biometriclogin.ui

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button

import com.example.biometriclogin.R
import com.examle.biometriclogin.communication.OnButtonSelection

class LoginFragment : Fragment() {
    private lateinit var listener: OnButtonSelection


    companion object {

        fun newInstance(): LoginFragment {
            return LoginFragment()
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
        val view = inflater.inflate(R.layout.fragment_login, container, false)
        view.findViewById<Button>(R.id.btn_sign_in)
            .setOnClickListener { listener.onClick("signIn") }
        view.findViewById<Button>(R.id.btn_register_biometric)
            .setOnClickListener { listener.onClick("enrollBiometric") }
        view.findViewById<Button>(R.id.btn_disable_biometric)
            .setOnClickListener { listener.onClick("disableBio") }

        return view
    }

}

