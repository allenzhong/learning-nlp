package allen.nlp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import allen.nlp.tagger.TaggedWord;
import allen.nlp.tagger.TaggingProcessor;
import allen.nlp.tagger.MultipleFilesTagger;
import allen.nlp.classifier.*;

public class Main {

    public static void main(String[] args) {
        String filePath = "/Users/allenzhong/Projects/NLP/frequency/data";
        DocumentsClassifier classifier = new DocumentsClassifier();
        classifier.setCategories(new String[]{"CCAT", "OTHERS"});
        classifier.setTrainingDataPath(filePath);
        classifier.setTestingDataPath(filePath);
        try {
            classifier.start();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
//        TaggedWord w1 = new TaggedWord("aaa","aaa");
//        TaggedWord w2 = new TaggedWord("aaa","aaa");
//        System.out.println(w1.equals(w2));
//	    String filePath = "/Users/allenzhong/Projects/NLP/frequency/data/CCAT";
//	    MultipleFilesTagger tagger = new MultipleFilesTagger(filePath, ".*.txt$");
//        tagger.writeToCSV("CCAT.csv");
//        TaggingProcessor processor = new TaggingProcessor(filePath);
//        List<TaggedWord> list = processor.tagTextinFile();
//        for(TaggedWord word : list) {
//            System.out.printf("%s : %s \n", word.getWord(), word.getTag());
//        }
    }
}
