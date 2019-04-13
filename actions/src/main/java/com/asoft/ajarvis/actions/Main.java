package com.asoft.ajarvis.actions;

import com.asoft.ajarvis.actions.services.Request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
//        Request request=new Request();
//
//        ArrayList<String> ids = new ArrayList<>();
//        ids.add("\"dc31a2fa-f046-4bba-8c43-305f9fe0ebc4\"");
        ArrayList<Object> list = new ArrayList<>();
        list.add(500);
        list.add(600);
//        HashMap<String, Object> arg = new HashMap<>();
//        arg.put("xy", list);
        System.out.println(list.getClass().getSimpleName()+list.get(0).getClass().getSimpleName());
//        Map<String, Object> request1 = request.sendRequest("http://10.241.128.214:5000/execute", ids, arg);
//        System.out.println("request1 = " + request1);
    }
}
