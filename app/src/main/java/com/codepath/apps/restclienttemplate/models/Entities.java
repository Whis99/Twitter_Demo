package com.codepath.apps.restclienttemplate.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

@Parcel
@Entity
public class Entities {

    @ColumnInfo
    @PrimaryKey
    public long entitiesId;

    @ColumnInfo
    public String mediaUrl, type;

    public Entities(){}

    public static Entities fromJson(JSONObject jsonObject) throws JSONException {
        Entities entities = new Entities();
        // if cover media is available
        if(!jsonObject.has("media")){
            entities.mediaUrl = "";
            entities.type = "";
        }else if(jsonObject.has("media")){
            final JSONArray mediaArray = jsonObject.getJSONArray("media");
            entities.entitiesId = mediaArray.getJSONObject(0).getLong("id");
            entities.mediaUrl = mediaArray.getJSONObject(0).getString("media_url_https");
            entities.type = mediaArray.getJSONObject(0).getString("type");
        }
        return entities;
    }

}
