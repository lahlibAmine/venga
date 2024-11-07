**Task Title:** Implement Signature Processing History Service

**Task Tag (Jira):** VEN-933

**Task Description:**

Develop and enhance the `SignatureProcessingHistoryService` class in the `com.ram.venga.service` package to manage operations related to the `AuditUserAction` entity/table, specifically focusing on the history of signature processing actions such as 'Changement Signature'. This involves the following key functionalities:

1. **Retrieve Logs by Keyword:**
    - Implement the `getLogsByKeyword` method to fetch logs related to the change of signatures in the `Vente` entity based on a provided keyword (numBillet, oldSignature, newSignature, modifierEmail, modificationDate).
    - Utilize `AuditUserActionRepository` and `VenteRepository` to gather relevant data.

2. **Search Process by numBillet:**
    - Implement the `searchProcessByNumBillet` method to retrieve and map audit user actions by numBillet.
    - Collect vente IDs and create a map linking vente IDs to numBillet.
    - Fetch relevant `AuditUserAction` entries and map them to `SignatureProcessingHistoryDto`.

3. **Search Process by Other Keywords:**
    - Implement the `searchProcessByOthers` method to handle searches based on oldSignature, newSignature, modifierEmail, or modificationDate.
    - Gather relevant `AuditUserAction` entries and corresponding `Vente` entries.
    - Map the results to `SignatureProcessingHistoryDto`.

4. **DTO Builder:**
    - Implement the `signatureProcessingHistoryDtoBuilder` method to convert `AuditUserAction` entities into `SignatureProcessingHistoryDto` objects.
