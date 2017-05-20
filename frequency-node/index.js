
// sync version
function walkSync(currentDirPath, callback) {
    let fs = require('fs'),
        path = require('path');
  fs.readdirSync(currentDirPath).forEach((name) => {
        let filePath = path.join(currentDirPath, name);
        let stat = fs.statSync(filePath);
        if (stat.isFile()) {
            callback(filePath, stat);
        } else if (stat.isDirectory()) {
            walkSync(filePath, callback);
        }
  });
}

const natural = require('natural'),
    TfIdf = natural.TfIdf,
    tfidf = new TfIdf();

const path = "./data/"
let files = [];
walkSync(path, (path, stat) => {
  tfidf.addFileSync(path);
})

let documents = tfidf.getDocuments()
let words = {}

documents.map((doc)=> {
    for (var key in doc) {
        if (words.hasOwnProperty(key)) {
            words[key] += parseInt(doc[key]);
        } else {
            words[key] = parseInt(doc[key]);
        }
    }
});

// Create items array
var items = Object.keys(words).map(function(key) {
    return [key, words[key]];
});

// Sort the array based on the second element
items.sort(function(first, second) {
    return second[1] - first[1];
});

let fs = require('fs');
let stream = fs.createWriteStream("frequency.txt");

stream.once('open', (fd) => {
    items.map((line) => {
        stream.write(line[0] + ": " + line[1] + "\n");
    })
    stream.end();
});
