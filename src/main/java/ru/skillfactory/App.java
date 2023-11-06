package ru.skillfactory;


import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import ru.skillfactory.entity.Event;
import ru.skillfactory.entity.Participant;
import ru.skillfactory.entity.Place;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class App {
    public static void main(String[] args) throws Exception {
        SessionFactory sessionFactory = null;

        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure()
                .build();
        try {
            sessionFactory = new MetadataSources(registry)
                    .addAnnotatedClass(Event.class)
                    .addAnnotatedClass(Participant.class)
                    .addAnnotatedClass(Place.class)
                    .buildMetadata().buildSessionFactory();
        } catch (Exception e) {
            // The registry would be destroyed by the SessionFactory, but we had trouble building the SessionFactory
            // so destroy it manually.
            StandardServiceRegistryBuilder.destroy(registry);
        }


        Session session = sessionFactory.openSession();
        session.beginTransaction();

        session.save(new Event("Our very first event!", new Date()));
        session.getTransaction().commit();
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        CriteriaQuery<Event> query = criteriaBuilder.createQuery(Event.class);
        Root<Event> root = query.from(Event.class);
        query.select(root);

        Query<Event> quers = session.createQuery(query);
        List<Event> events = quers.getResultList();
        System.out.println(events.get(0).getTitle());

    }

    public static List<Participant> getParticipants() {
        return Arrays.asList(
                new Participant("Ivan", "Ivanov"),
                new Participant("Ivan", "Fedorod"),
                new Participant("George", "Bush"),
                new Participant("Probably", "Robot")
        );
    }
}
