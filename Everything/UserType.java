package Everything;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

/**
 * Account types and their access levels.
 *
 * CLIENT    - the patient. Books and cancels only their own appointments.
 * DENTIST   - sees the whole agenda, confirms or cancels appointments (the patient is
 *             notified), but does not book.
 * SECRETARY - the front desk. Sees the whole agenda, books and cancels for any patient
 *             and registers new patients.
 * STAFF     - administrative staff. Everything the secretary does, plus creating
 *             accounts of any type.
 */
public enum UserType {
    CLIENT("Client", EnumSet.of(
            Permission.VIEW_OWN_APPOINTMENTS,
            Permission.BOOK_FOR_SELF,
            Permission.CANCEL_OWN)),

    DENTIST("Dentist", EnumSet.of(
            Permission.VIEW_ALL_APPOINTMENTS,
            Permission.RESPOND_TO_APPOINTMENTS)),

    SECRETARY("Secretary", EnumSet.of(
            Permission.VIEW_ALL_APPOINTMENTS,
            Permission.BOOK_FOR_PATIENTS,
            Permission.CANCEL_ANY,
            Permission.REGISTER_PATIENTS)),

    STAFF("Staff", EnumSet.allOf(Permission.class));

    private final String label;
    private final Set<Permission> permissions;

    UserType(String label, Set<Permission> permissions) {
        this.label = label;
        this.permissions = Collections.unmodifiableSet(permissions);
    }

    public String getLabel() {
        return label;
    }

    public Set<Permission> getPermissions() {
        return permissions;
    }

    public boolean can(Permission permission) {
        return permissions.contains(permission);
    }
}
