from __future__ import division, unicode_literals
import math
import os
from os import listdir
from os.path import isfile, join
from sets import Set

from textblob import TextBlob as tb

def tf(word, blob):
    return blob.words.count(word) / len(blob.words)

def n_containing(word, bloblist):
    return sum(1 for blob in bloblist if word in blob.words)

def idf(word, bloblist):
    return math.log(len(bloblist) / (1 + n_containing(word, bloblist)))

def tfidf(word, blob, bloblist):
    return tf(word, blob) * idf(word, bloblist)

def read_file(file_path):
    data = ''
    with open(file_path, 'r') as text_file:
        data = text_file.read().replace('\n', '')

    return data


def get_file_list(path):
    files = [f for f in listdir(path) if isfile(join(path, f))]
    return files

def build_documents(path):
    documents = []
    dirs = os.listdir(path)
    for item in dirs:
        files = get_file_list(os.path.join(path, item))
        for single_file in files:
            data = read_file(os.path.join(path, item, single_file))
            documents.append(tb(data))

    return documents

def find_words(bloblist):
    words = Set()
    for i, blob in enumerate(bloblist):
        scores = { word: idf(word, bloblist) for word in blob.words }
        sorted_words = sorted(scores.items(), key=lambda x:x[1])
                # sorted_words = sorted(scores.items(), key=lambda x:x[1], reverse=True)
        for word, score in sorted_words[:20]:
            words.add(word)
            print("\tWord: {}, Score: {}".format(word, round(score, 5)))

    return words

def read_stopwords(path):
    list = []
    with open(path, 'r') as file:
        for line in file:
            list.append(line)

    return list

def compare_to_stopwords(list, stopwords_list):
    result = []
    for item in list:
        if not item in stopwords_list:
            result.append(item)
    return result


def main(path, stopwords_path):
    documents = build_documents(path)
    words = find_words(documents)
    stopwords_list = read_stopwords(stopwords_path)
    result = compare_to_stopwords(words, stopwords_list)
    with open('./output', 'w') as text_file:
        for item in result:
        text_file.write(item)
        

if __name__ == '__main__':
    main('./2000SW/', './rainbow')
