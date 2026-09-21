# External Workers - Initial Release

The BPMN model uses six external worker job types:

| Job type | Purpose | Primary module owner |
|---|---|---|
| `search-appointment-slot` | Simulates the external scheduling service and returns `slotAvailable`, `appointmentDate`, and `appointmentWithinTwoWeeks` | WU JIA WEI |
| `send-appointment-letter` | Simulates the external correspondence service | WU JIA WEI |
| `check-treatment-resource` | Simulates treatment/lab/imaging availability | LIN JIA QIANG |
| `process-payment` | Simulates the external Payment Service Provider | WU TONG YU |
| `process-refund` | Simulates an authorised refund request | WU TONG YU |
| `send-clinic-letter` | Simulates distribution through the correspondence service | XIAO YU YING |

DENG JIN WANG owns Referral & Consultant Review configuration/forms; that module has no case-study external automated service before referral acceptance, so it does not invent an unnecessary worker.

## Dependency information
- Java 17+
- Maven 3.8+
- `io.camunda:zeebe-client-java` (version in `pom.xml`; align with the lab Camunda version if required)

## Run
Set environment variables shown in `../07_Configuration/worker.env.template`, then:

```bash
mvn package
mvn exec:java -Dexec.mainClass=org.hospital.workers.HospitalExternalWorkers
```

All external services in this submission are simulations. The code explicitly marks simulated results with variables such as `paymentProviderSimulated=true`.
