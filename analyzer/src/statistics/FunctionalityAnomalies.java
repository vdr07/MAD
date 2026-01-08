package results;

import java.util.List;

public class FunctionalityAnomalies {

    private AnomalyOccurances anomalies;
    private String functionalities;


    public FunctionalityAnomalies(String functionalities){
        this.functionalities = functionalities;
        this.anomalies = new AnomalyOccurances();
    }

    public void addAnomaly(String anomalyType){
        this.anomalies.addAnomaly(anomalyType);
    }

    public AnomalyOccurances getAnomalies(){
        return anomalies;
    }

    public String getFunctionalityAnomalies(){
        return functionalities;
    }
}