import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Dataset {

    public static List<List<List<Double>>> readFromFile(String path){
        List<List<List<Double>>> dataset=new LinkedList<>();
        try {
            List<String> lines= Files.readAllLines(Paths.get(path));
            for(String line:lines){
                List<String> xy=new LinkedList(Arrays.asList(line.split(" ")));
                List<Double> x=new LinkedList<>();
                List<Double> y=new LinkedList<>();
                boolean switched=false;
                for(String xs:xy){
                    if (xs.equals("#")){
                        switched=true;
                        continue;
                    }
                    if (!switched){
                        x.add(Double.parseDouble(xs));
                    }
                    else{
                        y.add(Double.parseDouble(xs));
                    }
                }
                List<List<Double>> example=new LinkedList<>();
                example.add(x);
                example.add(y);
                dataset.add(example);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dataset;
    }

    public static void writeDrawingsToFile(List<List<List<List<Double>>>> dataset,String path){
        List<String> lines=new LinkedList<>();
        for(List<List<List<Double>>> classDrawings:dataset){

            for(List<List<Double>> drawing:classDrawings) {
                String newLine = "";
                for (List<Double> point : drawing) {
                    newLine += point.get(0) + " " + point.get(1) + " ";
                }

                newLine += "#";
                for (int i = 0; i < dataset.size(); i++) {
                    if (dataset.get(i).equals(classDrawings)) {
                        newLine += " 1";
                    } else {
                        newLine += " 0";
                    }
                }
                lines.add(newLine);
            }
        }
        Path filePath=Paths.get(path);
        try {
            Files.write(filePath,lines);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
