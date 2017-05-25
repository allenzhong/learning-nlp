package allen.nlp.tagger;

/**
 * Created by allenzhong on 22/05/17.
 */

import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

public class TaggingProcessor {

    private String fileName;
    private File file;
    private String text;
    private List<TaggedWord> taggedWords;

    public TaggingProcessor(String fileName) {
        this.fileName = fileName;
    }

    public TaggingProcessor(File file) {
        this.file = file;
    }
    public List<TaggedWord> tagTextinFile() {
        this.text = this.readFile();
        this.taggedWords = this.taggingWords(this.text);
        return taggedWords;
    }

    protected List<TaggedWord> taggingWords(String text) {
        Properties props = new Properties();
        props.put("annotators", "tokenize, ssplit, pos");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

        Annotation document = new Annotation(text);
        pipeline.annotate(document);
        List<CoreMap> sentences = document.get(SentencesAnnotation.class);
        List<TaggedWord> wordsList = new ArrayList<>();
        for(CoreMap sentence: sentences) {
            for(CoreLabel token: sentence.get(TokensAnnotation.class)) {
                String word = token.get(TextAnnotation.class);

                String pos = token.get(PartOfSpeechAnnotation.class);
                TaggedWord taggedWord = new TaggedWord(word, pos);
                wordsList.add(taggedWord);
            }
        }
        return wordsList;
    }

    protected String readFile() {
        if(this.file == null) {
            this.file = new File(this.fileName);
        }
        StringBuilder text = new StringBuilder();
        LineIterator iterator = null;
        try {
            iterator = FileUtils.lineIterator(this.file, "UTF-8");
            while(iterator.hasNext()) {
                text.append(" ");
                text.append(iterator.nextLine());
            }
        }catch(IOException exception) {
            System.out.println(exception.getMessage());
        }finally {
            LineIterator.closeQuietly(iterator);
        }

        return text.toString();
    }
}
