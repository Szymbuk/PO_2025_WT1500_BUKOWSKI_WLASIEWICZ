package project.model.util;

import project.model.Simulation;

import java.io.*;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Scanner;

public class FileManager {
    private static final List<String> simulationParameters = List.of(
            "mapHeight",
            "mapWidth",
            "startMaleNumber",
            "startFemaleNumber",
            "habsburgEnergyLoss",
            "plantStartingQuantity",
            "plantEnergyGain",
            "plantDailyGrowth",
            "energyStartLevel",
            "energyDailLoss",
            "energyBreadingThreshold",
            "energyBreadingLoss",
            "minMutation",
            "maxMutation",
            "genomLength",
            "dayDurationMillis",
            "csvSave"
    );
    public static void saveSimulationDataToCSV(Simulation simulation, String filePath) throws IOException {
        File file = new File(filePath);
        boolean doesExist = file.exists();
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(file,true));){
            if (!doesExist) {
                String[] headerDraft = {"Day","MaleNumber", "FemaleNumber", "PlantNumber", "EmptyFieldsNumber", "MostPopularGenotype", "NumberOfOccurrence", "AverageEnergyLevel", "AverageLifeLength", "AverageChildNumber"};
                String header = "";
                for (String word : headerDraft) {
                    if (!header.equals("")) {
                        header = header + "\t" + word;
                    } else {
                        header += word;
                    }
                }
                writer.write(header);
                writer.newLine();
            }
            String dayString = "%d".formatted(simulation.getDay());
            String maleString = "%d".formatted(simulation.getMaleNumber());
            String femaleString = "%d".formatted(simulation.getFemaleNumber());
            String plantString = "%d".formatted(simulation.getCurrentPlants());
            String emptyFieldsString = "%d".formatted(simulation.getFreeSpace());
            String genotypeString = "%s ".formatted(simulation.getMostPopularGenotype())
                    .replace("[","")
                    .replace("]","")
                    .replace(",","-")
                    .replace(" ","")
                    + " ";
            String numberOfOccurenceString = "%d".formatted(simulation.getMostPopularGenotypeOccurrenceNumber());
            String energyLevelString = "%d".formatted(simulation.getAverageEnergy());
            String lifeLengthString = "%d".formatted(simulation.getAverageDeadLifeLength());
            String childNumberString = "%f".formatted(simulation.getAverageChildNumber().orElse(0.0));
            String record = String.join("\t",dayString,maleString,femaleString,plantString,emptyFieldsString,genotypeString,numberOfOccurenceString,energyLevelString,lifeLengthString,childNumberString);
            writer.write(record);
            writer.newLine();
        }
    }

    public static void saveSimulationParameters(Simulation simulation, String filePath) throws IOException{
        File file = new File(filePath);
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(file,true));){

            String header = "";
            for (String word : simulationParameters) {
                if (!header.equals("")) {
                    header = header + "\t" + word;
                } else {
                    header += word;
                }
            }
            writer.write(header);
            writer.newLine();

            //używamy refleksji w celu ograniczeniu getterów klasy simulation
            for (String parameter : simulationParameters){
                Field field = simulation.getClass().getDeclaredField(parameter);
                field.setAccessible(true);
                // bezpieczna konwersja, ponieważ to tylko liczby
                String anotherFieldValue = String.valueOf(field.get(simulation));
                field.setAccessible(false);
                writer.write(anotherFieldValue+"\t");
            }

        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

    }

    public static Simulation loadSimulationParameters(String filePath) throws IOException{
        File  file = new File(filePath);
        Simulation.SimulationBuilder builder = new Simulation.SimulationBuilder();
        try (Scanner scanner = new Scanner(file)) {
            if (!scanner.hasNextLine()) throw new IOException("Pusty plik!");
            String headerLine = scanner.nextLine();
            String[] headers = headerLine.split("\t");

            //Odczytujemy ostatnią linię danych
            String lastDataLine = null;
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (!line.isBlank()) {
                    lastDataLine = line;
                }
            }

            if (lastDataLine == null) throw new IOException("Plik zawiera tylko nagłówek!");
            String[] values = lastDataLine.split("\t");

            Class<?> builderClass = builder.getClass();

            for (int i = 0; i < headers.length; i++) {
                String fieldName = headers[i];
                String stringValue = values[i];

                try {
                    Field field = builderClass.getDeclaredField(fieldName);
                    field.setAccessible(true);

                    Class<?> fieldType = field.getType();
                    Object typedValue = convertToType(stringValue, fieldType);

                    field.set(builder, typedValue);

                } catch (NoSuchFieldException e) {
                    System.err.println("Pole w pliku nie istnieje w Builderze: " + fieldName);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Błąd dostępu do pola buildera", e);
                }
            }
        }
        return builder.build();
    }

// Metoda pomocnicza do konwersji Stringa na typ pola
private static Object convertToType(String value, Class<?> targetType) {
    if (targetType == int.class || targetType == Integer.class) {
        return (int) Double.parseDouble(value);
    } else if (targetType == double.class || targetType == Double.class) {
        return Double.parseDouble(value);
    } else if (targetType == boolean.class || targetType == Boolean.class) {
        return Boolean.parseBoolean(value);
    } else if (targetType == String.class) {
        return value;
    }
    throw new IllegalArgumentException("Nieobsługiwany typ pola: " + targetType.getName());
    }
}
