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

package com.example.mego.adas.accidents.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * Database helper for Pets app. Manages database creation and version management.
 */
public class AccidentsDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "accidents.db";

    private static final int DATABASE_VERSION = 1;

    public AccidentsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        //Create table to hold accident table
        final String SQL_CREATE_ACCIDENT_TABLE = "CREATE TABLE " +
                AccidentsContract.AccidentsEntry.TABLE_NAME + " (" +
                AccidentsContract.AccidentsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                AccidentsContract.AccidentsEntry.COLUMN_ACCIDENT_LATITUDE + " REAL NOT NULL DEFAULT 0.0, " +
                AccidentsContract.AccidentsEntry.COLUMN_ACCIDENT_LONGITUDE + " REAL NOT NULL DEFAULT 0.0, " +
                AccidentsContract.AccidentsEntry.COLUMN_ACCIDENT_TITLE + " TEXT NOT NULL DEFAULT Accident, " +
                AccidentsContract.AccidentsEntry.COLUMN_ACCIDENT_DATE + " TEXT NOT NULL DEFAULT Feb, " +
                AccidentsContract.AccidentsEntry.COLUMN_ACCIDENT_TIME + " TEXT NOT NULL DEFAULT 11, " +
                AccidentsContract.AccidentsEntry.COLUMN_ACCIDENT_ID + " TEXT NOT NULL, " +
                "UNIQUE (" + AccidentsContract.AccidentsEntry.COLUMN_ACCIDENT_ID + ") ON CONFLICT REPLACE" +
                ");";

        sqLiteDatabase.execSQL(SQL_CREATE_ACCIDENT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        //drop the list and create new one
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + AccidentsContract.AccidentsEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}