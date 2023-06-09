/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.druid.data.input;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;
import org.apache.avro.generic.GenericRecord;
import org.apache.druid.data.input.avro.AvroBytesDecoder;
import org.apache.druid.data.input.avro.AvroParsers;
import org.apache.druid.data.input.impl.MapInputRowParser;
import org.apache.druid.data.input.impl.ParseSpec;
import org.apache.druid.java.util.common.parsers.ObjectFlattener;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Objects;

public class AvroStreamInputRowParser implements ByteBufferInputRowParser
{
  private final ParseSpec parseSpec;
  private final Boolean binaryAsString;
  private final Boolean extractUnionsByType;
  private final AvroBytesDecoder avroBytesDecoder;
  private final ObjectFlattener<GenericRecord> avroFlattener;
  private final MapInputRowParser mapParser;

  @JsonCreator
  public AvroStreamInputRowParser(
      @JsonProperty("parseSpec") ParseSpec parseSpec,
      @JsonProperty("avroBytesDecoder") AvroBytesDecoder avroBytesDecoder,
      @JsonProperty("binaryAsString") Boolean binaryAsString,
      @JsonProperty("extractUnionsByType") Boolean extractUnionsByType
  )
  {
    this.parseSpec = Preconditions.checkNotNull(parseSpec, "parseSpec");
    this.avroBytesDecoder = Preconditions.checkNotNull(avroBytesDecoder, "avroBytesDecoder");
    this.binaryAsString = binaryAsString != null && binaryAsString;
    this.extractUnionsByType = extractUnionsByType != null && extractUnionsByType;
    this.avroFlattener = AvroParsers.makeFlattener(parseSpec, false, this.binaryAsString, this.extractUnionsByType);
    this.mapParser = new MapInputRowParser(parseSpec);
  }

  @Override
  public List<InputRow> parseBatch(ByteBuffer input)
  {
    return AvroParsers.parseGenericRecord(avroBytesDecoder.parse(input), mapParser, avroFlattener);
  }

  @JsonProperty
  @Override
  public ParseSpec getParseSpec()
  {
    return parseSpec;
  }

  @JsonProperty
  public AvroBytesDecoder getAvroBytesDecoder()
  {
    return avroBytesDecoder;
  }

  @JsonProperty
  public Boolean getBinaryAsString()
  {
    return binaryAsString;
  }

  @JsonProperty
  public Boolean isExtractUnionsByType()
  {
    return extractUnionsByType;
  }

  @Override
  public ByteBufferInputRowParser withParseSpec(ParseSpec parseSpec)
  {
    return new AvroStreamInputRowParser(
        parseSpec,
        avroBytesDecoder,
        binaryAsString,
        extractUnionsByType
    );
  }

  @Override
  public boolean equals(final Object o)
  {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    final AvroStreamInputRowParser that = (AvroStreamInputRowParser) o;
    return Objects.equals(parseSpec, that.parseSpec) &&
           Objects.equals(avroBytesDecoder, that.avroBytesDecoder) &&
           Objects.equals(binaryAsString, that.binaryAsString) &&
           Objects.equals(extractUnionsByType, that.extractUnionsByType);
  }

  @Override
  public int hashCode()
  {
    return Objects.hash(parseSpec, avroBytesDecoder, binaryAsString, extractUnionsByType);
  }

  @Override
  public String toString()
  {
    return "AvroStreamInputRowParser{" +
           "parseSpec=" + parseSpec +
           ", binaryAsString=" + binaryAsString +
           ", extractUnionsByType=" + extractUnionsByType +
           ", avroBytesDecoder=" + avroBytesDecoder +
           '}';
  }
}
