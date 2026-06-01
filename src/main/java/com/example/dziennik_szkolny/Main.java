package com.example.dziennik_szkolny;

import DAO.UczenDAO;
import models.Uczen;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        UczenDAO uczen_dao = new UczenDAO();
        List<Uczen> uczniowie  = uczen_dao.wszyscyUczniowie();
        for(Uczen u : uczniowie){
            System.out.println(u.toString());

        }
    }
}