# Deployment Configuration

1. Open `01_Operational_Model/603_Operational_BPMN_Executable.bpmn` in Camunda Modeler.
2. Deploy the BPMN model and all `.form` files in `03_Camunda_Forms` to the same Camunda environment.
3. Start the external workers using the connection values in `worker.env.template`.
4. Start a process instance for `Process_Hospital`.
5. Complete user tasks in Tasklist. Service tasks are handled by the six worker job types listed in the worker README.
6. Use the demonstration scenarios in `06_Demonstration/DEMO_SCENARIOS.md`.

## Configuration-management evidence
For final Blackboard submission, record the actual Git tag/commit used for the demonstration in `SUBMITTED_VERSION.txt` and keep the repository link accessible to the tutors.
