package allen.nlp.tagger;

public class TaggedWord  implements Comparable<TaggedWord> {
    private String word;
    private String tag;
    private int count;

    public TaggedWord() {
        this.count = 1;
    }

    public TaggedWord(String word, String tag){
       this.word = word;
       this.tag = tag;
       this.count = 1;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public int compareTo(TaggedWord o) {
        int result = this.getWord().compareTo(o.getWord()) | this.getTag().compareTo(o.getTag());
        if(result == 0) {
            o.setCount(o.getCount() + 1);
        }
        return result;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
//    public boolean equals(TaggedWord o) {
//        return (this.getWord().equals(o.getWord()) && this.getTag().equals(o.getTag()));
//    }
//
//    public int hashCode() {
//        return this.getWord().hashCode() + this.getTag().hashCode();
//    }
}
