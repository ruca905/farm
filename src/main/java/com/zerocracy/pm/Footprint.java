/**
 * Copyright (c) 2016-2017 Zerocracy
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to read
 * the Software only. Permissions is hereby NOT GRANTED to use, copy, modify,
 * merge, publish, distribute, sublicense, and/or sell copies of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.zerocracy.pm;

import com.jcabi.xml.XML;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.zerocracy.entry.ExtMongo;
import com.zerocracy.jstk.Farm;
import com.zerocracy.jstk.Project;
import java.io.Closeable;
import java.io.IOException;
import java.util.Map;
import org.bson.Document;

/**
 * Footprint.
 *
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @since 0.9
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public final class Footprint implements Closeable {

    /**
     * Project ID.
     */
    private final String pid;

    /**
     * Mongo client.
     */
    private final MongoClient mongo;

    /**
     * Ctor.
     * @param farm Farm
     * @param pkt Project
     * @throws IOException If fails
     */
    public Footprint(final Farm farm, final Project pkt) throws IOException {
        this(new ExtMongo(farm).value(), pkt.pid());
    }

    /**
     * Ctor.
     * @param clt Client
     * @param pkt Project name
     */
    public Footprint(final MongoClient clt, final String pkt) {
        this.mongo = clt;
        this.pid = pkt;
    }

    /**
     * Add new claim, it was just opened.
     * @param xml The claim XML
     */
    public void open(final XML xml) {
        final ClaimIn claim = new ClaimIn(xml);
        Document doc = new Document()
            .append("cid", claim.cid())
            .append("project", this.pid)
            .append("type", claim.type())
            .append("created", claim.created());
        if (claim.hasAuthor()) {
            doc = doc.append("author", claim.author());
        }
        if (claim.hasToken()) {
            doc = doc.append("token", claim.token());
        }
        for (final Map.Entry<String, String> ent : claim.params().entrySet()) {
            final Object val;
            if (ent.getValue().matches("[0-9]+")) {
                val = Long.parseLong(ent.getValue());
            } else {
                val = ent.getValue();
            }
            doc = doc.append(ent.getKey(), val);
        }
        this.mongo.getDatabase("footprint")
            .getCollection("claims")
            .insertOne(doc);
    }

    /**
     * Close this claim.
     * @param xml The claim XML
     */
    public void close(final XML xml) {
        final ClaimIn claim = new ClaimIn(xml);
        this.mongo.getDatabase("footprint").getCollection("claims").updateOne(
            Filters.and(
                Filters.eq("cid", claim.cid()),
                Filters.eq("project", this.pid),
                Filters.eq("type", claim.type()),
                Filters.eq("created", claim.created())
            ),
            Updates.currentDate("closed")
        );
    }

    /**
     * Mongo collection to work with.
     * @return Collection
     */
    public MongoCollection<Document> collection() {
        return this.mongo.getDatabase("footprint").getCollection("claims");
    }

    @Override
    public void close() {
        this.mongo.close();
    }
}
