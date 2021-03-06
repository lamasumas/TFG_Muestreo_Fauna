package com.example.myapplication.fragments.transects.dialog

import android.app.AlertDialog
import android.content.Context
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.example.myapplication.R
import com.example.myapplication.rest.RestManager
import com.example.myapplication.room.data_classes.Transect
import com.example.myapplication.utils.InputValidator
import com.example.myapplication.viewmodels.TransectViewModel
import com.example.myapplication.viewmodels.controllers.LocationControllerViewModel
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.new_transect_dialog.*

class NewTransectDialog() : DialogFragment() {

    val disposables = CompositeDisposable()

    private val transectViewModel: TransectViewModel by activityViewModels()
    private val locationController: LocationControllerViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.new_transect_dialog, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etNearestPlace = view.findViewById<EditText>(R.id.etNearestPlace)
        val etTransect = view.findViewById<EditText>(R.id.etTransectName)
        val etCountry = view.findViewById<EditText>(R.id.etCountry)
        val etAnimales = view.findViewById<EditText>(R.id.etAnimales)
        val etLocalidad = view.findViewById<EditText>(R.id.etLocalidad)
        val etAltitudeFirst = view.findViewById<EditText>(R.id.etAltitudeFirst)
        val chPressure = view.findViewById<CheckBox>(R.id.chPressure)
        val validator = InputValidator()


        val wrappedDrawable = DrawableCompat.wrap(etAltitudeFirst.background)
        DrawableCompat.setTint(wrappedDrawable, resources.getColor(R.color.colorPrimaryPaleta))
        etAltitudeFirst.background = wrappedDrawable

        chPressure.setOnCheckedChangeListener { buttonView, isChecked ->
            val tempwrappedDrawable = DrawableCompat.wrap(etAltitudeFirst.background)
            etAltitudeFirst.isEnabled = isChecked
            if (isChecked)
                DrawableCompat.setTint(wrappedDrawable, resources.getColor(R.color.colorSecundarioPaleta))
            else
                DrawableCompat.setTint(wrappedDrawable, resources.getColor(R.color.colorPrimaryPaleta))
            etAltitudeFirst.background = tempwrappedDrawable
        }

        (view.context.getSystemService(Context.LOCATION_SERVICE) as LocationManager).also {
            if (it.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                locationController.getOneGPSPosition().subscribe { location ->
                    view.findViewById<EditText>(R.id.etCountry).setText(location.country)
                    view.findViewById<EditText>(R.id.etLocalidad).setText(location.place)

                    RestManager().getGeoNameLocation(location.latitude.toString(), location.longitude.toString())
                            ?.subscribeOn(AndroidSchedulers.mainThread())?.subscribe {
                                etNearestPlace.setText(it?.first()?.name.toString())
                            }
                }
            }
        }

        disposables.add(view.findViewById<Button>(R.id.btnStoreTransect).clicks().subscribe {
            if (validator.isEditTextEmpty(etTransectName)) {
                AlertDialog.Builder(context).setTitle(R.string.alertTitleTransect)
                        .setMessage(R.string.alertMessageTransect)
                        .setNeutralButton(R.string.cerrarAlertBoton) { dialogInterface, _ -> dialogInterface.dismiss() }
                        .show()
            } else {
                if (chPressure.isChecked) {
                    if (validator.isEditTextEmpty(etAltitudeFirst)) {
                        AlertDialog.Builder(context).setTitle(R.string.titleAltitude)
                                .setMessage(R.string.messageAltitude)
                                .setNeutralButton(R.string.btnReturn) { dialogInterface, _ -> dialogInterface.dismiss() }
                                .setPositiveButton(R.string.btnContinue) { dialogInterface, _ ->
                                    transectViewModel.addTransect(Transect(
                                            name = etTransect.text.toString(),
                                            aniamlList = validator.nullOrEmpty(etAnimales.text.toString()),
                                            country = validator.nullOrEmpty(etCountry.text.toString()),
                                            locality = validator.nullOrEmpty(etLocalidad.text.toString()),
                                            pressureSampling = null,
                                            altitudeSampling = null,
                                            nearestPlace = validator.nullOrEmpty(etNearestPlace.text.toString())))
                                    dialogInterface.dismiss()
                                    this.dismiss()
                                }.show()

                    } else {
                        var tempTransect = Transect(
                                name = etTransect.text.toString(),
                                aniamlList = validator.nullOrEmpty(etAnimales.text.toString()),
                                locality = validator.nullOrEmpty(etLocalidad.text.toString()),
                                country = validator.nullOrEmpty(etCountry.text.toString()),
                                pressureSampling = null,
                                altitudeSampling = validator.nullOrEmpty(etAltitudeFirst.text.toString()).toDouble(),
                                nearestPlace = validator.nullOrEmpty(etNearestPlace.text.toString())
                        )
                        tempTransect.isAltitudeSamplingSet = true
                        transectViewModel.addTransect(tempTransect)
                        this.dismiss()
                    }
                } else {
                    val transect = Transect(
                            name = etTransect.text.toString(),
                            aniamlList = validator.nullOrEmpty(etAnimales.text.toString()),
                            locality = validator.nullOrEmpty(etLocalidad.text.toString()),
                            country = validator.nullOrEmpty(etCountry.text.toString()),
                            pressureSampling = null,
                            altitudeSampling = null,
                            nearestPlace = validator.nullOrEmpty(etNearestPlace.text.toString())
                    )

                    transect.isAltitudeSamplingSet = false
                    transectViewModel.addTransect(transect)
                    this.dismiss()
                }
            }
        })

    }

    override fun onDestroyView() {
        disposables.dispose()
        super.onDestroyView()
    }

    override fun dismiss() {
        disposables.dispose()
        super.dismiss()
    }
}