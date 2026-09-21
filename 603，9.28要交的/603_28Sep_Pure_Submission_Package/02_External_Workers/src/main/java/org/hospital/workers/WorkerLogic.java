package org.hospital.workers;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** Pure business/simulation logic used by the Zeebe workers and by local component tests. */
public final class WorkerLogic {
    private WorkerLogic() {}

    public static Map<String, Object> searchAppointmentSlot(Map<String, Object> input) {
        boolean available = bool(input.get("requestedSlotAvailable"), true);
        Map<String, Object> out = new HashMap<>();
        out.put("slotAvailable", available);
        out.put("schedulingServiceSimulated", true);
        if (available) {
            LocalDate date = parseDate(input.get("requestedAppointmentDate"));
            if (date == null) date = LocalDate.now().plusDays(10);
            out.put("appointmentDate", date.toString());
            long days = ChronoUnit.DAYS.between(LocalDate.now(), date);
            out.put("appointmentWithinTwoWeeks", days >= 0 && days <= 14);
        }
        return out;
    }

    public static Map<String, Object> sendAppointmentLetter(Map<String, Object> input) {
        return Map.of(
            "appointmentLetterSent", true,
            "appointmentCorrespondenceReference", "APT-" + shortId(),
            "correspondenceServiceSimulated", true
        );
    }

    public static Map<String, Object> checkTreatmentResource(Map<String, Object> input) {
        boolean available = bool(input.get("requestedTreatmentAvailable"), true);
        return Map.of(
            "treatmentAvailable", available,
            "treatmentResourceServiceSimulated", true,
            "resourceCheckReference", "RES-" + shortId()
        );
    }

    public static Map<String, Object> processPayment(Map<String, Object> input) {
        String scenario = str(input.get("paymentScenario"), "SUCCESSFUL").toUpperCase();
        Map<String, Object> out = new HashMap<>();
        out.put("paymentStatus", scenario);
        out.put("paymentProviderSimulated", true);
        out.put("paymentDate", LocalDate.now().toString());
        if ("SUCCESSFUL".equals(scenario)) out.put("transactionReference", "PAY-" + shortId());
        if ("CONFIRMATION_LOST".equals(scenario) || "DUPLICATE_RISK".equals(scenario)) out.put("paymentInvestigationRequired", true);
        return out;
    }

    public static Map<String, Object> processRefund(Map<String, Object> input) {
        boolean approved = bool(input.get("refundRequired"), false);
        if (!approved) return Map.of("refundStatus", "NOT_REQUIRED", "paymentProviderSimulated", true);
        return Map.of(
            "refundStatus", "APPROVED_AND_SENT",
            "refundReference", "REF-" + shortId(),
            "paymentProviderSimulated", true
        );
    }

    public static Map<String, Object> sendClinicLetter(Map<String, Object> input) {
        boolean approved = bool(input.get("clinicLetterApproved"), false);
        if (!approved) throw new IllegalArgumentException("Clinic Letter must be clinically approved before distribution");
        return Map.of(
            "clinicLetterSent", true,
            "clinicLetterDistributionReference", "CL-" + shortId(),
            "correspondenceServiceSimulated", true
        );
    }

    private static boolean bool(Object v, boolean dflt) {
        if (v == null) return dflt;
        if (v instanceof Boolean b) return b;
        return Boolean.parseBoolean(v.toString());
    }
    private static String str(Object v, String dflt) { return v == null ? dflt : v.toString(); }
    private static LocalDate parseDate(Object v) {
        if (v == null || v.toString().isBlank()) return null;
        try { return LocalDate.parse(v.toString().substring(0,10)); } catch (Exception e) { return null; }
    }
    private static String shortId() { return UUID.randomUUID().toString().substring(0,8).toUpperCase(); }
}
