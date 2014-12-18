package com.linkedin.pinot.core.segment.index.loader;

import java.io.File;
import java.io.IOException;

import com.linkedin.pinot.common.segment.ReadMode;
import com.linkedin.pinot.core.index.reader.DataFileReader;
import com.linkedin.pinot.core.segment.index.BitmapInvertedIndex;
import com.linkedin.pinot.core.segment.index.ColumnMetadata;
import com.linkedin.pinot.core.segment.index.IndexSegmentImpl;
import com.linkedin.pinot.core.segment.index.readers.DictionaryReader;
import com.linkedin.pinot.core.segment.index.readers.DoubleDictionary;
import com.linkedin.pinot.core.segment.index.readers.FixedBitCompressedMVForwardIndexReader;
import com.linkedin.pinot.core.segment.index.readers.FixedBitCompressedSVForwardIndexReader;
import com.linkedin.pinot.core.segment.index.readers.FloatDictionary;
import com.linkedin.pinot.core.segment.index.readers.IntDictionary;
import com.linkedin.pinot.core.segment.index.readers.LongDictionary;
import com.linkedin.pinot.core.segment.index.readers.StringDictionary;


/**
 * @author Dhaval Patel<dpatel@linkedin.com>
 * Nov 13, 2014
 */

public class Loaders {

  public static class IndexSegment {
    public static com.linkedin.pinot.core.indexsegment.IndexSegment load(File indexDir, ReadMode mode) throws Exception {
      return new IndexSegmentImpl(indexDir, mode);
    }
  }

  public static class ForwardIndex {
    public static DataFileReader loadFwdIndexForColumn(ColumnMetadata columnMetadata, File indexFile, ReadMode loadMode)
        throws Exception {
      DataFileReader fwdIndexReader;
      if (columnMetadata.isSingleValue()) {
        fwdIndexReader =
            new FixedBitCompressedSVForwardIndexReader(indexFile, columnMetadata.getTotalDocs(), columnMetadata.getBitsPerElement(),
                loadMode == ReadMode.mmap);
      } else {
        fwdIndexReader =
            new FixedBitCompressedMVForwardIndexReader(indexFile, columnMetadata.getTotalDocs(), columnMetadata.getBitsPerElement(),
                loadMode == ReadMode.mmap);
      }

      return fwdIndexReader;
    }
  }

  public static class InvertedIndex {
    public static BitmapInvertedIndex load(ColumnMetadata metadata, File invertedIndexFile, ReadMode loadMode) throws IOException {
      return new BitmapInvertedIndex(invertedIndexFile, metadata.getCardinality(), loadMode == ReadMode.mmap);
    }
  }

  public static class Dictionary {

    @SuppressWarnings("incomplete-switch")
    public static DictionaryReader load(ColumnMetadata metadata, File dictionaryFile, ReadMode loadMode) throws IOException {
      switch (metadata.getDataType()) {
        case INT:
          return new IntDictionary(dictionaryFile, metadata, loadMode);
        case LONG:
          return new LongDictionary(dictionaryFile, metadata, loadMode);
        case FLOAT:
          return new FloatDictionary(dictionaryFile, metadata, loadMode);
        case DOUBLE:
          return new DoubleDictionary(dictionaryFile, metadata, loadMode);
        case STRING:
        case BOOLEAN:
          return new StringDictionary(dictionaryFile, metadata, loadMode);
      }

      throw new UnsupportedOperationException("unsupported data type : " + metadata.getDataType());
    }
  }

}