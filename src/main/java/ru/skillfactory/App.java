package ru.skillfactory;


import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import ru.skillfactory.entity.Event;
import ru.skillfactory.entity.Participant;
import ru.skillfactory.entity.Place;

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
        session.close();

        session = sessionFactory.openSession();
        session.beginTransaction();
        List result = session.createQuery("from Event").list();
        for (Event event : (List<Event>) result) {
            System.out.println("Event (" + event.getDate() + ") : " + event.getTitle());
        }
        session.getTransaction().commit();
        session.close();

        session = sessionFactory.openSession();
        session.beginTransaction();
        Event event = session.load(Event.class, 1L);
        List<Participant> participants = getParticipants();
        for (Participant participant : participants) {
            session.save(participant);
        }
        event.setParticipantList(new ArrayList<>());
        event.getParticipantList().addAll(participants);
        session.save(event);
        session.getTransaction().commit();


        result = session.createQuery("from Event").list();
        for (Event iterableEvent : (List<Event>) result) {
            System.out.println("Event (" + iterableEvent.getDate() + ") : " + iterableEvent.getTitle() + " with participants = " + iterableEvent.getParticipantList().size());
        }
        session.close();

        session = sessionFactory.openSession();
        session.beginTransaction();
        Place place = new Place("Moscow", "Lenina", "2");
        session.save(place);
        event = session.load(Event.class, 1L);
        event.setPlace(place);
        session.save(event);
        session.getTransaction().commit();

        result = session.createQuery("from Event").list();
        for (Event iterableEvent : (List<Event>) result) {
            System.out.println("Event (" + iterableEvent.getDate() + ") :" +
                    " " + iterableEvent.getTitle()
                    + " with participants = " + iterableEvent.getParticipantList().size()
                    + " at the " + event.getPlace().getCity());
        }



        session.close();
        if (sessionFactory != null) {
            sessionFactory.close();
        }
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
