package Everything;

/**
 * Every action in the system that depends on who is logged in.
 * Which account type gets which permission is defined in one place: {@link UserType}.
 */
public enum Permission {
    VIEW_OWN_APPOINTMENTS,   // see the appointments that belong to my own account
    VIEW_ALL_APPOINTMENTS,   // see every appointment and who booked it
    BOOK_FOR_SELF,           // book a time slot for my own account
    BOOK_FOR_PATIENTS,       // book a time slot on behalf of a registered patient
    CANCEL_OWN,              // cancel an appointment that belongs to my own account
    CANCEL_ANY,              // cancel any appointment
    RESPOND_TO_APPOINTMENTS, // confirm an appointment, or cancel it with a reason; the patient is notified
    REGISTER_PATIENTS,       // create patient (client) accounts
    MANAGE_ACCOUNTS          // create accounts of any type (dentist, secretary, staff...)
}
