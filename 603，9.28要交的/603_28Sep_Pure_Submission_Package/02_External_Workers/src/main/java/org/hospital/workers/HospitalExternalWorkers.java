package org.hospital.workers;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobWorker;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * External workers for the Initial Release.
 * External hospital/scheduling/payment/correspondence services are intentionally simulated.
 */
public class HospitalExternalWorkers implements AutoCloseable {
    private final ZeebeClient client;
    private final List<JobWorker> workers = new ArrayList<>();

    public HospitalExternalWorkers(ZeebeClient client) { this.client = client; }

    public void start() {
        register("search-appointment-slot", WorkerLogic::searchAppointmentSlot);
        register("send-appointment-letter", WorkerLogic::sendAppointmentLetter);
        register("check-treatment-resource", WorkerLogic::checkTreatmentResource);
        register("process-payment", WorkerLogic::processPayment);
        register("process-refund", WorkerLogic::processRefund);
        register("send-clinic-letter", WorkerLogic::sendClinicLetter);
    }

    private void register(String jobType, Logic logic) {
        workers.add(client.newWorker()
            .jobType(jobType)
            .handler((jobClient, job) -> handle(jobType, logic, jobClient, job))
            .timeout(Duration.ofSeconds(30))
            .name("hospital-" + jobType)
            .open());
    }

    private void handle(String jobType, Logic logic, JobClient jobClient, ActivatedJob job) {
        try {
            Map<String,Object> output = logic.apply(job.getVariablesAsMap());
            jobClient.newCompleteCommand(job.getKey()).variables(output).send().join();
        } catch (IllegalArgumentException e) {
            jobClient.newFailCommand(job.getKey())
                .retries(Math.max(job.getRetries() - 1, 0))
                .errorMessage(jobType + ": invalid input - " + e.getMessage()).send().join();
        } catch (Exception e) {
            jobClient.newFailCommand(job.getKey())
                .retries(Math.max(job.getRetries() - 1, 0))
                .errorMessage(jobType + ": simulated/external service failure - " + e.getMessage()).send().join();
        }
    }

    @FunctionalInterface interface Logic { Map<String,Object> apply(Map<String,Object> input); }

    @Override public void close() {
        for (JobWorker w : workers) w.close();
        client.close();
    }

    public static void main(String[] args) throws Exception {
        String gateway = System.getenv().getOrDefault("ZEEBE_ADDRESS", "127.0.0.1:26500");
        boolean plaintext = Boolean.parseBoolean(System.getenv().getOrDefault("ZEEBE_PLAINTEXT", "true"));
        var builder = ZeebeClient.newClientBuilder().gatewayAddress(gateway);
        if (plaintext) builder.usePlaintext();
        ZeebeClient client = builder.build();
        HospitalExternalWorkers app = new HospitalExternalWorkers(client);
        app.start();
        Runtime.getRuntime().addShutdownHook(new Thread(app::close));
        Thread.currentThread().join();
    }
}
