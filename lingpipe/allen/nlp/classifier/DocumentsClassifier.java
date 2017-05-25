package allen.nlp.classifier;

import com.aliasi.classify.*;

import com.aliasi.lm.NGramProcessLM;

import com.aliasi.util.AbstractExternalizable;

import java.io.File;
import java.io.IOException;
import java.util.*;

import com.aliasi.util.Files;

public class DocumentsClassifier {
    private static int NGRAM_SIZE = 3;
    private HashMap<String, List<File>> trainingMap;
    private HashMap<String, List<File>> testingMap;
    private String trainingDataPath;
    private String testingDataPath;

    private String[] categories;

    public DocumentsClassifier() {
        trainingMap = new HashMap<>();
        testingMap = new HashMap<>();
    }

    public String getTrainingDataPath() {
        return trainingDataPath;
    }

    public void setTrainingDataPath(String trainingDataPath) {
        this.trainingDataPath = trainingDataPath;
    }

    public String getTestingDataPath() {
        return testingDataPath;
    }

    public void setTestingDataPath(String testingDataPath) {
        this.testingDataPath = testingDataPath;
    }

    public String[] getCategories() {
        return categories;
    }

    public void setCategories(String[] categories) {
        this.categories = categories;
    }

    public void start() throws IOException, ClassNotFoundException {
        JointClassifier classifier = createClassifier();
        boolean storeCategories = true;
        JointClassifierEvaluator<CharSequence> evaluator
                = new JointClassifierEvaluator<CharSequence>(classifier,
                getCategories(),
                storeCategories);
        for(int i = 0; i < getCategories().length; ++i)
        {
//            File classDir = new File(getTestingDataPath(),getCategories()[i]);
            List<File> testingList = this.testingMap.get(getCategories()[i]);
            System.out.printf("Size of testing: " + testingList.size());
            System.out.println();
            for (int j=0; j<testingList.size();  ++j) {
                String text
                        = Files
                        .readFromFile(testingList.get(j),"ISO-8859-1");
//                System.out.print("Testing on " + getCategories()[i] + "/" + testingList.get(j) + " ");
                Classification classification
                        = new Classification(getCategories()[i]);
                Classified<CharSequence> classified
                        = new Classified<>(text, classification);
                evaluator.handle(classified);
                JointClassification jc =
                        classifier.classify(text);
                String bestCategory = jc.bestCategory();
                String details = jc.toString();
//                System.out.println("Details: " + details.toString());
//                System.out.println("Got best category of: " + bestCategory);
//                System.out.println(jc.toString());
//                System.out.println("---------------");
            }
        }
        ConfusionMatrix confMatrix = evaluator.confusionMatrix();
        System.out.println("Total Accuracy: " + confMatrix.totalAccuracy());

//        System.out.println("\nFULL EVAL");
//        System.out.println(evaluator);
    }


    private void randomSelectFiles(String category, List<File> files, double percentage) {
        int trainingSize = (int)(files.size() * percentage);
        System.out.println(category + " - train size: " + trainingSize + "in all: " + files.size());

        List<File> trainingList = new ArrayList<>();

        Set<Integer> numbers = generateRandomNumbers(files.size() - 1, trainingSize);
        Iterator<Integer> it = numbers.iterator();
        while(it.hasNext()) {
            trainingList.add(files.get(it.next()));
        }

        ArrayList<File> removingList = new ArrayList<>(files);
        removingList.removeAll(trainingList);
        System.out.println(category + " - testing size: " + removingList.size() + "in all: " + files.size());
        trainingMap.put(category, trainingList);
        testingMap.put(category, removingList);
    }

    private Set<Integer> generateRandomNumbers(Integer size, Integer numbersNeeded ) {
        if (size < numbersNeeded)
        {
            throw new IllegalArgumentException("Can't ask for more numbers than are available");
        }
        Random rng = new Random(); // Ideally just create one instance globally
// Note: use LinkedHashSet to maintain insertion order
        Set<Integer> generated = new LinkedHashSet<Integer>();
        while (generated.size() < numbersNeeded)
        {
            Integer next = rng.nextInt(size) + 1;
            // As we're adding to a set, this will automatically do a containment check
            generated.add(next);
        }
        return generated;
    }
    private JointClassifier createClassifier() throws IOException, ClassNotFoundException {
        DynamicLMClassifier<NGramProcessLM> classifier = DynamicLMClassifier.createNGramProcess(getCategories(), NGRAM_SIZE);
        for(int i=0; i<getCategories().length; ++i) {
            File classDir = new File(getTrainingDataPath(),getCategories()[i]);
            if (!classDir.isDirectory()) {
                String msg = "Could not find dataset";
                System.out.println(msg); // in case exception gets lost in shell
                throw new IllegalArgumentException(msg);
            }
            File[] files = classDir.listFiles();
            List<File> fileList = Arrays.asList(files);
            this.randomSelectFiles(getCategories()[i], fileList, 0.66);
            List<File> trainingFiles = this.trainingMap.get(getCategories()[i]);
            System.out.printf("Size of training: " + trainingFiles.size());
            System.out.println();
            for (int j = 0; j < trainingFiles.size(); ++j) {
                String text = Files.readFromFile(trainingFiles.get(j),"ISO-8859-1");
//                System.out.println("Training on " + getCategories()[i] + "/" + trainingFiles.get(j));
                Classification classification
                        = new Classification(getCategories()[i]);
                Classified<CharSequence> classified
                        = new Classified<>(text,classification);
                classifier.handle(classified);
            }
        }
        //compiling
        System.out.println("Compiling");
        JointClassifier<CharSequence> compiledClassifier
                = (JointClassifier<CharSequence>)
                AbstractExternalizable.compile(classifier);
        return compiledClassifier;
    }
}
