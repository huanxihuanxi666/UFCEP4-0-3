package org.hospital.workers;

import java.time.LocalDate;
import java.util.Map;

/** No external test framework required: compile and run with javac/java for evidence. */
public class WorkerLogicSelfTest {
    private static int passed = 0;
    public static void main(String[] args) {
        testSlotAvailableWithinTwoWeeks();
        testNoSlot();
        testPaymentSuccess();
        testPaymentConfirmationLost();
        testTreatmentUnavailable();
        testClinicLetterApprovalRequired();
        testRefund();
        System.out.println("PASSED=" + passed + "/7");
    }
    private static void ok(boolean condition, String name) {
        if (!condition) throw new AssertionError(name);
        passed++; System.out.println("PASS " + name);
    }
    private static void testSlotAvailableWithinTwoWeeks() {
        var r=WorkerLogic.searchAppointmentSlot(Map.of("requestedSlotAvailable",true,"requestedAppointmentDate",LocalDate.now().plusDays(7).toString()));
        ok(Boolean.TRUE.equals(r.get("slotAvailable")) && Boolean.TRUE.equals(r.get("appointmentWithinTwoWeeks")), "appointment slot + two-week rule");
    }
    private static void testNoSlot() { ok(Boolean.FALSE.equals(WorkerLogic.searchAppointmentSlot(Map.of("requestedSlotAvailable",false)).get("slotAvailable")), "no-slot route"); }
    private static void testPaymentSuccess() { ok("SUCCESSFUL".equals(WorkerLogic.processPayment(Map.of("paymentScenario","SUCCESSFUL")).get("paymentStatus")), "payment success"); }
    private static void testPaymentConfirmationLost() { var r=WorkerLogic.processPayment(Map.of("paymentScenario","CONFIRMATION_LOST")); ok(Boolean.TRUE.equals(r.get("paymentInvestigationRequired")), "payment confirmation-lost investigation"); }
    private static void testTreatmentUnavailable() { ok(Boolean.FALSE.equals(WorkerLogic.checkTreatmentResource(Map.of("requestedTreatmentAvailable",false)).get("treatmentAvailable")), "treatment resource unavailable"); }
    private static void testClinicLetterApprovalRequired() { boolean failed=false; try { WorkerLogic.sendClinicLetter(Map.of("clinicLetterApproved",false)); } catch(IllegalArgumentException e){ failed=true; } ok(failed,"clinic-letter approval rule"); }
    private static void testRefund() { ok("APPROVED_AND_SENT".equals(WorkerLogic.processRefund(Map.of("refundRequired",true)).get("refundStatus")), "refund processing"); }
}
