package com.linkedin.feathr.core.configvalidator.typesafe;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;


class FeatureDefConfFixture {
  static final String featureDefWithMvel = String.join("\n",
      "// all possible feature definitions using MVEL",
      "{",
      "  \"anchors\": {",

      "    // SimpleFeatureConfig",
      "    industry-local: {",
      "       source: \"LocalSQLAnchorTest/industry.avro.json\"",
      "       features: {",
      "          waterloo_member_geoCountry_local: \"$.countryCode in geoStdData\"",
      "       }",
      "    }",

      "    // ComplexFeatureConfig",
      "    swaAnchorWithKeyExtractor: {",
      "      source: \"swaSource\"",
      "      keyExtractor: \"com.linkedin.frame.offline.SimpleSampleKeyExtractor\"",
      "      features: {",
      "        waterloo_job_standardizedSkillsString: {",
      "          def: \"aggregationWindow\"",
      "          aggregation: SUM",
      "          window: 3d",
      "        }",
      "      }",
      "    }",

      "    // TimeWindowFeatureConfig",
      "    nearLineFeatureAnchor: {",
      "      source: kafkaTestSource,",
      "      key.mvel: \"a in b\",",
      "      features: {",
      "        maxPV12h: {",
      "          def.mvel: pageView,",
      "          aggregation: MAX,",
      "          windowParameters: {",
      "              type: SLIDING,",
      "              size: 1h,",
      "              slidingInterval: 10m,",
      "            },",
      "          groupBy: pageKey,",
      "          filter.mvel: \"$.getAsTermVector().keySet()\"",
      "        }",
      "      }",
      "    }",
      "  }",

      "  \"derivations\": {",

      "    // SimpleFeatureConfig",
      "    \"waterloo_member_geoCountry_local_alias\": \"waterloo_member_geoCountry_local\",",

      "    abuse_member_invitation_inboundOutboundSkew: { ",
      "      sqlExpr: \"case when abuse_member_invitation_numInviters = 0 then -1 else abuse_member_invitation_numInvites/abuse_member_invitation_numInviters end\"",
      "    },",

      "    \"waterloo_member_job_cosineSimilarity\": {",
      "      \"key\": [",
      "        \"m\",",
      "        \"j\"",
      "      ],",
      "      \"inputs\": {",
      "        \"a\": {",
      "          \"key\": \"m\",",
      "          \"feature\": \"waterloo_member_geoCountry_local\"",
      "        },",
      "        \"b\": {",
      "          \"key\": \"j\",",
      "          \"feature\": \"waterloo_job_standardizedSkillsString\"",
      "        }",
      "      },",
      "      \"definition\": \"cosineSimilarity(a, b)\",",
      "       type: \"NUMERIC\"",
      "    },",
      "  }",
      "}");

  static final String featureDefWithHdfsSource = String.join("\n",
      "sources: {",
      "  hdfsSource1: {",
      "    location: { path: \"/data/tracking_column/test\" }",
      "    isTimeSeries: true",
      "    timeWindowParameters: {",
      "      timestamp: \"timestamp\"",
      "      timestamp_format: \"yyyy-MM-dd\"" + "    }",
      "  }",

      "  hdfsSource2: {",
      "    type: \"HDFS\"",
      "    location: { path: \"/jobs/metrics/ump_v2/metrics/test/test/test/test\" }",
      "    isTimeSeries: true",
      "    timeWindowParameters: {",
      "      timestamp: \"metadataMap.timestamp.STRING\"",
      "      timestamp_format: \"epoch\"",
      "    }",
      "  }",

      "  hdfsSource3: {",
      "     location: { path: \"/jobs/metrics/udp/datafiles/test\" }",
      "  }",
      "}",

      "anchors: {",
      "  testAnchor1: { ",
      "    source: \"/jobs/metrics/udp/snapshot/test/#LATEST\" ",
      "    keyAlias: \"x\" ",
      "    extractor: \"com.linkedin.frame.feature.anchor.TestExtractor\" ",
      "    features: [ ",
      "      test_feature_1 ",
      "    ] ",
      "  } ",
      "}"
  );

