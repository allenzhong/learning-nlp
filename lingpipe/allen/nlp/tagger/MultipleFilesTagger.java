package allen.nlp.tagger;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.io.File;

public class MultipleFilesTagger {
    private String directory;
    private String filePattern;

    public MultipleFilesTagger(String directory, String filePattern) {
        this.directory = directory;
        this.filePattern = filePattern;
    }

    public void writeToCSV(String fileName) throws FileNotFoundException {
        Set<TaggedWord> wordsSet = getTaggedSet();

        PrintWriter pw;
        pw = new PrintWriter(new File(fileName));
        StringBuffer sb = new StringBuffer();
        for(TaggedWord word : wordsSet) {
            sb.append("\""+word.getWord()+"\"");
            sb.append(",");
            sb.append(word.getTag()).append(",");
            sb.append(word.getCount());
            sb.append("\n");
        }
        pw.write(sb.toString());
        pw.close();
        System.out.printf("Words count: %d", wordsSet.size());
    }
    public void tagFilesInDirectory() {
       List<File> filesList = this.findFilesFromDirectory();
       for(File file : filesList) {
           TaggingProcessor processor = new TaggingProcessor(file);
           List<TaggedWord> list = processor.tagTextinFile();
           for(TaggedWord word : list) {
               System.out.printf("%s : %s \n", word.getWord(), word.getTag());
           }
       }
    }

    public Set<TaggedWord> getTaggedSet() {
        List<File> filesList = this.findFilesFromDirectory();
        Set<TaggedWord> taggedSet = new TreeSet<>();

        for(File file : filesList) {
            TaggingProcessor processor = new TaggingProcessor(file);
            List<TaggedWord> list = processor.tagTextinFile();
            for(TaggedWord word : list) {
                taggedSet.add(word);
            }
        }

        return taggedSet;
    }

    private List<File> findFilesFromDirectory() {
        File dir = new File(this.directory);
        File[] files = dir.listFiles();
        List<File> filesList= new ArrayList<File>();
        for(File file : files) {
            if(!file.isFile())
                continue;
            Pattern pattern = Pattern.compile(this.filePattern);
            Matcher matcher = pattern.matcher(file.getName());
            if(matcher.matches()) {
                filesList.add(file);
            }
        }
        return filesList;
    }
}
