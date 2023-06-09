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

package org.apache.druid.java.util.common.parsers;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class JSONPathParserTest
{
  private static final String JSON =
      "{\"one\": \"foo\", \"two\" : [\"bar\", \"baz\"], \"three\" : \"qux\", \"four\" : null}";
  private static final String NUMBERS_JSON =
      "{\"five\" : 5.0, \"six\" : 6, \"many\" : 1234567878900, \"toomany\" : 1234567890000000000000}";
  private static final String WHACKY_CHARACTER_JSON =
      "{\"one\": \"foo\\uD900\"}";
  private static final String NESTED_JSON =
      "{\"simpleVal\":\"text\", \"ignore_me\":[1, {\"x\":2}], \"blah\":[4,5,6], \"newmet\":5, " +
      "\"foo\":{\"bar1\":\"aaa\", \"bar2\":\"bbb\"}, " +
      "\"baz\":[1,2,3], \"timestamp\":\"2999\", \"foo.bar1\":\"Hello world!\", " +
      "\"testListConvert\":[1234567890000000000000, \"foo\\uD900\"], " +
      "\"testListConvert2\":[1234567890000000000000, \"foo\\uD900\", [1234567890000000000000]], " +
      "\"testMapConvert\":{\"big\": 1234567890000000000000, \"big2\":{\"big2\":1234567890000000000000}}, " +
      "\"testEmptyList\": [], " +
      "\"hey\":[{\"barx\":\"asdf\"}], \"met\":{\"a\":[7,8,9]}}";
  private static final String NOT_JSON = "***@#%R#*(TG@(*H(#@(#@((H#(@TH@(#TH(@SDHGKJDSKJFBSBJK";

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void testSimple()
  {
    List<JSONPathFieldSpec> fields = new ArrayList<>();
    final Parser<String, Object> jsonParser = new JSONPathParser(new JSONPathSpec(true, fields), null, false);
    final Map<String, Object> jsonMap = jsonParser.parseToMap(JSON);
    Assert.assertEquals(
        "jsonMap",
        ImmutableMap.of("one", "foo", "two", ImmutableList.of("bar", "baz"), "three", "qux"),
        jsonMap
    );
  }

  @Test
  public void testWithNumbers()
  {
    List<JSONPathFieldSpec> fields = new ArrayList<>();
    final Parser<String, Object> jsonParser = new JSONPathParser(new JSONPathSpec(true, fields), null, false);
    final Map<String, Object> jsonMap = jsonParser.parseToMap(NUMBERS_JSON);
    Assert.assertEquals(
        "jsonMap",
        ImmutableMap.of("five", 5.0, "six", 6L, "many", 1234567878900L, "toomany", 1.23456789E21),
        jsonMap
    );
  }

  @Test
  public void testWithWhackyCharacters()
  {
    List<JSONPathFieldSpec> fields = new ArrayList<>();
    final Parser<String, Object> jsonParser = new JSONPathParser(new JSONPathSpec(true, fields), null, false);
    final Map<String, Object> jsonMap = jsonParser.parseToMap(WHACKY_CHARACTER_JSON);
    Assert.assertEquals(
        "jsonMap",
        ImmutableMap.of("one", "foo?"),
        jsonMap
    );
  }

  @Test
  public void testNestingWithFieldDiscovery()
  {
    List<JSONPathFieldSpec> fields = new ArrayList<>();
    fields.add(new JSONPathFieldSpec(JSONPathFieldType.ROOT, "baz", "baz"));
    fields.add(new JSONPathFieldSpec(JSONPathFieldType.PATH, "nested-foo.bar1", "$.foo.bar1"));
    fields.add(new JSONPathFieldSpec(JSONPathFieldType.PATH, "nested-foo.bar2", "$.foo.bar2"));
    fields.add(new JSONPathFieldSpec(JSONPathFieldType.PATH, "heybarx0", "$.hey[0].barx"));
    fields.add(new JSONPathFieldSpec(JSONPathFieldType.PATH, "met-array", "$.met.a"));
    fields.add(new JSONPathFieldSpec(JSONPathFieldType.ROOT, "testListConvert2", "testListConvert2"));
    fields.add(new JSONPathFieldSpec(JSONPathFieldType.ROOT, "testMapConvert", "testMapConvert"));

    fields.add(new JSONPathFieldSpec(JSONPathFieldType.ROOT, "INVALID_ROOT", "INVALID_ROOT_EXPR"));
    fields.add(new JSONPathFieldSpec(JSONPathFieldType.PATH, "INVALID_PATH", "INVALID_PATH_EXPR"));

    fields.add(new JSONPathFieldSpec(JSONPathFieldType.JQ, "jq-nested-foo.bar1", ".foo.bar1"));
    fields.add(new JSONPathFieldSpec(JSONPathFieldType.JQ, "jq-nested-foo.bar2", ".foo.bar2"));
    fields.add(new JSONPathFieldSpec(JSONPathFieldType.JQ, "jq-heybarx0", ".hey[0].barx"));
    fields.add(new JSONPathFieldSpec(JSONPathFieldType.JQ, "jq-met-array", ".met.a"));


    final Parser<String, Object> jsonParser = new JSONPathParser(new JSONPathSpec(true, fields), null, false);
    final Map<String, Object> jsonMap = jsonParser.parseToMap(NESTED_JSON);

    // Root fields
    Assert.assertEquals(ImmutableList.of(1L, 2L, 3L), jsonMap.get("baz"));
    Assert.assertEquals(ImmutableList.of(4L, 5L, 6L), jsonMap.get("blah"));
    Assert.assertEquals("text", jsonMap.get("simpleVal"));
    Assert.assertEquals(5L, jsonMap.get("newmet"));
    Assert.assertEquals("2999", jsonMap.get("timestamp"));
    Assert.assertEquals("Hello world!", jsonMap.get("foo.bar1"));

    List<Object> testListConvert = (List) jsonMap.get("testListConvert");
    Assert.assertEquals(1.23456789E21, testListConvert.get(0));
    Assert.assertEquals("foo?", testListConvert.get(1));

    List<Object> testListConvert2 = (List) jsonMap.get("testListConvert2");
    Assert.assertEquals(1.23456789E21, testListConvert2.get(0));
    Assert.assertEquals("foo?", testListConvert2.get(1));
    Assert.assertEquals(1.23456789E21, ((List) testListConvert2.get(2)).get(0));

    Map<String, Object> testMapConvert = (Map) jsonMap.get("testMapConvert");
    Assert.assertEquals(1.23456789E21, testMapConvert.get("big"));
    Assert.assertEquals(1.23456789E21, ((Map) testMapConvert.get("big2")).get("big2"));

    Assert.assertEquals(ImmutableList.of(), jsonMap.get("testEmptyList"));

    // Nested fields
    Assert.assertEquals("aaa", jsonMap.get("nested-foo.bar1"));
    Assert.assertEquals("bbb", jsonMap.get("nested-foo.bar2"));
    Assert.assertEquals("asdf", jsonMap.get("heybarx0"));
    Assert.assertEquals(ImmutableList.of(7L, 8L, 9L), jsonMap.get("met-array"));

    Assert.assertEquals("aaa", jsonMap.get("jq-nested-foo.bar1"));
    Assert.assertEquals("bbb", jsonMap.get("jq-nested-foo.bar2"));
    Assert.assertEquals("asdf", jsonMap.get("jq-heybarx0"));
    Assert.assertEquals(ImmutableList.of(7L, 8L, 9L), jsonMap.get("jq-met-array"));

    // Fields that should not be discovered
    Assert.assertFalse(jsonMap.containsKey("hey"));
    Assert.assertFalse(jsonMap.containsKey("met"));
    Assert.assertFalse(jsonMap.containsKey("ignore_me"));
    Assert.assertFalse(jsonMap.containsKey("foo"));

    // Invalid fields
    Assert.assertNull(jsonMap.get("INVALID_ROOT"));
    Assert.assertNull(jsonMap.get("INVALID_PATH"));
  }

  @Test
  public void testNestingNoDiscovery()
  {
    List<JSONPathFieldSpec> fields = new ArrayList<>();
    fields.add(new JSONPathFieldSpec(JSONPathFieldType.ROOT, "simpleVal", "simpleVal"));
    fields.add(new JSONPathFieldSpec(JSONPathFieldType.ROOT, "timestamp", "timestamp"));
    fields.add(new JSONPathFieldSpec(JSONPathFieldType.PATH, "nested-foo.bar2", "$.foo.bar2"));
    fields.add(new JSONPathFieldSpec(JSONPathFieldType.PATH, "heybarx0", "$.hey[0].barx"));
    fields.add(new JSONPathFieldSpec(JSONPathFieldType.PATH, "met-array", "$.met.a"));
    fields.add(new JSONPathFieldSpec(JSONPathFieldType.JQ, "jq-nested-foo.bar2", ".foo.bar2"));
    fields.add(new JSONPathFieldSpec(JSONPathFieldType.JQ, "jq-heybarx0", ".hey[0].barx"));
    fields.add(new JSONPathFieldSpec(JSONPathFieldType.JQ, "jq-met-array", ".met.a"));

    final Parser<String, Object> jsonParser = new JSONPathParser(new JSONPathSpec(false, fields), null, false);
    final Map<String, Object> jsonMap = jsonParser.parseToMap(NESTED_JSON);

    // Root fields
    Assert.assertEquals("text", jsonMap.get("simpleVal"));
    Assert.assertEquals("2999", jsonMap.get("timestamp"));

    // Nested fields
    Assert.assertEquals("bbb", jsonMap.get("nested-foo.bar2"));
    Assert.assertEquals("asdf", jsonMap.get("heybarx0"));
    Assert.assertEquals(ImmutableList.of(7L, 8L, 9L), jsonMap.get("met-array"));
    Assert.assertEquals("bbb", jsonMap.get("jq-nested-foo.bar2"));
    Assert.assertEquals("asdf", jsonMap.get("jq-heybarx0"));
    Assert.assertEquals(ImmutableList.of(7L, 8L, 9L), jsonMap.get("jq-met-array"));

    // Fields that should not be discovered
    Assert.assertFalse(jsonMap.containsKey("newmet"));
    Assert.assertFalse(jsonMap.containsKey("foo.bar1"));
    Assert.assertFalse(jsonMap.containsKey("baz"));
    Assert.assertFalse(jsonMap.containsKey("blah"));
    Assert.assertFalse(jsonMap.containsKey("nested-foo.bar1"));
    Assert.assertFalse(jsonMap.containsKey("hey"));
    Assert.assertFalse(jsonMap.containsKey("met"));
    Assert.assertFalse(jsonMap.containsKey("ignore_me"));
    Assert.assertFalse(jsonMap.containsKey("foo"));
  }

  @Test
  public void testRejectDuplicates()
  {
    List<JSONPathFieldSpec> fields = new ArrayList<>();
    fields.add(new JSONPathFieldSpec(JSONPathFieldType.PATH, "met-array", "$.met.a"));
    fields.add(new JSONPathFieldSpec(JSONPathFieldType.PATH, "met-array", "$.met.a"));

    thrown.expect(IllegalArgumentException.class);
    thrown.expectMessage("Cannot have duplicate field definition: met-array");

    final Parser<String, Object> jsonParser = new JSONPathParser(new JSONPathSpec(false, fields), null, false);
    jsonParser.parseToMap(NESTED_JSON);
  }

  @Test
  public void testRejectDuplicates2()
  {
    List<JSONPathFieldSpec> fields = new ArrayList<>();
    fields.add(new JSONPathFieldSpec(JSONPathFieldType.PATH, "met-array", "$.met.a"));
    fields.add(new JSONPathFieldSpec(JSONPathFieldType.JQ, "met-array", ".met.a"));

    thrown.expect(IllegalArgumentException.class);
    thrown.expectMessage("Cannot have duplicate field definition: met-array");

    final Parser<String, Object> jsonParser = new JSONPathParser(new JSONPathSpec(false, fields), null, false);
    jsonParser.parseToMap(NESTED_JSON);
  }

  @Test
  public void testParseFail()
  {
    List<JSONPathFieldSpec> fields = new ArrayList<>();

    thrown.expect(ParseException.class);
    thrown.expectMessage("Unable to parse row [" + NOT_JSON + "]");

    final Parser<String, Object> jsonParser = new JSONPathParser(new JSONPathSpec(true, fields), null, false);
    jsonParser.parseToMap(NOT_JSON);
  }

  @Test
  public void testJSONPathFunctions()
  {
    List<JSONPathFieldSpec> fields = Arrays.asList(
        new JSONPathFieldSpec(JSONPathFieldType.PATH, "met-array-length", "$.met.a.length()"),
        new JSONPathFieldSpec(JSONPathFieldType.PATH, "met-array-min", "$.met.a.min()"),
        new JSONPathFieldSpec(JSONPathFieldType.PATH, "met-array-max", "$.met.a.max()"),
        new JSONPathFieldSpec(JSONPathFieldType.PATH, "met-array-avg", "$.met.a.avg()"),
        new JSONPathFieldSpec(JSONPathFieldType.PATH, "met-array-sum", "$.met.a.sum()"),
        new JSONPathFieldSpec(JSONPathFieldType.PATH, "met-array-stddev", "$.met.a.stddev()"),
        new JSONPathFieldSpec(JSONPathFieldType.PATH, "met-array-append", "$.met.a.append(10)"),
        new JSONPathFieldSpec(JSONPathFieldType.PATH, "concat", "$.concat($.foo.bar1, $.foo.bar2)")
    );

    final Parser<String, Object> jsonParser = new JSONPathParser(new JSONPathSpec(true, fields), null, false);
    final Map<String, Object> jsonMap = jsonParser.parseToMap(NESTED_JSON);

    // values of met.a array are: 7,8,9
    Assert.assertEquals(3, jsonMap.get("met-array-length"));
    Assert.assertEquals(7.0, jsonMap.get("met-array-min"));
    Assert.assertEquals(9.0, jsonMap.get("met-array-max"));
    Assert.assertEquals(8.0, jsonMap.get("met-array-avg"));
    Assert.assertEquals(24.0, jsonMap.get("met-array-sum"));

    //deviation of [7,8,9] is 1/3, stddev is sqrt(1/3), approximately 0.8165
    Assert.assertEquals(0.8165, (double) jsonMap.get("met-array-stddev"), 0.00001);

    Assert.assertEquals(ImmutableList.of(7L, 8L, 9L, 10L), jsonMap.get("met-array-append"));
    Assert.assertEquals("aaabbb", jsonMap.get("concat"));
  }

}
