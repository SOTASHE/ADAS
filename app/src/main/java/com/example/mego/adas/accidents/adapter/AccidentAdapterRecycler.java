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


package com.example.mego.adas.accidents.adapter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mego.adas.R;
import com.example.mego.adas.accidents.db.entity.Accident;

import java.util.ArrayList;
import java.util.List;

/**
 * custom recycler view to view the list of accidents
 */
public class AccidentAdapterRecycler extends RecyclerView.Adapter<AccidentAdapterRecycler.AccidentViewHolder> {

    /**
     * An on-click handler that we've defined to make it easy for an Activity to interface with
     * our RecyclerView
     */
    final private AccidentClickCallBacks mOnClickListener;

    /**
     * List to hold accidents object
     */
    private ArrayList<Accident> accidentList = new ArrayList<>();


    /**
     * Constructor for GreenAdapter that accepts a number of items to display and the specification
     * for the ListItemClickListener.
     *
     * @param listener Listener for list item clicks
     */
    public AccidentAdapterRecycler(AccidentClickCallBacks listener) {
        mOnClickListener = listener;
    }

    @Override
    public AccidentAdapterRecycler.AccidentViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.item_accident;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);

        return new AccidentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AccidentAdapterRecycler.AccidentViewHolder holder, int position) {

        //get the current accident to extract DataSend from it
        Accident currentAccident = accidentList.get(position);

        assert currentAccident != null;
        String date = currentAccident.getDate();
        holder.dateTextView.setText(date);

        String time = currentAccident.getTime();
        holder.timeTextView.setText(time);

        String accidentTitle = currentAccident.getAccidentTitle();
        holder.accidentTitleTextView.setText(accidentTitle + " " + (position + 1));

        double longitude = currentAccident.getAccidentLongitude();
        double latitude = currentAccident.getAccidentLatitude();
        String accidentPosition = "lng: " + longitude + " ,lat: " + latitude;
        holder.accidentPositionTextView.setText(accidentPosition);

    }

    @Override
    public int getItemCount() {
        if (accidentList == null) return 0;
        return accidentList.size();
    }

    /**
     * Cache of the children views for a list item.
     */
    class AccidentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        //set the current accident date
        TextView dateTextView;

        //set the current step time
        TextView timeTextView;

        //setup the accident title
        TextView accidentTitleTextView;

        //setup the accident position
        TextView accidentPositionTextView;


        AccidentViewHolder(View itemView) {
            super(itemView);

            dateTextView = itemView.findViewById(R.id.accident_date_text_view);
            timeTextView = itemView.findViewById(R.id.accident_time_text_view);
            accidentTitleTextView = itemView.findViewById(R.id.accident_name_textView);
            accidentPositionTextView = itemView.findViewById(R.id.accident_position_textView);

            itemView.setOnClickListener(this);
        }

        /**
         * Called whenever a user clicks on an item in the list.
         *
         * @param v The View that was clicked
         */
        @Override
        public void onClick(View v) {
            mOnClickListener.onAccidentClick(
                    accidentList.get(getAdapterPosition()).getAccidentId());
        }
    }


    public void setAccidents(List<Accident> accidents) {
        accidentList.addAll(accidents);
        notifyDataSetChanged();
    }

    public void clearAccidents() {
        accidentList.clear();
        notifyDataSetChanged();
    }

    public void addAccident(Accident accident) {
        accidentList.add(accident);
        notifyDataSetChanged();
    }

}
