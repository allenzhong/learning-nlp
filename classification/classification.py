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



def classfication_by_naive_bayes(path):
	mr = CategorizedPlaintextCorpusReader(path, r'.*/.*', cat_pattern=r'(.*)/.*') 
	stop = stopwords.words('rainbow')
	documents = [([w for w in mr.words(i) if w.lower() not in stop and w.lower() not in string.punctuation], i.split('/')[0]) for i in mr.fileids()]

	word_features = FreqDist(chain(*[i for i,j in documents]))
	word_features = word_features.keys()[:12000]
	print len(word_features)

	numtrain = int(len(documents) * 90 / 100)
	train_set = [({i:(i in tokens) for i in word_features}, tag) for tokens,tag in documents[:numtrain]]
	test_set = [({i:(i in tokens) for i in word_features}, tag) for tokens,tag  in documents[numtrain:]]

	classifier = nbc.train(train_set)
	print("NaiveBayes accuracy percent:", nltk.classify.accuracy(classifier, test_set))
	classifier.show_most_informative_features(10)


def classfication_by_sklearn(path):
	mr = CategorizedPlaintextCorpusReader(path, r'.*/.*', cat_pattern=r'(.*)/.*') 
	stop = stopwords.words('rainbow')
	documents = [([w for w in mr.words(i) if w.lower() not in stop and w.lower() not in string.punctuation], i.split('/')[0]) for i in mr.fileids()]

	word_features = FreqDist(chain(*[i for i,j in documents]))
	word_features = word_features.keys()[:12000]

	numtrain = int(len(documents) * 90 / 100)
	train_set = [({i:(i in tokens) for i in word_features}, tag) for tokens,tag in documents[:numtrain]]
	test_set = [({i:(i in tokens) for i in word_features}, tag) for tokens,tag  in documents[numtrain:]]

	MNB_classifier = SklearnClassifier(MultinomialNB())
	MNB_classifier.train(train_set)
	print("MultinomialNB accuracy percent:",nltk.classify.accuracy(MNB_classifier, test_set))

	BNB_classifier = SklearnClassifier(BernoulliNB())
	BNB_classifier.train(train_set)
	print("BernoulliNB accuracy percent:",nltk.classify.accuracy(BNB_classifier, test_set))



if __name__ == "__main__":

	path = "./balanced/"
	classfication_by_naive_bayes(path)
	classfication_by_sklearn(path)

