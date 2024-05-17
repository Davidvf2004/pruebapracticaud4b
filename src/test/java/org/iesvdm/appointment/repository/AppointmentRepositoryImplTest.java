package org.iesvdm.appointment.repository;

import org.iesvdm.appointment.entity.Appointment;
import org.iesvdm.appointment.entity.AppointmentStatus;
import org.iesvdm.appointment.entity.Customer;
import org.iesvdm.appointment.entity.User;
import org.iesvdm.appointment.repository.impl.AppointmentRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Nested
class AppointmentRepositoryImplTest {

    private Set<Appointment> appointments;

    private AppointmentRepository appointmentRepository;

    @BeforeEach
    public void setup() {
        appointments = new HashSet<>();
        appointmentRepository = new AppointmentRepositoryImpl(appointments);
    }

    /**
     * Crea 2 citas (Appointment) una con id 1 y otra con id 2,
     * resto de valores inventados.
     * Agrégalas a las citas (appointments) con la que
     * construyes el objeto appointmentRepository bajo test.
     * Comprueba que cuando invocas appointmentRepository.getOne con uno
     * de los id's anteriores recuperas obtienes el objeto.
     * Pero si lo invocas con otro id diferente recuperas null
     */
            @Test

            void getOneTest() {
                Customer customer1 = new Customer();
                Customer customer2 = new Customer();

                Appointment appointment1 = new Appointment(LocalDateTime.now(), LocalDateTime.now().plusHours(1), customer1);
                appointment1.setId(1);
                Appointment appointment2 = new Appointment(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(1).plusHours(1), customer2);
                appointment2.setId(2);

                appointmentRepository.getOne(appointment1.getId());
                appointmentRepository.getOne(appointment2.getId());

                Appointment retrievedAppointment1 = appointmentRepository.getOne(1);
                assertNotNull(retrievedAppointment1);
                assertEquals(appointment1, retrievedAppointment1);

                Appointment retrievedAppointment2 = appointmentRepository.getOne(2);
                assertNotNull(retrievedAppointment2);
                assertEquals(appointment2, retrievedAppointment2);

                Appointment retrievedAppointment3 = appointmentRepository.getOne(3);
                assertNull(retrievedAppointment3);
            }

    /**
     * Crea 2 citas (Appointment) y guárdalas mediante
     * appointmentRepository.save.
     * Comprueba que la colección appointments
     * contiene sólo esas 2 citas.
     */
    @Test
    void saveTest() {

                Appointment appointment = new Appointment();

                Customer customer1 = new Customer();
                customer1.setId(1);
                Customer customer2 = new Customer();
                customer2.setId(2);

                Appointment appointment1 = new Appointment(LocalDateTime.now(), LocalDateTime.now().plusHours(1), customer1);
                appointment1.setId(1);

                Appointment appointment2 = new Appointment(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(1).plusHours(1), customer2);
                appointment2.setId(2);

                appointmentRepository.save(appointment1);
                appointmentRepository.save(appointment2);

                List<Appointment> allAppointments = appointmentRepository.getEligibleAppointmentsForExchange(LocalDateTime.now(), 1);
                assertEquals(2, allAppointments.size());

                assertTrue(allAppointments.add(appointment1));
                assertTrue(allAppointments.add(appointment2));
    }

    /**
     * Crea 2 citas (Appointment) una cancelada por un usuario y otra no,
     * (atención al estado de la cita, lee el código) y agrégalas mediante
     * appointmentRepository.save a la colección de appointments
     * Comprueba que mediante appointmentRepository.findCanceledByUser
     * obtienes la cita cancelada.
     */
    @Test
    void findCanceledByUserTest() {
        Appointment appointment = new Appointment();

        Customer customer = new Customer();
        customer.setId(1);
        User user = new User();
        user.setId(1);
        Appointment appointment1 = new Appointment(LocalDateTime.now(), LocalDateTime.now().plusHours(1), customer);
        appointment1.setId(1);
        appointment1.setStatus(AppointmentStatus.SCHEDULED);

        Appointment appointment2 = new Appointment(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(1).plusHours(1), customer);
        appointment2.setId(2);
        appointment2.setStatus(AppointmentStatus.CANCELED);
        appointment2.setCanceledAt(LocalDateTime.now());
        appointment2.setCanceler(user);

        appointmentRepository.save(appointment1);
        appointmentRepository.save(appointment2);

        List<Appointment> canceledAppointments = appointmentRepository.findCanceledByUser(user.getId());

        assertEquals(1, canceledAppointments.size());
        assertTrue(canceledAppointments.contains(appointment2));
        assertFalse(canceledAppointments.contains(appointment1));
    }

