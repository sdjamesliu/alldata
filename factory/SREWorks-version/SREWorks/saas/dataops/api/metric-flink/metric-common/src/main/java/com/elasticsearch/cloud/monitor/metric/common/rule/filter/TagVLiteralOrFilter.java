package com.elasticsearch.cloud.monitor.metric.common.rule.filter;

// This file is part of OpenTSDB.
// Copyright (C) 2015  The OpenTSDB Authors.
//
// This program is free software: you can redistribute it and/or modify it
// under the terms of the GNU Lesser General Public License as published by
// the Free Software Foundation, either version 2.1 of the License, or (at your
// option) any later version.  This program is distributed in the hope that it
// will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
// of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser
// General Public License for more details.  You should have received a copy
// of the GNU Lesser General Public License along with this program.  If not,
// see <http://www.gnu.org/licenses/>.
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Objects;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * A filter that lets the user list one or more explicit strings that should
 * be included in a result set for aggregation.
 *
 * @since 2.2
 */
public class TagVLiteralOrFilter extends TagVFilter {

    /**
     * Name of this filter
     */
    final public static String FILTER_NAME = "literal_or";

    /**
     * A list of strings to match on
     */
    final protected Set<String> literals;

    /**
     * Whether or not the match should be case insensitive
     */
    final protected boolean case_insensitive;

    /**
     * The default Ctor that disables case insensitivity
     *
     * @param tagk   The tag key to associate with this filter
     * @param filter The filter to match on
     * @throws IllegalArgumentException if the tagk or filter were empty or null
     */
    public TagVLiteralOrFilter(final String tagk, final String filter) {
        this(tagk, filter, false);
    }

    /**
     * A ctor that allows enabling case insensitivity
     *
     * @param tagk             The tag key to associate with this filter
     * @param filter           The filter to match on
     * @param case_insensitive Whether or not to match on case
     * @throws IllegalArgumentException if the tagk or filter were empty or null
     */
    public TagVLiteralOrFilter(final String tagk, final String filter, final boolean case_insensitive) {
        super(tagk, filter);
        this.case_insensitive = case_insensitive;

        // we have to have at least one character.
        if (filter == null || filter.isEmpty()) {
            throw new IllegalArgumentException("Filter cannot be null or empty");
        }
        if (filter.length() == 1 && filter.charAt(0) == '|') {
            throw new IllegalArgumentException("Filter must contain more than just a pipe");
        }
        final String[] split = filter.split("\\|");
        if (case_insensitive) {
            for (int i = 0; i < split.length; i++) {
                split[i] = split[i].toLowerCase();
            }
        }
        literals = new HashSet<String>(Arrays.asList(split));
    }

    public Set<String> getLiterals() {
        return literals;
    }

    @Override
    public boolean match(final Map<String, String> tags) {
        final String tagv = tags.get(tagk);
        if (tagv == null) {
            return false;
        }
        return literals.contains(case_insensitive ? tagv.toLowerCase() : tagv);
    }

    @Override
    public String debugInfo() {
        return "{literals=" + literals + ", case=" + case_insensitive + "}";
    }

    /**
     * @return Whether or not this filter has case insensitivity enabled
     */
    @JsonIgnore
    public boolean isCaseInsensitive() {
        return case_insensitive;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof TagVLiteralOrFilter)) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        final TagVLiteralOrFilter filter = (TagVLiteralOrFilter) obj;
        return Objects.equal(tagk, filter.tagk) && Objects.equal(literals, filter.literals) && Objects.equal(
            case_insensitive, filter.case_insensitive);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(tagk, literals, case_insensitive);
    }

    @Override
    public String getType() {
        return FILTER_NAME;
    }

    /**
     * @return a string describing the filter
     */
    public static String description() {
        return "Accepts one or more exact values and matches if the series contains "
            + "any of them. Multiple values can be included and must be seperated "
            + "by the | (pipe) character. The filter is case sensitive and will not "
            + "allow characters that TSDB does not allow at write time.";
    }

    /**
     * @return a list of examples showing how to use the filter
     */
    public static String examples() {
        return "host=literal_or(web01),  host=literal_or(web01|web02|web03)  "
            + "{\"type\":\"literal_or\",\"tagk\":\"host\"," + "\"filter\":\"web01|web02|web03\",\"groupBy\":false}";
    }

    /**
     * Case insensitive version
     */
    public static class TagVILiteralOrFilter extends TagVLiteralOrFilter {

        /**
         * Name of this filter
         */
        final public static String FILTER_NAME = "iliteral_or";

        public TagVILiteralOrFilter(final String tagk, final String filter) {
            super(tagk, filter, true);
        }

        @Override
        public String getType() {
            return FILTER_NAME;
        }

        @Override
        public boolean equals(final Object obj) {
            if (obj == null) {
                return false;
            }
            if (!(obj instanceof TagVILiteralOrFilter)) {
                return false;
            }
            if (obj == this) {
                return true;
            }
            final TagVILiteralOrFilter filter = (TagVILiteralOrFilter) obj;
            return Objects.equal(tagk, filter.tagk) && Objects.equal(literals, filter.literals) && Objects.equal(
                case_insensitive, filter.case_insensitive);
        }

        /**
         * @return a string describing the filter
         */
        public static String description() {
            return "Accepts one or more exact values and matches if the series contains "
                + "any of them. Multiple values can be included and must be seperated "
                + "by the | (pipe) character. The filter is case insensitive and will not "
                + "allow characters that TSDB does not allow at write time.";
        }

        /**
         * @return a list of examples showing how to use the filter
         */
        public static String examples() {
            return "host=iliteral_or(web01),  host=iliteral_or(web01|web02|web03)  "
                + "{\"type\":\"iliteral_or\",\"tagk\":\"host\","
                + "\"filter\":\"web01|web02|web03\",\"groupBy\":false}";
        }
    }
}

