package io.reloop.dpp.config;

import java.util.UUID;

/**
 * Company ID associé à la requête courante (déduit de X-API-KEY par le filtre).
 */
public final class CompanyContextHolder {

    private static final ThreadLocal<UUID> HOLDER = new ThreadLocal<>();

    public static void setCompanyId(UUID companyId) {
        HOLDER.set(companyId);
    }

    public static UUID getCompanyId() {
        return HOLDER.get();
    }

    public static void clear() {
        HOLDER.remove();
    }
}
