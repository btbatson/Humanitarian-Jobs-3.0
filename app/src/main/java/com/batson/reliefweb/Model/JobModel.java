package com.batson.reliefweb.Model;

import java.util.List;

/**
 * Titus Batson
 * Website: titusbatson.com
 * Date: 10/03/2020
 */

public class JobModel {

    public static class Source {
        public String name;

        public String getName() {
            return name;
        }
    }
    public static class Career_categories {
        public String name;

        public String getCat() {
            return name;
        }
    }



    public static class County {
        public String name;

        public String getName() {
            return name;
        }
    }

    public static class Field {
        public String title;
        public String url;

        public Date date;
        public List<County> country;

        public List<County> getCountry() {
            return country;
        }
        public List<Source> source;
        public List<Career_categories> career_categories;
        public List<Source> theme;
        public List<Career_categories> getCareer_categories() {
            return career_categories;
        }
        public List<Source> getSource() {
            return source;
        }

        public String getTitle() {
            return title;
        }
        public String getUrl() {
            return url;
        }
        public Date getDate() {
            return date;
        }
    }
    public static class Date {
        public String closing;

        public String getCreated() {
            return closing;
        }
    }


    public static class Data{
        public String id;
        public Field fields;

        public String getId() {
            return id;
        }

        public Field getFields() {
            return fields;
        }
    }

    public List<Data> data;

    public List<Data> getData() {
        return data;

    }
}
