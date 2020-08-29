package com.salesappmedicento.networking.data;

import java.util.ArrayList;

public class GetMedicineResponse {

    private String message;

    private ArrayList<MedicineResponse> medicines;

    public GetMedicineResponse(String message, ArrayList<MedicineResponse> medicineResponses) {
        this.message = message;
        this.medicines = medicineResponses;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ArrayList<MedicineResponse> getMedicineResponses() {
        return medicines;
    }

    public void setMedicineResponses(ArrayList<MedicineResponse> medicineResponses) {
        this.medicines = medicineResponses;
    }
}
