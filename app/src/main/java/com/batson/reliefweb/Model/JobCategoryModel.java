package com.batson.reliefweb.Model;

import java.util.List;

/**
 * Titus Batson
 * Website: titusbatson.com
 * Date: 10/03/2020
 */

public class JobCategoryModel {


    public static class Field {
        public String name;
        public String description;



    }



    public static class Jobtype {
        public String id;
        public Field fields;

        public String getId() {
            return id;
        }

        public Field getFields() {
            return fields;
        }
    }

    public List<Jobtype> data;

    public List<Jobtype> getJobtype() {
        return data;

    }
}