  static final String featureDefWithExtractors = String.join("\n",
      "anchors: { ",
      "  offlineAnchor1: { ",
      "    source: \"/test/test/test/#LATEST\" ",
      "    extractor: \"com.linkedin.frame.offline.anchor.test.Extractor1\" ",
      "    features: [ ",
      "      offline_feature1_1 ",
      "    ] ",
      "  } ",

      "  offlineAnchor2: { ",
      "    source: \"/test/test/test/#LATEST\" ",
      "    transformer: \"com.linkedin.frame.offline.anchor.test.Transformer2\" ",
      "    features: [ ",
      "      \"offline_feature2_1\", ",
      "      \"offline_feature2_2\"",
      "    ] ",
      "  } ",

      "  offlineAnchor3: { ",
      "    source: \"/test/test/test/#LATEST\" ",
      "    keyExtractor: \"com.linkedin.frame.offline.anchor.test.KeyExtractor3\" ",
      "    features: { ",
      "      offline_feature3_1: { ",
      "        def: \"count\" ",
      "        filter: \"name = 'queryCount14d'\" ",
      "        aggregation: LATEST ",
      "        window: 3d ",
      "        default: 0.0 ",
      "      } ",
      "    } ",
      "  } ",

      "  offlineAnchor4: { ",
      "    source: \"/test/test/test/#LATEST\" ",
      "    extractor: \"com.linkedin.frame.offline.anchor.test.Extractor4\" ",
      "    keyExtractor: \"com.linkedin.frame.offline.anchor.test.KeyExtractor4\" ",
      "    features: [ ",
      "      \"offline_feature4_1\", ",
      "      \"offline_feature4_2\"",
      "    ] ",
      "  } ",

      "  \"onlineAnchor1\": {",
      "    source: \"testSource\"",
      "    extractor: {class: \"com.linkedin.frame.online.anchor.test.Extractor1\"}",
      "    features: [",
      "      online_feature1_1",
      "    ]",
      "  }",

      "  \"onlineAnchor2\": {",
      "    source: \"testSource\"",
      "    extractor: {class: \"com.linkedin.frame.online.anchor.test.Extractor2\"}",
      "    features: [",
      "      online_feature2_1",
      "    ]",
      "  }",

      "  \"onlineAnchorNotUsed\": {",
      "    source: \"testSource\"",
      "    extractor: {class: \"com.linkedin.frame.online.anchor.test.ExtractorNotUsed\"}",
      "    features: [",
      "      online_feature_not_used",
      "    ]",
      "  }",
      "}",

      "derivations: { ",
      "  derived_feature_1: { ",
      "    key: [\"member\"] ",
      "    inputs: [ { key: \"member\", feature: \"offline_feature3_1\"} ] ",
      "    class: \"com.linkedin.frame.offline.derived.DerivedExtractor1\" ",
      "  }",

      "  derived_feature_2: \"import com.linkedin.frame.offline.derived.DerivationUtil; DerivationUtil.extractRegionCode(online_feature1_1)\"",

      "  derived_feature_3: \"online_feature2_1\"",
      "  derived_feature_4: \"derived_feature_3\"",
      "}");

  static Set<String> expectedExtractors;
  static {
    expectedExtractors = Stream.of("com.linkedin.frame.offline.anchor.test.Extractor1",
            "com.linkedin.frame.offline.anchor.test.Transformer2",
            "com.linkedin.frame.offline.anchor.test.KeyExtractor3",
            "com.linkedin.frame.offline.anchor.test.Extractor4",
            "com.linkedin.frame.offline.anchor.test.KeyExtractor4",
            "com.linkedin.frame.online.anchor.test.Extractor1",
            "com.linkedin.frame.online.anchor.test.Extractor2",
            "com.linkedin.frame.online.anchor.test.ExtractorNotUsed",
            "com.linkedin.frame.offline.derived.DerivedExtractor1")
        .collect(Collectors.toSet());
  }
}
