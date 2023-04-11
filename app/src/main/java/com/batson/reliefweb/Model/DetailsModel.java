package com.batson.reliefweb.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by minos.ai on 16/12/17.
 */

public class DetailsModel {

    public class Date {
        public String closing;
        public String changed;
        public String created;

        public String getClosing() {
            return closing;
        }

        public String getChanged() {
            return changed;
        }

        public String getCreated() {
            return created;
        }
    }




    public class Source {
        public String href;
        public int id;
        public String name;
        public String shortname;
        public String longname;
        public String homepage;

        public String getHref() {
            return href;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getShortname() {
            return shortname;
        }

        public String getLongname() {
            return longname;
        }

        public String getHomepage() {
            return homepage;
        }
    }

    public class Country {
        public String href;
        public int id;
        public String name;

        public String getHref() {
            return href;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }

    public class PrimaryCountry {
        public String href;
        public String name;

        public String getHref() {
            return href;
        }

        public String getName() {
            return name;
        }
    }

    public class File {
        public String id;
        public String description;
        public String url;
        public String filename;

        public String getId() {
            return id;
        }

        public String getDescription() {
            return description;
        }

        public String getUrl() {
            return url;
        }

        public String getFilename() {
            return filename;
        }
    }

    public class Fields {
        public int id;
        public String title;
        public String status;
        @SerializedName("body-html") public String body;
        @SerializedName("how_to_apply-html") public String how_to_apply;
        public List<Country> country;
        public List<Source> source;

        public List<Source> getSource() {
            return source;
        }


        public String url;
        public Date date;

        public Date getDate() {
            return date;
        }

        public int getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public String getStatus() {
            return status;
        }

        public String getBody() {
            return body;
        }
        public String gethow_to_apply() {
            return how_to_apply;
        }

        public List<Country> getCountry() {
            return country;
        }


        public String getUrl() {
            return url;
        }

    }

    public class Data {

        public Fields fields;
        public String id;

        public String getId() {
            return id;
        }

        public Fields getFields() {
            return fields;
        }
    }

    public String href;
    public List<Data> data;

    public String getHref() {
        return href;
    }

    public List<Data> getData() {
        return data;
    }
}
