# Initial Release Demonstration Scenarios

## Scenario A - Normal Patient Journey

Use this as the primary demonstration.

1. Start `Process_Hospital` with a new referral.
2. Medical Secretary records referral and sets `informationComplete=true`.
3. Consultant chooses `referralDecision=ACCEPT` and supplies speciality/timeframe.
4. Appointment booking sets `requestedSlotAvailable=true` and an appointment date within 14 days.
5. `search-appointment-slot` returns `slotAvailable=true` and `appointmentWithinTwoWeeks=true`.
6. `send-appointment-letter` completes and records a simulated correspondence reference.
7. Telephone task is completed with `contactOutcome=ANSWERED`.
8. Clinical assessment records `patientConsent=true`.
9. Treatment request sets `treatmentAuthorised=true` and `requestedTreatmentAvailable=true`.
10. `check-treatment-resource` returns `treatmentAvailable=true`.
11. Funding route uses `fundingType=PATIENT`; simulated payment uses `paymentScenario=SUCCESSFUL`.
12. `process-payment` returns `paymentStatus=SUCCESSFUL` and a transaction reference.
13. Treatment completes with `moreCycles=false`.
14. Consultant prepares/approves the Clinic Letter.
15. `send-clinic-letter` distributes the letter; set `clinicLetterWithin7Days=true`.
16. Process reaches the normal completion event.

Evidence to capture: process instance, completed user tasks, worker output variables, final state, Git commit/tag.

## Scenario B - Alternative / Failure Journey

1. Start an accepted referral.
2. Appointment request sets `requestedSlotAvailable=false`.
3. Demonstrate no-slot/pathway escalation route.
4. Continue with a slot and proceed to payment.
5. Set `paymentScenario=CONFIRMATION_LOST`.
6. Demonstrate that the process routes to investigation rather than automatically charging again.
7. If using the Clinic Letter branch, set `clinicLetterWithin7Days=false` and demonstrate monitoring/escalation.

Evidence to capture: alternative gateway route, worker result, investigation task, monitoring/escalation task.

## Contribution Explanation Order

- DENG JIN WANG - Referral & Consultant Review; referral/consultant forms and rules.
- WU JIA WEI - Appointment & Patient Communication; scheduling and appointment-letter workers.
- LIN JIA QIANG - Treatment & Chemotherapy; treatment request/form and resource worker.
- WU TONG YU - Funding / Payment / Refund; funding form and payment/refund workers.
- XIAO YU YING - Enquiry / Clinic Letter / Pathway Monitoring; enquiry/letter forms and clinic-letter distribution worker.
