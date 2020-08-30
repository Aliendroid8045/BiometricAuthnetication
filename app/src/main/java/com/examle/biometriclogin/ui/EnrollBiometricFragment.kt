package com.examle.biometriclogin.ui

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView

import com.example.biometriclogin.R
import com.examle.biometriclogin.communication.OnButtonSelection
import com.examle.biometriclogin.utility.BiometricUtility
import com.examle.biometriclogin.utility.Constants.TYPE_OF_BIO

class EnrollBiometricFragment : Fragment() {

    private lateinit var listener: OnButtonSelection

    companion object {

        fun newInstance(): EnrollBiometricFragment {
            return EnrollBiometricFragment()
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
        val view = inflater.inflate(R.layout.fragment_register_biometric, container, false)

        view.findViewById<Button>(R.id.btn_not_now)
            .setOnClickListener { listener.onClick("notNow") }

        view.findViewById<Button>(R.id.btn_enable_bio)
            .setOnClickListener { listener.onClick("enable") }

        setBiometricImage(view)

        return view
    }

    private fun setBiometricImage(view: View) {
        when (BiometricUtility.loadStringPreference(requireContext(), TYPE_OF_BIO)) {
            "Face recognition" -> view.findViewById<ImageView>(R.id.img_bio_type_on_enroll)
                .setImageResource(R.drawable.faceauthxxxhdpi)
            "Fingerprint" -> view.findViewById<ImageView>(R.id.img_bio_type_on_enroll)
                .setImageResource(R.drawable.fingerprintauthenticationhdpi)
        }

    }
}

