import re
import os
import string
import random
import shutil
from itertools import chain

from nltk.corpus import stopwords
from nltk.probability import FreqDist
from nltk.classify import NaiveBayesClassifier as nbc
from nltk.classify.scikitlearn import SklearnClassifier
from sklearn.naive_bayes import MultinomialNB,BernoulliNB
from nltk.corpus import CategorizedPlaintextCorpusReader
import nltk

def camel_case_split2(string):
    # set the logic for creating a "break"
    def is_transition(c1, c2):
      return c1.islower() and c2.isupper()

    # start the builder list with the first character
    # enforce upper case
    bldr = [string[0].upper()]
    for c in string[1:]:
        # get the last character in the last element in the builder
        # note that strings can be addressed just like lists
        previous_character = bldr[-1][-1]
        if is_transition(previous_character, c):
            # start a new element in the list
            bldr.append(c.lower())
        else:
            # append the character to the last string
            bldr[-1] += c
    return bldr

def create_corpus(path):
	mr = CategorizedPlaintextCorpusReader(path, r'.*/.*', cat_pattern=r'(.*)/.*') 
	stop = stopwords.words('rainbow')

	documents = [([w for w in mr.words(i) if w.lower() not in stop and w.lower() not in string.punctuation], i.split('/')[0]) for i in mr.fileids()]
	random.shuffle(documents)
	word_features = FreqDist(chain(*[i for i,j in documents]))
	word_features = word_features.keys()[:3000]
	# print len(word_features)
	new_features = []
	for word in word_features:
		splited_words = camel_case_split2(word)
		[new_features.append(new_word) for new_word in splited_words]

	# print len(new_features)
	numtrain = int(len(documents) * 66 / 100)

	train_set = [({i:(i in tokens) for i in new_features}, tag) for tokens,tag in documents[:numtrain]]
	test_set = [({i:(i in tokens) for i in new_features}, tag) for tokens,tag  in documents[numtrain:]]
	return (train_set, test_set)

def classfication_by_naive_bayes(training_set, testing_set):
	classifier = nbc.train(training_set)
	print("NaiveBayes accuracy percent:", nltk.classify.accuracy(classifier, testing_set))
	classifier.show_most_informative_features(10)

def classfication_by_sklearn(training_set, testing_set):
	MNB_classifier = SklearnClassifier(MultinomialNB())
	MNB_classifier.train(training_set)
	print("MultinomialNB accuracy percent:",nltk.classify.accuracy(MNB_classifier, testing_set))

	BNB_classifier = SklearnClassifier(BernoulliNB())
	BNB_classifier.train(training_set)
	print("BernoulliNB accuracy percent:",nltk.classify.accuracy(BNB_classifier, testing_set))

def main():
	path = "./balanced/"
	(training_set, testing_set) = create_corpus(path)
	classfication_by_naive_bayes(training_set, testing_set)
	classfication_by_sklearn(training_set, testing_set)

if __name__ == "__main__":
	main()


