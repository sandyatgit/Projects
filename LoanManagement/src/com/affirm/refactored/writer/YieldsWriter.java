package com.affirm.refactored.writer;

import com.affirm.refactored.entity.Facility;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;

public class YieldsWriter {

    public static void writeToCSVFile(String fileName, String[] headers, List<Facility> facilities) {
        File csvOutputFile = new File(fileName);
        try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
            pw.println(headers[0]+","+headers[1]);
            facilities.stream().filter(x -> x.getYield()!=null).map(x->{
                return x.getId()+","+Math.round(x.getYield());
            }).forEach(pw::println);
        } catch (FileNotFoundException e) {
            System.out.println(fileName+ " not_found. Exception = "+e);
        }
    }
}
