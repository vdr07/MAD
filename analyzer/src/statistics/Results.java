package results;

import java.util.HashMap;

public class Results {

    private long executionTime;
    private int coreAnomalies = 0;
    private int totalAnomalies = 0;

    private HashMap<String, Integer> anomalyInstances;
    private HashMap<String, AnomalyOccurances> perEntiteInstances;
    private HashMap<String, FunctionalityAnomalies> perSubTransactionAnomaly;

    public Results(){
        this.anomalyInstances = new HashMap<>();
        this.perEntiteInstances = new HashMap<>();
        this.perSubTransactionAnomaly = new HashMap<>();
    }
    public void addAnomaly(String anomalyType, String entitities, String subTransactions, String transactions){

        totalAnomalies++;

        if (anomalyType != "Extensions"){
            coreAnomalies++;
            this.anomalyInstances.put(anomalyType, this.anomalyInstances.getOrDefault(anomalyType, 0) +1);
            AnomalyOccurances occurances = this.perEntiteInstances.computeIfAbsent(entitities, k -> new AnomalyOccurances());
            occurances.addAnomaly(anomalyType);

            FunctionalityAnomalies subTxnAnomalies = this.perSubTransactionAnomaly.computeIfAbsent(subTransactions, k -> new FunctionalityAnomalies(transactions));
            subTxnAnomalies.addAnomaly(anomalyType);
        }

    }

    public void setFinalStatistics(long executionTime){
        this.executionTime = executionTime;
    }

    public long getExecutionTime() { return executionTime; }
    public int getCoreAnomalies() { return coreAnomalies; }
    public int getTotalAnomalies() { return totalAnomalies; }
    public HashMap<String, Integer> getAnomalyInstances() { return anomalyInstances; }
    public HashMap<String, AnomalyOccurances> getPerEntiteInstances() { return perEntiteInstances; }
    public HashMap<String, FunctionalityAnomalies> getPerSubTransactionAnomaly() { return perSubTransactionAnomaly; }

}