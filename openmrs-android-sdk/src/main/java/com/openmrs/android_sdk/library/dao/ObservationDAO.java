/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package com.openmrs.android_sdk.library.dao;

import com.openmrs.android_sdk.library.OpenmrsAndroid;
import com.openmrs.android_sdk.library.databases.AppDatabase;
import com.openmrs.android_sdk.library.databases.AppDatabaseHelper;
import com.openmrs.android_sdk.library.databases.entities.ObservationEntity;
import com.openmrs.android_sdk.library.databases.entities.StandaloneObservationEntity;
import com.openmrs.android_sdk.library.models.Observation;

import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

/**
 * The type Observation dao.
 */
public class ObservationDAO {
    /**
     * The Observation room dao.
     */
    ObservationRoomDAO observationRoomDAO = AppDatabase.getDatabase(OpenmrsAndroid.getInstance().getApplicationContext()).observationRoomDAO();

    @Inject
    public ObservationDAO() { }
    /**
     * Saves an observation entity to db
     *
     * @param obs the observation model
     * @param encounterID the encounterId
     *
     * @return id (primary key)
     */
    public Long saveObservation(Observation obs, long encounterID) {
        ObservationEntity observationEntity = AppDatabaseHelper.convert(obs, encounterID);
        return observationRoomDAO.addObservation(observationEntity);
    }

    /**
     * Update observation in db (ROOM will match the primary keys for updating)
     *
     * @param obs the observation
     * @param encounterId the observation (needed to verify that the observation already exists)
     *
     * @return count of updated values
     */
    public int updateObservation(Observation obs, long encounterId) {
        ObservationEntity observationEntity = AppDatabaseHelper.convert(obs, encounterId);
        observationEntity.setId(encounterId);
        return observationRoomDAO.updateObservation(observationEntity);
    }

    /**
     * Find observation by encounter id list.
     *
     * @param encounterID the encounter id
     * @return the list
     */
    public List<Observation> findObservationByEncounterID(Long encounterID) {
        List<Observation> observationList;
        List<ObservationEntity> observationEntityList;
        try {
            observationEntityList = observationRoomDAO.findObservationByEncounterID(encounterID).blockingGet();
            observationList = AppDatabaseHelper.convert(observationEntityList);
            return observationList;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    /**
     * Save observations independently to the offline database
     *
     * @param observationList the observation list to be saved
     * @return the list of primary keys
     */
    public List<Long> saveStandaloneObservations(List<Observation> observationList) {
        List<StandaloneObservationEntity> standaloneObservationEntityList = new ArrayList<>();
        for(Observation observation: observationList){
            StandaloneObservationEntity standaloneObservationEntity = AppDatabaseHelper.convertToStandalone(observation);
            standaloneObservationEntityList.add(standaloneObservationEntity);
        }
        return observationRoomDAO.addStandaloneObservationList(standaloneObservationEntityList);
    }

    /**
     * Delete all standalone observations in the database
     *
     * @param patientUuid the patient uuid for which the standalone encounters should be deleted
     */
    public void deleteAllStandaloneObservations(String patientUuid) {
        observationRoomDAO.deleteAllStandaloneObservationsByPatientUuid(patientUuid);
    }

}
