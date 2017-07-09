/*
 * Copyright (c) 2017 Ahmed-Abdelmeged
 *
 * github: https://github.com/Ahmed-Abdelmeged
 * email: ahmed.abdelmeged.vm@gamil.com
 * Facebook: https://www.facebook.com/ven.rto
 * Twitter: https://twitter.com/A_K_Abd_Elmeged
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package com.example.mego.adas.api.directions;

import com.example.mego.adas.api.directions.model.Direction;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;


public interface DirectionsApiInterface {
    @GET("maps/api/directions/json?key=AIzaSyDzUFC3PRm7vehzlCZdhoCxq2awhEY3sZ0")
    Call<Direction> getDirections(
            @Query("origin") String origin,
            @Query("destination") String destination
    );
}
