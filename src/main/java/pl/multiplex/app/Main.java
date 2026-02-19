package pl.multiplex.app;

import pl.multiplex.network.*;
import pl.multiplex.pricing.DefaultPricingPolicy;
import pl.multiplex.pricing.TicketFactory;
import pl.multiplex.sales.*;
import pl.multiplex.screening.*;
import pl.multiplex.shared.SeatId;
import pl.multiplex.shared.SeatZone;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class Main {

    public static void main(String[] args) {
        TicketFactory ticketFactory = new TicketFactory(new DefaultPricingPolicy("PLN"));

        MultiplexNetwork network = new MultiplexNetwork();

        Cinema cinema1 = new Cinema("Super Tarasy", "ul. Akademicka 5");
        Cinema cinema2 = new Cinema("Mega Kino", "ul. Centralna 10");

        network.addCinema(cinema1);
        network.addCinema(cinema2);

        Hall hallA = new Hall("Hall A", buildSeats("A", 1, 12, SeatZone.STANDARD));
        Hall hallVIP = new Hall("Hall VIP", buildMixedSeatsVIP());

        cinema1.addHall(hallA);
        cinema1.addHall(hallVIP);

        Hall hallB = new Hall("Hall B", buildSeats("B", 1, 10, SeatZone.STANDARD));
        cinema2.addHall(hallB);

        Movie movie1 = new Movie(
                "James Bon: Return of the Bug",
                "Ada Lovelace",
                115,
                LanguageOption.EN_PL_SUBS,
                List.of("action", "spy"),
                AgeRating.OVER_12
        );

        Movie movie2 = new Movie(
                "Kret: Zemsta Tunelu",
                "Grace Hopper",
                95,
                LanguageOption.PL,
                List.of("comedy", "family"),
                AgeRating.FAMILY
        );

        LocalDateTime now = LocalDateTime.now();

        Screening s1 = new Screening(movie1, hallA, now.plusHours(2), ScreeningFormat.TWO_D, ScreeningClass.STANDARD);
        Screening s2 = new Screening(movie1, hallVIP, now.plusHours(4), ScreeningFormat.THREE_D, ScreeningClass.VIP);
        Screening s3 = new Screening(movie2, hallA, now.plusDays(1).withHour(18).withMinute(0), ScreeningFormat.TWO_D, ScreeningClass.STANDARD);

        cinema1.schedule(s1);
        cinema1.schedule(s2);
        cinema1.schedule(s3);

        Screening s4 = new Screening(movie2, hallB, now.plusDays(2).withHour(20).withMinute(0), ScreeningFormat.TWO_D, ScreeningClass.STANDARD);
        cinema2.schedule(s4);

        System.out.println("\nRepertuar na najbliższy tydzień:");
        cinema1.printProgramme(LocalDate.now(), LocalDate.now().plusDays(7));
        cinema2.printProgramme(LocalDate.now(), LocalDate.now().plusDays(7));

        Guest guestNoAccount = new Guest("guest#anon");
        Customer customerA = new Customer("CUST-001", "Jan", "Kowalski");
        Customer customerB = new Customer("CUST-002", "Anna", "Nowak");
        Customer familyBuyer = new Customer("CUST-003", "Piotr", "Familijny");

        System.out.println("\nDwóch klientów robi rezerwacje:");
        SeatId a1 = new SeatId("A", 1);
        SeatId a2 = new SeatId("A", 2);
        SeatId a3 = new SeatId("A", 3);
        SeatId a4 = new SeatId("A", 4);

        Reservation resA = s1.reserveSeats(new SeatReservationRequest(customerA, Set.of(a1, a2)));
        System.out.println("Rezerwacja A: " + resA.getReservationId() + " miejsca=" + resA.getSeatIds());

        Reservation resB = s1.reserveSeats(new SeatReservationRequest(customerB, Set.of(a3, a4)));
        System.out.println("Rezerwacja B: " + resB.getReservationId() + " miejsca=" + resB.getSeatIds());

        System.out.println("Statusy miejsc:");
        System.out.println("A1=" + s1.getSeatStatus(a1) + " A2=" + s1.getSeatStatus(a2)
                + " A3=" + s1.getSeatStatus(a3) + " A4=" + s1.getSeatStatus(a4));

        System.out.println("\nDrugi klient cofa rezerwację:");
        s1.cancelReservation(resB.getReservationId());

        System.out.println("Po cofnięciu:");
        System.out.println("A1=" + s1.getSeatStatus(a1) + " A2=" + s1.getSeatStatus(a2)
                + " A3=" + s1.getSeatStatus(a3) + " A4=" + s1.getSeatStatus(a4));

        System.out.println("\nGość bez konta kupuje bilety:");
        TicketOrder guestOrder = s1.buyTickets(
                new TicketPurchaseRequest(guestNoAccount, Set.of(a3, a4)),
                ticketFactory
        );

        System.out.println("Numer zamówienia: " + guestOrder.getOrderId());
        guestOrder.getTickets().forEach(t ->
                System.out.println("bilet=" + t.getTicketId() + " miejsce=" + t.getSeatId() + " cena=" + t.getPrice())
        );

        System.out.println("Status po zakupie:");
        System.out.println("A3=" + s1.getSeatStatus(a3) + " A4=" + s1.getSeatStatus(a4));

        System.out.println("\nRodzina kupuje kilka biletów razem:");
        SeatId a5 = new SeatId("A", 5);
        SeatId a6 = new SeatId("A", 6);
        SeatId a7 = new SeatId("A", 7);
        SeatId a8 = new SeatId("A", 8);

        TicketOrder familyOrder = s1.buyTickets(
                new TicketPurchaseRequest(familyBuyer, Set.of(a5, a6, a7, a8)),
                ticketFactory
        );

        System.out.println("Zamówienie rodzinne: " + familyOrder.getOrderId());
        familyOrder.getTickets().forEach(t ->
                System.out.println("miejsce=" + t.getSeatId() + " cena=" + t.getPrice())
        );

        System.out.println("\nBilety zapisane na koncie:");
        familyBuyer.getTickets().forEach(t ->
                System.out.println("film=" + t.getScreening().getMovie().getTitle()
                        + " godzina=" + t.getScreening().getStart()
                        + " miejsce=" + t.getSeatId()
                        + " cena=" + t.getPrice())
        );

        System.out.println("\nZakup VIP + 3D:");
        SeatId vip1 = new SeatId("V", 1);
        SeatId vip2 = new SeatId("V", 2);

        TicketOrder vipOrder = s2.buyTickets(
                new TicketPurchaseRequest(customerA, Set.of(vip1, vip2)),
                ticketFactory
        );

        System.out.println("Zamówienie VIP: " + vipOrder.getOrderId());
        vipOrder.getTickets().forEach(t ->
                System.out.println("miejsce=" + t.getSeatId() + " cena=" + t.getPrice()
                        + " [" + t.getScreening().getFormat() + ", " + t.getScreening().getClazz() + "]")
        );

        System.out.println("\nBilety klienta A:");
        customerA.getTickets().forEach(t ->
                System.out.println("film=" + t.getScreening().getMovie().getTitle()
                        + " godzina=" + t.getScreening().getStart()
                        + " miejsce=" + t.getSeatId()
                        + " cena=" + t.getPrice())
        );

        System.out.println("\nPróba rezerwacji sprzedanego miejsca:");
        try {
            s1.reserveSeats(new SeatReservationRequest(customerB, Set.of(a3)));
            System.out.println("Nie powinno się udać");
        } catch (Exception ex) {
            System.out.println("Błąd: " + ex.getMessage());
        }

        System.out.println("\nWyszukiwanie filmu:");
        cinema1.findMovie("James").forEach(m ->
                System.out.println("Znaleziono: " + m.getTitle() + " (" + m.getDirector() + ")")
        );

        System.out.println("\nWolne miejsca:");
        System.out.println("Liczba wolnych: " + s1.getFreeSeats().size());
        System.out.println("Przykład: " + s1.getFreeSeats().stream().limit(8).toList());

        System.out.println("\nKoniec programu");
    }

    private static Collection<Seat> buildSeats(String row, int from, int toInclusive, SeatZone zone) {
        List<Seat> seats = new ArrayList<>();
        for (int i = from; i <= toInclusive; i++) {
            seats.add(new Seat(new SeatId(row, i), zone));
        }
        return seats;
    }

    private static Collection<Seat> buildMixedSeatsVIP() {
        List<Seat> seats = new ArrayList<>();
        for (int i = 1; i <= 6; i++) seats.add(new Seat(new SeatId("V", i), SeatZone.VIP));
        for (int i = 1; i <= 4; i++) seats.add(new Seat(new SeatId("P", i), SeatZone.PROMO));
        return seats;
    }
}
