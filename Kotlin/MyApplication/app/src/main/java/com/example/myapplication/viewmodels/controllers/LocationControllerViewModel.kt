package com.example.myapplication.viewmodels.controllers;

import android.annotation.SuppressLint
import android.app.Application
import android.location.Geocoder
import android.location.Location
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.myapplication.viewmodels.GeneralViewModel
import com.google.android.gms.location.*
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.lang.Exception


@SuppressLint("MissingPermission")
class LocationControllerViewModel(application: Application) : Controller(application),
    GeneralViewModel {

    var locationObservable: Observable<Location>
    val LATITUDE_ID = 0
    val LONGITUDE_ID = 1
    val COUNTRY_ID = 2
    val PLACE_ID = 3

    private var geocoder: Geocoder = Geocoder(application)
    private val disposables = CompositeDisposable()

    data class MylocationObject(
        var longitude: Double,
        var latitude: Double,
        var country: String,
        var place: String
    )

    /**
     * Se inicializa el hasmap con los valores mutables de la posición.
     * Aqui tambien se crea el observable infinito con el que se actualiza se pide actualizar la posicion
     */
    init {

        myData[LATITUDE_ID] = MutableLiveData("")
        myData[LONGITUDE_ID] = MutableLiveData("")
        myData[COUNTRY_ID] = MutableLiveData("")
        myData[PLACE_ID] = MutableLiveData("")


        locationObservable = Observable.create { emitter ->
            val locationRequest = LocationRequest.create();
            locationRequest.interval = 10000;
            locationRequest.fastestInterval = 5000;
            locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY;

            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult?) {
                    super.onLocationResult(locationResult)
                    if (locationResult != null) {
                        for (location: Location in locationResult.locations) {
                            if (location != null) {
                                emitter.onNext(location)
                            }
                        }
                    }
                }
            }
            val locationClient = LocationServices.getFusedLocationProviderClient(application)
            locationClient.requestLocationUpdates(locationRequest, locationCallback, null)
        }
    }


    /**
     * Función en la que se subscribe al observable creado en el constructor, y en el que se
     * actualiza los valores del hasmap
     */
    fun startGettingInfinitePositions() {
        disposables.add(
            locationObservable.observeOn(Schedulers.io())
                .flatMap { Observable.just(translateGPS2Place(it.longitude, it.latitude)) }
                .observeOn(AndroidSchedulers.mainThread()).subscribe {
                    myData[LATITUDE_ID]?.value = it.latitude.toString()
                    myData[LONGITUDE_ID]?.value = it.longitude.toString()
                    myData[PLACE_ID]?.value = it.place
                    myData[COUNTRY_ID]?.value = it.country
                }
        )

    }

    /**
     * Función con la que se obtiene el lugar y pais basandose en una posicion gps
     * @param lat: Double con la latitud de la posicion a traducir
     * @param lon: Double con la longitud de la posicion a traducir
     * @return MylocationObject, objeto de una data class, este objeto contiene la lat, lon, el país
     * y el lugar obtenidos de Geocoder
     */
    fun translateGPS2Place(lon: Double, lat: Double): MylocationObject {
        val mylocationObject = MylocationObject(lon, lat, "", "")
        try {
            geocoder.getFromLocation(lat, lon, 3).forEach { address ->
                mylocationObject.country = address.countryName;
                mylocationObject.place = address.adminArea;
            }
        } catch (e: Exception) {
            Log.d("Geocoder", "Geocoder didn't found anything");
        }
        return mylocationObject
    }

    fun getOneGPSPosition(): Observable<MylocationObject> {

        return locationObservable.take(1).observeOn(Schedulers.io())
            .flatMap { Observable.just(translateGPS2Place(it.longitude, it.latitude)) }
            .observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * Función que termina la subscripción del observable "locaitonObservable"
     */
    fun stopGettingPositions() {
        disposables.clear()
    }

    override fun preStart() {
        Log.d("Initialization", "LocationViewModel startted")
    }


}
