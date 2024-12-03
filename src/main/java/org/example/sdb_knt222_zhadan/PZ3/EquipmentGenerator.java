package org.example.sdb_knt222_zhadan.PZ3;

import java.text.SimpleDateFormat;
import java.util.*;

public class EquipmentGenerator {
    private static final String[] types = {
            "Мікроскоп", "Аналізатор крові", "Ультразвуковий сканер", "Електрокардіограф",
            "Центрифуга", "Рентгенівська установка", "Томограф", "Дефібрилятор",
            "Вентилятор легенів", "Інфузомат", "Фізіотерапевтичний апарат", "Монітор пацієнта"
    };

    private static final String[] model = {
            "MedTech", "BioSys", "HealthCorp", "DiagnoMax", "SurgiPro",
            "TheraWave", "ClinicaPlus", "LifeCare", "UltraScan", "CardioPro",
            "PhysioWave", "HealthLine", "VitalCore", "Medica", "BioHealth"
    };

    private static final String[] modelSuffixes = {
            "X100", "A200", "UltraScan Alpha", "Cardiologix 2100", "Centriflex T-500",
            "Radix Fusion X-Ray", "ClarityView 360", "LifeSaver AED Lite", "Ventura X200",
            "InfusioMax 3", "PhysioWave Ultra", "PatientMonitor Pro", "ThermoScan 500",
            "BioAnalyser Pro 300", "SurgiMax 4000"
    };

    private static final Random random = new Random();

    // Генерація серійного номера
    public static String generateSerialNumber() {
        return "SN-" + random.nextInt(1000000);
    }

    // Генерація моделі
    public static String generateModel() {
        String manufacturer = model[random.nextInt(model.length)];
        String suffix = modelSuffixes[random.nextInt(modelSuffixes.length)];
        return manufacturer + " " + suffix;
    }

    // Генерація типу
    public static String generateType() {
        return types[random.nextInt(types.length)];
    }

    // Генерація дати придбання
    public static String generatePurchaseDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -random.nextInt(5));
        calendar.add(Calendar.DAY_OF_YEAR, -random.nextInt(365));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(calendar.getTime());
    }

    // Генерація одного запису обладнання
    public static Map<String, String> generateEquipmentRecord() {
        Map<String, String> record = new HashMap<>();
        record.put("serial_number", generateSerialNumber());
        record.put("model", generateModel());
        record.put("type", generateType());
        record.put("purchase_date", generatePurchaseDate());
        return record;
    }

    // Генерація списку записів
    public static List<Map<String, String>> generateEquipmentList(int count) {
        List<Map<String, String>> equipmentList = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            equipmentList.add(generateEquipmentRecord());
        }
        return equipmentList;
    }
}
