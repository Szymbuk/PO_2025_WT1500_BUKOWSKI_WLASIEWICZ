package project.model.util;

import project.model.Simulation;

import java.io.*;

public class StatsToFile {
    public static void saveToFile(Simulation simulation, String filePath) throws IOException {
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
}
