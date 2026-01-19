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
            String dayString = "%d\t".formatted(simulation.getDay());
            String maleString = "%d\t".formatted(simulation.getMaleNumber());
            String femaleString = "%d\t".formatted(simulation.getFemaleNumber());
            String plantString = "%d\t".formatted(simulation.getCurrentPlants());
            String emptyFieldsString = "%d\t".formatted(simulation.getFreeSpace());
            String genotypeString = "%s ".formatted(simulation.getMostPopularGenotype())
                    .replace("[","")
                    .replace("]","")
                    .replace(",","-")
                    .replace(" ","")
                    + "\t";
            String numberOfOccurenceString = "%d\t".formatted(simulation.getMostPopularGenotypeOccurrenceNumber());
            String energyLevelString = "%d\t".formatted(simulation.getAverageEnergy());
            String lifeLengthString = "%d\t".formatted(simulation.getAverageDeadLifeLength());
            String childNumberString = "%f\t".formatted(simulation.getAverageChildNumber().orElse(0.0));
            writer.write(dayString+maleString+femaleString+plantString+emptyFieldsString+genotypeString+numberOfOccurenceString+energyLevelString+lifeLengthString+childNumberString);
            writer.newLine();
        }
    }
}