    /**
     * Crea 3 citas (Appointment), 2 para un mismo cliente (Customer)
     * con sólo una cita de ellas presentando fecha de inicio (start)
     * y fin (end) dentro del periodo de búsqueda (startPeriod,endPeriod).
     * Guárdalas mediante appointmentRepository.save.
     * Comprueba que appointmentRepository.findByCustomerIdWithStartInPeroid
     * encuentra la cita en cuestión.
     * Nota: utiliza LocalDateTime.of(...) para crear los LocalDateTime
     */
        @Test
        void findByCustomerIdWithStartInPeroidTest() {

            Appointment appointment = new Appointment();

            Customer customer = new Customer();
            customer.setId(1);

            LocalDateTime startPeriod = LocalDateTime.of(2023, 5, 1, 0, 0);
            LocalDateTime endPeriod = LocalDateTime.of(2023, 5, 31, 23, 59);

            Appointment appointment1 = new Appointment(LocalDateTime.of(2023, 4, 25, 10, 0), LocalDateTime.of(2023, 4, 25, 11, 0), customer);
            appointment1.setId(1);

            Appointment appointment2 = new Appointment(LocalDateTime.of(2023, 5, 15, 14, 0), LocalDateTime.of(2023, 5, 15, 15, 0), customer);
            appointment2.setId(2);

            Appointment appointment3 = new Appointment(LocalDateTime.of(2023, 6, 1, 9, 0), LocalDateTime.of(2023, 6, 1, 10, 0), customer);
            appointment3.setId(3);

            appointmentRepository.save(appointment1);
            appointmentRepository.save(appointment2);
            appointmentRepository.save(appointment3);

            List<Appointment> appointmentsInPeriod = appointmentRepository.findByCustomerIdWithStartInPeroid(customer.getId(), startPeriod, endPeriod);

            assertEquals(1, appointmentsInPeriod.size());
            assertTrue(appointmentsInPeriod.contains(appointment2));
            assertFalse(appointmentsInPeriod.contains(appointment1));
            assertFalse(appointmentsInPeriod.contains(appointment3));
        }



    /**
     * Crea 2 citas (Appointment) una planificada (SCHEDULED) con tiempo fin
     * anterior a la tiempo buscado por appointmentRepository.findScheduledWithEndBeforeDate
     * guardándolas mediante appointmentRepository.save para la prueba de findScheduledWithEndBeforeDate
     *
     */
            @Test
            void findScheduledWithEndBeforeDateTest() {
                Appointment appointment = new Appointment();

                Customer customer = new Customer();
                customer.setId(1);

                LocalDateTime searchDate = LocalDateTime.of(2023, 5, 15, 12, 0);

                Appointment appointment1 = new Appointment(LocalDateTime.of(2023, 5, 14, 10, 0), LocalDateTime.of(2023, 5, 14, 11, 0), customer);
                appointment1.setId(1);
                appointment1.setStatus(AppointmentStatus.SCHEDULED);

                Appointment appointment2 = new Appointment(LocalDateTime.of(2023, 5, 16, 14, 0), LocalDateTime.of(2023, 5, 16, 15, 0), customer);
                appointment2.setId(2);
                appointment2.setStatus(AppointmentStatus.SCHEDULED);

                appointmentRepository.save(appointment1);
                appointmentRepository.save(appointment2);

                List<Appointment> scheduledAppointments = appointmentRepository.findScheduledWithEndBeforeDate(searchDate);

                assertEquals(1, scheduledAppointments.size());
                assertTrue(scheduledAppointments.contains(appointment1));
                assertFalse(scheduledAppointments.contains(appointment2));
            }


