/*
 * Copyright Robert Newson
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.rnewson.couchdb.lucene;

import com.github.rnewson.couchdb.lucene.couchdb.FieldType;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.SortField;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class TypedField {

    private static Pattern PATTERN = Pattern.compile("^([^<]+)(<([^>]+)>)?$");

    private final String name;

    private final FieldType type;

    public TypedField(final String string) throws ParseException {
        final Matcher matcher = PATTERN.matcher(string);

        if (!matcher.matches()) {
            throw new ParseException("Field '" + string + "' not recognized.");
        }

        this.name = matcher.group(1);
        try {
            this.type = matcher.group(3) == null ? FieldType.TEXT : FieldType.valueOf(matcher.group(3).toUpperCase());
        } catch (final IllegalArgumentException e) {
            throw new ParseException("Unrecognized type '" + matcher.group(3) + "'");
        }
    }

    public String getName() {
        return name;
    }

    public FieldType getType() {
        return type;
    }

    public SortField.Type toSortField() {
        return type.toType();
    }

    public Query toRangeQuery(final String lower, final String upper,
                              final boolean lowerInclusive, final boolean upperInclusive)
            throws ParseException {
        return type.toRangeQuery(name, lower, upper, lowerInclusive, upperInclusive);
    }

    public Query toTermQuery(final String text) throws ParseException {
        return type.toTermQuery(name, text);
    }

    @Override
    public String toString() {
        return String.format("%s<%s>", name, type.toString().toLowerCase());
    }

}
