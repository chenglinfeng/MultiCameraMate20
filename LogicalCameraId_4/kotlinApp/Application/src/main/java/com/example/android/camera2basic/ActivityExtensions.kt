/*
 * Copyright 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.camera2basic

import android.support.v4.app.FragmentActivity
import android.support.v4.content.ContextCompat
import android.widget.Toast

/**
 * This file illustrates Kotlin's Extension Functions by extending FragmentActivity.
 */

/**
 * Shows a [Toast] on the UI thread.
 *
 * @param text The message to show
 */
fun FragmentActivity.showToast(text: String) {
    runOnUiThread { Toast.makeText(this, text, Toast.LENGTH_SHORT).show() }
}

/**
 * Helper class used to encapsulate a logical camera and two underlying
 * physical cameras
 */
data class DualCamera(val logicalId: String, val physicalId1: String, val physicalId2: String)

fun findDualCameras(manager: CameraManager, facing: Int? = null): Array<DualCamera> {
    val dualCameras = ArrayList<DualCamera>()

    // Iterate over all the available camera characteristics
    manager.cameraIdList.map {
        Pair(manager.getCameraCharacteristics(it), it)
    }.filter {
        // Filter by cameras facing the requested direction
        facing == null || it.first.get(CameraCharacteristics.LENS_FACING) == facing
    }.filter {
        // Filter by logical cameras
        it.first.get(CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES)!!.contains(
                CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_LOGICAL_MULTI_CAMERA)
    }.forEach {
        // All possible pairs from the list of physical cameras are valid results
        // NOTE: There could be N physical cameras as part of a logical camera grouping
        val physicalCameras = it.first.physicalCameraIds.toTypedArray()
        for (idx1 in 0 until physicalCameras.size) {
            for (idx2 in (idx1 + 1) until physicalCameras.size) {
                dualCameras.add(DualCamera(
                        it.second, physicalCameras[idx1], physicalCameras[idx2]))
            }
        }
    }

    return dualCameras.toTypedArray()
}