    /**
     * Crea 3 citas (Appointment) planificadas (SCHEDULED)
     * , 2 para un mismo cliente, con una elegible para cambio (con fecha de inicio, start, adecuada)
     * y otra no.
     * La tercera ha de ser de otro cliente.
     * Guárdalas mediante appointmentRepository.save
     * Comprueba que getEligibleAppointmentsForExchange encuentra la correcta.
     */
            @Test
            void getEligibleAppointmentsForExchangeTest() {

                Appointment appointment = new Appointment();

                Customer customer1 = new Customer();
                customer1.setId(1);
                Customer customer2 = new Customer();
                customer2.setId(2);

                LocalDateTime eligibilityStartDate = LocalDateTime.of(2023, 5, 20, 0, 0);

                Appointment appointment1 = new Appointment(LocalDateTime.of(2023, 5, 21, 10, 0), LocalDateTime.of(2023, 5, 21, 11, 0), customer1);
                appointment1.setId(1);
                appointment1.setStatus(AppointmentStatus.SCHEDULED);

                Appointment appointment2 = new Appointment(LocalDateTime.of(2023, 5, 15, 14, 0), LocalDateTime.of(2023, 5, 15, 15, 0), customer1);
                appointment2.setId(2);
                appointment2.setStatus(AppointmentStatus.SCHEDULED);

                Appointment appointment3 = new Appointment(LocalDateTime.of(2023, 5, 25, 9, 0), LocalDateTime.of(2023, 5, 25, 10, 0), customer2);
                appointment3.setId(3);
                appointment3.setStatus(AppointmentStatus.SCHEDULED);

                appointmentRepository.save(appointment1);
                appointmentRepository.save(appointment2);
                appointmentRepository.save(appointment3);

                List<Appointment> eligibleAppointments = appointmentRepository.getEligibleAppointmentsForExchange(eligibilityStartDate, customer1.getId());

                assertEquals(1, eligibleAppointments.size());
                assertTrue(eligibleAppointments.contains(appointment1));
                assertFalse(eligibleAppointments.contains(appointment2));
                assertFalse(eligibleAppointments.contains(appointment3));
            }


    /**
     * Igual que antes, pero ahora las 3 citas tienen que tener
     * clientes diferentes y 2 de ellas con fecha de inicio (start)
     * antes de la especificada en el método de búsqueda para
     * findExchangeRequestedWithStartBefore
     */
            @Test
            void findExchangeRequestedWithStartBeforeTest() {
                Appointment appointment = new Appointment();

                Customer customer1 = new Customer();
                customer1.setId(1);
                Customer customer2 = new Customer();
                customer2.setId(2);
                Customer customer3 = new Customer();
                customer3.setId(3);

                LocalDateTime searchDate = LocalDateTime.of(2023, 5, 20, 0, 0);

                Appointment appointment1 = new Appointment(LocalDateTime.of(2023, 5, 19, 10, 0), LocalDateTime.of(2023, 5, 19, 11, 0), customer1);
                appointment1.setId(1);
                appointment1.setStatus(AppointmentStatus.EXCHANGE_REQUESTED);

                Appointment appointment2 = new Appointment(LocalDateTime.of(2023, 5, 18, 14, 0), LocalDateTime.of(2023, 5, 18, 15, 0), customer2);
                appointment2.setId(2);
                appointment2.setStatus(AppointmentStatus.EXCHANGE_REQUESTED);

                Appointment appointment3 = new Appointment(LocalDateTime.of(2023, 5, 21, 9, 0), LocalDateTime.of(2023, 5, 21, 10, 0), customer3);
                appointment3.setId(3);
                appointment3.setStatus(AppointmentStatus.EXCHANGE_REQUESTED);

                appointmentRepository.save(appointment1);
                appointmentRepository.save(appointment2);
                appointmentRepository.save(appointment3);

                List<Appointment> exchangeRequestedAppointments = appointmentRepository.findExchangeRequestedWithStartBefore(searchDate);

                assertEquals(2, exchangeRequestedAppointments.size());
                assertTrue(exchangeRequestedAppointments.contains(appointment1));
                assertTrue(exchangeRequestedAppointments.contains(appointment2));
                assertFalse(exchangeRequestedAppointments.contains(appointment3));
            }
}