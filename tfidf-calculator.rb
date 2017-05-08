# Calculate tf-idf for weighting terms in documents
class TFIDFCaculator
  def initialize(path)
    @path = path 
    @documents = [] 
    @count = 0
  end

  # Read Files
  def read
    raise Error unless File.directory?(@path)
    #ignore file naming starts with .
    files = Dir.entries(@path).reject {|entry| entry =~ /^\.{1,2}/}
    files = files.map {|f| File.join(@path, f) }
    calculate_frequency(files)
  end

  # Start to calculate words frequency with files path array
  def calculate_frequency(files)
    files.each do |file|
      frequency = word_frequency_in_file(file)
      @count = @count + frequency[:count]
      @documents << calculate_tf(frequency)
    end
  end

  # Scan files with Regx which requires words in 2-20 letters
  def word_frequency_in_file(file)
    frequency = Hash.new(0) 
    words_count = 0
    input_file = File.open(file, 'r')
    # scan word with letters in length range 1 to 20
    input_file.read.downcase.scan(/\b[a-z]{1,20}\b/) do |word|
      words_count = words_count + 1
      frequency[word] = frequency[word] + 1
    end
    { frequency: frequency, file: file, count: words_count }
  end

  # Calculate tf value with one file frequency
  def calculate_tf(file_frequency)
    file_frequency[:tf] = Hash.new(0.0)
    frequency = file_frequency[:frequency]
    count = file_frequency[:count]
    frequency.keys.sort.each do |key|
      tf = frequency[key].to_f / count
      file_frequency[:tf][key] = tf.round(3)
    end
    file_frequency
  end

  # Calculate idf value with a word as parameter
  def calculate_idf(word)
    Math.log(@documents.count) / ( 1 + n_containing(word))
  end

  # Cound number of occurance a word in all documents.
  def n_containing(word)
    count = 0
    @documents.each do |doc|
      if doc[:frequency].has_key?(word)
        count = count + 1
      end
    end
    count
  end

  # combine results to generate a hash formatting as below
  # { word: tf-idf_value }
  def combine_frequency
    final_result = Hash.new(0)
    @documents.each do |doc|
      frequency = doc[:frequency]
      tf = doc[:tf]
      # key is a word
      frequency.keys.sort.each do |key|
        idf_value = calculate_idf(key).round(5)
        tf_idf = (tf[key] * idf_value).round(5)
        final_result[key] = final_result[key] + tf_idf
      end
    end 
    final_result
  end

  # Given a block with documents as parameters
  # Documents is an array containing all documents in following hash format
  # {
  #   frequency : {word: number_of_frequency},
  #   count: number_of_words_in_files,
  #   file: filename_path 
  # }
  def output(&block)
    yield(@documents) if block_given?
  end

  # output all documents
  def output_documents 
    @documents.each do |doc|
      print_documents(doc)
    end
  end

  # print all documents
  def print_documents(file_frequency)
    frequency = file_frequency[:frequency]
    count = file_frequency[:count]
    tf = file_frequency[:tf]
    frequency.keys.sort.each do |key|
      idf_value = calculate_idf(key).round(3)
      tf_idf = (tf[key] * idf_value).round(3)
      puts "#{key}: #{frequency[key]}, tf: #{tf[key].round(3)}, idf: #{idf_value}, tf-idf: #{tf_idf}"
    end
  end
end


