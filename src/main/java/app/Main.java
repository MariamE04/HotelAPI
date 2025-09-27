package app;

import app.DAO.HotelDAO;
import app.config.ApplicationConfig;
import app.config.HibernateConfig;
import app.dtos.HotelDTO;
import app.mappers.HotelMapper;
import app.utils.Populater;
import jakarta.persistence.EntityManagerFactory;

import java.util.ArrayList;
import java.util.List;

import static io.javalin.apibuilder.ApiBuilder.get;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
        System.out.println("test");

        ApplicationConfig.startServer(7071);

    }

}