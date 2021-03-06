package com.example.myapplication.room

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.myapplication.room.data_classes.*
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single


@Dao
interface AvistamientoDAO{


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAnimal(muestreo: Transect): Single<Long>

    @Query("SELECT * FROM muestreos")
    fun getMuestreos():Observable<List<Transect>>

    @Delete
    fun deleteMuestreos(muestreo: Transect):Completable

    @Query("SELECT * FROM muestreos")
    fun getAllTransect(): Observable<List<Transect>>

    @Query("SELECT * FROM muestreos WHERE transect_id= :transectId")
    fun getTransectById(transectId: Long):Observable<Transect>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAnimal(avistamiento: AnimalSimpleData): Single<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAnimal(avistamiento: AnimalAdvanceData): Completable

    @Query("SELECT * FROM animals")
    fun getAnimalSimple(): Observable<List<AnimalSimpleData>>

    @Query("SELECT * FROM animals WHERE simpleId=:uid")
    fun getAnimalSimpleById( uid:Int): Observable<AnimalSimpleData>

    @Update
    fun updateAnimal( avistamientoData: AnimalSimpleData):Completable

    @Update
    fun updateAnimal( avistamientoData: AnimalAdvanceData):Completable

    @Update
    fun updateTransect( transect: Transect):Completable

    @Delete
    fun deleteAnimal( avistamientoData: AnimalSimpleData):Completable
    @Delete
    fun deleteAnimal( avistamientoData: AnimalAdvanceData):Completable

    @Query("DELETE FROM muestreos WHERE transect_id=:idTransect")
    fun deleteTransectById(idTransect: Long):Completable

    @Query("SELECT DISTINCT * FROM animals WHERE simpleId=:simpleId")
    fun getAnimalFullData(simpleId: Long):  Observable<SimpleAdvanceRelation>

    @Query("DELETE FROM animals WHERE animals.transect_id = :transectId")
    fun deleteAnimalsByTransectId(transectId: Long):  Completable

    @Transaction
    @Query("SELECT * FROM   animals WHERE animals.transect_id = :transectId")
    fun getAnimalFullDataOfTransectTransaction(transectId: Long):  Observable<List<SimpleAdvanceRelation>>

    @Transaction
    @Query("SELECT * FROM animals")
    fun getAllData(): Observable<List<SimpleAdvanceRelation>>

    @Transaction
    @Query("Select * From muestreos WHERE transect_id=:transectId")
    fun getRelationAnimalsTransect(transectId:Long):Observable<TransectAnimalRelation>

}